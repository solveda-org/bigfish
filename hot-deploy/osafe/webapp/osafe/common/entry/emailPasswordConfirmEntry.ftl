<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<script type="text/javascript">
    jQuery(document).ready(function () {
            getAddressFormat("USER");
    });
</script>
<#assign COUNTRY_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"COUNTRY_DEFAULT")!""/>
<input type="hidden" name="USER_COUNTRY" id="USER_COUNTRY" value="${COUNTRY_DEFAULT!}"/>
<input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
<#assign partyDBAllowSolicit=allowSolicitation!""/>
<#assign partyDBEmailPref=partyEmailPreference!""/>
<#assign partyDBTextPref=partyTextPreference!""/>
<input type="hidden" name="partyId" value="${partyId!""}"/>
<div id="emailPasswordEntry" class="displayBox">
<h3>${uiLabelMap.ChangeLoginHeading}</h3>
<p class="instructions">${StringUtil.wrapString(uiLabelMap.ChangeLoginInstructionsInfo)}</p>
  <fieldset class="col">
    <div class="entry">
      <label for= "CUSTOMER_EMAIL"><@required/>${uiLabelMap.EmailAddressCaption}</label>
      <input type="hidden" name="emailAddressContactMechId" value="${userEmailContactMech.contactMechId!}"/>
      <input type="text" maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL" id="CUSTOMER_EMAIL" value="${requestParameters.CUSTOMER_EMAIL!userEmailAddress!""}"/>
      <span class="instructions">${uiLabelMap.EditEmailAddressInstructionsInfo}</span>
      <@fieldErrors fieldName="CUSTOMER_EMAIL"/>
    </div>
    <div class="entry">
      <label for= "CUSTOMER_EMAIL_CONFIRM"><@required/>${uiLabelMap.EmailAddressConfirmCaption}</label>
      <input type="text" maxlength="100" class="emailAddress" name="CUSTOMER_EMAIL_CONFIRM" id="CUSTOMER_EMAIL_CONFIRM" value="${requestParameters.CUSTOMER_EMAIL_CONFIRM!""}"/>
      <@fieldErrors fieldName="CUSTOMER_EMAIL_CONFIRM"/>
    </div>
      <div class="entry">
        <label for="OLD_PASSWORD"><@required/>${uiLabelMap.OldPasswordCaption}</label>
        <input type="password" maxlength="60" class="password"  name="OLD_PASSWORD" id="OLD_PASSWORD" value="${requestParameters.OLD_PASSWORD?if_exists}" maxlength="50"/>
        <@fieldErrors fieldName="OLD_PASSWORD"/>
      </div>
      <div class="entry">
        <label for="PASSWORD"><@required/>${uiLabelMap.NewPasswordCaption}</label>
        <input type="hidden" name="USERNAME" id="USERNAME" value="${userLoginId?if_exists}" maxlength="255"/>
        <input type="password" maxlength="60" class="password" name="PASSWORD" id="PASSWORD" value="${requestParameters.PASSWORD?if_exists}" maxlength="50"/>
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
        <input type="password" maxlength="60" class="password" name="CONFIRM_PASSWORD" id="CONFIRM_PASSWORD" value="${requestParameters.CONFIRM_PASSWORD?if_exists}" maxlength="50"/>
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
        <label for="PARTY_EMAIL_PREFERENCE">${uiLabelMap.EmailMessageNotificationsCaption}</label>
        <input type="radio" id="PARTY_EMAIL_HTML" name="PARTY_EMAIL_PREFERENCE" value="HTML" ${partyEmailPreferenceHtml!""}/><span class="radioOptionText">${uiLabelMap.RegistrationEmailPreferenceHtmlLabel}</span>
      </div>
      <div class="entry">
        <label>&nbsp;</label>
        <input type="radio" id="PARTY_EMAIL_TEXT" name="PARTY_EMAIL_PREFERENCE" value="TEXT" ${partyEmailPreferenceText!""}/><span class="radioOptionText">${uiLabelMap.RegistrationEmailPreferenceTextLabel}</span>
      </div>
      
      <#assign txtMessageMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"TXT_MESSAGE_METHOD ")!""/>
	  <#if txtMessageMethod?has_content && (txtMessageMethod.toUpperCase() != "NONE") >
        <#assign partyTextPref=parameters.PARTY_TEXT_PREFERENCE!partyDBTextPref!""/>
        <#assign partyTextPreferenceYes=""/>
        <#assign partyTextPreferenceNo="checked"/>
        <#if partyTextPref?has_content>
            <#if partyTextPref == "Y">
              <#assign partyTextPreferenceNo=""/>
              <#assign partyTextPreferenceYes="checked"/>
            <#else>
              <#assign partyTextPreferenceYes=""/>
              <#assign partyTextPreferenceNo="checked"/>
            </#if>
        </#if>
        <div class="entry">
          <label for="PARTY_TEXT_PREFERENCE">${uiLabelMap.TextMessageNotificationsCaption}</label>
          <input type="radio" id="PARTY_TEXT_YES" name="PARTY_TEXT_PREFERENCE" value="Y" ${partyTextPreferenceYes!""}/><span class="radioOptionText">${uiLabelMap.RegistrationTextPreferenceYesLabel}</span>
          <br/>
          <input type="radio" id="PARTY_TEXT_NO" name="PARTY_TEXT_PREFERENCE" value="N" ${partyTextPreferenceNo!""}/><span class="radioOptionText">${uiLabelMap.RegistrationTextPreferenceNoLabel}</span>
        </div>
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#personalInfoPhoneCell")}
        <@fieldErrors fieldName="TEXT_PREFERENCE"/>
      </#if>
  </fieldset>
</div>
