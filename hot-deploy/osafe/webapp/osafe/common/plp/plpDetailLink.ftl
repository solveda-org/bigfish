<#assign productUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!""}')/>
<div class="plpDetailLink">
<a class="eCommerceProductLink" title="${productName!""}" href="${productUrl!""}" id="detailLink_${productId!}">
    ${productName!""}
</a>
</div>
           