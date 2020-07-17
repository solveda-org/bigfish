<#if paymentPrefInfo?has_content>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.OrderPaymentPreferenceIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.orderPaymentPreferenceId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentMethodTypeIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.paymentMethodTypeId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MaxAmountCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.maxAmount!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ProcessAttemptCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.processAttempt!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.paymentStatusIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.statusId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CreatedByUserLoginCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.createdByUserLogin!""}</p>
      </div>
    </div>
  </div>
<#else>
    <div class="infoRow">
       <div class="infoEntry">
         <div class="infoValue">${uiLabelMap.NoDataAvailableInfo}</div>
       </div>
    </div>
</#if>