<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<input type="hidden" name="DONE_PAGE" value="${requestParameters.DONE_PAGE!}"/>
<#if requestParameters.shoppingListId?exists && requestParameters.shoppingListId?has_content>
	<input type="hidden" name="shoppingListId" value="${requestParameters.shoppingListId!}"/>
</#if>
<#assign paymentMethodId= parameters.paymentMethodId!""/>
<#assign mode= parameters.mode!""/>
<#assign cardType= ""/>
<#assign cardNumber= ""/>
<#assign firstNameOnCard= ""/>
<#assign setAsDefaultCard= parameters.setAsDefaultCard?if_exists/>
<#if paymentMethodId?has_content>
    <#assign creditCard = delegator.findOne("CreditCard", Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodId", paymentMethodId), true)/>
    <#if creditCard?has_content>
      <#assign mode= "edit"/>
      <#assign cardType= creditCard.cardType!""/>
      <#assign cardNumber= creditCard.cardNumber!""/>
      <#assign firstNameOnCard= creditCard.firstNameOnCard!""/>
      <#assign lastNameOnCard= creditCard.lastNameOnCard!""/>
      <#assign postalAddressData = creditCard.getRelatedOne("PostalAddress")!"" />
      <#if postalAddressData?has_content>
         <#assign postalAddressContactMechId = postalAddressData.contactMechId!"" />
      </#if>
      <input type="hidden" name="mode" value="${mode!""}"/>
    </#if>
</#if>
<input type="hidden" name="paymentMethodId" value="${paymentMethodId!""}"/>
<#if paymentMethodId?has_content && userLogin?has_content && mode == "edit">
    <#assign partyId = userLogin.partyId!"">
    <#assign partyProfileDefault = delegator.findOne("PartyProfileDefault", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "productStoreId", productStore.productStoreId), true)?if_exists/>
    <#if partyProfileDefault?has_content>
    	<#if partyProfileDefault.defaultPayMeth?has_content && (partyProfileDefault.defaultPayMeth == paymentMethodId)>
        	<#assign setAsDefaultCard = "Y" />
        </#if>
    </#if>
</#if>
<div id="creditCardInfo" class="displayBox">
<h3><#if mode == "edit">${uiLabelMap.CreditCardInfoHeading}<#else>${uiLabelMap.AddCardInfoHeading}</#if></h3>
   <div class="entryForm creditCardEntry">
    <div class="entry firstName">
      <label for="firstNameOnCard"><@required/>${uiLabelMap.FirstNameOnCardCaption}</label>
      <div class="entryField">
	      <input type="text" class="firstNameOnCard" maxlength="30" id="firstNameOnCard" name="firstNameOnCard" value="${requestParameters.firstNameOnCard!firstNameOnCard!""}"/>
	      <@fieldErrors fieldName="firstNameOnCard"/>
      </div>
    </div>
    <div class="entry lastName">
      <label for="lastNameOnCard"><@required/>${uiLabelMap.LastNameOnCardCaption}</label>
      <div class="entryField">
	      <input type="text" class="lastNameOnCard" maxlength="30" id="lastNameOnCard" name="lastNameOnCard" value="${requestParameters.lastNameOnCard!lastNameOnCard!""}"/>
	      <@fieldErrors fieldName="lastNameOnCard"/>
      </div>
    </div>
    <div class="entry cardType">
      <label for="cardType"><@required/>${uiLabelMap.CardTypeCaption}</label>
      <div class="entryField">
	      <#if mode == "edit">
	          <input type="text" readonly="readonly" class="cardType" id="cardType"  name="cardType" value="${parameters.cardType!cardType!""}"/>
	      <#else>
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
	      </#if>
	      <@fieldErrors fieldName="cardType"/>
      </div>
    </div>
    <div class="entry cardNumber">
      <label for="cardNumber"><@required/>${uiLabelMap.CardNumberCaption}</label>
      <div class="entryField">
	      <#if mode == "edit">
	          <#assign cardNumberDisplay = "">
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
	          <input type="text" readonly="readonly" class="cardNumber" maxlength="30" id="cardNumber"  name="cardNumberDisplay" value="${cardNumberDisplay!""}"/>
	          <input type="hidden"  id="cardNumber"  name="cardNumber" value="${cardNumberDisplay!""}"/>
	      <#else>
	          <input type="text" class="cardNumber" maxlength="30" id="cardNumber"  name="cardNumber" value="${requestParameters.cardNumber!cardNumber!""}"/>
	      </#if>
	      <@fieldErrors fieldName="cardNumber"/>
      </div>
    </div>
    <div class="entry expDate">
      <#assign expMonth = "">
      <#assign expYear = "">
      <#if creditCard?exists && creditCard.expireDate?exists>
        <#assign expDate = creditCard.expireDate>
        <#if (expDate?exists && expDate.indexOf("/") > 0)>
            <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
            <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
        </#if>
      </#if>
      <label for="expMonth"><@required/>${uiLabelMap.ExpirationMonthCaption}</label>
      <div class="entryField">
	      <#assign ccExprMonth = parameters.expMonth!expMonth!"">
	      <#if ccExprMonth?has_content>
	          <select id="expMonth" name="expMonth" class="expMonth">
	            <option value="${parameters.expMonth!ccExprMonth}">${parameters.expMonth!ccExprMonth!""}</option>
	            <option value="">${uiLabelMap.CommonSelectOne}</option>
	            ${screens.render("component://osafe/widget/CommonScreens.xml#ccMonths")}
	          </select>
	      <#else>
	          <select id="expMonth" name="expMonth" class="expMonth">
	            <option value="">${uiLabelMap.CommonSelectOne}</option>
	            ${screens.render("component://osafe/widget/CommonScreens.xml#ccMonths")}
	          </select>
	      </#if>   
	      <@fieldErrors fieldName="expMonth"/>
      </div>
    </div>
    <div class="entry expDate">
      <label for="expYear"><@required/>${uiLabelMap.ExpirationYearCaption}</label>
      <div class="entryField">
	      <#assign ccExprYear = parameters.expYear!expYear!"">
	      <#if ccExprYear?has_content>
	          <select id="expYear" name="expYear" class="expYear">
	            <option value="${parameters.expYear!ccExprYear}">${parameters.expYear!ccExprYear!""}</option>
	            <option value="">${uiLabelMap.CommonSelectOne}</option>
	            ${screens.render("component://osafe/widget/CommonScreens.xml#ccYears")}
	          </select>
	      <#else>
	          <select id="expYear" name="expYear" class="expYear">
	            <option value="">${uiLabelMap.CommonSelectOne}</option>
	            ${screens.render("component://osafe/widget/CommonScreens.xml#ccYears")}
	          </select>
	      </#if>
	      <@fieldErrors fieldName="expYear"/>
      </div>
    </div>
    <div class="entry defaultSelection">
      <label for="setAsDefaultCard">${uiLabelMap.SetAsDefaultCaption}</label>
      <label class="radioOptionLabel"><input type="radio" id="setAsDefaultCard" name="setAsDefaultCard" value="Y" <#if ((setAsDefaultCard?exists && setAsDefaultCard?string == "Y"))>checked="checked"</#if>/><span>${uiLabelMap.YesLabel}</span></label>
      <label class="radioOptionLabel"><input type="radio" id="setAsDefaultCard" name="setAsDefaultCard" value="N" <#if ((setAsDefaultCard?exists && setAsDefaultCard?string == "N")|| !(setAsDefaultCard?has_content))>checked="checked"</#if>/><span>${uiLabelMap.NoLabel}</span></label>
    </div>

    <#assign contactMechList = context.get(fieldPurpose+"ContactMechList") />
    <#if contactMechList?has_content>
	    ${setRequestAttribute("DISPLAY_FORMAT", addressSelectionDisplayFormat!)}
	    ${setRequestAttribute("contactMechList", contactMechList!)}
	    ${setRequestAttribute("addressFieldPurpose",fieldPurpose!)}
	    ${setRequestAttribute("addressSelectionInputName",addressSelectionInputName!)}
	    ${setRequestAttribute("addressSelectionCaption",addressSelectionCaption!)}
	    ${setRequestAttribute("addAddressAction",addAddressAction!)}
	    ${setRequestAttribute("paymentMethodId",paymentMethodId!)}
	    ${setRequestAttribute("selectedContactMechId",postalAddressContactMechId!)}
	    ${screens.render("component://osafe/widget/CommonScreens.xml#postalAddressSelection")}
    </#if>

  </div>
</div>
