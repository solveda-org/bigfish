<#assign cart = shoppingCart?if_exists />
<#if cart?exists && 0 < cart.size()>
    <!-- DIV for Displaying Order Summary STARTS here -->
    <div class="orderSummaryPaypal">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderSummaryPayPalDivSequence")}
    </div>
    <!-- DIV for Displaying Order Summary ENDS here -->  
<#else>
  <h3>${uiLabelMap.OrderErrorShoppingCartEmpty}.</h3>
</#if>