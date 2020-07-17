<#if appliedPromoList?exists && appliedPromoList?has_content>
  <#list appliedPromoList as appliedPromo >   
    <li class="${request.getAttribute("attributeClass")!}">
      <div>
         <label><#if appliedPromo.promoText?has_content>(<#if appliedPromo.promoCodeText?has_content>${appliedPromo.promoCodeText!} </#if>${appliedPromo.promoText!})<#else>${appliedPromo.adjustmentTypeDesc!}</#if></label>
         <span><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(appliedPromo.cartAdjustment, shoppingCartSubTotal) isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
      </div>
    </li>
  </#list>
</#if>