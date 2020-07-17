<#-- Check Savings Money -->
<#assign showSavingMoneyAbove = PDP_MONEY_THRESHOLD!"0"/>
<#assign youSaveMoney = (priceMap.listPrice - priceMap.price)/>
<#if youSaveMoney gt showSavingMoneyAbove?number>
 <div class="plpPriceSavingMoney">
     <p class="price">${uiLabelMap.YouSaveCaption}<@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!priceMap.currencyUsed /></p>
 </div>
</#if>
