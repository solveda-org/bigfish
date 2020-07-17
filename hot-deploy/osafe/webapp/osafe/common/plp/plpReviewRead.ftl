 <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!""}')/>
  <#if productFeatureType?has_content && featureValueSelected?has_content>
      <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}&productFeatureType=${productFeatureType!""}:${featureValueSelected!""}')/>
  </#if>
  <div class="plpReviewRead">
    <div class="customerRatingLinks">
      <#assign productCalculatedInfo = delegator.findByPrimaryKeyCache("ProductCalculatedInfo", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId!""))?if_exists>
      <#if productCalculatedInfo?has_content>
        <#assign averageCustomerRating=productCalculatedInfo.getBigDecimal("averageCustomerRating")?if_exists/>
         <#if averageCustomerRating?has_content>
           <#assign averageCustomerRating=averageCustomerRating.setScale(decimals,rounding)/>
         <#else>
           <#assign averageCustomerRating=0/>
         </#if>
       </#if>
    <!-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
    <#if averageCustomerRating?has_content && (averageCustomerRating > 0)>
        <a class="pdpUrl review" href="${productFriendlyUrl!""}#productReviews" title="Read all reviews" id="seeReviewLink_${productId!}"><span>${uiLabelMap.ReadLabel} ${reviewPLPSize} ${uiLabelMap.ReviewsLabel}</span></a>
    </#if>
    </div>
  </div>
