<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "DOB_DDMM"}, true)?if_exists />
    <#if partyAttribute?has_content>
          <#assign DOB_DDMM = partyAttribute.attrValue!"">
      <#if DOB_DDMM?has_content && DOB_DDMM?length gt 4>
          <#assign dobDay= DOB_DDMM.substring(0, 2) />
          <#assign dobMonth = DOB_DDMM.substring(3,5) />
      </#if>
    </#if>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class = "${request.getAttribute("attributeClass")!}">
      <label for="DOB_DDMM"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.DOB_Caption}</label>
      <div class="entryField">
	      <select id="dobShortDayUk" name="dobShortDayUk" class="dobDay">
	      <#assign dobDay = requestParameters.dobShortDayUk!dobDay!"">
	      <#if dobDay?has_content && (dobDay?length gt 1)>
	          <option value="${dobDay?if_exists}">${dobDay?if_exists}</option>
	      </#if>
	        <option value="">${uiLabelMap.DOB_Day}</option>
	        ${screens.render("component://osafe/widget/CommonScreens.xml#ddDays")}
	      </select>
	      <select id="dobShortMonthUk" name="dobShortMonthUk" class="dobMonth">
	      <#assign dobMonth = requestParameters.dobShortMonthUk!dobMonth!"">
	      <#if dobMonth?has_content && (dobMonth?length gt 1)>
	          <option value="${dobMonth?if_exists}">${dobMonth?if_exists}</option>
	      </#if>
	        <option value="">${uiLabelMap.DOB_Month}</option>
	        ${screens.render("component://osafe/widget/CommonScreens.xml#ddMonths")}
	      </select>
	      <input type="hidden" name="DOB_DDMM_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="DOB_DDMM"/>
      </div>
</div>

