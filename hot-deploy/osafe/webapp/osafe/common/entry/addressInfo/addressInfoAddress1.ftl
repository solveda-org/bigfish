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