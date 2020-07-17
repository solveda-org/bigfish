<#if paymentInfo?has_content>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.paymentId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentTypeIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.paymentTypeId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentMethodTypeIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.paymentMethodTypeId!""}</p>
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.paymentStatusIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.statusId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentRefNumCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.paymentRefNum!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.AmountCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.amount!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CurrencyUomIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.currencyUomId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CommentsCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.comments!""}</p>
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