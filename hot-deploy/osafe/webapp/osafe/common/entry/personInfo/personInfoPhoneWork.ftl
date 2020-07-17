<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
 <#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate")/>
<#assign fieldsMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_WORK")/>
<#assign workPhonePartyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", fieldsMap, orderByList)?if_exists/>
<#if workPhonePartyContactDetails?has_content>
    <#assign workPhonePartyContactDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(workPhonePartyContactDetails?if_exists)/>
    <#assign workPhonePartyContactDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workPhonePartyContactDetails?if_exists)/>
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
        <label for="PERSON_WORK_CONTACT">${workPhoneCaption!}</label>
        <span class="USER_USA USER_CAN">
            <input type="text" class="phone3" id="PERSON_WORK_AREA" name="PERSON_WORK_AREA" maxlength="3" value="${requestParameters.get("PERSON_WORK_AREA")!areaCodeWork!""}" />
            <input type="hidden" id="PERSON_WORK_CONTACT" name="PERSON_WORK_CONTACT" value="${requestParameters.get("PERSON_WORK_CONTACT")!contactNumberWork!""}"/>
            <input type="text" class="phone3" id="PERSON_WORK_CONTACT3" name="PERSON_WORK_CONTACT3" value="${requestParameters.get("PERSON_WORK_CONTACT3")!contactNumber3Work!""}" maxlength="3"/>
            <input type="text" class="phone4" id="PERSON_WORK_CONTACT4" name="PERSON_WORK_CONTACT4" value="${requestParameters.get("PERSON_WORK_CONTACT4")!contactNumber4Work!""}" maxlength="4"/>
            ${uiLabelMap.PhoneExtCaption}
            <input type="text" class="phoneExt" id="PERSON_WORK_EXT" name="PERSON_WORK_EXT" value="${requestParameters.get("PERSON_WORK_EXT")!extensionWork!""}" maxlength="10"/>
        </span>
        <span style="display:none" class="USER_OTHER">
            <input type="text" class="address" id="PERSON_WORK_CONTACT_OTHER" name="PERSON_WORK_CONTACT_OTHER" value="${requestParameters.get("PERSON_WORK_CONTACT_OTHER")!contactNumberWork!""}" />
            ${uiLabelMap.PhoneExtCaption}
            <input type="text" class="phoneExt" id="PERSON_WORK_EXT_OTHER" name="PERSON_WORK_EXT_OTHER" value="${requestParameters.get("PERSON_WORK_EXT_OTHER")!extensionWork!""}" maxlength="10"/>
        </span>
        <@fieldErrors fieldName="PERSON_WORK_AREA"/>
        <@fieldErrors fieldName="PERSON_WORK_CONTACT"/>
    </div>
</div>