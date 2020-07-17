<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div id="emailPasswordEntry" class="displayBox">
    <h3>${uiLabelMap.LoginInformationHeading}</h3>
    <p class="instructions">${StringUtil.wrapString(uiLabelMap.ChangeLoginInstructionsInfo)}</p>
  <div class="entryForm customerLogin editLoginInfo">
    <div class="entry userLogin editLoginInfo">
      <label for= "CUSTOMER_EMAIL">${uiLabelMap.CurrentEmailAddressCaption}</label>
      <div class="entryField">
	      <input type="hidden" name="emailAddressContactMechId" value="${userEmailContactMech.contactMechId!}"/>
	      <input type="hidden" maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL" id="js_CUSTOMER_EMAIL" value="${requestParameters.CUSTOMER_EMAIL!userLoginId!""}"/>
	      ${requestParameters.CUSTOMER_EMAIL!userLoginId!""}
      </div>
    </div>
    <div class="entry userLogin editLoginInfo">
      <label for= "CUSTOMER_EMAIL"><@required/>${uiLabelMap.NewEmailAddressCaption}</label>
      <div class="entryField">
	      <input type="text" maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL_NEW" id="js_CUSTOMER_EMAIL_NEW" value="${requestParameters.CUSTOMER_EMAIL_NEW!""}"/>
	      <@fieldErrors fieldName="CUSTOMER_EMAIL_NEW"/>
      </div>
    </div>
    <div class="entry userLoginConfirm editLoginInfo">
      <label for= "CUSTOMER_EMAIL_CONFIRM"><@required/>${uiLabelMap.ConfirmEmailAddressCaption}</label>
      <div class="entryField">
	      <input type="text" maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL_CONFIRM" id="CUSTOMER_EMAIL_CONFIRM" value="${requestParameters.CUSTOMER_EMAIL_CONFIRM!""}"/>
	      <@fieldErrors fieldName="CUSTOMER_EMAIL_CONFIRM"/>
      </div>
    </div>
    <div class="entry userPassword editLoginInfo">
        <label for="OLD_PASSWORD"><@required/>${uiLabelMap.CurrentPasswordCaption}</label>
        <div class="entryField">
            <input type="hidden" name="USERNAME" id="js_USERNAME" value="${userLoginId?if_exists}" maxlength="255"/>
	        <input type="password" maxlength="60" class="password"  name="OLD_PASSWORD" id="OLD_PASSWORD" value="${requestParameters.OLD_PASSWORD?if_exists}" maxlength="50"/>
	        <@fieldErrors fieldName="OLD_PASSWORD"/>
        </div>
    </div>
  </div>
</div>
