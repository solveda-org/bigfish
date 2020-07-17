  <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
  <#if feedsOutDirParm?exists && feedsOutDirParm == 'customer'>
    <#assign feedsOutDir = FEEDS_OUT_CUSTOMER_URL_DIR!"" />
    <#assign feedsOutPrefix = FEEDS_OUT_CUSTOMER_PREFIX!"" />
  </#if>
  <#if feedsOutDirParm?exists && feedsOutDirParm == 'order'>
    <#assign feedsOutDir = FEEDS_OUT_ORDER_URL_DIR!"" />
    <#assign feedsOutPrefix = FEEDS_OUT_ORDER_PREFIX!"" />
  </#if>
  <#if feedsOutDirParm?exists && feedsOutDirParm == 'contactUs'>
    <#assign feedsOutDir = FEEDS_OUT_CONTACT_US_URL_DIR!"" />
    <#assign feedsOutPrefix = FEEDS_OUT_CONTACT_US_PREFIX!"" />
  </#if>
  <#if feedsOutDirParm?exists && feedsOutDirParm == 'requestCatalog'>
    <#assign feedsOutDir = FEEDS_OUT_REQUEST_CATALOG_URL_DIR!"" />
    <#assign feedsOutPrefix = FEEDS_OUT_REQUEST_CATALOG_PREFIX!"" />
  </#if>
  <#if feedsOutDirParm?exists && feedsOutDirParm == 'googleProductFeed'>
    <#assign feedsOutDir = FEEDS_OUT_GOOGLE_PRODUCT_URL_DIR!"" />
    <#assign feedsOutPrefix = FEEDS_OUT_GOOGLE_PRODUCT_PREFIX!"" />
  </#if>
  <#if feedsOutDir?has_content>
    <#assign currentDateString = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("yyyyMMdd") />
    <#assign currentTimeString = Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("HHmmss") />
    <#assign exportFileServerPath = feedsOutDir + "/"+ feedsOutPrefix + "_"+currentDateString+"_"+currentTimeString+".xml" />
  </#if>
  <input type="hidden" name="exportId" value="${parameters.exportId!exportId!""}" />
  <input type="hidden" name="exportIdList" value="${parameters.exportIdList!exportIdList!""}" />
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ExportXmlFileOnServerCaption}</label>
      </div>
      <div class="infoValue">
        <input type="text" name="exportFileServerPath" id="exportFileServerPath" class="medium" value="${parameters.exportFileServerPath!exportFileServerPath!""}" />
      </div>
      <div class="infoIcon">
        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.ExportXmlFileOnServerInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
  <#if !parameters.exportIdList?has_content && !exportIdList?has_content>
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
  </#if>
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
