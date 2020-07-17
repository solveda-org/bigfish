<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <#if inStock>
      <a class="standardBtn addToCart" href="javascript:submitCheckoutForm(document.${formName!},'ACW','${wishListItem.shoppingListItemSeqId}');" title="Add to Cart">
        <span>${uiLabelMap.OrderAddToCartBtn}</span>
      </a>
      <input type="hidden" name="add_category_id_${wishListItem.shoppingListItemSeqId}" value="${productCategoryId!}" /> 
    </#if>
  </div>
</li>