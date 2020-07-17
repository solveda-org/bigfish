<#if orderReadHelper?has_content>
  <#assign currencyUomId = orderReadHelper.getCurrency()>
  <#assign orderPayments = orderReadHelper.getPaymentPreferences()/>
  <#if orderPayments?has_content>
    <#list orderPayments as orderPaymentPreference>
      <#if ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "CREDIT_CARD") && (orderPaymentPreference.getString("paymentMethodId")?has_content))>
        <#assign creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("CreditCard")>
        <#if creditCard?has_content>
          <#assign cardNumber = creditCard.get("cardNumber")>
          <#assign cardNumber = cardNumber?substring(cardNumber?length - 4)>
          <#assign cardNumber = "*" + cardNumber>
        </#if>
      </#if>
    </#list>
  </#if>
  <#assign orderSubTotal = orderReadHelper.getOrderItemsSubTotal()>
  <#assign orderGrandTotal = orderReadHelper.getOrderGrandTotal()>
  
  <#assign orderAdjustments = orderReadHelper.getAdjustments()>
  <#assign orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments()>
  <#assign headerAdjustmentsToShow = orderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false)/>
  
  <#assign shippingAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getAllOrderItemsAdjustmentsTotal(resultList, orderAdjustments, false, false, true)>
  <#assign shippingAmount = shippingAmount.add(Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true))>
  <#assign taxAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal>
</#if>
<#if creditCard?has_content>
<input type="hidden" name="returnAdjustmentTypeId" id="returnAdjustmentTypeId" value="RET_MAN_ADJ" />
<div class="displayBox generalInfo">
  <div class="header">
    <h2>${uiLabelMap.CreditCardRefundHeadeing!}</h2>
  </div>
  <div class="boxBody">
      <div class="header">
        <h2>${uiLabelMap.OriginalChargeHeading!}</h2>
      </div>
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.CreditCardCaption}</label>
          </div>
          <div class="infoValue">
            ${cardNumber!}
          </div>
        </div>
      </div>
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.CardTypeCaption}</label>
          </div>
          <div class="infoValue">
            ${creditCard.get("cardType")?if_exists}
          </div>
        </div>
      </div>
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.OriginalChargeCaption}</label>
          </div>
          <div class="infoValue">
            <@ofbizCurrency amount=orderGrandTotal?default(0.00) isoCode=currencyUomId/>
          </div>
        </div>
      </div>
      
      
      <div class="header">
        <h2>${uiLabelMap.AdjustmentsHeading!}</h2>
      </div>
      
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.TotalRefundsCaption}</label>
          </div>
          <div class="infoValue">
            <@ofbizCurrency amount=totalReturns?default(0.00) isoCode=currencyUomId/>
          </div>
        </div>
      </div>
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.TotalOtherAdjustmentCaption}</label>
          </div>
          <div class="infoValue">
            <@ofbizCurrency amount=orderItemAdjustmentTotal?default(0.00) isoCode=currencyUomId/>
          </div>
        </div>
      </div>
      
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.AdjustedTotalChargeCaption}</label>
          </div>
          <div class="infoValue">
            <#assign adjustedTotalCharge = orderGrandTotal - (totalReturns + orderItemAdjustmentTotal) />
            <@ofbizCurrency amount=adjustedTotalCharge?default(0.00) isoCode=currencyUomId/>
          </div>
        </div>
      </div>
      
      <#assign newCharge = 0/>
      <div class="header">
        <h2>${uiLabelMap.NewChargeHeading!}</h2>
      </div>
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.TotalForRemainingItemsCaption}</label>
          </div>
          <div class="infoValue">
            <@ofbizCurrency amount=remainingQuantityPriceTotal?default(0.00) isoCode=currencyUomId/>
            <#assign newCharge = newCharge + remainingQuantityPriceTotal/>
          </div>
        </div>
      </div>
      
      <#assign promoAmount = 0/>
      <#list headerAdjustmentsToShow as orderHeaderAdjustment>
          <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
          <#assign productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo")!"">
          <#if productPromo?has_content>
              <#assign promoText = productPromo.promoText?if_exists/>
              <#assign proomoCodeText = "" />
              <#assign productPromoCode = productPromo.getRelated("ProductPromoCode")>
              <#assign promoCodesEntered = orderReadHelper.getProductPromoCodesEntered()!""/>
			  <#if promoCodesEntered?has_content>
			    <#list promoCodesEntered as promoCodeEntered>
      			  <#if productPromoCode?has_content>
				    <#list productPromoCode as promoCode>
					  <#assign promoCodeEnteredId = promoCodeEntered/>
					  <#assign promoCodeId = promoCode.productPromoCodeId!""/>
					  <#if promoCodeEnteredId?has_content>
					    <#if promoCodeId == promoCodeEnteredId>
						  <#assign promoCodeText = promoCode.productPromoCodeId?if_exists/>
						</#if>
				      </#if>
					</#list>
				  </#if>
			    </#list>
			  </#if>
          </#if>
                                  
                                  
          <div class="infoRow row">
            <div class="infoEntry">
              <div class="infoCaption">
                <label>${uiLabelMap.PromoCaption}<#if promoCodeText?has_content>[${promoCodeText}]:</#if></label>
              </div>
              <div class="infoValue">
                <@ofbizCurrency amount=orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/>
                <#assign promoAmount = promoAmount + orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)/>
              </div>
            </div>
          </div>
      </#list>
      
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.ShippingCaption}</label>
          </div>
          <div class="infoValue">
            <@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/>
            <#assign newCharge = newCharge + shippingAmount/>
          </div>
        </div>
      </div>
      
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.TaxCaption}</label>
          </div>
          <div class="infoValue">
            <@ofbizCurrency amount=taxAmount isoCode=currencyUomId/>
            <#assign newCharge = newCharge + taxAmount/>
          </div>
        </div>
      </div>
      
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.NewChargeCaption}</label>
          </div>
          <div class="infoValue">
            <#assign newCharge = newCharge + promoAmount />
            <@ofbizCurrency amount = newCharge isoCode=currencyUomId/>
          </div>
        </div>
      </div>
      
      <div class="header">
        <h2>${uiLabelMap.ManualReturnAdjustmentHeading!}</h2>
      </div>
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.DescriptionCaption}</label>
          </div>
          <div class="infoValue">
            <input type="text" id="description" name="description" value="${parameters.description!""}"/>
          </div>
        </div>
      </div>
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.AmountCaption}</label>
          </div>
          <div class="infoValue">
            <input type="text" id="amount" name="amount" class="small" value="${parameters.amount!""}"/>
          </div>
        </div>
      </div>
      
      <div class="header">
        <h2>${uiLabelMap.RefundDetailsHeading!}</h2>
      </div>
      <div class="infoRow row">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.TotalRefundCaption}</label>
          </div>
          <div class="infoValue">
            <#if (newCharge >= 0 )>
              <#assign totalRefundAmount = adjustedTotalCharge - newCharge />
            <#else>
              <#assign totalRefundAmount = adjustedTotalCharge + newCharge />
            </#if>
            <input type="hidden" name="totalRefundAmount" value="${parameters.totalRefundAmount!totalRefundAmount!}" />
            <@ofbizCurrency amount = totalRefundAmount isoCode=currencyUomId/>
          </div>
        </div>
      </div>
  </div>
    
</div>
</#if>