<#if billingAddress?has_content>
 <div class="${request.getAttribute("attributeClass")!}">
    <div class="displayBox">
      <h3>${uiLabelMap.BillingAddressTitle}</h3>
       ${setRequestAttribute("PostalAddress", billingAddress)}
       ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
    </div>
 </div>
</#if>