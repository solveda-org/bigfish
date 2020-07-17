<#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!""}')/>
<div class="plpDetailLink">
<!-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
<a class="eCommerceProductLink pdpUrl" title="${productName!""}" href="${productFriendlyUrl!""}" id="detailLink_${productId!}"><span>${productName!""}</span></a>
</div>
           