<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_ACTIVE_FLAG!"")>
  <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_WRITE_REVIEW!"")>
     <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!""}')/>
      <#if productFeatureType?has_content && featureValueSelected?has_content>
          <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}&productFeatureType=${productFeatureType!""}:${featureValueSelected!""}')/>
      </#if>
      <div class="plpReviewRead">
        <div class="customerRatingLinks">
        <#assign averageCustomerRating = Static["org.ofbiz.product.product.ProductWorker"].getAverageProductRating(delegator,productId)>
        <!-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
        <#if averageCustomerRating?has_content && (averageCustomerRating > 0)>
            <a class="pdpUrl review" href="${productFriendlyUrl!""}#productReviews" title="Read all reviews" id="seeReviewLink_${productId!}"><span>${uiLabelMap.ReadLabel} ${reviewSize} ${uiLabelMap.ReviewsLabel}</span></a>
        </#if>
        </div>
      </div>
  </#if>
</#if>