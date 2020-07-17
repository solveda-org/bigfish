<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<div class="checkoutOrderPayments">
	<div id="orderPayment">
	<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
	    <#-- Enter Credit Card Box -->
	          <form method="post" action="<@ofbizUrl>setPayPalCheckout</@ofbizUrl>" name="paypalForm">
	          </form>
	    <div class="displayBox">
	     <div class="displayBoxHeader">
	        <span class="displayBoxHeaderCaption">${uiLabelMap.PaymentInformationHeading}</span>
	     </div>
	        <#assign donePage = "donePage">
	        <#if !creditCard?exists>
	          <form method="post" action="<@ofbizUrl>${validateCreditCardRequest!}?DONE_PAGE=${donePage}</@ofbizUrl>" name="editcreditcardform">
	           <div>
	            <#--
	                Sending to validation routine, we want to only show field level messages when adding a "Credit Card"
	                but want to allow teh screen in general to show general messages if you try to continue without choosing
	                a payment method
	            -->
	            <input type="hidden" name="fieldLevelErrors" value="Y" />
	        <#else>
	          <form method="post" action="<@ofbizUrl>${validateCreditCardRequest!}?DONE_PAGE=${donePage}</@ofbizUrl>" name="editcreditcardform">
	           <div>
	            <input type="hidden" name="paymentMethodId" value="${paymentMethodId}" />
	            <input type="hidden" name="fieldLevelErrors" value="Y" />
	        </#if>
	
	        <#-- Added to deal with payment being declined -->
	        <input type="hidden" id="checkoutpage" name="checkoutpage" value="payment" />
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
	          <input type="hidden" name="firstNameOnCard" id="firstNameOnCard" value="${billingPersonFirstName}" />
	          <input type="hidden" name="middleNameOnCard" id="middleNameOnCard" value="" />
	          <input type="hidden" name="lastNameOnCard" id="lastNameOnCard" value="${billingPersonLastName}" />
	          <input type="hidden" name="suffixOnCard" id="suffixOnCard" value="" />
	
	          <input type="hidden" name="cardSecurityCode" id="cardSecurityCode" value="" />
	          <input type="hidden" name="description" id="cardSecurityCode" value="" />
	          <input type="hidden" name="contactMechId" id="contactMechId" value="${billingContactMechId!""}" />
	
	            <div class="entry paypal">
	            <h3>${uiLabelMap.PayPalHeading}</h3>
	              <label>${uiLabelMap.PayPalCaption}</label>
	              <a href="<@ofbizUrl>setPayPalCheckout</@ofbizUrl>">
	                    <img class="paymentMethodImg" alt="PayPal Checkout" src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif">
	              </a>
	            </div>

                 <div class="entry">
                    <h3>${uiLabelMap.CreditCardHeading}</h3>
                </div>
                <#if CHECKOUT_KEEP_PAYMENT_METHODS?has_content && Static["com.osafe.util.Util"].isProductStoreParmTrue(CHECKOUT_KEEP_PAYMENT_METHODS)>
                    <#assign savedPaymentMethodValueMaps = Static["org.ofbiz.accounting.payment.PaymentWorker"].getPartyPaymentMethodValueMaps(delegator, shoppingCart.getOrderPartyId()) />
                    <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
                        <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod/>
                        <#if "CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId>
                            <#assign hasSavedCard= "Y"/>
                            <#break>
                        </#if>
                    </#list>
                    <#if hasSavedCard?has_content>
                        <div class="entry">
                            <input type="radio" id="useSavedCard" name="useSavedCard" value="Y" <#if ((parameters.useSavedCard?exists && parameters.useSavedCard?string == "Y") || (savedPaymentMethodValueMaps?has_content))>checked="checked"</#if>/>${uiLabelMap.UseSavedCardlabel}
                        </div>
                        <div class="entry">
                            <label for="savedCard">${uiLabelMap.SelectSavedcardCaption}</label>
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
                        <div class="entry">
                          <label for="savedVerificationNo"><@required/>${uiLabelMap.VerificationCaption}</label>
                           <input type="text" class="cardNumber" maxlength="30" id="savedVerificationNo"  name="savedVerificationNo" value="${requestParameters.savedVerificationNo!""}"/>
                          <@fieldErrors fieldName="savedVerificationNo"/>
                        </div>
                        <div class="entry">
                            <input type="radio" id="useSavedCard" name="useSavedCard" value="N" <#if ((parameters.useSavedCard?exists && parameters.useSavedCard?string == "N"))>checked="checked"</#if>/>${uiLabelMap.PayWithAnotherCardlabel}
                        </div>
                    </#if>
                </#if>

	            <div class="entry">
	              <label for="cardType"><@required/>${uiLabelMap.CardTypeCaption}</label>
	                <select id="cardType" name="cardType" class="cardType">
	                    <#if creditCard.cardType?exists>
	                        <option>${creditCard.cardType}</option>
	
	                        <option value="${creditCard.cardType}">---</option>
	                    </#if>
	                    <option value="">${uiLabelMap.CommonSelectOne}</option>
	                    ${screens.render("component://common/widget/CommonScreens.xml#cctypes")}
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
	                ${screens.render("component://common/widget/CommonScreens.xml#ccmonths")}
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
	                ${screens.render("component://common/widget/CommonScreens.xml#ccyears")}
	              </select>
	              <@fieldErrors fieldName="expYear"/>
	            </div>
                <div class="entry">
                  <label for="verificationNo"><@required/>${uiLabelMap.VerificationCaption}</label>
                   <input type="text" class="cardNumber" maxlength="30" id="verificationNo"  name="verificationNo" value="${requestParameters.verificationNo!""}"/>
                  <@fieldErrors fieldName="verificationNo"/>
                </div>
	
	        </fieldset>
	
	           </div>
	          </form>
	        <a name="paymentMethod"></a>
	
	    </div>
	</div>
 </div>
