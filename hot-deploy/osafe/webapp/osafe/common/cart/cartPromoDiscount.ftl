<#if appliedPromoList?exists && appliedPromoList?has_content>
  <#list appliedPromoList as appliedPromo >
    <div class="cartPromoDiscount">
      <div class="labelText">
        <label><#if appliedPromo.promoText?has_content>(<#if appliedPromo.promoCodeText?has_content>${appliedPromo.promoCodeText!} </#if>${appliedPromo.promoText!})<#else>${appliedPromo.adjustmentTypeDesc!}</#if></label>
      </div>
      <div class="labelValue">
        <span class="amount"><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(appliedPromo.cartAdjustment, shoppingCartSubTotal) isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
      </div>
    </div>
  </#list>
</#if>