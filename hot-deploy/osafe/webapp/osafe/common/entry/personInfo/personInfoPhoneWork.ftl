<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
 
<#-- Checks the Field Purpose -->
<#if fieldPurpose?has_content>
    <#assign phoneContactfieldPurpose = fieldPurpose />
<#else>
    <#assign phoneContactfieldPurpose = "USER" />
</#if>

<#-- Splits the contactNumber -->
<#if workPhonePartyContactDetail?exists && workPhonePartyContactDetail?has_content>
    <#assign extensionWork = workPhonePartyContactDetail.extension?if_exists />
    <#assign telecomWorkNoContactMechId = workPhonePartyContactDetail.contactMechId!"" />
    <#assign areaCodeWork = workPhonePartyContactDetail.areaCode?if_exists />
    <#assign contactNumberWork = workPhonePartyContactDetail.contactNumber?if_exists />
    <#if (contactNumberWork?has_content) && (contactNumberWork?length gt 6)>
        <#assign contactNumber3Work = contactNumberWork.substring(0, 3) />
        <#assign contactNumber4Work = contactNumberWork.substring(3, 7) />
    </#if>
</#if>

<#-- Displays the work phone entry -->
<div class = "personInfoPhoneWork">
    <input type="hidden" name="workPhoneContactMechId" value="${telecomWorkNoContactMechId!}" />
    <div class="entry">
        <label for="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT">${workPhoneCaption!}</label>
        <span class="${phoneContactfieldPurpose?if_exists}_USA ${phoneContactfieldPurpose?if_exists}_CAN">
            <input type="text" class="phone3" id="${phoneContactfieldPurpose?if_exists}_WORK_AREA" name="${phoneContactfieldPurpose?if_exists}_WORK_AREA" maxlength="3" value="${requestParameters.get(phoneContactfieldPurpose+"_WORK_AREA")!areaCodeWork!""}" />
            <input type="hidden" id="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT" name="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT" value="${requestParameters.get(phoneContactfieldPurpose+"_WORK_CONTACT")!contactNumberWork!""}"/>
            <input type="text" class="phone3" id="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT3" name="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT3" value="${requestParameters.get(phoneContactfieldPurpose+"_WORK_CONTACT3")!contactNumber3Work!""}" maxlength="3"/>
            <input type="text" class="phone4" id="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT4" name="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT4" value="${requestParameters.get(phoneContactfieldPurpose+"_WORK_CONTACT4")!contactNumber4Work!""}" maxlength="4"/>
            ${uiLabelMap.PhoneExtCaption}
            <input type="text" class="phoneExt" id="${phoneContactfieldPurpose?if_exists}_WORK_EXT" name="${phoneContactfieldPurpose?if_exists}_WORK_EXT" value="${requestParameters.get(phoneContactfieldPurpose+"_WORK_EXT")!extensionWork!""}" maxlength="10"/>
        </span>
        <span style="display:none" class="${phoneContactfieldPurpose?if_exists}_OTHER">
            <input type="text" class="address" id="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT_OTHER" name="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT_OTHER" value="${requestParameters.get(phoneContactfieldPurpose+"_WORK_CONTACT_OTHER")!contactNumberWork!""}" />
            ${uiLabelMap.PhoneExtCaption}
            <input type="text" class="phoneExt" id="${phoneContactfieldPurpose?if_exists}_WORK_EXT_OTHER" name="${phoneContactfieldPurpose?if_exists}_WORK_EXT_OTHER" value="${requestParameters.get(phoneContactfieldPurpose+"_WORK_EXT_OTHER")!extensionWork!""}" maxlength="10"/>
        </span>
        <@fieldErrors fieldName="${phoneContactfieldPurpose?if_exists}_WORK_AREA"/>
        <@fieldErrors fieldName="${phoneContactfieldPurpose?if_exists}_WORK_CONTACT"/>
    </div>
</div>