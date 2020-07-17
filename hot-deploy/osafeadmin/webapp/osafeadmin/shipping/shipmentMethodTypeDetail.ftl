<#if mode?has_content>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ShippingMethodCaption}</label>
      </div>
      <div class="infoValue">
          <input name="shipmentMethodTypeId" type="text" id="shipmentMethodTypeId" maxlength="20" value="${parameters.shipmentMethodTypeId!shipmentMethodTypeId!""}"/>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.DescriptionCaption}</label>
      </div>
      <div class="infoValue">
          <input name="description" type="text" id="description" maxlength="60" value="${parameters.description!description!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.SequenceCaption}</label>
      </div>
      <div class="infoValue">
          <input name="sequenceNum" type="text" id="sequenceNum" maxlength="20" value="${parameters.sequenceNum!sequenceNum!""}"/>
      </div>
    </div>
  </div>

<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>
