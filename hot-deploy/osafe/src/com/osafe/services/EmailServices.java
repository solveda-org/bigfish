package com.osafe.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
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
        if (UtilValidate.isEmpty(intervalHours)) {
            intervalHours = UtilMisc.toInteger(UtilProperties.getPropertyValue("osafe.properties", "abandon-cart-email.interval-hours"));
        }

        if (UtilValidate.isEmpty(emailCount)) {
            emailCount = UtilMisc.toInteger(UtilProperties.getPropertyValue("osafe.properties", "abandon-cart-email.email-count"));
        }

        Collection<GenericValue> emailList = null;
        String sendTo = null;
        try {

            String emailType = "PRDS_ABD_CART";
            GenericValue productStoreEmail = null;
            try {
                productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting the ProductStoreEmailSetting for productStoreId=" + productStoreId + " and emailType=" + emailType, module);
            }
            if (productStoreEmail == null) {
                return ServiceUtil.returnFailure("No valid email setting for store with productStoreId=" + productStoreId + " and emailType=" + emailType);
            }

            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            if (UtilValidate.isEmpty(bodyScreenLocation)) {
                bodyScreenLocation = ProductStoreWorker.getDefaultProductStoreEmailScreenLocation(emailType);
            }
            sendMap.put("bodyScreenUri", bodyScreenLocation);

            List<GenericValue> shoppingLists = delegator.findByAnd("ShoppingList", UtilMisc.toMap("productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP"));
            Timestamp currentDateAsDate = UtilDateTime.getDayEnd(new Timestamp(System.currentTimeMillis()));
            for (GenericValue shoppingList : shoppingLists) {
                List<GenericValue> shoppingListItems = shoppingList.getRelated("ShoppingListItem");
                if (UtilValidate.isNotEmpty(shoppingListItems)) {
                    GenericValue party = shoppingList.getRelatedOne("Party");
                    String partyId = party.getString("partyId");

                    GenericValue testShoppingListItem = EntityUtil.getFirst((List<GenericValue>) shoppingListItems);
                    Timestamp itemLastUpdatedStamp = testShoppingListItem.getTimestamp("lastUpdatedStamp");

                    GregorianCalendar gcStart = new GregorianCalendar();
                    gcStart.setTimeInMillis(itemLastUpdatedStamp.getTime());
                    gcStart.add(Calendar.HOUR, intervalHours);

                    GregorianCalendar gcEnd = new GregorianCalendar();
                    gcEnd.setTimeInMillis(currentDateAsDate.getTime());

                    // Has this shopping list been abandoned long enough
                    if (gcStart.before(gcEnd)) {

                        // Check to see if an email was sent previously
                        int communicationEventsCount = 0;
                        List<GenericValue> communicationEvents = delegator.findByAnd("CommunicationEvent", UtilMisc.toMap("partyIdTo", partyId, "reasonEnumId", "ABCART_EMAIL"));
                        ComparableRange itemLastUpdatedToCurrent = new ComparableRange(itemLastUpdatedStamp, currentDateAsDate);

                        for (GenericValue event : communicationEvents) {
                            Timestamp entryDate = event.getTimestamp("entryDate");
                            if (itemLastUpdatedToCurrent.includes(entryDate)) {
                                communicationEventsCount++;
                            }
                        }

                        // Have we already sent the number of emails allowed
                        if (communicationEventsCount < emailCount) {

                            emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false);
                            if (UtilValidate.isNotEmpty(emailList)) {
                                GenericValue email = EntityUtil.getFirst((List<GenericValue>) emailList);
                                sendTo = email.getString("infoString");

                                Debug.logInfo(sendTo, module);
                                ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeUiLabels", locale);

                                Map bodyParameters = UtilMisc.toMap("uiLabelMap", uiLabelMap, "locale", locale);

                                GenericValue person = party.getRelatedOne("Person");
                                bodyParameters.put("person", person);

                                GenericValue productStore = productStoreEmail.getRelatedOne("ProductStore");
                                String storeName = productStore.getString("storeName");

                                // Store Phone Number
                                GenericValue payToParty = productStore.getRelatedOne("Party");
                                Collection<GenericValue> productStorePhoneList = ContactHelper.getContactMech(payToParty, "PRIMARY_PHONE", "TELECOM_NUMBER", false);

                                if (UtilValidate.isNotEmpty(productStorePhoneList)) {
                                    GenericValue productStorePhone = EntityUtil.getFirst((List<GenericValue>) productStorePhoneList);
                                    GenericValue productStoreTelecomNumber = productStorePhone.getRelatedOne("TelecomNumber");
                                    String customerServiceTelephone = Util.formatTelephone(productStoreTelecomNumber.getString("areaCode"), productStoreTelecomNumber.getString("contactNumber"));
                                    bodyParameters.put("customerServiceTelephone", customerServiceTelephone);
                                }

                                // Add debugging information to the email if we are redirecting to test addresses
                                String mailNnotificationsRedirectTo = UtilProperties.getPropertyValue("general.properties", "mail.notifications.redirectTo");
                                if (UtilValidate.isNotEmpty(mailNnotificationsRedirectTo)) {

                                    String abandonEmailDebugInfo = "";
                                    String shoppingListId = shoppingList.getString("shoppingListId");

                                    abandonEmailDebugInfo += "<p>" + "shoppingListId:" + shoppingListId + "</p>";
                                    abandonEmailDebugInfo += "<p>" + "itemLastUpdatedStamp:" + UtilDateTime.toDateTimeString(itemLastUpdatedStamp) + "</p>";
                                    abandonEmailDebugInfo += "<p>" + "currentDateAsDate:" + UtilDateTime.toDateTimeString(currentDateAsDate) + "</p>";
                                    abandonEmailDebugInfo += "<p>" + "communicationEventsCount:" + communicationEventsCount + "</p>";
                                    abandonEmailDebugInfo += "<p>" + "intervalHours:" + intervalHours + "</p>";
                                    abandonEmailDebugInfo += "<p>" + "emailCount:" + emailCount + "</p>";
                                    abandonEmailDebugInfo += "<p>" + "productStoreId:" + productStoreId + "</p>";
                                    bodyParameters.put("abandonEmailDebugInfo", abandonEmailDebugInfo);
                                }

                                sendMap.put("bodyParameters", bodyParameters);
                                sendMap.put("userLogin", userLogin);

                                String subjectString = productStoreEmail.getString("subject");
                                Map subjectMap = Util.getProductStoreParmMap(delegator, null,productStoreId);
                                subjectString = FlexibleStringExpander.expandString(subjectString, subjectMap);
                                sendMap.put("subject", subjectString);

                                sendMap.put("contentType", productStoreEmail.get("contentType"));
                                sendMap.put("sendFrom", productStoreEmail.get("fromAddress"));
                                sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
                                sendMap.put("sendBcc", productStoreEmail.get("bccAddress"));

                                if ((sendTo != null) && UtilValidate.isEmail(sendTo)) {
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

                                if (ServiceUtil.isSuccess(communicationEventResp)) {
                                    String communicationEventId = (String) communicationEventResp.get("communicationEventId");
                                    sendMap.put("communicationEventId", communicationEventId);
                                    Debug.logInfo(communicationEventId, module);

                                    // send the notification
                                    Map sendResp = null;
                                    try {
                                        sendResp = dispatcher.runSync("sendMailFromScreen", sendMap);
                                    } catch (Exception e) {
                                        Debug.logError(e, module);
                                        return ServiceUtil.returnError(UtilProperties.getMessage("OSafeUiLabels", "ProductStoreAbandonCartEmailError", locale));
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

        try {
            if (UtilValidate.isNotEmpty(productStoreParmMap)) {
                intervaldays = Integer.parseInt(productStoreParmMap.get("EMAIL_REVIEW_SHP_DYS"));
                reviewSendEmail = productStoreParmMap.get("REVIEW_SEND_EMAIL");
            }
        } catch (NumberFormatException nfe) {
            Debug.logError(nfe, "Problem getting the XProductStoreParm for parmKey=EMAIL_REVIEW_SHP_DYS and emailType=" + emailType, module);
        }
        // Check intervaldays
        if (UtilValidate.isEmpty(intervaldays)) {
            return ServiceUtil.returnFailure("No valid intervaldays to send email with productStoreId=" + productStoreId + " and emailType=" + emailType);
        }

        // Check reviewSendEmail
        if (!Util.isProductStoreParmTrue(reviewSendEmail)) {
            return result;
        }

        Collection<GenericValue> emailList = null;
        String sendTo = null;
        try {
            GenericValue productStoreEmail = null;
            try {
                productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting the ProductStoreEmailSetting for productStoreId=" + productStoreId + " and emailType=" + emailType, module);
            }
            if (productStoreEmail == null) {
                return ServiceUtil.returnFailure("No valid email setting for store with productStoreId=" + productStoreId + " and emailType=" + emailType);
            }

            // prepare the email information
            Map sendMap = FastMap.newInstance();

            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            if (UtilValidate.isEmpty(bodyScreenLocation)) {
                bodyScreenLocation = ProductStoreWorker.getDefaultProductStoreEmailScreenLocation(emailType);
            }
            sendMap.put("bodyScreenUri", bodyScreenLocation);

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
            for (GenericValue orderHeader : orderHeaderList) {
                Timestamp itemLastUpdatedStamp = orderHeader.getTimestamp("lastUpdatedStamp");
                if(UtilDateTime.getIntervalInDays(itemLastUpdatedStamp, currentDateAsDate) < intervaldays) continue;

                orderReadHelper = new OrderReadHelper(orderHeader);
                GenericValue party = orderReadHelper.getBillToParty();
                String partyId = party.getString("partyId");

                emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false);
                if (UtilValidate.isNotEmpty(emailList)) {
                    GenericValue email = EntityUtil.getFirst((List<GenericValue>) emailList);
                    sendTo = email.getString("infoString");

                    ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeUiLabels", locale);

                    Map bodyParameters = UtilMisc.toMap("uiLabelMap", uiLabelMap, "locale", locale);

                    //GenericValue person = party.getRelatedOne("Person");
                    bodyParameters.put("person", party);

                    GenericValue productStore = productStoreEmail.getRelatedOne("ProductStore");
                    String storeName = productStore.getString("storeName");

                        // Store Phone Number
                    GenericValue payToParty = productStore.getRelatedOne("Party");
                    Collection<GenericValue> productStorePhoneList = ContactHelper.getContactMech(payToParty, "PRIMARY_PHONE", "TELECOM_NUMBER", false);

                    if (UtilValidate.isNotEmpty(productStorePhoneList)) {
                        GenericValue productStorePhone = EntityUtil.getFirst((List<GenericValue>) productStorePhoneList);
                        GenericValue productStoreTelecomNumber = productStorePhone.getRelatedOne("TelecomNumber");
                        String customerServiceTelephone = Util.formatTelephone(productStoreTelecomNumber.getString("areaCode"), productStoreTelecomNumber.getString("contactNumber"));
                        bodyParameters.put("customerServiceTelephone", customerServiceTelephone);
                    }

                    // Add debugging information to the email if we are redirecting to test addresses
                    String mailNnotificationsRedirectTo = UtilProperties.getPropertyValue("general.properties", "mail.notifications.redirectTo");
                    String orderId = orderHeader.getString("orderId");
                    bodyParameters.put("orderId", orderId);
                    if (UtilValidate.isNotEmpty(mailNnotificationsRedirectTo)) {
                        String shipReviewEmailDebugInfo = "";

                        shipReviewEmailDebugInfo += "<p>" + "orderListId:" + orderId + "</p>";
                        shipReviewEmailDebugInfo += "<p>" + "itemLastUpdatedStamp:" + UtilDateTime.toDateTimeString(itemLastUpdatedStamp) + "</p>";
                        shipReviewEmailDebugInfo += "<p>" + "currentDateAsDate:" + UtilDateTime.toDateTimeString(currentDateAsDate) + "</p>";
                        shipReviewEmailDebugInfo += "<p>" + "intervaldays:" + intervaldays + "</p>";
                        shipReviewEmailDebugInfo += "<p>" + "productStoreId:" + productStoreId + "</p>";
                        bodyParameters.put("shipReviewEmailDebugInfo", shipReviewEmailDebugInfo);
                    }

                    sendMap.put("bodyParameters", bodyParameters);
                    sendMap.put("userLogin", userLogin);

                    String subjectString = productStoreEmail.getString("subject");
                    subjectString = FlexibleStringExpander.expandString(subjectString, productStoreParmMap);
                    sendMap.put("subject", subjectString);

                    sendMap.put("contentType", productStoreEmail.get("contentType"));
                    sendMap.put("sendFrom", productStoreEmail.get("fromAddress"));
                    sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
                    sendMap.put("sendBcc", productStoreEmail.get("bccAddress"));

                    if ((sendTo != null) && UtilValidate.isEmail(sendTo)) {
                        sendMap.put("sendTo", sendTo);
                    } else {
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

                    if (ServiceUtil.isSuccess(communicationEventResp)) {
                        String communicationEventId = (String) communicationEventResp.get("communicationEventId");
                        sendMap.put("communicationEventId", communicationEventId);

                        // send the notification
                        Map sendResp = null;
                        try {
                            sendResp = dispatcher.runSync("sendMailFromScreen", sendMap);
                        } catch (Exception e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage("OSafeUiLabels", "ProductStoreShipReviewEmailError", locale));
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
    public static Map<String, Object> sendMailFromScreen(DispatchContext dctx, Map<String, ? extends Object> rServiceContext) {
        Map<String, Object> serviceContext = UtilMisc.makeMapWritable(rServiceContext);
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String webSiteId = (String) serviceContext.remove("webSiteId");
        String bodyText = (String) serviceContext.remove("bodyText");
        String bodyScreenUri = (String) serviceContext.remove("bodyScreenUri");
        String xslfoAttachScreenLocation = (String) serviceContext.remove("xslfoAttachScreenLocation");
        String attachmentName = (String) serviceContext.remove("attachmentName");
        Locale locale = (Locale) serviceContext.get("locale");
        System.out.println("sendfromscreen");
        Map<String, Object> bodyParameters = UtilGenerics.checkMap(serviceContext.remove("bodyParameters"));
        if (bodyParameters == null) {
            bodyParameters = MapStack.create();
        }
        if (!bodyParameters.containsKey("locale")) {
            bodyParameters.put("locale", locale);
        } else {
            locale = (Locale) bodyParameters.get("locale");
        }
        String partyId = (String) serviceContext.get("partyId");
        if (partyId == null) {
            partyId = (String) bodyParameters.get("partyId");
        }
        String orderId = (String) bodyParameters.get("orderId");
        String productStoreId = (String) bodyParameters.get("productStoreId");
        if (UtilValidate.isEmpty(productStoreId) && UtilValidate.isEmpty(webSiteId))
        {
        	if (UtilValidate.isNotEmpty(orderId))
        	{
                GenericValue orderHeader = null;
                try {
                    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                    productStoreId=(String)orderHeader.get("productStoreId");
                    webSiteId=(String)orderHeader.get("webSiteId");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem getting OrderHeader And Store For Email Services", module);
                }
        	}
        	
        }
        //I give up getting the first productStoreId
        if (UtilValidate.isEmpty(productStoreId) && UtilValidate.isEmpty(webSiteId))
        {
            try {
            	List productStoreList = delegator.findList("ProductStore",null,null,null,null,true);
            	GenericValue productStore = EntityUtil.getFirst(productStoreList);
            	productStoreId = productStore.getString("productStoreId");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting Product Store  For Email Services", module);
            }
        	
        }
        
        bodyParameters.put("communicationEventId", serviceContext.get("communicationEventId"));
        NotificationServices.setBaseUrl(dctx.getDelegator(), webSiteId, bodyParameters);
        String contentType = (String) serviceContext.remove("contentType");

        if (UtilValidate.isEmpty(attachmentName)) {
            attachmentName = "Details.pdf";
        }
        StringWriter bodyWriter = new StringWriter();

        MapStack<String> screenContext = MapStack.create();
        screenContext.put("locale", locale);
        screenContext.putAll(bodyParameters);
        screenContext.putAll(Util.getProductStoreParmMap(dctx.getDelegator(), webSiteId,productStoreId));
        ScreenRenderer screens = new ScreenRenderer(bodyWriter, screenContext, htmlScreenRenderer);
        screens.populateContextForService(dctx, bodyParameters);

        if (bodyScreenUri != null) {
            try {
                screens.render(bodyScreenUri);
            } catch (GeneralException e) {
                String errMsg = "Error rendering screen for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            } catch (IOException e) {
                String errMsg = "Error rendering screen for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            } catch (SAXException e) {
                String errMsg = "Error rendering screen for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            } catch (ParserConfigurationException e) {
                String errMsg = "Error rendering screen for email: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        }

        boolean isMultiPart = false;

        // check if attachment screen location passed in
        if (UtilValidate.isNotEmpty(xslfoAttachScreenLocation)) {
            isMultiPart = true;
            // start processing fo pdf attachment
            try {
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
                if (bodyText != null) {
                    bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
                    bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyText, "type", "text/html"));
                } else {
                    bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyWriter.toString(), "type", "text/html"));
                }
                bodyParts.add(UtilMisc.<String, Object>toMap("content", baos.toByteArray(), "type", "application/pdf", "filename", attachmentName));
                serviceContext.put("bodyParts", bodyParts);
            } catch (GeneralException ge) {
                String errMsg = "Error rendering PDF attachment for email: " + ge.toString();
                Debug.logError(ge, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            } catch (IOException ie) {
                String errMsg = "Error rendering PDF attachment for email: " + ie.toString();
                Debug.logError(ie, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            } catch (FOPException fe) {
                String errMsg = "Error rendering PDF attachment for email: " + fe.toString();
                Debug.logError(fe, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            } catch (SAXException se) {
                String errMsg = "Error rendering PDF attachment for email: " + se.toString();
                Debug.logError(se, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            } catch (ParserConfigurationException pe) {
                String errMsg = "Error rendering PDF attachment for email: " + pe.toString();
                Debug.logError(pe, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        } else {
            isMultiPart = false;
            // store body and type for single part message in the context.
            if (bodyText != null) {
                bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
                serviceContext.put("body", bodyText);
            } else {
                serviceContext.put("body", bodyWriter.toString());
            }

            // Only override the default contentType in case of plaintext, since other contentTypes may be multipart
            //    and would require specific handling.
            if (contentType != null && contentType.equalsIgnoreCase("text/plain")) {
                serviceContext.put("contentType", "text/plain");
            } else {
                serviceContext.put("contentType", "text/html");
            }
        }

        // also expand the subject at this point, just in case it has the FlexibleStringExpander syntax in it...
        String subject = (String) serviceContext.remove("subject");
        subject = FlexibleStringExpander.expandString(subject, screenContext, locale);
        Debug.logInfo("Expanded email subject to: " + subject, module);
        serviceContext.put("subject", subject);
        serviceContext.put("partyId", partyId);
        if (UtilValidate.isNotEmpty(orderId)) {
            serviceContext.put("orderId", orderId);
        }            
        
        if (Debug.verboseOn()) Debug.logVerbose("sendMailFromScreen sendMail context: " + serviceContext, module);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Map<String, Object> sendMailResult;
        try {
            if (isMultiPart) {
            	System.out.println("muti");
            	sendMailResult = dispatcher.runSync("sendMailMultiPart", serviceContext);
            } else {
                sendMailResult = dispatcher.runSync("sendMail", serviceContext);
            }
        } catch (Exception e) {
            String errMsg = "Error send email :" + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        if (ServiceUtil.isError(sendMailResult)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(sendMailResult));
        }

        result.put("messageWrapper", sendMailResult.get("messageWrapper"));
        result.put("body", bodyWriter.toString());
        result.put("subject", subject);
        result.put("communicationEventId", sendMailResult.get("communicationEventId"));
        if (UtilValidate.isNotEmpty(orderId)) {
            result.put("orderId", orderId);
        }            
        return result;
    }

}
