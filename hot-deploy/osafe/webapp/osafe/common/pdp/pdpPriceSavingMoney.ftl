<#-- Check Savings Money -->
<#assign showSavingDollarAbove = PDP_DOLLAR_THRESHOLD!"0"/>
<#assign youSaveDollar = (priceMap.listPrice - priceMap.price)/>
<#if youSaveDollar gt showSavingDollarAbove?number>
 <div id="pdpPriceSavingMoney">
     <label>${uiLabelMap.YouSaveCaption}</label>
     <span class="savings"><@ofbizCurrency amount=youSaveDollar isoCode=CURRENCY_UOM_DEFAULT!priceMap.currencyUsed /></span>
 </div>
</#if>
