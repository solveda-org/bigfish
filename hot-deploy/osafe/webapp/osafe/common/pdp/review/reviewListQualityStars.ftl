<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.QualityCaption}</label>
    <div class="rating_bar"><div style="width:${qualityRatePercentage!}%"></div></div>
    <span class="ratingSummary">${qualityRate!} ${uiLabelMap.ReviewRatingOutOfFiveLabel}</span>
  </div>
</li>
