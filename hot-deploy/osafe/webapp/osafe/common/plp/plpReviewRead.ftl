<div class="plpReviewRead">
  <div class="customerRatingLinks">
  <!-- using class pdpUrl for preparing PDP URL according to the selected swatch. -->
    <#if plpReviewSize?has_content && (plpReviewSize > 0)>
      <a class="pdpUrl review" href="${plpProductFriendlyUrl!""}#productReviews" title="Read all reviews" id="seeReviewLink_${plpProductId!}"><span>${uiLabelMap.ReadLabel} ${plpReviewSize} ${uiLabelMap.ReviewsLabel}</span></a>
    </#if>
  </div>
</div>
