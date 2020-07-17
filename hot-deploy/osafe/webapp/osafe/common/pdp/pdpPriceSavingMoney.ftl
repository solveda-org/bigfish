<#-- Check Savings Money -->
<#if pdpPriceMap?exists && pdpPriceMap?has_content>
<#assign showSavingMoneyAbove = PDP_MONEY_THRESHOLD!"0"/>
<#assign youSaveMoney = (pdpPriceMap.listPrice - pdpPriceMap.price)/>
<#if youSaveMoney gt showSavingMoneyAbove?number>
 <div class="pdpPriceSavingMoney">
     <label>${uiLabelMap.YouSaveCaption}</label>
     <span class="savings"><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed /></span>
 </div>
</#if>
</#if>