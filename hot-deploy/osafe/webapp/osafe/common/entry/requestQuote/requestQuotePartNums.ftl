<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="partNumber"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.PartNumberCaption}</label>
      <div class="entryField">
	      <textarea name="partNumber" id="js_partNumber" class="content characterLimit" cols="65" rows="5" maxlength="255">${parameters.partNumber!""}</textarea>
	      <!-- <span class="js_textCounter textCounter"></span> -->
	      <input type="hidden" name="partNumber_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="partNumber"/>
      </div>
</div>