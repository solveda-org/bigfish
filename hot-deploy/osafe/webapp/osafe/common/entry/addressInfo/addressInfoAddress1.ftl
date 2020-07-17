<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign address1 = postalAddressData.address1!"">
</#if>
<!-- address Line1 entry -->
<div class="addressInfoAddress1">
    <div class="entry">
      <label for="${fieldPurpose?if_exists}_ADDRESS1"><@required/>${uiLabelMap.AddressLine1Caption}</label>
      <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_ADDRESS1" id="${fieldPurpose?if_exists}_ADDRESS1" value="${requestParameters.get(fieldPurpose+"_ADDRESS1")!address1!""}" />
      <@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS1"/>
    </div>
</div>