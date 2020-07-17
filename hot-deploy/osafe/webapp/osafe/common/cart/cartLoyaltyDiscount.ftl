<#if appliedLoyaltyPointsList?exists && appliedLoyaltyPointsList?has_content>
  <#list appliedLoyaltyPointsList as appliedLoyaltyPoints>    
    <li class="${request.getAttribute("attributeClass")!}">
      <div>
        <label>${appliedLoyaltyPoints.adjustmentTypeDesc!}</label>
        <span><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(appliedLoyaltyPoints.cartAdjustment, shoppingCartSubTotal) isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
      </div>
    </li>
  </#list>
</#if>