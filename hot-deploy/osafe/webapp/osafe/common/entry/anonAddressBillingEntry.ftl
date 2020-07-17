<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class="${request.getAttribute("attributeClass")!}">
<input type="hidden" name="USER_EMAIL" value="${parameters.USER_EMAIL!parameters.USERNAME!userEmailAddress!}"/>
<div class ="personInfoFirstName">
  <div class="entry firstName">
      <label for="USER_FIRST_NAME"><@required/>${uiLabelMap.FirstNameCaption}</label>
      <div class="entryField">
	      <input type="text" maxlength="100" name="USER_FIRST_NAME" id="js_USER_FIRST_NAME" value="${requestParameters.USER_FIRST_NAME!firstName!""}" />
	      <@fieldErrors fieldName="USER_FIRST_NAME"/>
      </div>
  </div>
</div>
<div class ="personInfoLastName">
   <div class="entry lastName">
      <label for="USER_LAST_NAME"><@required/>${uiLabelMap.LastNameCaption}</label>
      <div class="entryField">
	      <input type="text" maxlength="100" name="USER_LAST_NAME" id="js_USER_LAST_NAME" value="${requestParameters.USER_LAST_NAME!lastName!""}" />
	      <@fieldErrors fieldName="USER_LAST_NAME"/>
      </div>
    </div>
</div>
</div>
<div id="js_${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <input type="hidden" id="emailProductStoreId" name="emailProductStoreId" value="${productStoreId!""}"/>
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
</div>
</div>