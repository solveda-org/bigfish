<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.NameOnCardCaption}</label>
    <#assign nameOnCard=savedCreditCard.firstNameOnCard?if_exists +" "+ savedCreditCard.lastNameOnCard?if_exists/>
    <span>${nameOnCard!}</span>
  </div>
</li>