<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
    <#assign partyProfileDefault = delegator.findOne("PartyProfileDefault", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "productStoreId", productStore.productStoreId), true)?if_exists/>
</#if>
<form method="post" id="paymentMethodForm" name="paymentMethodForm" >
<input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
<input type="hidden" name="paymentMethodId" value="" id="paymentMethodId"/>
<table id="paymentInformation" class="standardTable">
  <thead>
    <tr>
      <th class="creditCard firstCol">${uiLabelMap.CreditCardsLabel}</th>
      <th class="cardNumber">${uiLabelMap.CardNumberLabel}</th>
      <th class="name">${uiLabelMap.NameLabel}</th>
      <th class="expiresOn">${uiLabelMap.ExpiresOnLabel}</th>
      <th class="actionCol"></th>
      <th class="actionCol"></th>
      <th class="actionCol lastCol"></th>
    </tr>
  </thead>
  <tbody>
    <#assign alreadyShownSavedCreditCardList = Static["javolution.util.FastList"].newInstance()/>
    <#if savedPaymentMethodValueMaps?has_content>
        <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
          <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod?if_exists/>
          <#assign savedCreditCard = savedPaymentMethodValueMap.creditCard?if_exists/>
          <#if ("CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId) && (savedCreditCard?has_content)>
              <#assign cardExpireDate=savedCreditCard.expireDate?if_exists/>
              <#assign cardNumber=savedCreditCard.cardNumber?if_exists/>
              <#if cardExpireDate?has_content && (cardNumber?has_content) && (!alreadyShownSavedCreditCardList.contains(cardNumber+cardExpireDate))>
                  <#assign nameOnCard=savedCreditCard.firstNameOnCard?if_exists +" "+ savedCreditCard.lastNameOnCard?if_exists/>
                  <#assign cardType=savedCreditCard.cardType?if_exists/>
                  <#assign showExpired="true"/> 
                  <#if (cardExpireDate?has_content) && (Static["org.ofbiz.base.util.UtilValidate"].isDateAfterToday(cardExpireDate))>
                      <#assign showExpired="false"/>
                  </#if>
                  <#assign cardNumberDisplay = "">
                  <#if cardNumber?has_content>
                      <#assign size = cardNumber?length - 4>
                      <#if (size > 0)>
                        <#list 0 .. size-1 as charno>
                           <#assign cardNumberDisplay = cardNumberDisplay + "*">
                        </#list>
                        <#assign cardNumberDisplay = cardNumberDisplay + "-">
                        <#assign cardNumberDisplay = cardNumberDisplay + cardNumber[size .. size + 3]>
                      <#else>
                        <#assign cardNumberDisplay = cardNumber>
                      </#if>
                      <tr>
                        <td class="creditCard firstCol">${cardType!""}</td>
                        <td class="name">${cardNumberDisplay!""}</td>
                        <td class="cardNumber">${nameOnCard!""}</td>
                        <td class="expiresOn">${cardExpireDate!""}<#if showExpired == "true"><label> ${uiLabelMap.ExpiredLabel}</label></#if></td>
                        <td class="actionCol"><a class="standardBtn update" href="<@ofbizUrl>eCommerceEditCreditCardInfo?paymentMethodId=${savedCreditCard.paymentMethodId!""}&amp;mode='edit'</@ofbizUrl>"><span>${uiLabelMap.EditLabel}</span></a></td>
                        <td class="actionCol"><a class="standardBtn delete" href="javascript:deleteConfirm('${cardNumberDisplay}');" onclick="void(document.getElementById('paymentMethodId').value=${savedCreditCard.paymentMethodId});"><span>${uiLabelMap.DeleteLabel}</span></a></td>
                        <td class="actionCol"><#if !(partyProfileDefault?has_content && partyProfileDefault.defaultPayMeth == savedCreditCard.paymentMethodId)><a class="standardBtn action" href="<@ofbizUrl>updateDefaultPmtMethod?paymentMethodId=${savedCreditCard.paymentMethodId!""}</@ofbizUrl>"><span>${uiLabelMap.SetAsDefaultLabel}</span></a></#if></td>
                      </tr>
                      <#assign changed = alreadyShownSavedCreditCardList.add(cardNumber+cardExpireDate)/>
                  </#if>
              </#if>
          </#if>
        </#list>
    <#else>
        <tr><td colspan="4">${uiLabelMap.NoSavedPaymentMethodFound}</td></tr>
    </#if>
  </tbody>
</table>
</form>
<div class="paymentMethodButtons">
    <#if backAction?exists && backAction?has_content>
      <a class="standardBtn negative" href="<@ofbizUrl>${backAction!}</@ofbizUrl>">${uiLabelMap.CommonBack}</a>
    </#if>
    <a href="<@ofbizUrl>eCommerceCreateCreditCardInfo</@ofbizUrl>" class="standardBtn action">${uiLabelMap.AddNewCardBtn}</a>
</div>
