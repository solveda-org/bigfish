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

package com.osafe.services.sagepay;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.accounting.payment.PaymentGatewayServices;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;

public class SagePayTokenPaymentServices {

    public static final String module = SagePayTokenPaymentServices.class.getName();

    private static Map<String, String> buildCustomerBillingInfo(Map<String, Object> context) {

        Debug.logInfo("SagePay Token - Entered buildCustomerBillingInfo", module);
//        Debug.logInfo("SagePay Token - buildCustomerBillingInfo context : " + context, module);

        Map<String, String> billingInfo = FastMap.newInstance();

        String orderId = null;
        BigDecimal processAmount = null;
        String currency = null;
        String cardNumber = null;
        String cardType = null;
        String nameOnCard = null;
        String expireDate = null;
        String securityCode = null;
        String postalCode = null;
        String address = null;
        String address2=null;
        String firstName=null;
        String lastName=null;
        String middleName=null;

        
        try {

            GenericValue opp = (GenericValue) context.get("orderPaymentPreference");
            if (opp != null) 
            {
                if ("CREDIT_CARD".equals(opp.getString("paymentMethodTypeId"))) 
                {

                    GenericValue creditCard = (GenericValue) context.get("creditCard");
                    if (creditCard == null || !(opp.get("paymentMethodId").equals(creditCard.get("paymentMethodId")))) {
                        creditCard = opp.getRelatedOne("CreditCard");
                    }

                    securityCode = opp.getString("securityCode");

                    //getting card details
                    cardNumber = creditCard.getString("cardNumber");
                    firstName = creditCard.getString("firstNameOnCard");
                    middleName = creditCard.getString("middleNameOnCard");
                    lastName = creditCard.getString("lastNameOnCard");
                    if (UtilValidate.isNotEmpty(middleName))
                    {
                    	firstName =firstName + " " + middleName;
                    }
                    nameOnCard = firstName + " " + lastName;
                    cardType = creditCard.getString("cardType");
                    if (cardType != null) 
                    {
                        if (cardType.equals("MasterCard")) {
                            cardType = "MC";
                        }
                        if (cardType.equals("VisaElectron")) {
                            cardType = "UKE";
                        }
                        if (cardType.equals("DinersClub")) {
                            cardType = "DC";
                        }
                        if (cardType.equals("Switch")) {
                            cardType = "MAESTRO";
                        }
                    }
                    expireDate = creditCard.getString("expireDate");
                    String month = expireDate.substring(0,2);
                    String year = expireDate.substring(5);
                    expireDate = month + year;

                    //getting order details
                    orderId = UtilFormatOut.checkNull((String) context.get("orderId"));
                    if (UtilValidate.isEmpty(orderId))
                    {
                    	orderId=opp.getString("orderId");
                    }
                    processAmount =  (BigDecimal) context.get("processAmount");
                    currency = (String) context.get("currency");
                    
                    billingInfo.put("paymentMethodId", (String)opp.get("paymentMethodId"));
                    billingInfo.put("orderId", orderId);
                    billingInfo.put("amount", processAmount.toString());
                    billingInfo.put("currency", currency);
                    billingInfo.put("description", orderId);
                    billingInfo.put("cardNumber", cardNumber);
                    billingInfo.put("cardHolder",  nameOnCard);
                    billingInfo.put("expiryDate", expireDate);
                    billingInfo.put("cardType", cardType);
                    billingInfo.put("cv2", securityCode);

                    //getting billing address
                    GenericValue billingAddress = (GenericValue) context.get("billingAddress");
                    if (UtilValidate.isNotEmpty(billingAddress))
                    {
                    	
                        billingInfo.put("contactMechId", billingAddress.getString("contactMechId"));
                        billingInfo.put("billingFirstnames", firstName);
                        billingInfo.put("billingSurname", lastName);
                        billingInfo.put("billingAddress", billingAddress.getString("address1"));
                        billingInfo.put("billingAddress2", billingAddress.getString("address2"));
                        billingInfo.put("billingCity", billingAddress.getString("city"));
                        billingInfo.put("billingState", billingAddress.getString("stateProvinceGeoId"));
                        billingInfo.put("billingPostCode", billingAddress.getString("postalCode"));
                        GenericValue GeoCountry = (GenericValue) billingAddress.getRelatedOneCache("CountryGeo");
                        if (UtilValidate.isNotEmpty(GeoCountry))
                        {
                            billingInfo.put("billingCountry", GeoCountry.getString("geoCode"));
                        	
                        }
                        billingInfo.put("billingPhone", null);
                    }
                    
                    //getting shipping address
                    GenericValue deliveryAddress = (GenericValue) context.get("shippingAddress");
                    if (UtilValidate.isNotEmpty(deliveryAddress)) 
                    {
                        Debug.logInfo("SagePay TokenshippingInfo : ", module);
                        billingInfo.put("deliveryFirstnames", firstName);
                        billingInfo.put("deliverySurname", lastName);
                        billingInfo.put("deliveryAddress", deliveryAddress.getString("address1"));
                        billingInfo.put("deliveryAddress2", deliveryAddress.getString("address2"));
                        billingInfo.put("deliveryCity", deliveryAddress.getString("city"));
                        billingInfo.put("deliveryState", deliveryAddress.getString("stateProvinceGeoId"));
                        billingInfo.put("deliveryPostCode", deliveryAddress.getString("postalCode"));
                        GenericValue GeoDeliverCountry = (GenericValue) deliveryAddress.getRelatedOneCache("CountryGeo");
                        billingInfo.put("deliveryCountry", GeoDeliverCountry.getString("geoCode"));
                        billingInfo.put("deliveryPhone", null);
                    	
                    }
                    
                    

                } 
                else 
                {
                    Debug.logWarning("Payment preference " + opp + " is not a credit card", module);
                }
            }
        } catch (GenericEntityException ex) {
            Debug.logError("Cannot build customer information for " + context + " due to error: " + ex.getMessage(), module);
            return null;
        }

        
//        Debug.logInfo("SagePay Token billingInfo : " + billingInfo, module);
        Debug.logInfo("SagePay Token - Exiting buildCustomerBillingInfo", module);

        return billingInfo;
    }    
    
    private static Map<String, String> buildOrderBasket(Map<String, Object> context) {

        Debug.logInfo("SagePay Token - Entered buildOrderBasket", module);
//        Debug.logInfo("SagePay Token - buildOrderBasket context : " + context, module);

        Map<String, String> basketInfo = FastMap.newInstance();

        
        try {

            List orderItems = (List) context.get("orderItems");
            if (orderItems != null) 
            {
                if (orderItems.size() < 1) {
                	return null;
                }
                BigDecimal totalOrderAmount =  (BigDecimal) context.get("processAmount");
                BigDecimal orderItemTotal = BigDecimal.ZERO;
                BigDecimal orderSubTotal = BigDecimal.ZERO;
                BigDecimal qtyItemTotal = BigDecimal.ZERO;
                StringBuffer sb = new StringBuffer();
                int itemIdx = 0;
/*                Number of lines of detail in the basket field (Items + Delivery): */
                sb.append((orderItems.size() + 2)+ ":");
                Iterator itemIter = orderItems.iterator();
                while (itemIter.hasNext()) {
                	itemIdx++;
                	
                    GenericValue orderItem = (GenericValue) itemIter.next();
                    /*Item 1 Description: */ 
                    String itemDescription=orderItem.getString("itemDescription");
                    BigDecimal qty = orderItem.getBigDecimal("quantity");
                    BigDecimal unitPrice = orderItem.getBigDecimal("unitPrice");
                    BigDecimal itemTotal = unitPrice.multiply(qty).setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
                    orderItemTotal = orderItemTotal.add(itemTotal);
                    qtyItemTotal = qtyItemTotal.add(qty);
                    orderSubTotal = orderSubTotal.add(unitPrice);
                    sb.append(orderItem.getString("itemDescription")+ ":");
                	/*Quantity of item 1:*/ 
                    sb.append(orderItem.getBigDecimal("quantity").setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"))+ ":");
                	/*Unit cost item 1 without tax:*/ 
                    sb.append(orderItem.getBigDecimal("unitPrice").setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toPlainString()+ ":");
                	/*Tax applied to item 1:*/ 
                    sb.append(":");
                	/*Cost of Item 1 including tax:*/ 
                    sb.append(":");
                	/*Total cost of item 1 (Quantity x cost including tax):*/ 
                    sb.append(itemTotal.toPlainString() + ":");
                }
                BigDecimal orderAdjustment = totalOrderAmount.subtract(orderItemTotal); 
                sb.append("Delivery:---:---:---:---:" + orderAdjustment.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toPlainString() + ":");
                sb.append("Total:" + qtyItemTotal.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toPlainString() +":" + orderSubTotal.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toPlainString() + ":---:---:" + totalOrderAmount.setScale(2, UtilNumber.getBigDecimalRoundingMode("order.rounding")).toPlainString());
                basketInfo.put("basket",sb.toString());

/*                
                Number of lines of detail in the basket field: 
                	Item 1 Description: 
                	Quantity of item 1: 
                	Unit cost item 1 without tax: 
                	Tax applied to item 1: 
                	Cost of Item 1 including tax: 
                	Total cost of item 1 (Quantity x cost including tax): 
                	Item 2 Description: 
                	Quantity of item 2: 
*/
            }
        } catch (Exception ex) {
            Debug.logError("Cannot build order information for " + context + " due to error: " + ex.getMessage(), module);
            return null;
        }

        
//        Debug.logInfo("SagePay Token billingInfo : " + basketInfo, module);
        Debug.logInfo("SagePay Token - Exiting buildCustomerBillingInfo", module);

        return basketInfo;
    }

    public static Map<String, Object> ccToken(DispatchContext dctx, Map<String,Object> context) {
    	Map<String,Object> response = null;
    	return response;
    }
    
    public static Map<String, Object> ccAuth(DispatchContext dctx, Map<String, Object> context) {
        Debug.logInfo("SagePay Token - Entered ccAuth", module);
//        Debug.logInfo("SagePay Token ccAuth context : " + context, module);
        Map<String, Object> response = null;

        String orderId = (String) context.get("orderId");
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = dctx.getDispatcher();

        if (null == orderPaymentPreference) 
        {
            response = ServiceUtil.returnError("OrderPaymentPreference for order : " + orderId + " is null : " + orderPaymentPreference);
        } 
        else 
        {
            response = processCardRegistrationPayment(dctx, context);
            if (UtilValidate.isEmpty((String)response.get("token")))
            {
                response = ServiceUtil.returnError("Could not process Token for order: " + orderId + " is null : ");
            }
            else
            {
                try {
	            	context.put("token",response.get("token"));
	                response = processCardAuthorisationPayment(dctx, context);
	                	
                }
                 catch (Exception e)
                 {
                     Debug.logInfo("SagePay Token processCardAuthorisationPayment: " + e.getMessage(), module);
                	 
                 }
               
            }
            try {
                //Now Clear all credit card Data regardless if everything went well
                Debug.logInfo("SagePay Token clear Credit Card Data", module);
                 dispatcher.runSync("clearCreditCardData", UtilMisc.toMap("userLogin", userLogin,"paymentMethodId",orderPaymentPreference.getString("paymentMethodId")));
                	
            }
             catch (Exception e)
             {
                 Debug.logInfo("SagePay Token clear credit card: " + e.getMessage(), module);
            	 
             }
        }
        Debug.logInfo("SagePay Token ccAuth response : " + response, module);
        Debug.logInfo("SagePay Token - Exiting ccAuth", module);
        return response;
    }


    private static Map<String, Object> processCardRegistrationPayment(DispatchContext ctx, Map<String, Object> context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        Map<String, String> billingInfo = buildCustomerBillingInfo(context);
        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");

        try {

            Map<String, Object> paymentTokenResult = dispatcher.runSync("SagePayTokenPaymentRegistration",
                    UtilMisc.toMap(
                            "paymentGatewayConfigId", paymentGatewayConfigId,
                            "vendorTxCode", billingInfo.get("orderId"),
                            "cardHolder", billingInfo.get("cardHolder"),
                            "cardNumber", billingInfo.get("cardNumber"),
                            "expiryDate", billingInfo.get("expiryDate"),
                            "cardType", billingInfo.get("cardType"),
                            "cv2", billingInfo.get("cv2"),
                            "currency", billingInfo.get("currency")
                        )
                    );
      	
           result.put("token",(String)paymentTokenResult.get("token"));
           String status = (String) paymentTokenResult.get("status");
           if ("OK".equals(status))
           {
               Map createTokePaymentMethod = dispatcher.runSync("createSagePayTokenPaymentMethod", UtilMisc.toMap("userLogin", userLogin,"paymentMethodId",billingInfo.get("paymentMethodId"),"contactMechId",billingInfo.get("contactMechId"),"sagePayToken",paymentTokenResult.get("token")));

           }
        } 
         catch(GenericServiceException e) {
        Debug.logError(e, "Error in calling SagePayTokenPaymentAuthentication", module);
        result = ServiceUtil.returnError("Exception in calling SagePayTokenPaymentRegistration : " + e.getMessage());
        }
       return result;
    }

    private static Map<String, Object> processCardAuthorisationPayment(DispatchContext ctx, Map<String, Object> context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();

        Map<String, String> billingInfo = buildCustomerBillingInfo(context);
        Map<String, String> basketInfo =  buildOrderBasket(context);
        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");
        String token = (String) context.get("token");

        try {

            Map<String, Object> paymentResult = dispatcher.runSync("SagePayTokenPaymentAuthentication",
                    UtilMisc.toMap(
                            "paymentGatewayConfigId", paymentGatewayConfigId,
                            "vendorTxCode", billingInfo.get("orderId"),
                            "amount", billingInfo.get("amount"),
                            "currency", billingInfo.get("currency"),
                            "description", billingInfo.get("description"),
                            "token", token,
                            "cv2", billingInfo.get("cv2"),
                            "billingFirstnames", billingInfo.get("billingFirstnames"),
                            "billingSurname", billingInfo.get("billingSurname"),
                            "billingAddress", billingInfo.get("billingAddress"),
                            "billingAddress2", billingInfo.get("billingAddress2"),
                            "billingCity", billingInfo.get("billingCity"),
                            "billingState", billingInfo.get("billingState"),
                            "billingPostCode", billingInfo.get("billingPostCode"),
                            "billingCountry", billingInfo.get("billingCountry"),
                            "billingPhone", billingInfo.get("billingPhone"),
                            "deliveryFirstnames", billingInfo.get("deliveryFirstnames"),
                            "deliverySurname", billingInfo.get("deliverySurname"),
                            "deliveryAddress", billingInfo.get("deliveryAddress"),
                            "deliveryAddress2", billingInfo.get("deliveryAddress2"),
                            "deliveryCity", billingInfo.get("deliveryCity"),
                            "deliveryState", billingInfo.get("deliveryState"),
                            "deliveryPostCode", billingInfo.get("deliveryPostCode"),
                            "deliveryCountry", billingInfo.get("deliveryCountry"),
                            "deliveryPhone", billingInfo.get("deliveryPhone"),
                            "isBillingSameAsDelivery", billingInfo.get("isBillingSameAsDelivery"),
                            "basket", basketInfo.get("basket")
                        )
                    );

            Debug.logInfo("SagePay Token - SagePayTokenPaymentAuthentication result : " + paymentResult, module);

            String transactionType = (String) paymentResult.get("transactionType");
            String status = (String) paymentResult.get("status");
            String statusDetail = (String) paymentResult.get("statusDetail");
            String vpsTxId = (String) paymentResult.get("vpsTxId");
            String securityKey = (String) paymentResult.get("securityKey");
            String txAuthNo = (String) paymentResult.get("txAuthNo");
            String vendorTxCode = (String) paymentResult.get("vendorTxCode");
            String amount = (String) paymentResult.get("amount");

            if (status != null && "OK".equals(status) || "REGISTERED".equals(status)) {
                Debug.logInfo("SagePay Token - Payment authorized for order : " + vendorTxCode, module);
                result = SagePayUtil.buildCardAuthorisationPaymentResponse(Boolean.TRUE, txAuthNo, securityKey, new BigDecimal(amount), vpsTxId, vendorTxCode, statusDetail);
                if ("PAYMENT".equals(transactionType)) {
                    Map<String,Object> captureResult = SagePayUtil.buildCardCapturePaymentResponse(Boolean.TRUE, txAuthNo, securityKey,null, vpsTxId, vendorTxCode, statusDetail);
                    result.putAll(captureResult);
                }
                
            } else if (status != null && "INVALID".equals(status)) {
                Debug.logInfo("SagePay Token - Invalid authorisation request for order : " + vendorTxCode, module);
                result = SagePayUtil.buildCardAuthorisationPaymentResponse(Boolean.FALSE, null, null, BigDecimal.ZERO, "INVALID", vendorTxCode, statusDetail);
            } else if (status != null && "MALFORMED".equals(status)) {
                Debug.logInfo("SagePay Token - Malformed authorisation request for order : " + vendorTxCode, module);
                result = SagePayUtil.buildCardAuthorisationPaymentResponse(Boolean.FALSE, null, null, BigDecimal.ZERO, "MALFORMED", vendorTxCode, statusDetail);
            } else if (status != null && "NOTAUTHED".equals(status)) {
                Debug.logInfo("SagePay Token - NotAuthed authorisation request for order : " + vendorTxCode, module);
                result = SagePayUtil.buildCardAuthorisationPaymentResponse(Boolean.FALSE, null, securityKey, BigDecimal.ZERO, vpsTxId, vendorTxCode, statusDetail);
            } else if (status != null && "REJECTED".equals(status)) {
                Debug.logInfo("SagePay Token - Rejected authorisation request for order : " + vendorTxCode, module);
                result = SagePayUtil.buildCardAuthorisationPaymentResponse(Boolean.FALSE, null, securityKey, new BigDecimal(amount), vpsTxId, vendorTxCode, statusDetail);
            } else {
                Debug.logInfo("SagePay Token - Invalid status " + status + " received for order : " + vendorTxCode, module);
                result = SagePayUtil.buildCardAuthorisationPaymentResponse(Boolean.FALSE, null, null, BigDecimal.ZERO, "ERROR", vendorTxCode, statusDetail);
            }
        } catch(GenericServiceException e) {
            Debug.logError(e, "Error in calling SagePayTokenPaymentAuthentication", module);
            result = ServiceUtil.returnError("Exception in calling SagePayTokenPaymentRegistration : " + e.getMessage());
        }
        return result;
    }

    public static Map<String, Object> ccCapture(DispatchContext ctx, Map<String, Object> context) {
        Debug.logInfo("SagePay Token - Entered ccCapture", module);
//        Debug.logInfo("SagePay Token ccCapture context : " + context, module);
        Map<String,Object> response=null;

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (null == authTransaction) {
            response = ServiceUtil.returnError("authorize Transaction  not found nothing to capture");
        }
        context.put("authTransaction", authTransaction);
        context.put("orderPaymentPreference", orderPaymentPreference);
        response = processCardCapturePayment(ctx, context);

        Debug.logInfo("SagePay Token - ccCapture response : " + response, module);
        Debug.logInfo("SagePay Token - Exiting ccCapture", module);

        return response;
    }


    private static Map<String, Object> processCardCapturePayment(DispatchContext ctx, Map<String, Object> context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();

        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");
        GenericValue authTransaction = (GenericValue) context.get("authTransaction");
        BigDecimal amount = (BigDecimal) context.get("captureAmount");
        String vendorTxCode = (String) authTransaction.get("altReference");
        String vpsTxId = (String) authTransaction.get("referenceNum");
        String securityKey = (String) authTransaction.get("gatewayFlag");
        String txAuthCode = (String) authTransaction.get("gatewayCode");
        String currency = (String) authTransaction.get("currencyUomId");

        try {

           	GenericValue paymentMethod = (GenericValue) authTransaction.getRelatedOne("PaymentMethod");
        	GenericValue sagePayTokenPaymentMethod = (GenericValue) paymentMethod.getRelatedOne("SagePayTokenPaymentMethod");
        	String token=sagePayTokenPaymentMethod.getString("sagePayToken");
        	GenericValue billingAddress = (GenericValue) sagePayTokenPaymentMethod.getRelatedOneCache("PostalAddress");
        	context.put("billingAddress", billingAddress);
        	context.put("processAmount", amount);
            Map<String, String> billingInfo = buildCustomerBillingInfo(context);

        	
        	Map<String, Object> paymentResult = dispatcher.runSync("SagePayTokenPaymentAuthorisation",
                    UtilMisc.toMap(
                            "paymentGatewayConfigId", paymentGatewayConfigId,
                            "token",token,
                            "vendorTxCode", vendorTxCode,
                            "vpsTxId", vpsTxId,
                            "securityKey", securityKey,
                            "txAuthNo", txAuthCode,
                            "amount", amount.toString(),
                            "currency",currency,
                            "description", billingInfo.get("description"),
                            "billingFirstnames", billingInfo.get("billingFirstnames"),
                            "billingSurname", billingInfo.get("billingSurname"),
                            "billingAddress", billingInfo.get("billingAddress"),
                            "billingAddress2", billingInfo.get("billingAddress2"),
                            "billingCity", billingInfo.get("billingCity"),
                            "billingState", billingInfo.get("billingState"),
                            "billingPostCode", billingInfo.get("billingPostCode"),
                            "billingCountry", billingInfo.get("billingCountry"),
                            "billingPhone", billingInfo.get("billingPhone")
                            
                        )
                    );
            Debug.logInfo("SagePay Token - SagePayTokenPaymentAuthorisation result : " + paymentResult, module);
            String status = (String) paymentResult.get("status");
            String statusDetail = (String) paymentResult.get("statusDetail");
            if (status != null && "OK".equals(status)) {
                Debug.logInfo("SagePay Token - Payment Captured for Order : " + vendorTxCode, module);
                result = SagePayUtil.buildCardCapturePaymentResponse(Boolean.TRUE, txAuthCode, securityKey, amount, vpsTxId, vendorTxCode, statusDetail);
            } else {
                Debug.logInfo("SagePay Token - Invalid status " + status + " received for order : " + vendorTxCode, module);
                result = SagePayUtil.buildCardCapturePaymentResponse(Boolean.FALSE, txAuthCode, securityKey, amount, vpsTxId, vendorTxCode, statusDetail);
            }
        } catch(Exception e) {
            Debug.logError(e, "Error in calling SagePayTokenPaymentAuthorisation", module);
            result = ServiceUtil.returnError("Exception in calling SagePayTokenPaymentRegistration : " + e.getMessage());
        }
        return result;
    }


    public static Map<String, Object> ccRefund(DispatchContext ctx, Map<String, Object> context) {
        Debug.logInfo("SagePay Token - Entered ccRefund", module);
        Debug.logInfo("SagePay Token - ccRefund context : " + context, module);

        Delegator delegator = ctx.getDelegator();
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue captureTransaction = PaymentGatewayServices.getCaptureTransaction(orderPaymentPreference);
        if (captureTransaction == null) {
            return ServiceUtil.returnError("No captured transaction found for the OrderPaymentPreference; cannot Refund");
        }
        Debug.logInfo("SagePay Token - ccRefund captureTransaction : " + captureTransaction, module);
        GenericValue creditCard = null;
        try {
            creditCard = delegator.getRelatedOne("CreditCard", orderPaymentPreference);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting CreditCard for OrderPaymentPreference : " + orderPaymentPreference, module);
            return ServiceUtil.returnError("Unable to obtain cc information from payment preference");
        }
        context.put("creditCard",creditCard);
        context.put("captureTransaction", captureTransaction);
        context.put("orderPaymentPreference", orderPaymentPreference);

        List<GenericValue> authTransactions = PaymentGatewayServices.getAuthTransactions(orderPaymentPreference);

        EntityCondition authCondition = EntityCondition.makeCondition("paymentServiceTypeEnumId", "PRDS_PAY_AUTH");
        List<GenericValue> authTransactions1 = EntityUtil.filterByCondition(authTransactions, authCondition);

        GenericValue authTransaction = EntityUtil.getFirst(authTransactions1);

        Timestamp authTime = authTransaction.getTimestamp("transactionDate");
        Calendar authCal = Calendar.getInstance();
        authCal.setTimeInMillis(authTime.getTime());

        Timestamp nowTime = UtilDateTime.nowTimestamp();
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTimeInMillis(nowTime.getTime());

        Calendar yesterday = Calendar.getInstance();
        yesterday.set(nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH), nowCal.get(Calendar.DATE), 23, 59, 59);
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        Map<String, Object> response = null;

        if (authCal.before(yesterday)) {
            Debug.logInfo("SagePay Token - Calling Refund for Refund", module);
            response = processCardRefundPayment(ctx, context);
        } else {

            Calendar cal = Calendar.getInstance();
            cal.set(nowCal.get(Calendar.YEAR), nowCal.get(Calendar.MONTH), nowCal.get(Calendar.DATE), 23, 49, 59);

            if (authCal.before(cal)) {
                Debug.logInfo("SagePay Token - Calling Void for Refund", module);
                response = processCardVoidPayment(ctx, context);
            } else {
                Debug.logInfo("SagePay Token - Calling Refund for Refund", module);
                response = processCardRefundPayment(ctx, context);
            }
        }

        Debug.logInfo("SagePay Token - ccRefund response : " + response, module);
        return response;
    }

    private static Map<String, Object> processCardRefundPayment(DispatchContext ctx, Map<String, Object> context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();

        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");
        GenericValue captureTransaction = (GenericValue) context.get("captureTransaction");
        BigDecimal amount = (BigDecimal) context.get("refundAmount");
        String currency = (String) captureTransaction.get("currencyUomId");

        String orderId = (String) captureTransaction.get("altReference");
        orderId = "R" + orderId;

        try {
        	GenericValue paymentMethod = (GenericValue) captureTransaction.getRelatedOne("PaymentMethod");
        	GenericValue sagePayTokenPaymentMethod = (GenericValue) paymentMethod.getRelatedOne("SagePayTokenPaymentMethod");
        	String token=sagePayTokenPaymentMethod.getString("sagePayToken");
        	GenericValue billingAddress = (GenericValue) sagePayTokenPaymentMethod.getRelatedOneCache("PostalAddress");
        	context.put("billingAddress", billingAddress);
        	context.put("processAmount", amount);
            Map<String, String> billingInfo = buildCustomerBillingInfo(context);

            Map<String, Object> paymentResult = dispatcher.runSync("SagePayTokenPaymentRefund",
                    UtilMisc.toMap(
                            "paymentGatewayConfigId", paymentGatewayConfigId,
                            "token",token,
                            "vendorTxCode", orderId,
                            "amount", amount.toString(),
                            "currency", currency,
                            "relatedVPSTxId", captureTransaction.get("referenceNum"),
                            "relatedVendorTxCode", captureTransaction.get("altReference"),
                            "relatedSecurityKey", captureTransaction.get("gatewayFlag"),
                            "relatedTxAuthNo", captureTransaction.get("gatewayCode"),
                            "description", billingInfo.get("description"),
                            "billingFirstnames", billingInfo.get("billingFirstnames"),
                            "billingSurname", billingInfo.get("billingSurname"),
                            "billingAddress", billingInfo.get("billingAddress"),
                            "billingAddress2", billingInfo.get("billingAddress2"),
                            "billingCity", billingInfo.get("billingCity"),
                            "billingState", billingInfo.get("billingState"),
                            "billingPostCode", billingInfo.get("billingPostCode"),
                            "billingCountry", billingInfo.get("billingCountry"),
                            "billingPhone", billingInfo.get("billingPhone")
                            
                        )
                    );
            Debug.logInfo("SagePay Token - SagePayTokenPaymentRefund result : " + paymentResult, module);

            String status = (String) paymentResult.get("status");
            String statusDetail = (String) paymentResult.get("statusDetail");
            String vpsTxId = (String) paymentResult.get("vpsTxId");
            String txAuthNo = (String) paymentResult.get("txAuthNo");

            if (status != null && "OK".equals(status)) {
                Debug.logInfo("SagePay Token - Payment Refunded for Order : " + orderId, module);
                result = SagePayUtil.buildCardRefundPaymentResponse(Boolean.TRUE, txAuthNo, amount, vpsTxId, orderId, statusDetail);
            } else {
                Debug.logInfo("SagePay Token - Invalid status " + status + " received for order : " + orderId, module);
                result = SagePayUtil.buildCardRefundPaymentResponse(Boolean.FALSE, null, BigDecimal.ZERO, status, orderId, statusDetail);
            }

        } catch(Exception e) {
            Debug.logError(e, "Error in calling SagePayTokenPaymentRefund", module);
            result = ServiceUtil.returnError("Exception in calling SagePayTokenPaymentRefund : " + e.getMessage());
        }

        return result;
    }

    private static Map<String, Object> processCardVoidPayment(DispatchContext ctx, Map<String, Object> context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();

        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");
        GenericValue captureTransaction = (GenericValue) context.get("captureTransaction");
        BigDecimal amount = (BigDecimal) context.get("refundAmount");
        String orderId = (String) captureTransaction.get("altReference");
        String currency = (String) captureTransaction.get("currencyUomId");

        try {
        	GenericValue paymentMethod = (GenericValue) captureTransaction.getRelatedOne("PaymentMethod");
        	GenericValue sagePayTokenPaymentMethod = (GenericValue) paymentMethod.getRelatedOne("SagePayTokenPaymentMethod");
        	String token=sagePayTokenPaymentMethod.getString("sagePayToken");
        	GenericValue billingAddress = (GenericValue) sagePayTokenPaymentMethod.getRelatedOneCache("PostalAddress");
        	context.put("billingAddress", billingAddress);
        	context.put("processAmount", amount);
            Map<String, String> billingInfo = buildCustomerBillingInfo(context);

            Map<String, Object> paymentResult = dispatcher.runSync("SagePayTokenPaymentVoid",
                    UtilMisc.toMap(
                            "paymentGatewayConfigId", paymentGatewayConfigId,
                            "token",token,
                            "vendorTxCode", captureTransaction.get("altReference"),
                            "amount", amount.toString(),
                            "currency", currency,
                            "vpsTxId", captureTransaction.get("referenceNum"),
                            "securityKey", captureTransaction.get("gatewayFlag"),
                            "txAuthNo", captureTransaction.get("gatewayCode"),
                            "description", billingInfo.get("description"),
                            "billingFirstnames", billingInfo.get("billingFirstnames"),
                            "billingSurname", billingInfo.get("billingSurname"),
                            "billingAddress", billingInfo.get("billingAddress"),
                            "billingAddress2", billingInfo.get("billingAddress2"),
                            "billingCity", billingInfo.get("billingCity"),
                            "billingState", billingInfo.get("billingState"),
                            "billingPostCode", billingInfo.get("billingPostCode"),
                            "billingCountry", billingInfo.get("billingCountry"),
                            "billingPhone", billingInfo.get("billingPhone")
                            
                        )
                    );

            Debug.logInfo("SagePay Token - SagePayTokenPaymentVoid result : " + paymentResult, module);

            String status = (String) paymentResult.get("status");
            String statusDetail = (String) paymentResult.get("statusDetail");

            if (status != null && "OK".equals(status)) {
                Debug.logInfo("SagePay Token - Payment Voided for Order : " + orderId, module);
                result = SagePayUtil.buildCardVoidPaymentResponse(Boolean.TRUE, amount, "SUCCESS", orderId, statusDetail);
            } else if (status != null && "MALFORMED".equals(status)) {
                Debug.logInfo("SagePay Token - Malformed void request for order : " + orderId, module);
                result = SagePayUtil.buildCardVoidPaymentResponse(Boolean.FALSE, BigDecimal.ZERO, "MALFORMED", orderId, statusDetail);
            } else if (status != null && "INVALID".equals(status)){
                Debug.logInfo("SagePay Token - Invalid void request for order : " + orderId, module);
                result = SagePayUtil.buildCardVoidPaymentResponse(Boolean.FALSE, BigDecimal.ZERO, "INVALID", orderId, statusDetail);
            } else if (status != null && "ERROR".equals(status)){
                Debug.logInfo("SagePay Token - Error in void request for order : " + orderId, module);
                result = SagePayUtil.buildCardVoidPaymentResponse(Boolean.FALSE, BigDecimal.ZERO, "ERROR", orderId, statusDetail);
            }

        } catch(Exception e) {
            Debug.logError(e, "Error in calling SagePayTokenPaymentVoid", module);
            result = ServiceUtil.returnError("Exception in calling SagePayTokenPaymentVoid : " + e.getMessage());
        }
        return result;
    }

    public static Map<String, Object> ccRelease(DispatchContext ctx, Map<String, Object> context) {

        Debug.logInfo("SagePay Token - Entered ccRelease", module);
        Debug.logInfo("SagePay Token - ccRelease context : " + context, module);

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");

        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (authTransaction == null) {
            return ServiceUtil.returnError("No authorization transaction found for the OrderPaymentPreference; cannot Release");
        }
        context.put("authTransaction", authTransaction);
        context.put("orderPaymentPreference", orderPaymentPreference);

        Map<String, Object> response = processCardReleasePayment(ctx, context);
        Debug.logInfo("SagePay Token - ccRelease response : " + response, module);
        return response;
    }
    public static Map<String, Object> ccVoid(DispatchContext ctx, Map<String, Object> context) {

        Debug.logInfo("SagePay Token - Entered ccVoid", module);
        Debug.logInfo("SagePay Token - ccVoid context : " + context, module);

        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");

        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (authTransaction == null) {
            return ServiceUtil.returnError("No authorization transaction found for the OrderPaymentPreference; cannot Void");
        }
        context.put("authTransaction", authTransaction);
        context.put("orderPaymentPreference", orderPaymentPreference);

        Map<String, Object> response = processCardVoidPayment(ctx, context);
        Debug.logInfo("SagePay Token - ccVoid response : " + response, module);
        return response;
    }


    private static Map<String, Object> processCardReleasePayment(DispatchContext ctx, Map<String, Object> context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();

        LocalDispatcher dispatcher = ctx.getDispatcher();

        String paymentGatewayConfigId = (String) context.get("paymentGatewayConfigId");
        BigDecimal amount = (BigDecimal) context.get("releaseAmount");

        GenericValue authTransaction = (GenericValue) context.get("authTransaction");
        String orderId =  (String) authTransaction.get("altReference");
        String refNum = (String) authTransaction.get("referenceNum");
        String currency = (String) authTransaction.get("currencyUomId");

        try {
           	GenericValue paymentMethod = (GenericValue) authTransaction.getRelatedOne("PaymentMethod");
        	GenericValue sagePayTokenPaymentMethod = (GenericValue) paymentMethod.getRelatedOne("SagePayTokenPaymentMethod");
        	String token=sagePayTokenPaymentMethod.getString("sagePayToken");
        	GenericValue billingAddress = (GenericValue) sagePayTokenPaymentMethod.getRelatedOneCache("PostalAddress");
        	context.put("billingAddress", billingAddress);
        	context.put("processAmount", amount);
            Map<String, String> billingInfo = buildCustomerBillingInfo(context);

            Map<String, Object> paymentResult = dispatcher.runSync("SagePayTokenPaymentRelease",
                    UtilMisc.toMap(
                            "paymentGatewayConfigId", paymentGatewayConfigId,
                            "token",token,
                            "vendorTxCode",orderId,
                            "amount", amount.toString(),
                            "currency", currency,
                            "releaseAmount", amount.toString(),
                            "vpsTxId", refNum,
                            "securityKey", authTransaction.get("gatewayFlag"),
                            "txAuthNo", authTransaction.get("gatewayCode"),
                            "description", billingInfo.get("description"),
                            "billingFirstnames", billingInfo.get("billingFirstnames"),
                            "billingSurname", billingInfo.get("billingSurname"),
                            "billingAddress", billingInfo.get("billingAddress"),
                            "billingAddress2", billingInfo.get("billingAddress2"),
                            "billingCity", billingInfo.get("billingCity"),
                            "billingState", billingInfo.get("billingState"),
                            "billingPostCode", billingInfo.get("billingPostCode"),
                            "billingCountry", billingInfo.get("billingCountry"),
                            "billingPhone", billingInfo.get("billingPhone")
                            
                        )
                    );

            Debug.logInfo("SagePay Token - SagePayTokenPaymentRelease result : " + paymentResult, module);

            String status = (String) paymentResult.get("status");
            String statusDetail = (String) paymentResult.get("statusDetail");

            if (status != null && "OK".equals(status)) {
                Debug.logInfo("SagePay Token - Payment Released for Order : " + orderId, module);
                result = SagePayUtil.buildCardReleasePaymentResponse(Boolean.TRUE, null, amount, refNum, orderId, statusDetail);
            } else {
                Debug.logInfo("SagePay Token - Invalid status " + status + " received for order : " + orderId, module);
                result = SagePayUtil.buildCardReleasePaymentResponse(Boolean.FALSE, null, amount, refNum, orderId, statusDetail);
            }

        } catch(Exception e) {
            Debug.logError(e, "Error in calling SagePayTokenPaymentRelease", module);
            result = ServiceUtil.returnError("Exception in calling SagePayTokenPaymentRefund : " + e.getMessage());
        }

        return result;
    }

}
