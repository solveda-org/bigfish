package com.osafe.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;
import org.ofbiz.base.lang.ComparableRange;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilObject;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.email.NotificationServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.view.ApacheFopWorker;
import org.ofbiz.webapp.website.WebSiteWorker;
import org.ofbiz.widget.fo.FoScreenRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;
import org.ofbiz.widget.screen.ScreenRenderer;
import org.xml.sax.SAXException;

import com.osafe.util.Util;

public class EmailServices {

    public static final String module = EmailServices.class.getName();

    protected static final HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
    protected static final FoScreenRenderer foScreenRenderer = new FoScreenRenderer();
    
    public static final String err_resource = "OSafeAdminUiLabels";

    @SuppressWarnings("unchecked")
    public static Map abandonCartEmail(DispatchContext dctx, Map context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        // prepare the email information
        Map sendMap = FastMap.newInstance();

        String productStoreId = (String) context.get("productStoreId");
        Integer emailCount = (Integer) context.get("emailCount");
        Integer intervalHours = (Integer) context.get("intervalHours");

        // Check passed params
        if (UtilValidate.isEmpty(intervalHours)) 
        {
        	try {
                intervalHours = UtilMisc.toInteger(Util.getProductStoreParm(productStoreId, "EMAIL_ABANDON_HRS"));
        		
        	}
        	  catch (Exception e)
        	  {
        		  intervalHours=-1;
                  Debug.logError(e, "Problem getting EMAIL_ABANDON_HRS for productStoreId=" + productStoreId);
        		  
        	  }
        }

        if (UtilValidate.isEmpty(emailCount)) 
        {
        	try {
                emailCount = UtilMisc.toInteger(Util.getProductStoreParm(productStoreId, "EMAIL_ABANDON_NUM"));
        		
        	}
        	  catch (Exception e)
        	  {
        		  emailCount=-1;
                  Debug.logError(e, "Problem getting EMAIL_ABANDON_NUM for productStoreId=" + productStoreId);
        		  
        	  }
        }

        Collection<GenericValue> emailList = null;
        String sendTo = null;
        try {

            GenericValue productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
        	
            String emailType = "PRDS_ABD_CART";
            sendMap.put("emailType",emailType);
            sendMap.put("productStoreId",productStoreId);
            
            GenericValue productStoreEmail = null;
            try {
                productStoreEmail = delegator.findByPrimaryKeyCache("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting the ProductStoreEmailSetting for productStoreId=" + productStoreId + " and emailType=" + emailType, module);
            }
            if (UtilValidate.isEmpty(productStoreEmail) || UtilValidate.isEmpty(productStore)) 
            {
                return ServiceUtil.returnFailure("No valid email setting for store with productStoreId=" + productStoreId + " and emailType=" + emailType);
            }

            String subjectString = productStoreEmail.getString("subject");
            Map subjectMap = Util.getProductStoreParmMap(delegator, null,productStoreId);
            subjectString = FlexibleStringExpander.expandString(subjectString, subjectMap);

            //TODO: THIS BLOCK OF CODE SETTING THE SEND MAIL INFORMATION BASED ON THE PRODUCT STORE EMAILS SETTING
            //HAS BEEN MOVED TO 'sendMailFromScrenn'.
            //THIS CODE SHOULD BE REMOVED.
//            sendMap.put("subject", subjectString);
//            sendMap.put("contentType", productStoreEmail.get("contentType"));
//            sendMap.put("sendFrom", productStoreEmail.get("fromAddress"));
//            sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
//            sendMap.put("sendBcc", productStoreEmail.get("bccAddress"));
//
//            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
//            if (UtilValidate.isEmpty(bodyScreenLocation)) {
//                bodyScreenLocation = ProductStoreWorker.getDefaultProductStoreEmailScreenLocation(emailType);
//            }
//            sendMap.put("bodyScreenUri", bodyScreenLocation);
            //TODO:

            
           
            EntityConditionList whereConditions = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId),
                    EntityCondition.makeCondition("shoppingListTypeId", EntityOperator.EQUALS, "SLT_SPEC_PURP"),
                    EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, "")
                    ), EntityOperator.AND);
//            List<GenericValue> shoppingLists = delegator.findByAnd("ShoppingList", UtilMisc.toMap("productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP"));
            List<GenericValue> shoppingLists = delegator.findList("ShoppingList", whereConditions, null, null, null, false);
            Timestamp currentDateAsDate = UtilDateTime.getDayEnd(new Timestamp(System.currentTimeMillis()));
            for (GenericValue shoppingList : shoppingLists) 
            {
                List<GenericValue> shoppingListItems = shoppingList.getRelated("ShoppingListItem");
                if (UtilValidate.isNotEmpty(shoppingListItems)) 
                {
                    GenericValue party = shoppingList.getRelatedOneCache("Party");
                    if (UtilValidate.isNotEmpty(party)) 
                    {
                        String partyId = party.getString("partyId");
                        sendMap.put("partyId", partyId);
                        GenericValue testShoppingListItem = EntityUtil.getFirst((List<GenericValue>) shoppingListItems);
                        Timestamp itemLastUpdatedStamp = testShoppingListItem.getTimestamp("lastUpdatedStamp");
    
                        GregorianCalendar gcStart = new GregorianCalendar();
                        gcStart.setTimeInMillis(itemLastUpdatedStamp.getTime());
                        gcStart.add(Calendar.HOUR, intervalHours);
    
                        GregorianCalendar gcEnd = new GregorianCalendar();
                        gcEnd.setTimeInMillis(currentDateAsDate.getTime());
    
                        // Has this shopping list been abandoned long enough
                        if (gcStart.before(gcEnd)) 
                        {
                            // Check to see if an email was sent previously
                            int communicationEventsCount = 0;
                            List<GenericValue> communicationEvents = delegator.findByAnd("CommunicationEvent", UtilMisc.toMap("partyIdTo", partyId, "reasonEnumId", "ABCART_EMAIL"));
                            ComparableRange itemLastUpdatedToCurrent = new ComparableRange(itemLastUpdatedStamp, currentDateAsDate);
    
                            for (GenericValue event : communicationEvents) 
                            {
                                Timestamp entryDate = event.getTimestamp("entryDate");
                                if (itemLastUpdatedToCurrent.includes(entryDate)) 
                                {
                                    communicationEventsCount++;
                                }
                            }
    
                            // Have we already sent the number of emails allowed
                            if (communicationEventsCount < emailCount) 
                            {
    
                                emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false);
                                if (UtilValidate.isNotEmpty(emailList)) 
                                {
                                    GenericValue email = EntityUtil.getFirst((List<GenericValue>) emailList);
                                    sendTo = email.getString("infoString");
    
                                    Debug.logInfo(sendTo, module);
                                    ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeUiLabels", locale);
    
                                    Map bodyParameters = FastMap.newInstance();
    
                                    GenericValue person = party.getRelatedOneCache("Person");
                                    bodyParameters.put("locale", locale);
                                    bodyParameters.put("person", person);
    
                                    bodyParameters.put("shoppingListId", shoppingList.get("shoppingListId"));
                                    bodyParameters.put("productStoreId", productStoreId);
                                    sendMap.put("bodyParameters", bodyParameters);
                                    sendMap.put("userLogin", userLogin);
    
                                    if ((sendTo != null) && UtilValidate.isEmail(sendTo)) 
                                    {
                                        sendMap.put("sendTo", sendTo);
                                    } else {
                                        String msg = UtilProperties.getMessage("OSafeUiLabels", "ProductStoreAbandonCartEmailError", locale);
                                        Debug.logError(msg, module);
                                        return ServiceUtil.returnError(UtilProperties.getMessage("OSafeUiLabels", "ProductStoreAbandonCartEmailError", locale));
                                    }
    
                                    Debug.logInfo(sendTo, module);
                                    Map communicationEventMap = FastMap.newInstance();
                                    communicationEventMap.put("userLogin", userLogin);
                                    communicationEventMap.put("partyIdTo", partyId);
                                    communicationEventMap.put("communicationEventTypeId", "EMAIL_COMMUNICATION");
                                    communicationEventMap.put("contactMechTypeId", "EMAIL_ADDRESS");
                                    communicationEventMap.put("reasonEnumId", "ABCART_EMAIL");
                                    communicationEventMap.put("subject", subjectString);
                                    Map communicationEventResp = dispatcher.runSync("createCommunicationEventWithoutPermission", communicationEventMap);
    
                                    if (ServiceUtil.isSuccess(communicationEventResp)) 
                                    {
                                        String communicationEventId = (String) communicationEventResp.get("communicationEventId");
                                        sendMap.put("communicationEventId", communicationEventId);
                                        Debug.logInfo(communicationEventId, module);
    
                                        // send the notification
                                        Map sendResp = null;
                                        try {
                                            dispatcher.runAsync("sendMailFromScreen", sendMap);
                                        } catch (Exception e) {
                                            Debug.logError(e, module);
//                                            return ServiceUtil.returnError(UtilProperties.getMessage("OSafeUiLabels", "ProductStoreAbandonCartEmailError", locale));
                                        }
                                    }
                                }
    
                            }
                        }
                    }

                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Shopping Lists", module);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem getting Shopping Lists", module);
        }
        return result;
    }

    public static Map shipReviewEmail(DispatchContext dctx, Map context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");

        OrderReadHelper orderReadHelper = null;
        Integer intervaldays = null;
        String reviewSendEmail = null;
        String emailType = "PRDS_SHIP_REVIEW";
        Map<String, String> productStoreParmMap = Util.getProductStoreParmMap(delegator, null, productStoreId);

        try 
        {
            if (UtilValidate.isNotEmpty(productStoreParmMap)) 
            {
                intervaldays = Integer.parseInt(productStoreParmMap.get("EMAIL_REVIEW_SHP_DYS"));
                reviewSendEmail = productStoreParmMap.get("REVIEW_SEND_EMAIL");
            }
        } catch (NumberFormatException nfe) {
            Debug.logError(nfe, "Problem getting the XProductStoreParm for parmKey=EMAIL_REVIEW_SHP_DYS and emailType=" + emailType, module);
        }
        // Check intervaldays
        if (UtilValidate.isEmpty(intervaldays)) 
        {
            return ServiceUtil.returnFailure("No valid intervaldays to send email with productStoreId=" + productStoreId + " and emailType=" + emailType);
        }

        // Check reviewSendEmail
        if (!Util.isProductStoreParmTrue(reviewSendEmail)) 
        {
            return result;
        }

        Collection<GenericValue> emailList = null;
        String sendTo = null;
        Map sendMap = FastMap.newInstance();
        try {

        	
            GenericValue productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
        	
            sendMap.put("emailType",emailType);
            sendMap.put("productStoreId",productStoreId);
            GenericValue productStoreEmail = null;
            try 
            {
                productStoreEmail = delegator.findByPrimaryKeyCache("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting the ProductStoreEmailSetting for productStoreId=" + productStoreId + " and emailType=" + emailType, module);
            }
            if (UtilValidate.isEmpty(productStoreEmail) || UtilValidate.isEmpty(productStore)) 
            {
                return ServiceUtil.returnFailure("No valid email setting for store with productStoreId=" + productStoreId + " and emailType=" + emailType);
            }

            String subjectString = productStoreEmail.getString("subject");
            Map subjectMap = Util.getProductStoreParmMap(delegator, null,productStoreId);
            subjectString = FlexibleStringExpander.expandString(subjectString, subjectMap);

            //TODO: THIS BLOCK OF CODE SETTING THE SEND MAIL INFORMATION BASED ON THE PRODUCT STORE EMAILS SETTING
            //HAS BEEN MOVED TO 'sendMailFromScrenn'.
            //THIS CODE SHOULD BE REMOVED.
            
//            sendMap.put("subject", subjectString);
//            sendMap.put("contentType", productStoreEmail.get("contentType"));
//            sendMap.put("sendFrom", productStoreEmail.get("fromAddress"));
//            sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
//            sendMap.put("sendBcc", productStoreEmail.get("bccAddress"));
//
//            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
//            if (UtilValidate.isEmpty(bodyScreenLocation)) {
//                bodyScreenLocation = ProductStoreWorker.getDefaultProductStoreEmailScreenLocation(emailType);
//            }
//            sendMap.put("bodyScreenUri", bodyScreenLocation);
            //TODO:
        	

            List<GenericValue> communicationEvents = delegator.findByAnd("CommunicationEventAndOrder", UtilMisc.toMap("communicationEventTypeId", "EMAIL_COMMUNICATION", "reasonEnumId", "SHIPREVIEW_EMAIL"));
            List fieldListFromEntityList = EntityUtil.getFieldListFromEntityList(communicationEvents, "orderId", true);

            EntityConditionList whereConditions = null;
            if (UtilValidate.isEmpty(fieldListFromEntityList)) {
                whereConditions = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId),
                        EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED")
                        ), EntityOperator.AND);
            } else {
                whereConditions = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId),
                        EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"),
                        EntityCondition.makeCondition("orderId", EntityOperator.NOT_IN, fieldListFromEntityList)
                        ), EntityOperator.AND);
            }
            
            List<GenericValue> orderHeaderList = delegator.findList("OrderHeader", whereConditions, null, null, null, false);
            Timestamp currentDateAsDate = UtilDateTime.getDayEnd(new Timestamp(System.currentTimeMillis()));
            for (GenericValue orderHeader : orderHeaderList) 
            {
                Timestamp itemLastUpdatedStamp = orderHeader.getTimestamp("lastUpdatedStamp");
                if(UtilDateTime.getIntervalInDays(itemLastUpdatedStamp, currentDateAsDate) < intervaldays) continue;

                orderReadHelper = new OrderReadHelper(orderHeader);
                GenericValue party = orderReadHelper.getBillToParty();
                String partyId = party.getString("partyId");

                emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false);
                if (UtilValidate.isNotEmpty(emailList)) 
                {
                    GenericValue email = EntityUtil.getFirst((List<GenericValue>) emailList);
                    sendTo = email.getString("infoString");

                    ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeUiLabels", locale);
                    Map bodyParameters = FastMap.newInstance();

                    //GenericValue person = party.getRelatedOne("Person");
                    bodyParameters.put("locale", locale);
                    bodyParameters.put("person", party);

                    String orderId = orderHeader.getString("orderId");
                    bodyParameters.put("orderId", orderId);
                    sendMap.put("bodyParameters", bodyParameters);
                    sendMap.put("userLogin", userLogin);



                    if ((sendTo != null) && UtilValidate.isEmail(sendTo)) 
                    {
                        sendMap.put("sendTo", sendTo);
                    } else 
                    {
                        String msg = UtilProperties.getMessage("OSafeUiLabels", "ProductStoreAbandonCartEmailError", locale);
                        Debug.logError(msg, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage("OSafeUiLabels", "ProductStoreShipReviewEmailError", locale));
                    }

                    Map communicationEventMap = FastMap.newInstance();

                    communicationEventMap.put("userLogin", userLogin);
                    communicationEventMap.put("partyIdTo", partyId);
                    communicationEventMap.put("orderId", orderId);
                    communicationEventMap.put("communicationEventTypeId", "EMAIL_COMMUNICATION");
                    communicationEventMap.put("contactMechTypeId", "EMAIL_ADDRESS");
                    communicationEventMap.put("reasonEnumId", "SHIPREVIEW_EMAIL");
                    communicationEventMap.put("subject", subjectString);

                    Map communicationEventResp = dispatcher.runSync("createCommunicationEventWithoutPermission", communicationEventMap);

                    if (ServiceUtil.isSuccess(communicationEventResp)) 
                    {
                        String communicationEventId = (String) communicationEventResp.get("communicationEventId");
                        sendMap.put("communicationEventId", communicationEventId);

                        // send the notification
                        Map sendResp = null;
                        try {
                            dispatcher.runAsync("sendMailFromScreen", sendMap);
                        } catch (Exception e) {
                            Debug.logError(e, module);
//                            return ServiceUtil.returnError(UtilProperties.getMessage("OSafeUiLabels", "ProductStoreShipReviewEmailError", locale));
                        }
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting order Lists", module);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem getting order Lists", module);
        }
        return result;
    }
    
    /**
     * JavaMail Service that gets body content from a Screen Widget
     * defined in the product store record and if available as attachment also.
     *@param dctx The DispatchContext that this service is operating in
     *@param rServiceContext Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> sendMailFromScreen(DispatchContext dctx, Map<String, ? extends Object> rServiceContext)
    {
        Map<String, Object> serviceContext = UtilMisc.makeMapWritable(rServiceContext);
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String webSiteId = (String) serviceContext.remove("webSiteId");
        String bodyText = (String) serviceContext.remove("bodyText");
        String bodyScreenUri = (String) serviceContext.remove("bodyScreenUri");
        // Attach the file
        String attachmentFileLocation = (String) serviceContext.remove("attachmentFileLocation");
        String attachmentFileMimeType = (String) serviceContext.remove("attachmentFileMimeType");
        String attachmentFileName = (String) serviceContext.remove("attachmentFileName");
        // Attach the dynamic PDF
        String xslfoAttachScreenLocation = (String) serviceContext.remove("xslfoAttachScreenLocation");
        String attachmentName = (String) serviceContext.remove("attachmentName");
        String emailType = (String) serviceContext.remove("emailType");        
        Locale locale = (Locale) serviceContext.get("locale");
        String sendFrom = (String) serviceContext.get("sendFrom");
        String subjectString = (String) serviceContext.get("subject");
        String productStoreIdContext = (String) serviceContext.remove("productStoreId");
        System.out.println("sendfromscreen");
        Map<String, Object> bodyParameters = UtilGenerics.checkMap(serviceContext.remove("bodyParameters"));
        if (bodyParameters == null)
        {
            bodyParameters = MapStack.create();
        }
        if (!bodyParameters.containsKey("locale"))
        {
            bodyParameters.put("locale", locale);
        }
        else
        {
            locale = (Locale) bodyParameters.get("locale");
        }
        String partyId = (String) serviceContext.get("partyId");
        if (partyId == null)
        {
            partyId = (String) bodyParameters.get("partyId");
        }

        String orderId = (String) bodyParameters.get("orderId");
        String productStoreId = (String) bodyParameters.get("productStoreId");
        if (UtilValidate.isEmpty(productStoreId))
        {
            if (UtilValidate.isNotEmpty(webSiteId))
            {
                try
                {
                    GenericValue webSite = delegator.findByPrimaryKeyCache("WebSite", UtilMisc.toMap("webSiteId", webSiteId));
                    productStoreId=webSite.getString("productStoreId");
                }
                catch (GenericEntityException e)
                {
                    Debug.logError(e, "Problem getting WebSite And Store For Email Services", module);
                }
            }
        }
        if (UtilValidate.isEmpty(productStoreId) && UtilValidate.isEmpty(webSiteId))
        {
        	if (UtilValidate.isNotEmpty(orderId))
        	{
                GenericValue orderHeader = null;
                try
                {
                    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                    productStoreId=(String)orderHeader.get("productStoreId");
                    webSiteId=(String)orderHeader.get("webSiteId");
                }
                catch (GenericEntityException e)
                {
                    Debug.logError(e, "Problem getting OrderHeader And Store For Email Services", module);
                }
        	}
        	
        }
        //I give up getting the first productStoreId
        if (UtilValidate.isEmpty(productStoreId) && UtilValidate.isEmpty(webSiteId))
        {
            Debug.logInfo("Problem getting Product Store For Email Services getting first store from DB list", module);
            try {
            	List productStoreList = delegator.findList("ProductStore",null,null,null,null,true);
            	GenericValue productStore = EntityUtil.getFirst(productStoreList);
            	productStoreId = productStore.getString("productStoreId");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting Product Store  For Email Services", module);
            }
        	
        }

        //TODO: getting the from address,ccAddress,bccaddress
        GenericValue productStoreEmail = null;
        if (UtilValidate.isNotEmpty(productStoreId) && UtilValidate.isNotEmpty(emailType))
        {
            try
            {
                productStoreEmail = delegator.findByPrimaryKeyCache("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
            }
            catch (GenericEntityException e)
            {
                Debug.logError(e, "Problem getting the ProductStoreEmailSetting for productStoreId=" + productStoreId + " and emailType=" + emailType, module);
            }
        }
        if (UtilValidate.isEmpty(productStoreEmail) && UtilValidate.isNotEmpty(bodyScreenUri))
        {
            try
            {
            	List<GenericValue> productStoreEmailList = delegator.findByAnd("ProductStoreEmailSetting", UtilMisc.toMap("bodyScreenLocation", bodyScreenUri));
                if (UtilValidate.isNotEmpty(productStoreEmailList))
                {
	            	productStoreEmail = EntityUtil.getFirst(productStoreEmailList);
	            	emailType = productStoreEmail.getString("emailType");
                }
            }
            catch (GenericEntityException e)
            {
                Debug.logError(e, "Problem getting the ProductStoreEmailSetting for bodyScreenLocation=" + bodyScreenUri, module);
            }
            
        }
        if (UtilValidate.isNotEmpty(productStoreEmail)) 
        {
            if (UtilValidate.isEmpty(subjectString)) 
            {
                subjectString = productStoreEmail.getString("subject");
            }
            Map<String, Object> subjectMap = Util.getProductStoreParmMap(delegator, null,productStoreId);
            subjectMap.putAll(bodyParameters);
            subjectString = FlexibleStringExpander.expandString(subjectString, subjectMap);
            serviceContext.put("subject", subjectString);
            serviceContext.put("contentType", productStoreEmail.get("contentType"));
            if (UtilValidate.isEmpty(sendFrom)) 
            {
                serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
            }
        	serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        	if(emailType.equals("PRDS_SCHED_JOB_ALERT"))
            {
            	serviceContext.put("sendCc", "");
            }
        	serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            if (UtilValidate.isEmpty(bodyScreenLocation)) 
            {
                bodyScreenLocation = ProductStoreWorker.getDefaultProductStoreEmailScreenLocation(emailType);
            }
            bodyScreenUri=bodyScreenLocation;
        }

        
        bodyParameters.put("productStoreId", productStoreId);
        bodyParameters.put("communicationEventId", serviceContext.get("communicationEventId"));
        NotificationServices.setBaseUrl(dctx.getDelegator(), webSiteId, bodyParameters);
        String contentType = (String) serviceContext.remove("contentType");

        if (UtilValidate.isEmpty(attachmentName))
        {
            attachmentName = "Details.pdf";
        }
        StringWriter bodyWriter = new StringWriter();

        MapStack<String> screenContext = MapStack.create();
        screenContext.put("locale", locale);
        screenContext.putAll(bodyParameters);
        screenContext.putAll(Util.getProductStoreParmMap(dctx.getDelegator(), webSiteId,productStoreId));
        ScreenRenderer screens = new ScreenRenderer(bodyWriter, screenContext, htmlScreenRenderer);
        screens.populateContextForService(dctx, bodyParameters);

        if (bodyScreenUri != null)
        {
            try
            {
                screens.render(bodyScreenUri);
            }
            catch (GeneralException e)
            {
                String errMsg = "Error rendering screen for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
            catch (IOException e)
            {
                String errMsg = "Error rendering screen for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
            catch (SAXException e)
            {
                String errMsg = "Error rendering screen for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
            catch (ParserConfigurationException e)
            {
                String errMsg = "Error rendering screen for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        }

        boolean isMultiPart = false;

        // check if file attachment location passed in
        if (UtilValidate.isNotEmpty(attachmentFileLocation) && UtilValidate.isNotEmpty(attachmentFileName) && UtilValidate.isNotEmpty(attachmentFileMimeType))
        {
            try
            {
	            // store in the list of maps for sendmail....
	            List<Map<String, ? extends Object>> bodyParts = FastList.newInstance();
	            if (bodyText != null)
	            {
	                bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
	                bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyText, "type", "text/html"));
	            }
	            else
	            {
	                bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyWriter.toString(), "type", "text/html"));
	            }
	
	            File file = new File(attachmentFileLocation, attachmentFileName);
	            if (file.exists())
	            {
	                isMultiPart = true;
	                bodyParts.add(UtilMisc.<String, Object>toMap("content", UtilObject.getBytes(new FileInputStream(file)), "type", attachmentFileMimeType, "filename", attachmentFileName));
	                serviceContext.put("bodyParts", bodyParts);
	            }
            }
            catch (Exception e)
            {
                String errMsg = "Error rendering attachment for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        }
        // check if attachment screen location passed in
        else if (UtilValidate.isNotEmpty(xslfoAttachScreenLocation))
        {
            isMultiPart = true;
            // start processing fo pdf attachment
            try
            {
                Writer writer = new StringWriter();
                MapStack<String> screenContextAtt = MapStack.create();
                // substitute the freemarker variables...
                ScreenRenderer screensAtt = new ScreenRenderer(writer, screenContext, foScreenRenderer);
                screensAtt.populateContextForService(dctx, bodyParameters);
                screenContextAtt.putAll(bodyParameters);
                screensAtt.render(xslfoAttachScreenLocation);

                /*
                try { // save generated fo file for debugging
                    String buf = writer.toString();
                    java.io.FileWriter fw = new java.io.FileWriter(new java.io.File("/tmp/file1.xml"));
                    fw.write(buf.toString());
                    fw.close();
                } catch (IOException e) {
                    Debug.logError(e, "Couldn't save xsl-fo xml debug file: " + e.toString(), module);
                }
                */

                // create the input stream for the generation
                StreamSource src = new StreamSource(new StringReader(writer.toString()));

                // create the output stream for the generation
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                Fop fop = ApacheFopWorker.createFopInstance(baos, MimeConstants.MIME_PDF);
                ApacheFopWorker.transform(src, null, fop);

                // and generate the PDF
                baos.flush();
                baos.close();

                // store in the list of maps for sendmail....
                List<Map<String, ? extends Object>> bodyParts = FastList.newInstance();
                if (bodyText != null)
                {
                    bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
                    bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyText, "type", "text/html"));
                }
                else
                {
                    bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyWriter.toString(), "type", "text/html"));
                }
                bodyParts.add(UtilMisc.<String, Object>toMap("content", baos.toByteArray(), "type", "application/pdf", "filename", attachmentName));
                serviceContext.put("bodyParts", bodyParts);
            }
            catch (GeneralException ge)
            {
                String errMsg = "Error rendering PDF attachment for email: " + ge.toString();
                Debug.logError(ge, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
            catch (IOException ie)
            {
                String errMsg = "Error rendering PDF attachment for email: " + ie.toString();
                Debug.logError(ie, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
            catch (FOPException fe)
            {
                String errMsg = "Error rendering PDF attachment for email: " + fe.toString();
                Debug.logError(fe, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
            catch (SAXException se)
            {
                String errMsg = "Error rendering PDF attachment for email: " + se.toString();
                Debug.logError(se, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
            catch (ParserConfigurationException pe)
            {
                String errMsg = "Error rendering PDF attachment for email: " + pe.toString();
                Debug.logError(pe, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        }
        else
        {
            isMultiPart = false;
            // store body and type for single part message in the context.
            if (bodyText != null)
            {
                bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
                serviceContext.put("body", bodyText);
            }
            else
            {
                serviceContext.put("body", bodyWriter.toString());
            }

            // Only override the default contentType in case of plaintext, since other contentTypes may be multipart
            //    and would require specific handling.
            if (contentType != null && contentType.equalsIgnoreCase("text/plain"))
            {
                serviceContext.put("contentType", "text/plain");
            }
            else
            {
                serviceContext.put("contentType", "text/html");
            }
        }

        // also expand the subject at this point, just in case it has the FlexibleStringExpander syntax in it...
        String subject = (String) serviceContext.remove("subject");
        subject = FlexibleStringExpander.expandString(subject, screenContext, locale);
        Debug.logInfo("Expanded email subject to: " + subject, module);

        serviceContext.put("subject", subject);
        serviceContext.put("partyId", partyId);
        if (UtilValidate.isNotEmpty(orderId))
        {
            serviceContext.put("orderId", orderId);
        }            
        
        if (Debug.verboseOn()) Debug.logVerbose("sendMailFromScreen sendMail context: " + serviceContext, module);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Map<String, Object> resultError;
        Map<String, Object> sendMailResult = null;
        String communicationEventId = (String) serviceContext.get("communicationEventId");
        try {
	            if (UtilValidate.isNotEmpty(productStoreEmail) && UtilValidate.isNotEmpty(productStoreEmail.getString("statusId")) && productStoreEmail.getString("statusId").equalsIgnoreCase("CTNT_PUBLISHED"))
	            {
	                if (isMultiPart)
	                {
	                	System.out.println("muti");
	                	sendMailResult = dispatcher.runSync("sendMailMultiPart", serviceContext);
	                }
	                else
	                {
	                	serviceContext.put("sendFailureNotification", Boolean.FALSE);
	                    sendMailResult = dispatcher.runSync("sendMail", serviceContext);
	                }
	            }
	            else
	            {
		            if (UtilValidate.isEmpty(communicationEventId))
		            {
		            	Map<String, Object> createCommEventFromEmailCtx = filterServiceContext(dctx, "createCommEventFromEmail", serviceContext);
	            	    createCommEventFromEmailCtx.put("userLogin", delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system")));
	                    dispatcher.runSync("createCommEventFromEmail", createCommEventFromEmailCtx);
		            }
	            }
        }
        catch (Exception e)
        {
            String errMsg = "Error send email :" + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        if (ServiceUtil.isError(sendMailResult)) 
        {        	
        	String errMsg = ServiceUtil.getErrorMessage(sendMailResult);
        	resultError = ServiceUtil.returnError(errMsg);
        	resultError.put("emailErrorMessage", errMsg); 
        	resultError.put("subjectString", subjectString);
        	return resultError;
        }
        if (UtilValidate.isNotEmpty(sendMailResult))
        {
            communicationEventId = (String)sendMailResult.get("communicationEventId");
            result.put("messageWrapper", sendMailResult.get("messageWrapper"));
        }

        result.put("body", bodyWriter.toString());
        result.put("subject", subject);
        result.put("communicationEventId", communicationEventId);
        if (UtilValidate.isNotEmpty(emailType)) 
        {
        	result.put("emailType", emailType);
        }
        if (UtilValidate.isNotEmpty(productStoreIdContext)) 
        {
            result.put("productStoreId", productStoreId);
        }    
        if (UtilValidate.isNotEmpty(orderId)) {
            result.put("orderId", orderId);
        }            
        return result;
    }

    /**
     *  Get rid of unnecessary parameters based on the given service name
     * @param dctx Service DispatchContext
     * @param serviceName
     * @param context   context before clean up
     * @return filtered context
     * @throws GenericServiceException
     */
    public static Map filterServiceContext(DispatchContext dctx, String serviceName, Map context) throws GenericServiceException
    {
        ModelService modelService = dctx.getModelService(serviceName);

        Map serviceContext = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(modelService))
        {
            List modelParmInList = modelService.getInModelParamList();
            Iterator modelParmInIter = modelParmInList.iterator();
            while (modelParmInIter.hasNext())
            {
                ModelParam modelParam = (ModelParam) modelParmInIter.next();
                String paramName =  modelParam.name;

                Object value = context.get(paramName);
                if (value != null)
                {
                    serviceContext.put(paramName, value);
                }
            }
        }
        return serviceContext;
    }

}
