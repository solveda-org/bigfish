<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.EffectivenessCaption}</label>
    <div class="rating_bar"><div style="width:${effectivenessRatePercentage!}%"></div></div>
    <span>${effectivenessRate!} ${uiLabelMap.ReviewRatingOutOfFiveLabel}</span>
  </div>
</li>

