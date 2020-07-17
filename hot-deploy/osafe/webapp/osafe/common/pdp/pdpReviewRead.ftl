  <div class="pdpReviewRead">
    <div class="customerRatingLinks">
    <#if averageStarRating?has_content  && (averageStarRating > 0)>
        <a class="pdpUrl review" href="#productReviews" title="Read all reviews" ><span>${uiLabelMap.ReadLabel} ${reviewSize} ${uiLabelMap.ReviewsLabel}</span></a>
    </#if>
    </div>
  </div>
