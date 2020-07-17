<#assign productUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${parameters.categoryId!""}')/>
<#assign featureType = ""/>
<#if parameters.productFeatureType?has_content>
    <#assign productFeatureValueList = parameters.productFeatureType.split(":") />
    <#assign featureType = productFeatureValueList[0]/>
</#if>

<#if (productUrl?exists && productUrl.indexOf("?") > 0)>
  <input type="hidden" id="pdpUrl" value="${productUrl!}&amp;productFeatureType=${featureType}:" />
  <#assign productUrl = productUrl + "&amp;productFeatureType=${parameters.productFeatureType!}">
<#else>
  <input type="hidden" id="pdpUrl" value="${productUrl!}?productFeatureType=${featureType}:" />
  <#assign productUrl = productUrl + "?productFeatureType=${parameters.productFeatureType!}">
</#if>
<div class="plpSeeItemDetails">
<a class="seeItemDetail" title="${productName!""}" href="${productUrl!""}" id="detailLink_${productId!}">
    ${uiLabelMap.SeeItemDetailsLabel}
</a>
</div>