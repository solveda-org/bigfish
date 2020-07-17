<#-- Check Savings Money -->
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<#assign PRODUCT_MONEY_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_MONEY_THRESHOLD")!"0"/>
<#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
<#if plpListPrice?has_content && plpPrice?has_content>
    <#assign youSaveMoney = (plpListPrice - plpPrice)/>
    <li class="${request.getAttribute("attributeClass")!}">
        <div class="js_plpPriceSavingMoney" id="js_plpPriceSavingMoney_${uiSequenceScreen}_${plpProduct.productId}">
            <#if youSaveMoney gt showSavingMoneyAbove?number>
                <label>${uiLabelMap.YouSaveCaption}</label>
                <span><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId rounding=globalContext.currencyRounding /></span>
            </#if>
        </div>
        
        <div class="js_plpPriceSavingMoney" id="js_plpPriceSavingMoney_Virtual_${uiSequenceScreen}_${plpProduct.productId}" style="display:none">
            <#if youSaveMoney gt showSavingMoneyAbove?number>
                <label>${uiLabelMap.YouSaveCaption}</label>
                <span><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId rounding=globalContext.currencyRounding /></span>
            </#if>
        </div>
        
        <#if plpProductVariantMapKeys?exists && plpProductVariantMapKeys?has_content>
		    <#list plpProductVariantMapKeys as key>
		        <#assign productPriceMap = plpProductVariantPriceMap.get('${key}')!""/>
		        <#if productPriceMap?has_content>
		            <#assign youSaveMoneyVar = (productPriceMap.listPrice - productPriceMap.basePrice) />
	                <div class="js_plpPriceSavingMoney" id="js_plpPriceSavingMoney_${key}" style="display:none">
	                    <#if youSaveMoneyVar gt showSavingMoneyAbove?number>
					        <label>${uiLabelMap.YouSaveCaption}</label>
					        <span><@ofbizCurrency amount=youSaveMoneyVar isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId rounding=globalContext.currencyRounding /></span>
				        </#if>
				    </div>
		        </#if>  
		    </#list>
		</#if>
    </li>   
</#if>