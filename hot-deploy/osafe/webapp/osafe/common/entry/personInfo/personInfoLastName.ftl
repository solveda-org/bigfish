<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if person?has_content>
  <#assign lastName= person.lastName!""/>
</#if>
<div class ="personalInfoLastName">
   <div class="entry">
      <label for="USER_LAST_NAME"><@required/>${uiLabelMap.LastNameCaption}</label>
      <input type="text" maxlength="100" name="USER_LAST_NAME" id="USER_LAST_NAME" value="${requestParameters.USER_LAST_NAME!lastName!""}" />
      <@fieldErrors fieldName="USER_LAST_NAME"/>
    </div>
</div>
