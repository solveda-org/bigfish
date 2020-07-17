<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign city = postalAddressData.city!"">
</#if>

<#if parameters.fbLocationCity?has_content>
	<#assign city = parameters.fbLocationCity/>
</#if>

<!-- address city entry -->
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div id="city">
        <label for="${fieldPurpose?if_exists}_CITY">
            <span><#if mandatory == "Y"><@required/></#if>${uiLabelMap.TownOrCityCaption}</span>
        </label>
        <div class="entryField">
        	<input type="text" maxlength="100" class="city" name="${fieldPurpose?if_exists}_CITY" id="js_${fieldPurpose?if_exists}_CITY" value="${requestParameters.get(fieldPurpose+"_CITY")!city!""}" />
        	<input type="hidden" id="${fieldPurpose?if_exists}_CITY_MANDATORY" name="${fieldPurpose?if_exists}_CITY_MANDATORY" value="${mandatory}"/>
        	<@fieldErrors fieldName="${fieldPurpose?if_exists}_CITY"/>
        </div>
    </div>
</div>