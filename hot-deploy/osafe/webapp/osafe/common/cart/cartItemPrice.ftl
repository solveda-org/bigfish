<div class="cartItemPrice">
TEST ITEM PRICE DIv
<#--
  <#assign price = cartLine.getBasePrice()>
  <#assign displayPrice = cartLine.getDisplayPrice()>
  <#assign offerPrice = "">
  <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
  <#if (cartItemAdjustment < 0) >
    <#assign offerPrice = cartLine.getDisplayPrice() + (cartItemAdjustment/cartLine.getQuantity())>
  </#if>
  <#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
    <#assign price= cartLine.getDisplayPrice()>
  <#else>
    <#if (cartLine.getSelectedAmount() > 0) >
      <#assign price = cartLine.getBasePrice() / cartLine.getSelectedAmount()>
    <#else>
      <#assign price = cartLine.getBasePrice()>
    </#if>
  </#if>
  <td class="total numberCol <#if !cartLine_has_next>lastRow</#if>">
    <ul>
      <li>
        <span class="price"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() rounding=2 isoCode=currencyUom/></span>
      </li>
    </ul>
  </td>
-->
</div>
