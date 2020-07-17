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
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <input type="hidden" name="mobilePhoneContactMechId" value="${telecomMobileNoContactMechId!}" />
      <label for="PHONE_MOBILE_CONTACT"><#if mandatory == "Y"><@required/></#if>${mobilePhoneCaption!}</label>
      <div class="entryField telephone">
        <span class="js_USER_USA js_USER_CAN">
            <input type="text" class="phone3" id="PHONE_MOBILE_AREA" name="PHONE_MOBILE_AREA" maxlength="3" value="${requestParameters.get("PHONE_MOBILE_AREA")!areaCodeMobile!""}" />
            <input type="hidden" id="PHONE_MOBILE_CONTACT" name="PHONE_MOBILE_CONTACT" value="${requestParameters.get("PHONE_MOBILE_CONTACT")!contactNumberMobile!""}"/>
            <input type="hidden" id="PHONE_MOBILE_MANDATORY" name="PHONE_MOBILE_MANDATORY" value="${mandatory}"/>
            <input type="text" class="phone3" id="PHONE_MOBILE_CONTACT3" name="PHONE_MOBILE_CONTACT3" value="${requestParameters.get("PHONE_MOBILE_CONTACT3")!contactNumber3Mobile!""}" maxlength="3"/>
            <input type="text" class="phone4" id="PHONE_MOBILE_CONTACT4" name="PHONE_MOBILE_CONTACT4" value="${requestParameters.get("PHONE_MOBILE_CONTACT4")!contactNumber4Mobile!""}" maxlength="4"/>
            <span class="entryHelper" >${uiLabelMap.CellPhoneNotificationLabel}</span>
        </span>
        <span style="display:none" class="js_USER_OTHER">
            <input type="text" class="phone10" id="PHONE_MOBILE_CONTACT_OTHER" name="PHONE_MOBILE_CONTACT_OTHER" value="${requestParameters.get("PHONE_MOBILE_CONTACT_OTHER")!contactNumberMobile!""}" />
            <span class="entryHelper" >${uiLabelMap.CellPhoneNotificationLabel}</span>
            <input type="hidden" name="PHONE_MOBILE_CONTACT_OTHER_MANDATORY" value="${mandatory}"/>
        </span>
        <@fieldErrors fieldName="PHONE_MOBILE_AREA"/>
        <@fieldErrors fieldName="PHONE_MOBILE_CONTACT"/>
      </div>
</div>