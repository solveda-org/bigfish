<div class="linkButton">
  <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
  
  <#if showImageLink?has_content && showImageLink == 'true'>
    <#if productContentWrapper?exists>
      <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
    </#if>
    <a href="<@ofbizUrl>productImages?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'${uiLabelMap.ProductImagesTooltip}','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
  </#if>
  
  <#if showProductFeatureLink?has_content && showProductFeatureLink == 'true'>
   <#if (product.isVariant?if_exists == 'N')>
     <#assign features = product.getRelated("ProductFeatureAppl")/>
     <#if features?exists && features?has_content>
       <a href="<@ofbizUrl>productFeatures?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductFeaturesTooltip}');" onMouseout="hideTooltip()"><span class="featureIcon"></span></a>
     </#if>
   </#if>
  </#if>
  
  <#if showVariantLink?has_content && showVariantLink == 'true'>
    <#if (product.isVirtual?if_exists == 'Y') && (product.isVariant?if_exists == 'N')>
      <#assign variants = delegator.findByAnd("ProductAssoc", {"productId" : product.productId, "productAssocTypeId" : "PRODUCT_VARIANT"})/>
      <#if variants?exists && variants?has_content>
        <a href="<@ofbizUrl>productVariants?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductVariantsTooltip}');" onMouseout="hideTooltip()"><span class="variantIcon"></span></a>
      </#if>
    </#if>
  </#if>
  
  <#if showMetaTagLink?has_content && showMetaTagLink == 'true'>
    <a href="<@ofbizUrl>productMetatag?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.HtmlMetatagTooltip}');" onMouseout="hideTooltip()"><span class="metatagIcon"></span></a>
  </#if>
  
  <#if showPricingLink?has_content && showPricingLink == 'true'>
    <a href="<@ofbizUrl>productPrice?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductPricingTooltip}');" onMouseout="hideTooltip()"><span class="priceIcon"></span></a>
  </#if>
  
  <#if showRelatedLink?has_content && showRelatedLink == 'true'>
    <a href="<@ofbizUrl>relatedProductsDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageRelatedProductsTooltip}');" onMouseout="hideTooltip()"><span class="relatedIcon"></span></a>
  </#if>
  
  <#if showVideoLink?has_content && showVideoLink == 'true'>
    <a href="<@ofbizUrl>productVideo?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductVideoTooltip}');" onMouseout="hideTooltip()"><span class="videoIcon"></span></a>
  </#if>
</div>
