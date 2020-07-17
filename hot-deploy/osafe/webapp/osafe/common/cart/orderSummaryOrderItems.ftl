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
  <#assign itemsFromList = false>
  <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
  <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
  <div class="boxList cartList">
      <#list shoppingCart.items() as cartLine>
        ${setRequestAttribute("cartLine", cartLine)}
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderSummaryOrderItemsDivSequence")}
      </#list>
  </div>
  </#if>

