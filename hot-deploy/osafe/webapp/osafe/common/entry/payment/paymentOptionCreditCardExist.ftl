<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign attributeClass = request.getAttribute("attributeClass")!""/>
<#assign attributeStyle = request.getAttribute("attributeStyle")!""/>
<#assign attributeId = request.getAttribute("attributeId")!""/>
<#assign includeRadioOption = request.getAttribute("includeRadioOption")!"N"/>
<#if parameters.paymentOption?exists && parameters.paymentOption?has_content>
     <#assign selectedPaymentOption = parameters.paymentOption!""/>
</#if>

<div <#if attributeId?has_content> id="${attributeId!}"</#if><#if attributeClass?has_content> class="${attributeClass!}"</#if><#if attributeStyle?has_content> style="${attributeStyle!}"</#if> >
	<#if includeRadioOption == "Y">
        <div class="entry">
          <label class="radioOptionLabel"><input type="radio" id="js_useSavedCard" name="paymentOption" value="PAYOPT_CC_EXIST" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_CC_EXIST")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.UseSavedCardLabel}</span></label>
        </div>
	</#if>
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
</div>
