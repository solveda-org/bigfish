<#-- Check Savings Money -->
<#if pdpPriceMap?exists && pdpPriceMap?has_content>
  <#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
  <#assign youSaveMoney = (pdpPriceMap.listPrice - pdpPriceMap.price)/>
  <div class="pdpPriceSavingMoney" id="pdpPriceSavingMoney">
    <#if youSaveMoney gt showSavingMoneyAbove?number>
      <label>${uiLabelMap.YouSaveCaption}</label>
      <span class="savings"><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed /></span>
    </#if>
  </div>
</#if>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",key), true)/>
    <#assign productPrice = dispatcher.runSync("calculateProductPrice", Static["org.ofbiz.base.util.UtilMisc"].toMap("product", product, "userLogin", userLogin))/>
    <#if productPrice?has_content>
      <#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
      <#assign youSaveMoney = (productPrice.listPrice - productPrice.basePrice)/>
      <#if youSaveMoney gt showSavingMoneyAbove?number>
        <div class="pdpPriceSavingMoney" id="pdpPriceSavingMoney_${key}" style="display:none">
          <label>${uiLabelMap.YouSaveCaption}</label>
          <span class="savings"><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed /></span>
        </div>
      </#if>
    </#if>
  </#list>
</#if>