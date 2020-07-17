<#-- Check Savings Money -->
<#assign showSavingDollarAbove = PDP_DOLLAR_THRESHOLD!"0"/>
<#assign youSaveDollar = (priceMap.listPrice - priceMap.price)/>
<#if youSaveDollar gt showSavingDollarAbove?number>
 <div class="plpPriceSavingMoney">
     <p class="price">${uiLabelMap.YouSaveCaption}<@ofbizCurrency amount=youSaveDollar isoCode=CURRENCY_UOM_DEFAULT!priceMap.currencyUsed /></p>
 </div>
</#if>
