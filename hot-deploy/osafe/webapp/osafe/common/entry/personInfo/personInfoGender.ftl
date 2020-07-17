<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "GENDER"}, true)?if_exists />
    <#if partyAttribute?has_content>
      <#assign USER_GENDER = partyAttribute.attrValue!"">
    </#if>
<#-- for Facebook login -->
<#elseif parameters.fbGender?has_content>
	<#if parameters.fbGender == "male">
		<#assign USER_GENDER = "M"/>
	</#if>
	<#if parameters.fbGender == "female">
		<#assign USER_GENDER = "F"/>
	</#if>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="USER_GENDER"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.GenderCaption}</label>
      <div class="entryField">
	      <select name="USER_GENDER" id="USER_GENDER">
	        <option value="">${uiLabelMap.SelectOneLabel}</option>
	        <option value="M" <#if ((parameters.USER_GENDER?exists && parameters.USER_GENDER?string == "M") || (USER_GENDER?exists && USER_GENDER == "M"))>selected</#if>>${uiLabelMap.GenderMale}</option>
	        <option value="F" <#if ((parameters.USER_GENDER?exists && parameters.USER_GENDER?string == "F") || (USER_GENDER?exists && USER_GENDER == "F"))>selected</#if>>${uiLabelMap.GenderFemale}</option>
	      </select>
	      <input type="hidden" name="USER_GENDER_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="USER_GENDER"/>
      </div>
</div>
