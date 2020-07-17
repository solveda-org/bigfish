<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>

<#if shoppingCart?has_content && shoppingCart.getOrderAttribute("STORE_LOCATION")?has_content && !Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC")>
  <#assign ccAvailable = "false" />
<#else>
  <#assign ccAvailable = "true" />
</#if>

<div class="${request.getAttribute("attributeClass")!}">
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

              <div id="js_remainingPayment" class="container remainingPayment">
                <h4>${uiLabelMap.RemainingPaymentHeading}</h4>
                  <label>${uiLabelMap.RemainingPaymentCaption}</label>
                  <#assign remainingPayment = shoppingCart.getGrandTotal().subtract(shoppingCart.getPaymentTotal())! />
                  <span><@ofbizCurrency amount=remainingPayment! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
                  <input type="hidden" name="remainingPayment" id="js_remainingPaymentValue" value="${remainingPayment}" />
              </div>
	
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
                  <#assign checkOutStoreCC= Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_STORE_CC")!""/>
                  <#assign checkOutStoreCCReq= Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_STORE_CC_REQ")!""/>
    
    	          <input type="hidden" name="companyNameOnCard" id="companyNameOnCard" value="" />
    	          <input type="hidden" name="titleOnCard" id="titleOnCard" value="" />
    	          <input type="hidden" name="firstNameOnCard" id="firstNameOnCard" value="${billingPersonFirstName!}" />
    	          <input type="hidden" name="middleNameOnCard" id="middleNameOnCard" value="" />
    	          <input type="hidden" name="lastNameOnCard" id="lastNameOnCard" value="${billingPersonLastName!}" />
    	          <input type="hidden" name="suffixOnCard" id="suffixOnCard" value="" />
    	
    	          <input type="hidden" name="cardSecurityCode" id="cardSecurityCode" value="" />
    	          <input type="hidden" name="description" id="cardSecurityCode" value="" />
    	          <input type="hidden" name="contactMechId" id="contactMechId" value="${billingContactMechId!""}" />
                  <input type="hidden" name="paymentMethodTypeId" id="js_paymentMethodTypeId" value="CREDIT_CARD" />
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
                    <div class="entry payInStore js_paymentOptions" <#if showPaymentOption == "false" || (remainingPayment <= 0)>style="display:none"</#if>>
                      <h4>${uiLabelMap.PaymentOptionsHeading}</h4>
                      <label class="radioOptionLabel"><input type="radio" id="js_payInStoreN" name="payInStore" value="N" <#if (!(parameters.payInStore?exists && parameters.payInStore?string == "Y"))>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.PayNowLabel}</span></label>
                      <label class="radioOptionLabel"><input type="radio" id="js_payInStoreY" name="payInStore" value="Y" <#if ((parameters.payInStore?exists && parameters.payInStore?string == "Y"))>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.PayInStoreLabel}</span></label>
                    </div>
                  <div id="js_checkoutPaymentOptions" <#if ccAvailable == "false" || (remainingPayment <= 0)>style="display:none"</#if>>
    	              <!-- EBS section Starts-->
    	              <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EBS")>
    	                  <div class="container ebs">
    		                <h4>${uiLabelMap.EBSHeading}</h4>
   	                        <div class="entry">
	    		                <label>${uiLabelMap.EBSCaption}</label>
	    		                <a href="javascript:submitCheckoutForm(document.${formName!}, 'EB', 'EXT_EBS');">
	    		                    <span class="ebsCheckoutImage"><span>${uiLabelMap.EBSHeading}</span></span>
	    		                </a>
	    		            </div>
    		              </div>
    		          </#if>

                      <!-- ATOM PAYNETZ section Starts-->
                      <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_PAYNETZ")>
                          <div class="container payNetz">
                            <h4>${uiLabelMap.PayNetzHeading}</h4>
   	                        <div class="entry">
	                            <label>${uiLabelMap.PayNetzCaption}</label>
	                            <a href="javascript:submitCheckoutForm(document.${formName!}, 'PNZ', 'EXT_PAYNETZ');">
	    		                    <span class="payNetzCheckoutImage"><span>${uiLabelMap.PayNetzHeading}</span></span>
	                            </a>
	                        </div>
                          </div>
                      </#if>
    	
    	              <!-- PAYPAL-->
    	              <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_PAYPAL")>
    		             <div class="container paypal">
       		                <h4>${uiLabelMap.PayPalHeading}</h4>
   	                        <div class="entry">
	    		               <label>${uiLabelMap.PayPalCaption}</label>
	    		               <a href="javascript:submitCheckoutForm(document.${formName!}, 'PA', 'EXT_PAYPAL');">
	    		                    <span class="payPalCheckoutImage"><span>${uiLabelMap.PayPalHeading}</span></span>
	    		               </a>
	    		            </div>
    		             </div>
    		          </#if>
    		            
    		          <!-- CREDIT CARD-->
    		          <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_CC")>
    		              <div class="container creditCard js_creditCardEntry">
    		              <h4>${uiLabelMap.CreditCardHeading}</h4>
    		              <#if userLogin?has_content>
    		                    <#assign partyId = userLogin.partyId!"">
    		                    <#assign partyProfileDefault = delegator.findOne("PartyProfileDefault", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "productStoreId", productStore.productStoreId), true)?if_exists/>
    		              </#if>
    		
    		              <#if (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_CC_EXIST")>
    		                  <#assign selectedPaymentOption = "PAYOPT_CC_EXIST">
    		              <#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_CC_NEW")>
    		                  <#assign selectedPaymentOption = "PAYOPT_CC_NEW">
    		              <#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_COD")>
    		                  <#assign selectedPaymentOption = "PAYOPT_COD">
    		              <#elseif Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
    		                  <#if savedPaymentMethodValueMaps?has_content>
    		                     <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
    		                       <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod/>
    		                       <#if "CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId>
    		                           <#assign hasSavedCard= "Y"/>
    		                           <#break>
    		                       </#if>
    		                     </#list>
    		                  </#if>
    		                  <#if hasSavedCard?has_content>
    		                      <#assign selectedPaymentOption = "PAYOPT_CC_EXIST">
    		                  <#else>
    		                      <#assign selectedPaymentOption = "PAYOPT_CC_NEW">
    		                  </#if>
    		              <#else>
    		                  <#assign selectedPaymentOption = "PAYOPT_CC_NEW">
    		              </#if>
    		
    		              <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
    		                <#if savedPaymentMethodValueMaps?has_content>
    		                   <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
    		                     <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod/>
    		                     <#if "CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId>
    		                         <#assign hasSavedCard= "Y"/>
    		                         <#break>
    		                     </#if>
    		                   </#list>
    		                </#if>
    		                
    		                <#if hasSavedCard?has_content>
    		                        <div class="entry">
    		                          <label class="radioOptionLabel"><input type="radio" id="js_useSavedCard" name="paymentOption" value="PAYOPT_CC_EXIST" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_CC_EXIST")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.UseSavedCardLabel}</span></label>
    		                        </div>
    		                        <div class="entry savedCreditCard">
    		                         <label for="savedCard">${uiLabelMap.SelectSavedCardCaption}</label>
              	                       <div class="entryField">
	    		                         <select id="js_savedCard" name="savedCard" class="savedCard">
	    		                           <option value="">${uiLabelMap.CommonSelectOne}</option>
	    		                             <#assign alreadyShownSavedCreditCardList = Static["javolution.util.FastList"].newInstance()/>
	    		                             <#assign selectedSavedCard = parameters.savedCard!""/>
	    		                             <#if savedPaymentMethodValueMaps?has_content>
	    		                                <#list savedPaymentMethodValueMaps as savedPaymentMethodValueMap>
	    		                                   <#assign savedPaymentMethod = savedPaymentMethodValueMap.paymentMethod?if_exists/>
	    		                                   <#assign savedCreditCard = savedPaymentMethodValueMap.creditCard?if_exists/>
	    		                                   <#if ("CREDIT_CARD" == savedPaymentMethod.paymentMethodTypeId) && (savedCreditCard?has_content)>
	    		                                     <#assign cardExpireDate=savedCreditCard.expireDate?if_exists/>
	    		                                     <#assign cardNumber=savedCreditCard.cardNumber?if_exists/>
	    		                                     <#if (cardExpireDate?has_content) && (Static["org.ofbiz.base.util.UtilValidate"].isDateAfterToday(cardExpireDate)) && (cardNumber?has_content) && (!alreadyShownSavedCreditCardList.contains(cardNumber+cardExpireDate))>
	    		                                      <#if partyProfileDefault?exists >
	    		                                      	<#assign partyProfileDefaultPayMeth = partyProfileDefault.defaultPayMeth!"" />
	    		                                      </#if>
	    		                                      <option value="${savedPaymentMethod.paymentMethodId}" <#if (selectedSavedCard == savedPaymentMethod.paymentMethodId) || (partyProfileDefaultPayMeth?exists && partyProfileDefaultPayMeth?has_content && (partyProfileDefaultPayMeth == savedCreditCard.paymentMethodId)) >selected=selected</#if>>
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
	    		                              </#if>
	    		                         </select>
	    		                         <@fieldErrors fieldName="savedCard"/>
	    		                       </div>
    		                        </div>
    		                        
    		                        <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_CC_VERIFICATION_REQ")>
    		                           <div class="entry verificationNo">
    		                              <label for="savedVerificationNo"><@required/>${uiLabelMap.VerificationCaption}</label>
		            	                   <div class="entryField">
	    		                               <input type="text" class="cardNumber" maxlength="30" id="js_savedVerificationNo"  name="savedVerificationNo" value="${requestParameters.savedVerificationNo!""}"/>
	    		                              <@fieldErrors fieldName="savedVerificationNo"/>
	    		                           </div>
    		                           </div>
    		                           <div class="entry content">
        						           <label for="content">&nbsp;</label>
                                           <span>${screens.render("component://osafe/widget/EcommerceContentScreens.xml#CHECKOUT_CC_VERIFY")}</span>
                                       </div>
    		                        </#if>
    		                        <div class="entry">
    		                            <label class="radioOptionLabel"><input type="radio" id="js_useOtherCard" name="paymentOption" value="PAYOPT_CC_NEW" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_CC_NEW")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.PayWithAnotherCardLabel}</span></label>
    		                        </div>
    		                    <#else>
    		                        <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD")>
    		                            <div class="entry">
    		                                <label class="radioOptionLabel"><input type="radio" id="useSavedCard" name="paymentOption" value="paymentOption" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_CC_NEW")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.CreditCardlabel}</span></label>
    		                            </div>
    		                        <#else>
    		                            <input type="hidden" name="paymentOption" value="paymentOption"/>
    		                        </#if>
    		                        <input type="hidden" name="useSavedCard" value="N"/>
    		                    </#if>
    		                <#else>
    		                    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD")>
    		                        <div class="entry">
    		                            <label class="radioOptionLabel"><input type="radio" id="useSavedCard" name="paymentOption" value="PAYOPT_CC_NEW" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_CC_NEW")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.CreditCardlabel}</span></label>
    		                        </div>
    		                    <#else>
    		                        <input type="hidden" name="paymentOption" value="paymentOption"/>
    		                    </#if>
    		                    <input type="hidden" name="useSavedCard" value="N"/>
    		                </#if>
    		
    			            <div class="entry cardType">
    			              <label for="cardType"><@required/>${uiLabelMap.CardTypeCaption}</label>
            	                   <div class="entryField">
		    			              <select id="js_cardType" name="cardType" class="cardType">
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
    			            </div>
    			            
    			            <div class="entry cardNumber">
    			              <label for="cardNumber"><@required/>${uiLabelMap.CardNumberCaption}</label>
            	                 <div class="entryField">
		    			              <input type="text" class="cardNumber" maxlength="30" id="js_cardNumber"  name="cardNumber" value="${creditCard.cardNumber!requestParameters.cardNumber!""}"/>
		    			              <@fieldErrors fieldName="cardNumber"/>
		    			         </div>
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
    			            
    			            <div class="entry expMonth">
    			              <label for="expMonth"><@required/>${uiLabelMap.ExpirationMonthCaption}</label>
            	                   <div class="entryField">
		    			              <select id="js_expMonth" name="expMonth" class="expMonth">
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
    			            </div>
    			            
    			            <div class="entry expYear">
    			              <label for="expYear"><@required/>${uiLabelMap.ExpirationYearCaption}</label>
            	                   <div class="entryField">
		    			              <select id="js_expYear" name="expYear" class="expYear">
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
    			            </div>
    		                
    		                <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_CC_VERIFICATION_REQ")>
    		                    <div class="entry">
    		                      <label for="verificationNo"><@required/>${uiLabelMap.VerificationCaption}</label>
            	                   <div class="entryField">
	    		                       <input type="text" class="cardNumber" maxlength="30" id="js_verificationNo"  name="verificationNo" value="${requestParameters.verificationNo!""}"/>
	    		                      <@fieldErrors fieldName="verificationNo"/>
	    		                   </div>
    		                    </div>
    		                    <div class="entry content">
        						  <label for="content">&nbsp;</label>
                                  <span>${screens.render("component://osafe/widget/EcommerceContentScreens.xml#CHECKOUT_CC_VERIFY")}</span>
                                </div>
    		                </#if>
    		              </div>
    		          <#elseif Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD")>
    		              <#assign selectedPaymentOption = "PAYOPT_COD">
    	              </#if>
    	              
    		          <!-- cod-->
	                  <div class="container paymentOption js_codOptions" <#if (shoppingCart?has_content && shoppingCart.getOrderAttribute("STORE_LOCATION")?has_content) || !(Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD"))>style="display:none"</#if>>
   	                   <h4>${uiLabelMap.CODHeading}</h4>
		                   <div class="entry">
			                    <label class="radioOptionLabel"><input type="radio" id="codPayment" name="paymentOption" value="PAYOPT_COD" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_COD")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.CODLabel}</span></label>
			               </div>
	                  </div>
                  </div> <!-- End of checkoutPaymentOptions DIV -->
	        
        </div>
	<a name="paymentMethod"></a>
    </div>
 </div>
