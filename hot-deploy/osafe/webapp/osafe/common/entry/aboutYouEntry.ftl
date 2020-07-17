<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if person?has_content>
    <#assign partyId= person.partyId!""/>
    <#assign gender= person.gender!""/>
    <#assign firstName= person.firstName!""/>
    <#assign lastName= person.lastName!""/>
</#if>
<div id="aboutYouEntry" class="displayBox">
<div class="displayBoxHeader">
<span class="displayBoxHeaderCaption"><#if isCheckoutPage?exists && isCheckoutPage! == "true">${uiLabelMap.CustomerPersonalHeading}<#else>${uiLabelMap.AboutYouHeading}</#if></span>
</div>
<p class="instructions">${StringUtil.wrapString(uiLabelMap.EditCustomerInstructionsInfo)}</p>
  <fieldset class="col">

     <input type="hidden" name="partyId" value="${partyId!""}"/>
     <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
     <#if contactMech?has_content>
       <#assign contactMechId=contactMech.contactMechId!"">
       <input type="hidden" name="contactMechId" value="${contactMechId!""}"/>
     </#if>
     <#if postalAddressData?has_content>
       <#assign attnName=postalAddressData.attnName!"">
     </#if>
    <div class="entry">
      <label for="USER_FIRST_NAME"><@required/>${uiLabelMap.FirstNameCaption}</label>
      <input type="text" maxlength="100" name="USER_FIRST_NAME" id="USER_FIRST_NAME" value="${requestParameters.USER_FIRST_NAME!firstName!""}" />
      <@fieldErrors fieldName="USER_FIRST_NAME"/>
    </div>

    <input type="hidden" name="USER_MIDDLE_NAME" value=""/>

    <div class="entry">
      <label for="USER_LAST_NAME"><@required/>${uiLabelMap.LastNameCaption}</label>
      <input type="text" maxlength="100" name="USER_LAST_NAME" id="USER_LAST_NAME" value="${requestParameters.USER_LAST_NAME!lastName!""}" />
      <@fieldErrors fieldName="USER_LAST_NAME"/>
    </div>

    <div class="entry">
      <label for="USER_GENDER"><@required/>${uiLabelMap.GenderCaption}</label>
      <select name="USER_GENDER" id="USER_GENDER">
        <option value="">${uiLabelMap.SelectOneLabel}</option>
        <option value="M" <#if ((requestParameters.USER_GENDER?exists && requestParameters.USER_GENDER == "M") || gender?if_exists == "M")>selected</#if>>${uiLabelMap.CommonMale}</option>
        <option value="F" <#if ((requestParameters.USER_GENDER?exists && requestParameters.USER_GENDER == "F") || gender?if_exists == "F")>selected</#if>>${uiLabelMap.CommonFemale}</option>
      </select>
      <@fieldErrors fieldName="USER_GENDER"/>
    </div>
  </fieldset>
</div>
