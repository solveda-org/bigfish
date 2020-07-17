<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate")/>
<#assign fieldsMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_HOME")/>
<#assign homePhonePartyContactDetails = delegator.findByAndCache("PartyContactDetailByPurpose", fieldsMap, orderByList)?if_exists/>
<#if homePhonePartyContactDetails?has_content>
     <#assign homePhonePartyContactDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(homePhonePartyContactDetails?if_exists)?if_exists/>
    <#assign homePhonePartyContactDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(homePhonePartyContactDetails?if_exists)?if_exists/>
</#if>
<#-- Splits the contactNumber -->
<#if homePhonePartyContactDetail?exists && homePhonePartyContactDetail?has_content>
    <#assign telecomHomeNoContactMechId = homePhonePartyContactDetail.contactMechId!"" />
    <#assign areaCodeHome = homePhonePartyContactDetail.areaCode?if_exists />
    <#assign contactNumberHome = homePhonePartyContactDetail.contactNumber?if_exists />
    <#if (contactNumberHome?has_content) && (contactNumberHome?length gt 6)>
        <#assign contactNumber3Home = contactNumberHome.substring(0, 3) />
        <#assign contactNumber4Home = contactNumberHome.substring(3, 7) />
    </#if>
</#if>

<#-- Displays the home phone entry -->
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
  <input type="hidden" name="homePhoneContactMechId" value="${telecomHomeNoContactMechId!}" />
    <label for="PHONE_HOME_CONTACT"><#if mandatory == "Y"><@required/></#if>${homePhoneCaption!}</label>
    <div class="entryField telephone">
      <span class="js_USER_USA js_USER_CAN">
          <input type="text" class="phone3" id="PHONE_HOME_AREA" name="PHONE_HOME_AREA" maxlength="3" value="${requestParameters.get("PHONE_HOME_AREA")!areaCodeHome!""}" />
          <input type="hidden" id="PHONE_HOME_CONTACT" name="PHONE_HOME_CONTACT" value="${requestParameters.get("PHONE_HOME_CONTACT")!contactNumberHome!""}"/>
          <input type="hidden" id="PHONE_HOME_MANDATORY" name="PHONE_HOME_MANDATORY" value="${mandatory}"/>
          <input type="text" class="phone3" id="PHONE_HOME_CONTACT3" name="PHONE_HOME_CONTACT3" value="${requestParameters.get("PHONE_HOME_CONTACT3")!contactNumber3Home!""}" maxlength="3" />
          <input type="text" class="phone4" id="PHONE_HOME_CONTACT4" name="PHONE_HOME_CONTACT4" value="${requestParameters.get("PHONE_HOME_CONTACT4")!contactNumber4Home!""}" maxlength="4" />
          <span class="entryHelper" >${uiLabelMap.HomePhoneNotificationLabel}</span>
      </span>
      <span style="display:none" class="js_USER_OTHER">
          <input type="text" class="phone10" id="PHONE_HOME_CONTACT_OTHER" name="PHONE_HOME_CONTACT_OTHER" value="${requestParameters.get("PHONE_HOME_CONTACT_OTHER")!contactNumberHome!""}" />
          <span class="entryHelper" >${uiLabelMap.HomePhoneNotificationLabel}</span>
          <input type="hidden" name="PHONE_HOME_CONTACT_OTHER_MANDATORY" value="${mandatory}"/>
      </span>
      <span>${addressHomePhoneInfo!""}</span>
      <@fieldErrors fieldName="PHONE_HOME_AREA"/>
      <@fieldErrors fieldName="PHONE_HOME_CONTACT"/>
    </div>
</div>