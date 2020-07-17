<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<!-- address skip verification -->
<div class="addressSkipVerification">
    <div class="entry">
        <label>&nbsp;</label>
        <input type="checkbox" id="${fieldPurpose?if_exists}_SKIP_VERIFICATION" name="${fieldPurpose?if_exists}_SKIP_VERIFICATION" value="Y" <#if requestParameters.get(fieldPurpose+"_SKIP_VERIFICATION")?has_content && requestParameters.get(fieldPurpose+"_SKIP_VERIFICATION") == "Y">checked</#if>/>
        <span class="radioOptionText">${uiLabelMap.AddressSkipVerificationLabel}</span>
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_SKIP_VERIFICATION"/>
    </div>
</div>