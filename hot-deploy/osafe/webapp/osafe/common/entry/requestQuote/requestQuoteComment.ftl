<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="content"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.CommentCaption}</label>
      <div class="entryField">
	      <textarea name="content" id="js_content" class="content characterLimit" cols="65" rows="5" maxlength="255">${parameters.content!""}</textarea>
	      <span class="js_textCounter textCounter"></span>
	      <input type="hidden" name="content_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="content"/>
      </div>
</div>