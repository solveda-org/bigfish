<#if sysConfigFileTextData?exists && sysConfigFileTextData?has_content>
  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoValue withOutCaption">
        <textarea class="largeArea" name="textData" cols="50" rows="5" >${sysConfigFileTextData!""}</textarea>
      </div>
    </div>
  </div>
<#else>
  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoValue withOutCaption">
        ${uiLabelMap.NoDataAvailableInfo}
      </div>
    </div>
  </div>
</#if>