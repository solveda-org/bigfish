<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="firstName"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.FirstNameCaption}</label>
      <div class="entryField">
	      <input type="text" maxlength="100" name="firstName" id="firstName" value="${parameters.firstName!userFirstName!""}"/>
	      <input type="hidden" name="firstName_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="firstName"/>
      </div>
</div>