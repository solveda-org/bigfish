<div class="${request.getAttribute("attributeClass")!}<#if className?exists && className?has_content> ${className}</#if>">
  <div class="displayBox">
    <h3>${uiLabelMap.NotRegisteredHeading?if_exists}</h3>
    <form method="post" action="<@ofbizUrl>validateNewCustomerEmail${previousParams!""}</@ofbizUrl>" id="newCustomerForm" name="newCustomerForm" onsubmit="submitForm(this)">
          <p class="instructions">${uiLabelMap.NotRegisteredInfo!""}</p>
          <ul class="displayActionList">
		      <li>
			       <div>
			              <label for="newCustomerEmail">${uiLabelMap.EmailAddressShortCaption}</label>
			              <input id="newCustomerEmail" name="USERNAME_NEW" type="text" class="userName" value="${parameters.USERNAME_NEW!parameters.USERNAME!""}" maxlength="200"/>
	                      <input type="submit" class="standardBtn action" name="continueBtn" value="${uiLabelMap.ContinueBtn}" />
		           </div>
	          </li>
		  </ul>
          <input type="hidden" name="loginUserName" value="${parameters.loginUserName!}"/>
          <input type="hidden" name="guest" value="${parameters.guest!}" />
          <input type="hidden" name="review" value="${parameters.review!}" />
    </form>
  </div>
</div>