<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="pdpPriceList" id="pdpPriceList">
  <#if pdpPriceMap?has_content>
	  <#if (pdpPriceMap.listPrice?has_content) && (pdpPriceMap.price?has_content) && (pdpPriceMap.listPrice gt pdpPriceMap.price)>
	    <label>${uiLabelMap.ListPriceCaption}</label>
	    <span class="price"><@ofbizCurrency amount=pdpPriceMap.listPrice isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed! /></span>
	  </#if>
  </#if>
</div>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign productPrice = productVariantPriceMap.get('${key}')/>
    <#if productPrice?has_content>
      <#if (productPrice.listPrice?has_content) && (productPrice.basePrice?has_content) && (productPrice.listPrice gt productPrice.basePrice)>
        <div class="pdpPriceList" id="pdpPriceList_${key}" style="display:none">
          <label>${uiLabelMap.ListPriceCaption}</label>
          <span class="price"><@ofbizCurrency amount=productPrice.listPrice isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed /></span>
        </div>
      </#if>
    </#if>
  </#list>
</#if>