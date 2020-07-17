<#-- Check Savings Money -->
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<#assign PRODUCT_MONEY_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_MONEY_THRESHOLD")!"0"/>
<#if pdpPriceMap?exists && pdpPriceMap?has_content>
  <#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
  <#if pdpPriceMap.listPrice?has_content && pdpPriceMap.price?has_content>
    <#assign youSaveMoney = (pdpPriceMap.listPrice - pdpPriceMap.price)/>
    <li class="${request.getAttribute("attributeClass")!}">
      <div class="pdpPriceSavingMoney" id="js_pdpPriceSavingMoney">
        <#if youSaveMoney gt showSavingMoneyAbove?number>
          <label>${uiLabelMap.YouSaveCaption}</label>
          <span><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed rounding=globalContext.currencyRounding /></span>
        </#if>
      </div>
      
      <div class="pdpPriceSavingMoney" id="js_pdpPriceSavingMoney_Virtual" style="display:none">
        <#if youSaveMoney gt showSavingMoneyAbove?number>
          <label>${uiLabelMap.YouSaveCaption}</label>
          <span><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed rounding=globalContext.currencyRounding /></span>
        </#if>
      </div>
      
    </li>
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
          <div class="pdpPriceSavingMoney" id="js_pdpPriceSavingMoney_${key}" style="display:none">
            <label>${uiLabelMap.YouSaveCaption}</label>
            <span><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed rounding=globalContext.currencyRounding /></span>
          </div>
        </#if>
      </#if>
    </#if>
  </#list>
</#if>