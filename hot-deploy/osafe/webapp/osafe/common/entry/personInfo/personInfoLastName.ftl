<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if person?has_content>
  <#assign lastName= person.lastName!""/>
<#-- for Facebook login -->
<#elseif parameters.fbLast_name?has_content>
  <#assign lastName= parameters.fbLast_name!""/>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="USER_LAST_NAME"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.LastNameCaption}</label>
      <div class="entryField">
	      <input type="text" maxlength="100" name="USER_LAST_NAME" id="js_USER_LAST_NAME" value="${requestParameters.USER_LAST_NAME!lastName!""}" />
	      <input type="hidden" name="USER_LAST_NAME_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="USER_LAST_NAME"/>
      </div>
</div>
