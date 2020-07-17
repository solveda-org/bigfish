 <#if (wishListSize > 0)>
  <#assign offerPriceVisible= "N"/>
  <div id="cart_wrap">
    <input type="hidden" name="removeSelected" value="false"/>
    <input type="hidden" name="add_item_id" id="add_item_id" value=""/>
    <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
        <input type="hidden" name="guest" value="guest"/>
    </#if>
    <#assign itemsFromList = false>
    <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
    <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
    
    <#assign rowNo = 0/>
    <#list wishList as wishListItem>
      ${setRequestAttribute("rowNo", rowNo)}
      ${setRequestAttribute("wishListItem", wishListItem)}
      <div class="showWishlistOrderItem">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#showWishlistOrderItemsDivSequence")}
      </div>
      <#assign rowNo = rowNo+1/>
    </#list>
  </div>
 </#if>
