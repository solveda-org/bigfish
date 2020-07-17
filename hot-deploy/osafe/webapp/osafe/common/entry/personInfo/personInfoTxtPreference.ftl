<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign partyDBTextPref=partyTextPreference!""/>
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
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <label for="PARTY_TEXT_PREFERENCE"><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.TextMessageNotificationsCaption}</label>
    <div class="entryField">
        <label class="radioOptionLabel"><input type="radio" id="PARTY_TEXT_YES" name="PARTY_TEXT_PREFERENCE" value="Y" ${partyTextPreferenceYes!""}/><span class="radioOptionText">${uiLabelMap.RegistrationTextPreferenceYesLabel}</span></label>
        <label class="radioOptionLabel"><input type="radio" id="PARTY_TEXT_NO" name="PARTY_TEXT_PREFERENCE" value="N" ${partyTextPreferenceNo!""}/><span class="radioOptionText">${uiLabelMap.RegistrationTextPreferenceNoLabel}</span></label>
        <@fieldErrors fieldName="PARTY_TEXT_PREFERENCE"/>
        <input type="hidden" name="PARTY_TEXT_PREFERENCE_MANDATORY" value="${mandatory}"/>
    </div>
</div>