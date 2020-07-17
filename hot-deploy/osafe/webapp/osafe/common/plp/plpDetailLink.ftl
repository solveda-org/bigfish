<#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!""}')/>
<#if productFeatureType?has_content && featureValueSelected?has_content>
  <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}&productFeatureType=${productFeatureType!""}:${featureValueSelected!""}')/>
</#if>
<div class="plpDetailLink">
<!-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
<a class="eCommerceProductLink pdpUrl" title="${productName!""}" href="${productFriendlyUrl!""}" id="detailLink_${productId!}"><span>${productName!""}</span></a>
</div>