<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign partyDBAllowSolicit = userEmailAllowSolicitation!""/>
<#assign partyAllowSolicit = parameters.CUSTOMER_EMAIL_ALLOW_SOL!partyDBAllowSolicit!""/>
<#if partyAllowSolicit?has_content && partyAllowSolicit == "Y">
    <#assign partyAllowSolicitChecked="checked"/>
<#else>
    <#assign partyAllowSolicitChecked=""/>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>

<div class="${request.getAttribute("attributeClass")!}">
	<label for="emailSolicitation"><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.OffersCaption}</label>
	<div class="entryField">
	    <#assign emailAddressContactMechId=""/>
	    <#if userEmailContactMech?has_content>
	         <#assign emailAddressContactMechId=userEmailContactMech.contactMechId!""/>
	    </#if>
	    <input type="hidden" name="emailAddressContactMechId" value="${emailAddressContactMechId!}"/>
	    <label class="checkboxOptionLabel"><input type="checkbox" id="CUSTOMER_EMAIL_ALLOW_SOL" name="CUSTOMER_EMAIL_ALLOW_SOL" value="Y" ${partyAllowSolicitChecked!""}/><span class="radioOptionText">${uiLabelMap.RegistrationSolicitCheckboxLabel}</span></label>
	    <@fieldErrors fieldName="CUSTOMER_EMAIL_ALLOW_SOL"/>
	    <input type="hidden" name="CUSTOMER_EMAIL_ALLOW_SOL_MANDATORY" value="${mandatory}"/>
	</div>
</div>