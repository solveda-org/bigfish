<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div id="emailSubscriberEntry" class="displayBox">
<#-- hidden fields -->
<input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
<input type="hidden" name="emailType" value="${parameters.emailType?default(emailType!"")}" />
<h3>${uiLabelMap.EmailSubscriberHeading}</h3>
   <div class="entryForm subscriber emailSubscriber">
    <div class="entry emailAddress">
      <label for= "SUBSCRIBER_EMAIL"><@required/>${uiLabelMap.EmailAddressCaption}</label>
      <div class="entryField">
	      <input type="text"  maxlength="100" class="emailAddress" name="SUBSCRIBER_EMAIL" id="SUBSCRIBER_EMAIL" value="${requestParameters.SUBSCRIBER_EMAIL!""}" maxlength="255"/>
	      <@fieldErrors fieldName="SUBSCRIBER_EMAIL"/>
      </div>
    </div>
    <div class="entry firstName">
      <label for="SUBSCRIBER_FIRST_NAME"><@required/>${uiLabelMap.FirstNameCaption}</label>
      <div class="entryField">
	      <input type="text" maxlength="100" name="SUBSCRIBER_FIRST_NAME" id="js_SUBSCRIBER_FIRST_NAME" value="${requestParameters.SUBSCRIBER_FIRST_NAME!""}" />
	      <@fieldErrors fieldName="SUBSCRIBER_FIRST_NAME"/>
      </div>
    </div>
    <div class="entry lastName">
      <label for="SUBSCRIBER_LAST_NAME"><@required/>${uiLabelMap.LastNameCaption}</label>
      <div class="entryField">
	      <input type="text" maxlength="100" name="SUBSCRIBER_LAST_NAME" id="js_SUBSCRIBER_LAST_NAME" value="${requestParameters.SUBSCRIBER_LAST_NAME!""}" />
	      <@fieldErrors fieldName="SUBSCRIBER_LAST_NAME"/>
      </div>
    </div>
    
      <#assign partyEmailPref=parameters.PARTY_EMAIL_PREFERENCE!""/>
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
      <div class="entry userEmailPreference">
        <label>&nbsp;</label>
        <label class="radioOptionLabel"><input type="radio" id="PARTY_EMAIL_HTML" name="PARTY_EMAIL_PREFERENCE" value="HTML" ${partyEmailPreferenceHtml!""}/><span class="radioOptionText">${uiLabelMap.RegistrationEmailPreferenceHtmlLabel}</span></label>
        <label class="radioOptionLabel"><input type="radio" id="PARTY_EMAIL_TEXT" name="PARTY_EMAIL_PREFERENCE" value="TEXT" ${partyEmailPreferenceText!""}/><span class="radioOptionText">${uiLabelMap.RegistrationEmailPreferenceTextLabel}</span></label>
      </div>
   </div>
</div>
