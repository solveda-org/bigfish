<!-- start customerDetailAddressInfo.ftl -->
<#if shippingContactMechList?has_content>
 <#list shippingContactMechList as shippingContactMech>
    <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
	<div class="displayBox addressInfo">
	  <div class="header"><h2>${shippingAddress.attnName?default((shippingAddress.address1)?if_exists)}</h2></div>
	  <div class="boxBody">
		 <div class="infoRow">
		   <div class="infoEntry">
		     <div class="infoCaption"><label>${uiLabelMap.AddressCaption}</label></div>
		     <div class="infoValue address">
	                 <#if shippingAddress.toName?has_content><p>${shippingAddress.toName}</p></#if>
	                 <#if shippingAddress.address1?has_content><p>${shippingAddress.address1}</p></#if>
	                 <#if shippingAddress.address2?has_content><p>${shippingAddress.address2}</p></#if>
	                 <#if shippingAddress.address3?has_content><p>${shippingAddress.address3}</p></#if>
	                 <p>
	                 <#-- city and state have to stay on one line otherwise an extra space is added before the comma -->
	                 <#if shippingAddress.city?has_content && shippingAddress.city != '_NA_'>${shippingAddress.city}</#if><#if shippingAddress.stateProvinceGeoId?has_content && shippingAddress.stateProvinceGeoId != '_NA_'>, ${shippingAddress.stateProvinceGeoId}</#if>
	                 <#if shippingAddress.postalCode?has_content && shippingAddress.postalCode != '_NA_' > ${shippingAddress.postalCode}</#if></p>
	                 <#if shippingAddress.countryGeoId?has_content><p>${shippingAddress.countryGeoId}</p></#if>
		     </div>
		   </div>
		 </div>
      </div>
    </div>
  </#list>
</#if>


<!-- end customerDetailAddressInfo.ftl -->