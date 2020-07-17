<#if pdpPriceMap?has_content>
 <#if (pdpPriceMap.listPrice?has_content) && (pdpPriceMap.price?has_content) && (pdpPriceMap.listPrice gt pdpPriceMap.price)>
  <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
  <li class="${request.getAttribute("attributeClass")!}">
    <div class="pdpPriceList" id="js_pdpPriceList">
	    <label>${uiLabelMap.ListPriceCaption}</label>
	    <span><@ofbizCurrency amount=pdpPriceMap.listPrice isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed! rounding=globalContext.currencyRounding/></span>
    </div>
    
    <div class="pdpPriceList" id="js_pdpPriceList_Virtual" style="display:none">
	    <label>${uiLabelMap.ListPriceCaption}</label>
	    <span><@ofbizCurrency amount=pdpPriceMap.listPrice isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed! rounding=globalContext.currencyRounding/></span>
    </div>
  </li>

  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
	  <#list productVariantMapKeys as key>
	    <#assign productPrice = productVariantPriceMap.get('${key}')/>
	    <#if productPrice?has_content>
	      <#if (productPrice.listPrice?has_content) && (productPrice.basePrice?has_content) && (productPrice.listPrice gt productPrice.basePrice)>
	        <div class="pdpPriceList" id="js_pdpPriceList_${key}" style="display:none">
	          <label>${uiLabelMap.ListPriceCaption}</label>
	          <span><@ofbizCurrency amount=productPrice.listPrice isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed rounding=globalContext.currencyRounding/></span>
	        </div>
	      </#if>
	    </#if>
	  </#list>
  </#if>
 </#if>
</#if>
	