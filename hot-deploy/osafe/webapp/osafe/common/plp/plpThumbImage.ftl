<li class="${request.getAttribute("attributeClass")!}">
 <div class="js_eCommerceThumbNailHolder eCommerceThumbNailHolder">
    <div class="js_swatchProduct">
    <#-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
        <a class="pdpUrl" title="${plpProductName!""}" href="${plpProductFriendlyUrl!"#"}" id="${plpProductId!}">
            <img alt="${plpProductName!""}" title="${plpProductName!""}" src="${plpProductImageUrl}" class="productThumbnailImage" <#if thumbImageHeight?has_content> height="${thumbImageHeight!""}"</#if> <#if thumbImageWidth?has_content> width="${thumbImageWidth!""}"</#if> <#if plpProductImageAltUrl?has_content && plpProductImageAltUrl != ''> onmouseover="src='${plpProductImageAltUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});" onmouseout="src='${plpProductImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});"</#if> onerror="onImgError(this, 'PLP-Thumb');"/>
        </a>
    </div>
	<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"QUICKLOOK_ACTIVE") && uiSequenceScreen?has_content && uiSequenceScreen == 'PLP'>
	  <div id="plpQuicklook_${plpProductId!""}" class="js_plpQuicklook plpQuicklookIcon" style="display:none">
	    <input type="hidden" class="param" name="productId" id="productId" value="${plpProductId!}"/>
	    <input type="hidden" class="param" name="productCategoryId" value="${plpCategoryId!}"/>
	    <#if plpProductFeatureType?has_content && featureValueSelected?has_content>
            <#assign featureValue = plpProductFeatureType+':'+featureValueSelected/>
        </#if>
	    <input type="hidden" class="param" name="productFeatureType" id="${plpProductId!}_productFeatureType" value="${featureValue!""}"/>
	    <a href="javaScript:void(0);" onClick="displayActionDialogBox('${dialogPurpose!}',this);"><img alt="${plpProductName!""}" src="/osafe_theme/images/user_content/images/quickLook.png"></a>
	  </div>
	</#if>
 </div>
</li>
 
 
 