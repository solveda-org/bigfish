<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div id="forgotPasswordEntry" class="displayBox">
<div class="displayBoxHeader">
 <span class="displayBoxHeaderCaption">${uiLabelMap.CommonForgotYourPassword?if_exists}?</span>
</div>
        <fieldset>
            <p>
                ${uiLabelMap.ForgotPasswordInfo!""}
            </p>
            <div class="entry">
                <label for="username">${uiLabelMap.EmailAddressShortCaption}</label>
                <input type="text"  maxlength="100" class="userName" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>" maxlength="255"/>
                <@fieldErrors fieldName="USERNAME"/>
            </div>
            <input type="hidden" name="EMAIL_PASSWORD" value="Y"/>
            <input type="hidden" name="JavaScriptEnabled" value="N"/>
         </fieldset>
</div>
