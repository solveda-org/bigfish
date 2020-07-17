<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#assign shoppingCartSize = shoppingCart.size() />
<p>${uiLabelMap.YourShoppingCartCurrentlyContains}</p>
