<#assign cart = shoppingCart?if_exists />
<#if cart?exists && 0 &lt; cart.size()>
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#entryFormJS")}
 <!-- DIV for Displaying Order Summary STARTS here -->
    <div class="${request.getAttribute("attributeClass")!}">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderSummaryDivSequence")}
    </div>
<!-- DIV for Displaying Order Summary ENDS here -->  
<#else>
    <h3>${uiLabelMap.OrderErrorShoppingCartEmpty}.</h3>
</#if>