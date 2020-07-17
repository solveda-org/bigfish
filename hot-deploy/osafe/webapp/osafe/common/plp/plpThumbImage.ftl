<div class="plpThumbImage">
 <div class="eCommerceThumbNailHolder">
    <div class="swatchProduct">
    <!-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
        <a class="pdpUrl" title="${productName!""}" href="${productFriendlyUrl!"#"}" id="${productId!}">
            <img alt="${productName!""}" title="${productName!""}" src="${productImageUrl}" class="productThumbnailImage" <#if IMG_SIZE_PLP_H?has_content> height="${thumbImageHeight!""}"</#if> <#if IMG_SIZE_PLP_W?has_content> width="${thumbImageWidth!""}"</#if> <#if productImageAltUrl?has_content && productImageAltUrl != ''> onmouseover="src='${productImageAltUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});" onmouseout="src='${productImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});"</#if> onerror="onImgError(this, 'PLP-Thumb');"/>
        </a>
    </div>
	<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(QUICKLOOK_ACTIVE) && uiSequenceScreen?has_content && uiSequenceScreen == 'PLP'>
	  <div id="plpQuicklook_${productId!""}" class="plpQuicklook" style="display:none">
	    <input type="hidden" class="param" name="productId" id="productId" value="${productId!}"/>
	    <input type="hidden" class="param" name="productCategoryId" value="${categoryId!}"/>
	    <input type="hidden" class="param" name="productFeatureType" id="${productId!}_productFeatureType" value=""/>
	    <a href="javaScript:void(0);" onClick="displayActionDialogBox('${dialogPurpose!}',this);"><img alt="${productName!""}" src="/osafe_theme/images/user_content/images/quickLook.png"></a>
	  </div>
	</#if>
 </div>
</div>
 
 
 