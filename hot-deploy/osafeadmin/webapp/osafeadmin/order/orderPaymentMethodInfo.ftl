<#if creditCardInfo?has_content>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentMethodIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.paymentMethodId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CardTypeCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.cardType!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CardNumberCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.cardNumber!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ValidFromDateCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.validFromDate!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ExpireDateCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.expireDate!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.IssueNumberCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.issueNumber!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CompanyNameOnCardCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.companyNameOnCard!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.TitleOnCardCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.titleOnCard!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.FirstNameOnCardCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.firstNameOnCard!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MiddleNameOnCardCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.middleNameOnCard!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.LastNameOnCardCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.lastNameOnCard!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.SuffixOnCardCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.suffixOnCard!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ConsecutiveFailedAuthsCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.consecutiveFailedAuths!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.LastFailedAuthDateCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.lastFailedAuthDate!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ConsecutiveFailedNsfCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.consecutiveFailedNsf!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.LastFailedNsfDateCaption}</label>
      </div>
      <div class="infoValue">
          <p>${creditCardInfo.lastFailedNsfDate!""}</p>
      </div>
    </div>
  </div>
<#elseif payPalInfo?has_content>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentMethodIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${payPalInfo.paymentMethodId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PayerIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${payPalInfo.payerId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ExpressCheckoutTokenCaption}</label>
      </div>
      <div class="infoValue">
          <p>${payPalInfo.expressCheckoutToken!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PayerStatusCaption}</label>
      </div>
      <div class="infoValue">
          <p>${payPalInfo.payerStatus!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.AvsAddrCaption}</label>
      </div>
      <div class="infoValue">
          <p>${payPalInfo.avsAddr!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.AvsZipCaption}</label>
      </div>
      <div class="infoValue">
          <p>${payPalInfo.avsZip!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CorrelationIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${payPalInfo.correlationId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.TransactionIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${payPalInfo.transactionId!""}</p>
      </div>
    </div>
  </div>
<#elseif ebsInfo?has_content>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentMethodIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${ebsInfo.paymentMethodId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.TransactionIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${ebsInfo.transactionId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${ebsInfo.paymentId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ReferenceNumberCaption}</label>
      </div>
      <div class="infoValue">
          <p>${ebsInfo.merchantRefNo!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ResponseCodeCaption}</label>
      </div>
      <div class="infoValue">
          <p>${ebsInfo.responseCode!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ResponseMessageCaption}</label>
      </div>
      <div class="infoValue">
          <p>${ebsInfo.responseMessage!""}</p>
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