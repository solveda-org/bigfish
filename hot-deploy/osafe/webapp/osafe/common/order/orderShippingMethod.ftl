<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.ShippingMethodCaption}</label>
    <#assign selectedStoreId = "" />
    <#if isStorePickup?exists && isStorePickup == "Y">
	        <span>${storePickupName!}</span>
    <#else>
	    <#if orderItemShipGroups?has_content>
          <#assign chosenShippingMethodDescription = "" />
		    <#list orderItemShipGroups as shipGroup>
		          <#assign shipmentMethodType = shipGroup.getRelatedOneCache("ShipmentMethodType")?if_exists>
		          <#assign carrierPartyId = shipGroup.carrierPartyId?if_exists>
		          <#if shipmentMethodType?has_content>
		            <#assign carrier =  delegator.findByPrimaryKeyCache("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
		            <#if carrier?has_content >
		              <#assign chosenShippingMethodDescription = carrier.groupName?default(carrier.partyId) + " " + shipmentMethodType.description >
		            </#if>
		          </#if>
		    </#list>
	        <span>${chosenShippingMethodDescription!}</span>
	    </#if>
    </#if>
  </div>
</li>