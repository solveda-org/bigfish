<li class="${request.getAttribute("attributeClass")!}">
  <#if pdpSelectMultiVariant?exists && pdpSelectMultiVariant?has_content && ((pdpSelectMultiVariant.toUpperCase() == "QTY") || (pdpSelectMultiVariant.toUpperCase() == "CHECKBOX")) >
      <div id="js_addToWishlist_div">
          <a href="javascript:void(0);" onClick="javascript:addMultiItemsToWishlist('${pdpSelectMultiVariant}');" class="standardBtn addToWishlist" id="js_addToWishlist"><span>${uiLabelMap.AddToWishlistBtn}</span></a>
      </div>
  <#else>
	  <div id="js_addToWishlist_div">
	      <a href="javascript:void(0);" onClick="javascript:addItemToWishlist();" <#if (featureOrder?exists && featureOrder?size gt 0) || (isPdpInStoreOnly?exists && isPdpInStoreOnly == "Y")>class="standardBtn addToWishlist inactiveAddToWishlist"<#else>class="standardBtn addToWishlist"</#if> id="js_addToWishlist"><span>${uiLabelMap.AddToWishlistBtn}</span></a>
	  </div>
  </#if>
</li>
