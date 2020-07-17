<!-- start productReviewDetail.ftl -->
<#if review?has_content>
  <#assign product = review.getRelatedOne("Product")>
  <#assign statusItem = review.getRelatedOne("StatusItem")!>
  <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product,request)>
  <#assign productName = productContentWrapper.get("PRODUCT_NAME")!product.productName!"">
  <#assign rating=review.productRating!>
  <#assign ratingDir=Static["org.ofbiz.base.util.UtilFormatOut"].replaceString(""+rating,".","_")/>
  <#if ratingDir.length() == 1>
    <#assign ratingDir = ratingDir + "_0">
   </#if>

<div class="infoRow column">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ReviewIdCaption}</label>
     </div>
     <div class="infoValue">
       ${review.productReviewId!""}
     </div>
   </div>
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ReviewNickNameCaption}</label>
     </div>
     <div class="infoValue">
       ${review.reviewNickName!""}
     </div>
   </div>
    <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.DatePostedCaption}</label>
     </div>
     <div class="infoValue">
       ${review.postedDateTime?string(preferredDateFormat)}
     </div>
   </div>
</div>
<div class="infoRow column">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ProductNoCaption}</label>
     </div>
     <div class="infoValue">
       ${review.productId!""}
     </div>
   </div>
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ProductNameCaption}</label>
     </div>
     <div class="infoValue">
       ${productName}
     </div>
   </div>
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.DaysSincePostedCaption}</label>
     </div>
     <div class="infoValue">
       ${postedInterval!""}
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry long">
     <div class="infoCaption">
      <label>${uiLabelMap.StarsCaption}</label>
     </div>
     <div class="infoValue">
       <#assign ratePercentage= ((rating / 5) * 100)>
       <div class="rating_bar"><div id="productRatingStars" style="width:${ratePercentage}%"></div></div>
     </div>
    <div class="starsButtons">
        <input type="button" class="standardBtn secondary" name="Star_1_0_Btn" value="1" onClick="setStars('1');"/>
        <input type="button" class="standardBtn secondary" name="Star_2_0_Btn" value="2" onClick="setStars('2');"/>
        <input type="button" class="standardBtn secondary" name="Star_3_0_Btn" value="3" onClick="setStars('3');"/>
        <input type="button" class="standardBtn secondary" name="Star_4_0_Btn" value="4" onClick="setStars('4');"/>
        <input type="button" class="standardBtn secondary" name="Star_5_0_Btn" value="5" onClick="setStars('5');"/>
    </div>
   </div>
</div>
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
<div class="infoRow">
   <div class="infoEntry long">
     <div class="infoCaption">
      <label>${uiLabelMap.StatusCaption}</label>
     </div>
     <div class="infoValue statusItem">
       <span id="reviewStatus">${(statusItem.get("description",locale)?default(statusItem.statusId?default("N/A")))!}</span>
     </div>
    <div class="statusButtons">
        <div class="PRR_PENDING">
            <input type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.ApproveBtn}" onClick="updateReview('PRR_APPROVED');"/>
            <input type="button" class="standardBtn secondary" name="rejectBtn" value="${uiLabelMap.DeleteBtn}"  onClick="updateReview('PRR_DELETED');"/>
        </div>
        <div class="PRR_APPROVED">
            <input type="button" class="standardBtn secondary" name="rejectBtn" value="${uiLabelMap.DeleteBtn}"  onClick="updateReview('PRR_DELETED');"/>
            <input type="button" class="standardBtn secondary" name="rejectBtn" value="${uiLabelMap.PendingBtn}"  onClick="updateReview('PRR_PENDING');"/>
        </div>
        <div class="PRR_DELETED">
            <input type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.ApproveBtn}" onClick="updateReview('PRR_APPROVED');"/>
            <input type="button" class="standardBtn secondary" name="rejectBtn" value="${uiLabelMap.PendingBtn}"  onClick="updateReview('PRR_PENDING');"/>
        </div>
    </div>

   </div>
</div>

<div class="infoRow">
       <input type="hidden" name="productReviewId" value="${review.productReviewId}">
       <input type="hidden" name="userLoginId" value="${userLogin.userLoginId}">
       <input type="hidden" name="productId" value="${review.productId}">
       <input type="hidden" name="productRating" value="${parameters.productRating!review.productRating}">
       <input type="hidden" name="statusId" value="${parameters.statusId!review.statusId}">
       <input type="hidden" name="productReviewHidden" value="${review.productReview!""}">
   </div>
</#if>
<!-- end productReviewDetail.ftl -->


