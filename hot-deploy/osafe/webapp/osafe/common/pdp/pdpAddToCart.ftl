<#assign pdpSelectMultiVariantAttribute = delegator.findOne("ProductAttribute", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",currentProduct.productId,"attrName","PDP_SELECT_MULTI_VARIANT"), true)! />
<#if pdpSelectMultiVariantAttribute?has_content >
  <#assign pdpSelectMultiVariant = pdpSelectMultiVariantAttribute.attrValue! />
</#if>
<#if pdpSelectMultiVariant?exists && pdpSelectMultiVariant?has_content && ((pdpSelectMultiVariant.toUpperCase() == "QTY") || (pdpSelectMultiVariant.toUpperCase() == "CHECKBOX")) >
  <div class="pdpAddToCart">
      <input type="hidden" name= "add_category_id"value="${parameters.add_category_id!productCategoryId!}" /> 
      <a href="javascript:addMultiItems('${pdpSelectMultiVariant}');" class="standardBtn addToCart" id="addMultiToCart"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
  </div>
<#else>
  <#assign inStock = true />
  <#assign isSellable = Static["org.ofbiz.product.product.ProductWorker"].isSellable(currentProduct?if_exists)/>
  <#assign productInventoryLevel = productInventoryMap.get('${currentProduct.productId}')!""/>
  <#assign inventoryLevel = productInventoryLevel.get("inventoryLevel")/>
  <#assign inventoryInStockFrom = productInventoryLevel.get("inventoryLevelInStockFrom")/>
  <#assign inventoryOutOfStockTo = productInventoryLevel.get("inventoryLevelOutOfStockTo")/>
  <div class="pdpAddToCart">
    <#if (currentProduct.isVirtual?if_exists?upper_case == "N" && currentProduct.isVariant?if_exists?upper_case == "N")>
      <#if (inventoryLevel?number lt inventoryOutOfStockTo?number || inventoryLevel?number == inventoryOutOfStockTo?number)>
        <#assign isSellable = false/>
      <#else>
        <input type="hidden" name="add_product_id" value="${currentProduct.productId}" />
      </#if>
    <#elseif !(currentProduct.isVirtual?if_exists?upper_case == "Y") || !(featureOrder?exists && featureOrder?size gt 0)>
      <#if (inventoryLevel?number lt inventoryOutOfStockTo?number || inventoryLevel?number == inventoryOutOfStockTo?number)>
        <#assign isSellable = false/>
      </#if>
    </#if>
    <#if !(currentProduct.isVirtual?if_exists?upper_case == "Y") || !(featureOrder?exists && featureOrder?size gt 0)>
    </#if>
    <#if !isSellable>
      <#assign inStock=false/>
    </#if>
    <input type="hidden" name= "add_category_id" id="add_category_id" value="${parameters.add_category_id!productCategoryId!}" /> 
    <#if inStock>
        <label>${uiLabelMap.QuantityLabel}:</label><input type="text" class="quantity" size="5" name="quantity" value="1" maxlength="5"/>
        <a href="javascript:void(0);" onClick="javascript:addItem('addToCart');" class="standardBtn addToCart <#if featureOrder?exists && featureOrder?size gt 0>inactiveAddToCart</#if>" id="addToCart"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
    <#elseif !isSellable>
        <label>${uiLabelMap.QuantityLabel}:</label><input type="text" class="quantity" size="5" name="quantity" value="1" maxlength="5" disabled="disabled"/>
        <a href="javascript:void(0);" class="standardBtn addToCart inactiveAddToCart" id="addToCart"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
    <#else>
      <div class="availability">${uiLabelMap.OutOfStockLabel}</div>
    </#if>
  </div>
</#if>