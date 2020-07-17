<div class="plpProductNameSeq">
<div class="plpProductName">
 <span>${productName!""}</span>
</div>
</div>
<div class="plpThumbImageSeq">
<div class="plpThumbImage">
 <div class="eCommerceThumbNailHolder">
    <div class="swatchProduct">
    <!-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
        <a class="pdpUrl" title="${productName!""}" href="${productFriendlyUrl!"#"}" id="${productId!}">
            <img alt="${productName!""}" title="${productName!""}" src="${productImageUrl}" class="productThumbnailImage" <#if thumbImageHeight?has_content> height="${thumbImageHeight!""}"</#if> <#if thumbImageWidth?has_content> width="${thumbImageWidth!""}"</#if> <#if productImageAltUrl?has_content && productImageAltUrl != ''> onmouseover="src='${productImageAltUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});" onmouseout="src='${productImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});"</#if> onerror="onImgError(this, 'PLP-Thumb');"/>
        </a>
    </div>
	<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"QUICKLOOK_ACTIVE") && uiSequenceScreen?has_content && uiSequenceScreen == 'PLP'>
	  <div id="plpQuicklook_${productId!""}" class="plpQuicklook" style="display:none">
	    <input type="hidden" class="param" name="productId" id="productId" value="${productId!}"/>
	    <input type="hidden" class="param" name="productCategoryId" value="${categoryId!}"/>
	    <#if productFeatureType?has_content && featureValueSelected?has_content>
            <#assign featureValue = productFeatureType+':'+featureValueSelected/>
        </#if>
	    <input type="hidden" class="param" name="productFeatureType" id="${productId!}_productFeatureType" value="${featureValue!""}"/>
	    <a href="javaScript:void(0);" onClick="displayActionDialogBox('${dialogPurpose!}',this);"><img alt="${productName!""}" src="/osafe_theme/images/user_content/images/quickLook.png"></a>
	  </div>
	</#if>
 </div>
</div>
</div>
<div class="plpTertiaryInfoSeq">
 <#if plpLabel?has_content>
	 <div class="plpTertiaryInfo">
	   <p class="tertiaryInformation">${plpLabel!""}</p>
	 </div>
 <#else>
	  <div class="plpTertiaryInfo">
	     <#if productInternalName?has_content>
	       <p class="tertiaryInformation">${uiLabelMap.InternalNameLabel}&nbsp;${productInternalName!""}</p>
	     </#if>
	  </div>
</#if>
</div>
<div class="plpPriceOnlineSeq">
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="plpPriceOnline">
  <#if price?exists && price?has_content>
    <p class="price">${uiLabelMap.PlpPriceLabel} <@ofbizCurrency amount=price isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
  </#if>
</div>
</div>
<div class="plpReviewStarsSeq">
<#assign reviewMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"REVIEW_METHOD")!""/>
<#if reviewMethod?has_content >
	<#if (reviewMethod.toUpperCase() == "BIGFISH") || (reviewMethod.toUpperCase() == "REEVOO")>
		<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"REVIEW_SHOW_ON_PLP")>
		 <div class="plpReviewStars">
	    	<#if reviewMethod.toUpperCase() == "REEVOO">
	    		<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"REEVOO_SHOW_ON_PLP")>
			        <#assign reevooBadgeurl = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_BADGE_URL")!""/>
			        <#assign reevooTrkref = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_TRKREF")!""/>
			        <#assign reevooSku = "">
			        <#assign skuProduct = delegator.findByPrimaryKeyCache("GoodIdentification", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId!"", "goodIdentificationTypeId", "SKU"))?if_exists />
			        <#if skuProduct?has_content>
			            <#assign reevooSku = skuProduct.idValue!"">
			        <#else>
			            <#assign reevooSku = productId!"">
			        </#if>
			        <#assign reevooBadgeurl = reevooBadgeurl.concat("/").concat(reevooTrkref!"").concat("/").concat(reevooSku!"")>
			        <div class="reevoo-area">
			            <a class="reevoomark" href="${reevooBadgeurl}">${uiLabelMap.ReevooProductReviewLabel}</a>
			        </div>
		        </#if>
	        </#if>
	    	<#if reviewMethod.toUpperCase() == "BIGFISH">
		        <#if averageStarPLPRating?has_content>
		            <#assign ratePercentage= ((averageStarPLPRating / 5) * 100)>
		            <div class="rating_bar"><div style="width:${ratePercentage}%"></div></div>
		        </#if>
	        </#if>
		 </div>
		</#if>
	</#if>
</#if>
</div>