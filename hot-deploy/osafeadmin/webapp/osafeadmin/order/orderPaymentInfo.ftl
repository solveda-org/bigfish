<#if paymentInfo?has_content>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.AmountCaption}</label>
      </div>
      <div class="infoValue">
          <p><@ofbizCurrency amount= paymentInfo.amount rounding=globalContext.currencyRounding isoCode=paymentInfo.currencyUomId/></p>
      </div>
    </div>
  </div>
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
  <#if paymentInfo.paymentTypeId?exists>
     <#assign PaymentType = delegator.findOne("PaymentType", {"paymentTypeId" : paymentInfo.paymentTypeId}, false)>
  </#if>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentTypeIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.paymentTypeId!""} <#if PaymentType?exists>(${PaymentType.description})</#if></p>
      </div>
    </div>
  </div>
  <#if paymentInfo.paymentMethodTypeId?exists>
     <#assign PaymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : paymentInfo.paymentMethodTypeId}, false)>
  </#if>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.PaymentMethodTypeIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.paymentMethodTypeId!""} <#if PaymentMethodType?exists>(${PaymentMethodType.description})</#if></p>
      </div>
    </div>
  </div>
  <#if paymentInfo.statusId?exists>
     <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : paymentInfo.statusId}, false)>
  </#if> 
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.paymentStatusIdCaption}</label>
      </div>
      <div class="infoValue">
          <p>${paymentInfo.statusId!""} <#if statusItem?exists>(${statusItem.description})</#if></p>
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
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>