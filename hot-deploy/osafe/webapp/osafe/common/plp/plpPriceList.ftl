<#if plpListPrice?exists &&  plpListPrice?has_content>
	<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
	<li class="${request.getAttribute("attributeClass")!}">
		<div class="js_plpPriceList" id="js_plpPriceList_${uiSequenceScreen}_${plpProduct.productId}">
		  <#if plpListPrice?has_content && plpListPrice gt plpPrice>
		    <label>${uiLabelMap.PlpListPriceLabel}</label>
		    <span><@ofbizCurrency amount=plpListPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
		  </#if>
		</div>
		
		<div class="js_plpPriceList" id="js_plpPriceList_Virtual_${uiSequenceScreen}_${plpProduct.productId}" style="display:none">
		  <#if plpListPrice?has_content && plpListPrice gt plpPrice>
		    <label>${uiLabelMap.PlpListPriceLabel}</label>
		    <span><@ofbizCurrency amount=plpListPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
		  </#if>
		</div>
		
		<#if plpProductVariantMapKeys?exists && plpProductVariantMapKeys?has_content>
		    <#list plpProductVariantMapKeys as key>
		        <#assign productPriceMap = plpProductVariantPriceMap.get('${key}')!""/>
		        <#if productPriceMap?has_content>
		            <#if (productPriceMap.listPrice?has_content) && (productPriceMap.basePrice?has_content) && (productPriceMap.listPrice gt productPriceMap.basePrice)>
		                <div class="plpPriceList" id="js_plpPriceList_${key}" style="display:none">
					        <label>${uiLabelMap.PlpListPriceLabel}</label>
					        <span><@ofbizCurrency amount=productPriceMap.listPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId rounding=globalContext.currencyRounding /></span>
					    </div>
		            </#if>
		        </#if>  
		    </#list>
		</#if>
	</li>
</#if>