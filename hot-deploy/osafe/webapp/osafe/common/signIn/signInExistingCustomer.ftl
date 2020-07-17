<div class="${request.getAttribute("attributeClass")!}<#if className?exists && className?has_content> ${className}</#if>">
  <div class="displayBox">
    <h3>${uiLabelMap.ReturningCustomerLoginHeading?if_exists}</h3>
    <form method="post" action="<@ofbizUrl>validateLogin${previousParams!""}</@ofbizUrl>" id="loginform"  name="loginform">
            <p class="instructions">${uiLabelMap.ReturningCustomerLoginInfo!""}</p>
          <ul class="displayActionList ${request.getAttribute("attributeClass")!}">
		      <li>
			     <div>
                      <label for="returnCustomerEmail">${uiLabelMap.EmailAddressShortCaption}</label>
                      <input id="returnCustomerEmail" name="USERNAME" type="text" class="userName" value="<#if requestUsername?has_content>${requestUsername}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}<#elseif parameters.USERNAME?exists>${parameters.USERNAME}<#elseif parameters.loginUserName?has_content>${parameters.loginUserName}</#if>" maxlength="200"/>
                 </div>
              </li>
		      <li>
			     <div>
		              <label for="password">${uiLabelMap.PasswordCaption}</label>
		              <input id="password" name="PASSWORD" type="password" class="password" value="" maxlength="50" />
                      <input type="submit" class="standardBtn action" name="signInBtn" value="${uiLabelMap.SignInBtn}"/>
                 </div>
              </li>
		      <li>
			     <div>
			            <a id="forgottenPassword" href="<@ofbizUrl>forgotPassword</@ofbizUrl>"><span>${uiLabelMap.ForgotPasswordLabel}</span></a>
			            <p class="loginTip">${uiLabelMap.UserNameIsEmailInfo}</p>
                 </div>
              </li>
          </ul>
          <input type="hidden" name="guest" value="${parameters.guest!}" />
          <input type="hidden" name="review" value="${parameters.review!}" />
    </form>
  </div>
</div>
