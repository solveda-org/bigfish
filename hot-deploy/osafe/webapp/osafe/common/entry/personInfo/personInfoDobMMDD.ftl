<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "DOB_MMDD"}, true)?if_exists />
    <#if partyAttribute?has_content>
          <#assign DOB_MMDD = partyAttribute.attrValue!"">
      <#if DOB_MMDD?has_content && DOB_MMDD?length gt 4>
          <#assign dobMonth= DOB_MMDD.substring(0, 2) />
          <#assign dobDay = DOB_MMDD.substring(3,5) />
      </#if>
    </#if>
</#if>
<div class = "personalInfoDateOfBirthMMDD">
 <div class="entry">
      <label for="DOB_MMDD"><@required/>${uiLabelMap.DOB_Caption}</label>
      <select id="dobShortMonth" name="dobShortMonth" class="dobMonth">
      <#assign dobMonth = requestParameters.dobShortMonth!dobMonth!"">
      <#if dobMonth?has_content && (dobMonth?length gt 1)>
          <option value="${dobMonth?if_exists}">${dobMonth?if_exists}</option>
      </#if>
        <option value="">${uiLabelMap.DOB_Month}</option>
        ${screens.render("component://osafe/widget/CommonScreens.xml#ddMonths")}
      </select>
      <select id="dobShortDay" name="dobShortDay" class="dobDay">
      <#assign dobDay = requestParameters.dobShortDay!dobDay!"">
      <#if dobDay?has_content && (dobDay?length gt 1)>
          <option value="${dobDay?if_exists}">${dobDay?if_exists}</option>
      </#if>
        <option value="">${uiLabelMap.DOB_Day}</option>
        ${screens.render("component://osafe/widget/CommonScreens.xml#ddDays")}
      </select>
      <@fieldErrors fieldName="DOB_MMDD"/>
  </div>
</div>

