<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class = "${request.getAttribute("attributeClass")!}">
      <label for="reviewTitle"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.ReviewTitleCaption}</label>
      <div class="entryField">
	      <input type="text" size="32" maxlength="100" onkeypress="return bvDisableReturn(event);" id="BVInputTitle" name="REVIEW_TITLE" value="${requestParameters.REVIEW_TITLE?if_exists}">
	      <span class="entryHelper">${uiLabelMap.TitleExampleInfo}</span>
	      <input type="hidden" name="REVIEW_TITLE_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="REVIEW_TITLE"/>
      </div>
</div>