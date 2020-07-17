<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign address3 = postalAddressData.address3!"">
</#if>
<!-- address Line3 entry -->
<div class="addressInfoAddress3">
    <div id="${fieldPurpose?if_exists}_STATE_TEXT" class="entry" style="display:none">
        <label for="${fieldPurpose?if_exists}_ADDRESS3"><span id="address3Label">${uiLabelMap.AddressLine3Caption}</span></label>
        <input type="text" maxlength="100" class="address" name="${fieldPurpose?if_exists}_ADDRESS3" id="${fieldPurpose?if_exists}_ADDRESS3" value="${requestParameters.get(fieldPurpose+"_ADDRESS3")!address3!""}" />
    </div>
</div>