<#-- Check Savings Money -->
<#if priceMap?exists && priceMap?has_content>
<#assign showSavingMoneyAbove = PDP_MONEY_THRESHOLD!"0"/>
<#assign youSaveMoney = (priceMap.listPrice - priceMap.price)/>
<#if youSaveMoney gt showSavingMoneyAbove?number>
 <div id="pdpPriceSavingMoney">
     <label>${uiLabelMap.YouSaveCaption}</label>
     <span class="savings"><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!priceMap.currencyUsed /></span>
 </div>
</#if>
</#if>