<#if Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(request,"PDP_WISHLIST_ACTIVE_FLAG")>
    <div class="pdpAddToWishlist">
        <a href="javascript:void(0);" onClick="javascript:addItem('addToWishlist');" class="standardBtn addToWishlist" id="addToWishlist"><span>${uiLabelMap.AddToWishlistBtn}</span></a>
    </div>
</#if>