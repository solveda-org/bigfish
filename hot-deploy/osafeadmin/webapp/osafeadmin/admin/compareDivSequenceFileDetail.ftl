  <div class="infoRow">
    <div class="infoDetail">
      <p>${uiLabelMap.CompareDivSequenceFileInfo}</p>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CompareYourDivSequenceFileCaption}</label>
      </div>
      <div class="infoValue">
        <#assign compareYourDivSequenceFile = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("osafeAdmin.properties", "osafe-deployment-uiSequence-xml-file")>
        <#if compareYourDivSequenceFile?has_content>
          <#assign compareYourDivSequenceFile = compareYourDivSequenceFile.substring(compareYourDivSequenceFile.indexOf("/")+1)>
        </#if>
        ${compareYourDivSequenceFile!""}
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CompareToDivSequenceFileCaption}</label>
      </div>
      <div class="infoValue">
        <input type="file" size="40" name="fname" accept="application/xml" onchange="return isXml(this)"/>
      </div>
    </div>
  </div>