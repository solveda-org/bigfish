  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <input type="hidden" name="sampleFile" value="Y" />
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.DownloadBigFishSampleXlsInfo}</p>
          </div>
      </div>
  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>