<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>

<#if shoppingCart?has_content && shoppingCart.getOrderAttribute("STORE_LOCATION")?has_content && !Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC")>
  <#assign ccRequired = "false" />
<#else>
  <#assign ccRequired = "true" />
</#if>

<div class="checkoutOrderPayments" >
	<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
	    <#-- Enter Credit Card Box -->
	    <div class="displayBox">
	        <h3>${uiLabelMap.PaymentInformationHeading}</h3>
	        <#assign donePage = "donePage">
	        <#if !creditCard?exists>
	           <div>
	            <#--
	                Sending to validation routine, we want to only show field level messages when adding a "Credit Card"
	                but want to allow teh screen in general to show general messages if you try to continue without choosing
	                a payment method
	            -->
	        <#else>
	           <div>
	            <input type="hidden" name="paymentMethodId" value="${paymentMethodId}" />
	        </#if>
	
	        <#-- Added to deal with payment being declined -->
	        <input type="hidden" id="BACK_PAGE" name="BACK_PAGE" value="checkoutoptions" />
	
	        <#-- Added so on successful redirect to "Order Complete" page we know to show the "Thank You" message  -->
	        <#-- <input type="hidden" id="showThankYouStatus" name="showThankYouStatus" value="Y" /> -->
	
	        <#if !creditCard?has_content>
	            <#assign creditCard = requestParameters>
	        </#if>
	
	        <#if !paymentMethod?has_content>
	            <#assign paymentMethod = requestParameters>
	        </#if>
	        <fieldset class="col">
	          <input type="hidden" name="companyNameOnCard" id="companyNameOnCard" value="" />
	          <input type="hidden" name="titleOnCard" id="titleOnCard" value="" />
	
	          <#-- Place holder for fields that are ootb but not currently on our screen -->
	          <input type="hidden" name="firstNameOnCard" id="firstNameOnCard" value="${billingPersonFirstName!}" />
	          <input type="hidden" name="middleNameOnCard" id="middleNameOnCard" value="" />
	          <input type="hidden" name="lastNameOnCard" id="lastNameOnCard" value="${billingPersonLastName!}" />
	          <input type="hidden" name="suffixOnCard" id="suffixOnCard" value="" />
	
	          <input type="hidden" name="cardSecurityCode" id="cardSecurityCode" value="" />
	          <input type="hidden" name="description" id="cardSecurityCode" value="" />
	          <input type="hidden" name="contactMechId" id="contactMechId" value="${billingContactMechId!""}" />
                <input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="CREDIT_CARD" />
                <#assign checkOutStoreCC= Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_STORE_CC")!""/>
                <#assign checkOutStoreCCReq= Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_STORE_CC_REQ")!""/>
                <input type="hidden" name="storeCCRequired" id="storeCCRequired" value="${checkOutStoreCC!"true"}" />
                <input type="hidden" name="storeCCValidate" id="storeCCValidate" value="${checkOutStoreCCReq!"false"}" />
               <#if shoppingCart?has_content && shoppingCart.getOrderAttribute("STORE_LOCATION")?has_content>
                <#if !Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC_REQ") && Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC")>
                   <#assign showPaymentOption = "true"/>
                <#else>
                   <#assign showPaymentOption = "false" />
                </#if>
              <#else>
                <#assign showPaymentOption = "false"/>
              </#if>
                <div class ="paymentOptions" <#if showPaymentOption == "false">style="display:none"</#if>>
                 <h3>${uiLabelMap.PaymentOptionsHeading}</h3>
                  <div class="entry">
                    <input type="radio" id="payInStore" name="payInStore" value="N" <#if ((parameters.payInStore?exists && parameters.payInStore?string == "N"))>checked="checked"</#if>/><span>${uiLabelMap.PayNowLabel}</span>
                    <input type="radio" id="payInStore" name="payInStore" value="Y" <#if ((parameters.payInStore?exists && parameters.payInStore?string == "Y"))>checked="checked"</#if>/><span>${uiLabelMap.PayInStoreLabel}</span>
                  </div>
                </div>
                
                <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EBS")>
                  <!-- EBS section Starts-->
                  <div class="entry ebs">
	                <h3>${uiLabelMap.EBSHeading}</h3>
	                <label>${uiLabelMap.EBSCaption}</label>
	                <a href="javascript:submitCheckoutForm(document.${formName!}, 'EB', 'EXT_EBS');">
	                    <img class="paymentMethodImg" alt="EBS Checkout" src="http://www.ebs.in/images/logo_ebs.png">
	                </a>
	              </div>
                  <!-- EBS section Ends-->
	            </#if>
                <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_PAYPAL")>
	            <div class="entry paypal">
	            <h3>${uiLabelMap.PayPalHeading}</h3>
	              <label>${uiLabelMap.PayPalCaption}</label>
	              <a href="javascript:submitCheckoutForm(document.${formName!}, 'PA', 'EXT_PAYPAL');">
	                    <img class="paymentMethodImg" alt="PayPal Checkout" src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif">
	              </a>
	            </div>
	            </#if>
	            <!-- Credit card section Starts-->
                 <div class = "creditCardEntry" <#if ccRequired == "false">style="display:none"</#if>>
                 <div class="entry">
                    <h3>${uiLabelMap.CreditCardHeading}</h3>
                </div>
                <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
                    <#assign savedPaymentMethodValueMaps = Static["org.ofbiz.accounting.payment.PaymentWorker"].getPartyPaymentMethodValueMaps(delegator, partyId!"") />
                    <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
                        <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod/>
                        <#if "CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId>
                            <#assign hasSavedCard= "Y"/>
                            <#break>
                        </#if>
                    </#list>
                    <#if hasSavedCard?has_content>
                        <div class="entry">
                            <input type="radio" id="useSavedCard" name="useSavedCard" value="Y" <#if ((parameters.useSavedCard?exists && parameters.useSavedCard?string == "Y") || (savedPaymentMethodValueMaps?has_content))>checked="checked"</#if>/><span>${uiLabelMap.UseSavedCardLabel}</span>
                        </div>
                        <div class="entry">
                            <label for="savedCard">${uiLabelMap.SelectSavedCardCaption}</label>
                             <select id="savedCard" name="savedCard" class="savedCard">
                                 <option value="">${uiLabelMap.CommonSelectOne}</option>
                                 <#assign alreadyShownSavedCreditCardList = Static["javolution.util.FastList"].newInstance()/>
                                 <#assign selectedSavedCard = parameters.savedCard!""/>
                                 <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
                                     <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod?if_exists/>
                                     <#assign savedCreditCard = savedPaymentMethodValueMap.creditCard?if_exists/>
                                     <#if ("CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId) && (savedCreditCard?has_content)>
                                         <#assign cardExpireDate=savedCreditCard.expireDate?if_exists/>
                                         <#assign cardNumber=savedCreditCard.cardNumber?if_exists/>
                                         <#if (cardExpireDate?has_content) && (Static["org.ofbiz.base.util.UtilValidate"].isDateAfterToday(cardExpireDate)) && (cardNumber?has_content) && (!alreadyShownSavedCreditCardList.contains(cardNumber+cardExpireDate))>
                                             <option value="${savedPaymentMethod.paymentMethodId}" <#if selectedSavedCard == savedPaymentMethod.paymentMethodId >selected=selected</#if>>
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
                                 </#list>
                            </select>
                            <@fieldErrors fieldName="savedCard"/>
                        </div>
                        <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_CC_VERIFICATION_REQ")>
                            <div class="entry">
                              <label for="savedVerificationNo"><@required/>${uiLabelMap.VerificationCaption}</label>
                               <input type="text" class="cardNumber" maxlength="30" id="savedVerificationNo"  name="savedVerificationNo" value="${requestParameters.savedVerificationNo!""}"/>
                              <@fieldErrors fieldName="savedVerificationNo"/>
                            </div>
                        </#if>
                        <div class="entry">
                            <input type="radio" id="useSavedCard" name="useSavedCard" value="N" <#if ((parameters.useSavedCard?exists && parameters.useSavedCard?string == "N"))>checked="checked"</#if>/><span>${uiLabelMap.PayWithAnotherCardlabel}</span>
                        </div>
                    <#else>
                        <input type="hidden" name="useSavedCard" value="N"/>
                    </#if>
                <#else>
                        <input type="hidden" name="useSavedCard" value="N"/>
                </#if>

	            <div class="entry">
	              <label for="cardType"><@required/>${uiLabelMap.CardTypeCaption}</label>
	                <select id="cardType" name="cardType" class="cardType">
                        <#if creditCard?has_content && creditCard.cardType?has_content>
                          <#assign cardType = creditCard.cardType>
                        <#else>
                          <#assign cardType = requestParameters.cardType?if_exists>
                        </#if>
                        <#if cardType?has_content>
                          <#assign cardTypeEnums = delegator.findByAndCache("Enumeration", {"enumCode" : cardType, "enumTypeId" : "CREDIT_CARD_TYPE"})?if_exists/>
                          <#if cardTypeEnums?has_content>
                            <#assign cardTypeEnum = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(cardTypeEnums) />
                            <option value="${cardTypeEnum.enumCode!}">${cardTypeEnum.description!}</option>
                          </#if>
                        </#if>
	                    <option value="">${uiLabelMap.CommonSelectOne}</option>
	                    ${screens.render("component://osafe/widget/CommonScreens.xml#ccTypes")}
	              </select>
	              <@fieldErrors fieldName="cardType"/>
	            </div>
	            <div class="entry">
	              <label for="cardNumber"><@required/>${uiLabelMap.CardNumberCaption}</label>
	               <input type="text" class="cardNumber" maxlength="30" id="cardNumber"  name="cardNumber" value="${creditCard.cardNumber!requestParameters.cardNumber!""}"/>
	              <@fieldErrors fieldName="cardNumber"/>
	            </div>
	              <#assign expMonth = "">
	              <#assign expYear = "">
	              <#if creditCard?exists && creditCard.expireDate?exists>
	                <#assign expDate = creditCard.expireDate>
	                <#if (expDate?exists && expDate.indexOf("/") > 0)>
	                  <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
	                  <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
	                </#if>
	              </#if>
	            <div class="entry">
	              <label for="expMonth"><@required/>${uiLabelMap.ExpirationMonthCaption}</label>
	              <select id="expMonth" name="expMonth" class="expMonth">
	                <#if creditCard?has_content && expMonth?has_content>
	                  <#assign ccExprMonth = expMonth>
	                <#else>
	                  <#assign ccExprMonth = requestParameters.expMonth?if_exists>
	                </#if>
	                <#if ccExprMonth?has_content>
	                  <option value="${ccExprMonth?if_exists}">${ccExprMonth?if_exists}</option>
	                </#if>
	                <option value="">${uiLabelMap.CommonSelectOne}</option>
	                ${screens.render("component://osafe/widget/CommonScreens.xml#ccMonths")}
	              </select>
	               <@fieldErrors fieldName="expMonth"/>
	            </div>
	            <div class="entry">
	              <label for="expYear"><@required/>${uiLabelMap.ExpirationYearCaption}</label>
	              <select id="expYear" name="expYear" class="expYear">
	                <#if creditCard?has_content && expYear?has_content>
	                  <#assign ccExprYear = expYear>
	                <#else>
	                  <#assign ccExprYear = requestParameters.expYear?if_exists>
	                </#if>
	                <#if ccExprYear?has_content>
	                  <option value="${ccExprYear?if_exists}">${ccExprYear?if_exists}</option>
	                </#if>
	                <option value="">${uiLabelMap.CommonSelectOne}</option>
	                ${screens.render("component://osafe/widget/CommonScreens.xml#ccYears")}
	              </select>
	              <@fieldErrors fieldName="expYear"/>
	            </div>
                <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_CC_VERIFICATION_REQ")>
                    <div class="entry">
                      <label for="verificationNo"><@required/>${uiLabelMap.VerificationCaption}</label>
                       <input type="text" class="cardNumber" maxlength="30" id="verificationNo"  name="verificationNo" value="${requestParameters.verificationNo!""}"/>
                      <@fieldErrors fieldName="verificationNo"/>
                    </div>
                </#if>
              </div>
              <!-- Credit card section Ends-->
              
              <#-- Cash on Delivery start -->
               <div class ="codOptions" <#if showPaymentOption == "true" || !(Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD"))>style="display:none"</#if>>
                 <h3>${uiLabelMap.CODHeading}</h3>
                  <div class="entry">
                    <input type="radio" id="codPayment" name="codPayment" value="Y" <#if ((parameters.codPayment?exists && parameters.codPayment?string == "Y"))>checked="checked"</#if>/><span>${uiLabelMap.CODLabel}</span>
                  </div>
               </div>
	        </fieldset>
	
	           </div>
	        <a name="paymentMethod"></a>
	
	    </div>
 </div>
