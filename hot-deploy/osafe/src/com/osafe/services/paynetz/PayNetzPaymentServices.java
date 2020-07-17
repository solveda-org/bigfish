package com.osafe.services.paynetz;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;

import org.ofbiz.accounting.payment.PaymentGatewayServices;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.HttpClient;
import org.ofbiz.base.util.HttpClientException;
import org.ofbiz.base.util.URLConnector;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilIO;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author rrlakhera
 *
 */
public class PayNetzPaymentServices {
    
    public static final String module = PayNetzPaymentServices.class.getName();

    /**
     * The token cart map.
     * 
     * Used to maintain a weak reference to the ShoppingCart for customers who
     * have gone to PayNetz to checkout so that we can quickly grab the cart,
     * perform shipment estimates and send the info back to PayNetz. The weak key is
     * a simple wrapper for the checkout token String and is stored as a cart
     * attribute. The value is a weak reference to the ShoppingCart itself.
     * Entries will be removed as carts are removed from the session (i.e. on
     * cart clear or successful checkout) or when the session is destroyed
     * */
    private static Map<PayNetzTokenWrapper, WeakReference<ShoppingCart>> tokenCartMap = new WeakHashMap<PayNetzTokenWrapper, WeakReference<ShoppingCart>>();

    public static Map<String, Object> setPayNetzChekout(DispatchContext dctx, Map<String, ? extends Object> context)
    {
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        if (cart == null || cart.items().size() <= 0)
        {
            return ServiceUtil.returnError("Shopping cart is empty, cannot proceed with PayNetz Checkout");
        }

        Map<String, String> props = buildPayNetzProperties(dctx, context, null);
        if (props == null)
        {
            return ServiceUtil.returnError("Couldn't retrieve a PaymentGatewayPayNetz record for PayNetz Checkout, cannot continue.");
        }

        // Cart information
        String refToken = getPayNetzCheckoutToken();
        try
        {
            parameters = addCartDetails(dctx, context);
            validateParam(parameters, "login", props.get("loginId"), true);
            validateParam(parameters, "pass", props.get("password"), true);
            validateParam(parameters, "prodid", props.get("productId"), true);
            validateParam(parameters, "ru", props.get("returnUrl"), false);
            validateParam(parameters, "ttype", props.get("transactionType"), true);
            // TODO: need to determine txnscamt & custacc; currently passing static value as per api doc
            validateParam(parameters, "txnscamt", "0", true);
            validateParam(parameters, "custacc", "1234567890", true);
            validateParam(parameters, "txnid", refToken, true);
            validateParam(parameters, "date", UtilDateTime.nowDateString("dd/MM/yyyy HH:mm:ss"), false);
        } 
        catch (Exception e)
        {
            Debug.logError(e, module);
            return ServiceUtil.returnError("An error occurred while retreiving cart details");
        }

        Document responseDoc = null;
        try 
        {
            responseDoc = sendRequest(props.get("redirectUrl"), parameters, false);
        }
        catch (GeneralException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }
        Map<String, Object> responseMap = processResponse(responseDoc);

        if (UtilValidate.isEmpty(responseMap))
        {
            return ServiceUtil.returnError("No Respone Found From PayNetz");
        }

        // If it is test mode then create the payment base on token
        if ("TEST".equals(props.get("payNetzMode")))
        {
            boolean anon = "anonymous".equals(cart.getUserLogin().getString("userLoginId"));
            // Even if anon, a party could already have been created
            String partyId = cart.getOrderPartyId();
            if (partyId == null && anon)
            {
                // Check nothing has been set on the anon userLogin either
                partyId = cart.getUserLogin() != null ? cart.getUserLogin().getString("partyId") : null;
                cart.setOrderPartyId(partyId);
            }

            Map<String, Object> inMap = FastMap.newInstance();
            inMap.put("userLogin", userLogin);
            inMap.put("partyId", partyId);
            inMap.put("amount", cart.getGrandTotal().subtract(cart.getPaymentTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
            inMap.put("dateCreated", UtilDateTime.nowDateString("dd/MM/yyyy HH:mm:ss"));
            inMap.put("productId", props.get("productId"));
            inMap.put("bankName", (String)responseMap.get("url"));
            inMap.put("bankTransactionId", (String) responseMap.get("token"));
            inMap.put("clientcode", "txnStage=".concat((String)responseMap.get("txnStage")));
            inMap.put("transactionId", "tempTxnId=".concat((String)responseMap.get("tempTxnId")));
            inMap.put("merchantTransactionId", refToken);
            inMap.put("responseCode", props.get("payNetzMode"));

            Map<String, Object> outMap = null;
            try 
            {
                outMap = dispatcher.runSync("createPayNetzPaymentMethod", inMap);
            } 
            catch (GenericServiceException e)
            {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            String paymentMethodId = (String) outMap.get("paymentMethodId");
            Debug.log("gotPayNetzPayment" + paymentMethodId, module);

            //cart.clearPayments(); for support the multiple payment
            BigDecimal maxAmount = cart.getGrandTotal().subtract(cart.getPaymentTotal()).setScale(2, BigDecimal.ROUND_HALF_UP);
            cart.addPaymentAmount(paymentMethodId, maxAmount, true);

            cart.setAttribute("payNetzCheckoutToken", props.get("payNetzMode"));
            return ServiceUtil.returnSuccess();
        }
        
        parameters = new LinkedHashMap<String, Object>();
        validateParam(parameters, "ttype", (String)responseMap.get("ttype"), true);
        validateParam(parameters, "tempTxnId", (String)responseMap.get("tempTxnId"), true);
        validateParam(parameters, "token", (String)responseMap.get("token"), true);
        validateParam(parameters, "txnStage", (String)responseMap.get("txnStage"), true);

        String encodedParameters = UtilHttp.urlEncodeArgs(parameters, false);

        StringBuilder redirectUrl = new StringBuilder((String)responseMap.get("url"));
        redirectUrl.append("?");
        redirectUrl.append(encodedParameters);

        cart.setAttribute("payNetzCheckoutToken", refToken);
        cart.setAttribute("payNetzCheckoutRedirectUrl", redirectUrl.toString());
        PayNetzTokenWrapper tokenWrapper = new PayNetzTokenWrapper(refToken);
        PayNetzPaymentServices.tokenCartMap.put(tokenWrapper, new WeakReference<ShoppingCart>(cart));
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> getPayNetzCheckout(DispatchContext dctx, Map<String, ? extends Object> context)
    {
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        Map<String, Object> payNetzResponse = (Map) context.get("payNetzResponse");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = dctx.getDispatcher();

        String responseCode = (String) payNetzResponse.get("f_code");
        String amount = (String) payNetzResponse.get("amt");
        String bankName = (String) payNetzResponse.get("bank_name");
        String bankTransactionId = (String) payNetzResponse.get("bank_txn");
        String transactionId = (String) payNetzResponse.get("mmp_txn");
        String merchantTransactionId = (String) payNetzResponse.get("mer_txn");
        String productId = (String) payNetzResponse.get("prod");
        String dateCreated = (String) payNetzResponse.get("date");
        String clientcode = (String) payNetzResponse.get("clientcode");

        
        String token = merchantTransactionId;
        WeakReference<ShoppingCart> weakCart = tokenCartMap.get(PayNetzTokenWrapper.getTokenWrapper(token));
        if (weakCart != null) {
            cart = weakCart.get();
        }

        boolean anon = "anonymous".equals(cart.getUserLogin().getString("userLoginId"));
        // Even if anon, a party could already have been created
        String partyId = cart.getOrderPartyId();
        if (partyId == null && anon)
        {
            // Check nothing has been set on the anon userLogin either
            partyId = cart.getUserLogin() != null ? cart.getUserLogin().getString("partyId") : null;
            cart.setOrderPartyId(partyId);
        }

        Map<String, Object> inMap = FastMap.newInstance();
        inMap.put("userLogin", userLogin);
        inMap.put("partyId", partyId);
        inMap.put("amount", amount);
        inMap.put("bankName", bankName);
        inMap.put("bankTransactionId", bankTransactionId);
        inMap.put("clientcode", clientcode);
        inMap.put("dateCreated", dateCreated);
        inMap.put("productId", productId);
        inMap.put("transactionId", transactionId);
        inMap.put("merchantTransactionId", merchantTransactionId);
        inMap.put("responseCode", responseCode);

        Map<String, Object> outMap = null;
        try 
        {
            outMap = dispatcher.runSync("createPayNetzPaymentMethod", inMap);
        } 
        catch (GenericServiceException e)
        {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        String paymentMethodId = (String) outMap.get("paymentMethodId");
        Debug.log("gotPayNetzPayment" + paymentMethodId, module);

        //cart.clearPayments(); for support the multiple payment
        BigDecimal maxAmount = cart.getGrandTotal().subtract(cart.getPaymentTotal()).setScale(2, BigDecimal.ROUND_HALF_UP);
        cart.addPaymentAmount(paymentMethodId, maxAmount, true);

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> doAuthorization(DispatchContext dctx, Map<String, Object> context)
    {
        Debug.logInfo("PayNetz - Entered paymentAuthorisation", module);
        Debug.logInfo("PayNetz paymentAuthorisation context : " + context, module);

        GenericValue payNetzPaymentMethod = null;
        try
        {
            GenericValue paymentPreference = (GenericValue) context.get("orderPaymentPreference");
            GenericValue paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
            payNetzPaymentMethod = paymentMethod.getRelatedOne("PayNetzPaymentMethod");
        }
        catch (GenericEntityException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }

        Map<String, String> props = buildPayNetzProperties(dctx, context, PaymentGatewayServices.AUTH_SERVICE_TYPE);

        //start - authorization parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        //Payment is already authorized so return success response

        Debug.logInfo("authorization parameters -> " + parameters, module);
        //end - authorization parameters

//        Document authResponseDoc = null;
//        try
//        {
//            authResponseDoc = sendRequest(props.get("apiUrl"), parameters);
//        }
//        catch (GeneralException ge)
//        {
//            return ServiceUtil.returnError(ge.getMessage());
//        }
//        return processAuthResponse(authResponseDoc);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        String refNum = UtilDateTime.nowAsString();
        result.put("authResult", Boolean.TRUE);
        result.put("processAmount", context.get("processAmount"));
        result.put("authRefNum", refNum);
        result.put("authAltRefNum", refNum);
        result.put("authCode", "100");
        result.put("authFlag", "A");
        result.put("authMessage", "Payment is already authorized");
        return result;
   }

   public static Map<String, Object> doRelease(DispatchContext dctx, Map<String, Object> context)
   {
       Debug.logInfo("PayNetz - Entered paymentRelease", module);
       Debug.logInfo("PayNetz paymentRelease context : " + context, module);

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTrans = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        BigDecimal releaseAmount = (BigDecimal) context.get("releaseAmount");

        Map<String, String> props = buildPayNetzProperties(dctx, context, PaymentGatewayServices.RELEASE_SERVICE_TYPE);

        //start - Release parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        // Currently No API is defined for Release so return success response

        Debug.logInfo("Release parameters -> " + parameters, module);
        //end - Release parameters

//        Document releaseResponseDoc = null;
//        try 
//        {
//            releaseResponseDoc = sendRequest(props.get("apiUrl"), parameters);
//        }
//        catch (GeneralException ge)
//        {
//            return ServiceUtil.returnError(ge.getMessage());
//        }
//        return processReleaseResponse(releaseResponseDoc);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        String refNum = UtilDateTime.nowAsString();
        result.put("releaseResult", Boolean.TRUE);
        result.put("releaseAmount", context.get("releaseAmount"));
        result.put("releaseRefNum", refNum);
        result.put("releaseAltRefNum", refNum);
        result.put("releaseFlag", "U");
        result.put("releaseMessage", "This is a dummy release");
        return result;
    }

    public static Map<String, Object> doCapture(DispatchContext dctx, Map<String, Object> context)
    {
        Debug.logInfo("PayNetz - Entered paymentCapture", module);
        Debug.logInfo("PayNetz paymentCapture context : " + context, module);

        GenericValue paymentPref = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTrans = (GenericValue) context.get("authTrans");
        if (authTrans == null)
        {
            authTrans = PaymentGatewayServices.getAuthTransaction(paymentPref);
        }
        BigDecimal captureAmount = (BigDecimal) context.get("captureAmount");

        Map<String, String> props = buildPayNetzProperties(dctx, context, PaymentGatewayServices.CAPTURE_SERVICE_TYPE);

        //start - Capture parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        //Payment is already captured so return success response

        Debug.logInfo("Capture parameters -> " + parameters, module);
        //end - Capture parameters

//        Document captureResponseDoc = null;
//        try 
//        {
//            captureResponseDoc = sendRequest(props.get("apiUrl"), parameters);
//        }
//        catch (GeneralException ge)
//        {
//            return ServiceUtil.returnError(ge.getMessage());
//        }
//        return processCaptureResponse(captureResponseDoc);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        String refNum = UtilDateTime.nowAsString();
        result.put("captureResult", Boolean.TRUE);
        result.put("captureAmount", context.get("captureAmount"));
        result.put("captureRefNum", refNum);
        result.put("captureAltRefNum", refNum);
        result.put("captureFlag", "C");
        result.put("captureMessage", "Payment is dummy captureed");
        return result;
    }

    public static Map<String, Object> doRefund (DispatchContext dctx, Map<String, Object> context)
    {
        Debug.logInfo("PayNetz - Entered paymentRefund", module);
        Debug.logInfo("PayNetz paymentRefund context : " + context, module);

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        BigDecimal refundAmount = (BigDecimal) context.get("refundAmount");
        GenericValue captureTrans = PaymentGatewayServices.getCaptureTransaction(orderPaymentPreference);

        Map<String, String> props = buildPayNetzProperties(dctx, context, PaymentGatewayServices.REFUND_SERVICE_TYPE);

        //start - Refund parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        // Currently No API is defined for Refund so return success response

        Debug.logInfo("Refund parameters -> " + parameters, module);
        //end - Refund parameters

//        Document refundResponseDoc = null;
//        try
//        {
//            refundResponseDoc = sendRequest(props.get("apiUrl"), parameters);
//        }
//        catch (GeneralException ge)
//        {
//            return ServiceUtil.returnError(ge.getMessage());
//        }
//        return processRefundResponse(refundResponseDoc);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("refundResult", Boolean.TRUE);
        result.put("refundAmount", context.get("refundAmount"));
        result.put("refundRefNum", UtilDateTime.nowAsString());
        result.put("refundFlag", "R");
        result.put("refundMessage", "This is a dummy refund");
        return result;
    }

    public static Document sendRequest(String serverURL, Map<String, Object> parameters, boolean usePost) throws GeneralException 
    {
        String response = null;
        try
        {
        	if (usePost)
        	{
                HttpClient http = new HttpClient(serverURL, parameters);
                http.setAllowUntrusted(true);
                http.setDebug(true);
                response = http.post(); //might be an exception warn log for host certification
        	}
        	else
        	{
                response = sendGetHttpRequest(serverURL, parameters);
        	}
        }
        catch (HttpClientException hce)
        {
			Debug.logError(hce, hce.toString(), module);
            throw new GeneralException("PayNetz api connection problem", hce);
        }

        Document responseDocument = null;
        try 
        {
            responseDocument = UtilXml.readXmlDocument(response, false);
        }
        catch (SAXException se)
        {
            throw new GeneralException("Error reading response Document from a String: " + se.getMessage());
        }
        catch (ParserConfigurationException pce)
        {
            throw new GeneralException("Error reading response Document from a String: " + pce.getMessage());
        }
        catch (IOException ioe)
        {
            throw new GeneralException("Error reading response Document from a String: " + ioe.getMessage());
        }
        return responseDocument;
    }

    private static String sendGetHttpRequest(String serverURL, Map<String, Object> parameters) throws HttpClientException
    {
        String arguments = null;
        String resultString = "";
        if (serverURL == null)
        {
            throw new HttpClientException("Cannot process a null URL.");
        }

        if (UtilValidate.isNotEmpty(parameters))
        {
            arguments = UtilHttp.urlEncodeArgs(parameters, false);
        }
        // Append the arguments to the query string if GET.
        if (arguments != null)
        {
            if (serverURL.contains("?"))
            {
                serverURL = serverURL + "&" + arguments;
            }
            else
            {
                serverURL = serverURL + "?" + arguments;
            }
        }
        try 
        {
            URL requestUrl = new URL(serverURL);
            URLConnection con = URLConnector.openUntrustedConnection(requestUrl);
            // connection settings
            con.setDoOutput(true);
            con.setUseCaches(false);
            InputStream in = con.getInputStream();
            if (UtilValidate.isNotEmpty(in))
            {
            	resultString = UtilIO.readString(in);
            }
        }
        catch (Exception e)
        {
            throw new HttpClientException("Error processing request", e);
        }
        return resultString;
    }

    private static synchronized String getPayNetzCheckoutToken()
    {
        String payNetzcheckoutToken = UtilDateTime.nowAsString();
        return payNetzcheckoutToken;
    }


    private static void validateParam(Map<String, Object> parameters, String paramName, String paramValue, boolean isRequired) throws IllegalArgumentException
    {
        if (UtilValidate.isNotEmpty(paramValue))
        {
            parameters.put(paramName, paramValue);
        }
        else
        {
            if (isRequired)
            {
                throw new IllegalArgumentException(paramName + " has an invalid value '" + paramValue + "'");
            }
            else
            {
                parameters.put(paramName, "NOT_APPLICABLE");
            }
        }
    }

    private static Map<String, Object> addCartDetails(DispatchContext dctx, Map<String, ? extends Object> context) throws Exception
    {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> parameters = new LinkedHashMap<String, Object>();

        //add amount in parameters
        validateParam(parameters, "amt", cart.getGrandTotal().subtract(cart.getPaymentTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString(), true);

        //add currency in parameters
        validateParam(parameters, "txncurr", cart.getCurrency(), true);

        // Retrieves the orderPartyId
        String orderPartyId = cart.getOrderPartyId();
        if (UtilValidate.isEmpty(orderPartyId) && UtilValidate.isNotEmpty(userLogin))
        {
            orderPartyId = userLogin.getString("partyId");
        }

        //add party id in parameters
        validateParam(parameters, "clientcode", Base64.base64Encode(orderPartyId), false);

        //get party name
//        String partyName = PartyHelper.getPartyName(delegator, orderPartyId, false);
//        validateParam(parameters, "udf1", partyName, false);

        //get party email address
//        String emailAddress = null;
//        Map<String, Object> emailResults = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", orderPartyId, "userLogin", userLogin));
//        if (ModelService.RESPOND_SUCCESS.equals(emailResults.get(ModelService.RESPONSE_MESSAGE)))
//        {
//            emailAddress = (String) emailResults.get("emailAddress");
//        }
//        validateParam(parameters, "udf2", emailAddress, false);

        //get party contact number
//        String phone = null;
//        Map<String, Object> phoneResults = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("partyId", orderPartyId, "userLogin", userLogin));
//        if (ModelService.RESPOND_SUCCESS.equals(phoneResults.get(ModelService.RESPONSE_MESSAGE)))
//        {
//            String areaCode = (String) phoneResults.get("areaCode");
//            String contactNum = (String) phoneResults.get("contactNumber");
//            if (areaCode == null) 
//            {
//                phone =  contactNum;
//            } 
//            else
//            {
//                phone =  areaCode + "-" + contactNum;
//            }
//        }
//        validateParam(parameters, "udf3", phone, false);

        //customer billing address
//        String billingContactMechId = cart.getContactMech("BILLING_LOCATION");
//        GenericValue billingAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", billingContactMechId), true);
//        String address = billingAddress.getString("address1");
//        if (UtilValidate.isNotEmpty(billingAddress.getString("address2"))) 
//        {
//            address = address+" "+billingAddress.getString("address2");
//        }
//        if (UtilValidate.isNotEmpty(billingAddress.getString("address3")))
//        {
//            address = address+" "+billingAddress.getString("address3");
//        }
//        if (UtilValidate.isNotEmpty(billingAddress.getString("address3")))
//        {
//            address = address+" "+billingAddress.getString("address3");
//        }
//        if (UtilValidate.isNotEmpty(billingAddress.getString("city")))
//        {
//            address = address+" "+billingAddress.getString("city");
//        }
//        if (UtilValidate.isNotEmpty(billingAddress.getString("stateProvinceGeoId")))
//        {
//            address = address+" "+billingAddress.getString("stateProvinceGeoId");
//        }
//        if (UtilValidate.isNotEmpty(billingAddress.getString("countryGeoId")))
//        {
//            address = address+" "+billingAddress.getString("countryGeoId");
//        }
//        if (UtilValidate.isNotEmpty(billingAddress.getString("postalCode")))
//        {
//            address = address+" "+billingAddress.getString("postalCode");
//        }
//        
//        validateParam(parameters, "udf3", address, false);
        return parameters;
    }

    private static Map<String, String> buildPayNetzProperties(DispatchContext dctx, Map<String, ? extends Object> context, String paymentServiceTypeEnumId)
    {
        Delegator delegator = dctx.getDelegator();
        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");
        GenericValue payNetzGatewayConfig = null;
        Map<String, String> payNetzConfig = new HashMap<String, String>();

        if (paymentGatewayConfigId == null)
        {
            String productStoreId = null;
            GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
            if (orderPaymentPreference != null)
            {
                OrderReadHelper orh = new OrderReadHelper(delegator, orderPaymentPreference.getString("orderId"));
                productStoreId = orh.getProductStoreId();
            }
            else
            {
                ShoppingCart cart = (ShoppingCart) context.get("cart");
                if (cart != null)
                {
                    productStoreId = cart.getProductStoreId();
                }
            }
            if (productStoreId != null)
            {
                GenericValue payNetzPaymentSetting = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, "EXT_PAYNETZ", paymentServiceTypeEnumId, true);
                if (payNetzPaymentSetting != null)
                {
                    paymentGatewayConfigId = payNetzPaymentSetting.getString("paymentGatewayConfigId");
                }
            }
        }
        if (paymentGatewayConfigId != null)
        {
            try 
            {
                payNetzGatewayConfig = delegator.findOne("PaymentGatewayPayNetz", true, "paymentGatewayConfigId", paymentGatewayConfigId);
            }
            catch (GenericEntityException e) 
            {
                Debug.logError(e, module);
            }
        }

        if (UtilValidate.isNotEmpty(payNetzGatewayConfig))
        {
            Map<String, Object> tmp = payNetzGatewayConfig.getAllFields();
            Set<String> keys = tmp.keySet();
            for (String key : keys) 
            {
                String value = "";
                if (UtilValidate.isNotEmpty(tmp.get(key)))
                {
                    value = tmp.get(key).toString();
                }
                payNetzConfig.put(key, value);
            }
        }

        Debug.logInfo("PayNetz Configuration : " + payNetzConfig.toString(), module);
        return payNetzConfig;
    }

    private static Map<String, Object> processResponse(Document responseDocument)
    {
        /*<?xml version="1.0" encoding="UTF-8" ?>
        <MMP>
            <MERCHANT>
                <RESPONSE>
                    <url>http://203.114.240.77/paynetz/epi/fts</url>
                    <param name="ttype">NBFundTransfer</param>
                    <param name="tempTxnId">2418</param>
                    <param name="token">ClT6Sr4mjGhvFpsiBaUwaorib62ihEwLUh69hNKBUAE%3D</param>
                    <param name="txnStage">1</param>
                </RESPONSE>
            </MERCHANT>
        </MMP>*/

        Map<String, Object> result = FastMap.newInstance();
        
        if(UtilValidate.isNotEmpty(responseDocument))
        {
            Element outputElement = responseDocument.getDocumentElement();
            result.put("url", UtilXml.elementValue((Element)outputElement.getElementsByTagName("url").item(0)));
    
            NodeList params = outputElement.getElementsByTagName("param");
            for (int i = 0; i < params.getLength(); i++) 
            {
                Element param = (Element) params.item(i);
                if (param.getNodeType() == Node.ELEMENT_NODE)
                {
                    result.put(UtilXml.elementAttribute(param, "name", null), UtilXml.elementValue(param));
                }
            }
        }
        return result;
    }

    private static Map<String, Object> processAuthResponse(Document responseDocument)
    {

       Element outputElement = responseDocument.getDocumentElement();
       Map<String, Object> result = ServiceUtil.returnSuccess();

       String errorCode = UtilXml.elementAttribute(outputElement, "errorCode", null);
       if (UtilValidate.isNotEmpty(errorCode))
       {
           result.put("authResult", Boolean.valueOf(false));
           result.put("processAmount", BigDecimal.ZERO);
           result.put("authMessage", UtilXml.elementAttribute(outputElement, "error", null));
       }
       else 
       {
           result.put("authResult", Boolean.valueOf(true));
           result.put("authCode", UtilXml.elementAttribute(outputElement, "paymentId", null));
           String authAmountStr = UtilXml.elementAttribute(outputElement, "amount", null);
           result.put("processAmount", new BigDecimal(authAmountStr));
           result.put("authRefNum", UtilXml.elementAttribute(outputElement, "referenceNo", null));
           result.put("authFlag", UtilXml.elementAttribute(outputElement, "status", null));
           result.put("authMessage", UtilXml.elementAttribute(outputElement, "transactionType", null));
       }
       try 
       {
           result.put("internalRespMsgs", UtilMisc.toList(UtilXml.writeXmlDocument(responseDocument)));
       }
       catch (Exception e)
       {
           Debug.logError(e, e.toString(), module);
       }
       return result;
    }

    private static Map<String, Object> processReleaseResponse(Document responseDocument) 
    {

      Element outputElement = responseDocument.getDocumentElement();
      Map<String, Object> result = ServiceUtil.returnSuccess();

      String errorCode = UtilXml.elementAttribute(outputElement, "errorCode", null);
      if (UtilValidate.isNotEmpty(errorCode))
      {
          result.put("releaseResult", Boolean.valueOf(true)); //because PAYNETZ integrated as auto capture; set false if auto capture is not integrated
          result.put("releaseAmount", BigDecimal.ZERO);
           result.put("releaseMessage", UtilXml.elementAttribute(outputElement, "error", null));
      } 
      else
      {
           result.put("releaseResult", Boolean.valueOf(true));
           result.put("releaseCode", UtilXml.elementAttribute(outputElement, "paymentId", null));
           String authAmountStr = UtilXml.elementAttribute(outputElement, "amount", null);
           result.put("releaseAmount", new BigDecimal(authAmountStr));
           result.put("releaseRefNum", UtilXml.elementAttribute(outputElement, "referenceNo", null));
           result.put("releaseFlag", UtilXml.elementAttribute(outputElement, "status", null));
           result.put("releaseMessage", UtilXml.elementAttribute(outputElement, "transactionType", null));
      }
      try 
      {
          result.put("internalRespMsgs", UtilMisc.toList(UtilXml.writeXmlDocument(responseDocument)));
      } 
      catch (Exception e)
      {
          Debug.logError(e, e.toString(), module);
      }
      return result;
    }


    private static Map<String, Object> processCaptureResponse(Document responseDocument) {

      Element outputElement = responseDocument.getDocumentElement();
      Map<String, Object> result = ServiceUtil.returnSuccess();

      String errorCode = UtilXml.elementAttribute(outputElement, "errorCode", null);
      if (UtilValidate.isNotEmpty(errorCode))
      {
          result.put("captureResult", Boolean.valueOf(false));
          result.put("captureAmount", BigDecimal.ZERO);
           result.put("captureMessage", UtilXml.elementAttribute(outputElement, "error", null));
      } 
      else
      {
           result.put("captureResult", Boolean.valueOf(true));
           result.put("captureCode", UtilXml.elementAttribute(outputElement, "paymentId", null));
           String authAmountStr = UtilXml.elementAttribute(outputElement, "amount", null);
           result.put("captureAmount", new BigDecimal(authAmountStr));
           result.put("captureRefNum", UtilXml.elementAttribute(outputElement, "referenceNo", null));
           result.put("captureFlag", UtilXml.elementAttribute(outputElement, "status", null));
           result.put("captureMessage", UtilXml.elementAttribute(outputElement, "transactionType", null));
      }
      try
      {
          result.put("internalRespMsgs", UtilMisc.toList(UtilXml.writeXmlDocument(responseDocument)));
      }
      catch (Exception e) 
      {
          Debug.logError(e, e.toString(), module);
      }
      return result;
    }

    private static Map<String, Object> processRefundResponse(Document responseDocument) {

      Element outputElement = responseDocument.getDocumentElement();
      Map<String, Object> result = ServiceUtil.returnSuccess();

      String errorCode = UtilXml.elementAttribute(outputElement, "errorCode", null);
      if (UtilValidate.isNotEmpty(errorCode))
      {
          result.put("refundResult", Boolean.valueOf(false));
          result.put("refundAmount", BigDecimal.ZERO);
           result.put("refundMessage", UtilXml.elementAttribute(outputElement, "error", null));
      } 
      else
      {
           result.put("refundResult", Boolean.valueOf(true));
           result.put("refundCode", UtilXml.elementAttribute(outputElement, "paymentId", null));
           String authAmountStr = UtilXml.elementAttribute(outputElement, "amount", null);
           result.put("refundAmount", new BigDecimal(authAmountStr));
           result.put("refundRefNum", UtilXml.elementAttribute(outputElement, "referenceNo", null));
           result.put("refundFlag", UtilXml.elementAttribute(outputElement, "status", null));
           result.put("refundMessage", UtilXml.elementAttribute(outputElement, "transactionType", null));
      }
      try
      {
          result.put("internalRespMsgs", UtilMisc.toList(UtilXml.writeXmlDocument(responseDocument)));
      } 
      catch (Exception e) 
      {
          Debug.logError(e, e.toString(), module);
      }
      return result;
    }

    /**
     * The Class PayNetzTokenWrapper.
     */
    @SuppressWarnings("serial")
    public static class PayNetzTokenWrapper implements Serializable 
    {
        
        /** The string. */
        String theString;
        
        /** The token paynetz wrapper map. */
        private static Map<String, PayNetzTokenWrapper> tokenPayNetzWrapperMap = FastMap.newInstance();
        
        /**
         * Instantiates a new paynetz token wrapper.
         *
         * @param theString the the string
         */
        public PayNetzTokenWrapper(String theString)
        {
            this.theString = theString;
            tokenPayNetzWrapperMap.put(theString, this);
        }

        /**
         * Gets the token wrapper.
         *
         * @param theString the the string
         * @return the token wrapper
         */
        public static PayNetzTokenWrapper getTokenWrapper(String theString)
        {
            return tokenPayNetzWrapperMap.get(theString);
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null) {
                return false;
            }
            if (!(o instanceof PayNetzTokenWrapper))
            {
                return false;
            }
            PayNetzTokenWrapper other = (PayNetzTokenWrapper) o;
            return theString.equals(other.theString);
        }
        
        @Override
        public int hashCode()
        {
            return theString.hashCode();
        }
    }
}
