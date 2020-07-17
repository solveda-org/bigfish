<#assign reviewMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"REVIEW_METHOD")!""/>
<#if reviewMethod?has_content >
	<#if (reviewMethod != "NONE") && (reviewMethod != "")>
		<div class="plpReviewWrite">
			   <div class="customerRatingLinks">
			      <#if reviewPLPSize?has_content && (reviewPLPSize > 0)>
			          <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${productId?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.WriteReviewLabel}" id="submitPageReview">${uiLabelMap.WriteReviewLabel}</a>
			      <#else>
			          <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${productId?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.FirstToReviewLabel}" id="submitPageReview"><span>${uiLabelMap.FirstToReviewLabel}</span></a>
			      </#if>
			   </div>    
		</div>
	</#if>
</#if>
