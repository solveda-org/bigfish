<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign giftCardMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_GIFTCARD_METHOD")!""/>
<#if giftCardMethod?has_content && (giftCardMethod.toUpperCase() != "NONE") >
    <#if localOrderReadHelper?has_content && paymentMethods?has_content>
        <#assign hasGiftCardPayment= "N"/>
        <#list paymentMethods as paymentMethod>
            <#if "GIFT_CARD" == paymentMethod.paymentMethodTypeId >
                <#assign hasGiftCardPayment= "Y"/>
                <#break>
            </#if>
        </#list>
        <#if (hasGiftCardPayment?has_content) && hasGiftCardPayment == "Y" >
            <div class="checkoutGiftCardPaymentInformation">
              <div class="displayBox">
                <h3>${uiLabelMap.GiftCardRedemptionHeading}</h3>
                <ul class="displayList giftCardInfo">
                    <#list paymentMethods as paymentMethod>
                      <#if "GIFT_CARD" == paymentMethod.paymentMethodTypeId >
                        <#assign redeemedAmount = 0>
                        <#assign orderPaymentPreferences = paymentMethod.getRelated("OrderPaymentPreference")>
                        <#assign orderPaymentPreference = ""/>
                        <#if orderPaymentPreferences?has_content>
                            <#assign orderPaymentPreference = orderPaymentPreferences[0]!"">
                            <#assign redeemedAmount = orderPaymentPreference.maxAmount!"">
                        <#else>
                            <#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
                            <#if (shoppingCart.size() > 0) && shoppingCart?has_content>
                                <#assign redeemedAmount = shoppingCart.getPaymentAmount(paymentMethod.paymentMethodId)!"">
                            </#if>
                        </#if>
                        <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")?if_exists>
                         <li>
                             <div>
                                 <label for="cardNumber">${uiLabelMap.CardNumberCaption}</label>
                                 <span>${giftCard.cardNumber!""}</span>
                             </div>
                         </li>
                         <li>
                             <div>
                                <label for="amount">${uiLabelMap.AmountCaption}</label>
                                <span>
                                    <@ofbizCurrency amount=redeemedAmount isoCode=currencyUom rounding=globalContext.currencyRounding/>
                                </span>
                             </div>
                         </li>
                      </#if>
                   </#list>
                 </ul>
              </div>
            </div>
        </#if>
    </#if>
</#if>