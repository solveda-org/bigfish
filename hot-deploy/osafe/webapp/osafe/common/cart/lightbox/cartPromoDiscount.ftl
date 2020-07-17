<#assign cartAdjustment = cartAdjustment/>
<#assign shoppingCart = shoppingCart/>
<#assign currencyUom = currencyUom/>
<div class="cartPromoDiscount">
  <div class="labelText">
    <label><#if promoText?has_content>(<#if promoCodeText?has_content>${promoCodeText!} </#if>${promoText!})<#else>${adjustmentTypeDesc!}</#if></label>
  </div>
  <#-- <div class="labelValue">
    <span class="amount"><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) rounding=2 isoCode=currencyUom/></span>
  </div> -->
</div>