<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>

<#-- Checks the Field Purpose -->
<#if fieldPurpose?has_content>
    <#assign phoneContactfieldPurpose = fieldPurpose />
<#else>
    <#assign phoneContactfieldPurpose = "USER" />
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
<div class = "personInfoPhoneHome">
  <input type="hidden" name="homePhoneContactMechId" value="${telecomHomeNoContactMechId!}" />
    <div class="entry">
      <label for="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT">
          <#if homePhoneRequired?has_content && homePhoneRequired == "Y"><@required/></#if>${homePhoneCaption!}
      </label>
      <span class="${phoneContactfieldPurpose?if_exists}_USA ${phoneContactfieldPurpose?if_exists}_CAN">
          <input type="text" class="phone3" id="${phoneContactfieldPurpose?if_exists}_HOME_AREA" name="${phoneContactfieldPurpose?if_exists}_HOME_AREA" maxlength="3" value="${requestParameters.get(phoneContactfieldPurpose+"_HOME_AREA")!areaCodeHome!""}" />
          <input type="hidden" id="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT" name="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT" value="${requestParameters.get(phoneContactfieldPurpose+"_HOME_CONTACT")!contactNumberHome!""}"/>
          <input type="hidden" id="${phoneContactfieldPurpose?if_exists}_HOME_REQUIRED" name="${phoneContactfieldPurpose?if_exists}_HOME_REQUIRED" value="true"/>
          <input type="text" class="phone3" id="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT3" name="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT3" value="${requestParameters.get(phoneContactfieldPurpose+"_HOME_CONTACT3")!contactNumber3Home!""}" maxlength="3" />
          <input type="text" class="phone4" id="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT4" name="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT4" value="${requestParameters.get(phoneContactfieldPurpose+"_HOME_CONTACT4")!contactNumber4Home!""}" maxlength="4" />
      </span>
      <span style="display:none" class="${phoneContactfieldPurpose?if_exists}_OTHER">
          <input type="text" class="address" id="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT_OTHER" name="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT_OTHER" value="${requestParameters.get(phoneContactfieldPurpose+"_HOME_CONTACT_OTHER")!contactNumberHome!""}" />
      </span>
      ${addressHomePhoneInfo!""}
      <@fieldErrors fieldName="${phoneContactfieldPurpose?if_exists}_HOME_AREA"/>
      <@fieldErrors fieldName="${phoneContactfieldPurpose?if_exists}_HOME_CONTACT"/>
  </div>
</div>