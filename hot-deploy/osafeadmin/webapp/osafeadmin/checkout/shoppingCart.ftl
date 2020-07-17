<#-- Note that shoppingCart is needed here because this screen is loaded (without any groovy) when shipping method is changed. -->
<#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request) />

<#if (shoppingCart?exists && shoppingCart.size() > 0)>
 <#assign shippingApplies = shoppingCart.shippingApplies() />
 <div class="showCartItems">
  <#assign offerPriceVisible= "N"/>
  <#list shoppingCart.items() as cartLine>
    <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
    <#if (cartItemAdjustment < 0) >
      <#assign offerPriceVisible= "Y"/>
      <#break>
    </#if>
  </#list>
  <div class="cartWrap">
    <input type="hidden" name="removeSelected" value="false"/>
    <table class="osafe">
        <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="descCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="descCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="actionCol"></th>
                <th class="qtyCol">${uiLabelMap.QtyLabel}</th>
                <th class="actionCol"></th>
                <th class="dollarCol">${uiLabelMap.UnitPriceLabel}</th>
                <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >
                    <th class="dollarCol">${uiLabelMap.OfferPriceLabel}</th>
                </#if>
                <th class="dollarCol">${uiLabelMap.ItemTotalLabel}</th>
                <th class="actions lastCol" scope="col">&nbsp;</th>
            </tr>
        </thead>
        <tbody>
        <#assign itemsFromList = false>
        <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
        <#assign rowClass = "1"/>
        <#list shoppingCart.items() as cartLine>
          <#assign hasNext = cartLine_has_next/>
          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
          <#assign product = cartLine.getProduct()>
          <#assign urlProductId = cartLine.getProductId()>
          <#assign productCategoryId = product.primaryProductCategoryId!""/>
          <#assign productCategoryId = cartLine.getProductCategoryId()?if_exists>
          <#if !productCategoryId?has_content>
             <#assign productCategoryMemberList = product.getRelated("ProductCategoryMember") />
             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMemberList,true)/>
             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productCategoryMemberList,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
             <#if productCategoryMemberList?has_content>
    	          <#assign productCategoryMember = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productCategoryMemberList)/>
                  <#assign productCategoryId = productCategoryMember.productCategoryId!"">
              </#if>
          </#if>
          <#if product.isVariant?if_exists?upper_case == "Y">
             <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(cartLine.getProductId(), delegator)?if_exists>
             <#assign urlProductId=virtualProduct.productId>
             <#if !productCategoryId?has_content>
	             <#assign productCategoryMemberList = virtualProduct.getRelated("ProductCategoryMember") />
	             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMemberList,true)/>
	             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productCategoryMemberList,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
	              <#if productCategoryMemberList?has_content>
	    	          <#assign productCategoryMember =Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productCategoryMemberList)/>
	                  <#assign productCategoryId = productCategoryMember.productCategoryId!"">
	              </#if>
              </#if>
          </#if>
          <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
          <#if virtualProduct?has_content>
            <#assign virtualProductContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(virtualProduct, request)!""/>
          </#if>
          <#assign price = cartLine.getBasePrice()>
          <#assign displayPrice = cartLine.getDisplayPrice()>
          <#assign offerPrice = "">
          <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
          <#if (cartItemAdjustment < 0) >
              <#assign offerPrice = cartLine.getDisplayPrice() + (cartItemAdjustment/cartLine.getQuantity())>
          </#if>
          <#if cartLine.getIsPromo() || (shoppingCart.getOrderType() == "SALES_ORDER" && !security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
              <#assign price= cartLine.getDisplayPrice()>
          <#else>
            <#if (cartLine.getSelectedAmount() > 0) >
                <#assign price = cartLine.getBasePrice() / cartLine.getSelectedAmount()>
            <#else>
                <#assign price = cartLine.getBasePrice()>
            </#if>
         </#if>
         <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
          <#if (product.isVariant?if_exists == 'Y')>
          	<td class="idCol <#if !hasNext?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>variantProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>">${product.productId?if_exists}</a></td>
          <#elseif product.productTypeId?if_exists == "FINISHED_GOOD" && (product.isVariant?if_exists == 'N') && (product.isVirtual?if_exists == 'N')>
            <td class="idCol <#if !hasNext?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>finishedProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>">${product.productId?if_exists}</a></td>
          </#if>
          <td class="descCol <#if !hasNext?if_exists>lastRow</#if>">${product.internalName?if_exists}</td>
          <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher)!""/>
          <#if !productName?has_content && virtualProductContentWrapper?has_content>
            <#assign productName = virtualProductContentWrapper.get("PRODUCT_NAME")!""/>
          </#if> 
          <td class="descCol lastRow"><#if productName?has_content>${productName!}</#if></td>
          <td class="actionCol <#if !hasNext?if_exists>lastRow</#if>">
            <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
            <#if productLongDescription?has_content && productLongDescription !="">
               <#assign productLongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
               <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
            </#if>
            <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
            <a href="javascript:void(0);" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
          </td>
          <td class="qtyCol <#if !hasNext>lastRow</#if>">
             <#if cartSection=="TOP">
              <input type="text" class="infoValue small qtyInCart_${cartLine.getProductId()}" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" <#if cartLine.getIsPromo()> readonly</#if>/>
             <#else>
              <input type="text" class="infoValue small BOTTOM_CART_ITEM" name="bottomCart_${cartLineIndex}" id="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" <#if cartLine.getIsPromo()> readonly</#if>/>
             </#if>
          </td>
          <td class="actionCol <#if !hasNext?if_exists>lastRow</#if>">
            <#if !cartLine.getIsPromo()>
              <#if cartSection=="TOP">
                <input type="hidden" name="qtyInCart_${cartLine.getProductId()}" id="qtyInCart_${cartLineIndex}"/>
            	<a href="javascript:submitDetailForm(document.adminCheckoutFORM, 'UC');"><span class="refreshIcon"></span></a>
              <#else>
            	<a href="javascript:refreshFromBottomCart();"><span class="refreshIcon"></span></a>
              </#if>
            </#if>
          </td>
          <td class="dollarCol <#if !hasNext>lastRow</#if>"><@ofbizCurrency amount=displayPrice rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
          <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y">
           <td class="dollarCol <#if !hasNext>lastRow</#if>">
             <#if (offerPrice?exists && offerPrice?has_content)>
               <@ofbizCurrency amount=offerPrice rounding=globalContext.currencyRounding isoCode=currencyUom/>
             </#if>
           </td>
          </#if>
          <td class="dollarCol total <#if !hasNext>lastRow</#if>"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
          <#-- display gift message option? -->
          <#assign showGiftMessageLink = false/>
          <#assign checkoutGiftMessage = Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(request, "CHECKOUT_GIFT_MESSAGE")!"" />  
          <#assign pdpGiftMessageAttributeValue = ""/>
          <#if product?has_content>
            <#assign pdpGiftMessageAttribute = delegator.findOne("ProductAttribute", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", product.productId,"attrName","CHECKOUT_GIFT_MESSAGE"), false)!/>
            <#if pdpGiftMessageAttribute?has_content>
              <#assign pdpGiftMessageAttributeValue = pdpGiftMessageAttribute.attrValue!""/>
            </#if>  
          </#if>
          <#-- if sys param is false then do not show gift message link -->
          <#if checkoutGiftMessage?has_content && checkoutGiftMessage && pdpGiftMessageAttributeValue?has_content && pdpGiftMessageAttributeValue!= "FALSE">
            <#assign showGiftMessageLink = true/>
          <#elseif checkoutGiftMessage?has_content && checkoutGiftMessage && !pdpGiftMessageAttributeValue?has_content>
            <#assign showGiftMessageLink = true/>
          <#elseif pdpGiftMessageAttributeValue?has_content && pdpGiftMessageAttributeValue == "TRUE">
            <#assign showGiftMessageLink = true/>
          </#if>
          <td class="actionColSmall <#if !hasNext?if_exists>lastRow</#if>">
            <#if !cartLine.getIsPromo()>
            	<#assign productIdStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("PRODUCT_NO", product.productId!)>
            	<#assign removeCartItemToolTipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","RemoveShoppingCartItemConfirmText",productIdStringMap, locale ) />
				<a href="javascript:setConfirmDialogContent('','${removeCartItemToolTipText}','deleteProductFromCart?delete_${cartLineIndex}=${cartLineIndex}');javascript:submitDetailForm(document.adminCheckoutFORM, 'CF');"  ><span class="crossIcon"></span></a>      
			</#if>
			<#if showGiftMessageLink>
              <a href="<@ofbizUrl>adminGiftMessageDetail?cartLineIndex=${cartLineIndex}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.EnterGiftMessageToolTip!}');" onMouseout="hideTooltip()"><span class="giftMessageIcon"></span></a>
            </#if>
          </td> 
         </tr>
	      <#if rowClass == "2">
	        <#assign rowClass = "1">
	      <#else>
	        <#assign rowClass = "2">
	      </#if>
        </#list>
      </tbody>
        <tfoot>
          <tr>
            <td <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >colspan="10"<#else>colspan="9"</#if>>
             <table class="osafe orderSummary">
                <tr>
                  <td class="totalCaption"><label>${uiLabelMap.SubTotalLabel}</label></td>
                  <td class="totalValue"><@ofbizCurrency amount=shoppingCart.getSubTotal() rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
                </tr>
                <#if shippingApplies>
                  <tr>
                    <td class="totalCaption"><label>${uiLabelMap.ShippingMethodCaption}</label></td>
                    <td class="totalValue">
                    <#if (shoppingCart.getShipmentMethodTypeId()?has_content)>
                      <#assign selectedStoreId = shoppingCart.getOrderAttribute("STORE_LOCATION")?if_exists />
                      <#if !selectedStoreId?has_content && shoppingCart.getShipmentMethodTypeId()?has_content && shoppingCart.getCarrierPartyId()?has_content>
                        <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shoppingCart.getCarrierPartyId()))?if_exists />
                        <#if carrier.groupName?has_content && carrier.partyId?has_content>
                          <#assign chosenShippingMethodDescription = carrier.groupName?default(carrier.partyId) + " " + shoppingCart.getShipmentMethodType(0).description />
                        </#if>
                      <#else>   
                     	<#assign chosenShippingMethodDescription = uiLabelMap.PickupInStoreLabel />    
                      </#if>
                      ${chosenShippingMethodDescription!}
                    </#if>
                  </td>
                </tr>
                <tr>
                  <td class="totalCaption"><label>${uiLabelMap.EstimatedShippingCaption}</label></td>
                  <td class="totalValue"><@ofbizCurrency amount=shoppingCart.getTotalShipping() isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                </tr>
                </#if>
                <#if shoppingCart.getAdjustments()?has_content>
                  <#list shoppingCart.getAdjustments() as cartAdjustment>
                    <#assign promoCodeText = ""/>
                    <#assign adjustmentType = cartAdjustment.getRelatedOne("OrderAdjustmentType")>
                    <#assign productPromo = cartAdjustment.getRelatedOne("ProductPromo")!"">
                    <#if productPromo?has_content>
                      <#assign promoText = productPromo.promoText?if_exists/>
                      <#assign productPromoCode = productPromo.getRelated("ProductPromoCode")>
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
                      <tr>
	                    <td class="totalCaption"><label><#if promoText?has_content>${promoText!""} <#if promoCodeText?has_content><a href="<@ofbizUrl>promotionCodeDetail?productPromoCodeId=${promoCodeText}</@ofbizUrl>">(${promoCodeText!})</a></#if><#elseif adjustmentType?has_content>${adjustmentType.get("description",locale)?if_exists}</#if>:</label></td>
	                    <td class="totalValue"><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
	                  </tr>
                    </#if>
                  </#list>
                </#if>
                <#if shoppingCart.getAdjustments()?has_content>
                  <#list shoppingCart.getAdjustments() as cartAdjustment>
                    <#assign adjustmentType = cartAdjustment.getRelatedOne("OrderAdjustmentType")>
                    <#assign loyaltyCartAdjustmentTypeId = adjustmentType.orderAdjustmentTypeId/>
                    <#if loyaltyCartAdjustmentTypeId == "LOYALTY_POINTS">
                      <#if adjustmentType.description?has_content>
                        <tr>
                          <#assign cartAdjTotal = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal())!0/>
	                      <td class="totalCaption"><label>${adjustmentType.get("description",locale)?if_exists}:</label></td>
	                      <td class="totalValue"><@ofbizCurrency amount=cartAdjTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
	                    </tr>
	                  </#if>
                    </#if>
                  </#list>
                </#if>
                <#assign taxAmount = shoppingCart.getTotalSalesTax()/>
                <#if (!Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO") || (orderTaxTotal?has_content && (orderTaxTotal &gt; 0)))>
				  <#if !Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_SHOW_SALES_TAX_MULTI")>
				    <tr>
				      <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", totalTaxPercent)>
				      <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxCaption",taxInfoStringMap, locale ) />
                      <td class="totalCaption"><label>${salesTaxCaption!}</label></td>
                      <td class="totalValue"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                    </tr>
				  <#else>
				    <#if appliedTaxList?exists && appliedTaxList?has_content && shipGroupSalesTaxSame>
				      <#list appliedTaxList as appliedTax >   
				        <tr>
					      <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", appliedTax.sourcePercentage, "description", appliedTax.description)>
				          <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxMultiCaption",taxInfoStringMap, locale ) />
	                      <td class="totalCaption"><label>${salesTaxCaption!}</label></td>
	                      <td class="totalValue"><@ofbizCurrency amount=appliedTax.amount isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
	                    </tr>
				      </#list>
				    <#else>
				      <tr>
				        <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", totalTaxPercent)>
				          <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxCaption",taxInfoStringMap, locale ) />
                        <td class="totalCaption"><label>${salesTaxCaption!}</label></td>
                        <td class="totalValue"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                      </tr>
				    </#if>
				  </#if>
				</#if>
                <tr>
                  <td class="totalCaption total"><label>${uiLabelMap.OrderTotalCaption}</label></td>
                  <td class="totalValue total">
                    <@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                  </td>
                </tr>
             </table>
            </td>
          </tr>
        </tfoot>
    </table>
  </div>
</div>
<#else>
    <table class="osafe">
        <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="descCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="descCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="actionCol"></th>
                <th class="dollarCol">${uiLabelMap.QtyLabel}</th>
                <th class="actionCol"></th>
                <th class="dollarCol">${uiLabelMap.UnitPriceLabel}</th>
                <th class="dollarCol">${uiLabelMap.ItemTotalLabel}</th>
                <th class="actions lastCol">&nbsp;</th>
            </tr>
        </thead>
            <tbody>
                 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
            </tbody>
    </table>
 </#if>