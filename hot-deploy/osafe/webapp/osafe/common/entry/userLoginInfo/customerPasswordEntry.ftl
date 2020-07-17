<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<input type="hidden" name="partyId" value="${partyId!""}"/>
<div id="emailPasswordEntry" class="displayBox">
    <h3>${uiLabelMap.ChangePasswordHeading}</h3>
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
    <div class="entry userPassword editLoginInfo">
        <label for="OLD_PASSWORD"><@required/>${uiLabelMap.CurrentPasswordCaption}</label>
        <div class="entryField">
	        <input type="password" maxlength="60" class="password"  name="OLD_PASSWORD" id="OLD_PASSWORD" value="${requestParameters.OLD_PASSWORD?if_exists}" maxlength="50"/>
	        <@fieldErrors fieldName="OLD_PASSWORD"/>
        </div>
      </div>
      <div class="entry userPassword editLoginInfo">
        <label for="PASSWORD"><@required/>${uiLabelMap.NewPasswordCaption}</label>
        <div class="entryField">
	        <input type="hidden" name="USERNAME" id="js_USERNAME" value="${userLoginId?if_exists}" maxlength="255"/>
	        <input type="password" maxlength="60" class="password" name="PASSWORD" id="PASSWORD" value="${requestParameters.PASSWORD?if_exists}" maxlength="50"/>
	        <@fieldErrors fieldName="PASSWORD"/>
	        <span class="entryHelper">
	           <#if REG_PWD_MIN_CHAR?has_content && (REG_PWD_MIN_CHAR == 0 )>
	             <#-- TODO: need to get minimum value from the security.properties because a property is there password.length.min=6-->
	             <#assign REG_PWD_MIN_CHAR = 6 />
	           </#if>
	           <#if REG_PWD_MIN_CHAR?has_content>
	             <#assign passwordHelpText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels", "PassMinLengthInstructionsInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("REG_PWD_MIN_CHAR", REG_PWD_MIN_CHAR), locale)>
	             <#assign  digitMsgStr = "digits" />
	             <#if REG_PWD_MIN_NUM?has_content && (REG_PWD_MIN_NUM == 1)>
	               <#assign digitMsgStr = "digit" />
	             </#if>
	             <#if REG_PWD_MIN_NUM?has_content && (REG_PWD_MIN_NUM > 0)>
	               <#assign passwordHelpText = passwordHelpText +" "+ Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels", "PassMinNumInstructionsInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("REG_PWD_MIN_NUM", REG_PWD_MIN_NUM, "digitMsgStr", digitMsgStr), locale)>
	             </#if>
	             <#assign upperCaseMsgStr = "letters" />
	              <#if REG_PWD_MIN_UPPER?has_content && (REG_PWD_MIN_UPPER == 1)>
	                <#assign upperCaseMsgStr = "letter" />
	              </#if>
	             <#if REG_PWD_MIN_UPPER?has_content && (REG_PWD_MIN_UPPER > 0)>
	               <#assign passwordHelpText = passwordHelpText +" "+ Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels", "PassMinUpperCaseInstructionsInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("REG_PWD_MIN_UPPER", REG_PWD_MIN_UPPER, "upperCaseMsgStr", upperCaseMsgStr), locale)>
	             </#if>
	             ${passwordHelpText!}
	           </#if>
	        </span>
     	 </div>
      </div>

      <div class="entry userPasswordConfirm editLoginInfo">
        <label for="CONFIRM_PASSWORD"><@required/>${uiLabelMap.ConfirmPasswordCaption}</label>
        <div class="entryField">
	        <input type="password" maxlength="60" class="password" name="CONFIRM_PASSWORD" id="CONFIRM_PASSWORD" value="${requestParameters.CONFIRM_PASSWORD?if_exists}" maxlength="50"/>
	        <@fieldErrors fieldName="CONFIRM_PASSWORD"/>
        </div>
      </div>
   </div>
</div>
