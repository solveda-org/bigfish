<li class="${request.getAttribute('attributeClass')!}">
   <div>
       <a href="javascript:void(0);" onClick="javascript:addItemPlpToWishlist('${uiSequenceScreen}_${plpProduct.productId}');" class="standardBtn addToWishlist <#if plpFeatureOrder?has_content && plpFeatureOrder?size gt 0>inactiveAddToWishlist</#if>" id="addToWishlist_${uiSequenceScreen}_${plpProduct.productId}"><span>${uiLabelMap.AddToWishlistBtn}</span></a>
   </div>
</li>