  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <input type="hidden" name="cacheName" value="properties.UtilPropertiesBundleCache|osafe.ManageXmlUrlCache" />
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.ClrLblCacheInfo}</p>
          </div>
      </div>
  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>