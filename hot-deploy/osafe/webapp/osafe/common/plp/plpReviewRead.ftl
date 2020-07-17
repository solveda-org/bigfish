 <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!""}')/>
  <#if productFeatureType?has_content && featureValueSelected?has_content>
      <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}&productFeatureType=${productFeatureType!""}:${featureValueSelected!""}')/>
  </#if>
  <div class="plpReviewRead">
    <div class="customerRatingLinks">
    <!-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
    <#if reviewPLPSize?has_content && (reviewPLPSize > 0)>
        <a class="pdpUrl review" href="${productFriendlyUrl!""}#productReviews" title="Read all reviews" id="seeReviewLink_${productId!}"><span>${uiLabelMap.ReadLabel} ${reviewPLPSize} ${uiLabelMap.ReviewsLabel}</span></a>
    </#if>
    </div>
  </div>
