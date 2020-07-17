  <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.XLSFileCaption}</label>
      </div>
      <div class="infoValue">
        <input type="file" size="40" name="uploadedFile" accept="*.xls"/>
      </div>
    </div>
  </div>
