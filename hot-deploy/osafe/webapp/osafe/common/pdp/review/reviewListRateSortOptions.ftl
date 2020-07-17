<div class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.SortByCaption}</label>
    <select onchange="sortReviews('${reviewScreenPrefix!}');return false;" name="reviewSort" class="sortDropdown" id="js_${reviewScreenPrefix!}reviewSort">
      <option class="sortDropdown" id="default-desc" value="-productRating" <#if sortReviewBy == "-productRating"> SELECTED</#if>>
        ${uiLabelMap.SortReviewByRatingHighLabel}
      </option>
      <option class="sortDropdown" id="featured-desc" value="productRating" <#if sortReviewBy == "productRating"> SELECTED</#if>>
        ${uiLabelMap.SortReviewByRatingLowLabel}
      </option>
      <option class="sortDropdown" id="rank-desc" value="-postedDateTime" <#if sortReviewBy == "-postedDateTime"> SELECTED</#if>>
        ${uiLabelMap.SortReviewByDateHighLabel}
      </option>
      <option class="sortDropdown" id="affiliation-desc" value="postedDateTime" <#if sortReviewBy == "postedDateTime"> SELECTED</#if>>
        ${uiLabelMap.SortReviewByDateLowLabel}
      </option>
    </select>
  </div>
</div>