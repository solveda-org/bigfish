<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#if (shoppingCart.size() > 0) && shoppingCart.getGiftCards()?has_content>
  <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>  
  <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
  <#assign showGiftCardAdjustedWarning = sessionAttributes.showGiftCardAdjustedWarning!/>
  <div class="boxList giftCardList">
    <#list shoppingCart.getGiftCards() as giftCardPayment>
     <div class="boxListItemTabular actionResultItem giftCardItem">
       <ul class="displayList">
	    <li class="string cardNumber">
	     <div>
           <label>${uiLabelMap.GiftCardNumberCaption}</label>
		   <span>${giftCardPayment.cardNumber!}</span>
		 </div>
	    </li>
        <#assign giftCardAmount = shoppingCart.getPaymentAmount(giftCardPayment.paymentMethodId)?if_exists />
        <#if giftCardAmount?has_content>
		    <li class="currency giftCard">
		     <div>
	           <label>${uiLabelMap.AmountCaption}</label>
			   <span><@ofbizCurrency amount=giftCardAmount isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
			 </div>
		    </li>
        </#if>
        <#if giftCardPayment.expireDate?has_content>
		    <li class="date expiration">
		     <div>
	           <label>${uiLabelMap.ExpirationDateCaption}</label>
			   <span>${giftCardPayment.expireDate!}</span>
			 </div>
		    </li>
        </#if>
	    <li class="action remove">
	     <div>
            <a href="javascript:removeGiftCardNumber('${giftCardPayment.paymentMethodId!}');"><span>${uiLabelMap.RemoveGiftCardLabel}</span></a>
		 </div>
	    </li>
       </ul>
	 </div>
    </#list>
  </div>
  <div class="displayBox">
    <ul class="displayList summary giftCardSummary">
	      <li class="currency total">
	       <div>
		        <label>${uiLabelMap.OrderTotalCaption}</label>
		        <span><@ofbizCurrency amount=shoppingCart.getGrandTotal()! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
	       </div>
	      </li>
	      <li class="currency total">
	       <div>
		        <label>${uiLabelMap.GiftCardTotalCaption}</label>
		        <span><@ofbizCurrency amount=shoppingCart.getPaymentTotal()! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
	       </div>
	      </li>
       <#assign remainingPayment = shoppingCart.getGrandTotal().subtract(shoppingCart.getPaymentTotal())! />
       <#if (remainingPayment &gt; 0)>
		      <li class="currency balance">
		       <div>
			        <label>${uiLabelMap.RemainingPaymentCaption}</label>
			        <span><@ofbizCurrency amount=remainingPayment isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
		       </div>
		      </li>
       <#else>
           <#assign gcNumber = shoppingCart.getOrderAttribute("GIFT_CARD_NUMBER")?if_exists />
           <#assign gcRemainingBal = shoppingCart.getOrderAttribute("GIFT_CARD_REMAINING_BAL")?if_exists />
		      <li class="currency balance">
		       <div>
			        <label>${uiLabelMap.RemainingGiftCardBalanceCaption}</label>
                   <#if gcRemainingBal?has_content>
                       <#assign gcRemainingBal = gcRemainingBal.replaceAll(",", "") >
                       <#assign gcRemainingBal = Static["java.lang.Double"].parseDouble(gcRemainingBal) >
 			           <span><@ofbizCurrency amount=gcRemainingBal isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
                   </#if>
                   <#if (shoppingCart.getGiftCards().size() &gt; 1)>
                        <span>(${uiLabelMap.GiftCardLabel}${gcNumber!})</span>
                   </#if>
		       </div>
		      </li>
       </#if>
       <#if showGiftCardAdjustedWarning?has_content && showGiftCardAdjustedWarning == "Y">
        <li id="js_gcWarningMessTest" class="string">
           <div>
	         <span class="warningMessText">${uiLabelMap.GiftCardExceedCartBalanceWarning}</span>
           </div>
        </li>
      </#if>
    </ul>
  </div>
</#if>
