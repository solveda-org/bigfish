<#assign inStock = true />
<#assign isSellable = Static["org.ofbiz.product.product.ProductWorker"].isSellable(currentProduct?if_exists) />

<#assign productInventoryLevel = Static["com.osafe.services.InventoryServices"].getProductInventoryLevel(currentProduct.productId?if_exists, request) />
<#assign inventoryLevel = productInventoryLevel.get("inventoryLevel")/>
<#assign inventoryInStockFrom = productInventoryLevel.get("inventoryLevelInStockFrom")/>
<#assign inventoryOutOfStockTo = productInventoryLevel.get("inventoryLevelOutOfStockTo")/>
<div class="pdpAddToCart">
<#if !(currentProduct.isVirtual?if_exists?upper_case == "Y") || !(featureOrder?exists && featureOrder?size gt 0)>
  <#if (inventoryLevel?number <= inventoryOutOfStockTo?number)>
    <#assign isSellable = false/>
  </#if>
</#if>
<#if !isSellable>
 <#assign inStock=false/>
</#if>
<#if inStock>
  <div class="addToCart">
	<label>${uiLabelMap.QuantityLabel}:</label><input type="text" class="quantity" size="5" name="quantity" value="1" maxlength="5"/>
	<a href="javascript:void(0);" onClick="javascript:addItem();" class="standardBtn addToCart <#if featureOrder?exists && featureOrder?size gt 0>inactiveAddToCart</#if>" id="addToCart"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
  </div>
<#elseif !isSellable>
  <div class="addToCart">
    <label>${uiLabelMap.QuantityLabel}:</label><input type="text" class="quantity" size="5" name="quantity" value="1" maxlength="5" disabled="disabled"/>
	<a href="javascript:void(0);" class="standardBtn addToCart inactiveAddToCart" id="addToCart"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
  </div>
<#else>
  <div class="availability">${uiLabelMap.OutOfStockLabel}</div>
</#if>
</div>