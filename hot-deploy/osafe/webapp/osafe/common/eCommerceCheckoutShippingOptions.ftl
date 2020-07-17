<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign cart = session.getAttribute("shoppingCart")/>
<#assign hasShipping = cart.shippingApplies()/>
<#if cart?has_content && hasShipping>
 <#if (!deliveryOption?has_content || deliveryOption != "SHIP_TO_MULTI")>
	 <div class="${request.getAttribute("attributeClass")!}">
	  <div id="js_deliveryOptionBox">
	    <div id="shippingOptionDisplay" class="displayBox">
	      <h3>${uiLabelMap.ShippingMethodsHeading}</h3>
	      <div class="js_shippingMethodsContainer">
		   <div class="entryForm shippingMethods">
	        <#-- Display All available Shipping Method Options -->
	        <#if carrierShipmentMethodList?exists && carrierShipmentMethodList?has_content>
	          <#list carrierShipmentMethodList as carrierMethod>
	            <#assign shippingMethod = carrierMethod.shipmentMethodTypeId + "@" + carrierMethod.partyId />
	            <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", carrierMethod.shipmentMethodTypeId, "partyId", carrierMethod.partyId,"roleTypeId" ,"CARRIER") />
	            <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)!"" />
	            <div>
	              <div class="entry radioOption">
	                <label class="radioOptionLabel"><input type="radio" class="js_shipping_method js_shippingMethodRadioButton" name="<#if !userLogin?has_content || userLogin.userLoginId == "anonymous">shipMethod<#else>shipping_method</#if>" value="${shippingMethod}" <#if (StringUtil.wrapString(shippingMethod) == StringUtil.wrapString(chosenShippingMethod!""))>checked="checked" </#if> onclick="setShippingMethod('${shippingMethod?if_exists}', 'N');" />
	                <#if shoppingCart.getShippingContactMechId()?exists>
	                  <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierMethod)?default(-1) />
	                </#if>
	                <span class="radioOptionText"> <#-- use margin left -->
	                  <#if carrierMethod.partyId != "_NA_" && carrierShipmentMethod?has_content>
	                    <#assign carrierParty = carrierShipmentMethod.getRelatedOneCache("Party")/>
	                    <#assign carrierPartyGroup = carrierParty.getRelatedOneCache("PartyGroup")/>
	                    ${carrierPartyGroup.groupName?if_exists}&nbsp;
	                  </#if>
	                  ${carrierMethod.description?if_exists}<#if carrierShipmentMethod.optionalMessage?has_content> - ${carrierShipmentMethod.optionalMessage}</#if>
	                </span>
	                <span class="radioOptionTextAdditional"><#if shippingEst?has_content> <#if (shippingEst > -1)><@ofbizCurrency amount=shippingEst isoCode=shoppingCart.getCurrency() rounding=globalContext.currencyRounding/><#else>${uiLabelMap.OrderCalculatedOffline}</#if></#if></span>
	                </label>
	              </div>
	            </div>
	          </#list>
	        <#else>
	          <span class="noShippingMessage">${uiLabelMap.NoShippingMessageInfo}</span>
	        </#if>
	        <#-- Display Store Pickup Option -->
	        <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_STORE_PICKUP")>
			  <div>
			    <div class="entry radioOption">
			      <#assign storePartyId = oneStoreOpenStoreId!"">
			      <#if !storePartyId?has_content>
			      	<#if storeInfo?has_content>
			      	  <#assign storePartyId = storeInfo.partyId!"">
			      	</#if>
			      </#if>
			      <input type="hidden" id="js_storeId" name="storeId" value="${storePartyId!""}">
			      <#-- flag to let BF know that there is only one store Open during checkout, so use this storeId for checkout -->
			      <#if oneStoreOpenStoreId?exists && oneStoreOpenStoreId?has_content && oneStoreOpenStoreId != "">
			        <input type="hidden" id="oneStoreOpen" name="oneStoreOpen" value="Y">
			      </#if>
			      <input type="hidden" id="isGoogleApi" name="isGoogleApi" value=""/>
			      <#assign shippingMethod = "NO_SHIPPING@_NA_" />
			      <#assign chosenShippingMethod = parameters.shipping_method!parameters.shipMethod!chosenShippingMethod!"" />
			      <label class="radioOptionLabel">
			      <input type="radio" class="js_shipping_method storePickupRadioButton" name="<#if !userLogin?has_content || userLogin.userLoginId == "anonymous">shipMethod<#else>shipping_method</#if>" value="${shippingMethod!}" <#if (StringUtil.wrapString(shippingMethod) == StringUtil.wrapString(chosenShippingMethod!"")) || cart.getOrderAttribute("STORE_LOCATION")?has_content>checked="checked" </#if> onclick="setShippingMethod('${shippingMethod?if_exists}', 'N');"/>
				  <span class="radioOptionText">
			        ${uiLabelMap.StorePickupLabel} 
			        <#if storeInfo?has_content>
			          (${storeInfo.groupName!}
			          <#if storeAddress?has_content>
			            , ${storeAddress.city!}, ${storeAddress.stateProvinceGeoId!} ${storeAddress.postalCode!}
			          </#if>
			            )
			        </#if>
			      </span>
			      <#if !oneStoreOpenStoreId?exists || !oneStoreOpenStoreId?has_content>
				    <span class="StorePickupBtn">
				      <#if cart.getOrderAttribute("STORE_LOCATION")?has_content>
				        <a href="javaScript:void(0);" onClick="prepareActionDialog('${dialogPurpose!}'); displayActionDialogBox('${dialogPurpose!}',this);" class="standardBtn positive">
				          <span>${uiLabelMap.ChangeStoreBtn!}</span>
				        </a>
				      <#else>
				        <a href="javaScript:void(0);" onClick="prepareActionDialog('${dialogPurpose!}'); displayActionDialogBox('${dialogPurpose!}',this);" class="standardBtn positive">
				          <span>${uiLabelMap.SelectStoreBtn!}</span>
				        </a>
				      </#if>
				    </span>
			      </#if>
			      <span class="radioOptionTextAdditional">
			        <@ofbizCurrency amount=0 isoCode=cart.getCurrency() rounding=globalContext.currencyRounding/>
			      </span>
			      </label>
			    </div>
			  </div>
			</#if>
		   </div>
	      </div>
	    </div>
	  </div>
	  <div id="js_deliveryOptionBoxError">
		  <@fieldErrors fieldName="NO_SHIPPING_METHOD"/>
		  <@fieldErrors fieldName="NO_STORE_PICKUP"/>
	  </div>
	 </div>
 </#if>
</#if>

<#-- Fileds that were on the original page, giving default values 
<input type="hidden" name="may_split" value="N"/>
<input type="hidden" name="shipping_instructions" value=""/>
<input type="hidden" name="correspondingPoId" value=""/>
<input type="hidden" name="is_gift" value=""/>
<input type="hidden" name="gift_message" value=""/>
<input type="hidden" name="order_additional_emails" value=""/>
-->