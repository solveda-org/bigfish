<div class ="container shippingGroup multiShipOptionsShippingGroup">
<#if shoppingCart?exists && shoppingCart?has_content>
   <#assign groupIndex=1?number/>
   <#assign shipGroupIndex=0?int/>
  <div class="displayBox">
	   <#list shoppingCart.getShipGroups() as cartShipInfo>
	      <h4>${uiLabelMap.ShippingGroupHeading} ${groupIndex} of ${shoppingCart.getShipGroupSize()}</h4>
	      <#assign contactMech = delegator.findByPrimaryKeyCache("ContactMech", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", cartShipInfo.contactMechId))?if_exists />
	      <#if contactMech?has_content>
	          <#assign postalAddress = contactMech.getRelatedOneCache("PostalAddress")?if_exists />
			    ${setRequestAttribute("PostalAddress", postalAddress)}
			    ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_FULL_ADDRESS")}
			    ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
	      </#if>
	
		   <div class="boxList cartList">
		  	 <#assign lineIndex=0?number/>
		  	 <#assign rowClass = "1">
	          <div class="boxListItemTabular shipItem multiShipOptionsShipItem">
                <div class="multiAddressItems grouping grouping1">
		           <#list cartShipInfo.getShipItems() as cartLine>
                    <div class="multiAddressItems groupRow">
				      ${setRequestAttribute("cartLine", cartLine)}
					  ${setRequestAttribute("lineIndex", lineIndex)}
			          ${setRequestAttribute("rowClass", rowClass)}
				      ${setRequestAttribute("cartShipInfo", cartShipInfo)}
				      ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#shipMultiOptionsCartItem")}
				        <#if rowClass == "2">
				            <#assign rowClass = "1">
				        <#else>
				            <#assign rowClass = "2">
				        </#if>
				        <#assign lineIndex= lineIndex + 1/>
				     </div>
			       </#list>
			   </div>
	 		   <#assign lineIndex=0?number/>
	           <div class="multiShipOptionsOptions grouping grouping2">
						  ${setRequestAttribute("lineIndex", lineIndex)}
				          ${setRequestAttribute("cartShipInfo", cartShipInfo)}
				          ${setRequestAttribute("shipGroupIndex", shipGroupIndex)}
					      ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#shipMultiOptionsShipGroupItem")}
			    </div>
			  </div>
		   </div>
	     <#assign shipGroupIndex= shipGroupIndex + 1/>
	     <#assign groupIndex= groupIndex + 1/>
	   </#list>
  </div>
</#if>
</div>
<#assign localPrevButtonVisible = prevButtonVisible!"Y">
<#assign localPrevButtonUrl = prevButtonUrl!"javascript:history.go(-1)">
<#assign localPrevButtonClass = prevButtonClass!"standardBtn negative">
<#assign localPrevButtonDescription = prevButtonDescription!uiLabelMap.PreviousBtn>
<#if localPrevButtonVisible == "Y">
  <div class="action previousButton multiShipOptionsPreviousButton">
    <a href="${localPrevButtonUrl}" class="${localPrevButtonClass}"><span>${localPrevButtonDescription}</span></a>
  </div>
</#if>
<#assign localNextButtonVisible = nextButtonVisible!"Y">
<#assign localNextButtonClass = nextButtonClass!"standardBtn positive">
<#assign localNextButtonDescription = nextButtonDescription!uiLabelMap.ContinueBtn>
<#if localNextButtonVisible == "Y">
	<div class="action continueButton multiShipOptionsContinueButton">
	    <a href="javascript:document.${formName}.submit();" class="${localNextButtonClass}"><span>${localNextButtonDescription}</span></a>
	</div>
</#if>	

