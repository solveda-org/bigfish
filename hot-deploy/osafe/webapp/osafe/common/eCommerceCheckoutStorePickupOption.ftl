<#assign cart = session.getAttribute("shoppingCart")/>
<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_PICKUP") && cart?has_content && !cart.getOrderAttribute("STORE_LOCATION")?has_content>
  <div class="shippingOptionsStorePickup">
        <span>${uiLabelMap.SelectStoreInfo}</span>
        <a href="javaScript:void(0);" onClick="displayActionDialogBox('${dialogPurpose!}',this);" class="standardBtn positive"><span>${uiLabelMap.SelectStoreBtn!}</span></a>
  </div>
</#if>
