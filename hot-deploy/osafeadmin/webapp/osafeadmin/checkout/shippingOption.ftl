<#-- Get the current shipping method on the cart -->
<#if (shoppingCart?has_content) && (shoppingCart.getShipmentMethodTypeId()?has_content)>
  <#assign selectedStoreId = shoppingCart.getOrderAttribute("STORE_LOCATION")?if_exists />
  <#if !selectedStoreId?has_content && shoppingCart.getShipmentMethodTypeId()?has_content && shoppingCart.getCarrierPartyId()?has_content>
    <#assign carrier = delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shoppingCart.getCarrierPartyId()))?if_exists /> 
    <#if carrier.groupName?has_content && carrier.partyId?has_content>
      <#assign currentCartShipMeth = shoppingCart.getShipmentMethodType(0).shipmentMethodTypeId + "@" + carrier.partyId  />
    </#if>    
  </#if>
  <#if selectedStoreId?has_content>
  	 <#assign currentCartShipMeth = "NO_SHIPPING@_NA_" />
  </#if>
</#if>
<#assign chosenShippingMethod= parameters.shipping_method!currentCartShipMeth!/>

<div id="shippingOptionList">
<#if carrierShipmentMethodList?exists && carrierShipmentMethodList?has_content>
  <#-- When a user first clicks into shopping cart page, this is needed to set the cart to contain the initial first Shipping Method option -->
  <#if (shoppingCart.size() > 0)  && !(chosenShippingMethod?has_content)>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        var ship_selected = jQuery('input[name=shipping_method]:radio:checked');	    	
        setShippingMethod(ship_selected.val(), 'N');
      });
    </script>
  </#if>
  <#-- Add the available Shipment Methods as radio buttons -->
  <#list carrierShipmentMethodList as carrierMethod>
    <#assign shippingMethod = carrierMethod.shipmentMethodTypeId + "@" + carrierMethod.partyId />
    <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", carrierMethod.shipmentMethodTypeId, "partyId", carrierMethod.partyId,"roleTypeId" ,"CARRIER") />
    <#assign carrierShipmentMethod = delegator.findByPrimaryKey("CarrierShipmentMethod", findCarrierShipmentMethodMap) />
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>&nbsp;</label>
        </div>
        <div class="entryInput checkbox medium">
          <input class="checkBoxEntry" type="radio" name="shipping_method" value="${shippingMethod}" <#if (StringUtil.wrapString(shippingMethod) == StringUtil.wrapString(chosenShippingMethod!"")) || (!chosenShippingMethod?has_content && carrierMethod_index == 0)> checked="checked" </#if> onclick="setShippingMethod('${shippingMethod?if_exists}', 'N');" />
          <#if shoppingCart.getShippingContactMechId()?exists>
            <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierMethod)?default(-1) />
          </#if>
          <#if carrierMethod.partyId != "_NA_" && carrierShipmentMethod?has_content>
            <#assign carrierParty = carrierShipmentMethod.getRelatedOne("Party")/>
            <#assign carrierPartyGroup = carrierParty.getRelatedOne("PartyGroup")/>
            ${carrierPartyGroup.groupName?if_exists}&nbsp;
          </#if>
          ${carrierMethod.description?if_exists}
          <#if carrierShipmentMethod.optionalMessage?has_content> 
            &nbsp; ${carrierShipmentMethod.optionalMessage}
          </#if>
          <#if shippingEst?has_content>              
            <span class="shippingOptionEstimate">
              <#if (shippingEst > -1)>
                <@ofbizCurrency amount=shippingEst isoCode=shoppingCart.getCurrency() rounding=globalContext.currencyRounding/>
              <#else>
                ${uiLabelMap.ShippingEstimateCalculatedOffline}
              </#if>
            </span>
          </#if>
        </div>
      </div>
    </div>
  </#list>
</#if>

<#-- Add the Pick up in store option as the last radio button -->
<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>&nbsp;</label>
    </div>
    <div class="entryInput checkbox medium">
      <#if (shoppingCart?exists) >
        <#assign store_id = shoppingCart.getOrderAttribute("STORE_LOCATION")! />
      </#if>
      <#if store_id?has_content >
        <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId": store_id}, false)! />
        <#--
        <#assign partyGroupList = delegator.findByAnd("PartyGroup", {"groupNameLocal": store_id})! />
        <#if partyGroupList?has_content>
          <#assign partyGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyGroupList) />
        </#if>
        -->
      </#if>
      <#if partyGroup?has_content>
        <#assign groupName = partyGroup.groupName!""/>
      </#if>
      <input class="checkBoxEntry" type="radio" name="shipping_method" value="NO_SHIPPING@_NA_" <#if store_id?has_content > checked </#if> onclick="setShippingMethod('NO_SHIPPING@_NA_', 'N');" />
      ${uiLabelMap.CommonPickupInStoreLabel} 
      <div id="checkoutStoreSearch">
        ${uiLabelMap.StoreIdCaption}
        <input type="text" value="" name="storeCode">
        <a href="javascript:submitDetailForm(document.adminCheckoutFORM, 'UCPS');"><span class="refreshIcon"></span></a>	
        <#if store_id?has_content >
          <p id="checkoutStoreName" >${groupName!uiLabelMap.StoreNotFoundLabel!}</p>	
        </#if>	
      </div>
    </div>
  </div>
</div>
</div>
