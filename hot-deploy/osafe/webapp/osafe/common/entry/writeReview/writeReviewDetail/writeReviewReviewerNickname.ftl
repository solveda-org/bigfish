<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="reviewNickname"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.YourNickNameCaption}</label>
      <div class="entryField">
	      <input type="text" size="32" maxlength="100" onkeypress="return bvDisableReturn(event);" id="nickTextField" name="REVIEW_NICK_NAME" value="${requestParameters.REVIEW_NICK_NAME!prevNickName!""}">
	      <span class="entryHelper">${uiLabelMap.NicknameExampleInfo}</span>
	      <input type="hidden" name="REVIEW_NICK_NAME_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="REVIEW_NICK_NAME"/>
      </div>
</div>