<!-- start productReviewDetail.ftl -->
<#if review?has_content>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.ReviewTitleCaption}</label>
         </div>
         <div class="infoValue">
            <input type="text" class="medium" name="reviewTitle" id="reviewTitle" size="32" maxlength="100" value="${parameters.reviewTitle!review.reviewTitle!""}"/>
         </div>
       </div>
    </div>

    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.ReviewTextCaption}</label>
         </div>
         <div class="infoValue">
            <textarea name="productReview" id="productReviewArea" cols="50" rows="5">${parameters.productReview!review.productReview!""}</textarea>
         </div>
       </div>
    </div>

    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.PrivateCommentCaption}</label>
         </div>
         <div class="infoValue">
            <textarea class="smallArea" name="reviewPrivateNote" id="privateCommentArea" cols="50" rows="3">${parameters.reviewPrivateNote!review.reviewPrivateNote!""}</textarea>
         </div>
       </div>
    </div>

    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.ReviewResponseCaption}</label>
         </div>
         <div class="infoValue">
            <textarea class="smallArea" name="reviewResponse" id="reviewResponseArea" cols="50" rows="3">${parameters.reviewResponse!review.reviewResponse!""}</textarea>
         </div>
       </div>
    </div>

</#if>
<!-- end productReviewDetail.ftl -->


