<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="reviewCustom01"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.ReviewCustom01Caption}</label>
      <div class="entryField">
	      <select name="REVIEW_CUSTOM_01" id="REVIEW_CUSTOM_01">
	          ${screens.render("component://osafe/widget/CommonScreens.xml#reviewCustom01")}
	      </select> 
	      <input type="hidden" name="REVIEW_CUSTOM_01_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="REVIEW_CUSTOM_01"/>
      </div>
</div>