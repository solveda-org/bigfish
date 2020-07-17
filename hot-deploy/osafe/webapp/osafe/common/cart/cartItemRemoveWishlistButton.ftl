<div class="cartItemRemoveButton">
  <div class="labelText">
    <label>${uiLabelMap.CartItemRemoveBtnCaption}</label>
  </div>
  <div class="labelValue">
    <a class="standardBtn action" href="<@ofbizUrl>${deleteFromWishListAction!}?delete_${wishListItem.shoppingListItemSeqId}=${wishListItem.shoppingListItemSeqId}</@ofbizUrl>" title="${uiLabelMap.RemoveItemBtn}">
      <span>${uiLabelMap.RemoveItemBtn}</span>
    </a>
  </div>
</div>


