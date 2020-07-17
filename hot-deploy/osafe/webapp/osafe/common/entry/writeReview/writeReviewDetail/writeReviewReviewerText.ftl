<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class = "writeReviewReviewerText">
    <div class="entry">
    <#assign reviewMinChar = "${REVIEW_MIN_CHAR!}" />
    <label for="reviewText"><#if reviewMinChar == "0"><#else><@required/></#if>${uiLabelMap.ReviewCaption}</label>
    <textarea rows="10" class="reviewTextField" cols="50" id="REVIEW_TEXT" name="REVIEW_TEXT">${requestParameters.REVIEW_TEXT?if_exists}</textarea>
    <@fieldErrors fieldName="REVIEW_TEXT"/>
    </div>
</div>