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
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.GenericValue;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);
context.orderId = orderId;

orderHeader = null;
orderItems = null;
orderNotes = null;
partyId = null;

if (UtilValidate.isNotEmpty(orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	
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
	
	messageMap=[:];
	messageMap.put("orderId", orderId);

	context.orderId=orderId;
	context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","OrderManagementOrderDetailTitle",messageMap, locale )
	context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderDetailInfoHeading",messageMap, locale )
}

if (UtilValidate.isNotEmpty(orderHeader)) 
{
	// note these are overridden in the OrderViewWebSecure.groovy script if run
	context.hasPermission = true;
	context.canViewInternalDetails = true;

	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();

	context.orderHeader = orderHeader;
	context.orderReadHelper = orderReadHelper;
	context.orderItems = orderItems;
	
	notes = orderHeader.getRelatedOrderBy("OrderHeaderNoteView", ["-noteDateTime"]);
	context.orderNotes = notes;

}	
	
//FOR EACH INDIVIDUAL ORDER ITEM SCREEN	
orderItem = request.getAttribute("orderItem");
if(UtilValidate.isNotEmpty(orderItem))
{
	context.orderItem = orderItem;
	orderItemSeqId = orderItem.orderItemSeqId;
	messageMap=[:];
	messageMap.put("orderItemSeqId", orderItemSeqId);

	context.orderId=orderId;
	context.orderItemBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderItemBoxHeading",messageMap, locale )
	
	statusItem = orderItem.getRelatedOneCache("StatusItem");
	context.statusItem = statusItem;
	
	//get Returned Quantity
	if(UtilValidate.isNotEmpty(orderHeader) && UtilValidate.isNotEmpty(orderReadHelper))
	{
		if("SALES_ORDER".equals(orderHeader.orderTypeId))
		{
			pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem);
			context.pickedQty = pickedQty;
		}
		
		// QUANTITY: get the returned quantity by order item map
		context.returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities();
	}
	
	//get shipGroup	
	shipDate = "";
	carrier = "";
	orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc");
	if(UtilValidate.isNotEmpty(orderItemShipGroupAssocs))
	{
		for (GenericValue shipGroupAssoc: orderItemShipGroupAssocs)
		{
			if(UtilValidate.isNotEmpty(shipGroupAssoc.getRelatedOne("OrderItemShipGroup")))
			{
				shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup");
				context.shipGroupAssoc = shipGroupAssoc;
			}
			if(UtilValidate.isNotEmpty(shipGroup.getRelatedOne("PostalAddress")))
			{
				orderItemShipGroupAddress = shipGroup.getRelatedOne("PostalAddress");
				orderItemShipDate = shipGroup.estimatedShipDate;
				orderItemCarrier = shipGroup.carrierPartyId + " " + shipGroup.shipmentMethodTypeId;
				orderItemTrackingNo = shipGroup.trackingNumber;
			}
			
		}
	}
	context.carrierPartyId = shipGroup.carrierPartyId;
	context.shipGroup = shipGroup;
	context.orderItemShipGroupAddress = orderItemShipGroupAddress;
	context.orderItemShipDate = orderItemShipDate;
	context.orderItemCarrier = orderItemCarrier;
	context.orderItemTrackingNo = orderItemTrackingNo;
	
	//get order adjustments
	context.orderAdjustments = orderReadHelper.getAdjustments();
	
	
	
	
	
	//get planned shipment info 
	orderShipments = orderItem.getRelated("OrderShipment");
	GenericValue orderShipment = null;
	if(UtilValidate.isNotEmpty(orderShipments))
	{
		for (GenericValue orderShip: orderShipments)
		{
			if(UtilValidate.isNotEmpty(orderShip.shipmentId))
			{
				orderShipment = orderShip;
			}
		}
	}
	
	context.orderShipment = orderShipment;
	
	//item issuances
	itemIssuances = orderItem.getRelated("ItemIssuance");
	GenericValue itemIssuance = null;
	if(UtilValidate.isNotEmpty(itemIssuances))
	{
		for (GenericValue itemIssu: itemIssuances)
		{
			if(UtilValidate.isNotEmpty(itemIssu.shipmentId))
			{
				itemIssuance = itemIssu;
			}
		}
	}
	context.itemIssuance = itemIssuance;
	
	
	//getting shipping info
	if(UtilValidate.isNotEmpty(shipGroup))
	{
		
	}
	/*
	 * <#if orderItem.orderId?exists && orderItem.orderId?has_content >
              	<#assign shipGroupAssoc = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAndCache("OrderItemShipGroupAssoc", {"orderId": orderItem.orderId, "orderItemSeqId": orderItem.orderItemSeqId}))/>
              	<#if shipGroupAssoc?has_content>
                  <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAndCache("OrderItemShipGroup", {"orderId": orderItem.orderId, "shipGroupSeqId": shipGroupAssoc.shipGroupSeqId}))/>
                  <#if shipGroup?has_content>
                      <#assign shipDate = ""/>
                      <#assign orderHeader = delegator.findByPrimaryKeyCache("OrderHeader", {"orderId": orderItem.orderId})/>
                      <#if orderHeader?has_content && (orderHeader.statusId == "ORDER_COMPLETED" || orderItem.statusId == "ITEM_COMPLETED") >
                          <#assign shipDate = shipGroup.estimatedShipDate!""/>
                          <#if shipDate?has_content>
                              <#assign shipDate = shipDate?string(preferredDateFormat)!""/>
                          </#if>
                      </#if>
                      <#assign trackingNumber = shipGroup.trackingNumber!""/>
                      <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", shipGroup.shipmentMethodTypeId, "partyId", shipGroup.carrierPartyId,"roleTypeId" ,"CARRIER")>
                      <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)>
                      <#assign shipmentMethodType = carrierShipmentMethod.getRelatedOneCache("ShipmentMethodType")/>
                      <#assign description = shipmentMethodType.description!""/>
                      <#assign carrierPartyGroupName = ""/>
                      <#if shipGroup.carrierPartyId != "_NA_">
                          <#assign carrierParty = carrierShipmentMethod.getRelatedOneCache("Party")/>
                          <#assign carrierPartyGroup = carrierParty.getRelatedOneCache("PartyGroup")/>
                          <#assign carrierPartyGroupName = carrierPartyGroup.groupName/>
                      </#if>
                  </#if>
              	</#if>
              </#if>
	 */
	
	
	
	
	
	
	
	
	
}	





