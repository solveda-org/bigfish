<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign partyDBAllowSolicit=allowSolicitation!""/>
<#assign partyDBEmailPref=partyEmailPreference!""/>
<div id="emailPasswordEntry" class="displayBox">
<div class="displayBoxHeader">
      <span class="displayBoxHeaderCaption">${uiLabelMap.EmailAddressHeading}</span>
</div>      
  <fieldset class="col">
    <div class="entry">
      <label for= "CUSTOMER_EMAIL"><@required/>${uiLabelMap.EmailAddressCaption}</label>
      <input type="text"  maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL" id="CUSTOMER_EMAIL" value="${requestParameters.CUSTOMER_EMAIL!requestParameters.USERNAME?if_exists}" onchange="changeEmail();" maxlength="255"/>
      <span class="instructions">${uiLabelMap.EmailAddressInstructionsInfo}</span>
      <input type="hidden" name="UNUSEEMAIL" id="UNUSEEMAIL" value="on" />
      <input type="hidden" name="USERNAME" id="USERNAME" value="${requestParameters.USERNAME?if_exists}" maxlength="255"/>
      <@fieldErrors fieldName="CUSTOMER_EMAIL"/>
    </div>
    <div class="entry">
      <label for= "CUSTOMER_EMAIL_CONFIRM"><@required/>${uiLabelMap.EmailAddressConfirmCaption}</label>
      <input type="text"  maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL_CONFIRM" id="CUSTOMER_EMAIL_CONFIRM" value="${requestParameters.CUSTOMER_EMAIL_CONFIRM!requestParameters.USERNAME?if_exists}" maxlength="255" />
      <@fieldErrors fieldName="CUSTOMER_EMAIL_CONFIRM"/>
    </div>
      <div class="entry">
        <label for="PASSWORD"><@required/>${uiLabelMap.PasswordCaption}</label>
        <input type="password"  maxlength="60" class="password" name="PASSWORD"  id="PASSWORD" value="${requestParameters.PASSWORD?if_exists}" maxlength="50"/>
        <span class="instructions">
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
        <@fieldErrors fieldName="PASSWORD"/>
      </div>

      <div class="entry">
        <label for="CONFIRM_PASSWORD"><@required/>${uiLabelMap.RepeatPasswordCaption}</label>
        <input type="password"  maxlength="60" name="CONFIRM_PASSWORD" id="CONFIRM_PASSWORD" class="password" value="${requestParameters.CONFIRM_PASSWORD?if_exists}" maxlength="50"/>
        <@fieldErrors fieldName="CONFIRM_PASSWORD"/>
      </div>

      <#assign partyAllowSolicit=parameters.CUSTOMER_EMAIL_ALLOW_SOL!partyDBAllowSolicit!""/>
      <#if partyAllowSolicit?has_content && partyAllowSolicit == "N">
        <#assign partyAllowSolicitChecked=""/>
      <#else>
        <#assign partyAllowSolicitChecked="checked"/>
      </#if>

      <div class="entry">
        <label>&nbsp;</label>
        <input type="checkbox" id="CUSTOMER_EMAIL_ALLOW_SOL" name="CUSTOMER_EMAIL_ALLOW_SOL" value="Y" ${partyAllowSolicitChecked!""}/><span class="radioOptionText">${uiLabelMap.RegistrationSolicitCheckboxLabel}</span>
        <@fieldErrors fieldName="PARTY_SOLICIT"/>
      </div>

      <#assign partyEmailPref=parameters.PARTY_EMAIL_PREFERENCE!partyDBEmailPref!""/>
      <#assign partyEmailPreferenceHtml="checked"/>
      <#assign partyEmailPreferenceText=""/>
      <#if partyEmailPref?has_content>
        <#if partyEmailPref == "HTML">
          <#assign partyEmailPreferenceHtml="checked"/>
        <#else>
           <#assign partyEmailPreferenceHtml=""/>
           <#assign partyEmailPreferenceText="checked"/>
        </#if>
      </#if>
      <div class="entry">
        <label>&nbsp;</label>
        <input type="radio" id="PARTY_EMAIL_HTML" name="PARTY_EMAIL_PREFERENCE" value="HTML" ${partyEmailPreferenceHtml!""}/><span class="radioOptionText">${uiLabelMap.RegistrationEmailPreferenceHtmlLabel}</span>
      </div>
      <div class="entry">
        <label>&nbsp;</label>
        <input type="radio" id="PARTY_EMAIL_TEXT" name="PARTY_EMAIL_PREFERENCE" value="TEXT" ${partyEmailPreferenceText!""}/><span class="radioOptionText">${uiLabelMap.RegistrationEmailPreferenceTextLabel}</span>
      </div>
  </fieldset>
</div>
