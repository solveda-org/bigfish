  <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
  <input type="hidden" name="feedsOutCustomerDir" value="${FEEDS_OUT_CUSTOMER_URL_DIR!""}" />
  <input type="hidden" name="feedsOutCustomerPrefix" value="${FEEDS_OUT_CUSTOMER_PREFIX!""}" />
  <input type="hidden" name="feedsOutOrderDir" value="${FEEDS_OUT_ORDER_URL_DIR!""}" />
  <input type="hidden" name="feedsOutOrderPrefix" value="${FEEDS_OUT_ORDER_PREFIX!""}" />
  <input type="hidden" name="feedsOutContactUsDir" value="${FEEDS_OUT_CONTACT_US_URL_DIR!""}" />
  <input type="hidden" name="feedsOutContactUsPrefix" value="${FEEDS_OUT_CONTACT_US_PREFIX!""}" />
  <input type="hidden" name="feedsOutRequestCatalogDir" value="${FEEDS_OUT_REQUEST_CATALOG_URL_DIR!""}" />
  <input type="hidden" name="feedsOutRequestCatalogPrefix" value="${FEEDS_OUT_REQUEST_CATALOG_PREFIX!""}" />
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ExportXmlFileOnServerCaption}</label>
      </div>
      <div class="infoValue">
        <input type="text" name="exportFileServerPath" id="exportFileServerPath" class="large" value="${parameters.exportFileServerPath!""}" />
      </div>
      <div class="infoIcon">
        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.ExportXmlFileOnServerInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.IncludeItemNotExportedLabel}</label>
      </div>
      <div class="infoValue radiobutton">
        <input type="radio" name="includeOnlyNotExportedItem" value="Y" <#if parameters.includeOnlyNotExportedItem?exists && parameters.includeOnlyNotExportedItem == 'Y'>checked="checked"</#if>/>${uiLabelMap.YesLabel}
        <input type="radio" name="includeOnlyNotExportedItem" value="N" <#if !parameters.includeOnlyNotExportedItem?exists || parameters.includeOnlyNotExportedItem == 'N'>checked="checked"</#if>/>${uiLabelMap.NoLabel}
      </div>
      <div class="infoIcon">
        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.IncludeItemNotExportedInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.AfterExportSetFlagLabel}</label>
      </div>
      <div class="infoValue radiobutton">
        <input type="radio" name="setFlagAfterExport" value="Y" <#if parameters.setFlagAfterExport?exists && parameters.setFlagAfterExport == 'Y'>checked="checked"</#if>/>${uiLabelMap.YesLabel}
        <input type="radio" name="setFlagAfterExport" value="N" <#if !parameters.setFlagAfterExport?exists || parameters.setFlagAfterExport == 'N'>checked="checked"</#if>/>${uiLabelMap.NoLabel}
      </div>
      <div class="infoIcon">
        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.AfterExportSetFlagInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.AfterExportAllowSaveLabel}</label>
      </div>
      <div class="infoValue radiobutton">
        <input type="radio" name="allowSaveAfterExport" value="Y" <#if !parameters.allowSaveAfterExport?exists || parameters.allowSaveAfterExport == 'Y'>checked="checked"</#if>/>${uiLabelMap.YesLabel}
        <input type="radio" name="allowSaveAfterExport" value="N" <#if parameters.allowSaveAfterExport?exists && parameters.allowSaveAfterExport == 'N'>checked="checked"</#if>/>${uiLabelMap.NoLabel}
      </div>
      <div class="infoIcon">
        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.AfterExportAllowSaveInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
