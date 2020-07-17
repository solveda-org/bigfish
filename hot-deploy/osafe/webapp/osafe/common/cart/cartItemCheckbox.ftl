<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
    <div>
      <label>${uiLabelMap.CartCheckBoxCaption}</label>
      <#if inStock?if_exists && inStock>
        <label class="checkboxOptionLabel"><input type="checkbox" class="js_add_multi_product_id" name="add_multi_product_id_${wishListSeqId}" id="js_add_multi_product_id_${wishListSeqId}" value="${productId!}"/></label>
      <#else>
        <span>${uiLabelMap.ProductNoLongerAvailableInfo}</span>
      </#if> 
    </div>
</li>