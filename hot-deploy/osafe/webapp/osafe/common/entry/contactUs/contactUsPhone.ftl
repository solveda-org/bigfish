<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<#assign countryDefault=COUNTRY_DEFAULT!""/>
<#if countryDefault?has_content>
 <#assign countryDefault = countryDefault.toUpperCase()/> 
</#if>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="contactPhoneContact"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.ContactPhoneCaption}</label>
      <div class="entryField telephone">
	      <#if countryDefault?has_content && (countryDefault =="USA" || countryDefault="CAN")>
		      <input type="text" class="phone3" name="contactPhoneArea" value="${parameters.contactPhoneArea!userPhoneHomeAreaCode!""}" maxlength="3"/>
		      <input type="text" class="phone3" id="contactPhoneContact3" name="contactPhoneContact3" value="${parameters.contactPhoneContact3!userPhoneHomeNumber3!""}" maxlength="3"/>
		      <input type="text" class="phone4" id="contactPhoneContact4" name="contactPhoneContact4" value="${parameters.contactPhoneContact4!userPhoneHomeNumber4!""}" maxlength="4"/>
	          <input type="hidden" name="contactPhoneNumber_MANDATORY" value="${mandatory}"/>
	      <#else>
		      <input type="text" class="phone10" name="contactPhoneNumber" maxlength="100" value="${parameters.contactPhoneNumber!userPhoneHomeNumber!""}" />
	          <input type="hidden" name="contactPhoneNumber_MANDATORY" value="${mandatory}"/>
	      </#if>
	      <@fieldErrors fieldName="contactPhoneContact"/>
      </div>
</div>