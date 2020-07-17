<div class="giftCardSummary" >
<#assign showGiftCardAdjustedWarning = sessionAttributes.showGiftCardAdjustedWarning!/>
<#if showGiftCardAdjustedWarning?has_content && showGiftCardAdjustedWarning == "Y">
  <div id="gcWarningMessText" class="warningMessText">
    <span>${uiLabelMap.GiftCardExceedCartBalanceWarning}</span>
  </div>
</#if>
</div>
      
<table class="osafe">
  <thead>
    <tr class="heading">
      <th class="idCol firstCol">${uiLabelMap.GiftCardNumberLabel}</th>
      <th class="descCol">${uiLabelMap.AmountRedeemedLabel}</th>
      <th class="statusCol">${uiLabelMap.StatusLabel}</th>
      <th class="actionCol"></th>
    </tr>
  </thead>
  <#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
  <#if (shoppingCart.size() > 0) && shoppingCart.getGiftCards()?has_content>
      <tbody>
      <#list shoppingCart.getGiftCards() as giftCardPayment>
        <tr>
          <td class="idCol firstCol">${giftCardPayment.cardNumber!}</td>
          <td class="descCol">
              <#assign giftCardAmount = shoppingCart.getPaymentAmount(giftCardPayment.paymentMethodId)?if_exists />
              <@ofbizCurrency amount=giftCardAmount!0 isoCode=currencyUom rounding=globalContext.currencyRounding/>
          </td>
          <td class="statusCol">
              ${uiLabelMap.GiftCardAppliedInfo}
          </td>
          <td class="actionCol">
            <a href="javascript:removeGiftCardNumber('${giftCardPayment.paymentMethodId!}');"><span class="crossIcon"></span></a>
          </td>
        </tr>
      </#list>
    </tbody>
</#if>
</table>

