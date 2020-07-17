<#-- This FTL will also be displayed either in PersonalInfo or in Billing so we can put hidden inputs that need to be processed here -->
<#if fieldPurpose=="BILLING" || fieldPurpose=="PERSONAL">
	<#if parameters.isFBLogin?has_content && parameters.isFBLogin == "Y">
		<input type="hidden" name="isFBLogin" value="Y"/>
		<input type="hidden" name="FACEBOOK_USER" value="TRUE"/>
		<input type="hidden" name="FACEBOOK_ID" value="${parameters.fbId!parameters.FACEBOOK_ID!""}"/>
		<input type="hidden" name="CUSTOMER_EMAIL" value="${parameters.fbEmail!parameters.CUSTOMER_EMAIL!""}"/>
		<input type="hidden" name="CUSTOMER_EMAIL_CONFIRM" value="${parameters.fbEmail!parameters.CUSTOMER_EMAIL_CONFIRM!""}"/>
		<input type="hidden" name="USERNAME" value="${parameters.fbEmail!parameters.USERNAME!""}"/>
		<input type="hidden" name="UNUSEEMAIL" id="UNUSEEMAIL" value="on" />
	</#if>
</#if>

<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign address1 = postalAddressData.address1!"">
</#if>
<!-- address Line1 entry -->
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="${fieldPurpose?if_exists}_ADDRESS1"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.AddressLine1Caption}</label>
      <div class="entryField">
      	<input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_ADDRESS1" id="js_${fieldPurpose?if_exists}_ADDRESS1" value="${requestParameters.get(fieldPurpose+"_ADDRESS1")!address1!""}" />
      	<input type="hidden" id="${fieldPurpose?if_exists}_ADDRESS1_MANDATORY" name="${fieldPurpose?if_exists}_ADDRESS1_MANDATORY" value="${mandatory}"/>
      	<@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS1"/>
      </div>
</div>