<#-- Check Savings Percent -->
<#assign PRODUCT_PCT_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_PCT_THRESHOLD")!"0"/>
<#if plpListPrice?has_content && plpListPrice != 0>
    <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
    <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
    <#assign youSavePercent = ((plpListPrice - plpPrice)/plpListPrice) />
    <li class="${request.getAttribute("attributeClass")!}">
        <div class="js_plpPriceSavingPercent" id="js_plpPriceSavingPercent_${uiSequenceScreen}_${plpProduct.productId}">
            <#if youSavePercent gt showSavingPercentAbove?number>
                <label>${uiLabelMap.YouSaveCaption}</label>
                <span>${youSavePercent?string("#0%")}</span>
            </#if>
        </div>
        
        <div class="js_plpPriceSavingPercent" id="js_plpPriceSavingPercent_Virtual_${uiSequenceScreen}_${plpProduct.productId}" style="display:none">
            <#if youSavePercent gt showSavingPercentAbove?number>
                <label>${uiLabelMap.YouSaveCaption}</label>
                <span>${youSavePercent?string("#0%")}</span>
            </#if>
        </div>
        
        <#if plpProductVariantMapKeys?exists && plpProductVariantMapKeys?has_content>
		    <#list plpProductVariantMapKeys as key>
		        <#assign productPriceMap = plpProductVariantPriceMap.get('${key}')!""/>
		        <#if productPriceMap?has_content>
		            <#assign youSavePercentVar = ((productPriceMap.listPrice - productPriceMap.basePrice)/productPriceMap.listPrice) />
	                <div class="js_plpPriceSavingPercent" id="js_plpPriceSavingPercent_${key}" style="display:none">
	                    <#if youSavePercentVar gt showSavingPercentAbove?number>
					        <label>${uiLabelMap.YouSaveCaption}</label>
					        <span>${youSavePercentVar?string("#0%")}</span>
				        </#if>
				    </div>
		        </#if>  
		    </#list>
		</#if>
		
    </li>   
</#if>
