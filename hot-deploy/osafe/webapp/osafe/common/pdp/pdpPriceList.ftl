<#if priceMap.listPrice gt priceMap.price>
  <div id="pdpPriceList">
       <label>${uiLabelMap.ListPriceCaption}</label>
       <span class="price listPrice"><@ofbizCurrency amount=priceMap.listPrice isoCode=CURRENCY_UOM_DEFAULT!priceMap.currencyUsed /></span>
  </div>
</#if>
