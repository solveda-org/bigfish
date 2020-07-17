<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <label for="privateNote"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.PrivateCommentCaption}</label>
    <div class="entryField">
	    <!-- characterLimit is linked with the Jquery To display 'nn Characters Left'-->
	    <textarea rows="10" id= content class="privateNoteField characterLimit" maxlength = "${privateNoteMaxlength!}" cols="35" name="REVIEW_PRIVATE_NOTE">${requestParameters.REVIEW_PRIVATE_NOTE?if_exists}</textarea>
	    <span class="js_textCounter textCounter"></span>
	    <input type="hidden" name="REVIEW_PRIVATE_NOTE_MANDATORY" value="${mandatory}"/>
	    <@fieldErrors fieldName="REVIEW_PRIVATE_NOTE"/>
    </div>
</div>