<#if billingContactMechAddress?has_content>
 <div class="checkoutOrderBillingAddress">
	<div id="billingAddress" class="displayBox">
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
	                <#if billingAddress.city?has_content>${billingAddress.city}</#if><#if billingAddress.stateProvinceGeoId?has_content>, ${billingAddress.stateProvinceGeoId}</#if>
	             <#if billingAddress.postalCode?has_content> ${billingAddress.postalCode}</#if></p>
	             <#if billingAddress.countryGeoId?has_content><p>${billingAddress.countryGeoId}</p></#if>
	             <#assign billingPhone = billingPhoneNumberMap["PHONE_HOME"]!"" />
	             <#if billingPhone?has_content>
	             <p>
	                    <#assign formattedPhone = Static["com.osafe.util.Util"].formatTelephone(billingPhone.areaCode, billingPhone.contactNumber)/>
	                    ${formattedPhone}
	            </p>
	            </#if>
	         </#if>
	       </div>
	 </div>
 </div>
</#if>