<table class="osafe">
    <tr class="heading">
	  <th class="seqCol">${uiLabelMap.DateLabel}</th>
      <th class="seqCol">${uiLabelMap.TimeLabel}</th>
      <th class="itemCol">${uiLabelMap.PaymentTypeLabel}</th>
      <th class="dollarCol">${uiLabelMap.AmountLabel}</th>
      <th class="statusCol">${uiLabelMap.StatusLabel}</th>
      <th class="nameCol">${uiLabelMap.CreatedByLabel}</th>
    </tr>
    <#if summaryPaymentInfo?exists && summaryPaymentInfo?has_content>
        <#assign rowClass = "1"/>
        <#list summaryPaymentInfo as paymentInfo>
          <#assign hasNext = paymentInfo_has_next/>
          <#assign paymentMethod = paymentInfo.getRelatedOne("PaymentMethodType")?if_exists />
          <#assign creditCard = paymentInfo.getRelatedOne("CreditCard")?if_exists />
          <#assign status = paymentInfo.getRelatedOne("StatusItem")?if_exists />
          
          <#assign orderHeader = paymentInfo.getRelatedOne("OrderHeader")?if_exists>
          <#if orderHeader?has_content>
              <#assign orderReadHelper = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)/>
          </#if>
          <#assign currencyUomId = orderReadHelper.getCurrency()/>
          <tbody>
            <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
              <td class="seqCol <#if !hasNext>lastRow</#if>">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(paymentInfo.createdDate, preferredDateFormat).toLowerCase())!"N/A"}</td>
              <td class="seqCol <#if !hasNext>lastRow</#if>">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(paymentInfo.createdDate, preferredTimeFormat).toLowerCase())!"N/A"}</td>
              <td class="itemCol <#if !hasNext>lastRow</#if>">
                  ${paymentMethod.description} 
                  <#if paymentMethod.paymentMethodTypeId == 'CREDIT_CARD' && creditCard?has_content>(${creditCard.cardType})</#if>
              </td>
              <td class="dollarCol <#if !hasNext>lastRow</#if>"><@ofbizCurrency amount=paymentInfo.maxAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
              <td class="statusCol <#if !hasNext>lastRow</#if>">${status.description}</td>
              <td class="nameCol <#if !hasNext>lastRow</#if>">${paymentInfo.createdByUserLogin}</td>
            </tr>
          </tbody>
        </#list>  
    <#else>
          <tbody>
              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
          </tbody>
    </#if>
</table>   
           