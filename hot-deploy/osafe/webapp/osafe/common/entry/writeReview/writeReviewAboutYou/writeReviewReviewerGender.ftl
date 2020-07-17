<div class = "writeReviewReviewerGender">
    <div class="entry">
      <label for="reviewGender">${uiLabelMap.GenderCaption}</label>
      <select name="REVIEW_GENDER" id="REVIEW_GENDER">
         <option value=""> ${uiLabelMap.SelectOneLabel}</option>
         <option value="M" <#if ((parameters.REVIEW_GENDER?exists && parameters.REVIEW_GENDER?string == "M"))>selected</#if>>${uiLabelMap.GenderMale}</option>
         <option value="F" <#if ((parameters.REVIEW_GENDER?exists && parameters.REVIEW_GENDER?string == "F"))>selected</#if>>${uiLabelMap.GenderFemale}</option>
      </select>
    </div>
</div>