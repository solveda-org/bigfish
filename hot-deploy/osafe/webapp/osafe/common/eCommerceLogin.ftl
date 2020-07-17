<script type="text/javascript">
    function submitForm(form) {
        form.loginUserName.value = document.loginform.USERNAME.value;
    }
</script>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>
<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_AS_GUEST") && parameters.guest?has_content && parameters.guest == "guest">
    <#if (shoppingCartSize > 0)>
        <#assign className = "withGuestCheckoutOption" />
    </#if>
</#if>

<div id="returningCustomer" class="displayBox<#if className?exists && className?has_content> ${className}</#if>">
    <h3>${uiLabelMap.ReturningCustomerLoginHeading?if_exists}</h3>
    <form method="post" action="<@ofbizUrl>validateLogin${previousParams!""}</@ofbizUrl>" id="loginform"  name="loginform">
        <fieldset>
          <div class="infoAndEntrysec">
            <p>${uiLabelMap.ReturningCustomerLoginInfo!""}</p>
            <div class="entry">
                <label for="returnCustomerEmail">${uiLabelMap.EmailAddressShortCaption}</label>
                <input id="returnCustomerEmail" name="USERNAME" type="text" class="userName" value="<#if requestUsername?has_content>${requestUsername}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}<#elseif parameters.USERNAME?exists>${parameters.USERNAME}<#elseif parameters.loginUserName?has_content>${parameters.loginUserName}</#if>" maxlength="200"/>
            </div>
            <div class="entry">
                <label for="password">${uiLabelMap.PasswordCaption}</label>
                <input id="password" name="PASSWORD" type="password" class="password" value="" maxlength="50" />
            </div>
          </div>
            <div class="entryButtons">
                <input type="submit" class="standardBtn action" name="signInBtn" value="${uiLabelMap.SignInBtn}"/>
            </div>
            <input type="hidden" name="guest" value="${parameters.guest!}" />
            <input type="hidden" name="review" value="${parameters.review!}" />
            <a id="forgottenPassword" href="<@ofbizUrl>forgotPassword</@ofbizUrl>">Forgotten your password?</a>
            <p class="loginTip">${uiLabelMap.UserNameIsEmailInfo}</p>
         </fieldset>
    </form>
</div>

<div id="newCustomer" class="displayBox<#if className?exists && className?has_content> ${className}</#if>">
    <h3>${uiLabelMap.NotRegisteredHeading?if_exists}</h3>
    <form method="post" action="<@ofbizUrl>validateNewCustomerEmail${previousParams!""}</@ofbizUrl>" id="newCustomerForm" name="newCustomerForm" onsubmit="submitForm(this)">
        <fieldset>
          <div class="infoAndEntrysec">
            <p>${uiLabelMap.NotRegisteredInfo!""}</p>
            <div class="entry">
                <label for="newCustomerEmail">${uiLabelMap.EmailAddressShortCaption}</label>
                <input id="newCustomerEmail" name="USERNAME_NEW" type="text" class="userName" value="${parameters.USERNAME_NEW!parameters.USERNAME!""}" maxlength="200"/>
            </div>
          </div>
          <input type="hidden" name="loginUserName" value="${parameters.loginUserName!}"/>
          <input type="hidden" name="guest" value="${parameters.guest!}" />
          <input type="hidden" name="review" value="${parameters.review!}" />
            <div class="entryButtons">
                <input type="submit" class="standardBtn action" name="continueBtn" value="${uiLabelMap.ContinueBtn}" />
            </div>
         </fieldset>
    </form>
</div>

<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_AS_GUEST") && parameters.guest?has_content && parameters.guest == "guest">
<#if (shoppingCartSize > 0)>
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
  <div id="guestCheckoutBox" class="displayBox<#if className?exists && className?has_content> ${className}</#if>">
      <h3>${uiLabelMap.GuestCheckoutHeading?if_exists}</h3>
    <form method="post" action="<@ofbizUrl>validateAnonCustomerEmail${previousParams!""}</@ofbizUrl>" id="guestCustomerForm" name="guestCustomerForm" onsubmit="submitForm(this)">
      <fieldset>
        <div class="infoAndEntrysec">
          <p>${uiLabelMap.GuestCheckoutInfo!""}</p>
          <div class="entry">
            <label for="guestCustomerEmail">${uiLabelMap.EmailAddressShortCaption}</label>
            <input id="guestCustomerEmail" name="USERNAME_GUEST" type="text" class="userName" value="${parameters.USERNAME_GUEST!parameters.USERNAME!""}" maxlength="200"/>
          </div>
        </div>
        <input type="hidden" name="loginUserName" value="${parameters.loginUserName!}"/>
        <input type="hidden" name="guest" value="${parameters.guest!}" />
        <div class="entryButtons">
          <input type="submit" class="standardBtn action" name="guestCheckoutBtn" value="${uiLabelMap.GuestCheckoutBtn}" />
        </div>
      </fieldset>
    </form>
  </div>
</#if>
</#if>
