<#if showGuestLogin?has_content && showGuestLogin == "Y">
	<#if (shoppingCartSize > 0)>
	  <#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
	  <div class="${request.getAttribute("attributeClass")!}<#if className?exists && className?has_content> ${className}</#if>">
	    <div class="displayBox">
	      <h3>${uiLabelMap.GuestCheckoutHeading?if_exists}</h3>
	      <form method="post" action="<@ofbizUrl>validateAnonCustomerEmail${previousParams!""}</@ofbizUrl>" id="guestCustomerForm" name="guestCustomerForm" onsubmit="submitForm(this)">
	          <p class="instructions">${uiLabelMap.GuestCheckoutInfo!""}</p>
	          <ul class="displayActionList ${request.getAttribute("attributeClass")!}">
			      <li>
				     <div>
			            <label for="guestCustomerEmail">${uiLabelMap.EmailAddressShortCaption}</label>
			            <input id="guestCustomerEmail" name="USERNAME_GUEST" type="text" class="userName" value="${parameters.USERNAME_GUEST!parameters.USERNAME!""}" maxlength="200"/>
    	                <input type="submit" class="standardBtn action" name="guestCheckoutBtn" value="${uiLabelMap.GuestCheckoutBtn}" />
		             </div>
	              </li>
		      </ul>
	          <input type="hidden" name="loginUserName" value="${parameters.loginUserName!}"/>
	          <input type="hidden" name="guest" value="${parameters.guest!}" />
	      </form>
	    </div>
	  </div>
	</#if>
</#if>
