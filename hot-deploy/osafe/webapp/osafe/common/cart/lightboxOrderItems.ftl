<div class="lightBoxOrderItems">
 <#if (shoppingCartSize > 0)>
  <#assign offerPriceVisible= "N"/>
  <#list shoppingCart.items() as cartLine>
    <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
    <#if (cartItemAdjustment &lt; 0) >
      <#assign offerPriceVisible= "Y" />
      <#break>
    </#if>
  </#list>
  <div id="cart_wrap">
    <input type="hidden" name="removeSelected" value="false"/>
    <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
        <input type="hidden" name="guest" value="guest"/>
    </#if>
    <#assign itemsFromList = false>
        <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
        <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
    <#list shoppingCart.items() as cartLine>
      ${setRequestAttribute("cartLine", cartLine)}
      ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#lightBoxOrderItemsDivSequence")}
    </#list>
  </div>
 </#if>
</div>