<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign address3 = postalAddressData.address3!"">
</#if>
<!-- address Line3 entry -->
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
        <label for="${fieldPurpose?if_exists}_ADDRESS3"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.AddressLine3Caption}</label>
        <div class="entryField">
        	<input type="text" maxlength="100" class="address" name="${fieldPurpose?if_exists}_ADDRESS3" id="js_${fieldPurpose?if_exists}_ADDRESS3" value="${requestParameters.get(fieldPurpose+"_ADDRESS3")!address3!""}" />
        	<input type="hidden" id="${fieldPurpose?if_exists}_ADDRESS3_MANDATORY" name="${fieldPurpose?if_exists}_ADDRESS3_MANDATORY" value="${mandatory}"/>
        	<@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS3"/>
        </div>
</div>
