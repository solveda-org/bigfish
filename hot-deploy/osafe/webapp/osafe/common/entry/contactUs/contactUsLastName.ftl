<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="lastName"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.LastNameCaption}</label>
      <div class="entryField">
	      <input type="text"  maxlength="100" name="lastName" id="lastName" value="${parameters.lastName!userLastName!""}"/>
	      <input type="hidden" name="lastName_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="lastName"/>
      </div>
</div>