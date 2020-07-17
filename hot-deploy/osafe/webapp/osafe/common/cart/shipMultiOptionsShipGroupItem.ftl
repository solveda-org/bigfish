   <ul class="displayList shipItemList multiShipOptions">
    <li class="container shipOption multiShipOptionsShipOptions<#if lineIndex == 0> firstRow</#if>">
	  <div>
        <label>${uiLabelMap.ChooseShipOptionLabel}</label>
        <#if carrierShipmentMethodList?exists && carrierShipmentMethodList?has_content>
	      <#assign paramChosenShippingMethod= parameters.get("shipping_method_${shipGroupIndex}")!""/>
          <#list carrierShipmentMethodList as carrierMethod>
            <#assign shippingMethod = carrierMethod.shipmentMethodTypeId + "@" + carrierMethod.partyId />
            <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", carrierMethod.shipmentMethodTypeId, "partyId", carrierMethod.partyId,"roleTypeId" ,"CARRIER") />
            <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)!"" />
	        <#if paramChosenShippingMethod?has_content>
                <#assign chosenShippingMethod= paramChosenShippingMethod/>
            <#else>
  	            <#if defaultShipMethodId?has_content && defaultShipMethodId == carrierMethod.productStoreShipMethId>
                     <#assign chosenShippingMethod = shippingMethod/>
  	            </#if>
  	           <#if !chosenShippingMethod?has_content>
                 <#assign chosenShippingMethod = shippingMethod/>
               </#if>
	        </#if>
              <div class="entry radioOption">
                <label class="radioOptionLabel">
                <input type="radio" name="shipping_method_${shipGroupIndex}" value="${shippingMethod}" <#if (StringUtil.wrapString(shippingMethod) == StringUtil.wrapString(chosenShippingMethod!""))>checked="checked" </#if>/>
                <span class="radioOptionText">
                  <#if carrierMethod.partyId != "_NA_" && carrierShipmentMethod?has_content>
                    <#assign carrierParty = carrierShipmentMethod.getRelatedOneCache("Party")/>
                    <#assign carrierPartyGroup = carrierParty.getRelatedOneCache("PartyGroup")/>
                    ${carrierPartyGroup.groupName?if_exists}&nbsp;
                  </#if>
                  ${carrierMethod.description?if_exists}<#if carrierShipmentMethod.optionalMessage?has_content> - ${carrierShipmentMethod.optionalMessage}</#if>
                </span>
                <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierMethod)?default(-1) />
                <span class="radioOptionTextAdditional">
                 <#if shippingEst?has_content> 
                  <#if (shippingEst > -1)>
                    <@ofbizCurrency amount=shippingEst isoCode=shoppingCart.getCurrency() rounding=globalContext.currencyRounding/>
                  <#else>${uiLabelMap.OrderCalculatedOffline}
                  </#if>
                 </#if>
                </span>
                </label>
              </div>
          </#list>
        <#else>
          <div class="entry radioOption">
            <span>${uiLabelMap.NoShippingMessageInfo}</span>
          </div>
        </#if>
      </div>
    </li>
   </ul>

