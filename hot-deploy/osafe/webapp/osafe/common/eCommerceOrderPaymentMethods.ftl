<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
    <#assign partyProfileDefault = delegator.findOne("PartyProfileDefault", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "productStoreId", productStore.productStoreId), true)?if_exists/>
</#if>

<#if shoppingCart?has_content && shoppingCart.getOrderAttribute("STORE_LOCATION")?has_content && !Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC")>
  <#assign ccAvailable = "false" />
<#else>
  <#assign ccAvailable = "true" />
</#if>
<#assign donePage = "donePage">
<#if !creditCard?exists>
	<#--
	    Sending to validation routine, we want to only show field level messages when adding a "Credit Card"
	    but want to allow teh screen in general to show general messages if you try to continue without choosing
	    a payment method
	-->
<#else>
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
<#assign checkOutStoreCC = Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_STORE_CC")!""/>
<#assign checkOutStoreCCReq = Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_STORE_CC_REQ")!""/>
<#assign checkoutPaymentStyle = Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_PAYMENT_STYLE")!"LIST"/>


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

<#-- LOGIC TO DISPLAY OPTIONS FOR PAY NOW /PAY IN STORE -->
<#if shoppingCart?has_content && shoppingCart.getOrderAttribute("STORE_LOCATION")?has_content>
	<#if !Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC_REQ") && Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_CC")>
   		<#assign showPaymentOption = "true"/>
	<#else>
   		<#assign showPaymentOption = "false" />
	</#if>
<#else>
	<#assign showPaymentOption = "false"/>
</#if>

<#if parameters.paymentOption?exists && parameters.paymentOption?has_content>
     <#assign selectedPaymentOption = parameters.paymentOption!""/>
<#else>
	 <#assign selectedPaymentOption = "PAYOPT_EMPTY">
</#if>     



<div class="${request.getAttribute("attributeClass")!}">
	<div class="displayBox">
		<h3>${uiLabelMap.PaymentInformationHeading}</h3>
		<div>
			<!-- REMAINING PAYMENT SECTION (Will display the balance due) -->
			<div id="js_remainingPayment" class="container currency remainingPayment">
				<h4>${uiLabelMap.RemainingPaymentHeading}</h4>
				<label>${uiLabelMap.RemainingPaymentCaption}</label>
				<#assign remainingPayment = shoppingCart.getGrandTotal().subtract(shoppingCart.getPaymentTotal())! />
				<span><@ofbizCurrency amount=remainingPayment! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
				<input type="hidden" name="remainingPayment" id="js_remainingPaymentValue" value="${remainingPayment}" />
			</div>
			
			<div class="entry payInStore js_paymentOptions" <#if showPaymentOption == "false" || (remainingPayment <= 0)>style="display:none"</#if>>
			  <h4>${uiLabelMap.PaymentOptionsHeading}</h4>
			  <label class="radioOptionLabel"><input type="radio" id="js_payInStoreN" name="payInStore" value="N" <#if (!(parameters.payInStore?exists && parameters.payInStore?string == "Y"))>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.PayNowLabel}</span></label>
			  <label class="radioOptionLabel"><input type="radio" id="js_payInStoreY" name="payInStore" value="Y" <#if ((parameters.payInStore?exists && parameters.payInStore?string == "Y"))>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.PayInStoreLabel}</span></label>
			</div>
			
			<!-- DISPLAY PAYMENT OPTIONS -->
			<div id="js_checkoutPaymentOptions" <#if ccAvailable == "false" || (remainingPayment <= 0)>style="display:none"</#if>>
		 
        	 <#if "DROPDOWN" == checkoutPaymentStyle.toUpperCase()>
        	   <div class="container paymentOptionsDD">
					<div class="entryForm paymentEntry paymentOptions">
						<div class="entry">
							<label>${uiLabelMap.ChoosePaymentOptionLabel}</label>
	                       	<div class="entryField">
	                         	<select id="js_paymentOptionsDD" name="paymentOption">
	                                <option value="PAYOPT_EMPTY" <#if selectedPaymentOption == "PAYOPT_EMPTY"> selected</#if>>${uiLabelMap.SelectOneLabel}</option>
	            			  		<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_CC")>
	                            		<option value="PAYOPT_CC_NEW" <#if selectedPaymentOption == "PAYOPT_CC_NEW"> selected</#if>>${uiLabelMap.PaymentOptionCreditCardLabel}</option>
	                            	</#if>
									<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
										<#-- IF WE ARE SAVING CC INFO -->
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
	                            		    <option value="PAYOPT_CC_EXIST" <#if selectedPaymentOption == "PAYOPT_CC_EXIST"> selected</#if>>${uiLabelMap.PaymentOptionSavedCreditCardLabel}</option>
				    		       		</#if>
				    		       </#if>
	          			      	   <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EFT")>
	                            		<option value="PAYOPT_EFT_NEW" <#if selectedPaymentOption == "PAYOPT_EFT_NEW"> selected</#if>>${uiLabelMap.PaymentOptionEftAccountLabel}</option>
	                               </#if>
					               <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
										<#-- IF WE ARE SAVING EFT INFO -->
							            <#if savedPaymentMethodEftValueMaps?has_content>
							               	<#list savedPaymentMethodEftValueMaps as savedPaymentMethodEftValueMap>
							                 	<#assign savedPaymentMethod =savedPaymentMethodEftValueMap.paymentMethod/>
							                 	<#if "EFT_ACCOUNT" == savedPaymentMethod.paymentMethodTypeId>
							                     	<#assign hasSavedEftAccount = "Y"/>
							                     	<#break>
							                 	</#if>
							               	</#list>
							          	</#if>
					             		<#if hasSavedEftAccount?has_content>
	                            		    <option value="PAYOPT_EFT_EXIST" <#if selectedPaymentOption == "PAYOPT_EFT_EXIST"> selected</#if>>${uiLabelMap.PaymentOptionSavedEftAccountLabel}</option>
	                            		</#if>
	                               </#if>
						           <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_PAYPAL")>
	                            		<option value="PAYOPT_PAYPAL" <#if selectedPaymentOption == "PAYOPT_PAYPAL"> selected</#if>>${uiLabelMap.PaymentOptionPayPalLabel}</option>
	                               </#if>
						           <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EBS")>
	                            		<option value="PAYOPT_EBS" <#if selectedPaymentOption == "PAYOPT_EBS"> selected</#if>>${uiLabelMap.PaymentOptionEbsLabel}</option>
	                               </#if>
						           <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_PAYNETZ")>
	                            		<option value="PAYOPT_PAYNETZ" <#if selectedPaymentOption == "PAYOPT_PAYNETZ"> selected</#if>>${uiLabelMap.PaymentOptionPayNetzLabel}</option>
	                               </#if>
	                               <#if (Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD"))>					           
	                            		<option value="PAYOPT_COD" <#if selectedPaymentOption == "PAYOPT_COD"> selected</#if>>${uiLabelMap.PaymentOptionCODLabel}</option>
	                               </#if>
								</select>
	                        </div>
						</div>
				    </div>

                        <!--CC -->
       			  		<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_CC")>
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry creditCardEntry")}
						    ${setRequestAttribute("attributeStyle", "display:none;")}
						    ${setRequestAttribute("attributeId", "PAYOPT_CC_NEW")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionCreditCardNew")}
	
	                        <!--EXISTING CC -->
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry creditCardExistEntry")}
						    ${setRequestAttribute("attributeStyle", "display:none;")}
						    ${setRequestAttribute("attributeId", "PAYOPT_CC_EXIST")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionCreditCardExist")}
						</#if>

                        <!--EFT -->
    			      	<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EFT")>
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry eftEntry")}
						    ${setRequestAttribute("attributeStyle", "display:none;")}
						    ${setRequestAttribute("attributeId", "PAYOPT_EFT_NEW")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionEftNew")}
	                        <!--EXISTING EFT -->
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry eftExistEntry")}
						    ${setRequestAttribute("attributeStyle", "display:none;")}
						    ${setRequestAttribute("attributeId", "PAYOPT_EFT_EXIST")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionEftExist")}
						</#if>
                        <!--PAYPAL -->
					    ${setRequestAttribute("attributeClass", "entryForm paymentEntry paypalEntry")}
					    ${setRequestAttribute("attributeStyle", "display:none;")}
					    ${setRequestAttribute("attributeId", "PAYOPT_PAYPAL")}
					    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionPayPal")}
                        <!--PAYNETZ -->
			            <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_PAYNETZ")>
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry payNetzEntry")}
						    ${setRequestAttribute("attributeStyle", "display:none;")}
						    ${setRequestAttribute("attributeId", "PAYOPT_PAYNETZ")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionPayNetz")}
					    </#if>
                        <!--EBS -->
			            <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EBS")>
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry ebsEntry")}
						    ${setRequestAttribute("attributeStyle", "display:none;")}
						    ${setRequestAttribute("attributeId", "PAYOPT_EBS")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionEbs")}
					    </#if>
					    	  
        	 <#else>
					<#-- DETERMINE THE SELECTED PAYMENT METHOD RADIO BUTTON ON LOAD OF THE SCREEN -->
					<#if (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_CC_EXIST")>
						<#assign selectedPaymentOption = "PAYOPT_CC_EXIST">
					<#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_CC_NEW")>
						<#assign selectedPaymentOption = "PAYOPT_CC_NEW">
					<#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_EFT_EXIST")>
						<#assign selectedPaymentOption = "PAYOPT_EFT_EXIST">
					<#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_EFT_NEW")>
						<#assign selectedPaymentOption = "PAYOPT_EFT_NEW">
					<#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_COD")>
						<#assign selectedPaymentOption = "PAYOPT_COD">
					<#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_EBS")>
						<#assign selectedPaymentOption = "PAYOPT_EBS">
					<#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_PAYPAL") || (parameters.token?exists && parameters.token?has_content)>
						<#assign selectedPaymentOption = "PAYOPT_PAYPAL">
					<#elseif (parameters.paymentOption?exists && parameters.paymentOption?string == "PAYOPT_PAYNETZ")>
						<#assign selectedPaymentOption = "PAYOPT_PAYNETZ">
					<#elseif Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_CC")>
						<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && savedPaymentMethodValueMaps?has_content && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
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
					<#elseif Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EFT")>
						<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && savedPaymentMethodEftValueMaps?has_content && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
							<#list savedPaymentMethodEftValueMaps as savedPaymentMethodEftValueMap>
								<#assign savedPaymentMethod =savedPaymentMethodEftValueMap.paymentMethod/>
								<#if "EFT_ACCOUNT" == savedPaymentMethod.paymentMethodTypeId>
									<#assign hasSavedEftAccount = "Y"/>
									<#break>
								</#if>
							</#list>
						</#if>
						<#if hasSavedEftAccount?has_content>
							<#assign selectedPaymentOption = "PAYOPT_EFT_EXIST">
						<#else>
							<#assign selectedPaymentOption = "PAYOPT_EFT_NEW">
						</#if>
					<#else>
						<#assign selectedPaymentOption = "PAYOPT_EMPTY">
					</#if>
						<#-- EBS OPTION -->
					<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EBS")>
						<div class="container ebs">
							<h4>${uiLabelMap.EBSHeading}</h4>
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry ebsEntry")}
						    ${setRequestAttribute("attributeId", "PAYOPT_EBS")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionEbs")}
						</div>
					</#if>
			
					<#-- ATOM PAYNETZ OPTION -->
					<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_PAYNETZ")>
						<div class="container payNetz">
							<h4>${uiLabelMap.PayNetzHeading}</h4>
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry payNetzEntry")}
						    ${setRequestAttribute("attributeId", "PAYOPT_PAYNETZ")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionPayNetz")}
						</div>
					</#if>
			    	
					<#-- PAYPAL -->
					<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_PAYPAL")>
						<div class="container paypal">
							<h4>${uiLabelMap.PayPalHeading}</h4>
						    ${setRequestAttribute("attributeClass", "entryForm paymentEntry paypalEntry")}
						    ${setRequestAttribute("attributeId", "PAYOPT_PAYPAL")}
						    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionPayPal")}
						</div>
					</#if>
	
			  		<#-- CREDIT CARD -->
			  		<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_CC")>
						<#-- CHECK WE ARE SAVING CC INFO -->
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
						</#if>
						<div class="container creditCard js_creditCardEntry">
							<h4>${uiLabelMap.CreditCardHeading}</h4>
								<#-- IF WE ARE SAVING CC INFO -->
		    		       		<#if hasSavedCard?has_content>
			                        <!--EXISTING CC -->
								    ${setRequestAttribute("attributeClass", "entryForm paymentEntry creditCardExistEntry")}
								    ${setRequestAttribute("attributeId", "PAYOPT_CC_EXIST")}
								    ${setRequestAttribute("includeRadioOption", "Y")}
								    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionCreditCardExist")}
				                </#if>
		                        <!--CC -->
							    ${setRequestAttribute("attributeClass", "entryForm paymentEntry creditCardEntry")}
							    ${setRequestAttribute("attributeId", "PAYOPT_CC_NEW")}
							    ${setRequestAttribute("includeRadioOption", "Y")}
							    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionCreditCardNew")}
						</div>
					</#if>
					<#-- END CREDIT CARD -->

			     	<#-- EFT -->
			      	<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_EFT")>
			            <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
				            <#if savedPaymentMethodEftValueMaps?has_content>
				               	<#list savedPaymentMethodEftValueMaps as savedPaymentMethodEftValueMap>
				                 	<#assign savedPaymentMethod =savedPaymentMethodEftValueMap.paymentMethod/>
				                 	<#if "EFT_ACCOUNT" == savedPaymentMethod.paymentMethodTypeId>
				                     	<#assign hasSavedEftAccount = "Y"/>
				                     	<#break>
				                 	</#if>
				               	</#list>
				          	</#if>
				         </#if>

			      		<div class="container eftAccount js_eftAccountEntry">
				      		<h4>${uiLabelMap.EftAccountHeading}</h4>
				             	<#if hasSavedEftAccount?has_content>
			                        <!--EXISTING EFT -->
								    ${setRequestAttribute("attributeClass", "entryForm paymentEntry eftExistEntry")}
								    ${setRequestAttribute("includeRadioOption", "Y")}
								    ${setRequestAttribute("attributeId", "PAYOPT_EFT_EXIST")}
								    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionEftExist")}

				        		</#if>
		                        <!--EFT -->
							    ${setRequestAttribute("attributeClass", "entryForm paymentEntry eftEntry")}
							    ${setRequestAttribute("includeRadioOption", "Y")}
							    ${setRequestAttribute("attributeId", "PAYOPT_EFT_NEW")}
							    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionEftNew")}
			      		</div>
			  		</#if>
			  		<#-- END EFT -->
			
			      	<#-- COD-->
			      	<div class="container paymentOption js_codOptions" <#if (shoppingCart?has_content && shoppingCart.getOrderAttribute("STORE_LOCATION")?has_content) || !(Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_ALLOW_COD"))>style="display:none"</#if>>
			       		<h4>${uiLabelMap.CODHeading}</h4>
					    ${setRequestAttribute("attributeClass", "entryForm paymentEntry codEntry")}
					    ${setRequestAttribute("includeRadioOption", "Y")}
					    ${setRequestAttribute("attributeId", "PAYOPT_COD")}
					    ${screens.render("component://osafe/widget/EntryScreens.xml#paymentOptionCod")}
			      	</div>
			      	<#-- END COD-->
        	 </#if>
		 	</div> <!-- End of checkoutPaymentOptions DIV -->
			        
		</div>
		<a name="paymentMethod"></a>
	</div>
 </div>
