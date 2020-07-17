<div class="pdpPriceOnLine" id="pdpPriceOnLine">
  <#if pdpPriceMap?exists && pdpPriceMap?has_content>
    <label>${uiLabelMap.OnlinePriceCaption}</label>
    <span class="price"><@ofbizCurrency amount=pdpPriceMap.price isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed /></span>
  </#if>
</div>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",key), true)/>
    <#assign productPrice = dispatcher.runSync("calculateProductPrice", Static["org.ofbiz.base.util.UtilMisc"].toMap("product", product, "userLogin", userLogin))/>
    <#if productPrice?has_content>
      <div class="pdpPriceOnLine" id="pdpPriceOnLine_${key}" style="display:none">
        <label>${uiLabelMap.OnlinePriceCaption}</label>
        <span class="price"><@ofbizCurrency amount=productPrice.basePrice isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed /></span>
      </div>
    </#if>
  </#list>
</#if>