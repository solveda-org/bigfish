<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <a class="standardBtn update" href="<@ofbizUrl>eCommerceEditCreditCardInfo?paymentMethodId=${savedCreditCard.paymentMethodId!""}&amp;mode=edit</@ofbizUrl>"><span>${uiLabelMap.EditLabel}</span></a>
  </div>
</li>