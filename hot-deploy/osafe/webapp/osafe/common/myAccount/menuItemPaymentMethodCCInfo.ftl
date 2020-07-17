<#if (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
<div class="${request.getAttribute("attributeClass")!}">
	<h2>${uiLabelMap.CreditCardPaymentMethodsHeading?if_exists}</h2>
	<ul class="displayList">
	 <li>
	  <div>
	   <p>${uiLabelMap.CreditCardPaymentMethodsInfo}</p>
	  </div>
	 </li>
	 <li>
	  <div>
	   <a href="<@ofbizUrl>eCommercePaymentMethodInfo</@ofbizUrl>"><span>${uiLabelMap.ClickViewCreditCardPaymentMethodInfo}</span></a>
	  </div>
	 </li>
	</ul>
</div>
</#if>

