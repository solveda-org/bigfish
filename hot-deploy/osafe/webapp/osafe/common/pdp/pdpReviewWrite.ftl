<#assign reviewMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"REVIEW_METHOD")!""/>
<#if reviewMethod?has_content >
	<#if (reviewMethod.toUpperCase() == "BIGFISH")>
	  <li class="${request.getAttribute("attributeClass")!} <#if !reviewSize?has_content  || (reviewSize == 0)> noReviews</#if>">
		<div class="pdpReviewWrite">
		   <div class="customerRatingLinks">
		        <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"REVIEW_AS_GUEST")>
		              <#if reviewSize?has_content  && (reviewSize > 0)>
		                  <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${product_id?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.WriteReviewLabel}" id="submitPageReview"><span>${uiLabelMap.WriteReviewLabel}</span></a>
		              <#else>
		                  <a href="<@ofbizUrl>eCommerceProductReviewSubmit?productId=${product_id?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.FirstToReviewLabel}" id="submitPageReview"><span>${uiLabelMap.FirstToReviewLabel}</span></a>
		              </#if>
		        <#else>
		              <#if reviewSize?has_content  && (reviewSize > 0)>
		                  <a href="<@ofbizUrl>eCommerceProductReviewSubmitLogged?productId=${product_id?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.WriteReviewLabel}" id="submitPageReview"><span>${uiLabelMap.WriteReviewLabel}</span></a>
		              <#else>
		                  <a href="<@ofbizUrl>eCommerceProductReviewSubmitLogged?productId=${product_id?if_exists}&productCategoryId=${productCategoryId?if_exists}<#if !userLogin?has_content>&amp;review=review</#if></@ofbizUrl>" title="${uiLabelMap.FirstToReviewLabel}" id="submitPageReview"><span>${uiLabelMap.FirstToReviewLabel}</span></a>
		              </#if>
		        </#if>
		   </div>    
		</div>
      </li>
	</#if>
</#if>
