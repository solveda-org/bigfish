<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="emailAddress"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.EmailAddressCaption}</label>
      <div class="entryField">
	      <input type="email"  maxlength="100" class="emailAddress" name="emailAddress" id="emailAddress" value="${parameters.emailAddress!userEmailAddress!""}"/>
	      <input type="hidden" name="emailAddress_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="emailAddress"/>
      </div>
</div>