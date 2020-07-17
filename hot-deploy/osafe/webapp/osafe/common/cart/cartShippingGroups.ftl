<#assign hideShipGroups = "N"/>
<#if checkDeliveryOptionMulti =="Y">
  <#if (!deliveryOption?has_content || deliveryOption != "SHIP_TO_MULTI")>
     <#assign hideShipGroups = "Y"/>
  </#if>
</#if>
<#if hideShipGroups =="N">
	<div class="${request.getAttribute("attributeClass")!}">
		<#if shoppingCart?exists && shoppingCart?has_content>
		   <#assign groupIndex=1?number/>
		   <#assign shipGroupIndex=0?int/>

        <div class="displayBox">
	      <h3>${uiLabelMap.ShippingOptionHeading} <#if multiAddressUrl?exists><span class="link"><a href="<@ofbizUrl>${multiAddressUrl?if_exists}</@ofbizUrl>"><span>${uiLabelMap.ShipToMultiAddressChangeLabel}</span></a></#if></h3>
        
		   <#list shoppingCart.getShipGroups() as cartShipInfo>
		      <h4>${uiLabelMap.ShippingGroupHeading} ${groupIndex} of ${shoppingCart.getShipGroupSize()}</h4>
		
			   <div class="boxList cartList">
			  	 <#assign lineIndex=0?number/>
			  	 <#assign rowClass = "1">
		          <div class="boxListItemTabular shipItem shippingGroupSummary">
                   <div class="shippingGroupCartItem grouping grouping1">
			           <#list cartShipInfo.getShipItems() as cartLine>
                        <div class="shippingGroupCartItem groupRow">
					      ${setRequestAttribute("cartLine", cartLine)}
						  ${setRequestAttribute("lineIndex", lineIndex)}
				          ${setRequestAttribute("rowClass", rowClass)}
				          ${setRequestAttribute("cartShipInfo", cartShipInfo)}
					      ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#shippingGroupCartItem")}
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
             	   <div class="shippingGroupShipGroupItem grouping grouping2">
				          ${setRequestAttribute("cartShipInfo", cartShipInfo)}
				          ${setRequestAttribute("shipGroupIndex", shipGroupIndex)}
						  ${setRequestAttribute("lineIndex", lineIndex)}
					      ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#shippingGroupShipGroupItem")}
				   </div>
				   
				 </div>
			   </div>
		
		     <#assign shipGroupIndex= shipGroupIndex + 1/>
		     <#assign groupIndex= groupIndex + 1/>
		   </#list>
		 </div>
		</#if>
	</div>
</#if>	
