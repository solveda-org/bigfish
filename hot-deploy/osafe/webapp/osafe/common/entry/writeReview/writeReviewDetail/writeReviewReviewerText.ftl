<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <#assign reviewMinChar = "${REVIEW_MIN_CHAR!}" />
    <label for="reviewText"><#if reviewMinChar == "0"><#else><#if mandatory == "Y"><@required/></#if></#if>${uiLabelMap.ReviewCaption}</label>
    <#assign reviewMaxlength = 0/>
    <#if REVIEW_MAX_CHAR?has_content && Static["org.ofbiz.base.util.UtilValidate"].isInteger(REVIEW_MAX_CHAR)>
      <#assign reviewMaxlength = Static["java.lang.Integer"].valueOf(REVIEW_MAX_CHAR)>
    </#if>
    <div class="entryField">
	    <!-- characterLimit is linked with the Jquery To display 'nn Characters Left'-->
	    <textarea rows="10" class="reviewTextField <#if reviewMaxlength &gt; 0>characterLimit</#if>" <#if reviewMaxlength &gt; 0>maxlength ="${reviewMaxlength!}"</#if> cols="35" id="REVIEW_TEXT" name="REVIEW_TEXT">${requestParameters.REVIEW_TEXT?if_exists}</textarea>
	    <span class="js_textCounter textCounter"></span>
	    <input type="hidden" name="REVIEW_TEXT_MANDATORY" value="${mandatory}"/>
	    <@fieldErrors fieldName="REVIEW_TEXT"/>
    </div>
</div>