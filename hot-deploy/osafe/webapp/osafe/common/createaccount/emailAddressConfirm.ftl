<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
        <label for= "CUSTOMER_EMAIL_CONFIRM"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.EmailAddressConfirmCaption}</label>
        <div class="entryField">
	        <input type="email"  maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL_CONFIRM" id="CUSTOMER_EMAIL_CONFIRM" value="${requestParameters.CUSTOMER_EMAIL_CONFIRM!requestParameters.USERNAME?if_exists}" maxlength="255" />
	        <input type="hidden" id="CUSTOMER_EMAIL_CONFIRM_MANDATORY" name="CUSTOMER_EMAIL_CONFIRM_MANDATORY" value="${mandatory}"/>
	        <@fieldErrors fieldName="CUSTOMER_EMAIL_CONFIRM"/>
        </div>
</div>