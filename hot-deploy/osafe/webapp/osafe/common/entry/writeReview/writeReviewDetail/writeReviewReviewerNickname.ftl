<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class = "writeReviewReviewerNickname">
    <label for="reviewNickname"><@required/>${uiLabelMap.YourNicknameCaption}</label>
    <input type="text" size="32" maxlength="100" onkeypress="return bvDisableReturn(event);" id="nickTextField" name="REVIEW_NICK_NAME" value="${requestParameters.REVIEW_NICK_NAME!prevNickName!""}">
    <span class="instructions">${uiLabelMap.NicknameExampleInfo}</span>
</div>