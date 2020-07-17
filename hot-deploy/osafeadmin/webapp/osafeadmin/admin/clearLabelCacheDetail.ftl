  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <input type="hidden" name="cacheName" value="properties.UtilPropertiesBundleCache|osafe.ManageXmlUrlCache" />
      <input type="hidden" name="UTIL_CACHE_NAME" value="entitycache.entity-list.default.XPixelTracking" />
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.ClrLblCacheInfo}</p>
          </div>
      </div>
      <div class="infoRow">
          <div class="infoDetail">
              <p>${uiLabelMap.ClrPixelCacheInfo}</p>
          </div>
      </div>
  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>