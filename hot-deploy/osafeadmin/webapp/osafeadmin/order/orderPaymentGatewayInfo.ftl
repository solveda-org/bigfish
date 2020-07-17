<#assign gatewayResponseInfo = request.getAttribute("gatewayResponseInfo")!/>
<#if gatewayResponseInfo?has_content>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.AmountCaption}</label>
      </div>
      <div class="infoValue">
          <p><@ofbizCurrency amount= gatewayResponseInfo.amount rounding=globalContext.currencyRounding isoCode=gatewayResponseInfo.currencyUomId/></p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentGatewayResponseIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.paymentGatewayResponseId!""}</p>
      </div>
    </div>
  </div>
  <#if gatewayResponseInfo.paymentServiceTypeEnumId?exists>
     <#assign serviceTypeEnumeration = delegator.findOne("Enumeration", {"enumId" : gatewayResponseInfo.paymentServiceTypeEnumId}, false)>
  </#if>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentServiceTypeEnumIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.paymentServiceTypeEnumId!""}<#if serviceTypeEnumeration?exists>(${serviceTypeEnumeration.description})</#if></p>
      </div>
    </div>
  </div>
  <#if gatewayResponseInfo.transCodeEnumId?exists>
     <#assign transCodeEnumeration = delegator.findOne("Enumeration", {"enumId" : gatewayResponseInfo.transCodeEnumId}, false)>
  </#if>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.TransCodeEnumIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.transCodeEnumId!""}<#if transCodeEnumeration?exists>(${transCodeEnumeration.description})</#if></p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.CurrencyUomIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.currencyUomId!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ReferenceNumCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.referenceNum!""}</p>
      </div>
    </div>
  </div>
    <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.AltReferenceCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.altReference!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.SubReferenceCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.subReference!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.GatewayCodeCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.gatewayCode!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.GatewayFlagCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.gatewayFlag!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.GatewayAvsResultCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.gatewayAvsResult!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.GatewayCvResultCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.gatewayCvResult!""}</p>
      </div>
    </div>
  </div>
    <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.GatewayScoreResultCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.gatewayScoreResult!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.GatewayMessageCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.gatewayMessage!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.TransactionDateCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.transactionDate!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ResultDeclinedCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.resultDeclined!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ResultNsfCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.resultNsf!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ResultBadExpireCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.resultBadExpire!""}</p>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ResultBadCardNumberCaption}</label>
      </div>
      <div class="infoValue">
          <p>${gatewayResponseInfo.resultBadCardNumber!""}</p>
      </div>
    </div>
  </div>
  <#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>