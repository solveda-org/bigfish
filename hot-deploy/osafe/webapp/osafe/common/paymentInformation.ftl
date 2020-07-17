<#if paymentMethods?has_content>
<div class="${request.getAttribute("attributeClass")!}">
  <div class="displayBox paymentInformation">
   <h3>${uiLabelMap.PaymentInformationHeading}</h3>
    <#list paymentMethods as paymentMethod>
           <#assign orderPaymentPreferences = paymentMethod.getRelated("OrderPaymentPreference")>
           <#assign orderPaymentPreference = ""/>
           <#if orderPaymentPreferences?has_content>
                <#assign orderPaymentPreference = orderPaymentPreferences[0]!"">
           </#if>
              <#-- credit card info -->
          <#if paymentMethod.paymentMethodTypeId?has_content && orderPaymentPreference?has_content>
	          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
		               <#assign orderPaymentPreferences = paymentMethod.getRelated("OrderPaymentPreference")>
		               <#assign orderPaymentPreference = ""/>
		               <#if orderPaymentPreferences?has_content>
		                    <#assign orderPaymentPreference = orderPaymentPreferences[0]!"">
		               </#if>
		                <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
		                <#-- create a display version of the card where all but the last four digits are * -->
		                <#assign cardNumberDisplay = "">
		                <#assign cardNumber = creditCard.cardNumber?if_exists>
		                <#if cardNumber?has_content>
		                    <#assign size = cardNumber?length - 4>
		                    <#if (size > 0)>
		                        <#list 0 .. size-1 as foo>
		                            <#assign cardNumberDisplay = cardNumberDisplay + "X">
		                        </#list>
		                        <#assign cardNumberDisplay = cardNumberDisplay + "-" + cardNumber[size .. size + 3]>
		                    <#else>
		                        <#-- but if the card number has less than four digits (ie, it was entered incorrectly), display it in full -->
		                        <#assign cardNumberDisplay = cardNumber>
		                    </#if>
		                </#if>
	                   <#if creditCard?has_content>
				          <ul class="displayDetail creditCardInfo">
		                    <#if creditCardTypesMap[creditCard.cardType]?has_content>
				             <li>
				              <div>
	                             <h4>${uiLabelMap.CreditCardHeading}</h4>
				                <label>${uiLabelMap.CardTypeCaption}</label>
				                <span>${creditCardTypesMap[creditCard.cardType]!""}</span>
				              </div>
				             </li>
				            </#if>
		                    <#if cardNumberDisplay?has_content>
				             <li>
				              <div>
				                <label>${uiLabelMap.CardNumberCaption}</label>
				                <span>${cardNumberDisplay}</span>
				              </div>
				             </li>
		                    </#if>
		                    <#if creditCard.expireDate?has_content>
				             <li>
				              <div>
				                <label>${uiLabelMap.ExpDateCaption}</label>
				                <span>${creditCard.expireDate}</span>
				              </div>
				             </li>
		                    </#if>
			                 <li>
			                     <div>
			                        <label>${uiLabelMap.AmountCaption}</label>
			                        <span>
			                            <@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/>
			                        </span>
			                     </div>
			                 </li>
				          </ul>
				       </#if>
	          <#elseif "EFT_ACCOUNT" == paymentMethod.paymentMethodTypeId>
		               <#assign orderPaymentPreferences = paymentMethod.getRelated("OrderPaymentPreference")>
		               <#assign orderPaymentPreference = ""/>
		               <#if orderPaymentPreferences?has_content>
		                    <#assign orderPaymentPreference = orderPaymentPreferences[0]!"">
		               </#if>
		                <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
			          <ul class="displayDetail eftAccount">
			             <li>
			              <div>
	                        <h4>${uiLabelMap.EftAccountHeading}</h4>
				            <label>${uiLabelMap.BankNameCaption}</label>
				            <span>${eftAccount.bankName!""}</span>
			              </div>
			             </li>
		                <#assign acctNumDisplay = "">
		                <#assign acctNumber = eftAccount.accountNumber?if_exists>
		                <#if acctNumber?has_content>
		                    <#assign size = acctNumber?length - 4>
		                    <#if (size > 0)>
		                        <#list 0 .. size-1 as foo>
		                            <#assign acctNumDisplay = acctNumDisplay + "X">
		                        </#list>
		                        <#assign acctNumDisplay = acctNumDisplay + "-" + acctNumber[size .. size + 3]>
		                    <#else>
		                        <#-- but if the card number has less than four digits (ie, it was entered incorrectly), display it in full -->
		                        <#assign acctNumDisplay = acctNumber>
		                    </#if>
		                </#if>
		                <#if acctNumDisplay?has_content>
			             <li>
			              <div>
			                <label>${uiLabelMap.AccountNumberCaption}</label>
			                <span>${acctNumDisplay}</span>
			              </div>
			             </li>
			            </#if>
		                <#assign acctNumDisplay = "">
		                <#assign acctNumber = eftAccount.routingNumber?if_exists>
		                <#if acctNumber?has_content>
		                    <#assign size = acctNumber?length - 4>
		                    <#if (size > 0)>
		                        <#list 0 .. size-1 as foo>
		                            <#assign acctNumDisplay = acctNumDisplay + "X">
		                        </#list>
		                        <#assign acctNumDisplay = acctNumDisplay + "-" + acctNumber[size .. size + 3]>
		                    <#else>
		                        <#-- but if the card number has less than four digits (ie, it was entered incorrectly), display it in full -->
		                        <#assign acctNumDisplay = acctNumber>
		                    </#if>
		                </#if>
		                <#if acctNumDisplay?has_content>
			             <li>
			              <div>
			                <label>${uiLabelMap.RoutingNumberCaption}</label>
			                <span>${acctNumDisplay}</span>
			              </div>
			             </li>
			            </#if>
			             <li>
			              <div>
			                <label>${uiLabelMap.AmountCaption}</label>
			                <span><@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
			              </div>
			             </li>
			          </ul>
	          <#elseif "EXT_PAYPAL" == paymentMethod.paymentMethodTypeId>
			          <ul class="displayDetail payPalInfo">
			             <li>
			              <div>
	                        <h4>${uiLabelMap.PayPalHeading}</h4>
			                <label>${uiLabelMap.PayPalOnlyCaption}</label>
			                <span class="payPalImage"><span>${uiLabelMap.PayPalHeading}</span></span>
			              </div>
			             </li>
			             <li>
			              <div>
			                <label>${uiLabelMap.AmountCaption}</label>
			                <span><@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
			              </div>
			             </li>
			          </ul>
	          <#elseif "SAGEPAY_TOKEN" == paymentMethod.paymentMethodTypeId>
	               <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
	               <#if creditCard?has_content>
			          <ul class="displayDetail creditCardInfo">
	                    <#if creditCardTypesMap[creditCard.cardType]?has_content>
			             <li>
			              <div>
	                        <h4>${uiLabelMap.SagePayHeading}</h4>
			                <label>${uiLabelMap.CardTypeCaption}</label>
			                <span>${creditCardTypesMap[creditCard.cardType]!""}</span>
			              </div>
			             </li>
			            </#if>
			          </ul>
	               </#if>
	          <#elseif "EXT_EBS" == paymentMethod.paymentMethodTypeId>
	                <#-- ebs info -->
			          <ul class="displayDetail ebsInfo">
			             <li>
			              <div>
	                        <h4>${uiLabelMap.EBSHeading}</h4>
			                <label>${uiLabelMap.EBSOnlyCaption}</label>
			                <span class="ebsCheckoutImage"><span>${uiLabelMap.EBSHeading}</span></span>
			              </div>
			             </li>
			             <li>
			              <div>
			                <label>${uiLabelMap.AmountCaption}</label>
			                <span><@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
			              </div>
			             </li>
			          </ul>
	          <#elseif "EXT_PAYNETZ" == paymentMethod.paymentMethodTypeId>
	                  <ul class="displayDetail payNetzInfo">
	                     <li>
	                      <div>
	                        <h4>${uiLabelMap.PayNetzHeading}</h4>
	                        <label>${uiLabelMap.PayNetZOnlyCaption}</label>
	                        <span class="payNetzCheckoutImage"><span>${uiLabelMap.PayNetzHeading}</span></span>
	                      </div>
	                     </li>
	                     <li>
	                      <div>
	                        <label>${uiLabelMap.AmountCaption}</label>
	                        <span><@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
	                      </div>
	                     </li>
	                  </ul>
	           <#elseif "GIFT_CARD" == paymentMethod.paymentMethodTypeId >
	                <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")?if_exists>
	                <#if giftCard?has_content>
			          <ul class="displayDetail giftCardInfo">
		                 <li>
		                     <div>
	                              <h4>${uiLabelMap.GiftCardHeading}</h4>
		                         <label>${uiLabelMap.GiftCardNumberCaption}</label>
		                         <span>${giftCard.cardNumber!""}</span>
		                     </div>
		                 </li>
		                 <li>
		                     <div>
		                        <label>${uiLabelMap.AmountCaption}</label>
		                        <span>
		                            <@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/>
		                        </span>
		                     </div>
		                 </li>
	                 </ul>
	                </#if>
	            <#elseif "EXT_COD" == paymentMethod.paymentMethodTypeId>
			          <ul class="displayDetail codInfo">
		                 <li>
		                     <div>
	                             <h4>${uiLabelMap.CODHeading}</h4>
		                         <label>${uiLabelMap.PayInStoreLabel}</label>
		                         <span>&nbsp;</span>
		                     </div>
		                 </li>
		                 <li>
		                     <div>
		                        <label>${uiLabelMap.AmountCaption}</label>
		                        <span>
		                            <@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/>
		                        </span>
		                     </div>
		                 </li>
	                 </ul>
                <#elseif "FIN_ACCOUNT" == paymentMethod.paymentMethodTypeId>
                      <ul class="displayDetail finAccountInfo">
                         <li>
                             <div>
                                 <h4>${uiLabelMap.StoreCreditHeading}</h4>
                             </div>
                         </li>
                         <li>
                             <div>
                                <label>${uiLabelMap.AmountCaption}</label>
                                <span>
                                    <@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/>
                                </span>
                             </div>
                         </li>
                     </ul>
	            <#else>
			          <ul class="displayDetail payInfo">
		                 <li>
		                     <div>
		                         <label>${paymentMethod.paymentMethodTypeId}</label>
		                         <span>&nbsp;</span>
		                     </div>
		                 </li>
		                 <li>
		                     <div>
		                        <label>${uiLabelMap.AmountCaption}</label>
		                        <span>
		                            <@ofbizCurrency amount=orderPaymentPreference.maxAmount isoCode=currencyUom rounding=globalContext.currencyRounding/>
		                        </span>
		                     </div>
		                 </li>
	                 </ul>
	            </#if>
	        </#if>
        </#list>
          
  </div>          
</div>
</#if>
  
 
