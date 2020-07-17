<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class = "writeReviewReviewerPrivateNote">
    <div class="entry">
    <label for="privateNote">${uiLabelMap.PrivateCommentCaption}</label>
    <!-- characterLimit is linked with the Jquery To display 'nn Characters Left'-->
    <textarea rows="10" id= content class="privateNoteField characterLimit" maxlength = "${privateNoteMaxlength!}" cols="50" name="REVIEW_PRIVATE_NOTE">${requestParameters.REVIEW_PRIVATE_NOTE?if_exists}</textarea>
    <span class="textCounter"></span>
    <@fieldErrors fieldName="REVIEW_PRIVATE_NOTE"/>
    </div>
</div>