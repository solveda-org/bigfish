<#if shippingAddress?has_content>
 <div class="checkoutOrderShippingAddress">
	<div class="displayBox">
        <h3>${uiLabelMap.ShippingAddressTitle}</h3>
	    <#-- shipping addresses -->
	       <div class="address">
	       <#if shippingAddress?has_content>
	             <#if shippingAddress.toName?has_content><p>${shippingAddress.toName}</p></#if>
	             <#if shippingAddress.address1?has_content><p>${shippingAddress.address1}</p></#if>
	             <#if shippingAddress.address2?has_content><p>${shippingAddress.address2}</p></#if>
                 <#if shippingAddress.address3?has_content><p>${shippingAddress.address3}</p></#if>
	             <p>
	                <#-- city and state have to stay on one line otherwise an extra space is added before the comma -->
	                <#if shippingAddress.city?has_content && shippingAddress.city != '_NA_'>${shippingAddress.city}</#if><#if shippingAddress.stateProvinceGeoId?has_content && shippingAddress.stateProvinceGeoId != '_NA_'>, ${shippingAddress.stateProvinceGeoId}</#if>
	             <#if shippingAddress.postalCode?has_content && shippingAddress.postalCode != '_NA_'> ${shippingAddress.postalCode}</#if></p>
	             <#if shippingAddress.countryGeoId?has_content><p>${shippingAddress.countryGeoId}</p></#if>
	         </#if>
	       </div>
	 </div>
 </div>
</#if>
