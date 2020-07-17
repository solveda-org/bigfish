<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign partyDBEmailPref = partyEmailPreference!""/>
<#assign partyEmailPref = parameters.PARTY_EMAIL_PREFERENCE!partyDBEmailPref!""/>
<#assign partyEmailPreferenceHtml = "checked"/>
<#assign partyEmailPreferenceText = ""/>
<#if partyEmailPref?has_content>
    <#if partyEmailPref == "HTML">
        <#assign partyEmailPreferenceHtml="checked"/>
    <#else>
        <#assign partyEmailPreferenceHtml=""/>
        <#assign partyEmailPreferenceText="checked"/>
    </#if>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <label for="PARTY_EMAIL_PREFERENCE"><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.EmailMessageNotificationsCaption}</label>
    <div class="entryField">
        <label class="radioOptionLabel"><input type="radio" id="PARTY_EMAIL_HTML" name="PARTY_EMAIL_PREFERENCE" value="HTML" ${partyEmailPreferenceHtml!""}/><span class="radioOptionText">${uiLabelMap.RegistrationEmailPreferenceHtmlLabel}</span></label>
        <label class="radioOptionLabel"><input type="radio" id="PARTY_EMAIL_TEXT" name="PARTY_EMAIL_PREFERENCE" value="TEXT" ${partyEmailPreferenceText!""}/><span class="radioOptionText">${uiLabelMap.RegistrationEmailPreferenceTextLabel}</span></label>
        <@fieldErrors fieldName="PARTY_EMAIL_PREFERENCE"/>
        <input type="hidden" name="PARTY_EMAIL_PREFERENCE_MANDATORY" value="${mandatory}"/>
    </div>
</div>