<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "DOB_MMDDYYYY"}, true)?if_exists />
    <#if partyAttribute?has_content>
      <#assign DOB_MMDDYYYY = partyAttribute.attrValue!"">
      <#if DOB_MMDDYYYY?has_content && (DOB_MMDDYYYY?length gt 9)>
          <#assign dobMonth= DOB_MMDDYYYY.substring(0, 2) />
          <#assign dobDay = DOB_MMDDYYYY.substring(3,5) />
          <#assign dobYear = DOB_MMDDYYYY.substring(6,10) />
      </#if>
    </#if>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="DOB_MMDDYYYY"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.DOB_Caption}</label>
      <div class="entryField">
	      <select id="dobLongMonthUs" name="dobLongMonthUs" class="dobMonth">
	      <#assign dobMonth = requestParameters.dobLongMonthUs!dobMonth!"">
	      <#if dobMonth?has_content && (dobMonth?length gt 1)>
	          <option value="${dobMonth?if_exists}">${dobMonth?if_exists}</option>
	      </#if>
	        <option value="">${uiLabelMap.DOB_Month}</option>
	        ${screens.render("component://osafe/widget/CommonScreens.xml#ddMonths")}
	      </select>
	      <select id="dobLongDayUs" name="dobLongDayUs" class="dobDay">
	      <#assign dobDay = requestParameters.dobLongDayUs!dobDay!"">
	      <#if dobDay?has_content && (dobDay?length gt 1)>
	          <option value="${dobDay?if_exists}">${dobDay?if_exists}</option>
	      </#if>
	        <option value="">${uiLabelMap.DOB_Day}</option>
	        ${screens.render("component://osafe/widget/CommonScreens.xml#ddDays")}
	      </select>
	      <select id="dobLongYearUs" name="dobLongYearUs" class="dobYear">
	      <#assign dobYear = requestParameters.dobLongYearUs!dobYear!"">
	      <#if dobYear?has_content && (dobYear?length gt 1)>
	          <option value="${dobYear?if_exists}">${dobYear?if_exists}</option>
	      </#if>
	        <option value="">${uiLabelMap.DOB_Year}</option>
	        ${screens.render("component://osafe/widget/CommonScreens.xml#ddYears")}
	      </select>
	      <input type="hidden" name="DOB_MMDDYYYY_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="DOB_MMDDYYYY"/>
      </div>
</div>

