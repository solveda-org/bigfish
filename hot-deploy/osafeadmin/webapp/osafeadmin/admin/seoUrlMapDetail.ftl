  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.SiteMapInfo}</p>
          </div>
      </div>
  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>