  <div class="infoRow">
    <div class="infoDetail">
      <p>${uiLabelMap.CompareLabelFileInfo}</p>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CompareYourLabelFileCaption}</label>
      </div>
      <div class="infoValue">
        <#assign compareYourLabelFile = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("osafeAdmin.properties", "ecommerce-deployment-UiLabel-xml-file")>
        <#if compareYourLabelFile?has_content>
          <#assign compareYourLabelFile = compareYourLabelFile.substring(compareYourLabelFile.indexOf("/")+1)>
        </#if>
        ${compareYourLabelFile!""}
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CompareToLabelFileCaption}</label>
      </div>
      <div class="infoValue">
        <input type="file" size="40" name="fname" accept="application/xml" onchange="return isXml(this)"/>
      </div>
    </div>
  </div>