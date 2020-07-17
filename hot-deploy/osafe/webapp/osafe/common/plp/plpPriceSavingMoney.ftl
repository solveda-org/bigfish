<#-- Check Savings Money -->
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<#assign PRODUCT_MONEY_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_MONEY_THRESHOLD")!"0"/>
<#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
<#if plpListPrice?has_content && plpPrice?has_content>
  <#assign youSaveMoney = (plpListPrice - plpPrice)/>
  <div class="plpPriceSavingMoney">
    <#if youSaveMoney gt showSavingMoneyAbove?number>
      <label>${uiLabelMap.YouSaveCaption}</label>
      <span class="price"><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId rounding=globalContext.currencyRounding /></span>
    </#if>
  </div>
</#if>