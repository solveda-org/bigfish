<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.SatisfactionCaption}</label>
    <div class="rating_bar"><div style="width:${satisfactionRatePercentage!}%"></div></div>
    <span>${satisfactionRate!} ${uiLabelMap.ReviewRatingOutOfFiveLabel}</span>
  </div>
</li>
 
