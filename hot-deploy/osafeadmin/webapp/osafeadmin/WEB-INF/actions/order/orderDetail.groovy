package order;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilNumber;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);
context.orderId = orderId;

orderHeader = null;
orderItems = null;
orderNotes = null;
partyId = null;
trackingURL ="";
orderReadHelper = null;
shippingApplies = true;

if (UtilValidate.isNotEmpty(orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	if (UtilValidate.isNotEmpty(orderHeader)) 
	{
		orderProductStore = orderHeader.getRelatedOne("ProductStore");
		if (UtilValidate.isNotEmpty(orderProductStore.storeName))
		{
			productStoreName = orderProductStore.storeName;
		}
		else
		{
			productStoreName = orderHeader.productStoreId;
		}
		context.productStoreName = productStoreName;
		
	    // note these are overridden in the OrderViewWebSecure.groovy script if run
	    context.hasPermission = true;
	    context.canViewInternalDetails = true;
	
	    orderReadHelper = new OrderReadHelper(orderHeader);
	    orderItems = orderReadHelper.getOrderItems();
		
		orderAdjustments = orderReadHelper.getAdjustments();
		
		
		//shipping applies check
		shippingApplies = orderReadHelper.shippingApplies();
		context.shippingApplies = shippingApplies;
	
	    // get the order type
	    orderType = orderHeader.orderTypeId;
	    context.orderType = orderType;
	
	    // get the display party
	    displayParty = null;
	    if ("PURCHASE_ORDER".equals(orderType)) 
	    {
	        displayParty = orderReadHelper.getSupplierAgent();
	    } 
	    else 
	    {
	        displayParty = orderReadHelper.getPlacingParty();
	    }
	    if (UtilValidate.isNotEmpty(displayParty)) 
	    {
	        partyId = displayParty.partyId;
	        context.displayParty = displayParty;
	        context.partyId = partyId;
	
	        
	        //Get PRIMARY EMAIL, TELEPHONE LOCATIONS
	        partyContactMechPurpose = displayParty.getRelated("PartyContactMechPurpose");
	        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
	
	        partyPurposeEmails = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL"));
	        partyPurposeEmails = EntityUtil.getRelated("PartyContactMech", partyPurposeEmails);
	        partyPurposeEmails = EntityUtil.filterByDate(partyPurposeEmails,true);
	        partyPurposeEmails = EntityUtil.orderBy(partyPurposeEmails, UtilMisc.toList("fromDate DESC"));
	        if (UtilValidate.isNotEmpty(partyPurposeEmails)) 
	        {
	            partyPurposeEmail = EntityUtil.getFirst(partyPurposeEmails);
	            contactMech = partyPurposeEmail.getRelatedOne("ContactMech");
	            context.userEmailContactMech = contactMech;
	            context.userEmailAddress = contactMech.infoString;
	            context.userEmailAllowSolicitation= partyPurposeEmail.allowSolicitation;
	            userEmailContactMechList= EntityUtil.getRelated("ContactMech",partyPurposeEmails);
	            context.userEmailContactMechList = userEmailContactMechList;
	            
	        }
	
	        partyPurposeHomePhones = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_HOME"));
	        partyPurposeHomePhones = EntityUtil.getRelated("PartyContactMech", partyPurposeHomePhones);
	        partyPurposeHomePhones = EntityUtil.filterByDate(partyPurposeHomePhones,true);
	        partyPurposeHomePhones = EntityUtil.orderBy(partyPurposeHomePhones, UtilMisc.toList("fromDate DESC"));
	        if (UtilValidate.isNotEmpty(partyPurposeHomePhones)) 
	        {
	            partyPurposePhone = EntityUtil.getFirst(partyPurposeHomePhones);
	            telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber");
	            context.phoneHomeTelecomNumber =telecomNumber;
	            context.phoneHomeAreaCode =telecomNumber.areaCode;
	            context.phoneHomeContactNumber =telecomNumber.contactNumber;
	            context.partyPurposeHomePhones =partyPurposeHomePhones;
	        }
	        
	        partyPurposeWorkPhones = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_WORK"));
	        partyPurposeWorkPhones = EntityUtil.getRelated("PartyContactMech", partyPurposeWorkPhones);
	        partyPurposeWorkPhones = EntityUtil.filterByDate(partyPurposeWorkPhones,true);
	        partyPurposeWorkPhones = EntityUtil.orderBy(partyPurposeWorkPhones, UtilMisc.toList("fromDate DESC"));
	        if (UtilValidate.isNotEmpty(partyPurposeWorkPhones)) 
	        {
	            partyPurposePhone = EntityUtil.getFirst(partyPurposeWorkPhones);
	            telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber");
		        context.partyPurposeWorkPhone =partyPurposePhone;
	            context.phoneWorkTelecomNumber =telecomNumber;
	            context.phoneWorkAreaCode =telecomNumber.areaCode;
	            context.phoneWorkContactNumber =telecomNumber.contactNumber;
	            context.partyPurposeWorkPhones =partyPurposeWorkPhones;
	        }
	
	        partyPurposeMobilePhones = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_MOBILE"));
	        partyPurposeMobilePhones = EntityUtil.getRelated("PartyContactMech", partyPurposeMobilePhones);
	        partyPurposeMobilePhones = EntityUtil.filterByDate(partyPurposeMobilePhones,true);
	        partyPurposeMobilePhones = EntityUtil.orderBy(partyPurposeMobilePhones, UtilMisc.toList("fromDate DESC"));
	        if (UtilValidate.isNotEmpty(partyPurposeMobilePhones)) 
	        {
	            partyPurposePhone = EntityUtil.getFirst(partyPurposeMobilePhones);
	            telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber");
	            context.phoneMobileTelecomNumber =telecomNumber;
	            context.phoneMobileAreaCode =telecomNumber.areaCode;
	            context.phoneMobileContactNumber =telecomNumber.contactNumber;
	            context.partyPurposeMobilePhones =partyPurposeMobilePhones;
	        }
	        
	    }
	
	    canceledPromoOrderItem = [:];
	    orderItemList = orderReadHelper.getOrderItems();
	    orderItemList.each { orderItem ->
	        if("Y".equals(orderItem.get("isPromo")) && "ITEM_CANCELLED".equals(orderItem.get("statusId"))) 
	        {
	            canceledPromoOrderItem = orderItem;
	        }
	        orderItemList.remove(canceledPromoOrderItem);
	    }
	    context.orderItemList = orderItemList;
	
	    shippingAddress = orderReadHelper.getShippingAddress();
	    context.shippingAddress = shippingAddress;
	
	    billingAddress = orderReadHelper.getBillingAddress();
	    context.billingAddress = billingAddress;
	
	    distributorId = orderReadHelper.getDistributorId();
	    context.distributorId = distributorId;
	
	    affiliateId = orderReadHelper.getAffiliateId();
	    context.affiliateId = affiliateId;
	
	    billingAccount = orderHeader.getRelatedOne("BillingAccount");
	    context.billingAccount = billingAccount;
	    context.billingAccountMaxAmount = orderReadHelper.getBillingAccountMaxAmount();
	
	    ecl = EntityCondition.makeCondition([
	                                    EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
	                                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")],
	                                EntityOperator.AND);
	    orderPaymentPreferences = delegator.findList("OrderPaymentPreference", ecl, null, null, null, false);
		orderPaymentPreferences = EntityUtil.orderBy(orderPaymentPreferences, UtilMisc.toList("createdDate ASC"));
	    context.orderPaymentPreferences = orderPaymentPreferences;
	
	    // ship groups
	    shipGroups = orderHeader.getRelatedOrderBy("OrderItemShipGroup", ["-shipGroupSeqId"]);
	    context.shipGroups = shipGroups;
	    shipGroupsSize = shipGroups.size();
	    context.shipGroupsSize = shipGroupsSize;
	    shipGroup = EntityUtil.getFirst(shipGroups);
		carrierPartyId = "";
		trackingNumber = "";
		if(UtilValidate.isNotEmpty(shipGroup))
	    {
			carrierPartyId = shipGroup.carrierPartyId;
			trackingNumber = shipGroup.trackingNumber;
	    }
	    customerPoNumber = null;
	    orderItemList.each { orderItem ->
	        customerPoNumber = orderItem.correspondingPoId;
	    }
	    context.customerPoNumber = customerPoNumber;
	
	    statusChange = delegator.findByAnd("StatusValidChange", [statusId : orderHeader.statusId]);
	    context.statusChange = statusChange;
	
	    currentStatus = orderHeader.getRelatedOne("StatusItem");
	    context.currentStatus = currentStatus;
	
	    orderHeaderStatuses = orderReadHelper.getOrderHeaderStatuses();
	    context.orderHeaderStatuses = orderHeaderStatuses;
	
	    adjustmentTypes = delegator.findList("OrderAdjustmentType", null, null, ["description"], null, false);
	    context.orderAdjustmentTypes = adjustmentTypes;
	
	    notes = orderHeader.getRelatedOrderBy("OrderHeaderNoteView", ["-noteDateTime"]);
	    context.orderNotes = notes;
	    orderNotes = notes;
	    
	    if(UtilValidate.isNotEmpty(context.showOrderNotesPaging) && context.showOrderNotesPaging == "true")
	    {
	        pagingListSize=orderNotes.size();
	        context.pagingListSize=pagingListSize;
	        context.pagingList = orderNotes;
	    }
	    
	    showNoteHeadingOnPDF = false;
	    if (UtilValidate.isNotEmpty(notes) && EntityUtil.filterByCondition(notes, EntityCondition.makeCondition("internalNote", EntityOperator.EQUALS, "N")).size() > 0) 
	    {
	        showNoteHeadingOnPDF = true;
	    }
	    context.showNoteHeadingOnPDF = showNoteHeadingOnPDF;
	
	    cmvm = ContactMechWorker.getOrderContactMechValueMaps(delegator, orderId);
	    context.orderContactMechValueMaps = cmvm;
	
	    orderItemChangeReasons = delegator.findByAnd("Enumeration", [enumTypeId : "ODR_ITM_CH_REASON"], ["sequenceId"]);
	    context.orderItemChangeReasons = orderItemChangeReasons;
		
		// Fetching the carrier tracking URL
		if(UtilValidate.isNotEmpty(shipGroupsSize) && shipGroupsSize == 1)
		{
		    trackingURLPartyContents = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId",carrierPartyId,"partyContentTypeId", "TRACKING_URL"));
		    if(UtilValidate.isNotEmpty(trackingURLPartyContents))
		    {
		        trackingURLPartyContent = EntityUtil.getFirst(trackingURLPartyContents);
		        if(UtilValidate.isNotEmpty(trackingURLPartyContent))
		        {
		            content = trackingURLPartyContent.getRelatedOne("Content");
		            if(UtilValidate.isNotEmpty(content))
		            {
		                dataResource = content.getRelatedOne("DataResource");
		                if(UtilValidate.isNotEmpty(dataResource))
		                {
		                    electronicText = dataResource.getRelatedOne("ElectronicText");
		                    trackingURL = electronicText.textData;
		                    if(UtilValidate.isNotEmpty(trackingURL))
		                    {
		                        trackingURL = FlexibleStringExpander.expandString(trackingURL,  UtilMisc.toMap("TRACKING_NUMBER":trackingNumber))
		                    }
		                }
		            }
		        }
		    }
		}
		
		if(security.hasEntityPermission('SPER_ORDER_MGMT', '_VIEW', session))
		{
			messageMap=[:];
			messageMap.put("orderId", orderId);
		
			context.orderId=orderId;
			context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","OrderManagementOrderDetailTitle",messageMap, locale )
			context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderDetailInfoHeading",messageMap, locale )
			if(UtilValidate.isNotEmpty(context.showOrderNoteHeading) && context.showOrderNoteHeading == "true" )
			{
				context.orderNoteInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderNoteHeading",messageMap, locale )
			}
			if(UtilValidate.isNotEmpty(context.showOrderAttributeHeading) && context.showOrderAttributeHeading == "true" )
			{
				context.orderAttributeInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderAttributeHeading",messageMap, locale )
			}
			
			context.notesCount = orderNotes.size();
			
			if(UtilValidate.isNotEmpty(context.showOrderItemsPaging) && context.showOrderItemsPaging == "true")
			{
				pagingListSize=orderItems.size();
				context.pagingListSize=pagingListSize;
				context.pagingList = orderItems;
			}
		
			storeId = "";
			orderDeliveryOptionAttr = orderHeader.getRelatedByAnd("OrderAttribute", [attrName : "DELIVERY_OPTION"]);
			orderDeliveryOptionAttr = EntityUtil.getFirst(orderDeliveryOptionAttr);
			
			if (UtilValidate.isNotEmpty(orderDeliveryOptionAttr) && orderDeliveryOptionAttr.attrValue == "STORE_PICKUP")
			{
				context.isStorePickup = "Y";
				orderStoreLocationAttr = orderHeader.getRelatedByAnd("OrderAttribute", [attrName : "STORE_LOCATION"]);
				orderStoreLocationAttr = EntityUtil.getFirst(orderStoreLocationAttr);
				if (UtilValidate.isNotEmpty(orderStoreLocationAttr))
				{
					storeId = orderStoreLocationAttr.attrValue;
				}
			}
		
			if (UtilValidate.isNotEmpty(storeId))
			{
				context.storeId = storeId;
				store = delegator.findOne("Party", [partyId : storeId], false);
				context.store = store;
				if (UtilValidate.isNotEmpty(store))
				{
					storeInfo = store.getRelatedOne("PartyGroup");
					if (UtilValidate.isNotEmpty(storeInfo))
					{
						context.storeInfo = storeInfo;
					}
				}
				partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, storeId, false);
				if (UtilValidate.isNotEmpty(partyContactMechValueMaps))
				{
					partyContactMechValueMaps.each { partyContactMechValueMap ->
						contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
						contactMechPurposes.each { contactMechPurpose ->
							if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION"))
							{
								context.storeContactMechValueMap = partyContactMechValueMap;
							}
						}
					}
				}
			}
		}
		
		//display success message for checkout
		String showThankYouStatus = request.getAttribute("showThankYouStatus");
		if (UtilValidate.isEmpty(showThankYouStatus))
		{
			context.showThankYouStatus ="N"
		}
		if("Y".equals (showThankYouStatus))
		{
			messageMap=[:];
			messageMap.put("orderId", orderId);
			checkoutSuccessMessageList = UtilMisc.toList(UtilProperties.getMessage("OSafeAdminUiLabels","OrderCheckoutSuccess",messageMap, locale ));
			context.checkoutSuccessMessageList = checkoutSuccessMessageList;
		}
	}
}

context.orderHeader = orderHeader;
context.orderReadHelper = orderReadHelper;
context.orderItems = orderItems;
context.trackingURL = trackingURL;
