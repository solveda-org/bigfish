<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class = "writeReviewReviewerTitle">
   <h3>${uiLabelMap.ShareOpinionHeading}</h3>
    <div class="entry">
      <label for="reviewTitle"><@required/>${uiLabelMap.ReviewTitleCaption}</label>
      <input type="text" size="32" maxlength="100" onkeypress="return bvDisableReturn(event);" id="BVInputTitle" name="REVIEW_TITLE" value="${requestParameters.REVIEW_TITLE?if_exists}">
      <span class="instructions">${uiLabelMap.TitleExampleInfo}</span>
      <@fieldErrors fieldName="REVIEW_TITLE"/>
    </div>
</div>