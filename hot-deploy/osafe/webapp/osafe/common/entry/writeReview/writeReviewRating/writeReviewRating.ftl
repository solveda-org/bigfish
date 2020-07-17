
<!-- DIV for Displaying Rating Section STARTS here --><div class="writeReviewRating">
  <div id="productRatingEntry" class="displayBox">
    <h3>${uiLabelMap.ProductRatingsHeading}</h3>
    <p class="instructions">${StringUtil.wrapString(uiLabelMap.RequiredInstructionsInfo)}</p>
    <fieldset class="col">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#writeReviewRatingDivSequence")}
    </fieldset> </div>
</div>
<!-- DIV for Displaying Rating Section ENDS here -->