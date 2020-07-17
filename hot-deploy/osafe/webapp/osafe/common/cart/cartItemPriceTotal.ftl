<div class="cartItemPriceTotal">
<h2><b>TEST CART PRICE TOTAL</b></h2>
<#--
  <tfoot class="cart_totals summary">
      <tr class=" discount_subtotal">
        <td  class="caption" <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >colspan="7"<#else>colspan="6"</#if>>
          <ul class="footContainer">
              <li>
                <div class="labelText">
                  <label>${uiLabelMap.SubTotalLabel}</label>
                </div>
                <div class="labelValue">
                  <span class="amount"><@ofbizCurrency amount=shoppingCart.getSubTotal() rounding=2 isoCode=currencyUom/></span>
                </div>
               </li>
               <#if (shoppingCart.getShipmentMethodTypeId()?has_content)>
                 <#assign selectedStoreId = shoppingCart.getOrderAttribute("STORE_LOCATION")?if_exists />
                 <#if !selectedStoreId?has_content && shoppingCart.getShipmentMethodTypeId()?has_content && shoppingCart.getCarrierPartyId()?has_content>
                     <#assign carrier =  delegator.findByPrimaryKeyCache("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shoppingCart.getCarrierPartyId()))?if_exists />
                     <#assign chosenShippingMethodDescription = carrier.groupName?default(carrier.partyId) + " " + shoppingCart.getShipmentMethodType(0).description />
                 </#if>
               <li>
                 <div class="labelText">
                   <label>${uiLabelMap.CartShippingMethodLabel}</label>
                 </div>
                 <div class="labelValue">
                   <span class="shippingMethod">${chosenShippingMethodDescription!}</span>
                 </div>
               </li>
               <li>
                  <div class="labelText">
                    <label>${uiLabelMap.CartShippingAndHandlingLabel}</label>
                  </div>
                  <div class="labelValue">
                    <span class="amount"><@ofbizCurrency amount=orderShippingTotal rounding=2 isoCode=currencyUom/></span>
                  </div>
               </li>
              </#if>
              <#if userLogin?has_content && (!Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO") || (orderTaxTotal?has_content && (orderTaxTotal &gt; 0)))>
               <li>
                    <div class="labelText">
                        <label>${uiLabelMap.CartTaxLabel}</label>
                    </div>
                    <div class="labelValue">
                        <span class="amount"><@ofbizCurrency amount=orderTaxTotal rounding=2 isoCode=currencyUom/></span>
                    </div>
               </li>
              </#if>
            <li>
              <div class="labelText">
                <label>${uiLabelMap.CartTotalLabel}</label>
              </div>
              <div class="labelValue">
                <span class="amount"><@ofbizCurrency amount=orderGrandTotal rounding=2 isoCode=currencyUom/></span>
              </div>
            </li>
            <#if shoppingCart.getAdjustments()?has_content>
              <#list shoppingCart.getAdjustments() as cartAdjustment>
                <#assign promoCodeText = ""/>
                <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType")>
                <#assign productPromo = cartAdjustment.getRelatedOneCache("ProductPromo")!"">
                <#if productPromo?has_content>
                  <#assign promoText = productPromo.promoText?if_exists/>
                  <#assign productPromoCode = productPromo.getRelatedCache("ProductPromoCode")>
                  <#if productPromoCode?has_content>
                    <#assign promoCodesEntered = shoppingCart.getProductPromoCodesEntered()!"">
                     <#if promoCodesEntered?has_content>
                        <#list promoCodesEntered as promoCodeEntered>
                          <#if productPromoCode?has_content>
                            <#list productPromoCode as promoCode>
                              <#assign promoCodeEnteredId = promoCodeEntered/>
                              <#assign promoCodeId = promoCode.productPromoCodeId!""/>
                              <#if promoCodeEnteredId?has_content>
                                  <#if promoCodeId == promoCodeEnteredId>
                                     <#assign promoCodeText = promoCode.productPromoCodeId?if_exists/>
                                  </#if>
                              </#if>
                            </#list>
                          </#if>
                         </#list>
                     </#if>
                  </#if>
                </#if>
              <li>
                  <div class="labelText">
                    <label><#if promoText?has_content>(<#if promoCodeText?has_content>${promoCodeText} </#if>${promoText})<#else>${adjustmentType.get("description",locale)?if_exists}</#if></label>
                  </div>
                  <div class="labelValue">
                    <span class="amount"><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) rounding=2 isoCode=currencyUom/></span>
                  </div>
                </li>
              </#list>
              //// show adjusted total if a promo is entered ////
              <#if promoText?has_content>
                 <li>
                    <div class="labelText">
                      <div class="adjustedTotalLabel"><label>${uiLabelMap.CartAdjustedTotalLabel}</label></div>
                    </div>
                    <div class="labelValue">
                      <div class="adjustedTotalValue"><span class="amount"><@ofbizCurrency amount=shoppingCart.getGrandTotal() rounding=2 isoCode=currencyUom/></span></div>
                    </div>
                 </li>
              </#if>
            </#if>
          </ul>
        </td>
      </tr>
      <tr>
        <td colspan="6">
          <span>${uiLabelMap.CartPostageInfo}</span>
        </td>
      </tr>
  </tfoot>
-->
</div>
