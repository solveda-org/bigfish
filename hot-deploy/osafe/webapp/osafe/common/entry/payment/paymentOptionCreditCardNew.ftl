<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign attributeClass = request.getAttribute("attributeClass")!""/>
<#assign attributeStyle = request.getAttribute("attributeStyle")!""/>
<#assign attributeId = request.getAttribute("attributeId")!""/>
<#assign includeRadioOption = request.getAttribute("includeRadioOption")!"N"/>
<#if parameters.paymentOption?exists && parameters.paymentOption?has_content>
     <#assign selectedPaymentOption = parameters.paymentOption!""/>
<#else>
     <#assign selectedPaymentOption = "PAYOPT_CC_NEW">
</#if>

<div <#if attributeId?has_content> id="${attributeId!}"</#if><#if attributeClass?has_content> class="${attributeClass!}"</#if><#if attributeStyle?has_content> style="${attributeStyle!}"</#if> >
	<#if includeRadioOption == "Y">
        <div class="entry">
            <label class="radioOptionLabel"><input type="radio" id="js_useOtherCard" name="paymentOption" value="PAYOPT_CC_NEW" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_CC_NEW")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.PayWithAnotherCardLabel}</span></label>
        </div>
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
		    <#assign ccNumber=""/>
			<#if creditCard?has_content && creditCard.cardNumber?has_content>
		         <#assign ccNumber=creditCard.cardNumber/>
			</#if>
			<input type="text" class="cardNumber" maxlength="30" id="js_cardNumber"  name="cardNumber" value="${ccNumber!requestParameters.cardNumber!""}"/>
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
