<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="reviewLocation"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.LocationCaption}</label>
      <div class="entryField">
	      <input type="text" size="32" maxlength="100" name="REVIEW_LOCATION" value="${requestParameters.REVIEW_LOCATION?if_exists}"> 
	      <span class="entryHelper">${uiLabelMap.LocationExampleInfo}</span>
	      <input type="hidden" name="REVIEW_LOCATION_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="REVIEW_LOCATION"/>
      </div>
</div>