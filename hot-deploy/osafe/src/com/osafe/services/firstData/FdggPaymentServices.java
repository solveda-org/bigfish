/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.osafe.services.firstData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.axis2.transport.http.HttpTransportProperties;
import org.ofbiz.accounting.payment.PaymentGatewayServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.HttpClient;
import org.ofbiz.base.util.HttpClientException;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.osafe.thirdparty.firstdata.globalgatewaye4.*;


/**
 * ClearCommerce Payment Services (CCE 5.4)
 */
public class FdggPaymentServices {

    public final static String module = FdggPaymentServices.class.getName();

    public static Map<String, Object> ccAuth(DispatchContext dctx, Map<String, Object> context) {

        Delegator delegator = dctx.getDelegator();
        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        if (UtilValidate.isEmpty(gatewayConfigProps))
        {
            return ServiceUtil.returnError("Payment Gateway Configuration Not Found");
        	
        }
        GlobalGatewayE4 e4 = getGlobalGateway(gatewayConfigProps);
        if (UtilValidate.isEmpty(e4))
        {
            return ServiceUtil.returnError("First Data Global Gateway is Not Configured");
        	
        }
        
        
        Response response=null;
 		try 
 		 {

 	        Request request = e4.getRequest();
 	        request.transaction_type(TransactionType.PreAuthorization);
    		BigDecimal amount = (BigDecimal) context.get("processAmount");
 	        buildRequest(context, request,amount);
 			response = request.submit();
 		 } 
 		  catch (Exception e) {
 	            return ServiceUtil.returnError(e.getMessage());
 			}
	    return processAuthResponse(response);
 			 
    }

    public static Map<String, Object> ccCapture(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        if (UtilValidate.isEmpty(orderPaymentPreference)) 
        {
            return ServiceUtil.returnError("Order Payment Preference not found");
        }
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (UtilValidate.isEmpty(authTransaction)) 
        {
            return ServiceUtil.returnError("Authorize Transaction not found");
        }

        
        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        if (UtilValidate.isEmpty(gatewayConfigProps))
        {
            return ServiceUtil.returnError("Payment Gateway Configuration Not Found");
        	
        }
        GlobalGatewayE4 e4 = getGlobalGateway(gatewayConfigProps);
        if (UtilValidate.isEmpty(e4))
        {
            return ServiceUtil.returnError("First Data Global Gateway is Not Configured");
        	
        }
        
        GenericValue creditCard = null;
        try 
        {
        	//Put the credit card and address associated with the card into context for the build Transaction methods
            creditCard = delegator.getRelatedOne("CreditCard",orderPaymentPreference);
            context.put("creditCard", creditCard);
            if (UtilValidate.isNotEmpty(creditCard.getString("contactMechId"))) 
            {
                GenericValue address = creditCard.getRelatedOne("PostalAddress");
                if (UtilValidate.isNotEmpty(address)) 
                {
                    context.put("billingAddress", address);
                	
                }
            }
            String orderId = orderPaymentPreference.getString("orderId");
            if (UtilValidate.isNotEmpty(orderId))
            {
            	context.put("orderId", orderId);
                OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
                GenericValue billToParty = orh.getBillToParty();
                if (UtilValidate.isNotEmpty(billToParty))
                {
                    context.put("billToParty", billToParty);
                    Collection<GenericValue> emails = ContactHelper.getContactMech(billToParty.getRelatedOne("Party"), "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
                    if (UtilValidate.isNotEmpty(emails)) 
                    {
                    	context.put("billToEmail", emails.iterator().next());
                    }
                }
            }
            
            
        } 
         catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Order Credit Card Not Found");
        }

        Response response=null;
 			try 
 			{
 	 	        Request request = e4.getRequest();
 	 	        request.transaction_type(TransactionType.PreAuthorizationCompletion);
 	            BigDecimal amount = (BigDecimal) context.get("captureAmount");
 	 	        buildRequest(context, request,amount);
 				response = request.submit();
 			} 
 			 catch (Exception e) {
 	            return ServiceUtil.returnError(e.getMessage());
 			}
	    return processCaptureResponse(response);
    }

    public static Map<String, Object> ccAuthCapture(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        if (UtilValidate.isEmpty(orderPaymentPreference)) 
        {
            return ServiceUtil.returnError("Order Payment Preference not found");
        }
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (UtilValidate.isEmpty(authTransaction)) 
        {
            return ServiceUtil.returnError("Authorize Transaction not found");
        }

        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        if (UtilValidate.isEmpty(gatewayConfigProps))
        {
            return ServiceUtil.returnError("Payment Gateway Configuration Not Found");
        	
        }
        GlobalGatewayE4 e4 = getGlobalGateway(gatewayConfigProps);
        if (UtilValidate.isEmpty(e4))
        {
            return ServiceUtil.returnError("First Data Global Gateway is Not Configured");
        	
        }
        
        GenericValue creditCard = null;
        try 
        {
        	//Put the credit card and address associated with the card into context for the build Transaction methods
            creditCard = delegator.getRelatedOne("CreditCard",orderPaymentPreference);
            context.put("creditCard", creditCard);
            if (UtilValidate.isNotEmpty(creditCard.getString("contactMechId"))) 
            {
                GenericValue address = creditCard.getRelatedOne("PostalAddress");
                if (UtilValidate.isNotEmpty(address)) 
                {
                    context.put("billingAddress", address);
                	
                }
            }
            String orderId = orderPaymentPreference.getString("orderId");
            if (UtilValidate.isNotEmpty(orderId))
            {
            	context.put("orderId", orderId);
                OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
                GenericValue billToParty = orh.getBillToParty();
                if (UtilValidate.isNotEmpty(billToParty))
                {
                    context.put("billToParty", billToParty);
                    Collection<GenericValue> emails = ContactHelper.getContactMech(billToParty.getRelatedOne("Party"), "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
                    if (UtilValidate.isNotEmpty(emails)) 
                    {
                    	context.put("billToEmail", emails.iterator().next());
                    }
                }
            }
            
        } 
         catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Order Credit Card Not Found");
        }

        
        Response response=null;
 			try 
 			{
 	 	        Request request = e4.getRequest();
 	 	        request.transaction_type(TransactionType.Purchase);
 	    		BigDecimal amount = (BigDecimal) context.get("processAmount");
 	 	        buildRequest(context, request,amount);
 				response = request.submit();
 			} 
 			 catch (Exception e) {
 	            return ServiceUtil.returnError(e.getMessage());
 			}
	    return processAuthCaptureResponse(response);
    }

    public static Map<String, Object> ccReAuth(DispatchContext dctx, Map<String, Object> context) {
        return ccAuth(dctx, context);

    }


    public static Map<String, Object> ccRelease(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        if (UtilValidate.isEmpty(orderPaymentPreference)) 
        {
            return ServiceUtil.returnError("Order Payment Preference not found");
        }
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (UtilValidate.isEmpty(authTransaction)) 
        {
            return ServiceUtil.returnError("Authorize Transaction not found");
        }

        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        if (UtilValidate.isEmpty(gatewayConfigProps))
        {
            return ServiceUtil.returnError("Payment Gateway Configuration Not Found");
        	
        }
        GlobalGatewayE4 e4 = getGlobalGateway(gatewayConfigProps);
        if (UtilValidate.isEmpty(e4))
        {
            return ServiceUtil.returnError("First Data Global Gateway is Not Configured");
        	
        }
        
        GenericValue creditCard = null;
        try 
        {
        	//Put the credit card and address associated with the card into context for the build Transaction methods
            creditCard = delegator.getRelatedOne("CreditCard",orderPaymentPreference);
            context.put("creditCard", creditCard);
            if (UtilValidate.isNotEmpty(creditCard.getString("contactMechId"))) 
            {
                GenericValue address = creditCard.getRelatedOne("PostalAddress");
                if (UtilValidate.isNotEmpty(address)) 
                {
                    context.put("billingAddress", address);
                	
                }
            }
            String orderId = orderPaymentPreference.getString("orderId");
            if (UtilValidate.isNotEmpty(orderId))
            {
            	context.put("orderId", orderId);
                OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
                GenericValue billToParty = orh.getBillToParty();
                if (UtilValidate.isNotEmpty(billToParty))
                {
                    context.put("billToParty", billToParty);
                    Collection<GenericValue> emails = ContactHelper.getContactMech(billToParty.getRelatedOne("Party"), "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
                    if (UtilValidate.isNotEmpty(emails)) 
                    {
                    	context.put("billToEmail", emails.iterator().next());
                    }
                }
            }
            
        } 
         catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Order Credit Card Not Found");
        }

        
        Response response=null;
 		try 
 		{
	 	        Request request = e4.getRequest();
	 	        request.transaction_type(TransactionType.Void);
	            BigDecimal amount = (BigDecimal) context.get("releaseAmount");
 	 	        buildRequest(context, request,amount);
 				response = request.submit();
 		} 
 		 catch (Exception e) {
 	            return ServiceUtil.returnError(e.getMessage());
 		 }
 			 
	    return processReleaseResponse(response);
    }

    public static Map<String, Object> ccRefund(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        if (UtilValidate.isEmpty(orderPaymentPreference)) 
        {
            return ServiceUtil.returnError("Order Payment Preference not found");
        }
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (UtilValidate.isEmpty(authTransaction)) 
        {
            return ServiceUtil.returnError("Authorize Transaction not found");
        }
        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        if (UtilValidate.isEmpty(gatewayConfigProps))
        {
            return ServiceUtil.returnError("Payment Gateway Configuration Not Found");
        	
        }
        GlobalGatewayE4 e4 = getGlobalGateway(gatewayConfigProps);
        if (UtilValidate.isEmpty(e4))
        {
            return ServiceUtil.returnError("First Data Global Gateway is Not Configured");
        	
        }
        
        GenericValue creditCard = null;
        try 
        {
        	//Put the credit card and address associated with the card into context for the build Transaction methods
            creditCard = delegator.getRelatedOne("CreditCard",orderPaymentPreference);
            context.put("creditCard", creditCard);
            if (UtilValidate.isNotEmpty(creditCard.getString("contactMechId"))) 
            {
                GenericValue address = creditCard.getRelatedOne("PostalAddress");
                if (UtilValidate.isNotEmpty(address)) 
                {
                    context.put("billingAddress", address);
                	
                }
            }
            String orderId = orderPaymentPreference.getString("orderId");
            if (UtilValidate.isNotEmpty(orderId))
            {
            	context.put("orderId", orderId);
                OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
                GenericValue billToParty = orh.getBillToParty();
                if (UtilValidate.isNotEmpty(billToParty))
                {
                    context.put("billToParty", billToParty);
                    Collection<GenericValue> emails = ContactHelper.getContactMech(billToParty.getRelatedOne("Party"), "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
                    if (UtilValidate.isNotEmpty(emails)) 
                    {
                    	context.put("billToEmail", emails.iterator().next());
                    }
                }
            }
            
        } 
         catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Order Credit Card Not Found");
        }

        
        Response response=null;
 		try 
 		{
	 	        Request request = e4.getRequest();
	 	        request.transaction_type(TransactionType.Refund);
	            BigDecimal amount = (BigDecimal) context.get("refundAmount");
	 	        buildRequest(context, request,amount);
 				response = request.submit();
 		} 
 		 catch (Exception e) {
 	            return ServiceUtil.returnError(e.getMessage());
 		 }
	    return processRefundResponse(response);
    }

    private static void buildRequest(Map<String, Object> context, Request request,BigDecimal amount) 
    {
    	try {

            GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
            GenericValue authTransaction = null;
            if (UtilValidate.isNotEmpty(orderPaymentPreference))
        	{
                  authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        	}
            GenericValue creditCard = (GenericValue) context.get("creditCard");
            String  cardSecurityCode = (String) context.get("cardSecurityCode");
            String  orderId = (String) context.get("orderId");
            GenericValue billAddress = (GenericValue) context.get("billingAddress");
            GenericValue billToParty = (GenericValue) context.get("billToParty");
            GenericValue billToEmail = (GenericValue) context.get("billToEmail");


        	if (UtilValidate.isNotEmpty(authTransaction))
        	{
                request.authorization_num(authTransaction.getString("referenceNum"));
        	}
            
            if (UtilValidate.isNotEmpty(orderId))
        	{
        	    request.reference_no(orderId);
        	}

        	if (UtilValidate.isNotEmpty(creditCard))
        	{
                request.amount(amount);
                request.cc_number(creditCard.getString("cardNumber"));
                String expDate = creditCard.getString("expireDate");
                if (UtilValidate.isNotEmpty(expDate))
                {
                	//Credit Card expireDate is formatted in the DB as mm/yyyy
                	//Gateway expects expiry date in format mmyy
                	String expMonth = expDate.substring(0, 2);
                	String expYear = expDate.substring(3);
                	String cc_expiry = expMonth + expYear.substring(2);
                    request.cc_expiry(cc_expiry);
                }
        	}
            if (UtilValidate.isNotEmpty(cardSecurityCode))
            {
            	request.cc_verification_str2(cardSecurityCode);
            	request.cvd_presence_ind("1");

            }
            if (UtilValidate.isNotEmpty(billAddress))
            {
                request.cardholder_name(billAddress.getString("toName"));

                request.cc_verification_str1(formatAddress(billAddress));
            }
            if (UtilValidate.isNotEmpty(billToEmail))
            {
            	request.client_email(billToEmail.getString("infoString"));
            	
            }
            if (UtilValidate.isNotEmpty(billToParty))
            {
                request.customer_ref(billToParty.getString("partyId"));
            	
            }
    		
    	}
    	 catch (Exception e) {
    		 Debug.logError("buildRequest: " + e.getMessage(),module);
    	 }
    	
    	return;
    }
    
    private static Map<String, Object> processAuthResponse(Response response) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
	    if (response.transaction_approved()) 
 	        {
 	            result.put("authResult", Boolean.valueOf(true));
 	            result.put("authCode",response.bank_resp_code());
 	            result.put("processAmount", response.amount());
 	            result.put("authMessage", response.bank_message());
 	 	        result.put("authRefNum", response.authorization_num());
 	        } 
 	        else 
 	        {
 	            result.put("authResult", Boolean.valueOf(false));
 	            result.put("processAmount", BigDecimal.ZERO);
 	            result.put("authMessage", response.bank_message());
 	 	        result.put("authRefNum", "Error");
 	        }

 	        result.put("authFlag", response.bank_resp_code());
 	        result.put("avsCode", response.avs());
        return result;
    }

    private static Map<String, Object> processCaptureResponse(Response response) {

    	
        Map<String, Object> result = ServiceUtil.returnSuccess();
	    if (response.transaction_approved()) 
 	        {
 	            result.put("captureResult", Boolean.valueOf(true));
 	            result.put("captureCode",response.bank_resp_code());
 	            result.put("captureAmount", response.amount());
 	            result.put("captureMessage", response.bank_message());
 	 	        result.put("captureRefNum", response.authorization_num());
 	        } 
 	        else 
 	        {
 	            result.put("captureResult", Boolean.valueOf(false));
 	            result.put("captureAmount", BigDecimal.ZERO);
 	            result.put("captureMessage", response.bank_message());
 	 	        result.put("captureRefNum", "Error");
 	        }

 	        result.put("captureFlag", response.bank_resp_code());
        return result;
    	
    }
    
    private static Map<String, Object> processAuthCaptureResponse(Response response) {

    	
        Map<String, Object> result = ServiceUtil.returnSuccess();
	    if (response.transaction_approved()) 
 	        {
                result.put("authResult", Boolean.valueOf(true));
	            result.put("authCode",response.bank_resp_code());
	            result.put("processAmount", response.amount());
	            result.put("authMessage", response.bank_message());
	 	        result.put("authRefNum", response.authorization_num());
 	
	 	        result.put("captureResult", Boolean.valueOf(true));
 	            result.put("captureCode",response.bank_resp_code());
 	            result.put("captureAmount", response.amount());
 	            result.put("captureMessage", response.bank_message());
 	 	        result.put("captureRefNum", response.authorization_num());
 	        } 
 	        else 
 	        {
 	            result.put("authResult", Boolean.valueOf(false));
 	            result.put("processAmount", BigDecimal.ZERO);
 	            result.put("authMessage", response.bank_message());
 	 	        result.put("authRefNum", "Error");

 	 	        result.put("captureResult", Boolean.valueOf(false));
 	            result.put("captureAmount", BigDecimal.ZERO);
 	            result.put("captureMessage", response.bank_message());
 	 	        result.put("captureRefNum", "Error");
 	        }

	        result.put("authFlag", response.bank_resp_code());
 	        result.put("avsCode", response.avs());
 	        result.put("captureFlag", response.bank_resp_code());
        return result;
    	
    }

    //No Service for this
    private static Map<String, Object> processCreditResponse(Response response) {
        Map<String, Object> result = ServiceUtil.returnSuccess();

	    if (response.transaction_approved())
	    {
            result.put("creditResult", Boolean.valueOf(true));
            result.put("creditCode", response.bank_resp_code());
            result.put("creditAmount", response.amount());
        } 
	    else 
	    {
            result.put("creditResult", Boolean.valueOf(false));
            result.put("creditAmount", BigDecimal.ZERO);
        }

        result.put("creditRefNum", response.authorization_num());
        result.put("creditFlag", response.bank_resp_code());
        result.put("creditMessage",response.bank_message());

        return result;
    }

    private static Map<String, Object> processReleaseResponse(Response response) {

        Map<String, Object> result = ServiceUtil.returnSuccess();

	    if (response.transaction_approved())
	    {
            result.put("releaseResult", Boolean.valueOf(true));
            result.put("releaseCode", response.bank_resp_code());
            result.put("releaseAmount", response.amount());
        } 
	    else 
	    {
            result.put("releaseResult", Boolean.valueOf(false));
            result.put("releaseAmount", BigDecimal.ZERO);
        }

        result.put("releaseRefNum", response.authorization_num());
        result.put("releaseFlag", response.bank_resp_code());
        result.put("releaseMessage", response.bank_message());

        return result;
    }

    private static Map<String, Object> processRefundResponse(Response response) {

    	
        Map<String, Object> result = ServiceUtil.returnSuccess();
	    if (response.transaction_approved()) 
 	        {
 	            result.put("refundResult", Boolean.valueOf(true));
 	            result.put("refundCode",response.bank_resp_code());
 	            result.put("refundAmount", response.amount());
 	            result.put("refundMessage", response.bank_message());
 	 	        result.put("refundRefNum", response.authorization_num());
 	        } 
 	        else 
 	        {
 	            result.put("refundResult", Boolean.valueOf(false));
 	            result.put("refundAmount", BigDecimal.ZERO);
 	            result.put("refundMessage", response.bank_message());
 	 	        result.put("refundRefNum", "Error");
 	        }

 	        result.put("refundFlag", response.bank_resp_code());
        return result;
    	
    }

    private static Map<String, Object> processReAuthResponse(Response response) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
	    if (response.transaction_approved()) 
        {
            result.put("reauthResult", Boolean.valueOf(true));
            result.put("reauthCode", response.bank_resp_code());
            result.put("reauthAmount", response.amount());
        } 
	    else 
	    {
            result.put("reauthResult", Boolean.valueOf(false));
            result.put("reauthAmount", BigDecimal.ZERO);
        }

        result.put("reauthRefNum", response.authorization_num());
        result.put("reauthFlag", response.bank_resp_code());
        result.put("reauthMessage", response.bank_message());
        return result;
    }


    private static Map<String, String> buildConfigProperties(Map<String, Object> context, Delegator delegator) {

        Map<String, String> paymentGatewayConfigProps = new HashMap<String, String>();

        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");

        if (UtilValidate.isNotEmpty(paymentGatewayConfigId)) 
        {
            try 
            {
                GenericValue paymentGatewayConfig = delegator.findOne("PaymentGatewayFdgg", UtilMisc.toMap("paymentGatewayConfigId", paymentGatewayConfigId), true);
                if (UtilValidate.isNotEmpty(paymentGatewayConfig)) 
                {
                    Map<String, Object> tmp = paymentGatewayConfig.getAllFields();
                    Set<String> keys = tmp.keySet();
                    for (String key : keys) 
                    {
                        Object keyValue = tmp.get(key);
                        String value="";
                        if (UtilValidate.isNotEmpty(keyValue))
                        {
                        	value = keyValue.toString();
                        }
                        paymentGatewayConfigProps.put(key, value);
                    }
                }
            } 
             catch (GenericEntityException e) 
             {
                Debug.logError(e, module);
             }
        }

        Debug.logInfo("PaymentGateway Configuration Fields : " + paymentGatewayConfigProps.toString(), module);
        return paymentGatewayConfigProps;
    }
    
    private static GlobalGatewayE4 getGlobalGateway(Map<String, String> gatewayConfigProps) {

    	GlobalGatewayE4 e4 =null;
        if (UtilValidate.isNotEmpty(gatewayConfigProps)) 
        {
            try 
            {
            	String apiVersion= gatewayConfigProps.get("apiVersion");
            	String apiVersionUrl= gatewayConfigProps.get("apiVersionUrl");
            	String gatewayId= gatewayConfigProps.get("gatewayId");
            	String apiPassword= gatewayConfigProps.get("apiPassword");
            	String apiKeyId= gatewayConfigProps.get("apiKeyId");
            	String apiHmacKey= gatewayConfigProps.get("apiHmacKey");
                
            	
                if (UtilValidate.isNotEmpty(apiVersionUrl)) 
                {
                   // e4 = new GlobalGatewayE4(environment, "AG9179-05", "n18i80z3b8hl46jzyoj037kkl120e0pd", "191765", "HXL3xz9k8924wnSxp_y9RCMYaw8Vs~JL");
                    Environment environment = new Environment(apiVersionUrl,apiVersion); 
                    e4 = new GlobalGatewayE4(environment,gatewayId,apiPassword,apiKeyId,apiHmacKey);
                	
                }
            } 
             catch (Exception e) 
             {
                Debug.logError(e, module);
             }
        }

        return e4;
    }
    
    private static String formatAddress(GenericValue address) {
    	StringBuffer sb = new StringBuffer();
        if (UtilValidate.isNotEmpty(address))
        {
        	// Street Address|Zip/Postal|City|State/Prov|Country
            sb.append(address.getString("address1"));
            sb.append("|");
            sb.append(address.getString("postalCode"));
            sb.append("|");
            sb.append(address.getString("city"));
            sb.append("|");
            sb.append(address.getString("stateProvinceGeoId"));
            sb.append("|");
            sb.append(address.getString("countryGeoId"));
        }
    	return sb.toString();
    	
    }  
}




