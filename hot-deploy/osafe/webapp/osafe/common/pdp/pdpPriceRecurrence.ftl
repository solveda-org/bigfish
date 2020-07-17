<#-- Check Recurrence Price -->
<#if (pdpPriceMap?exists && pdpPriceMap?has_content && pdpRecurrencePriceMap?exists && pdpRecurrencePriceMap?has_content) && (pdpRecurrencePriceMap.price?has_content && pdpRecurrencePriceMap.price != 0) && (pdpPriceMap.price?has_content && pdpPriceMap.price != 0)>
 <#if (pdpPriceMap.price > pdpRecurrencePriceMap.price)>
  <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
  <#assign recurrenceSavePercent = ((pdpPriceMap.price - pdpRecurrencePriceMap.price)/pdpPriceMap.price) />
  <li class="${request.getAttribute("attributeClass")!}">
    <div class="pdpPriceRecurrence" id="pdpPriceRecurrence">
      <label class="checkboxOptionLabel">
        <input type="checkbox" id="js_pdpPriceRecurrenceCB" name="pdpPriceRecurrenceCB" value="Y" <#if requestParameters.pdpPriceRecurrenceCB?has_content && requestParameters.pdpPriceRecurrenceCB == "Y">checked</#if>/>
        <label>${uiLabelMap.RecurrencePriceCaption}</label>
        <span><@ofbizCurrency amount=pdpRecurrencePriceMap.price isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed rounding=globalContext.currencyRounding /></span>
        <label>${uiLabelMap.RecurrencePriceSavingsCaption}</label>
        <span>${recurrenceSavePercent?string("#0%")}</span>
      </label>
      <input type="hidden" name="price" value="${pdpRecurrencePriceMap.price}"/>
      <input type="hidden" name="add_amount" value="${pdpRecurrencePriceMap.price}"/>
    </div>
  </li>
 </#if>
</#if>
