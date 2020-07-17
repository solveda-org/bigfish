<#assign selectedPaymentOption = parameters.paymentOption!"">
  <input type="hidden" name="firstNameOnCard" id="firstNameOnCard" value="${billingFirstName!}" />
  <input type="hidden" name="middleNameOnCard" id="middleNameOnCard" value="" />
  <input type="hidden" name="lastNameOnCard" id="lastNameOnCard" value="${billingLastName!}" />
  <input type="hidden" name="suffixOnCard" id="suffixOnCard" value="" />
<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>&nbsp;</label>
        </div>
        <div class="entryInput checkbox medium">
            <input class="checkBoxEntry paymentOption" type="radio" id="paymentOptionCCExist" name="paymentOption"  value="CCExist" <#if (selectedPaymentOption?has_content && selectedPaymentOption?string == "CCExist") || !selectedPaymentOption?has_content> checked</#if> />${uiLabelMap.PaymentOptionCreditCardExistLabel}
        </div>
    </div>
</div>

<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>&nbsp;</label>
        </div>
        <div class="entryInput checkbox medium">
            <input class="checkBoxEntry paymentOption" type="radio" id="paymentOptionCCNew" name="paymentOption"  value="CCNew" <#if selectedPaymentOption?has_content && selectedPaymentOption?string == "CCNew"> checked</#if> />${uiLabelMap.PaymentOptionCreditCardNewLabel}
        </div>
    </div>
</div>
    
<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>&nbsp;</label>
        </div>
        <div class="entryInput checkbox medium">
            <input class="checkBoxEntry paymentOption" type="radio" id="paymentOptionOffLine" name="paymentOption"  value="OffLine" <#if selectedPaymentOption?has_content && selectedPaymentOption?string == "OffLine"> checked</#if> />${uiLabelMap.PaymentOptionOffLineLabel}
        </div>
    </div>
</div>    


<div class="infoRow row CCExist">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.PickOneCaption}<span class="required">*</span></label>
        </div>
        <div class="infoValue">
           <#assign hasSavedCard= ""/>
            <input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="CREDIT_CARD" />
           <#if paymentMethodValueMaps?has_content>
	          <select name="savedCard" id="savedCard" class="small">
                 <option value="">${uiLabelMap.CommonSelectOne}</option>
                 <#assign alreadyShownSavedCreditCardList = Static["javolution.util.FastList"].newInstance()/>
                 <#assign selectedSavedCard = parameters.savedCard!""/>
	             <#list paymentMethodValueMaps as savedPaymentMethodValueMap>
	                <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod/>
	                <#if "CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId>
                       <#assign savedCreditCard = savedPaymentMethodValueMap.creditCard?if_exists/>
                        <#if savedCreditCard?has_content>
                           <#assign cardExpireDate=savedCreditCard.expireDate?if_exists/>
                           <#assign cardNumber=savedCreditCard.cardNumber?if_exists/>
                            <#if (cardExpireDate?has_content) && (Static["org.ofbiz.base.util.UtilValidate"].isDateAfterToday(cardExpireDate)) && (cardNumber?has_content) && (!alreadyShownSavedCreditCardList.contains(cardNumber+cardExpireDate))>
	                            <option value="${savedPaymentMethod.paymentMethodId}" <#if selectedSavedCard == savedPaymentMethod.paymentMethodId> selected</#if>>
	                              ${savedCreditCard.cardType}
	                              <#assign cardNumberDisplay = "">
	                              <#if cardNumber?has_content>
	                                 <#assign size = cardNumber?length - 4>
	                                 <#if (size > 0)>
	                                   <#list 0 .. size-1 as charno>
	                                     <#assign cardNumberDisplay = cardNumberDisplay + "*">
	                                   </#list>
	                                   <#assign cardNumberDisplay = cardNumberDisplay + cardNumber[size .. size + 3]>
	                                 <#else>
	                                   <#assign cardNumberDisplay = cardNumber>
	                                 </#if>
	                              </#if>
	                               ${cardNumberDisplay?if_exists}
	                               ${uiLabelMap.CardExpirationLabel}${savedCreditCard.expireDate}
	                            </option>
	                            <#assign changed = alreadyShownSavedCreditCardList.add(cardNumber+cardExpireDate)/>
                            </#if>
                        </#if>
	                </#if>
	             </#list>
             </select>
           </#if>
        </div>
    </div>
</div>

<div class="infoRow row CCExist">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.VerificationCaption}<span class="required">*</span></label>
        </div>
        <div class="infoValue">
        		<input class="medium" type="text" maxlength="30" id="savedVerificationNo"  name="savedVerificationNo" value="${parameters.savedVerificationNo!""}"/>
        </div>
    </div>
</div>

<div class="infoRow row CCNew">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.CardTypeCaption}<span class="required">*</span></label>
        </div>
        <div class="infoValue">
             <input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="CREDIT_CARD" />
	        <select id="cardType" name="cardType" class="cardType">
                <#assign cardType = requestParameters.cardType?if_exists>
	            <#if cardType?has_content>
	              <#assign cardTypeEnums = delegator.findByAnd("Enumeration", {"enumCode" : cardType, "enumTypeId" : "CREDIT_CARD_TYPE"})?if_exists/>
	              <#if cardTypeEnums?has_content>
	                <#assign cardTypeEnum = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(cardTypeEnums) />
	                <option value="${cardTypeEnum.enumCode!}">${cardTypeEnum.description!}</option>
	              </#if>
	            </#if>
	            <option value="">${uiLabelMap.CommonSelectOne}</option>
	            ${screens.render("component://osafe/widget/CommonScreens.xml#ccTypes")}
	      </select>
        </div>
    </div>
</div>

<div class="infoRow row CCNew">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.CardNumberCaption}<span class="required">*</span></label>
        </div>
        <div class="infoValue">
        		<input class="medium" type="text" maxlength="30" id="cardNumber"  name="cardNumber" value="${parameters.cardNumber!""}"/>
        </div>
    </div>
</div>

<div class="infoRow row CCNew">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.ExpirationMonthCaption}<span class="required">*</span></label>
        </div>
        <div class="infoValue">
          <select id="expMonth" name="expMonth" class="expMonth">
            <#assign ccExprMonth = requestParameters.expMonth?if_exists>
            <#if ccExprMonth?has_content>
              <option value="${ccExprMonth?if_exists}">${ccExprMonth?if_exists}</option>
            </#if>
            <option value="">${uiLabelMap.CommonSelectOne}</option>
            ${screens.render("component://osafe/widget/CommonScreens.xml#ccMonths")}
          </select>
        </div>
    </div>
</div>

<div class="infoRow row CCNew">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.ExpirationYearCaption}<span class="required">*</span></label>
        </div>
        <div class="infoValue">
          <select id="expYear" name="expYear" class="expYear">
            <#assign ccExprYear = requestParameters.expYear?if_exists>
            <#if ccExprYear?has_content>
              <option value="${ccExprYear?if_exists}">${ccExprYear?if_exists}</option>
            </#if>
            <option value="">${uiLabelMap.CommonSelectOne}</option>
            ${screens.render("component://osafe/widget/CommonScreens.xml#ccYears")}
          </select>
        </div>
    </div>
</div>

<div class="infoRow row CCNew">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.VerificationCaption}<span class="required">*</span></label>
        </div>
        <div class="infoValue">
        		<input class="medium" type="text" maxlength="30" id="verificationNo"  name="verificationNo" value="${parameters.verificationNo!""}"/>
        </div>
    </div>
</div>

<div class="infoRow row OffLine">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>${uiLabelMap.ReferenceCaption}<span class="required">*</span></label>
        </div>
        <div class="infoValue">
        		<input class="medium" type="text" maxlength="30" id="referenceNo"  name="referenceNo" value="${parameters.referenceNo!""}"/>
        </div>
    </div>
</div>
