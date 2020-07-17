<#-- Check Savings Money -->
<#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
<#assign youSaveMoney = (priceMap.listPrice - priceMap.price)/>
<div class="plpPriceSavingMoney">
  <#if youSaveMoney gt showSavingMoneyAbove?number>
    <p class="price">${uiLabelMap.YouSaveCaption}<@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!priceMap.currencyUsed /></p>
  </#if>
</div>
