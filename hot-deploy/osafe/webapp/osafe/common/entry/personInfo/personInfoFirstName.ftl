<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if person?has_content>
  <#assign firstName= person.firstName!""/>
</#if>
<div class ="personInfoFirstName">
  <div class="entry">
      <label for="USER_FIRST_NAME"><@required/>${uiLabelMap.FirstNameCaption}</label>
      <input type="text" maxlength="100" name="USER_FIRST_NAME" id="USER_FIRST_NAME" value="${requestParameters.USER_FIRST_NAME!firstName!""}" />
      <@fieldErrors fieldName="USER_FIRST_NAME"/>
  </div>
</div>