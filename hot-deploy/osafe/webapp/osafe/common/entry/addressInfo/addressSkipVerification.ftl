<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<!-- address skip verification -->
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
        <label>${uiLabelMap.AddressSkipVerificationLabel}</label>
        <div class="entryField">
        	<label class="checkboxOptionLabel"><input type="checkbox" id="${fieldPurpose?if_exists}_SKIP_VERIFICATION" name="${fieldPurpose?if_exists}_SKIP_VERIFICATION" value="Y" <#if requestParameters.get(fieldPurpose+"_SKIP_VERIFICATION")?has_content && requestParameters.get(fieldPurpose+"_SKIP_VERIFICATION") == "Y">checked</#if>/></label>
        	<@fieldErrors fieldName="${fieldPurpose?if_exists}_SKIP_VERIFICATION"/>
        </div>
</div>