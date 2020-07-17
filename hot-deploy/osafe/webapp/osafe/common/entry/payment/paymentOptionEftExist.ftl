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
			<label class="radioOptionLabel"><input type="radio" id="js_useSavedEftAccount" name="paymentOption" value="PAYOPT_EFT_EXIST" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_EFT_EXIST")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.UseSavedEftAccountLabel}</span></label>
		</div>
	</#if>
   	<div class="entry savedEftAccount">
		<label for="savedEftAccount">${uiLabelMap.SelectSavedEftAccountCaption}</label>
		<div class="entryField">
 			<select id="js_savedEftAccount" name="savedEftAccount" class="savedEftAccount">
   				<option value="">${uiLabelMap.CommonSelectOne}</option>
     			<#assign alreadyShownSavedEFTList = Static["javolution.util.FastList"].newInstance()/>
     			<#if savedPaymentMethodEftValueMaps?has_content>
        			<#list savedPaymentMethodEftValueMaps as savedPaymentMethodEftValueMap>
           				<#assign savedPaymentMethod = savedPaymentMethodEftValueMap.paymentMethod?if_exists/>
           				<#assign savedEftAccount = savedPaymentMethodEftValueMap.eftAccount?if_exists/>
           				<#if ("EFT_ACCOUNT" == savedPaymentMethod.paymentMethodTypeId) && (savedEftAccount?has_content)>
             				<#assign paymentMethodId = savedEftAccount.paymentMethodId?if_exists/>
             				<#assign bankName = savedEftAccount.bankName?if_exists/>
             				<#assign nameOnAccount = savedEftAccount.nameOnAccount?if_exists/>
             				<#assign accountType = savedEftAccount.accountType?if_exists/>
             				<#assign accountNumber = savedEftAccount.accountNumber?if_exists/>
             				<#if !alreadyShownSavedEFTList.contains(accountNumber+bankName)>
              					<#if partyProfileDefault?exists >
                					<#assign partyProfileDefaultPayMeth = partyProfileDefault.defaultPayMeth!"" />
              					</#if>
              					<#assign selectedSavedEFT = parameters.savedEFT!partyProfileDefaultPayMeth!""/>
              					<option value="${savedPaymentMethod.paymentMethodId}" <#if (selectedSavedEFT == savedPaymentMethod.paymentMethodId)>selected=selected</#if>>
                					${bankName?if_exists},
                					${nameOnAccount?if_exists},
                					${accountType?if_exists}
              					</option>
              					<#assign changed = alreadyShownSavedEFTList.add(accountNumber+bankName)/>
             				</#if>
           				</#if>
         			</#list>
      			</#if>
 			</select>
 			<@fieldErrors fieldName="savedEftAccount"/>
		</div>
	</div>

    <div class="entry content">
      	<label for="content">&nbsp;</label>
      	<span>${screens.render("component://osafe/widget/EcommerceContentScreens.xml#CHECKOUT_EFT_GUIDE")}</span>
    </div>
</div>