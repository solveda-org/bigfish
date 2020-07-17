<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
    <div>
      <label>${uiLabelMap.ReOrderQtyCaption}</label>
      <#if productBuyable == 'true'>
         <input type="text" class="js_add_multi_product_quantity" name="add_multi_product_quantity_${lineIndex}" id="js_add_multi_product_quantity_${lineIndex}" value="${quantityOrdered!}" onBlur="javascript:seqReOrderCheck(this);"/>
         <input type="hidden" name="add_category_id_${lineIndex}" value="${productCategoryId!}" /> 
      <#else>
        <span>${uiLabelMap.ProductNoLongerAvailableInfo}</span>
      </#if> 
    </div>
</li>