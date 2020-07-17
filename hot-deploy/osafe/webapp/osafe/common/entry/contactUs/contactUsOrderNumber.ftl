<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="orderIdNumber"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.OrderNumberCaption}</label>
      <div class="entryField">
	      <input type="text"  maxlength="20" name="orderIdNumber" id="orderIdNumber" value="${parameters.orderIdNumber!""}"/>
	      <input type="hidden" name="orderIdNumber_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="orderIdNumber"/>
      </div>
</div>