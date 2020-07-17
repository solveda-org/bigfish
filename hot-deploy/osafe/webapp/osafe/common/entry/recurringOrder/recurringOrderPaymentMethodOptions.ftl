<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign selectedSavedPayment = ""/>
<#if parameters.paymentMethodId?exists && parameters.paymentMethodId?has_content>
  <#assign selectedSavedPayment = parameters.paymentMethodId/>
<#else>
  <#if paymentMethodId?exists && paymentMethodId?has_content>
    <#assign selectedSavedPayment = paymentMethodId/>
  <#else>
    <#if partyProfileDefault?exists && partyProfileDefault?has_content>
  	  <#assign selectedSavedPayment = partyProfileDefault.defaultPayMeth!"" />
    </#if>
  </#if>
</#if>
<div class="${request.getAttribute("attributeClass")!}">
  <label>${uiLabelMap.CartItemPaymentMethodCaption}</label>
  <div class="entryField">
    <select id="paymentMethodId" name="paymentMethodId" class="paymentMethodId">
       <option value="">${uiLabelMap.CommonSelectOne}</option>
         <#assign alreadyShownSavedCreditCardList = Static["javolution.util.FastList"].newInstance()/>
         <#if savedPaymentMethodValueMaps?has_content>
            <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
               <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod?if_exists/>
               <#assign savedCreditCard = savedPaymentMethodValueMap.creditCard?if_exists/>
               <#if ("CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId) && (savedCreditCard?has_content)>
                 <#assign cardExpireDate=savedCreditCard.expireDate?if_exists/>
                 <#assign cardNumber=savedCreditCard.cardNumber?if_exists/>
                 <#if (cardExpireDate?has_content) && (Static["org.ofbiz.base.util.UtilValidate"].isDateAfterToday(cardExpireDate)) && (cardNumber?has_content) && ((!alreadyShownSavedCreditCardList.contains(cardNumber+cardExpireDate)) || selectedSavedPayment == savedPaymentMethod.paymentMethodId)>
                  
                  <option value="${savedPaymentMethod.paymentMethodId}" <#if (selectedSavedPayment == savedPaymentMethod.paymentMethodId) >selected=selected</#if>>
                        ${savedCreditCard.cardType}
                    <#assign cardNumberDisplay = "">
                    <#if cardNumber?has_content>
                       <#assign size = cardNumber?length - 4>
                       <#if (size > 0)>
                         <#assign cardNumberDisplay = cardNumberDisplay + "*">
                         <#assign cardNumberDisplay = cardNumberDisplay + cardNumber[size .. size + 3]>
                       <#else>
                         <#assign cardNumberDisplay = cardNumber>
                       </#if>
                    </#if>
                    ${cardNumberDisplay?if_exists}
                    ${savedCreditCard.expireDate}
                  </option>
                  <#assign changed = alreadyShownSavedCreditCardList.add(cardNumber+cardExpireDate)/>
                 </#if>
               </#if>
             </#list>
          </#if>
          
          <#assign alreadyShownSavedEFTList = Static["javolution.util.FastList"].newInstance()/>
             <#if savedPaymentMethodEftValueMaps?has_content>
                <#list savedPaymentMethodEftValueMaps as savedPaymentMethodEftValueMap>
                   <#assign savedPaymentMethod = savedPaymentMethodEftValueMap.paymentMethod?if_exists/>
                   <#assign savedEftAccount = savedPaymentMethodEftValueMap.eftAccount?if_exists/>
                   <#if ("EFT_ACCOUNT" == savedPaymentMethod.paymentMethodTypeId) && (savedEftAccount?has_content)>

                     <#assign paymentMethodId = savedEftAccount.paymentMethodId?if_exists/>
                     <#assign bankName = savedEftAccount.bankName?if_exists/>
                     <#assign nameOnAccount = savedEftAccount.nameOnAccount?if_exists/>
                     <#assign accountType = savedEftAccount.accountType?if_exists/>
                     <#assign accountNumber = savedEftAccount.accountNumber?if_exists/>
                     
                     <#if !alreadyShownSavedEFTList.contains(accountNumber+bankName) || selectedSavedPayment == savedPaymentMethod.paymentMethodId>
                      <option value="${savedPaymentMethod.paymentMethodId}" <#if (selectedSavedPayment == savedPaymentMethod.paymentMethodId)>selected=selected</#if>>
                        ${bankName?if_exists},
                        ${nameOnAccount?if_exists},
                        ${accountType?if_exists}
                      </option>
                      <#assign changed = alreadyShownSavedEFTList.add(accountNumber+bankName)/>
                     </#if>
                   </#if>
                 </#list>
              </#if>
     </select>
     <@fieldErrors fieldName="paymentMethodId"/>
   </div>
</div>
