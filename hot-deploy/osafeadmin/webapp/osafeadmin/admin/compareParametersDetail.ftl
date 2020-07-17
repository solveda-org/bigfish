  <div class="infoRow">
    <div class="infoDetail">
      <p>${uiLabelMap.CompareParametersInfo}</p>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CompareYourDatabaseCaption}</label>
      </div>
      <div class="infoValue">
        ${entityName!""}
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CompareToParametersFileCaption}</label>
      </div>
      <div class="infoValue">
        <input type="file" size="40" name="fname" accept="application/xml" onchange="return isXml(this)"/>
      </div>
    </div>
  </div>