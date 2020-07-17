<div class="plpThumbImage">
 <div class="eCommerceThumbNailHolder">
    <div class="swatchProduct">
        <a title="${productName!""}" href="${productUrl!"#"}" id="${productId!}">
            <img alt="${productName!""}" title="${productName!""}" src="${productImageUrl}" class="productThumbnailImage" <#if IMG_SIZE_PLP_H?has_content> height="${thumbImageHeight!""}"</#if> <#if IMG_SIZE_PLP_W?has_content> width="${thumbImageWidth!""}"</#if> <#if productImageAltUrl?has_content && productImageAltUrl != ''> onmouseover="src='${productImageAltUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});" onmouseout="src='${productImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});"</#if> onerror="onImgError(this, 'PLP-Thumb');"/>
        </a>
    </div>
	<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(QUICKLOOK_ACTIVE)>
	  <div id="plpQuickLook_${productId!""}" class="plpQuickLook" style="display:none">
	    <input type="hidden" class="param" name="productId" id="productId" value="${productId!}"/>
	    <input type="hidden" class="param" name="categoryId" value="${categoryId!}"/>
	    <input type="hidden" class="param" name="productFeatureType" id="${productId!}_productFeatureType" value=""/>
	    <a href="javaScript:void(0);" onClick="displayActionDialogBox('${dialogPurpose!}',this);"><img alt="${productName!""}" src="/osafe_theme/images/user_content/images/quickLook.png"></a>
	  </div>
	</#if>
 </div>
</div>
 
 
 