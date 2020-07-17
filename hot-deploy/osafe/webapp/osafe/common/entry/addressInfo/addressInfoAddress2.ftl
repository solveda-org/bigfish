<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign address2 = postalAddressData.address2!"">
</#if>
<!-- address Line2 entry -->
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
        <label for="${fieldPurpose?if_exists}_ADDRESS2"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.AddressLine2Caption}</label>
        <div class="entryField">
        	<input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_ADDRESS2" id="js_${fieldPurpose?if_exists}_ADDRESS2" value="${requestParameters.get(fieldPurpose+"_ADDRESS2")!address2!""}" />
        	<input type="hidden" id="${fieldPurpose?if_exists}_ADDRESS2_MANDATORY" name="${fieldPurpose?if_exists}_ADDRESS2_MANDATORY" value="${mandatory}"/>
        	<@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS2"/>
        </div>
</div>