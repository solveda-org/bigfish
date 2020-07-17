<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if person?has_content>
  <#assign gender= person.gender!""/>
</#if>
<div class ="personInfoGender">
     <div class="entry">
      <label for="USER_GENDER"><@required/>${uiLabelMap.GenderCaption}</label>
      <select name="USER_GENDER" id="USER_GENDER">
        <option value="">${uiLabelMap.SelectOneLabel}</option>
        <option value="M" <#if ((requestParameters.USER_GENDER?exists && requestParameters.USER_GENDER == "M") || gender?if_exists == "M")>selected</#if>>${uiLabelMap.CommonMale}</option>
        <option value="F" <#if ((requestParameters.USER_GENDER?exists && requestParameters.USER_GENDER == "F") || gender?if_exists == "F")>selected</#if>>${uiLabelMap.CommonFemale}</option>
      </select>
      <@fieldErrors fieldName="USER_GENDER"/>
    </div>
</div>
