<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
 <#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate")/>
<#assign fieldsMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_WORK")/>
<#assign workPhonePartyContactDetails = delegator.findByAndCache("PartyContactDetailByPurpose", fieldsMap, orderByList)?if_exists/>
<#if workPhonePartyContactDetails?has_content>
    <#assign workPhonePartyContactDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(workPhonePartyContactDetails?if_exists)?if_exists/>
    <#assign workPhonePartyContactDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workPhonePartyContactDetails?if_exists)?if_exists/>
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
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <input type="hidden" name="workPhoneContactMechId" value="${telecomWorkNoContactMechId!}" />
      <label for="PHONE_WORK_CONTACT"><#if mandatory == "Y"><@required/></#if>${workPhoneCaption!}</label>
      <div class="entryField telephone">
        <span class="js_USER_USA js_USER_CAN">
            <input type="text" class="phone3" id="PHONE_WORK_AREA" name="PHONE_WORK_AREA" maxlength="3" value="${requestParameters.get("PHONE_WORK_AREA")!areaCodeWork!""}" />
            <input type="hidden" id="PHONE_WORK_CONTACT" name="PHONE_WORK_CONTACT" value="${requestParameters.get("PHONE_WORK_CONTACT")!contactNumberWork!""}"/>
            <input type="hidden" name="PHONE_WORK_MANDATORY" value="${mandatory}"/>
            <input type="text" class="phone3" id="PHONE_WORK_CONTACT3" name="PHONE_WORK_CONTACT3" value="${requestParameters.get("PHONE_WORK_CONTACT3")!contactNumber3Work!""}" maxlength="3"/>
            <input type="text" class="phone4" id="PHONE_WORK_CONTACT4" name="PHONE_WORK_CONTACT4" value="${requestParameters.get("PHONE_WORK_CONTACT4")!contactNumber4Work!""}" maxlength="4"/>
            <span class="innerLabel">${uiLabelMap.PhoneExtCaption}</span>
            <input type="text" class="phoneExt" id="PHONE_WORK_EXT" name="PHONE_WORK_EXT" value="${requestParameters.get("PHONE_WORK_EXT")!extensionWork!""}" maxlength="4"/>
        </span>
        <span style="display:none" class="js_USER_OTHER">
            <input type="text" class="phone10" id="PHONE_WORK_CONTACT_OTHER" name="PHONE_WORK_CONTACT_OTHER" value="${requestParameters.get("PHONE_WORK_CONTACT_OTHER")!contactNumberWork!""}" />
            <span class="innerLabel">${uiLabelMap.PhoneExtCaption}</span>
            <input type="text" class="phoneExt" id="PHONE_WORK_EXT_OTHER" name="PHONE_WORK_EXT_OTHER" value="${requestParameters.get("PHONE_WORK_EXT_OTHER")!extensionWork!""}" maxlength="4"/>
            <input type="hidden" name="PHONE_WORK_CONTACT_OTHER_MANDATORY" value="${mandatory}"/>
        </span>
        <@fieldErrors fieldName="PHONE_WORK_AREA"/>
        <@fieldErrors fieldName="PHONE_WORK_CONTACT"/>
      </div>
</div>