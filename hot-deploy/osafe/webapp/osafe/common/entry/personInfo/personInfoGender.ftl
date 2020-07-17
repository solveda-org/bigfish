<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "GENDER"}, true)?if_exists />
    <#if partyAttribute?has_content>
      <#assign USER_GENDER = partyAttribute.attrValue!"">
    </#if>
</#if>

<div class ="personalInfoGender">
     <div class="entry">
      <label for="USER_GENDER"><@required/>${uiLabelMap.GenderCaption}</label>
      <select name="USER_GENDER" id="USER_GENDER">
        <option value="">${uiLabelMap.SelectOneLabel}</option>
        <option value="M" <#if ((parameters.USER_GENDER?exists && parameters.USER_GENDER?string == "M") || (USER_GENDER?exists && USER_GENDER == "M"))>selected</#if>>${uiLabelMap.CommonMale}</option>
        <option value="F" <#if ((parameters.USER_GENDER?exists && parameters.USER_GENDER?string == "F") || (USER_GENDER?exists && USER_GENDER == "F"))>selected</#if>>${uiLabelMap.CommonFemale}</option>
      </select>
      <@fieldErrors fieldName="USER_GENDER"/>
    </div>
</div>
