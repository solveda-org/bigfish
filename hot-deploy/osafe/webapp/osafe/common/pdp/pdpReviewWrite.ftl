<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_ACTIVE_FLAG!"")>
  <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_WRITE_REVIEW!"")>
    <div class="pdpReviewWrite">
       <div class="customerRatingLinks">
			<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_AS_GUEST!"")>
				  <#if averageStarRating?exists>
					  <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${product_id?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.WriteReviewLabel}" id="submitPageReview">${uiLabelMap.WriteReviewLabel}</a>
				  <#else>
					  <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${product_id?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.FirstToReviewLabel}" id="submitPageReview"><span>${uiLabelMap.FirstToReviewLabel}</span></a>
				  </#if>
			<#else>
				  <#if averageStarRating?exists>
					  <a href="<@ofbizUrl>eCommerceProductReviewSubmitLogged?productId=${product_id?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.WriteReviewLabel}" id="submitPageReview">${uiLabelMap.WriteReviewLabel}</a>
				  <#else>
					  <a href="<@ofbizUrl>eCommerceProductReviewSubmitLogged?productId=${product_id?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.FirstToReviewLabel}" id="submitPageReview"><span>${uiLabelMap.FirstToReviewLabel}</span></a>
				  </#if>
			</#if>
         </div>    
      </div>
  </#if>
</#if>