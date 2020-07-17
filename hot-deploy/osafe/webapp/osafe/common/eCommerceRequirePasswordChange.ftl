<#assign username = ""/>
<#if requestParameters.USERNAME?has_content>
  <#assign username = requestParameters.USERNAME/>
</#if>

<div id="changePassword" class="displayBox">
    <h3>${uiLabelMap.CommonPasswordChange?if_exists}</h3>
      <form method="post" action="<@ofbizUrl>eCommerceLoginChangePassword${previousParams}</@ofbizUrl>" name="loginform" class="entryForm">
          <input type="hidden" name="requirePasswordChange" value="Y"/>
          <input type="hidden" name="USERNAME" value="${username}" maxlength="200"/>
            <div class="entry userEmail passwordChangeUserEmail">
                <label for="USERNAME">${uiLabelMap.UserNameCaption}</label>
                <input id="usernameReadOnly" name="USERNAMEReadOnly" type="text" class="userName readonly" value="${parameters.USERNAME!username!""}" maxlength="200" readonly="readonly" disabled="disabled"/>
            </div>
            <div class="entry userPassword passwordChangeUserPassword">
                <label for="PASSWORD">${uiLabelMap.PasswordCaption}</label>
                <input type="password" class="password" id="PASSWORD" name="PASSWORD" value="${requestParameters.PASSWORD!""}" maxlength="50"/>
            </div>
            <div class="entry newPassword passwordChangeNewPassword">
                <label for="newPassword">${uiLabelMap.NewPasswordCaption}</label>
                <input type="password" class="password" id="newPassword" name="newPassword" value="" maxlength="50"/>
            </div>
            <div class="entry verifyPassword passwordChangeVerifyPassword">
                <label for="newPasswordVerify">${uiLabelMap.ConfirmPasswordCaption}</label>
                <input type="password" class="password" id="newPasswordVerify" name="newPasswordVerify" value="" maxlength="50"/>
            </div>
            <div class="container button">
                <input type="submit" class="standardBtn action" name="continueBtn" value="${uiLabelMap.ContinueBtn}" />
            </div>
      </form>
</div>

