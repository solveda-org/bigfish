<#if billingContactMechAddress?has_content>
   <#-- Billing addresses -->
  <#assign billingAddress = billingContactMechAddress.getRelatedOneCache("PostalAddress")>
  <#if billingAddress?has_content>
    <div class="displayBox">
      <h3>${uiLabelMap.BillingAddressTitle}</h3>
       ${setRequestAttribute("PostalAddress", billingAddress)}
       ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
    </div>
  </#if>
</#if>