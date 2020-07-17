<#if (recurrenceItem?has_content) && recurrenceItem == "Y" >
  <#if recurrencePrice?exists && recurrencePrice?has_content>
   <li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
     <div>
       <label>${uiLabelMap.CartItemPriceRecurrenceCaption}</label>
       <span><@ofbizCurrency amount=recurrencePrice isoCode=CURRENCY_UOM_DEFAULT! rounding=globalContext.currencyRounding /></span>
       <label>${uiLabelMap.CartItemRecurrencePriceSavingsLabel}</label>
       <span>${recurrenceSavePercent?string("#0%")}</span>
     </div>
   </li>
  </#if>
</#if>
