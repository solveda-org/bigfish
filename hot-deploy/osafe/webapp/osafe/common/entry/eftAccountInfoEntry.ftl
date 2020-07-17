<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<input type="hidden" name="DONE_PAGE" value="${requestParameters.DONE_PAGE!}"/>
<#if requestParameters.shoppingListId?exists && requestParameters.shoppingListId?has_content>
	<input type="hidden" name="shoppingListId" value="${requestParameters.shoppingListId!}"/>
</#if>
<#assign paymentMethodId= parameters.paymentMethodId!""/>
<#assign mode= parameters.mode!""/>
<#assign accountName= ""/>
<#assign bankName= ""/>
<#assign routingNumber= ""/>
<#assign accountNumber= ""/>
<#assign accountType= "checking"/>

<#assign setAsDefaultAccount= parameters.setAsDefaultAccount?if_exists/>
<#if paymentMethodId?has_content>
    <#assign eftAccount = delegator.findOne("EftAccount", Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodId", paymentMethodId), true)/>
    <#if eftAccount?has_content>
       <#assign mode= "edit"/>
	   <#assign nameOnAccount = eftAccount.nameOnAccount!""/>
	   <#assign bankName = eftAccount.bankName!""/>
	   <#assign routingNumber = eftAccount.routingNumber!""/>
	   <#assign accountNumber = eftAccount.accountNumber!""/>
	   <#assign accountType = eftAccount.accountType!""/>
       <#assign postalAddressData = eftAccount.getRelatedOne("PostalAddress")!"" />
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
        	<#assign setAsDefaultAccount = "Y" />
        </#if>
    </#if>
</#if>
<div id="eftAccountInfo" class="displayBox">
<h3><#if mode == "edit">${uiLabelMap.EftAccountInfoHeading}<#else>${uiLabelMap.AddEftInfoHeading}</#if></h3>
  <div class="entryForm eftAccountEntry">
    <div class="entry nameOnAccount">
      <label for="nameOnAccount"><@required/>${uiLabelMap.NameOnAccountCaption}</label>
      <div class="entryField">
	      <input type="text" class="nameOnAccount" maxlength="100" id="nameOnAccount" name="nameOnAccount" value="${requestParameters.nameOnAccount!nameOnAccount!""}"/>
	      <@fieldErrors fieldName="nameOnAccount"/>
      </div>
    </div>
    
    <div class="entry bankName">
      <label for="bankName"><@required/>${uiLabelMap.BankNameCaption}</label>
      <div class="entryField">
          <input type="text" class="bankName" maxlength="100" id="bankName" name="bankName" value="${requestParameters.bankName!bankName!""}"/>
	      <@fieldErrors fieldName="bankName"/>
      </div>
    </div>
    
    <div class="entry routingNumber">
      <label for="routingNumber"><@required/>${uiLabelMap.RoutingNumberCaption}</label>
      <div class="entryField">
          <input type="text" class="routingNumber" maxlength="60" id="routingNumber" name="routingNumber" value="${requestParameters.routingNumber!routingNumber!""}"/>
	      <@fieldErrors fieldName="routingNumber"/>
      </div>
    </div>
    
    <div class="entry accountNumber">
      <label for="accountNumber"><@required/>${uiLabelMap.AccountNumberCaption}</label>
      <div class="entryField">
	      <#if mode == "edit">
	          <#assign accountNumberDisplay = "">
	          <#assign size = accountNumber?length - 4>
	          <#if (size > 0)>
	            <#list 0 .. size-1 as charno>
	               <#assign accountNumberDisplay = accountNumberDisplay + "*">
	            </#list>
	            <#assign accountNumberDisplay = accountNumberDisplay + "-">
	            <#assign accountNumberDisplay = accountNumberDisplay + accountNumber[size .. size + 3]>
	          <#else>
	            <#assign accountNumberDisplay = accountNumber>
	          </#if>
	          <input type="text" readonly="readonly" class="accountNumber" maxlength="30" id="accountNumber"  name="accountNumberDisplay" value="${accountNumberDisplay!""}"/>
	          <input type="hidden"  id="accountNumber"  name="accountNumber" value="${accountNumberDisplay!""}"/>
	      <#else>
	          <input type="text" class="accountNumber" maxlength="255" id="accountNumber"  name="accountNumber" value="${requestParameters.accountNumber!accountNumber!""}"/>
	      </#if>
	      <@fieldErrors fieldName="accountNumber"/>
      </div>
    </div>
    
    <div class="entry accountType">
      <label for="accountType"><@required/>${uiLabelMap.AccountTypeCaption}</label>
      <div class="entryField">
          <select id="js_accountType" name="accountType" class="accountType">
              <option value="checking" <#if ("checking" == requestParameters.accountType!accountType!"")>selected=selected</#if>>${uiLabelMap.AccountTypeCheckingLabel}</option>
              <option value="saving" <#if ("saving" == requestParameters.accountType!accountType!"")>selected=selected</#if>>${uiLabelMap.AccountTypeSavingLabel}</option>
          </select>
          <@fieldErrors fieldName="accountType"/>
      </div>
    </div>
    
    <div class="entry defaultSelection">
      <label for="setAsDefaultAccount">${uiLabelMap.SetAsDefaultCaption}</label>
      <label class="radioOptionLabel"><input type="radio" id="setAsDefaultAccount" name="setAsDefaultAccount" value="Y" <#if ((setAsDefaultAccount?exists && setAsDefaultAccount?string == "Y"))>checked="checked"</#if>/><span>${uiLabelMap.YesLabel}</span></label>
      <label class="radioOptionLabel"><input type="radio" id="setAsDefaultAccount" name="setAsDefaultAccount" value="N" <#if ((setAsDefaultAccount?exists && setAsDefaultAccount?string == "N")|| !(setAsDefaultAccount?has_content))>checked="checked"</#if>/><span>${uiLabelMap.NoLabel}</span></label>
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
