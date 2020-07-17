<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.TypeCaption}</label>
    <#assign cardType=savedCreditCard.cardType?if_exists/>
    <span>${cardType!}</span>
  </div>
</li>