<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign city = postalAddressData.city!"">
</#if>
<!-- address city entry -->
<div class="addressInfoCityTown">
    <div id="city" class="entry">
        <label for="${fieldPurpose?if_exists}_CITY"><@required/>
            <span class="${fieldPurpose?if_exists}_USA ${fieldPurpose?if_exists}_CAN">${uiLabelMap.CityCaption}</span>
            <span class="${fieldPurpose?if_exists}_OTHER">${uiLabelMap.TownOrCityCaption}</span>
        </label>
        <input type="text" maxlength="100" class="city" name="${fieldPurpose?if_exists}_CITY" id="${fieldPurpose?if_exists}_CITY" value="${requestParameters.get(fieldPurpose+"_CITY")!city!""}" />
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_CITY"/>
    </div>
</div>