package com.osafe.services.ebs;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javolution.util.FastMap;

import org.ofbiz.accounting.payment.PaymentGatewayServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
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

/**
 * The Class EBSPaymentServices.
 */
public class EBSPaymentServices
{
    
    public static final String module = EBSPaymentServices.class.getName();

    /**
     * The token cart map.
     * 
     * Used to maintain a weak reference to the ShoppingCart for customers who
     * have gone to EBS to checkout so that we can quickly grab the cart,
     * perform shipment estimates and send the info back to EBS. The weak key is
     * a simple wrapper for the checkout token String and is stored as a cart
     * attribute. The value is a weak reference to the ShoppingCart itself.
     * Entries will be removed as carts are removed from the session (i.e. on
     * cart clear or successful checkout) or when the session is destroyed
     * */
    private static Map<EBSTokenWrapper, WeakReference<ShoppingCart>> tokenCartMap = new WeakHashMap<EBSTokenWrapper, WeakReference<ShoppingCart>>();

    public static Map<String, Object> setEbsChekout(DispatchContext dctx, Map<String, ? extends Object> context)
    {
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        Locale locale = cart.getLocale();
        Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        if (cart == null || cart.items().size() <= 0)
        {
            return ServiceUtil.returnError("Shopping cart is empty, cannot proceed with EBS Checkout");
        }

        Map<String, String> props = buildEbsProperties(dctx, context, null);
        if (props == null) 
        {
            return ServiceUtil.returnError("Couldn't retrieve a PaymentGatewayEbs record for Ebs Checkout, cannot continue.");
        }

        // Cart information
        String refToken = getEbsCheckoutToken();
        try
        {
            parameters = addCartDetails(dctx, context);
            EBSPaymentUtil.validateParam(parameters, "reference_no", refToken, true);
            EBSPaymentUtil.validateParam(parameters, "account_id", props.get("merchantId"), true);
            EBSPaymentUtil.validateParam(parameters, "mode", props.get("ebsMode"), true);
            EBSPaymentUtil.validateParam(parameters, "description", "Ebs Payment", true);
            EBSPaymentUtil.validateParam(parameters, "return_url", props.get("returnUrl"), true);
            EBSPaymentUtil.validateParam(parameters, "secure_hash", EBSPaymentUtil.getSecureHash(props.get("secretKey"), parameters), true);
        }
        catch (Exception e)
        {
            Debug.logError(e, module);
            return ServiceUtil.returnError("An error occurred while retreiving cart details");
        }

        String encodedParameters = UtilHttp.urlEncodeArgs(parameters, false);
        cart.setAttribute("ebsCheckoutToken", refToken);
        cart.setAttribute("ebsCheckoutRedirectParam", encodedParameters);
        EBSTokenWrapper tokenWrapper = new EBSTokenWrapper(refToken);
        EBSPaymentServices.tokenCartMap.put(tokenWrapper, new WeakReference<ShoppingCart>(cart));
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> getEbsCheckout(DispatchContext dctx, Map<String, ? extends Object> context)
    {
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        Map<String, Object> ebsResponse = (Map) context.get("ebsResponse");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = dctx.getDispatcher();

        String transactionId = (String) ebsResponse.get("TransactionID");
        String responseCode = (String) ebsResponse.get("ResponseCode");
        String paymentId = (String) ebsResponse.get("PaymentID");
        String amount = (String) ebsResponse.get("Amount");
        String merchantReferenceNum = (String) ebsResponse.get("MerchantRefNo");
        String responseMessage = (String) ebsResponse.get("ResponseMessage");
        String dateCreated = (String) ebsResponse.get("DateCreated");
        String isFlagged = (String) ebsResponse.get("IsFlagged");
        String paymentMethod = (String) ebsResponse.get("PaymentMethod");

        String token = merchantReferenceNum;
        WeakReference<ShoppingCart> weakCart = tokenCartMap.get(EBSTokenWrapper.getTokenWrapper(token));
        if (weakCart != null)
        {
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
        inMap.put("transactionId", transactionId);
        inMap.put("responseCode", responseCode);
        inMap.put("responseMessage", responseMessage);
        inMap.put("dateCreated", dateCreated);
        inMap.put("paymentId", paymentId);
        inMap.put("merchantReferenceNum", merchantReferenceNum);
        inMap.put("isFlagged", isFlagged);
        inMap.put("amount", amount);
        inMap.put("paymentMethod", paymentMethod);

        Map<String, Object> outMap = null;
        try
        {
            outMap = dispatcher.runSync("createEbsPaymentMethod", inMap);
        }
        catch (GenericServiceException e)
        {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        String paymentMethodId = (String) outMap.get("paymentMethodId");
        Debug.log("gotEbsPayment" + paymentMethodId, module);

        //cart.clearPayments(); for support the multiple payment
        BigDecimal maxAmount = cart.getGrandTotal().subtract(cart.getPaymentTotal()).setScale(2, BigDecimal.ROUND_HALF_UP);
        cart.addPaymentAmount(paymentMethodId, maxAmount, true);

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> doAuthorization(DispatchContext dctx, Map<String, Object> context)
    {
        Debug.logInfo("EBS - Entered paymentAuthorisation", module);
        Debug.logInfo("EBS paymentAuthorisation context : " + context, module);

        GenericValue ebsPaymentMethod = null;
        try
        {
            GenericValue paymentPreference = (GenericValue) context.get("orderPaymentPreference");
            GenericValue paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
            ebsPaymentMethod = paymentMethod.getRelatedOne("EbsPaymentMethod");
        } 
        catch (GenericEntityException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }

        Map<String, String> props = buildEbsProperties(dctx, context, PaymentGatewayServices.AUTH_SERVICE_TYPE);

        //start - authorization parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("Action", "status");
        parameters.put("TransactionID", ebsPaymentMethod.getString("transactionId"));
        parameters.put("SecretKey", props.get("secretKey"));
        parameters.put("AccountID", props.get("merchantId"));
        parameters.put("PaymentID", ebsPaymentMethod.getString("paymentId"));

        Debug.logInfo("authorization parameters -> " + parameters, module);
        //end - authorization parameters

        Document authResponseDoc = null;
        try
        {
            authResponseDoc = EBSPaymentUtil.sendRequest(props.get("apiUrl"), parameters);
        } 
        catch (GeneralException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }
        return processAuthResponse(authResponseDoc);
   }

   public static Map<String, Object> doRelease(DispatchContext dctx, Map<String, Object> context)
   {
       Debug.logInfo("EBS - Entered paymentRelease", module);
       Debug.logInfo("EBS paymentRelease context : " + context, module);

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTrans = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        BigDecimal releaseAmount = (BigDecimal) context.get("releaseAmount");

        Map<String, String> props = buildEbsProperties(dctx, context, PaymentGatewayServices.RELEASE_SERVICE_TYPE);

        //start - Release parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("Action", "cancel");
        parameters.put("SecretKey", props.get("secretKey"));
        parameters.put("AccountID", props.get("merchantId"));
        parameters.put("Amount", releaseAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        parameters.put("PaymentID", authTrans.getString("gatewayCode"));

        Debug.logInfo("Capture parameters -> " + parameters, module);
        //end - Release parameters

        Document releaseResponseDoc = null;
        try
        {
            releaseResponseDoc = EBSPaymentUtil.sendRequest(props.get("apiUrl"), parameters);
        }
        catch (GeneralException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }
        return processReleaseResponse(releaseResponseDoc);
    }


    public static Map<String, Object> doCapture(DispatchContext dctx, Map<String, Object> context)
    {
        Debug.logInfo("EBS - Entered paymentCapture", module);
        Debug.logInfo("EBS paymentCapture context : " + context, module);

        GenericValue paymentPref = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTrans = (GenericValue) context.get("authTrans");
        if (authTrans == null)
        {
            authTrans = PaymentGatewayServices.getAuthTransaction(paymentPref);
        }
        BigDecimal captureAmount = (BigDecimal) context.get("captureAmount");

        boolean isPaymentFlagged = isPaymentFlagged(context);
        if (isPaymentFlagged)
        {
            Document paymentInquireResponseDoc = paymentInquire(dctx, context);
	        Map<String, Object> paymentInquireResponse = processResponse(paymentInquireResponseDoc);
	        if ((Boolean)paymentInquireResponse.get("result"))
	        {
	            String currentPaymentFlagged = (String) paymentInquireResponse.get("isFlagged");
	            if (currentPaymentFlagged.equalsIgnoreCase("NO"))
	            {
	                return processCaptureResponse(paymentInquireResponseDoc);
	            }
	        }
        }

        Map<String, String> props = buildEbsProperties(dctx, context, PaymentGatewayServices.CAPTURE_SERVICE_TYPE);

        //start - Capture parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("Action", "capture");
        parameters.put("SecretKey", props.get("secretKey"));
        parameters.put("AccountID", props.get("merchantId"));
        parameters.put("Amount", captureAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        parameters.put("PaymentID", authTrans.getString("gatewayCode"));

        Debug.logInfo("Capture parameters -> " + parameters, module);
        //end - Capture parameters

        Document captureResponseDoc = null;
        try
        {
            captureResponseDoc = EBSPaymentUtil.sendRequest(props.get("apiUrl"), parameters);
        } 
        catch (GeneralException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }
        return processCaptureResponse(captureResponseDoc);
    }

    public static Map<String, Object> doRefund (DispatchContext dctx, Map<String, Object> context)
    {
        Debug.logInfo("EBS - Entered paymentRefund", module);
        Debug.logInfo("EBS paymentRefund context : " + context, module);

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        BigDecimal refundAmount = (BigDecimal) context.get("refundAmount");
        GenericValue captureTrans = PaymentGatewayServices.getCaptureTransaction(orderPaymentPreference);

        Map<String, String> props = buildEbsProperties(dctx, context, PaymentGatewayServices.REFUND_SERVICE_TYPE);

        //start - Refund parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("Action", "refund");
        parameters.put("SecretKey", props.get("secretKey"));
        parameters.put("AccountID", props.get("merchantId"));
        parameters.put("Amount", refundAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        parameters.put("PaymentID", captureTrans.getString("gatewayCode"));

        Debug.logInfo("Refund parameters -> " + parameters, module);
        //end - Refund parameters

        Document refundResponseDoc = null;
        try
        {
            refundResponseDoc = EBSPaymentUtil.sendRequest(props.get("apiUrl"), parameters);
        }
        catch (GeneralException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }
        return processRefundResponse(refundResponseDoc);
    }

    private static synchronized String getEbsCheckoutToken()
    {
        String ebscheckoutToken = UtilDateTime.nowAsString();
        return ebscheckoutToken;
    }

    private static Map<String, Object> addCartDetails(DispatchContext dctx, Map<String, ? extends Object> context) throws Exception{
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> parameters = new LinkedHashMap<String, Object>();

        //add amount in parameters
        EBSPaymentUtil.validateParam(parameters, "amount", cart.getGrandTotal().subtract(cart.getPaymentTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString(), true);

        // Retrieves the orderPartyId
        String orderPartyId = cart.getOrderPartyId();
        if (UtilValidate.isEmpty(orderPartyId) && UtilValidate.isNotEmpty(userLogin)){
            orderPartyId = userLogin.getString("partyId");
        }

        //get party name
        String partyName = PartyHelper.getPartyName(delegator, orderPartyId, false);

        //get party email address
        String emailAddress = null;
        Map<String, Object> emailResults = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", orderPartyId, "userLogin", userLogin));
        if (ModelService.RESPOND_SUCCESS.equals(emailResults.get(ModelService.RESPONSE_MESSAGE)))
        {
            emailAddress = (String) emailResults.get("emailAddress");
        }
        EBSPaymentUtil.validateParam(parameters, "email", emailAddress, false);

        //get party contact number
        String phone = null;
        Map<String, Object> phoneResults = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("partyId", orderPartyId, "userLogin", userLogin));
        if (ModelService.RESPOND_SUCCESS.equals(phoneResults.get(ModelService.RESPONSE_MESSAGE)))
        {
            String areaCode = (String) phoneResults.get("areaCode");
            String contactNum = (String) phoneResults.get("contactNumber");
            if (areaCode == null)
            {
                phone =  contactNum;
            }
            else
            {
                phone =  areaCode + "-" + contactNum;
            }
        }

        //customer billing address
        String billingContactMechId = cart.getContactMech("BILLING_LOCATION");
        GenericValue billingAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", billingContactMechId), true);
        String address = billingAddress.getString("address1");
        if (UtilValidate.isNotEmpty(billingAddress.getString("address2")))
        {
            address = address+" "+billingAddress.getString("address2");
        }
        if (UtilValidate.isNotEmpty(billingAddress.getString("address3")))
        {
            address = address+" "+billingAddress.getString("address3");
        }
        String billName = billingAddress.getString("toName");
        if (UtilValidate.isEmpty(billName))
        {
            billName = partyName;
        }
        EBSPaymentUtil.validateParam(parameters, "name", billName, false);
        EBSPaymentUtil.validateParam(parameters, "address", address, false);
        EBSPaymentUtil.validateParam(parameters, "city", billingAddress.getString("city"), false);
        EBSPaymentUtil.validateParam(parameters, "state", billingAddress.getString("stateProvinceGeoId"), false);
        EBSPaymentUtil.validateParam(parameters, "country", billingAddress.getString("countryGeoId"), false);
        EBSPaymentUtil.validateParam(parameters, "postal_code", billingAddress.getString("postalCode"), false);
        EBSPaymentUtil.validateParam(parameters, "phone", phone, false);

        //customer shipping address
        GenericValue shippingAddress = cart.getShippingAddress();
        String shipAddress = shippingAddress.getString("address1");
        if (UtilValidate.isNotEmpty(shippingAddress.getString("address2")))
        {
            shipAddress = shipAddress+" "+shippingAddress.getString("address2");
        }
        if (UtilValidate.isNotEmpty(shippingAddress.getString("address3")))
        {
            shipAddress = shipAddress+" "+shippingAddress.getString("address3");
        }
        String shipName = shippingAddress.getString("toName");
        if (UtilValidate.isEmpty(shipName))
        {
            shipName = partyName;
        }
        EBSPaymentUtil.validateParam(parameters, "ship_name", shipName, false);
        EBSPaymentUtil.validateParam(parameters, "ship_address", shipAddress, false);
        EBSPaymentUtil.validateParam(parameters, "ship_city", shippingAddress.getString("city"), false);
        EBSPaymentUtil.validateParam(parameters, "ship_state", shippingAddress.getString("stateProvinceGeoId"), false);
        EBSPaymentUtil.validateParam(parameters, "ship_country", shippingAddress.getString("countryGeoId"), false);
        EBSPaymentUtil.validateParam(parameters, "ship_postal_code", shippingAddress.getString("postalCode"), false);
        EBSPaymentUtil.validateParam(parameters, "ship_phone", phone, false);
        return parameters;
    }

    private static Map<String, String> buildEbsProperties(DispatchContext dctx, Map<String, ? extends Object> context, String paymentServiceTypeEnumId)
    {
        Delegator delegator = dctx.getDelegator();
        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");
        GenericValue payPalGatewayConfig = null;
        Map<String, String> ebsConfig = new HashMap<String, String>();

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
                GenericValue payPalPaymentSetting = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, "EXT_EBS", paymentServiceTypeEnumId, true);
                if (payPalPaymentSetting != null)
                {
                    paymentGatewayConfigId = payPalPaymentSetting.getString("paymentGatewayConfigId");
                }
            }
        }
        if (paymentGatewayConfigId != null)
        {
        	try
            {
                payPalGatewayConfig = delegator.findOne("PaymentGatewayEbs", true, "paymentGatewayConfigId", paymentGatewayConfigId);
            }
        	catch (GenericEntityException e)
            {
                Debug.logError(e, module);
            }
        }

        if (UtilValidate.isNotEmpty(payPalGatewayConfig))
        {
            Map<String, Object> tmp = payPalGatewayConfig.getAllFields();
            Set<String> keys = tmp.keySet();
            for (String key : keys)
            {
                String value = tmp.get(key).toString();
                ebsConfig.put(key, value);
            }
        }

        Debug.logInfo("EBS Configuration : " + ebsConfig.toString(), module);
        return ebsConfig;
    }

    private static boolean isPaymentFlagged(Map<String, Object> context)
    {
 	    boolean isPaymentFlagged = Boolean.FALSE;
        GenericValue paymentPref = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTrans = (GenericValue) context.get("authTrans");
        if (authTrans == null)
        {
            authTrans = PaymentGatewayServices.getAuthTransaction(paymentPref);
        }
        GenericValue ebsPaymentMethod = null;
        try
        {
            GenericValue paymentMethod = paymentPref.getRelatedOne("PaymentMethod");
            ebsPaymentMethod = paymentMethod.getRelatedOne("EbsPaymentMethod");
        }
        catch (GenericEntityException ge)
        {
            Debug.logError(ge, module);
        }
        if (UtilValidate.isNotEmpty(ebsPaymentMethod))
        {
            // If Flagged indicator of EbsPaymentMethod is empty then set it from previous gateway response
            if (UtilValidate.isEmpty(ebsPaymentMethod.getString("isFlagged")) && UtilValidate.isNotEmpty(authTrans))
            {
                try
                {
                    GenericValue paymentGatewayRespMsg = EntityUtil.getFirst(authTrans.getRelated("PaymentGatewayRespMsg"));
                    if (UtilValidate.isNotEmpty(paymentGatewayRespMsg) && UtilValidate.isNotEmpty(paymentGatewayRespMsg.getString("pgrMessage")))
                    {
                        Document pgrDocument = UtilXml.readXmlDocument(paymentGatewayRespMsg.getString("pgrMessage"), false);
                        Element outputElement = pgrDocument.getDocumentElement();
                        String isFlagged = UtilXml.elementAttribute(outputElement, "isFlagged", null);
                        ebsPaymentMethod.set("isFlagged", isFlagged);
                        ebsPaymentMethod.store();
                    }
                }
                catch (Exception exe)
                {
                    Debug.logError(exe, module);
                }
            }
            if (ebsPaymentMethod.getString("isFlagged").equalsIgnoreCase("YES"))
            {
                isPaymentFlagged = Boolean.TRUE;
            }
        }
 	   return isPaymentFlagged;
    }

    private static Document paymentInquire(DispatchContext dctx, Map<String, ? extends Object> context)
    {
        Debug.logInfo("EBS - Entered paymentInquire", module);
        Debug.logInfo("EBS paymentInquire context : " + context, module);

        Map<String, String> props = buildEbsProperties(dctx, context, PaymentGatewayServices.CAPTURE_SERVICE_TYPE);

        GenericValue ebsPaymentMethod = null;
        try
        {
            GenericValue paymentPreference = (GenericValue) context.get("orderPaymentPreference");
            GenericValue paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
            ebsPaymentMethod = paymentMethod.getRelatedOne("EbsPaymentMethod");
        }
        catch (GenericEntityException ge)
        {
            Debug.logError(ge, module);
        }

        //start - paymentInquire parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("Action", "statusByRef");
        parameters.put("SecretKey", props.get("secretKey"));
        parameters.put("AccountID", props.get("merchantId"));
        parameters.put("RefNo", ebsPaymentMethod.getString("merchantReferenceNum"));

        Debug.logInfo("paymentInquire parameters -> " + parameters, module);
        //end - paymentInquire parameters

        Document paymentInquireResponseDoc = null;
        try
        {
            paymentInquireResponseDoc = EBSPaymentUtil.sendRequest(props.get("apiUrl"), parameters);
        } 
        catch (GeneralException ge)
        {
            Debug.logError(ge, module);
        }

        return paymentInquireResponseDoc;
    }

    private static Map<String, Object> processResponse(Document responseDocument)
    {
       /*
        <output transactionId="2144154" paymentId="1112034" amount="1" dateTime="2010-07-31 16:59:28"
        mode="TEST" referenceNo="223" transactionType="Authorized" status="Processed" isFlagged="NO"/>
       */
       Element outputElement = responseDocument.getDocumentElement();
       Map<String, Object> result = new HashMap<String, Object>();

       String errorCode = UtilXml.elementAttribute(outputElement, "errorCode", null);
       if (UtilValidate.isNotEmpty(errorCode))
       {
           result.put("result", Boolean.valueOf(false));
           result.put("errorCode", UtilXml.elementAttribute(outputElement, "errorCode", null));
           result.put("error", UtilXml.elementAttribute(outputElement, "error", null));
       }
       else
       {
           result.put("result", Boolean.valueOf(true));
           result.put("transactionId", UtilXml.elementAttribute(outputElement, "transactionId", null));
           result.put("paymentId", UtilXml.elementAttribute(outputElement, "paymentId", null));
           result.put("amount", UtilXml.elementAttribute(outputElement, "amount", null));
           result.put("dateTime", UtilXml.elementAttribute(outputElement, "dateTime", null));
           result.put("mode", UtilXml.elementAttribute(outputElement, "mode", null));
           result.put("referenceNo", UtilXml.elementAttribute(outputElement, "referenceNo", null));
           result.put("transactionType", UtilXml.elementAttribute(outputElement, "transactionType", null));
           result.put("status", UtilXml.elementAttribute(outputElement, "status", null));
           result.put("isFlagged", UtilXml.elementAttribute(outputElement, "isFlagged", null));
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
           result.put("authRefNum", errorCode);
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
          result.put("releaseResult", Boolean.valueOf(true)); //because EBS integrated as auto capture; set false if auto capture is not integrated
          result.put("releaseAmount", BigDecimal.ZERO);
          result.put("releaseMessage", UtilXml.elementAttribute(outputElement, "error", null));
          result.put("releaseRefNum", errorCode);
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


    private static Map<String, Object> processCaptureResponse(Document responseDocument)
    {

      Element outputElement = responseDocument.getDocumentElement();
      Map<String, Object> result = ServiceUtil.returnSuccess();

      String errorCode = UtilXml.elementAttribute(outputElement, "errorCode", null);
      if (UtilValidate.isNotEmpty(errorCode))
      {
          result.put("captureResult", Boolean.valueOf(false));
          result.put("captureAmount", BigDecimal.ZERO);
          result.put("captureMessage", UtilXml.elementAttribute(outputElement, "error", null));
          result.put("captureRefNum", errorCode);
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

    private static Map<String, Object> processRefundResponse(Document responseDocument)
    {

      Element outputElement = responseDocument.getDocumentElement();
      Map<String, Object> result = ServiceUtil.returnSuccess();

      String errorCode = UtilXml.elementAttribute(outputElement, "errorCode", null);
      if (UtilValidate.isNotEmpty(errorCode))
      {
          result.put("refundResult", Boolean.valueOf(false));
          result.put("refundAmount", BigDecimal.ZERO);
          result.put("refundMessage", UtilXml.elementAttribute(outputElement, "error", null));
          result.put("refundRefNum",errorCode);
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
     * The Class EBSTokenWrapper.
     */
    @SuppressWarnings("serial")
    public static class EBSTokenWrapper implements Serializable
    {
        
        /** The string. */
        String theString;
        
        /** The token ebs wrapper map. */
        private static Map<String, EBSTokenWrapper> tokenEBSWrapperMap = FastMap.newInstance();
        
        /**
         * Instantiates a new eBS token wrapper.
         *
         * @param theString the the string
         */
        public EBSTokenWrapper(String theString)
        {
            this.theString = theString;
            tokenEBSWrapperMap.put(theString, this);
        }

        /**
         * Gets the token wrapper.
         *
         * @param theString the the string
         * @return the token wrapper
         */
        public static EBSTokenWrapper getTokenWrapper(String theString)
        {
            return tokenEBSWrapperMap.get(theString);
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null)
            {
                return false;
            }
            if (!(o instanceof EBSTokenWrapper))
            {
                return false;
            }
            EBSTokenWrapper other = (EBSTokenWrapper) o;
            return theString.equals(other.theString);
        }
        
        @Override
        public int hashCode()
        {
            return theString.hashCode();
        }
    }
}
