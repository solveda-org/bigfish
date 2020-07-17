<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
    <div>
      <label>${uiLabelMap.ReOrderCheckBoxCaption}</label>
      <#if productBuyable == 'true'>
        <label class="checkboxOptionLabel"><input type="checkbox" class="add_multi_product_id" name="add_multi_product_id_${lineIndex}" id="js_add_multi_product_id_${lineIndex}" value="${productId!}"/></label>
        <#if pdpQtyMinAttributeValue?exists && pdpQtyMinAttributeValue?has_content && pdpQtyMaxAttributeValue?exists && pdpQtyMaxAttributeValue?has_content>
		  <input type="hidden" name="pdpQtyMinAttributeValue_${lineIndex}" id="js_pdpQtyMinAttributeValue_${productId}" value="${pdpQtyMinAttributeValue!}"/>
		  <input type="hidden" name="pdpQtyMaxAttributeValue_${lineIndex}" id="js_pdpQtyMaxAttributeValue_${productId}" value="${pdpQtyMaxAttributeValue!}"/>
		</#if>
		<input type="hidden" name="productName${lineIndex}" id="js_productName_${lineIndex}" value="${wrappedProductName!}" /> 
      <#else>
        <span>${uiLabelMap.ProductNoLongerAvailableInfo}</span>
      </#if> 
    </div>
</li>