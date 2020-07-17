<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.OverallRatingCaption}</label>
    <div class="rating_bar"><div style="width:${overallRatePercentage}%"></div></div>
    <span>${overallRate} ${uiLabelMap.ReviewRatingOutOfFiveLabel}</span>
  </div>
</li>