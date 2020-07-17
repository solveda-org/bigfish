<#assign reviewMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"REVIEW_METHOD")!""/>
<#if reviewMethod?has_content >
	<#if (reviewMethod.toUpperCase() == "BIGFISH")>
		<div class="plpReviewWrite">
			   <div class="customerRatingLinks">
			      <#if plpReviewSize?has_content && (plpReviewSize > 0)>
			          <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${plpProductId?if_exists}&productCategoryId=${plpCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.WriteReviewLabel}" id="submitPageReview">${uiLabelMap.WriteReviewLabel}</a>
			      <#else>
			          <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${plpProductId?if_exists}&productCategoryId=${plpCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.FirstToReviewLabel}" id="submitPageReview"><span>${uiLabelMap.FirstToReviewLabel}</span></a>
			      </#if>
			   </div>    
		</div>
	</#if>
</#if>
