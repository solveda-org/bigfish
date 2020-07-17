<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_ACTIVE_FLAG!"")>
  <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(REVIEW_WRITE_REVIEW!"")>
      <div class="pdpReviewRead">
        <div class="customerRatingLinks">
        <#if averageStarRating?has_content  && (averageStarRating > 0)>
            <a class="pdpUrl review" href="#productReviews" title="Read all reviews" ><span>${uiLabelMap.ReadLabel} ${reviewSize} ${uiLabelMap.ReviewsLabel}</span></a>
        </#if>
        </div>
      </div>
  </#if>
</#if>  