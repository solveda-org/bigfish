<#if billingContactMechAddress?has_content>
 <div class="checkoutOrderBillingAddress">
	<div class="displayBox">
		  <div class="displayBoxHeader">
		        <span class="displayBoxHeaderCaption">${uiLabelMap.BillingAddressTitle}</span>
		  </div>
	    <#-- Billing addresses -->
	       <#assign billingAddress = billingContactMechAddress.getRelatedOne("PostalAddress")>
	       <div class="address">
	       <#if billingAddress?has_content>
	             <#if billingAddress.toName?has_content><p>${billingAddress.toName}</p></#if>
	             <#if billingAddress.address1?has_content><p>${billingAddress.address1}</p></#if>
	             <#if billingAddress.address2?has_content><p>${billingAddress.address2}</p></#if>
	             <p>
	                <#-- city and state have to stay on one line otherwise an extra space is added before the comma -->
	                <#if billingAddress.city?has_content && billingAddress.city != '_NA_'>${billingAddress.city}</#if><#if billingAddress.stateProvinceGeoId?has_content && billingAddress.stateProvinceGeoId != '_NA_'>, ${billingAddress.stateProvinceGeoId}</#if>
	             <#if billingAddress.postalCode?has_content && billingAddress.postalCode != '_NA_'> ${billingAddress.postalCode}</#if></p>
	             <#if billingAddress.countryGeoId?has_content><p>${billingAddress.countryGeoId}</p></#if>
	         </#if>
	       </div>
	 </div>
 </div>
</#if>