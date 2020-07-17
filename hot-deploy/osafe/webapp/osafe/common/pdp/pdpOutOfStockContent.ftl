<li class="${request.getAttribute("attributeClass")!}">
    <#assign isOutOfStock = true />
    <#if currentProduct.isVirtual?if_exists?upper_case == "Y">
        <#if (inventoryMethod?exists && inventoryMethod?has_content)>
            <#if inventoryMethod.toUpperCase() == "BIGFISH">
                <#if (productVariantInventoryMap?exists && productVariantInventoryMap?has_content)>
                     <#list productVariantInventoryMap.entrySet() as entry>
                          <#assign inventoryLevelMap = entry.value />
                          <#assign inventoryLevel = inventoryLevelMap.get("inventoryLevel")/>
                          <#assign inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo")/>
                          <#if (inventoryLevel?number > inventoryOutOfStockTo?number)>
                              <#assign isOutOfStock = false />
                              <#break>
                          </#if>
                     </#list>
                 </#if>
            </#if>
        </#if>
    <#else>
        <#if inventoryLevel?has_content && inventoryOutOfStockTo?has_content && (inventoryLevel?number > inventoryOutOfStockTo?number)>
            <#assign isOutOfStock = false />
        </#if>
    </#if>
    <div class="js_pdpOutOfStockContent" id="js_pdpOutOfStockContent" <#if !isOutOfStock>style="display:none;"</#if>>
        ${screens.render("component://osafe/widget/EcommerceContentScreens.xml#PS_OUT_OF_STOCK")}
    </div>
</li>
  
