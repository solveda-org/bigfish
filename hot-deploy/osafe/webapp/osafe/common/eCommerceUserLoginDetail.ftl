<div class="container updateLogin myAccountUpdateLogin">
    <h3>${uiLabelMap.LoginInformationHeading} <span class="headerHelper">${StringUtil.wrapString(uiLabelMap.EmailAddressLoginInfo)}</span></h3>
    <ul class="displayActionList emailAddress updateLoginEmailAddress">
      <li>
        <div>
            <label for= "CUSTOMER_EMAIL">${uiLabelMap.EmailAddressCaption}</label>
            <span>${userLoginId!}</span>
	        <a class="standardBtn update" href="<@ofbizUrl>eCommerceEditCustomerLogin</@ofbizUrl>">${uiLabelMap.CommonEdit}</a>
	    </div>
	  </li>
    </ul>
    <ul class="displayActionList password updateLoginPassword">
      <li>
        <div>
            <label for="PASSWORD">${uiLabelMap.PasswordCaption}</label>
            <span>******</span>
            <a class="standardBtn update" href="<@ofbizUrl>eCommerceEditCustomerPassword</@ofbizUrl>">${uiLabelMap.CommonEdit}</a>
        </div>
      </li>
    </ul>
    <div class="container button">
	    <#if backAction?exists && backAction?has_content>
	        <a class="standardBtn negative" href="<@ofbizUrl>${backAction!}</@ofbizUrl>">${uiLabelMap.CommonBack}</a>
	    </#if>
	</div>
</div>