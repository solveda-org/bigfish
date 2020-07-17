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
  <#if paymentPrefInfo.paymentMethodTypeId?exists>
     <#assign PaymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : paymentPrefInfo.paymentMethodTypeId}, false)>
  </#if>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentMethodTypeIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.paymentMethodTypeId!""} <#if PaymentMethodType?exists>(${PaymentMethodType.description})</#if></p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MaxAmountCaption}</label>
      </div>
      <div class="infoValue">
          <p><@ofbizCurrency amount= paymentPrefInfo.maxAmount rounding=globalContext.currencyRounding isoCode=globalContext.currencyUomId/></p>
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
  <#if paymentPrefInfo.statusId?exists>
     <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : paymentPrefInfo.statusId}, false)>
  </#if>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.paymentStatusIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.statusId!""} <#if statusItem?exists>(${statusItem.description})</#if></p>
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
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PresentFlagCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.presentFlag!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.SwipedFlagCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.swipedFlag!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.OverflowFlagCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.overflowFlag!""}</p>
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
        <label>${uiLabelMap.BillingPostalCodeCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.billingPostalCode!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ManualAuthCodeCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.manualAuthCode!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ManualRefNumCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.manualRefNum!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.NeedsNSFRetryCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentPrefInfo.needsNsfRetry!""}</p>
      </div>
    </div>
  </div>
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>