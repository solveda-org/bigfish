  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <input type="hidden" name="sampleFile" value="N" />
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.CreateBigFishLoadFromDBInfo}</p>
          </div>
      </div>
  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>