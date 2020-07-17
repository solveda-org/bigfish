<div class ="container addressItems multiAddressAddressItems">
<#if multiAddressList?exists && multiAddressList?has_content>
   <input type="hidden" name="itemTotalQuantity" value="${itemTotalQuantity!0}"/>
   <input type="hidden" name="numberOfItems" value="${numberOfItems!0}"/>
   <div class="boxList cartList">
  	 <#assign lineIndex=0?number/>
  	 <#assign rowClass = "1">
     <#list multiAddressList as cartLine>
	      ${setRequestAttribute("cartLine", cartLine)}
		  ${setRequestAttribute("lineIndex", lineIndex)}
          ${setRequestAttribute("rowClass", rowClass)}
          ${setRequestAttribute("lineIndexGiftMessageMap", lineIndexGiftMessageMap)}
          ${setRequestAttribute("lineIndexCartItemIndexMap", lineIndexCartItemIndexMap)}
	      ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#shipMultiAddressCartItem")}
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
        <#assign lineIndex= lineIndex + 1/>
	 </#list>
   </div>
</#if>
</div>

<#assign localPrevButtonVisible = prevButtonVisible!"Y">
<#assign localPrevButtonUrl = prevButtonUrl!"javascript:history.go(-1)">
<#assign localPrevButtonClass = prevButtonClass!"standardBtn negative">
<#assign localPrevButtonDescription = prevButtonDescription!uiLabelMap.PreviousBtn>
<#if localPrevButtonVisible == "Y">
  <div class="action previousButton multiAddressPreviousButton">
    <a href="${localPrevButtonUrl}" class="${localPrevButtonClass}"><span>${localPrevButtonDescription}</span></a>
  </div>
</#if>
<div class="action addressButton multiAddressAddressButton">
    <a href="<@ofbizUrl>${addAddressAction!""}?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION</@ofbizUrl>" class="standardBtn action"><span>${uiLabelMap.AddAddressBtn}</span></a>
</div>
<#assign localNextButtonVisible = nextButtonVisible!"Y">
<#assign localNextButtonClass = nextButtonClass!"standardBtn positive">
<#assign localNextButtonDescription = nextButtonDescription!uiLabelMap.ContinueBtn>
<#if localNextButtonVisible == "Y">
	<div class="action continueButton multiAddressContinueButton">
	    <a href="javascript:document.${formName}.submit();" class="${localNextButtonClass}"><span>${localNextButtonDescription}</span></a>
	</div>
</#if>	

