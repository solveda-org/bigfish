<#-- Check Savings Money -->
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<#assign PRODUCT_MONEY_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_MONEY_THRESHOLD")!"0"/>
<#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
<#if listPrice?has_content && price?has_content>
  <#assign youSaveMoney = (listPrice - price)/>
  <div class="plpPriceSavingMoney">
    <#if youSaveMoney gt showSavingMoneyAbove?number>
      <p class="price">${uiLabelMap.YouSaveCaption}<@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId /></p>
    </#if>
  </div>
</#if>