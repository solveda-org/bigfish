<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <#if !(partyProfileDefault?has_content && partyProfileDefault.defaultPayMeth?has_content && partyProfileDefault.defaultPayMeth == savedCreditCard.paymentMethodId)>
      <a class="standardBtn action" href="<@ofbizUrl>updateDefaultPmtMethod?paymentMethodId=${savedCreditCard.paymentMethodId!""}</@ofbizUrl>"><span>${uiLabelMap.SetAsDefaultLabel}</span></a>
    </#if>
  </div>
</li>