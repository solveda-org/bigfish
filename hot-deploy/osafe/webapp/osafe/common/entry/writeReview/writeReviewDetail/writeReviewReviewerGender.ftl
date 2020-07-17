<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="reviewGender"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.GenderCaption}</label>
      <div class="entryField">
	      <select name="REVIEW_GENDER" id="REVIEW_GENDER">
	         <option value=""> ${uiLabelMap.SelectOneLabel}</option>
	         <option value="M" <#if ((parameters.REVIEW_GENDER?exists && parameters.REVIEW_GENDER?string == "M"))>selected</#if>>${uiLabelMap.GenderMale}</option>
	         <option value="F" <#if ((parameters.REVIEW_GENDER?exists && parameters.REVIEW_GENDER?string == "F"))>selected</#if>>${uiLabelMap.GenderFemale}</option>
	      </select>
	      <input type="hidden" name="REVIEW_GENDER_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="REVIEW_GENDER"/>
      </div>
</div>