<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign postalCode = postalAddressData.postalCode!"">
    <#if postalCode?has_content && postalCode == '_NA_'>
      <#assign postalCode = "">
    </#if>
</#if>
<!-- address zip entry -->
<div class="addressInfoZipPostcode">
    <div class="entry">
        <label for="${fieldPurpose?if_exists}_POSTAL_CODE"><@required/>
            <span class="${fieldPurpose?if_exists}_USA">${uiLabelMap.ZipCodeCaption}</span>
            <span class="${fieldPurpose?if_exists}_CAN ${fieldPurpose?if_exists}_OTHER">${uiLabelMap.PostalCodeCaption}</span>
        </label>
        <input type="text" maxlength="60" class="postalCode" name="${fieldPurpose?if_exists}_POSTAL_CODE" id="${fieldPurpose?if_exists}_POSTAL_CODE" value="${requestParameters.get(fieldPurpose+"_POSTAL_CODE")!postalCode!""}" />
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_POSTAL_CODE"/>
    </div>
</div>