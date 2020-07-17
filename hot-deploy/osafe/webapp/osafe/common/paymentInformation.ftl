  <#if paymentMethods?has_content>
      <div class="checkoutOrderPaymentInformation">
        <#list paymentMethods as paymentMethod>
          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId >
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

              <#-- credit card info -->
              <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId && creditCard?has_content>
                 <div class="displayBox">
                        <h3>${uiLabelMap.PaymentInformationHeading}</h3>
                        <div class="creditCardInfo">
                            <#if creditCardTypesMap[creditCard.cardType]?has_content><p><label for="cardType">${uiLabelMap.CardTypeCaption}</label>${creditCardTypesMap[creditCard.cardType]!""}</p></#if>
                            <#if cardNumberDisplay?has_content><p><label for="cardNumber">${uiLabelMap.CardNumberCaption}</label>${cardNumberDisplay}</p></#if>
                            <#if creditCard.expireDate?has_content><p><label for="expMonth">${uiLabelMap.ExpirationDateCaption}</label>${creditCard.expireDate}</p></#if>
                        </div>
                  </div>
              </#if>
          
          <#elseif "EXT_PAYPAL" == paymentMethod.paymentMethodTypeId>

                <#assign orderPaymentPreferences = paymentMethod.getRelated("OrderPaymentPreference")>
                <#assign orderPaymentPreference = ""/>
                <#if orderPaymentPreferences?has_content>
                    <#assign orderPaymentPreference = orderPaymentPreferences[0]!"">
                </#if>
              <#-- paypal info -->
              <#if "EXT_PAYPAL" == paymentMethod.paymentMethodTypeId && orderPaymentPreference?has_content>
                 <div class="displayBox">
                           <h3>${uiLabelMap.PaymentInformationHeading}</h3>
                        <div class="payPalInfo">
                            <p><label for="paypalImage">${uiLabelMap.PayPalOnlyCaption}</label><img class="payPalImg" alt="PayPal" src="/osafe_theme/images/icon/paypal_wider.gif"></p>
                            <p><label for="amount">${uiLabelMap.AmountCaption}</label>${orderPaymentPreference.maxAmount}</p>
                        </div>
                 </div>
              </#if>
          <#elseif "SAGEPAY_TOKEN" == paymentMethod.paymentMethodTypeId>
                   <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                   <#if creditCard?has_content>
                   <div class="displayBox">
                        <h3>${uiLabelMap.PaymentInformationHeading}</h3>
                        <div class="creditCardInfo">
                            <#if creditCardTypesMap[creditCard.cardType]?has_content><p><label for="cardType">${uiLabelMap.CardTypeCaption}</label>${creditCardTypesMap[creditCard.cardType]!""}</p></#if>
                        </div>
                   </div>
                   </#if>
           </#if>
        </#list>
      </div>
<!-- section rendered when user selects STORE-PICKUP -->
  <#elseif paymentMethodType?has_content>
   <div class="checkoutOrderPaymentInformation">
      <div class="displayBox">
          <#if  isStorePickUp?has_content && isStorePickUp == "true">
          <h3>${uiLabelMap.PaymentInformationHeading}</h3>
              <#if  "EXT_COD" == paymentMethodType.paymentMethodTypeId>
                  <div class="PayInStoreInfo">
                      <p>${uiLabelMap.PayInStorelabel}</p>
                  </div>
              <#elseif "CREDIT_CARD" == paymentMethodType.paymentMethodTypeId>
                  <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                  <#if creditCard?has_content>
                     <div class="displayBox">
                          <div class="creditCardInfo">
                              <#if creditCardTypesMap[creditCard.cardType]?has_content><p><label for="cardType">${uiLabelMap.CardTypeCaption}</label>${creditCardTypesMap[creditCard.cardType]!""}</p></#if>
                          </div>
                     </div>
                  </#if>
              </#if>
          </#if>
       </div>
    </div>        
  </#if>
  
 
