 <div class = "writeReviewReviewerLocation">
 <h3>${uiLabelMap.TellOthersHeading}</h3>
    <div class="entry">
      <label for="reviewLocation">${uiLabelMap.LocationCaption}</label>
      <input type="text" size="32" maxlength="100" name="REVIEW_LOCATION" value="${requestParameters.REVIEW_LOCATION?if_exists}"> 
      <span class="instructions">${uiLabelMap.LocationExampleInfo}</span>
    </div>
</div>