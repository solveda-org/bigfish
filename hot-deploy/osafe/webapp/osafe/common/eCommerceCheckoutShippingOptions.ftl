<div id="deliveryOptionBox">
<#assign cart = session.getAttribute("shoppingCart")/>
<#if cart?has_content && cart.getOrderAttribute("STORE_LOCATION")?has_content>
  ${screens.render("component://osafe/widget/EcommerceScreens.xml#storeDetail")}
  <div class="deliveryOption">
    <span>${uiLabelMap.DeliverMyItemsInfo}</span>
    <a href="javaScript:void(0);" onclick="removeStorePickup('CREDIT_CARD');" class="standardBtn positive">${uiLabelMap.DeliverMyItemsBtn!}</a>
  </div>
<#else>
<div id="shippingOptionDisplay" class="displayBox">
    <div class="displayBoxHeader">
      <span class="displayBoxHeaderCaption">${uiLabelMap.ShippingMethodsHeading}</span>
    </div>
    <input type="hidden" name="checkoutpage" value="shippingoptions"/>
    <div class="shippingMethodsContainer">
      <#if chosenShippingMethod?has_content && chosenShippingMethod.equals("NO_SHIPPING@_NA_")>
        <#assign chosenShippingMethod = "">
      </#if>
      <#list carrierShipmentMethodList as carrierMethod>
        <#assign shippingMethod = carrierMethod.shipmentMethodTypeId + "@" + carrierMethod.partyId />
        <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", carrierMethod.shipmentMethodTypeId, "partyId", carrierMethod.partyId,"roleTypeId" ,"CARRIER") />
        <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap) />
        <div>
          <fieldset class="col">
            <div class="entry radioOption">
              <input type="radio" name="shipping_method" value="${shippingMethod}" <#if (StringUtil.wrapString(shippingMethod) == StringUtil.wrapString(chosenShippingMethod!"")) || (!chosenShippingMethod?has_content && carrierMethod_index == 0)>checked="checked" </#if> onclick="setShippingMethod('${shippingMethod?if_exists}');" />
              <#if shoppingCart.getShippingContactMechId()?exists>
                <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierMethod)?default(-1) />
              </#if>
              <span class="radioOptionText"> <#-- use margin left -->
                <#if carrierMethod.partyId != "_NA_" && carrierShipmentMethod?has_content>
                  <#assign carrierParty = carrierShipmentMethod.getRelatedOne("Party")/>
                  <#assign carrierPartyGroup = carrierParty.getRelatedOne("PartyGroup")/>
                  ${carrierPartyGroup.groupName?if_exists}&nbsp;
                </#if>
                ${carrierMethod.description?if_exists}

                  <#if carrierShipmentMethod.optionalMessage?has_content> - ${carrierShipmentMethod.optionalMessage}</#if>
              </span>
              <span class="radioOptionTextAdditional"><#if shippingEst?has_content> <#if (shippingEst > -1)><@ofbizCurrency amount=shippingEst isoCode=shoppingCart.getCurrency()/><#else>${uiLabelMap.OrderCalculatedOffline}</#if></#if></span>
            </div>
          </fieldset>
        </div>
      </#list>
    </div>
</div>
    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(CHECKOUT_STORE_PICKUP)>
      <div class="deliveryOption">
        <span>${uiLabelMap.SelectStoreInfo}</span>
        <a href="javaScript:void(0);" onClick="displayDialogBox('${dialogPurpose!}');" class="standardBtn positive">${uiLabelMap.SelectStoreBtn!}</a>
      </div>
    </#if>
</#if>
<input type="hidden" id="isGoogleApi" name="isGoogleApi" value=""/>
</div>

<#-- Fileds that were on the original page, giving default values 
<input type="hidden" name="may_split" value="N"/>
<input type="hidden" name="shipping_instructions" value=""/>
<input type="hidden" name="correspondingPoId" value=""/>
<input type="hidden" name="is_gift" value=""/>
<input type="hidden" name="gift_message" value=""/>
<input type="hidden" name="order_additional_emails" value=""/>
-->