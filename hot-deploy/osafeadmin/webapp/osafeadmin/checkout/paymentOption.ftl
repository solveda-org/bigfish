<#assign selectedPaymentOption = parameters.paymentOption!"">
  <input type="hidden" name="firstNameOnCard" id="firstNameOnCard" value="${billingFirstName!}" />
  <input type="hidden" name="middleNameOnCard" id="middleNameOnCard" value="" />
  <input type="hidden" name="lastNameOnCard" id="lastNameOnCard" value="${billingLastName!}" />
  <input type="hidden" name="suffixOnCard" id="suffixOnCard" value="" />
  <#if !screen?has_content || screen !="checkout">
  	<input type="hidden" name="orderId" id="orderId" value="${parameters.orderId!}" />
  	<#if orderReadHelper?has_content>
	  	<#assign currencyUomId = orderReadHelper.getCurrency()>
	  	<#if currencyUomId?has_content>
	  	  <input type="hidden" name="currencyUomId" id="currencyUomId" value="${currencyUomId!}" />
	    </#if>
    </#if>
  </#if>

<div class="infoRow row balanceDue">
    <div class="infoEntry long" id="balanceDue">
        <div class="infoCaption">
            <label>${uiLabelMap.BalanceDueCaption}</label>
        </div>
        <div class="infoValue">
          <#assign currencyUom = CURRENCY_UOM_DEFAULT! />
          <#if !currencyUom?has_content && shoppingCart?has_content>
          	<#assign currencyUom = shoppingCart.getCurrency() />
          </#if>
          <#if screen?has_content && screen =="checkout">
        	<#if !shoppingCart?has_content>
        	  <#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request) />
        	</#if>
            <#assign remainingPayment = shoppingCart.getGrandTotal().subtract(shoppingCart.getPaymentTotal())! />
            <@ofbizCurrency amount=remainingPayment! isoCode=currencyUom  rounding=globalContext.currencyRounding/>
            <input type="hidden" name="remainingPayment" id="remainingPayment" value="${remainingPayment!}" />
          <#else>
            <@ofbizCurrency amount=orderOpenAmount?default(0.00) isoCode=currencyUom rounding=globalContext.currencyRounding/>
            <input type="hidden" name="remainingPayment" id="remainingPayment" value="${orderOpenAmount!}" />
          </#if>
        </div>
    </div>
</div>


<div id="paymentOptionInfo">
	<div class="infoRow row">
	    <div class="infoEntry long">
	        <div class="infoCaption">
	            <label>${uiLabelMap.PickOneCaption}</label>
	        </div>
	        <div class="entry checkbox medium">
	            <input class="checkBoxEntry paymentOption" type="radio" id="paymentOptionCCExist" name="paymentOption"  value="CCExist" <#if (selectedPaymentOption?has_content && selectedPaymentOption?string == "CCExist") || !selectedPaymentOption?has_content> checked</#if> />${uiLabelMap.PaymentOptionCreditCardExistLabel}</br>
	            <input class="checkBoxEntry paymentOption" type="radio" id="paymentOptionCCNew" name="paymentOption"  value="CCNew" <#if selectedPaymentOption?has_content && selectedPaymentOption?string == "CCNew"> checked</#if> />${uiLabelMap.PaymentOptionCreditCardNewLabel}</br>
	            <input class="checkBoxEntry paymentOption" type="radio" id="paymentOptionOffLine" name="paymentOption"  value="OffLine" <#if selectedPaymentOption?has_content && selectedPaymentOption?string == "OffLine"> checked</#if> />${uiLabelMap.PaymentOptionOffLineLabel}</br>
	        </div>
	    </div>
	</div>
	
	<div class="infoRow row CCExist">
	    <div class="infoEntry long">
	        <div class="infoCaption">
	            <label>${uiLabelMap.SelectOneCaption}</label>
	        </div>
	        <div class="infoValue">
	          <#assign hasSavedCard= ""/>
	          <input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="CREDIT_CARD" />
		        <select name="savedCard" id="savedCard" class="small">
	             <option value="">${uiLabelMap.CommonSelectOne}</option>
	               <#if paymentMethodValueMaps?has_content>
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
		           </#if>
	            </select>
	        </div>
	    </div>
	</div>
	
	<div class="infoRow row CCExist">
	    <div class="infoEntry long">
	        <div class="infoCaption">
	            <label><span class="required">*</span>${uiLabelMap.VerificationCaption}</label>
	        </div>
	        <div class="infoValue">
	        		<input class="medium" type="text" maxlength="30" id="savedVerificationNo"  name="savedVerificationNo" value="${parameters.savedVerificationNo!""}"/>
	        </div>
	    </div>
	</div>
	
	<div class="infoRow row CCNew">
	    <div class="infoEntry long">
	        <div class="infoCaption">
	            <label><span class="required">*</span>${uiLabelMap.CardTypeCaption}</label>
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
	    
	    <#if !screen?has_content || screen !="checkout">
		  <input type="hidden" name="billingContactMechId" value="${billingContactMechId!""}"/>
		</#if>
	
	</div>
	
	<div class="infoRow row CCNew">
	    <div class="infoEntry long">
	        <div class="infoCaption">
	            <label><span class="required">*</span>${uiLabelMap.CardNumberCaption}</label>
	        </div>
	        <div class="infoValue">
	        		<input class="medium" type="text" maxlength="30" id="cardNumber"  name="cardNumber" value="${parameters.cardNumber!""}"/>
	        </div>
	    </div>
	</div>
	
	<div class="infoRow row CCNew">
	    <div class="infoEntry long">
	        <div class="infoCaption">
	            <label><span class="required">*</span>${uiLabelMap.ExpirationMonthCaption}</label>
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
	            <label><span class="required">*</span>${uiLabelMap.ExpirationYearCaption}</label>
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
	            <label><span class="required">*</span>${uiLabelMap.VerificationCaption}</label>
	        </div>
	        <div class="infoValue">
	        		<input class="medium" type="text" maxlength="30" id="verificationNo"  name="verificationNo" value="${parameters.verificationNo!""}"/>
	        </div>
	    </div>
	</div>
	
	<#if showOfflinePaymentOptions?exists && showOfflinePaymentOptions?has_content && showOfflinePaymentOptions =="Y">
	   <div class="infoRow row OffLine">
	      <div class="infoEntry">
	        <div class="infoCaption">
	            <label><span class="required">*</span>${uiLabelMap.OfflinePaymentCaption}</label>
	        </div>
	        <div class="infoValue">
	          <select name="paymentMethodId" id="paymentMethodId">
	              <#assign offlinePaymentMethodId = requestParameters.paymentMethodId?if_exists>
		          <#if offlinePaymentMethodId?has_content>
		            <#assign offlinePaymentMethods = delegator.findByAnd("PaymentMethodType", {"paymentMethodTypeId" : offlinePaymentMethodId})?if_exists/>
		            <#if offlinePaymentMethods?has_content>
		              <#assign offlinePaymentMethod = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(offlinePaymentMethods) />
		              <option value="${offlinePaymentMethod.paymentMethodTypeId!}">${offlinePaymentMethod.description!}</option>
		            </#if>
		          </#if>
	              <option value="">${uiLabelMap.SelectOneLabel}</option>
	              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#offlinePaymentMethodType")}
	          </select>
	        </div>
	      </div>
	    </div>
	    
	    <div class="infoRow row OffLine">
	      <div class="infoEntry">
	        <div class="infoCaption">
	            <label><span class="required">*</span>${uiLabelMap.AmountPaidCaption}</label>
	        </div>
	        <div class="infoValue">
	          <input class="medium" type="text" maxlength="30" id="maxAmount"  name="maxAmount" value="${parameters.maxAmount!""}"/>
	        </div>
	      </div>
	    </div>
	    
	    <div class="infoRow row OffLine">
		    <div class="infoEntry long">
		        <div class="infoCaption">
		            <label><span class="required">*</span>${uiLabelMap.ReferenceCaption}</label>
		        </div>
		        <div class="infoValue">
		        		<input class="medium" type="text" maxlength="30" id="referenceNo"  name="referenceNo" value="${parameters.referenceNo!""}"/>
		        </div>
		    </div>
		</div>
	</#if>
	
	
	
	
</div>

