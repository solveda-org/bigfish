<#if product?has_content>
<div class="linkButton">
  <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
  <#if !showImageLink?has_content>
      <#assign showImageLink = "true"/>
  </#if>
  <#if !showProductFeatureLink?has_content>
	  <#assign showProductFeatureLink = "true"/>
  </#if>
  <#if !showVariantLink?has_content>
	  <#assign showVariantLink = "true"/>
  </#if>
  <#if !showMetaTagLink?has_content>
	  <#assign showMetaTagLink = "true"/>
  </#if>
  <#if !showPricingLink?has_content>
	  <#assign showPricingLink = "true"/>
  </#if>
  <#if !showRelatedLink?has_content>
	  <#assign showRelatedLink = "true"/>
  </#if>
  <#if !showCategoryMemberLink?has_content>
	  <#assign showCategoryMemberLink = "true"/>
  </#if>
  <#if !showVideoLink?has_content>
	  <#assign showVideoLink = "true"/>
  </#if>
  
  <#if productContentWrapper?exists>
      <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
  </#if>
  
  <#if showImageLink == 'true'>
    <a href="<@ofbizUrl>productImages?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'${uiLabelMap.ProductImagesTooltip}','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
  </#if>
  
  <#if showProductFeatureLink == 'true'>
   <#if (product.isVariant?if_exists == 'N')>
     <#assign features = product.getRelated("ProductFeatureAppl")/>
     <#if features?exists && features?has_content>
       <a href="<@ofbizUrl>productFeatures?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductFeaturesTooltip}');" onMouseout="hideTooltip()"><span class="featureIcon"></span></a>
     </#if>
   </#if>
  </#if>
  
  <#if showVariantLink == 'true'>
    <#if (product.isVirtual?if_exists == 'Y') && (product.isVariant?if_exists == 'N')>
        <a href="<@ofbizUrl>productVariants?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductVariantsTooltip}');" onMouseout="hideTooltip()"><span class="variantIcon"></span></a>
    </#if>
  </#if>
  
  <#if showMetaTagLink == 'true'>
    <a href="<@ofbizUrl>productMetatag?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.HtmlMetatagTooltip}');" onMouseout="hideTooltip()"><span class="metatagIcon"></span></a>
  </#if>
  
  <#if showPricingLink == 'true'>
    <a href="<@ofbizUrl>productPrice?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductPricingTooltip}');" onMouseout="hideTooltip()"><span class="priceIcon"></span></a>
  </#if>
  
  <#if showRelatedLink == 'true'>
    <a href="<@ofbizUrl>relatedProductsDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageRelatedProductsTooltip}');" onMouseout="hideTooltip()"><span class="relatedIcon"></span></a>
  </#if>
  
  <#if showCategoryMemberLink == 'true'>
    <#if product.isVariant?if_exists == 'N'>
        <a href="<@ofbizUrl>productCategoryMembershipDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductcategoryMembershipTooltip}');" onMouseout="hideTooltip()"><span class="membershipIcon"></span></a>
    </#if>
  </#if>
  
  <#if showVideoLink == 'true'>
    <a href="<@ofbizUrl>productVideo?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductVideoTooltip}');" onMouseout="hideTooltip()"><span class="videoIcon"></span></a>
  </#if>
</div>
</#if>
