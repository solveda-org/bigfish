<#-- Check Savings Money -->
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<#assign PRODUCT_MONEY_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_MONEY_THRESHOLD")!"0"/>
<#if pdpPriceMap?exists && pdpPriceMap?has_content>
  <#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
  <#if pdpPriceMap.listPrice?has_content && pdpPriceMap.price?has_content>
    <#assign youSaveMoney = (pdpPriceMap.listPrice - pdpPriceMap.price)/>
    <div class="pdpPriceSavingMoney" id="pdpPriceSavingMoney">
      <#if youSaveMoney gt showSavingMoneyAbove?number>
        <label>${uiLabelMap.YouSaveCaption}</label>
        <span class="savings"><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed /></span>
      </#if>
    </div>
  </#if>
</#if>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign productPrice = productVariantPriceMap.get('${key}')/>
    <#if productPrice?has_content>
      <#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
      <#if productPrice.listPrice?has_content && productPrice.basePrice?has_content>
        <#assign youSaveMoney = (productPrice.listPrice - productPrice.basePrice)/>
        <#if youSaveMoney gt showSavingMoneyAbove?number>
          <div class="pdpPriceSavingMoney" id="pdpPriceSavingMoney_${key}" style="display:none">
            <label>${uiLabelMap.YouSaveCaption}</label>
            <span class="savings"><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed /></span>
          </div>
        </#if>
      </#if>
    </#if>
  </#list>
</#if>