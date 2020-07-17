<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<input type="hidden" name="USER_EMAIL" value="${parameters.USER_EMAIL!parameters.USERNAME!userEmailAddress!}"/>
<div class ="personInfoFirstName">
  <div class="entry">
      <label for="USER_FIRST_NAME"><@required/>${uiLabelMap.FirstNameCaption}</label>
      <input type="text" maxlength="100" name="USER_FIRST_NAME" id="USER_FIRST_NAME" value="${requestParameters.USER_FIRST_NAME!firstName!""}" />
      <@fieldErrors fieldName="USER_FIRST_NAME"/>
  </div>
</div>
<div class ="personInfoLastName">
   <div class="entry">
      <label for="USER_LAST_NAME"><@required/>${uiLabelMap.LastNameCaption}</label>
      <input type="text" maxlength="100" name="USER_LAST_NAME" id="USER_LAST_NAME" value="${requestParameters.USER_LAST_NAME!lastName!""}" />
      <@fieldErrors fieldName="USER_LAST_NAME"/>
    </div>
</div>
</div><div id="${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <input type="hidden" id="emailProductStoreId" name="emailProductStoreId" value="${productStoreId!""}"/>
    <input type="hidden" id="${fieldPurpose?if_exists}_ATTN_NAME" name="${fieldPurpose?if_exists}_ATTN_NAME" value="Billing Address"/>
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
</div>