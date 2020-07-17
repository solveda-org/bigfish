<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate")/>
<#assign fieldsMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_MOBILE")/>
<#assign mobilePhonePartyContactDetails = delegator.findByAndCache("PartyContactDetailByPurpose", fieldsMap, orderByList)?if_exists/>
<#if mobilePhonePartyContactDetails?has_content>
    <#assign mobilePhonePartyContactDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(mobilePhonePartyContactDetails?if_exists)?if_exists/>
    <#assign mobilePhonePartyContactDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(mobilePhonePartyContactDetails?if_exists)?if_exists/>
</#if>
<#-- Splits the contactNumber -->
<#if mobilePhonePartyContactDetail?exists && mobilePhonePartyContactDetail?has_content>
    <#assign telecomMobileNoContactMechId = mobilePhonePartyContactDetail.contactMechId!"" />
    <#assign areaCodeMobile = mobilePhonePartyContactDetail.areaCode?if_exists />
    <#assign contactNumberMobile = mobilePhonePartyContactDetail.contactNumber?if_exists />
    <#if (contactNumberMobile?has_content) && (contactNumberMobile?length gt 6)>
        <#assign contactNumber3Mobile = contactNumberMobile.substring(0, 3) />
        <#assign contactNumber4Mobile = contactNumberMobile.substring(3, 7) />
    </#if>
</#if>

<#-- Displays the mobile phone entry -->
<div class = "personalInfoPhoneCell">
    <input type="hidden" name="mobilePhoneContactMechId" value="${telecomMobileNoContactMechId!}" />
    <div class="entry">
        <label for="PHONE_MOBILE_CONTACT">${mobilePhoneCaption!}</label>
        <span class="USER_USA USER_CAN">
            <input type="text" class="phone3" id="PHONE_MOBILE_AREA" name="PHONE_MOBILE_AREA" maxlength="3" value="${requestParameters.get("PHONE_MOBILE_AREA")!areaCodeMobile!""}" />
            <input type="hidden" id="PHONE_MOBILE_CONTACT" name="PHONE_MOBILE_CONTACT" value="${requestParameters.get("PHONE_MOBILE_CONTACT")!contactNumberMobile!""}"/>
            <input type="hidden" id="PHONE_MOBILE_REQUIRED" name="PHONE_MOBILE_REQUIRED" value="true"/>
            <input type="text" class="phone3" id="PHONE_MOBILE_CONTACT3" name="PHONE_MOBILE_CONTACT3" value="${requestParameters.get("PHONE_MOBILE_CONTACT3")!contactNumber3Mobile!""}" maxlength="3"/>
            <input type="text" class="phone4" id="PHONE_MOBILE_CONTACT4" name="PHONE_MOBILE_CONTACT4" value="${requestParameters.get("PHONE_MOBILE_CONTACT4")!contactNumber4Mobile!""}" maxlength="4"/>
            <span class="instructions" >${uiLabelMap.CellPhoneNotificationLabel}</span>
        </span>
        <span style="display:none" class="USER_OTHER">
            <input type="text" class="address" id="PHONE_MOBILE_CONTACT_OTHER" name="PHONE_MOBILE_CONTACT_OTHER" value="${requestParameters.get("PHONE_MOBILE_CONTACT_OTHER")!contactNumberMobile!""}" />
            <span class="instructions" >${uiLabelMap.CellPhoneNotificationLabel}</span>
        </span>
        <@fieldErrors fieldName="PHONE_MOBILE_AREA"/>
        <@fieldErrors fieldName="PHONE_MOBILE_CONTACT"/>
    </div>
</div>