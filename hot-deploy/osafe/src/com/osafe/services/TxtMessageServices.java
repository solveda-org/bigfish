package com.osafe.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.HttpClient;
import org.ofbiz.base.util.HttpClientException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.widget.fo.FoScreenRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;

import com.osafe.util.Util;

public class TxtMessageServices {

    public static final String module = TxtMessageServices.class.getName();

    protected static final HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
    protected static final FoScreenRenderer foScreenRenderer = new FoScreenRenderer();
    
    public static final String err_resource = "OSafeAdminUiLabels";

    @SuppressWarnings("unchecked")
    public static Map sendTxtMessage(DispatchContext dctx, Map context) 
    {

        Map<String, Object> results = ServiceUtil.returnSuccess();
        String errMsg = null;
        
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String partyId = (String) context.get("partyId");
        String textMessage = (String) context.get("textMessage");
        String communicationEventId = (String) context.get("communicationEventId");
        String orderId = (String) context.get("orderId");
        String toCell = (String) context.get("toCell");

        
        String textMessageMode = Util.getProductStoreParm(productStoreId, "TXT_MESSAGE_METHOD");
        if (UtilValidate.isNotEmpty(textMessageMode))
        {
            textMessageMode = textMessageMode.toUpperCase().trim();
            if (!textMessageMode.equalsIgnoreCase("CLICKATELL") && !textMessageMode.equalsIgnoreCase("PINNACLE"))
            {
                errMsg = UtilProperties.getMessage("OSafeAdminUiLabels", "TxtMsgMethodUnknownError", locale);
                return results;
            }
        }
        

        
        if (UtilValidate.isNotEmpty(textMessage)) 
        {
            Map<String, Object> paramMap = Util.getProductStoreParmMap(delegator, null,productStoreId);
            getGlobalTextVariables(paramMap, dctx, context);
            textMessage = FlexibleStringExpander.expandString(textMessage, paramMap);
        }
        results.put("communicationEventId", communicationEventId);

        //Need to send HTTP request and response
        //http://api.clickatell.com/http/sendmsg?
        //user=solveda&password=solveda2013
        //&api_id=3414659
        //&to=<Mobile Number(s)>&text=<SMS Message>
        //check the send sms response

        if (textMessageMode.equalsIgnoreCase("CLICKATELL"))
        {

            ResourceBundle PARAMETERS_CLICKATELL =  UtilProperties.getResourceBundle("parameters_clickatell.xml", Locale.getDefault());
            if (UtilValidate.isEmpty(PARAMETERS_CLICKATELL))
            {
                return ServiceUtil.returnFailure("CLICKATELL Parameters have not been configured");

            }
            String userId = PARAMETERS_CLICKATELL.getString("TXT_MESSAGE_USER_ID");
            String password = PARAMETERS_CLICKATELL.getString("TXT_MESSAGE_PASSWORD");
            String apiId = PARAMETERS_CLICKATELL.getString("TXT_MESSAGE_API_ID");
            String fromNumber = PARAMETERS_CLICKATELL.getString("TXT_MESSAGE_VIRTUAL_NUMBER");
            String fromNumberMo = PARAMETERS_CLICKATELL.getString("TXT_MESSAGE_FROM_MO");
            String httpUrl = PARAMETERS_CLICKATELL.getString("TXT_MESSAGE_API_URL");
            String httpMsgUrl = PARAMETERS_CLICKATELL.getString("TXT_MESSAGE_API_MSG_URL");

            
            String responseSendMsg = null;
            boolean isMsgSent = false;
            // send sms
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("user", userId);
            parameters.put("password", password);
            parameters.put("api_id", apiId);
            parameters.put("from", fromNumber);
            parameters.put("mo", fromNumberMo);
            parameters.put("to", toCell);
            parameters.put("text", textMessage);
            HttpClient http = new HttpClient(httpUrl, parameters);
            http.setAllowUntrusted(true);
            try 
            {
             responseSendMsg = http.get(); 
             Debug.logInfo(responseSendMsg, responseSendMsg);
             
            } catch (HttpClientException hce) 
            {
                   Debug.logError(hce, hce.toString(), module);
            }        

            if (UtilValidate.isEmpty(responseSendMsg) || responseSendMsg.startsWith("ERR"))
            {
                if (UtilValidate.isEmpty(responseSendMsg))
                {
                    errMsg= "Unknown Error.";
                    
                }
                else
                {
                    errMsg= responseSendMsg;
                }
                return ServiceUtil.returnError(responseSendMsg);
            }
            

            if (UtilValidate.isNotEmpty(responseSendMsg))
            {
                // get send sms status
                String sentMsgId = responseSendMsg.substring(4);
                Debug.logError("sentMsgId:" + sentMsgId, module);
                parameters = new HashMap<String, Object>();
                parameters.put("user", userId);
                parameters.put("password", password);
                parameters.put("api_id", apiId);
                parameters.put("from", fromNumber);
                parameters.put("mo", fromNumberMo);
                parameters.put("apimsgid", sentMsgId);
                //http = new HttpClient("http://api.clickatell.com/http/querymsg", parameters);
                http = new HttpClient(httpMsgUrl, parameters);
                http.setAllowUntrusted(true);
                try 
                {
                    String responseQueryMsg = http.get();
                    Debug.logInfo(responseQueryMsg, responseQueryMsg);
                    if (UtilValidate.isNotEmpty(responseQueryMsg) && responseQueryMsg.startsWith("ID"))
                    {
                        Debug.logError("responseQueryMsg:" + responseQueryMsg, module);
                        Debug.logError("errMsg:" + errMsg, module);
                        if (UtilValidate.isEmpty(responseQueryMsg))
                        {
                            errMsg= "Unknown Error.";
                            
                        }
                        else
                        {
                            String msgStatusId = responseQueryMsg.substring(responseQueryMsg.lastIndexOf(":")+1);
                            msgStatusId = msgStatusId.replaceAll("^\\s+", "");
                            msgStatusId = msgStatusId.trim();
                            Debug.logError("msgStatusId:" + msgStatusId.trim() +"end", module);
                            //See Appendix B in api pdf for status codes. 
                            // Results Codes
                            if (msgStatusId.equals("001"))
                            {
                                errMsg= msgStatusId + ":" + "The message ID is incorrect or reporting is delayed.";
                            }
                            else if (msgStatusId.equals("002"))
                            {
                                errMsg= msgStatusId + ":" + "The message could not be delivered and has been queued for attempted redelivery.";
                                return ServiceUtil.returnError(errMsg);
                            }
                            else if (msgStatusId.equals("003"))
                            {
                                errMsg= msgStatusId + ":" + "Foreign Postal Code Detected";
                            }
                            else if (msgStatusId.equals("004"))
                            {
                                errMsg= msgStatusId + ":" + "Delivered to the upStream gateway or network (delivered to the recipient).";
                            }
                            else if (msgStatusId.equals("005"))
                            {
                                errMsg= msgStatusId + ":" + "There was an error with the message, probably    caused by the content of the message itself.";
                                return ServiceUtil.returnError(errMsg);
                            }
                            else if (msgStatusId.equals("006"))
                            {
                                errMsg= msgStatusId + ":" + "The message was terminated by    a user (stop message command) or by o ur staff.";
                                return ServiceUtil.returnError(errMsg);
                            }
                            else if (msgStatusId.equals("007"))
                            {
                                errMsg= msgStatusId + ":" + "An error occurred delivering the message to the handset.";
                                return ServiceUtil.returnError(errMsg);
                            }
                            else if (msgStatusId.equals("008"))
                            {
                                //"OK Message received by gateway";
                            }
                            else if (msgStatusId.equals("009"))
                            {
                                errMsg= msgStatusId + ":" + "The routing gateway or network has had an error routing the message.";
                                return ServiceUtil.returnError(errMsg);
                            }
                            else if (msgStatusId.equals("010"))
                            {
                                errMsg= msgStatusId + ":" + "Message has expired before we were able to deliver it to the upStream gateway. No charge applies.";
                            }
                            else if (msgStatusId.equals("011"))
                            {
                                errMsg= msgStatusId + ":" + "Message has been queued at the gateway for delivery at a later time (delayed delivery).";
                            }
                            else if (msgStatusId.equals("012"))
                            {
                                errMsg= msgStatusId + ":" + "The message cannot be delivered due to a lack of funds in your account. Please re - purchase credits.";
                                return ServiceUtil.returnError(errMsg);
                            }
                            else if (msgStatusId.equals("013"))
                            {
                                errMsg= msgStatusId + ":" + "Address Deliverable by USPS only";
                                return ServiceUtil.returnError(errMsg);
                            }
                            else if (msgStatusId.equals("014"))
                            {
                                errMsg= msgStatusId + ":" + "The allowable amount for MT messaging has been exceeded.";
                                return ServiceUtil.returnError(errMsg);
                            }
                            else
                            {
                              errMsg= msgStatusId + ":" + "Unknown Status Code.";
                            }
                            if (UtilValidate.isNotEmpty(errMsg))
                            {
                                Debug.logError("Clickatell Error Message:" + errMsg + " Message Status:" + msgStatusId , module);

                            }
                        }
                        
                    }
                 
                } catch (HttpClientException hce) 
                {
                       Debug.logError(hce, hce.toString(), module);
                }
            }            
        }
        //http://www.smsjust.com/blank/sms/user/urlsms.php?
        //username=tpgwholesale&pass=123456&senderid=09000&dest_mobileno=9650067747&
        //message=test-ritu&response=N
        else if (textMessageMode.equalsIgnoreCase("PINNACLE"))
        {
            ResourceBundle PARAMETERS_PINNACLE =  UtilProperties.getResourceBundle("parameters_pinnacle.xml", Locale.getDefault());
            if (UtilValidate.isEmpty(PARAMETERS_PINNACLE))
            {
                return ServiceUtil.returnFailure("PINNACLE Parameters have not been configured");
            }
            String userName = PARAMETERS_PINNACLE.getString("TXT_MESSAGE_USER_ID");
            String password = PARAMETERS_PINNACLE.getString("TXT_MESSAGE_PASSWORD");
            String senderId = PARAMETERS_PINNACLE.getString("TXT_MESSAGE_API_ID");
            String httpUrl = PARAMETERS_PINNACLE.getString("TXT_MESSAGE_API_URL");

            String responseSendMsg = null;
            // send sms
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("username", userName);
            parameters.put("pass", password);
            parameters.put("senderid", senderId);
            parameters.put("dest_mobileno", toCell);
            // Requirement from Vishal to support template
            // textMessage will be like tempid=XX&F1=XXXX&F2=XXXX&F3=XXXX  OR  message=XXXXX
            // parameters.put("message", textMessage);
            httpUrl = httpUrl.concat("?").concat(UtilHttp.encodeBlanks(textMessage));
            parameters.put("response", "Y");
            HttpClient http = new HttpClient(httpUrl, parameters);
            http.setAllowUntrusted(true);
            try 
            {
                responseSendMsg = http.get(); 
                Debug.logInfo(responseSendMsg, responseSendMsg);
            }
            catch (HttpClientException hce) 
            {
                Debug.logError(hce, hce.toString(), module);
            }

            if (UtilValidate.isEmpty(responseSendMsg) || responseSendMsg.startsWith("ES") || !Util.isNumber(StringUtil.removeNonNumeric(responseSendMsg)))
            {
                return ServiceUtil.returnError(responseSendMsg);
            }
        }

        return results;
    }


    public static Map<String, Object> sendTxtMessageFromTemplate(DispatchContext dctx, Map context) 
    {
        Map<String, Object> results = ServiceUtil.returnSuccess();
        String errMsg = null;
        
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String partyId = (String) context.get("partyId");
        String textMessage = (String) context.get("textMessage");
        String communicationEventId = (String) context.get("communicationEventId");
        String orderId = (String) context.get("orderId");
        String toCell = (String) context.get("toCell");
        String contentId = (String) context.get("templateId");
        GenericValue electronicText=null;
        try 
        {   
            if(UtilValidate.isEmpty(partyId))
            {
                partyId = getPartyId(dctx, context);
            }
            
            if(UtilValidate.isEmpty(productStoreId) && UtilValidate.isNotEmpty(orderId))
            {
                productStoreId = getProductStoreIdFromOrder(dctx, context);
            }
            GenericValue content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", contentId));
            
            GenericValue party = null;
            boolean isPartyTextPreferenceTrue = false;
            if(UtilValidate.isNotEmpty(partyId))
            {
                party = delegator.findByPrimaryKeyCache("Party", UtilMisc.toMap("partyId", partyId));
                isPartyTextPreferenceTrue = getPartyTextPreference(party);
            }
            if(UtilValidate.isEmpty(toCell) && UtilValidate.isNotEmpty(party))
            {
                toCell = getPartyMobileContactNumber(party, delegator);
            }
            //check part text preference and mobile number
            if(!isPartyTextPreferenceTrue || UtilValidate.isEmpty(toCell))
            {
                return results;
            }
            // check content id status
            if (UtilValidate.isEmpty(content) || UtilValidate.isEmpty(content.getString("statusId")) || !content.getString("statusId").equalsIgnoreCase("CTNT_PUBLISHED"))
            {
                return results;
            }
            GenericValue dataResource=content.getRelatedOne("DataResource");
            if (UtilValidate.isNotEmpty(dataResource))
            {
                String dataResourceTypeId=dataResource.getString("dataResourceTypeId");
                if ("ELECTRONIC_TEXT".equals(dataResourceTypeId))
                {
                    electronicText = dataResource.getRelatedOne("ElectronicText");
                    
                }
            }
            if (UtilValidate.isEmpty(electronicText) || UtilValidate.isEmpty(electronicText.getString("textData")))
            {
                return results;
            }
            Map sendTxtMsg = FastMap.newInstance();
            sendTxtMsg.putAll(context);
            sendTxtMsg.put("partyId", partyId);
            sendTxtMsg.put("toCell", toCell);
            sendTxtMsg.put("productStoreId", productStoreId);
            sendTxtMsg.put("textMessage", electronicText.getString("textData"));
            results = dispatcher.runSync("sendTxtMessage", sendTxtMsg);
            
         }
         catch(Exception e)
         {
            Debug.logError(e,module); 
         }

        return results;
    }
    
    public static void getGlobalTextVariables(Map paramMap, DispatchContext dctx, Map context)
    {
        Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String orderId = (String) context.get("orderId");
        try
        {
            if (UtilValidate.isNotEmpty(orderId)) 
            {
                GenericValue orderHeader = null;
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                
                if (UtilValidate.isNotEmpty(orderHeader)) 
                {
                    OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);
                    List<GenericValue> orderItems = orderReadHelper.getOrderItems();
                    List<GenericValue> orderAdjustments = orderReadHelper.getAdjustments();
                    List<GenericValue> orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
                    BigDecimal orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
    
                    BigDecimal orderShippingTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
                    orderShippingTotal = orderShippingTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
    
                    BigDecimal orderTaxTotal = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, true, false);
                    orderTaxTotal = orderTaxTotal.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false));
    
                    BigDecimal orderGrandTotal = orderReadHelper.getOrderGrandTotal();
    
                    paramMap.put("ORDER_CURRENCY",orderReadHelper.getCurrency());
                    paramMap.put("ORDER_ID",orderId);
                    paramMap.put("ORDER_SUB_TOTAL",orderSubTotal);
                    paramMap.put("ORDER_SHIP_TOTAL",orderShippingTotal);
                    paramMap.put("ORDER_TAX_TOTAL",orderTaxTotal);
                    paramMap.put("ORDER_TOTAL",orderGrandTotal);
                    List<GenericValue> orderAttributes = orderHeader.getRelatedCache("OrderAttribute");
                    if (UtilValidate.isNotEmpty(orderAttributes))
                    {
                        Iterator orderAttributeIter = orderAttributes.iterator();
                        while (orderAttributeIter.hasNext()) 
                        {
                            GenericValue orderAttribute = (GenericValue) orderAttributeIter.next();
                            String attrValue = orderAttribute.getString("attrValue");
                            if (UtilValidate.isNotEmpty(attrValue))
                            {
                                attrValue = attrValue.trim();
                            }
                            else
                            {
                                attrValue="";
                            }
                            paramMap.put(orderAttribute.getString("attrName"),attrValue);
                        }
                    }
                }
    
            }
        
            if (UtilValidate.isNotEmpty(partyId)) 
            {
                GenericValue gvParty = null;
                gvParty = delegator.findByPrimaryKeyCache("Party", UtilMisc.toMap("partyId", partyId));
                
                if (UtilValidate.isNotEmpty(gvParty)) 
                {
                    GenericValue person = null;
                    person = gvParty.getRelatedOneCache("Person");
                    if (UtilValidate.isNotEmpty(person)) 
                    {
                        paramMap.put("PARTY_ID",partyId);
                        paramMap.put("FIRST_NAME",person.getString("firstName"));
                        paramMap.put("LAST_NAME",person.getString("lastName"));
                        paramMap.put("MIDDLE_NAME",person.getString("middleName"));
                        paramMap.put("GENDER",person.getString("gender"));
                        paramMap.put("SUFFIX",person.getString("suffix"));
                    }
                    List<GenericValue> userLogins=gvParty.getRelatedCache("UserLogin");
                    GenericValue userLogin = EntityUtil.getFirst(userLogins);
                    if (UtilValidate.isNotEmpty(userLogin)) 
                    {
                        paramMap.put("USER_LOGIN_ID",userLogin.getString("userLoginId"));
                        paramMap.put("LOGIN_EMAIL",userLogin.getString("userLoginId"));
                    }
                    List<GenericValue> partyAttributes = gvParty.getRelatedCache("PartyAttribute");
                    if (UtilValidate.isNotEmpty(partyAttributes))
                    {
                        Iterator partyAttributeIter = partyAttributes.iterator();
                        while (partyAttributeIter.hasNext()) 
                        {
                            GenericValue partyAttribute = (GenericValue) partyAttributeIter.next();
                            String attrValue = partyAttribute.getString("attrValue");
                            if (UtilValidate.isNotEmpty(attrValue))
                            {
                                attrValue = attrValue.trim();
                            }
                            else
                            {
                                attrValue="";
                            }
                            paramMap.put(partyAttribute.getString("attrName"),attrValue);
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            Debug.logError(e,module);
        }
    }
    
    public static String getPartyId(DispatchContext dctx, Map context)
    {
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        String orderId = (String) context.get("orderId");
        try
        {
            if(UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(orderId))
            {
                GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                OrderReadHelper orderReadHelper = null;
                if(UtilValidate.isNotEmpty(orderHeader)) 
                {
                    orderReadHelper = new OrderReadHelper(orderHeader);
                  }
                partyId = (String) orderReadHelper.getPlacingParty().get("partyId");
            }
            else if(UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(userLogin))
            {
                partyId = userLogin.getString("partyId");
            }
        }
        catch (Exception e) 
        {
            Debug.logError(e,module);
        }
        return partyId;
    }
    
    public static boolean getPartyTextPreference(GenericValue party)
    {
        boolean isPartyTextPreferenceTrue = false;
        if(UtilValidate.isNotEmpty(party))
        {
            try 
            {
                List<GenericValue> partyAttributes = party.getRelatedCache("PartyAttribute");
                partyAttributes = EntityUtil.filterByAnd(partyAttributes, UtilMisc.toMap("attrName", "PARTY_TEXT_PREFERENCE"));
                if (UtilValidate.isNotEmpty(partyAttributes)) 
                {
                    GenericValue partyAttribute = EntityUtil.getFirst(partyAttributes);
                    if(partyAttribute.getString("attrValue").equalsIgnoreCase("Y"))
                    {
                        isPartyTextPreferenceTrue = true;
                    }
                }
            } 
            catch (GenericEntityException e) 
            {
                Debug.logError(e,module);
            }
        }
        return isPartyTextPreferenceTrue;
    }
    
    public static String getPartyMobileContactNumber(GenericValue party, Delegator delegator)
    {
        String toCell = "";
        try
        {
            List<GenericValue> partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
            List<GenericValue> partyPurposeMobilePhones = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_MOBILE"));
            partyPurposeMobilePhones = EntityUtil.getRelated("PartyContactMech", partyPurposeMobilePhones);
            partyPurposeMobilePhones = EntityUtil.filterByDate(partyPurposeMobilePhones,true);
            partyPurposeMobilePhones = EntityUtil.orderBy(partyPurposeMobilePhones, UtilMisc.toList("fromDate DESC"));
            if (UtilValidate.isNotEmpty(partyPurposeMobilePhones)) 
            {
                GenericValue partyPurposePhone = EntityUtil.getFirst(partyPurposeMobilePhones);
                GenericValue telecomNumber = partyPurposePhone.getRelatedOneCache("TelecomNumber");
                String countryTeleCode = telecomNumber.getString("countryCode");
                if(UtilValidate.isEmpty(countryTeleCode))
                {
                    List<GenericValue> partyPurposeGeneralLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "GENERAL_LOCATION"));
                    partyPurposeGeneralLocations = EntityUtil.getRelated("PartyContactMech", partyPurposeGeneralLocations);
                    partyPurposeGeneralLocations = EntityUtil.filterByDate(partyPurposeGeneralLocations,true);
                    partyPurposeGeneralLocations = EntityUtil.orderBy(partyPurposeGeneralLocations, UtilMisc.toList("fromDate DESC"));
                    if (UtilValidate.isNotEmpty(partyPurposeGeneralLocations)) 
                    {
                        GenericValue partyPurposeGeneralLocation = EntityUtil.getFirst(partyPurposeGeneralLocations);
                        GenericValue postalAddress = partyPurposeGeneralLocation.getRelatedOneCache("PostalAddress");
                        String countryGeoId = postalAddress.getString("countryGeoId");
                        if(UtilValidate.isNotEmpty(countryGeoId))
                        {
                            
                            GenericValue geo = delegator.findByPrimaryKeyCache("Geo", UtilMisc.toMap("geoId", countryGeoId));
                            String geoCode =  geo.getString("geoCode");
                            if(UtilValidate.isNotEmpty(geoCode))
                            {
                                GenericValue countryTeleCodeGv = delegator.findByPrimaryKeyCache("CountryTeleCode", UtilMisc.toMap("countryCode", geoCode));
                                if(UtilValidate.isNotEmpty(countryTeleCodeGv))
                                {
                                    countryTeleCode = countryTeleCodeGv.getString("teleCode");
                                }
                            }
                        }
                    }
                }
                if(UtilValidate.isNotEmpty(countryTeleCode))
                {
                    toCell = countryTeleCode;
                }
                if(UtilValidate.isNotEmpty(telecomNumber.getString("areaCode")))
                {
                    toCell = toCell.concat(telecomNumber.getString("areaCode"));
                }
                if(UtilValidate.isNotEmpty(telecomNumber.getString("contactNumber")))
                {
                    toCell = toCell.concat(telecomNumber.getString("contactNumber"));
                }
            }
        }
        catch(Exception e)
        {
            Debug.logError(e,module);
        }
        return toCell;
    }
    
    public static String getProductStoreIdFromOrder(DispatchContext dctx, Map context)
    {
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String productStoreId = "";
        try
        {
            if (UtilValidate.isNotEmpty(orderId))
            {
                GenericValue orderHeader = null;
                try 
                {
                    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                    productStoreId=(String)orderHeader.get("productStoreId");
                }
                catch (GenericEntityException e) 
                {
                    Debug.logError(e, module);
                }
            }
        }
        catch(Exception e)
        {
            Debug.logError(e,module);
        }
        return productStoreId;
    }

}
