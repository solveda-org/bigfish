<#if (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
<div class="${request.getAttribute("attributeClass")!}">
    <h2>${uiLabelMap.EFTPaymentMethodsHeading?if_exists}</h2>
    <ul class="displayList">
     <li>
      <div>
       <p>${uiLabelMap.EFTPaymentMethodsInfo}</p>
      </div>
     </li>
     <li>
      <div>
       <a href="<@ofbizUrl>eCommerceEftPaymentMethodInfo</@ofbizUrl>"><span>${uiLabelMap.ClickViewEFTPaymentMethodInfo}</span></a>
      </div>
     </li>
    </ul>
</div>
</#if>
