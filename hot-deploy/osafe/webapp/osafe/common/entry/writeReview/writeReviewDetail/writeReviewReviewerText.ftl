<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class = "writeReviewReviewerText">
    <div class="entry">
    <#assign reviewMinChar = "${REVIEW_MIN_CHAR!}" />
    <label for="reviewText"><#if reviewMinChar == "0"><#else><@required/></#if>${uiLabelMap.ReviewCaption}</label>
    <#assign reviewMaxlength = 0/>
    <#if REVIEW_MAX_CHAR?has_content && Static["org.ofbiz.base.util.UtilValidate"].isInteger(REVIEW_MAX_CHAR)>
      <#assign reviewMaxlength = Static["java.lang.Integer"].valueOf(REVIEW_MAX_CHAR)>
    </#if>
    <!-- characterLimit is linked with the Jquery To display 'nn Characters Left'-->
    <textarea rows="10" class="reviewTextField <#if reviewMaxlength &gt; 0>characterLimit</#if>" <#if reviewMaxlength &gt; 0>maxlength = "${reviewMaxlength!}"</#if> cols="50" id="REVIEW_TEXT" name="REVIEW_TEXT">${requestParameters.REVIEW_TEXT?if_exists}</textarea>
    <span class="textCounter"></span>
    <@fieldErrors fieldName="REVIEW_TEXT"/>
    </div>
</div>