<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label><#if mandatory == "Y"><@required/></#if>${uiLabelMap.ReasonForContactCaption}</label>
      <div class="entryField">
	      <select name="contactReason" id="contactReason">
	          ${screens.render("component://osafe/widget/CommonScreens.xml#contactReasonType")}
	      </select>
	      <input type="hidden" name="contactReason_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="contactReason"/>
      </div>
</div>