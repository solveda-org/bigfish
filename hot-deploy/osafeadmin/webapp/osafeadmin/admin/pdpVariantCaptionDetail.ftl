<#if pdpVariantCaptionDetail?has_content>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption"><label>${uiLabelMap.CaptionIDCaption}</label></div>
      <div class="infoValue">
        ${pdpVariantCaptionDetail.productFeatureTypeId?default("")}
        <input name="productFeatureTypeId" type="hidden" value="${pdpVariantCaptionDetail.productFeatureTypeId?default("")}" />
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption"><label>${uiLabelMap.CaptionCaption}</label></div>
      <div class="infoValue">
        <input name="description" type="text" id="description" value="${parameters.description?default(pdpVariantCaptionDetail.description!"")}" />
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoCaption"><label>${uiLabelMap.CreatedDateCaption}</label></div>
      <div class="infoValue">
        ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(pdpVariantCaptionDetail.createdStamp, preferredDateTimeFormat).toLowerCase())!"N/A"}
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoCaption"><label>${uiLabelMap.UpdatedDateCaption}</label></div>
      <div class="infoValue">
        ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(pdpVariantCaptionDetail.lastUpdatedStamp, preferredDateTimeFormat).toLowerCase())!"N/A"}
      </div>
    </div>
  </div>

<#else>
  ${uiLabelMap.NoDataAvailableInfo}
</#if>
