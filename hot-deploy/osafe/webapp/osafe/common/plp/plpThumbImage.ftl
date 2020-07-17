<#-- the value of uiSequenceScreen is being reset in the call to plpFlyoutDivSquence. So set it here -->
<#assign uiSequenceScreenHolder = uiSequenceScreen!"" /> 
<li class="${request.getAttribute("attributeClass")!}">
 <div class="js_eCommerceThumbNailHolder eCommerceThumbNailHolder">
    <div id="js_plpThumbImageContainer_${uiSequenceScreenHolder}_${plpProduct.productId}">
        <div class="js_swatchProduct">
        	
	        <a class="pdpUrl js_pdpUrl toolTipLink" title="${plpProductName!""}" href="${plpProductFriendlyUrl!"#"}" id="${plpProductId!}" <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PLP_FLYOUT_ACTIVE")>onMouseover="showPLPFlyout(this);" onMouseout="hideTooltip()"</#if>>
	            <img alt="${plpProductName!""}" title="${plpProductName!""}" src="${plpProductImageUrl}" class="productThumbnailImage" <#if thumbImageHeight?has_content> height="${thumbImageHeight!""}"</#if> <#if thumbImageWidth?has_content> width="${thumbImageWidth!""}"</#if> <#if plpProductImageAltUrl?has_content && plpProductImageAltUrl != ''> onmouseover="src='${plpProductImageAltUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});" onmouseout="src='${plpProductImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});"</#if> onerror="onImgError(this, 'PLP-Thumb');"/>
	            <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PLP_FLYOUT_ACTIVE")>
	            	<div class="js_tooltipHtmlHolder" style="display:none">
	            		<div class="js_tooltipHtml">
			            	${setRequestAttribute("plpItem",plpProduct)}
			            	${screens.render("component://osafe/widget/EcommerceDivScreens.xml#plpFlyoutDivSequence")}
		            	</div>
	            	</div>
	            </#if>
	        </a>
	    </div>
    </div>
	<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"QUICKLOOK_ACTIVE") && uiSequenceScreenHolder?has_content && uiSequenceScreenHolder == 'PLP'>
	  <div id="plpQuicklook_${plpProductId!""}" class="js_plpQuicklook plpQuicklookIcon">
	    <input type="hidden" class="param" name="productId" id="productId" value="${plpProductId!}"/>
	    <input type="hidden" class="param" name="productCategoryId" value="${plpCategoryId!}"/>
	    <#if plpProductFeatureType?has_content && featureValueSelected?has_content>
            <#assign featureValue = plpProductFeatureType+':'+featureValueSelected/>
        </#if>
	    <input type="hidden" class="param" name="productFeatureType" id="${plpProductId!}_productFeatureType" value="${featureValue!""}"/>
	    <a href="javaScript:void(0);" onClick="displayActionDialogBoxQuicklook('${dialogPurpose!}',this);"><img style="display:none" alt="${plpProductName!""}" src="/osafe_theme/images/user_content/images/quickLook.png" /></a>
	  </div>
	</#if>
	
	<#-- Initial plpItemFlyout implementation
	
	<div id="tooltip" class="plpItemFlyout js_tooltip js_plpItemFlyout_${plpProductId!}">
	  <div id="tooltipTop" class="js_tooltipTop"></div>
	  <div class="tooltipMiddle" id="">
		  <span id="tooltipText" class="js_tooltipText">
		  	${setRequestAttribute("plpItem",plpProduct)}
			${screens.render("component://osafe/widget/EcommerceDivScreens.xml#plpItemFlyoutDivSequence")}
		  </span>
	  </div>
	  <div id="tooltipBottom" class="js_tooltipBottom"></div>
	</div>
	-->
	
 </div>
 


 
 
 <#if plpProductVariantMapKeys?exists && plpProductVariantMapKeys?has_content>
	  <#list plpProductVariantMapKeys as key>
	    <#assign variantProdCtntWrapper = plpProductVariantContentWrapperMap.get('${key}')!/>
	    <#assign variantContentIdMap = plpProductVariantProductContentIdMap.get('${key}')!""/>
	    <#assign productSmallImageUrl = plpProductImageUrl!""/>
	    <#if variantProdCtntWrapper?has_content >
	        <#assign productSmallImageUrl = variantProdCtntWrapper.get("SMALL_IMAGE_URL")!""/>
	        <#assign productAltImageUrl = variantProdCtntWrapper.get("SMALL_IMAGE_ALT_URL")!""/>
	    </#if>
	    <#if productSmallImageUrl?has_content && productSmallImageUrl!=''>
		    <div class="js_swatchProduct_${key}" id="js_swatchProduct_${key}" style="display:none">
		        <a class="pdpUrl" title="${plpProductName!""}" href="${plpProductFriendlyUrl!"#"}" id="${plpProductId!}">
		            <img alt="${plpProductName!""}" title="${plpProductName!""}" src="${productSmallImageUrl}" class="productThumbnailImage" <#if thumbImageHeight?has_content> height="${thumbImageHeight!""}"</#if> <#if thumbImageWidth?has_content> width="${thumbImageWidth!""}"</#if> <#if plpProductImageAltUrl?has_content && plpProductImageAltUrl != ''> onmouseover="src='${productAltImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});" onmouseout="src='${productSmallImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});"</#if> onerror="onImgError(this, 'PLP-Thumb');"/>
		        </a>
		    </div>
	    </#if>  
	  </#list>
	</#if>
</li>