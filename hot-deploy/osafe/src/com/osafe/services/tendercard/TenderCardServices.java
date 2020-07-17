package com.osafe.services.tendercard;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;

import org.ofbiz.accounting.payment.PaymentGatewayServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.HttpClientException;
import org.ofbiz.base.util.URLConnector;
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
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * @author rrlakhera
 *
 */
public class TenderCardServices 
{

    public static final String module = TenderCardServices.class.getName();

    public static Map<String, Object> balanceInquire(DispatchContext dctx, Map<String, Object> context)
    {
        Map<String, String> props = buildTenderCardProperties(dctx, context, null);
        String cardNumber = (String) context.get("cardNumber");
        String CVV = (String) context.get("CVV");

        //start - balanceInquire parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("Type", "Balance");
        parameters.put("Lty", "false");
        parameters.put("TCID", props.get("tenderCardId"));
        parameters.put("Tid", props.get("terminalId"));
        parameters.put("Act", cardNumber);
        if (UtilValidate.isNotEmpty(CVV))
        {
            parameters.put("CVV", CVV);
        }
        parameters.put("Amt", 0);

        Debug.logInfo("balanceInquire parameters -> " + parameters, module);
        //end - balanceInquire parameters

        Document balanceInquireResponseDoc = null;
        try
        {
            balanceInquireResponseDoc = sendRequest(props.get("apiUrl"), parameters);
        }
        catch (GeneralException ge)
        {
            Debug.logError("balanceInquire error -> " + ge.getMessage(), module);

            Map<String, Object> result = ServiceUtil.returnFailure(ge.getMessage());
  	        result.put("processResult", Boolean.valueOf(false));
            result.put("balance", BigDecimal.ZERO);
            result.put("responseCode", "Error");
        	return result;
        }
        return processBalanceInquireResponse(balanceInquireResponseDoc);
    }

    public static Map<String, Object> doAuthorization(DispatchContext dctx, Map<String, Object> context)
    {
        Debug.logInfo("TenderCard - Entered paymentAuthorisation", module);
        Debug.logInfo("TenderCard paymentAuthorisation context : " + context, module);

        GenericValue giftCard = null;
        GenericValue paymentPreference = null;
        try
        {
            paymentPreference = (GenericValue) context.get("orderPaymentPreference");
            GenericValue paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
            giftCard = paymentMethod.getRelatedOne("GiftCard");
        } 
        catch (GenericEntityException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }

        Map<String, String> props = buildTenderCardProperties(dctx, context, PaymentGatewayServices.AUTH_SERVICE_TYPE);

        //start - authorization parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("Type", "Redeem");
        parameters.put("Lty", "false");
        parameters.put("TCID", props.get("tenderCardId"));
        parameters.put("Tid", props.get("terminalId"));
        parameters.put("Act", giftCard.getString("cardNumber"));
        parameters.put("Amt", paymentPreference.getBigDecimal("maxAmount").setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

        Debug.logInfo("authorization parameters -> " + parameters, module);
        //end - authorization parameters

        Document authResponseDoc = null;
        try
        {
            authResponseDoc = sendRequest(props.get("apiUrl"), parameters);
        }
        catch (GeneralException ge)
        {
            return ServiceUtil.returnError(ge.getMessage());
        }
        return processAuthResponse(authResponseDoc);
   }

    public static Map<String, Object> doRelease(DispatchContext dctx, Map<String, Object> context)
    {
        Debug.logInfo("TenderCard - Entered paymentRelease", module);
        Debug.logInfo("TenderCard paymentRelease context : " + context, module);

         GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
         GenericValue authTrans = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
         BigDecimal releaseAmount = (BigDecimal) context.get("releaseAmount");
         GenericValue giftCard = null;
         try
         {
             GenericValue paymentMethod = authTrans.getRelatedOne("PaymentMethod");
             giftCard = paymentMethod.getRelatedOne("GiftCard");
         } 
         catch (GenericEntityException ge)
         {
             return ServiceUtil.returnError(ge.getMessage());
         }

         Map<String, String> props = buildTenderCardProperties(dctx, context, PaymentGatewayServices.RELEASE_SERVICE_TYPE);

         //start - Release parameters
         Map<String, Object> parameters = new HashMap<String, Object>();
         parameters.put("Type", "Void");
         parameters.put("Lty", "false");
         parameters.put("TCID", props.get("tenderCardId"));
         parameters.put("Tid", props.get("terminalId"));
         parameters.put("Act", giftCard.getString("cardNumber"));
         parameters.put("Amt", releaseAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

         Debug.logInfo("Release parameters -> " + parameters, module);
         //end - Release parameters

         Document releaseResponseDoc = null;
         try
         {
         	releaseResponseDoc = sendRequest(props.get("apiUrl"), parameters);
         }
         catch (GeneralException ge)
         {
             return ServiceUtil.returnError(ge.getMessage());
         }
         return processReleaseResponse(releaseResponseDoc);
     }

     public static Map<String, Object> doCapture(DispatchContext dctx, Map<String, Object> context)
     {
         Debug.logInfo("TenderCard - Entered paymentCapture", module);
         Debug.logInfo("TenderCard paymentCapture context : " + context, module);

         GenericValue paymentPref = (GenericValue) context.get("orderPaymentPreference");
         GenericValue authTrans = (GenericValue) context.get("authTrans");
         if (authTrans == null) {
             authTrans = PaymentGatewayServices.getAuthTransaction(paymentPref);
         }
         BigDecimal captureAmount = (BigDecimal) context.get("processAmount");
         GenericValue giftCard = null;
         try
         {
             GenericValue paymentMethod = authTrans.getRelatedOne("PaymentMethod");
             giftCard = paymentMethod.getRelatedOne("GiftCard");
         } 
         catch (GenericEntityException ge)
         {
             return ServiceUtil.returnError(ge.getMessage());
         }

         Map<String, String> props = buildTenderCardProperties(dctx, context, PaymentGatewayServices.CAPTURE_SERVICE_TYPE);

         //start - Capture parameters
         Map<String, Object> parameters = new HashMap<String, Object>();
         // Tender does not provide the capture method so call the balance method
         parameters.put("Type", "Balance");
         parameters.put("Lty", "false");
         parameters.put("TCID", props.get("tenderCardId"));
         parameters.put("Tid", props.get("terminalId"));
         parameters.put("Act", giftCard.getString("cardNumber"));
         parameters.put("Amt", captureAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

         Debug.logInfo("Capture parameters -> " + parameters, module);
         //end - Capture parameters

         Document captureResponseDoc = null;
         try
         {
         	captureResponseDoc = sendRequest(props.get("apiUrl"), parameters);
         }
         catch (GeneralException ge)
         {
             return ServiceUtil.returnError(ge.getMessage());
         }
         // Tender does not provide the capture method so pass the capture amount
         return processCaptureResponse(captureAmount, captureResponseDoc);
     }

     public static Map<String, Object> doRefund (DispatchContext dctx, Map<String, Object> context)
     {
         Debug.logInfo("TenderCard - Entered paymentRefund", module);
         Debug.logInfo("TenderCard paymentRefund context : " + context, module);

         GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
         BigDecimal refundAmount = (BigDecimal) context.get("refundAmount");
         GenericValue captureTrans = PaymentGatewayServices.getCaptureTransaction(orderPaymentPreference);
         GenericValue giftCard = null;
         try
         {
             GenericValue paymentMethod = captureTrans.getRelatedOne("PaymentMethod");
             giftCard = paymentMethod.getRelatedOne("GiftCard");
         } 
         catch (GenericEntityException ge)
         {
             return ServiceUtil.returnError(ge.getMessage());
         }

         Map<String, String> props = buildTenderCardProperties(dctx, context, PaymentGatewayServices.REFUND_SERVICE_TYPE);

         //start - Refund parameters
         Map<String, Object> parameters = new HashMap<String, Object>();
         parameters.put("Type", "Credit");
         parameters.put("Lty", "false");
         parameters.put("TCID", props.get("tenderCardId"));
         parameters.put("Tid", props.get("terminalId"));
         parameters.put("Act", giftCard.getString("cardNumber"));
         parameters.put("Amt", refundAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

         Debug.logInfo("Refund parameters -> " + parameters, module);
         //end - Refund parameters

         Document refundResponseDoc = null;
         try
         {
         	refundResponseDoc = sendRequest(props.get("apiUrl"), parameters);
         }
         catch (GeneralException ge)
         {
             return ServiceUtil.returnError(ge.getMessage());
         }
         return processRefundResponse(refundResponseDoc);
     }

     private static Map<String, Object> processBalanceInquireResponse(Document responseDocument)
     {
        /*
         <error>error</error>
         OR
         <Resp v1:Bal="10100.00" v1:AuthAmt="100.00" v1:Msg=""
          v1:Auth="1953" v1:Amt="100.00" v1:RespCode="Success" v1:Loyalty="False"/>
         */
        Element outputElement = null;
        if(UtilValidate.isNotEmpty(responseDocument))
        {
            outputElement = responseDocument.getDocumentElement();
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();

        if (UtilValidate.isEmpty(outputElement) || outputElement.getNodeName().equalsIgnoreCase("error"))
        {
  	        result.put("processResult", Boolean.valueOf(false));
            result.put("balance", BigDecimal.ZERO);
            result.put("responseCode", "Error");
        }
        else 
        {
           String responseCode = UtilXml.elementAttribute(outputElement, "v1:RespCode", "Error");
           if (responseCode.equalsIgnoreCase(getSuccessStatusCode()))
           {
	 	       result.put("processResult", Boolean.valueOf(true));
	 	       String balanceAmountStr = UtilXml.elementAttribute(outputElement, "v1:Bal", null);
	 	       result.put("balance", new BigDecimal(balanceAmountStr));
	 	       result.put("responseCode", UtilXml.elementAttribute(outputElement, "v1:RespCode", null));
           }
           else
           {
     	       result.put("processResult", Boolean.valueOf(false));
               result.put("balance", BigDecimal.ZERO);
               result.put("responseCode", responseCode);
           }
        }
        try
        {
            Debug.logInfo("balanceInquire result -> " + UtilMisc.toList(UtilXml.writeXmlDocument(responseDocument)), module);
        }
        catch (Exception e)
        {
        }
        return result;
     }

    private static Map<String, Object> processAuthResponse(Document responseDocument)
    {
       /*
        <error>error</error>
        OR
        <Resp v1:Bal="4958.68" v1:AuthAmt="100.00" v1:Msg="" v1:Auth="1909" 
        v1:Amt="100.00" v1:RespCode="Success" v1:Loyalty="False"/>
        */
       Element outputElement = null;
       if(UtilValidate.isNotEmpty(responseDocument))
       {
           outputElement = responseDocument.getDocumentElement();
       }
       Map<String, Object> result = ServiceUtil.returnSuccess();

       if (UtilValidate.isEmpty(outputElement) || outputElement.getNodeName().equalsIgnoreCase("error"))
       {
           result.put("authResult", Boolean.valueOf(false));
           result.put("processAmount", BigDecimal.ZERO);
           result.put("authMessage", UtilValidate.isNotEmpty(outputElement)?UtilXml.elementValue(outputElement):"did not find any response");
       }
       else 
       {
           String responseCode = UtilXml.elementAttribute(outputElement, "v1:RespCode", "Error");
           if (responseCode.equalsIgnoreCase(getSuccessStatusCode()))
           {
    	       result.put("authResult", Boolean.valueOf(true));
    	       //result.put("captureResult", Boolean.valueOf(true));
    	       result.put("authCode", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
    	       //result.put("captureCode", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
    	       String authAmountStr = UtilXml.elementAttribute(outputElement, "v1:Amt", null);
    	       result.put("processAmount", new BigDecimal(authAmountStr));
    	       result.put("authRefNum", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
    	       //result.put("captureRefNum", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
    	       result.put("authFlag", UtilXml.elementAttribute(outputElement, "v1:RespCode", null));
    	       result.put("authMessage", UtilXml.elementAttribute(outputElement, "v1:Msg", null));
           }
           else
           {
               result.put("authResult", Boolean.valueOf(false));
               result.put("processAmount", BigDecimal.ZERO);
               result.put("authMessage", responseCode);
           }
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
        /*
         <error>error</error>
         OR
         <Resp v1:Bal="5058.68" v1:AuthAmt="0.00" v1:Msg="" v1:Auth="1923"
          v1:Amt="100.00" v1:RespCode="Success" v1:Loyalty="False"/>
         */
        Element outputElement = null;
        if(UtilValidate.isNotEmpty(responseDocument))
        {
            outputElement = responseDocument.getDocumentElement();
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();

        if (UtilValidate.isEmpty(outputElement) || outputElement.getNodeName().equalsIgnoreCase("error"))
        {
            result.put("releaseResult", Boolean.valueOf(true));
            result.put("releaseAmount", BigDecimal.ZERO);
	        result.put("releaseMessage", UtilValidate.isNotEmpty(outputElement)?UtilXml.elementValue(outputElement):"did not find any response");
        }
        else
        {
            String responseCode = UtilXml.elementAttribute(outputElement, "v1:RespCode", "Error");
            if (responseCode.equalsIgnoreCase(getSuccessStatusCode()))
            {
   	             result.put("releaseResult", Boolean.valueOf(true));
   	             result.put("releaseCode", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
   	             String releaseAmountStr = UtilXml.elementAttribute(outputElement, "v1:Amt", null);
   	             result.put("releaseAmount", new BigDecimal(releaseAmountStr));
   	             result.put("releaseRefNum", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
   	             result.put("releaseFlag", UtilXml.elementAttribute(outputElement, "v1:RespCode", null));
   	             result.put("releaseMessage", UtilXml.elementAttribute(outputElement, "v1:Msg", null));
            }
            else
            {
                result.put("releaseResult", Boolean.valueOf(true)); 
                result.put("releaseAmount", BigDecimal.ZERO);
    	        result.put("releaseMessage", responseCode);
            }
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


    private static Map<String, Object> processCaptureResponse(BigDecimal captureAmount, Document responseDocument)
    {
        /*
         <error>error</error>
         OR
         <Resp v1:Bal="10000.00" v1:AuthAmt="0.00" v1:Msg="" 
         v1:Auth="1951" v1:Amt="0.00" v1:RespCode="Success" v1:Loyalty="False"/>
         */
        Element outputElement = null;
        if(UtilValidate.isNotEmpty(responseDocument))
        {
            outputElement = responseDocument.getDocumentElement();
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();

        if (UtilValidate.isEmpty(outputElement) || outputElement.getNodeName().equalsIgnoreCase("error"))
        {
          result.put("captureResult", Boolean.valueOf(false));
          result.put("processAmount", BigDecimal.ZERO);
	      result.put("captureMessage", UtilValidate.isNotEmpty(outputElement)?UtilXml.elementValue(outputElement):"did not find any response");
        }
        else
        {
            String responseCode = UtilXml.elementAttribute(outputElement, "v1:RespCode", "Error");
            if (responseCode.equalsIgnoreCase(getSuccessStatusCode()))
            {
		         result.put("captureResult", Boolean.valueOf(true));
		         result.put("captureCode", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
		         result.put("processAmount", captureAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
	    	     result.put("authRefNum", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
		         result.put("captureRefNum", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
		         result.put("captureFlag", UtilXml.elementAttribute(outputElement, "v1:RespCode", null));
		         result.put("captureMessage", UtilXml.elementAttribute(outputElement, "v1:Msg", null));
            }
            else
            {
                result.put("captureResult", Boolean.valueOf(false));
                result.put("processAmount", BigDecimal.ZERO);
      	        result.put("captureMessage", responseCode);
            }
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
        /*
         <error>error</error>
         OR
         <Resp v1:Bal="10100.00" v1:AuthAmt="100.00" v1:Msg=""
         v1:Auth="1953" v1:Amt="100.00" v1:RespCode="Success" v1:Loyalty="False"/>
         */
        Element outputElement = null;
        if(UtilValidate.isNotEmpty(responseDocument))
        {
            outputElement = responseDocument.getDocumentElement();
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();

        if (UtilValidate.isEmpty(outputElement) || outputElement.getNodeName().equalsIgnoreCase("error"))
        {
          result.put("refundResult", Boolean.valueOf(false));
          result.put("refundAmount", BigDecimal.ZERO);
	      result.put("refundMessage", UtilValidate.isNotEmpty(outputElement)?UtilXml.elementValue(outputElement):"did not find any response");
        }
        else
        {
            String responseCode = UtilXml.elementAttribute(outputElement, "v1:RespCode", "Error");
            if (responseCode.equalsIgnoreCase(getSuccessStatusCode()))
            {
	   	         result.put("refundResult", Boolean.valueOf(true));
	   	         result.put("refundCode", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
	   	         String authAmountStr = UtilXml.elementAttribute(outputElement, "v1:Amt", null);
	   	         result.put("refundAmount", new BigDecimal(authAmountStr));
	   	         result.put("refundRefNum", UtilXml.elementAttribute(outputElement, "v1:Auth", null));
	   	         result.put("refundFlag", UtilXml.elementAttribute(outputElement, "v1:RespCode", null));
	   	         result.put("refundMessage", UtilXml.elementAttribute(outputElement, "v1:Msg", null));
            }
            else
            {
                result.put("refundResult", Boolean.valueOf(false));
                result.put("refundAmount", BigDecimal.ZERO);
      	        result.put("refundMessage", responseCode);
            }
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

    private static Map<String, String> buildTenderCardProperties(DispatchContext dctx, Map<String, ? extends Object> context, String paymentServiceTypeEnumId)
    {
        Delegator delegator = dctx.getDelegator();
        String productStoreId = (String) context.get("productStoreId");
        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");
        GenericValue tenderCardGatewayConfig = null;
        Map<String, String> tenderCardConfig = new HashMap<String, String>();

        if (paymentGatewayConfigId == null)
        {
            if (productStoreId == null)
            {
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
        	}
            if (productStoreId != null)
            {
                GenericValue tenderCardPaymentSetting = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, "GIFT_CARD", paymentServiceTypeEnumId, true);
                if (tenderCardPaymentSetting != null)
                {
                    paymentGatewayConfigId = tenderCardPaymentSetting.getString("paymentGatewayConfigId");
                }
            }
        }
        if (paymentGatewayConfigId != null)
        {
            try
            {
                tenderCardGatewayConfig = delegator.findOne("PaymentGatewayTenderCard", true, "paymentGatewayConfigId", paymentGatewayConfigId);
            }
            catch (GenericEntityException e)
            {
                Debug.logError(e, module);
            }
        }

        if (UtilValidate.isNotEmpty(tenderCardGatewayConfig))
        {
            Map<String, Object> tmp = tenderCardGatewayConfig.getAllFields();
            Set<String> keys = tmp.keySet();
            for (String key : keys)
            {
                String value = tmp.get(key).toString();
                tenderCardConfig.put(key, value);
            }
        }

        Debug.logInfo("TenderCard Configuration : " + tenderCardConfig.toString(), module);
        return tenderCardConfig;
    }

    public static String getSuccessStatusCode()
    {
        return "Success";
    }

    public static String getStatusCodeString(String statusCode)
    {
        return getStatusCode().get(statusCode);
    }

    public static Map<String, String> getStatusCode()
    {
    	Map<String, String> fields = FastMap.newInstance();
	    fields.put("Success", "Transaction went through ok");
	    fields.put("Error", "Transaction went through Error");
	    fields.put("CallTC", "Error occurred, typically the reason would be the built in dupe transaction checking which only allows the same exact transaction on the card, amount and type every 1 minute.");
	    fields.put("TerminalNotFound", "Invalid TID");
	    fields.put("MerchantNotFound", "TCID is invalid");
	    fields.put("DBError", "Error with the server");
	    fields.put("InsufficentFunds", "Not enough funds on the account to perform the redemption");
	    fields.put("AccountNotFound", "Account number not found or CVV was sent with Account and the CVV did not match");
        return fields;
    }

    public static Document sendRequest(String serverURL, Map<String, Object> parameters) throws GeneralException 
    {
        String response = null;
        try
        {
            response = sendHttpRequest(serverURL, parameters); //might be an exception warn log for host certification
        }
        catch (HttpClientException hce)
        {
			Debug.logError(hce, hce.toString(), module);
            throw new GeneralException("TenderCard api connection problem", hce);
        }

        Document responseDocument = null;
        try 
        {
            Debug.logInfo("Tender card API response" + response, module);
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

    private static String sendHttpRequest(String serverURL, Map<String, Object> parameters) throws HttpClientException
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
            	//remove non-ascii characters from response
            	resultString = UtilIO.readString(in).replaceAll("[^\\x20-\\x7e]", "");
            }
        }
        catch (Exception e)
        {
            throw new HttpClientException("Error processing request", e);
        }
        return resultString;
    }

}
