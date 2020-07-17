<div class="cartItemAddToCartButton">
  <div class="labelText">
      <label>${uiLabelMap.CartItemAddToCartButtonCaption}</label>
  </div>
  <div class="labelValue">
    <div class="addToCart">
      <#if inStock>
        <span class="action">
          <a class="standardBtn addToCart" href="javascript:submitCheckoutForm(document.${formName!},'ACW','${wishListItem.shoppingListItemSeqId}');" title="Add to Cart">
            <span>${uiLabelMap.OrderAddToCartBtn}</span>
          </a>
        </span>
      </#if>
    </div>
  </div>
</div>