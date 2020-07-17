<#if pdpPriceMap.listPrice gt pdpPriceMap.price>
  <div class="pdpPriceList">
       <label>${uiLabelMap.ListPriceCaption}</label>
       <span class="price"><@ofbizCurrency amount=pdpPriceMap.listPrice isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed /></span>
  </div>
</#if>
