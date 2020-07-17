<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_ACTIVE_FLAG!"")>
  <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_WRITE_REVIEW!"")>
    <div class="plpReviewWrite">
       <div class="customerRatingLinks">
          <#assign averageCustomerRating = Static["org.ofbiz.product.product.ProductWorker"].getAverageProductRating(delegator,productId)>
          <#if averageCustomerRating?has_content && (averageCustomerRating > 0)>
              <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${productId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.WriteReviewLabel}" id="submitPageReview">${uiLabelMap.WriteReviewLabel}</a>
          <#else>
              <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${productId?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.FirstToReviewLabel}" id="submitPageReview"><span>${uiLabelMap.FirstToReviewLabel}</span></a>
          </#if>
         </div>    
      </div>
  </#if>
</#if>