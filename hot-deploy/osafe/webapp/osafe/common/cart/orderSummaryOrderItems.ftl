<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
  <#if shoppingCart?has_content>
    <#assign offerPriceVisible= "N"/>
    <#list shoppingCart.items() as cartLine>
      <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
      <#if (cartItemAdjustment &lt; 0) >
        <#assign offerPriceVisible= "Y" />
        <#break>
      </#if>
    </#list>
    <div id="cart_wrap">
      <#assign itemsFromList = false>
      <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
      <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
      <#list shoppingCart.items() as cartLine>
        ${setRequestAttribute("cartLine", cartLine)}
        <div class="orderSummaryOrderItem">
          ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderSummaryOrderItemsDivSequence")}
        </div>
      </#list>
    </div>
  </#if>

