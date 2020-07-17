<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
        <label for="CONFIRM_PASSWORD"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.ConfirmPasswordCaption}</label>
        <div class="entryField">
	        <input type="password"  maxlength="60" name="CONFIRM_PASSWORD" id="CONFIRM_PASSWORD" class="password" value="${requestParameters.CONFIRM_PASSWORD?if_exists}" maxlength="50"/>
	        <input type="hidden" id="CONFIRM_PASSWORD_MANDATORY" name="CONFIRM_PASSWORD_MANDATORY" value="${mandatory}"/>
	        <@fieldErrors fieldName="CONFIRM_PASSWORD"/>
        </div>
</div>