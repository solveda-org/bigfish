	   <ul class="displayList shipItemList shippingGroupSummary">
	    <li class="container shipAddress shippingGroupSummaryShipAddress<#if lineIndex == 0> firstRow</#if>">
		  <div>
		    <#if shippingAddress?has_content && ((!isStorePickUp?exists) || isStorePickUp=="N")>
	          <label>${uiLabelMap.ShippingToLabel}</label>
		      <#assign contactMech = delegator.findByPrimaryKeyCache("ContactMech", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", cartShipInfo.contactMechId))?if_exists />
		      <#if contactMech?has_content>
		          <#assign postalAddress = contactMech.getRelatedOneCache("PostalAddress")?if_exists />
				    ${setRequestAttribute("PostalAddress", postalAddress)}
				    ${setRequestAttribute("DISPLAY_FORMAT", "MULTI_LINE_FULL_ADDRESS")}
				    ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
		      </#if>
		    <#else>
		      <#if storeInfo?has_content && shoppingCartStoreId?exists && shoppingCartStoreId?has_content>
			        <label>${uiLabelMap.StoreAddressCaption}</label>
		            ${setRequestAttribute("PostalAddress", storeAddress)}
		            ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
			    </#if>
		    </#if>
	      </div>
	    </li>
	   </ul>
	   <ul class="displayList shipItemList shippingGroupSummary">
	    <li class="container shipOption shippingGroupSummaryShipOption<#if lineIndex == 0> firstRow</#if>">
		  <div>
	        <label>${uiLabelMap.ShippingMethodLabel}</label>
		        <#if shippingAddress?has_content && ((!isStorePickUp?exists) || isStorePickUp=="N")>
	            <#assign carrierGroupName =""/>
	            <#assign carrierPartyId = cartShipInfo.getCarrierPartyId()!""/>
			    <#assign carrierPartyGroup =  delegator.findByPrimaryKeyCache("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", carrierPartyId))?if_exists />
	            <#if carrierPartyGroup?has_content>
	                <#assign carrierGroupName =carrierPartyGroup.groupName!""/>
	            </#if>
	            <#assign shipMethodTypeDescription =""/>
		        <#assign shipMethodTypeId = cartShipInfo.getShipmentMethodTypeId()!""/>
			    <#assign shipMethodType =  delegator.findByPrimaryKeyCache("ShipmentMethodType", Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", shipMethodTypeId))?if_exists />
	   	        <#if shipMethodType?has_content>
	               <#assign shipMethodTypeDescription =shipMethodType.description!""/>
	   	        </#if>
		        <#assign shipEstimate = cartShipInfo.getShipEstimate()!"0"/>
		        <span>${carrierGroupName!carrierPartyId!}</span>
		        <span>${shipMethodTypeDescription!shipMethodTypeId!}</span>
		        <span><@ofbizCurrency amount=shipEstimate isoCode=shoppingCart.getCurrency() rounding=globalContext.currencyRounding/></span>
		    <#else>
		        <#if storeInfo?has_content && shoppingCartStoreId?exists && shoppingCartStoreId?has_content>
		            <span>${uiLabelMap.StorePickupLabel}</span>
		        </#if>
		    </#if>
	     </div>
	    </li>
	   </ul>

