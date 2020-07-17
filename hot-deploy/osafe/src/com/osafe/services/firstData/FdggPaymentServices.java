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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * ClearCommerce Payment Services (CCE 5.4)
 */
public class FdggPaymentServices {

    public final static String module = FdggPaymentServices.class.getName();
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
    public final static String resource = "AccountingUiLabels";

    public static Map<String, Object> ccAuth(DispatchContext dctx, Map<String, Object> context) {

        Delegator delegator = dctx.getDelegator();
        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }
        Document requestDoc =createSoapEnvelopeDocument(gatewayConfigProps);

        Element envelopeElement = requestDoc.getDocumentElement();
        Element envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");

        requestDoc = appendOrderRequest(envelopeBodyElement, gatewayConfigProps);
        Debug.logInfo("CCAuth ccAuth:requestDoc - 83", module);
        
        envelopeElement = requestDoc.getDocumentElement();
        envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");
        Element orderRequestElement = UtilXml.firstChildElement(envelopeBodyElement, "fdggwsapi:FDGGWSApiOrderRequest");
        Element transactionElement = UtilXml.addChildElement(orderRequestElement, version + "Transaction", requestDoc);
        
        Debug.logInfo("CCAuth transactionElement - 91" + UtilXml.toXml(transactionElement), module);
        requestDoc = appendCreditCardTxTypeNode(transactionElement,"preAuth", gatewayConfigProps);
        Debug.logInfo("CCAuth appendCreditCardTxTypeNode", module);
        requestDoc = appendCreditCardNode(context,transactionElement, gatewayConfigProps);
        Debug.logInfo("CCAuth appendCreditCardNode", module);
        requestDoc = appendPaymentNode(context,transactionElement, gatewayConfigProps);
        Debug.logInfo("CCAuth appendPaymentNode", module);
        requestDoc = appendTransactionDetailsNode(context,transactionElement, gatewayConfigProps);
        Debug.logInfo("CCAuth appendTransactionDetailsNode", module);
        requestDoc = appendBillingNode(context,transactionElement, gatewayConfigProps);
        Debug.logInfo("CCAuth appendBillingNode", module);
        requestDoc = appendShippingNode(context,transactionElement, gatewayConfigProps);
        Debug.logInfo("CCAuth appendShippingNode", module);

        Document responseDoc = null;
        try {
            responseDoc = sendRequest(requestDoc,gatewayConfigProps);
        } 
         catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
         }

        return processAuthResponse(responseDoc);
    }

    public static Map<String, Object> ccCapture(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = (Locale) context.get("locale");
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (authTransaction == null) 
        {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "AccountingPaymentTransactionAuthorizationNotFoundCannotCapture", locale));
        }

        Delegator delegator = dctx.getDelegator();
        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }
        Document requestDoc =createSoapEnvelopeDocument(gatewayConfigProps);

        Element envelopeElement = UtilXml.firstChildElement(requestDoc.getDocumentElement(), "SOAP-ENV:Envelope");
        Element envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");
        requestDoc = appendOrderRequest(envelopeBodyElement, gatewayConfigProps);
        
        envelopeElement = UtilXml.firstChildElement(requestDoc.getDocumentElement(), "SOAP-ENV:Envelope");
        envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");
        Element orderRequestElement = UtilXml.firstChildElement(envelopeBodyElement, "fdggwsapi:FDGGWSApiOrderRequest");
        Element transactionElement = UtilXml.addChildElement(orderRequestElement, version + "Transaction", requestDoc);
        
        requestDoc = appendCreditCardTxTypeNode(transactionElement,"postAuth", gatewayConfigProps);
        requestDoc = appendCreditCardNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendPaymentNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendTransactionDetailsNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendBillingNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendShippingNode(context,transactionElement, gatewayConfigProps);

        Document responseDoc = null;
        try {
            responseDoc = sendRequest(requestDoc,gatewayConfigProps);
        } 
         catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
         }

        return processCaptureResponse(responseDoc);
    }

    public static Map<String, Object> ccRelease(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = (Locale) context.get("locale");
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        Delegator delegator = dctx.getDelegator();
        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }
        Document requestDoc =createSoapEnvelopeDocument(gatewayConfigProps);

        Element envelopeElement = UtilXml.firstChildElement(requestDoc.getDocumentElement(), "SOAP-ENV:Envelope");
        Element envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");
        requestDoc = appendOrderRequest(envelopeBodyElement, gatewayConfigProps);
        
        envelopeElement = UtilXml.firstChildElement(requestDoc.getDocumentElement(), "SOAP-ENV:Envelope");
        envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");
        Element orderRequestElement = UtilXml.firstChildElement(envelopeBodyElement, "fdggwsapi:FDGGWSApiOrderRequest");
        Element transactionElement = UtilXml.addChildElement(orderRequestElement, version + "Transaction", requestDoc);
        
        requestDoc = appendCreditCardTxTypeNode(transactionElement,"void", gatewayConfigProps);
        requestDoc = appendCreditCardNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendPaymentNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendTransactionDetailsNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendBillingNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendShippingNode(context,transactionElement, gatewayConfigProps);

        Document responseDoc = null;
        try {
            responseDoc = sendRequest(requestDoc,gatewayConfigProps);
        } 
         catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
         }

        return processReleaseResponse(responseDoc);
    }

    public static Map<String, Object> ccRefund(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = (Locale) context.get("locale");
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        if (authTransaction == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "AccountingPaymentTransactionAuthorizationNotFoundCannotRefund", locale));
        }

        Delegator delegator = dctx.getDelegator();
        Map<String, String> gatewayConfigProps = buildConfigProperties(context, delegator);
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }
        Document requestDoc =createSoapEnvelopeDocument(gatewayConfigProps);

        Element envelopeElement = UtilXml.firstChildElement(requestDoc.getDocumentElement(), "SOAP-ENV:Envelope");
        Element envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");
        requestDoc = appendOrderRequest(envelopeBodyElement, gatewayConfigProps);
        
        envelopeElement = UtilXml.firstChildElement(requestDoc.getDocumentElement(), "SOAP-ENV:Envelope");
        envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");
        Element orderRequestElement = UtilXml.firstChildElement(envelopeBodyElement, "fdggwsapi:FDGGWSApiOrderRequest");
        Element transactionElement = UtilXml.addChildElement(orderRequestElement, version + "Transaction", requestDoc);
        
        requestDoc = appendCreditCardTxTypeNode(transactionElement,"return", gatewayConfigProps);
        requestDoc = appendCreditCardNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendPaymentNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendTransactionDetailsNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendBillingNode(context,transactionElement, gatewayConfigProps);
        requestDoc = appendShippingNode(context,transactionElement, gatewayConfigProps);

        Document responseDoc = null;
        try {
            responseDoc = sendRequest(requestDoc,gatewayConfigProps);
        } 
         catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
         }

        return processRefundResponse(responseDoc);
    }

    public static Map<String, Object> ccReAuth(DispatchContext dctx, Map<String, Object> context) {
        return null;
        //processReAuthResponse(reauthResponseDoc);

    }


    private static Map<String, Object> processAuthResponse(Document responseDocument) {

    	/*
           <?xml version="1.0" encoding="UTF-8"?> 
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"> 
             <SOAP-ENV:Header /> 
             <SOAP-ENV:Body> 
              <fdggwsapi:FDGGWSApiOrderResponse xmlns:fdggwsapi= "http://secure.linkpt.net/fdggwsapi/schemas_us/fdggwsapi"> 
               <fdggwsapi:CommercialServiceProvider> CSI </fdggwsapi:CommercialServiceProvider> 
               <fdggwsapi:TransactionTime> Tue Nov 03 09:35:05 2009 </fdggwsapi:TransactionTime> 
               <fdggwsapi:TransactionID>2000486340 </fdggwsapi:TransactionID> 
               <fdggwsapi:ProcessorReferenceNumber> OK289C </fdggwsapi:ProcessorReferenceNumber> 
               <fdggwsapi:ProcessorResponseMessage> APPROVED </fdggwsapi:ProcessorResponseMessage> 
               <fdggwsapi:ErrorMessage /> 
               <fdggwsapi:OrderId> A-eb0406bc-7eb8-419b-aa1a-7a4394e2c83e </fdggwsapi:OrderId> 
               <fdggwsapi:ApprovalCode> OK289C0003529354:NNN: </fdggwsapi:ApprovalCode> 
               <fdggwsapi:AVSResponse>PPX</fdggwsapi:AVSResponse> 
               <fdggwsapi:TDate>1256168682</fdggwsapi:TDate> 
               <fdggwsapi:TransactionResult> APPROVED </fdggwsapi:TransactionResult> 
               <fdggwsapi:ProcessorResponseCode> A </fdggwsapi:ProcessorResponseCode> 
               <fdggwsapi:ProcessorApprovalCode> 440368 </fdggwsapi:ProcessorApprovalCode> 
               <fdggwsapi:CalculatedTax/> 
               <fdggwsapi:CalculatedShipping/> 
               <fdggwsapi:TransactionScore> 496 </ fdggwsapi:TransactionScore> 
               <fdggwsapi:FraudAction> ACCEPT </fdggwsapi:FraudAction> 
               <fdggwsapi:AuthenticationResponseCode> XXX </fdggwsapi:AuthenticationResponseCode> 
              </fdggwsapi:FDGGWSApiOrderResponse>              </fdggwsapi:FDGGWSApiOrderResponse> 
             </SOAP-ENV:Body> 
            </SOAP-ENV:Envelope>    	 
    	 */
        Element envelopeElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "SOAP-ENV:Envelope");
        Element envelopeBodyElement = UtilXml.firstChildElement(envelopeElement, "SOAP-ENV:Body");
        Element orderResponseElement = UtilXml.firstChildElement(envelopeBodyElement, "fdggwsapi:FDGGWSApiOrderResponse");
        
        String commercialService = UtilXml.childElementValue(orderResponseElement, "fdggwsapi:CommercialServiceProvider");
        String transactionTime = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:TransactionTime");
        String processorReferenceNumber = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:ProcessorReferenceNumber");
        String processorResponseMessage = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:ProcessorResponseMessage");
        String processorResponseCode = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:ProcessorResponseCode");
        String processorApprovalCode = UtilXml.childElementValue(orderResponseElement, "fdggwsapi:ProcessorApprovalCode");
        String errorMessage = UtilXml.childElementValue(orderResponseElement, "fdggwsapi:ErrorMessage");
        String orderId = UtilXml.childElementValue(orderResponseElement, "fdggwsapi:OrderId");
        String approvalCode = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:ApprovalCode");
        String aVSResponse = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:AVSResponse");
        String tDate = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:TDate");
        String transactionResult = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:TransactionResult");
        String transactionId = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:TransactionID");
        String calculatedTax = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:CalculatedTax");
        String calculatedShipping = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:CalculatedShipping");
        String transactionScore = UtilXml.childElementValue(orderResponseElement, "fdggwsapi:TransactionScore");
        String authenticationResponseCode = UtilXml.childElementValue(orderResponseElement,"fdggwsapi:AuthenticationResponseCode");

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map<String, Object> result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("APPROVED".equals(transactionResult)) 
        {
            result.put("authResult", Boolean.valueOf(true));
            result.put("authCode",processorResponseCode);
            String authAmountStr = "29.99";
            result.put("processAmount", new BigDecimal(authAmountStr).movePointLeft(2));
            result.put("authMessage", processorResponseMessage);
        } 
        else 
        {
            result.put("authResult", Boolean.valueOf(false));
            result.put("processAmount", BigDecimal.ZERO);
            result.put("authMessage", errorMessage);
        }

        result.put("authRefNum", orderId);
        result.put("authFlag", approvalCode);
        result.put("avsCode", aVSResponse);
        result.put("scoreCode", transactionScore);
        result.put("internalRespMsgs", processorResponseMessage);

        return result;
    }

    private static Map<String, Object> processCreditResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map<String, Object> result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("creditResult", Boolean.valueOf(true));
            result.put("creditCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String creditAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("creditAmount", new BigDecimal(creditAmountStr).movePointLeft(2));
        } else {
            result.put("creditResult", Boolean.valueOf(false));
            result.put("creditAmount", BigDecimal.ZERO);
        }

        result.put("creditRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("creditFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("creditMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        List<String> messages = getMessageList(responseDocument);
        if (UtilValidate.isNotEmpty(messages)) {
            result.put("internalRespMsgs", messages);
        }
        return result;
    }

    private static Map<String, Object> processCaptureResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map<String, Object> result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("captureResult", Boolean.valueOf(true));
            result.put("captureCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String captureAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("captureAmount", new BigDecimal(captureAmountStr).movePointLeft(2));
        } else {
            result.put("captureResult", Boolean.valueOf(false));
            result.put("captureAmount", BigDecimal.ZERO);
        }

        result.put("captureRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("captureFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("captureMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        List<String> messages = getMessageList(responseDocument);
        if (UtilValidate.isNotEmpty(messages)) {
            result.put("internalRespMsgs", messages);
        }
        return result;
    }

    private static Map<String, Object> processReleaseResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map<String, Object> result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("releaseResult", Boolean.valueOf(true));
            result.put("releaseCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String releaseAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("releaseAmount", new BigDecimal(releaseAmountStr).movePointLeft(2));
        } else {
            result.put("releaseResult", Boolean.valueOf(false));
            result.put("releaseAmount", BigDecimal.ZERO);
        }

        result.put("releaseRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("releaseFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("releaseMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        List<String> messages = getMessageList(responseDocument);
        if (UtilValidate.isNotEmpty(messages)) {
            result.put("internalRespMsgs", messages);
        }
        return result;
    }

    private static Map<String, Object> processRefundResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map<String, Object> result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("refundResult", Boolean.valueOf(true));
            result.put("refundCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String refundAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("refundAmount", new BigDecimal(refundAmountStr).movePointLeft(2));
        } else {
            result.put("refundResult", Boolean.valueOf(false));
            result.put("refundAmount", BigDecimal.ZERO);
        }

        result.put("refundRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("refundFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("refundMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        List<String> messages = getMessageList(responseDocument);
        if (UtilValidate.isNotEmpty(messages)) {
            result.put("internalRespMsgs", messages);
        }
        return result;
    }

    private static Map<String, Object> processReAuthResponse(Document responseDocument) {

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
        Element transactionElement = UtilXml.firstChildElement(orderFormElement, "Transaction");
        Element procResponseElement = UtilXml.firstChildElement(transactionElement, "CardProcResp");

        Map<String, Object> result = ServiceUtil.returnSuccess();

        String errorCode = UtilXml.childElementValue(procResponseElement, "CcErrCode");
        if ("1".equals(errorCode)) {
            result.put("reauthResult", Boolean.valueOf(true));
            result.put("reauthCode", UtilXml.childElementValue(transactionElement, "AuthCode"));

            Element currentTotalsElement = UtilXml.firstChildElement(transactionElement, "CurrentTotals");
            Element totalsElement = UtilXml.firstChildElement(currentTotalsElement, "Totals");
            String reauthAmountStr = UtilXml.childElementValue(totalsElement, "Total");
            result.put("reauthAmount", new BigDecimal(reauthAmountStr).movePointLeft(2));
        } else {
            result.put("reauthResult", Boolean.valueOf(false));
            result.put("reauthAmount", BigDecimal.ZERO);
        }

        result.put("reauthRefNum", UtilXml.childElementValue(orderFormElement, "Id"));
        result.put("reauthFlag", UtilXml.childElementValue(procResponseElement, "Status"));
        result.put("reauthMessage", UtilXml.childElementValue(procResponseElement, "CcReturnMsg"));

        List<String> messages = getMessageList(responseDocument);
        if (UtilValidate.isNotEmpty(messages)) {
            result.put("internalRespMsgs", messages);
        }
        return result;
    }

    private static List<String> getMessageList(Document responseDocument) {

        List<String> messageList = new ArrayList<String>();

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element messageListElement = UtilXml.firstChildElement(engineDocElement, "MessageList");
        List<? extends Element> messageElementList = UtilXml.childElementList(messageListElement, "Message");
        if (UtilValidate.isNotEmpty(messageElementList)) {
            for (Iterator<? extends Element> i = messageElementList.iterator(); i.hasNext();) {
                Element messageElement = i.next();
                int severity = 0;
                try {
                    severity = Integer.parseInt(UtilXml.childElementValue(messageElement, "Sev"));
                } catch (NumberFormatException nfe) {
                    Debug.logError("Error parsing message severity: " + nfe.getMessage(), module);
                    severity = 9;
                }
                String message = "[" + UtilXml.childElementValue(messageElement, "Audience") + "] " +
                        UtilXml.childElementValue(messageElement, "Text") + " (" + severity + ")";
                messageList.add(message);
            }
        }

        return messageList;
    }

    private static int getMessageListMaxSev(Document responseDocument) {

        int maxSev = 0;

        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        Element messageListElement = UtilXml.firstChildElement(engineDocElement, "MessageList");
        String maxSevStr = UtilXml.childElementValue(messageListElement, "MaxSev");
        if (UtilValidate.isNotEmpty(maxSevStr)) {
            try {
                maxSev = Integer.parseInt(maxSevStr);
            } catch (NumberFormatException nfe) {
                Debug.logError("Error parsing MaxSev: " + nfe.getMessage(), module);
                maxSev = 9;
            }
        }
        return maxSev;
    }

    private static String getReferenceNum(Document responseDocument) {
        String referenceNum = null;
        Element engineDocElement = UtilXml.firstChildElement(responseDocument.getDocumentElement(), "EngineDoc");
        if (engineDocElement != null) {
            Element orderFormElement = UtilXml.firstChildElement(engineDocElement, "OrderFormDoc");
            if (orderFormElement != null) {
                referenceNum = UtilXml.childElementValue(orderFormElement, "Id");
            }
        }
        return referenceNum;
    }


    private static Document createSoapEnvelopeDocument(Map<String,String> gatewayConfigProps) {

        // Envelope
    	/*
    	 <?xml version="1.0" encoding="UTF-8"?> 
    	   <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"> 
    	     <SOAP-ENV:Header /> 
    	     <SOAP-ENV:Body> 
    	          <!-- Transaction or action XML --> 
    	     </SOAP-ENV:Body> 
    	   </SOAP-ENV:Envelope>
    	 */
        String soapEnvelopeXmlns = gatewayConfigProps.get("versionXmlns");
        Document requestEnvelopeDocument = UtilXml.makeEmptyXmlDocument("SOAP-ENV:Envelope");
        Debug.logInfo("Create Envelope Element:" + UtilXml.toXml(requestEnvelopeDocument),module);
        Element envelopeElement = requestEnvelopeDocument.getDocumentElement();
        envelopeElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:SOAP-ENV",soapEnvelopeXmlns);
        Element headerElement = UtilXml.addChildElement(envelopeElement, "SOAP-ENV:Header", requestEnvelopeDocument);
        Element bodyElement = UtilXml.addChildElement(envelopeElement, "SOAP-ENV:Body", requestEnvelopeDocument);

        return requestEnvelopeDocument;
    }    
    
    
    
    private static Document appendOrderRequest(Element element,Map<String,String> gatewayConfigProps) {

    	/*
    	 <fdggwsapi:FDGGWSApiOrderRequest xmlns:v1= "http://secure.linkpt.net/fdggwsapi/schemas_us/v1" xmlns:fdggwsapi= "http://secure.linkpt.net/fdggwsapi/schemas_us/fdggwsapi"> 
    	  <v1:Transaction> 
    	    <v1:TeleCheckTxType>...</v1:TeleCheckTxType> 
    	    <v1:TeleCheckData>...</v1:TeleCheckData> 
    	    <v1:Payment>...</v1:Payment> 
    	    <v1:TransactionDetails>...</v1:TransactionDetails> 
    	    <v1:Billing>...</v1:Billing> 
    	    <v1:Shipping>...</v1:Shipping> 
    	  </v1:Transaction> 
    	 </fdggwsapi:FDGGWSApiOrderRequest>
    	 */
        Document document = element.getOwnerDocument();
        Element orderRequestElement = UtilXml.addChildElement(element, "fdggwsapi:FDGGWSApiOrderRequest", document);
        Debug.logInfo("Order request init:" + UtilXml.toXml(orderRequestElement), module);

        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
            String versionXmlns = gatewayConfigProps.get("versionXmlns");
            orderRequestElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + version ,versionXmlns);
            Debug.logInfo("Order request version:" + UtilXml.toXml(orderRequestElement), module);
        }
        String fdggwsapiXmlns = gatewayConfigProps.get("fdggwsapiXmlns");
        if (UtilValidate.isNotEmpty(fdggwsapiXmlns))
        {
           orderRequestElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:fdggwsapi",fdggwsapiXmlns);
           Debug.logInfo("Order request fdgg:" + UtilXml.toXml(orderRequestElement), module);
        }
        
        return document;
    }
    
    private static Document appendCreditCardTxTypeNode(Element element,String type, Map<String, String> gatewayConfigProps) {

    	/*
           <v1:Transaction> 
             <v1:CreditCardTxType>...</v1:CreditCardTxType> 
             <v1:CreditCardData>...</v1:CreditCardData> 
             <v1:Payment>...</v1:Payment> 
             <v1:TransactionDetails>...</v1:TransactionDetails> 
             <v1:Billing>...</v1:Billing> 
             <v1:Shipping>...</v1:Shipping> 
            </v1:Transaction>    	 
        */
        Document document = element.getOwnerDocument();
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }
        String storeId= gatewayConfigProps.get("storeId");

        Element creditCardTxTypeElement = UtilXml.addChildElement(element,version + "CreditCardTxType", document);
        Element storeIdElement = UtilXml.addChildElementValue(creditCardTxTypeElement, version + "StoreId", storeId, document);
        Element typeElement = UtilXml.addChildElementValue(creditCardTxTypeElement, version + "Type", type, document);

        return document;
    
    }
    
    
    private static Document appendCreditCardNode(Map<String, Object> context, Element element,Map<String, String> gatewayConfigProps) {

    	/*
           <v1:Transaction> 
             <v1:CreditCardTxType>...</v1:CreditCardTxType> 
             <v1:CreditCardData>...</v1:CreditCardData> 
             <v1:Payment>...</v1:Payment> 
             <v1:TransactionDetails>...</v1:TransactionDetails> 
             <v1:Billing>...</v1:Billing> 
             <v1:Shipping>...</v1:Shipping> 
            </v1:Transaction>    	 
        */
        Document document = element.getOwnerDocument();
        GenericValue creditCard = (GenericValue) context.get("creditCard");
        String  cardSecurityCode = (String) context.get("cardSecurityCode");
    	if (UtilValidate.isNotEmpty(creditCard))
    	{
            String version= gatewayConfigProps.get("apiVersion");
            if (UtilValidate.isNotEmpty(version))
            {
            	version = version + ":";
            }

            Element creditCardDateElement = UtilXml.addChildElement(element, version + "CreditCardData", document);
            Element cardNumberElement = UtilXml.addChildElementValue(creditCardDateElement, version + "CardNumber", creditCard.getString("cardNumber"), document);
            String expDate = creditCard.getString("expireDate");
            if (UtilValidate.isNotEmpty(expDate))
            {
            	String expMonth = expDate.substring(0, 2);
	            Element expMonthElement = UtilXml.addChildElementValue(creditCardDateElement, version + "ExpMonth", expMonth, document);
            	String expYear = expDate.substring(3);
	            Element expYearElement = UtilXml.addChildElementValue(creditCardDateElement, version + "ExpYear", "", document);
            }
            if (UtilValidate.isNotEmpty(cardSecurityCode))
            {
                Element cardCodeValueElement = UtilXml.addChildElementValue(creditCardDateElement, version + "CardCodeValue", cardSecurityCode, document);

            }
            Element cardCodeIndicatorElement = UtilXml.addChildElementValue(creditCardDateElement, version + "CardCodeIndicator", "", document);
            Element trackDataElement = UtilXml.addChildElementValue(creditCardDateElement, version + "TrackData", "", document);

    	}

        return document;
    
    }
    private static Document appendPaymentNode(Map<String, Object> context, Element element, Map<String, String> gatewayConfigProps) {

    	/*
           <v1:Transaction> 
             <v1:CreditCardTxType>...</v1:CreditCardTxType> 
             <v1:CreditCardData>...</v1:CreditCardData> 
             <v1:Payment>...</v1:Payment> 
             <v1:TransactionDetails>...</v1:TransactionDetails> 
             <v1:Billing>...</v1:Billing> 
             <v1:Shipping>...</v1:Shipping> 
            </v1:Transaction>    	 
        */
        Document document = element.getOwnerDocument();
        BigDecimal amount = (BigDecimal) context.get("processAmount");

        String chargeTotal = amount.setScale(decimals, rounding).movePointRight(2).toPlainString();
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }

        Element paymentElement = UtilXml.addChildElement(element, version + "Payment", document);
        Element chargeTotalElement = UtilXml.addChildElementValue(paymentElement, version + "ChargeTotal", chargeTotal, document);
        
        Element subTotalElement = UtilXml.addChildElementValue(paymentElement, version + "SubTotal", "", document);
        Element vATTaxElement = UtilXml.addChildElementValue(paymentElement, version + "VATTax", "", document);
        Element shippingElement = UtilXml.addChildElementValue(paymentElement, version + "Shipping", "", document);
        
        return document;

    }

    private static Document appendTransactionDetailsNode(Map<String, Object> context, Element element,Map<String, String> gatewayConfigProps) {

    	/*
           <v1:Transaction> 
             <v1:CreditCardTxType>...</v1:CreditCardTxType> 
             <v1:CreditCardData>...</v1:CreditCardData> 
             <v1:Payment>...</v1:Payment> 
             <v1:TransactionDetails>...</v1:TransactionDetails> 
             <v1:Billing>...</v1:Billing> 
             <v1:Shipping>...</v1:Shipping> 
            </v1:Transaction>    	 
        */
        Document document = element.getOwnerDocument();
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }
        
        GenericValue billToParty = (GenericValue) context.get("billToParty");
        String orderId = (String) context.get("orderId");
        String customerIp = (String) context.get("customerIpAddress");

        Element transactionDetailsElement = UtilXml.addChildElement(element, version + "TransactionDetails", document);
        Element userIDElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "UserID", billToParty.getString("partyId"), document);
        Element orderIdElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "OrderId", orderId, document);
        if (UtilValidate.isNotEmpty(customerIp))
        {
            Element ipElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "Ip", customerIp, document);

        }
        Element invoiceNumberElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "InvoiceNumber", orderId, document);
        Element referenceNumberElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "ReferenceNumber", "", document);
        Element tDateElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "TDate", "", document);
        Element recurringElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "Recurring", "No", document);
        Element taxExemptElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "TaxExempt", "No", document);
        Element terminalTypeElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "TerminalType", "", document);
        Element transactionOriginElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "TransactionOrigin", "ECI", document);
        Element poNumberElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "PONumber", "", document);
        Element deviceIDElement = UtilXml.addChildElementValue(transactionDetailsElement, version + "DeviceID", "", document);
    
        return document;
        
    }

    private static Document appendBillingNode(Map<String, Object> context, Element element,Map<String, String> gatewayConfigProps) {

    	/*
           <v1:Transaction> 
             <v1:CreditCardTxType>...</v1:CreditCardTxType> 
             <v1:CreditCardData>...</v1:CreditCardData> 
             <v1:Payment>...</v1:Payment> 
             <v1:TransactionDetails>...</v1:TransactionDetails> 
             <v1:Billing>...</v1:Billing> 
             <v1:Shipping>...</v1:Shipping> 
            </v1:Transaction>    	 
        */
        Document document = element.getOwnerDocument();
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }
        GenericValue address = (GenericValue) context.get("billingAddress");
        if (UtilValidate.isNotEmpty(address))
        {
            GenericValue billToParty = (GenericValue) context.get("billToParty");
            GenericValue billToEmail = (GenericValue) context.get("billToEmail");
            Element billingElement = UtilXml.addChildElement(element, version + "Billing", document);
            Element customerIDElement = UtilXml.addChildElementValue(billingElement, version + "CustomerID", billToParty.getString("partyId"), document);
            Element nameElement = UtilXml.addChildElementValue(billingElement, version + "Name", address.getString("toName"), document);
            Element companyElement = UtilXml.addChildElementValue(billingElement, version + "Company", "", document);
            Element address1Element = UtilXml.addChildElementValue(billingElement, version + "Address1", address.getString("address1"), document);
            Element address2Element = UtilXml.addChildElementValue(billingElement, version + "Address2", address.getString("address2"), document);
            Element cityElement = UtilXml.addChildElementValue(billingElement, version + "City", address.getString("city"), document);
            Element stateElement = UtilXml.addChildElementValue(billingElement, version + "State", address.getString("stateProvinceGeoId"), document);
            Element zipElement = UtilXml.addChildElementValue(billingElement, version + "Zip", address.getString("postalCode"), document);
            Element countryElement = UtilXml.addChildElementValue(billingElement, version + "Country", address.getString("countryGeoId"), document);
            Element phoneElement = UtilXml.addChildElementValue(billingElement, version + "Phone", "", document);
            Element faxElement = UtilXml.addChildElementValue(billingElement, version + "Fax", "", document);
            if (UtilValidate.isNotEmpty(billToEmail))
            {
                Element emailElement = UtilXml.addChildElementValue(billingElement, version + "Email", billToEmail.getString("infoString"), document);
            	
            }
        	
        }

    
        return document;
    }
    private static Document appendShippingNode(Map<String, Object> context, Element element,Map<String, String> gatewayConfigProps) {

    	/*
           <v1:Transaction> 
             <v1:CreditCardTxType>...</v1:CreditCardTxType> 
             <v1:CreditCardData>...</v1:CreditCardData> 
             <v1:Payment>...</v1:Payment> 
             <v1:TransactionDetails>...</v1:TransactionDetails> 
             <v1:Billing>...</v1:Billing> 
             <v1:Shipping>...</v1:Shipping> 
            </v1:Transaction>    	 
        */
        Document document = element.getOwnerDocument();
        String version= gatewayConfigProps.get("apiVersion");
        if (UtilValidate.isNotEmpty(version))
        {
        	version = version + ":";
        }
        GenericValue address = (GenericValue) context.get("shippingAddress");
        if (UtilValidate.isNotEmpty(address))
        {

	        Element shippingElement = UtilXml.addChildElement(element, version + "Shipping", document);
	        Element typeElement = UtilXml.addChildElementValue(shippingElement, version + "Type", "", document);
            Element nameElement = UtilXml.addChildElementValue(shippingElement, version + "Name", address.getString("toName"), document);
            Element address1Element = UtilXml.addChildElementValue(shippingElement, version + "Address1", address.getString("address1"), document);
            Element address2Element = UtilXml.addChildElementValue(shippingElement, version + "Address2", address.getString("address2"), document);
            Element cityElement = UtilXml.addChildElementValue(shippingElement, version + "City", address.getString("city"), document);
            Element stateElement = UtilXml.addChildElementValue(shippingElement, version + "State", address.getString("stateProvinceGeoId"), document);
            Element zipElement = UtilXml.addChildElementValue(shippingElement, version + "Zip", address.getString("postalCode"), document);
            Element countryElement = UtilXml.addChildElementValue(shippingElement, version + "Country", address.getString("countryGeoId"), document);

        }
    
        return document;
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
    
    
    private static Document sendRequest(Document requestDocument, Map<String, String> gatewayConfigProps) throws Exception {
        String serverURL = gatewayConfigProps.get("serverUrl");
        
        if (UtilValidate.isEmpty(serverURL)) 
        {
            throw new Exception("Missing server URL; check your FDGG configuration");
        }
        Debug.logInfo("FDGG server URL: " + serverURL, module);

        OutputStream os = new ByteArrayOutputStream();
        String userId = gatewayConfigProps.get("userId");
        String userPassword = gatewayConfigProps.get("userPassword");

        try 
        {
        	
            UtilXml.writeXmlDocument(requestDocument, os, "UTF-8", true, false, 0);

            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setPreemptiveAuthentication(true);
            auth.setUsername(userId);
            auth.setPassword(userId);
            	
            	
        } 
          catch (TransformerException e) 
          {
            throw new Exception("Error serializing requestDocument: " + e.getMessage());
          }

        String xmlString = os.toString();

        Debug.logInfo("FDGG XML request string: " + xmlString, module);
        
        String certificateAlias = gatewayConfigProps.get("certificateAlias");
        HttpClient httpClient = new HttpClient(serverURL);
        httpClient.setClientCertificateAlias(certificateAlias);
        httpClient.setBasicAuthInfo(userId, userPassword);
        
        Debug.logInfo("FDGG certificateAlias string: " + certificateAlias, module);
        Debug.logInfo("FDGG userId string: " + userId, module);
        Debug.logInfo("FDGG userPassword string: " + userPassword, module);

        String response = null;
        try 
        {
            response = httpClient.post(xmlString);
        } 
         catch (HttpClientException hce) 
          {
            Debug.logInfo(hce, module);
            throw new Exception("FDGG connection problem", hce);
          }

        Debug.logInfo("FDGG response: " + response, module);

        Document responseDocument = null;
        try 
        {
            responseDocument = UtilXml.readXmlDocument(response, false);
        } 
         catch (SAXException se){
            throw new Exception("Error reading response Document from a String: " + se.getMessage());
         } catch (ParserConfigurationException pce) {
             throw new Exception("Error reading response Document from a String: " + pce.getMessage());
         } catch (IOException ioe) {
            throw new Exception("Error reading response Document from a String: " + ioe.getMessage());
         }
        if (Debug.verboseOn())
        {
       	 Debug.logVerbose("Result severity from clearCommerce:" + getMessageListMaxSev(responseDocument), module);
        }
        return responseDocument;
    }

}




