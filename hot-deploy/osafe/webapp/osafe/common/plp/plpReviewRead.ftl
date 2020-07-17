<#if plpReviewSize?has_content && (plpReviewSize > 0)>
	<li class="${request.getAttribute("attributeClass")!}">
	 <div>
	      <a class="js_pdpUrl js_review" href="${plpProductFriendlyUrl!""}#productReviews" title="Read all reviews" id="seeReviewLink_${plpProductId!}"><span>${uiLabelMap.ReadLabel} ${plpReviewSize} ${uiLabelMap.ReviewsLabel}</span></a>
	 </div>
	</li> 
</#if>  
