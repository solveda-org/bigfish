<div class="${request.getAttribute("attributeClass")!}">
	<form method="post" id="paymentMethodForm" name="paymentMethodForm" >
        <input type="hidden" name="paymentMethodId" value="" id="js_paymentMethodId"/>
        <#if savedPaymentMethodValueMaps?has_content>
            <div class="boxList paymentMethodList">
                <#assign alreadyShownSavedCreditCardList = Static["javolution.util.FastList"].newInstance()/>
                <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
                  <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod?if_exists/>
                  <#assign savedCreditCard = savedPaymentMethodValueMap.creditCard?if_exists/>
                  <#if (savedCreditCard?has_content) && ("CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId)>
                      <#assign cardExpireDate=savedCreditCard.expireDate?if_exists/>
                      <#assign cardNumber=savedCreditCard.cardNumber?if_exists/>
                      <#if cardExpireDate?has_content && (cardNumber?has_content) && (!alreadyShownSavedCreditCardList.contains(cardNumber+cardExpireDate))>
                          ${setRequestAttribute("savedCreditCard", savedCreditCard)}
                          ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#paymentMethodItemsDivSequence")}
                          <#assign changed = alreadyShownSavedCreditCardList.add(cardNumber+cardExpireDate)/>
                      </#if>
                  </#if>
                </#list>
            </div>
        <#else>
            <div class="displayBox">
              <h3>${uiLabelMap.NoSavedPaymentMethodFound}</h3>
            </div>
        </#if>
    </form>
</div>

