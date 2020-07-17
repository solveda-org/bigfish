<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class = "writeReviewReviewerNickname">
    <div class = "entry">
        <label for="reviewNickname"><@required/>${uiLabelMap.YourNickNameCaption}</label>
        <input type="text" size="32" maxlength="100" onkeypress="return bvDisableReturn(event);" id="nickTextField" name="REVIEW_NICK_NAME" value="${requestParameters.REVIEW_NICK_NAME!prevNickName!""}">
        <span class="instructions">${uiLabelMap.NicknameExampleInfo}</span>
    </div>
</div>