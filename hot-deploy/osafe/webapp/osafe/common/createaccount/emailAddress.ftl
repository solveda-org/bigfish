<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
        <label for= "CUSTOMER_EMAIL"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.EmailAddressCaption}</label>
        <div class="entryField">
	        <input type="email"  maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL" id="js_CUSTOMER_EMAIL" value="${sessionAttributes.USER_LOGIN_EMAIL!}" onchange="changeEmail();" maxlength="255" readonly="readonly"/>
	        <span class="entryHelper">${uiLabelMap.EmailAddressInstructionsInfo}</span>
	        <input type="hidden" name="UNUSEEMAIL" id="UNUSEEMAIL" value="on" />
	        <input type="hidden" name="USERNAME" id="js_USERNAME" value="${sessionAttributes.USER_LOGIN_EMAIL!}" maxlength="255"/>
	        <input type="hidden" id="CUSTOMER_EMAIL_MANDATORY" name="CUSTOMER_EMAIL_MANDATORY" value="${mandatory}"/>
	        <@fieldErrors fieldName="CUSTOMER_EMAIL"/>
        </div>
</div>