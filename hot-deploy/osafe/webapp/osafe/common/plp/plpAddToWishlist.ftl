<li class="${request.getAttribute('attributeClass')!}">
   <div id="js_addToWishlist_div_${uiSequenceScreen}_${plpProduct.productId}">
       <a href="javascript:void(0);" onClick="javascript:addItemPlpToWishlist('${uiSequenceScreen}_${plpProduct.productId}');" class="standardBtn addToWishlist <#if plpFeatureOrder?has_content && plpFeatureOrder?size gt 0 || (isPdpInStoreOnly?exists && isPdpInStoreOnly == "Y")>inactiveAddToWishlist</#if>" id="js_addToWishlist_${uiSequenceScreen}_${plpProduct.productId}"><span>${uiLabelMap.AddToWishlistBtn}</span></a>
   </div>
</li>