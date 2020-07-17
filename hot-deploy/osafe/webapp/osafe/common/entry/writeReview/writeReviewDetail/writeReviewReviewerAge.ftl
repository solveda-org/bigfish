<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="reviewAge"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.AgeCaption}</label>
      <div class="entryField">
	      <select name="REVIEW_AGE" id="REVIEW_AGE">
	          ${screens.render("component://osafe/widget/CommonScreens.xml#reviewAges")}
	      </select> 
	      <input type="hidden" name="REVIEW_AGE_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="REVIEW_AGE"/>
      </div>
</div>