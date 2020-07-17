<#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}')/>

<#if parameters.productFeatureType?has_content>
  <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}&productFeatureType=${StringUtil.wrapString(parameters.productFeatureType!)}')/>
</#if>

<div class="plpSeeItemDetails">
  <a class="seeItemDetail pdpUrl" title="${productName!""}" href="${productFriendlyUrl!""}"><span>${uiLabelMap.SeeItemDetailsLabel}</span></a>
</div>