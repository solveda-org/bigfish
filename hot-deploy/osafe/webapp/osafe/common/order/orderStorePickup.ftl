<#if isStorePickup?exists && isStorePickup == "Y">
	<div class="${request.getAttribute("attributeClass")!}">
      <div class="displayBox">
	       <h3>${uiLabelMap.StorePickupHeading}</h3>
	       <ul class="displayList address">
	        <#if storePickupName?has_content>
	         <li><div><span>${storePickupName}</span></div></li>
	        </#if>
	       <#if storePickupAddress.address1?has_content>
	        <li><div><span>${storePickupAddress.address1}</span></div></li>
	       </#if>
	       <#if storePickupAddress.address2?has_content>
	        <li><div><span>${storePickupAddress.address2}</span></div></li>
	       </#if>
	       <#if storePickupAddress.address3?has_content>
	        <li><div><span>${storePickupAddress.address3}</span></div></li>
	       </#if>
	        <li>
	         <div>
	         <#if storePickupAddress.city?has_content  && storePickupAddress.city != '_NA_'>
	          <span>${storePickupAddress.city!}, </span>
	         </#if>
	         <#if storePickupAddress.stateProvinceGeoId?has_content && storePickupAddress.stateProvinceGeoId != '_NA_'>
	          <span>${storePickupAddress.stateProvinceGeoId}</span>
	         </#if>
	         <#if storePickupAddress.postalCode?has_content && storePickupAddress.postalCode != '_NA_'>
	          <span>${storePickupAddress.postalCode} </span>
	         </#if>
	         <#if storePickupAddress.countryGeoId?has_content>
	          <span>${storePickupAddress.countryGeoId}</span>
	         </#if>
	        </div>
	        </li>
	       </ul>
      </div>
    </div>
</#if>	