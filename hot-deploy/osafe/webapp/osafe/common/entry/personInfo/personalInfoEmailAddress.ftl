<!-- address email entry -->
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class = "${request.getAttribute("attributeClass")!}">
        <label for="${fieldPurpose?if_exists}_EMAIL_ADDR"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.EmailAddressCaption}</label>
        <div class="entryField">
	        <input type="email" maxlength="100" name="${fieldPurpose?if_exists}_EMAIL_ADDR" id="${fieldPurpose?if_exists}_EMAIL_ADDR" value="${requestParameters.get(fieldPurpose+"_EMAIL_ADDR")!emailLogin!""}"/>
	        <input type="hidden" name="${fieldPurpose?if_exists}_EMAIL_ADDR_MANDATORY" value="${mandatory}"/>
	        <@fieldErrors fieldName="${fieldPurpose?if_exists}_EMAIL_ADDR"/>
        </div>
</div>
