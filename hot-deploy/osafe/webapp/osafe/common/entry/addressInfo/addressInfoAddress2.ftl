<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign address2 = postalAddressData.address2!"">
</#if>
<!-- address Line2 entry -->
<div class="addressInfoAddress2">
    <div class="entry">
        <label for="${fieldPurpose?if_exists}_ADDRESS2">${uiLabelMap.AddressLine2Caption}</label>
        <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_ADDRESS2" id="${fieldPurpose?if_exists}_ADDRESS2" value="${requestParameters.get(fieldPurpose+"_ADDRESS2")!address2!""}" />
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS2"/>
    </div>
</div>