<#if reviewSize?has_content  && (reviewSize > 0)>
  <li class="${request.getAttribute("attributeClass")!}">
    <div class="pdpReviewRead">
      <div class="customerRatingLinks">
          <a class="js_pdpUrl js_review" href="#productReviews" title="Read all reviews" ><span>${uiLabelMap.ReadLabel} ${reviewSize} ${uiLabelMap.ReviewsLabel}</span></a>
      </div>
    </div>
  </li>
</#if>