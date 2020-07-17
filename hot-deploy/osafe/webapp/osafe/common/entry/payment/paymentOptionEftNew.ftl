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
	            <label class="radioOptionLabel">
	                <input type="radio" id="js_useOtherEftAccount" name="paymentOption" value="PAYOPT_EFT_NEW" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_EFT_NEW")>checked="checked"</#if>/>
	                <span class="radioOptionText">
	                    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
	                        ${uiLabelMap.PayWithAnotherEftAccountLabel}
	                    <#else>
	                        ${uiLabelMap.EftAccountLabel}
	                    </#if>
	                </span>
	            </label>
	        </div>
		</#if>
	    <div class="entry nameOnAccount">
	      	<label for="nameOnAccount"><@required/>${uiLabelMap.NameOnAccountCaption}</label>
	      	<div class="entryField">
	          	<input type="text" class="nameOnAccount" maxlength="100" id="js_nameOnAccount" name="nameOnAccount" value="${requestParameters.nameOnAccount!""}"/>
	          	<@fieldErrors fieldName="nameOnAccount"/>
	      	</div>
	    </div>
	
	    <div class="entry bankName">
	      	<label for="bankName"><@required/>${uiLabelMap.BankNameCaption}</label>
	      	<div class="entryField">
	          	<input type="text" class="bankName" maxlength="100" id="js_bankName" name="bankName" value="${requestParameters.bankName!""}"/>
	          	<@fieldErrors fieldName="bankName"/>
	      	</div>
	    </div>
	
	    <div class="entry routingNumber">
	      	<label for="routingNumber"><@required/>${uiLabelMap.RoutingNumberCaption}</label>
	      	<div class="entryField">
	          	<input type="text" class="routingNumber" maxlength="60" id="js_routingNumber" name="routingNumber" value="${requestParameters.routingNumber!""}"/>
	          	<@fieldErrors fieldName="routingNumber"/>
	      	</div>
	    </div>
	
	    <div class="entry accountNumber">
	      	<label for="accountNumber"><@required/>${uiLabelMap.AccountNumberCaption}</label>
	      	<div class="entryField">
	          	<input type="text" class="accountNumber" maxlength="255" id="js_accountNumber"  name="accountNumber" value="${requestParameters.accountNumber!""}"/>
	          	<@fieldErrors fieldName="accountNumber"/>
	      	</div>
	    </div>
	
	    <div class="entry accountType">
	      	<label for="accountType"><@required/>${uiLabelMap.AccountTypeCaption}</label>
	      	<div class="entryField">
	          	<select id="js_accountType" name="accountType" class="accountType">
	            	<option value="checking" <#if (("checking" == requestParameters.accountType!"") || !(requestParameters.accountType?has_content))>selected=selected</#if>>${uiLabelMap.AccountTypeCheckingLabel}</option>
	            	<option value="saving" <#if ("saving" == requestParameters.accountType!"")>selected=selected</#if>>${uiLabelMap.AccountTypeSavingLabel}</option>
	          	</select>
	          	<@fieldErrors fieldName="accountType"/>
	      	</div>
	    </div>
	
		<div class="entry content">
			<label for="content">&nbsp;</label>
			<span>${screens.render("component://osafe/widget/EcommerceContentScreens.xml#CHECKOUT_EFT_GUIDE")}</span>
		</div>
</div>
