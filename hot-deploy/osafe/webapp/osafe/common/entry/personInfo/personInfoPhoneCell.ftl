<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>

<#-- Checks the Field Purpose -->
<#if fieldPurpose?has_content>
    <#assign phoneContactfieldPurpose = fieldPurpose />
<#else>
    <#assign phoneContactfieldPurpose = "USER" />
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
<div class = "personInfoPhoneCell">
    <input type="hidden" name="mobilePhoneContactMechId" value="${telecomMobileNoContactMechId!}" />
    <div class="entry">
        <label for="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT">${mobilePhoneCaption!}</label>
        <span class="${phoneContactfieldPurpose?if_exists}_USA ${phoneContactfieldPurpose?if_exists}_CAN">
            <input type="text" class="phone3" id="${phoneContactfieldPurpose?if_exists}_MOBILE_AREA" name="${phoneContactfieldPurpose?if_exists}_MOBILE_AREA" maxlength="3" value="${requestParameters.get(phoneContactfieldPurpose+"_MOBILE_AREA")!areaCodeMobile!""}" />
            <input type="hidden" id="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT" name="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT" value="${requestParameters.get(phoneContactfieldPurpose+"_MOBILE_CONTACT")!contactNumberMobile!""}"/>
            <input type="text" class="phone3" id="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT3" name="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT3" value="${requestParameters.get(phoneContactfieldPurpose+"_MOBILE_CONTACT3")!contactNumber3Mobile!""}" maxlength="3"/>
            <input type="text" class="phone4" id="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT4" name="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT4" value="${requestParameters.get(phoneContactfieldPurpose+"_MOBILE_CONTACT4")!contactNumber4Mobile!""}" maxlength="4"/>
        </span>
        <span style="display:none" class="${phoneContactfieldPurpose?if_exists}_OTHER">
            <input type="text" class="address" id="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT_OTHER" name="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT_OTHER" value="${requestParameters.get(phoneContactfieldPurpose+"_MOBILE_CONTACT_OTHER")!contactNumberMobile!""}" />
        </span>
        <@fieldErrors fieldName="${phoneContactfieldPurpose?if_exists}_MOBILE_AREA"/>
        <@fieldErrors fieldName="${phoneContactfieldPurpose?if_exists}_MOBILE_CONTACT"/>
    </div>
</div>