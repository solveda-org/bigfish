<div class="pdpPriceList" id="pdpPriceList">
  <#if pdpPriceMap.listPrice gt pdpPriceMap.price>
    <label>${uiLabelMap.ListPriceCaption}</label>
    <span class="price"><@ofbizCurrency amount=pdpPriceMap.listPrice isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed /></span>
  </#if>
</div>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",key), true)/>
    <#assign productPrice = dispatcher.runSync("calculateProductPrice", Static["org.ofbiz.base.util.UtilMisc"].toMap("product", product, "userLogin", userLogin))/>
    <#if productPrice?has_content>
      <#if productPrice.listPrice gt productPrice.basePrice>
        <div class="pdpPriceList" id="pdpPriceList_${key}" style="display:none">
          <label>${uiLabelMap.ListPriceCaption}</label>
          <span class="price"><@ofbizCurrency amount=productPrice.listPrice isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed /></span>
        </div>
      </#if>
    </#if>
  </#list>
</#if>