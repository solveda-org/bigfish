<#if (orderItems?has_content && orderItems.size() > 0)>
 <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
 <div class="checkoutOrderItems">
    <#assign currencyUom = CURRENCY_UOM_DEFAULT!currencyUomId />
    <#assign offerPriceVisible= "N"/>
    <#list orderItems as orderItem>
        <#assign orderItemAdjustment = localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem)/>
        <#if (orderItemAdjustment < 0) >
            <#assign offerPriceVisible= "Y"/>
            <#break>
        </#if>
    </#list>
    <div class="orderDetails">
        <div>
            <div id="orderItemsWrap">
        <table id="order_display" class="standardTable" summary="CurrentOrder_TABLE_SUMMARY">
            <thead>
                <tr><th class="firstCol lastCol" colspan="<#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >10<#else>9</#if>"><span class="headerCaption">${uiLabelMap.OrderDetailsHeading}</span></th></tr>
                <tr>
                  <th class="product firstCol" scope="col" colspan="2">${uiLabelMap.ProductLabel}</th>
                  <th class="statusCol" scope="col">${uiLabelMap.StatusLabel}</th>
                  <th class="shipDateCol" scope="col">${uiLabelMap.ShippingDateLabel}</th>
                  <th class="carrierCol" scope="col">${uiLabelMap.CarrierLabel}</th>
                  <th class="trackingIdCol" scope="col">${uiLabelMap.TrackingIdLabel}</th>
                  <th class="quantity" scope="col">${uiLabelMap.QuantityLabel}</th>
                  <th class="priceCol numberCol" scope="col">${uiLabelMap.PriceLabel}</th>
                  <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >
                      <th class="priceCol numberCol" scope="col">${uiLabelMap.OfferPriceLabel}</th>
                  </#if>
                  <th class="total numberCol lastCol" scope="col">${uiLabelMap.TotalLabel}</th>
                </tr>
            </thead>
            <tfoot>
                <tr>
                    <td id="summaryCell" <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >colspan="10"<#else>colspan="9"</#if>>
                        <table class="summary">
                            <tr>
                              <th class="caption"><label>${uiLabelMap.SubTotalLabel}</label></th>
                              <td class="value numberCol"><@ofbizCurrency amount=orderSubTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
                            </tr>
                            <#-- Shipping Method -->
                            <#if shippingApplies?exists && shippingApplies>
                              <#if orderItemShipGroups?has_content>
                                <#list orderItemShipGroups as shipGroup>
                                  <#if orderHeader?has_content>
                                    <#assign orderAttrPickupStoreList = orderHeader.getRelatedByAnd("OrderAttribute", Static["org.ofbiz.base.util.UtilMisc"].toMap("attrName", "STORE_LOCATION")) />
                                    <#if orderAttrPickupStoreList?has_content>
                                      <#assign orderAttrPickupStore = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderAttrPickupStoreList) />
                                      <#assign selectedStoreId = (orderAttrPickupStore.attrValue)?if_exists />
                                    </#if>
                                    <#if !selectedStoreId?has_content >
                                      <#assign shipmentMethodType = shipGroup.getRelatedOneCache("ShipmentMethodType")?if_exists>
                                      <#assign carrierPartyId = shipGroup.carrierPartyId?if_exists>
                                      <#if shipmentMethodType?has_content>
                                        <#assign carrier =  delegator.findByPrimaryKeyCache("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
                                        <#if carrier?has_content >
                                          <#assign chosenShippingMethodDescription = carrier.groupName?default(carrier.partyId) + " " + shipmentMethodType.description >
                                        </#if>
                                      </#if>
                                    </#if>
                                  </#if>
                                </#list><#-- end list of orderItemShipGroups -->
                              </#if>
                              <tr>
                                <th class="caption"><label>${uiLabelMap.ShippingMethodLabel}</label></th>
                                <td class="shippingMethod"><#if selectedStoreId?has_content>${uiLabelMap.StorePickupCaption} <#else> ${chosenShippingMethodDescription!""}</#if>
                                </td>
                              </tr>
                              <tr>
                                <th class="caption"><label>${uiLabelMap.ShippingAndHandlingLabel}</label></th>
                                <td class="value numberCol"><@ofbizCurrency amount=orderShippingTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
                              </tr>
                            </#if>
                            <#list headerAdjustmentsToShow as orderHeaderAdjustment>
                                      <#assign adjustmentType = orderHeaderAdjustment.getRelatedOneCache("OrderAdjustmentType")>
                                      <#assign productPromo = orderHeaderAdjustment.getRelatedOneCache("ProductPromo")!"">
                                      <#assign promoCodeText = ""/>
                                      <#if productPromo?has_content>
                                         <#assign promoText = productPromo.promoText?if_exists/>
                                         <#assign productPromoCode = productPromo.getRelatedCache("ProductPromoCode")>
                                         <#assign promoCodesEntered = localOrderReadHelper.getProductPromoCodesEntered()!""/>
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
                                <th class="caption"><label><#if promoText?has_content>${promoText}<#if promoCodeText?has_content> (${promoCodeText})</#if><#else>${adjustmentType.get("description",locale)?if_exists}</#if></label></th>
                                <td class="value numberCol"><@ofbizCurrency amount=localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
                              </tr>
                            </#list>
                            <#if (!Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO")) || (orderTaxTotal?has_content && (orderTaxTotal &gt; 0))>
                                <tr>
                                  <th class="caption"><label>${uiLabelMap.SalesTaxLabel}</label></th>
                                  <td class="value numberCol"><@ofbizCurrency amount=orderTaxTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></td>
                                </tr>
                            </#if>
                            <tr>
                              <td></td>
                            </tr>
                            <tr>
	                            <#-- show adjusted total if a promo is entered -->
	                  			<#if promoText?has_content>
									<th class="caption"><label>${uiLabelMap.AdjustedTotalLabel}</label></th>
		                            <td class="total numberCol">
		                                <div class="adjustedTotalLabel"><@ofbizCurrency amount=orderGrandTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></div>
		                            </td>
								<#else>
									<th class="caption"><label>${uiLabelMap.TotalLabel}</label></th>
	                              	<td class="total numberCol">
	                                	<div class="adjustedTotalValue"><@ofbizCurrency amount=orderGrandTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></div>
	                              	</td>
	                  			</#if>
                            </tr>
                        </table>
                    </td>
               </tr>
            </tfoot>
            <tbody>
            <#list orderItems as orderItem>
              <#assign product = orderItem.getRelatedOneCache("Product")?if_exists/>
              <#assign urlProductId = product.productId>
              <#assign productCategoryId = product.primaryProductCategoryId!""/>
              <#assign productCategoryId = orderItem.productCategoryId!""/>
              <#assign trackingURL = "">
              <#assign trackingNumber = "">
              <#if orderItem.orderId?exists && orderItem.orderId?has_content >
              	<#assign shipGroupAssoc = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAndCache("OrderItemShipGroupAssoc", {"orderId": orderItem.orderId, "orderItemSeqId": orderItem.orderItemSeqId}))/>
              	<#if shipGroupAssoc?has_content>
                  <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAndCache("OrderItemShipGroup", {"orderId": orderItem.orderId, "shipGroupSeqId": shipGroupAssoc.shipGroupSeqId}))/>
                  <#if shipGroup?has_content>
                      <#assign shipDate = ""/>
                      <#assign orderHeader = delegator.findByPrimaryKeyCache("OrderHeader", {"orderId": orderItem.orderId})/>
                      <#if orderHeader?has_content && (orderHeader.statusId == "ORDER_COMPLETED" || orderItem.statusId == "ITEM_COMPLETED") >
                          <#assign shipDate = shipGroup.estimatedShipDate!""/>
                          <#if shipDate?has_content>
                              <#assign shipDate = shipDate?string(preferredDateFormat)!""/>
                          </#if>
                      </#if>
                      <#assign trackingNumber = shipGroup.trackingNumber!""/>
                      <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", shipGroup.shipmentMethodTypeId, "partyId", shipGroup.carrierPartyId,"roleTypeId" ,"CARRIER")>
                      <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)>
                      <#assign shipmentMethodType = carrierShipmentMethod.getRelatedOneCache("ShipmentMethodType")/>
                      <#assign description = shipmentMethodType.description!""/>
                      <#assign carrierPartyGroupName = ""/>
                      <#if shipGroup.carrierPartyId != "_NA_">
                          <#assign carrierParty = carrierShipmentMethod.getRelatedOneCache("Party")/>
                          <#assign carrierPartyGroup = carrierParty.getRelatedOneCache("PartyGroup")/>
                          <#assign carrierPartyGroupName = carrierPartyGroup.groupName/>
                          <#assign trackingURLPartyContents = delegator.findByAndCache("PartyContent", {"partyId": shipGroup.carrierPartyId, "partyContentTypeId": "TRACKING_URL"})/>
                          <#if trackingURLPartyContents?has_content>
                              <#assign trackingURLPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(trackingURLPartyContents)/>
                              <#if trackingURLPartyContent?has_content>
                                  <#assign content = trackingURLPartyContent.getRelatedOneCache("Content")/>
                                  <#if content?has_content>
                                      <#assign dataResource = content.getRelatedOneCache("DataResource")!""/>
                                      <#if dataResource?has_content>
                                          <#assign electronicText = dataResource.getRelatedOneCache("ElectronicText")!""/>
                                          <#assign trackingURL = electronicText.textData!""/>
                                          <#if trackingURL?has_content>
                                              <#assign trackingURL = Static["org.ofbiz.base.util.string.FlexibleStringExpander"].expandString(trackingURL, {"TRACKING_NUMBER":trackingNumber})/>
                                          </#if>
                                      </#if>
                                  </#if>
                              </#if>
                          </#if>
                      </#if>
                  </#if>
              	</#if>
              </#if>
              <#if !productCategoryId?has_content>
                  <#assign currentProductCategories = Static["org.ofbiz.product.product.ProductWorker"].getCurrentProductCategories(product)![]>
                  <#if currentProductCategories?has_content>
                      <#assign productCategoryId = currentProductCategories[0].productCategoryId!"">
                  </#if>
              </#if>
              <#if product.isVariant?if_exists?upper_case == "Y">
                 <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(product.productId, delegator)?if_exists>
                 <#assign urlProductId=virtualProduct.productId>
                 <#if !productCategoryId?has_content>
                      <#assign currentProductCategories = Static["org.ofbiz.product.product.ProductWorker"].getCurrentProductCategories(virtualProduct)![]>
                      <#if currentProductCategories?has_content>
                          <#assign productCategoryId = currentProductCategories[0].productCategoryId!"">
                      </#if>
                  </#if>
              </#if>
              
              <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
              <#if (!productImageUrl?has_content && !(productImageUrl == "null")) && virtualProduct?has_content>
                   <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
              </#if>
              <#-- If the string is a literal "null" make it an "" empty string then all normal logic can stay the same -->
              <#if (productImageUrl?string?has_content && (productImageUrl == "null"))>
                   <#assign productImageUrl = "">
              </#if>
    
              <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher)?if_exists>
              <#if !productName?has_content && virtualProduct?has_content>
                   <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher)?if_exists>
              </#if>
    
              <#assign price = orderItem.unitPrice>
              <#assign displayPrice = orderItem.unitPrice>
              <#assign offerPrice = "">
              <#assign orderItemAdjustment = localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem)/>
              <#if (orderItemAdjustment < 0) >
                  <#assign offerPrice = orderItem.unitPrice + (orderItemAdjustment/orderItem.quantity)>
              </#if>
              <#if (orderItem.isPromo == "Y")>
                  <#assign price= orderItem.unitPrice>
              <#else>
                <#if (orderItem.selectedAmount > 0) >
                    <#assign price = orderItem.unitPrice / orderItem.selectedAmount>
                <#else>
                    <#assign price = orderItem.unitPrice>
                </#if>
             </#if>
             <#assign productFriendlyUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId=${urlProductId}&productCategoryId=${productCategoryId!""}')/>
             <#assign IMG_SIZE_CART_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_CART_H")!""/>
             <#assign IMG_SIZE_CART_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_CART_W")!""/>
    
            <#assign availability = uiLabelMap.InStockLabel>
                <tr>
                    <td class="image firstCol <#if !orderItem_has_next>lastRow</#if>" scope="row">
                        <a href="${productFriendlyUrl}" id="image_${urlProductId}">
                            <img alt="${StringUtil.wrapString(productName)}" src="${productImageUrl}" class="productCartListImage" height="${IMG_SIZE_CART_H!""}" width="${IMG_SIZE_CART_W!""}">
                        </a>
                    </td>
                    <td class="description <#if !orderItem_has_next>lastRow</#if>">
                        <dl>
                            <dt>${uiLabelMap.ProductDescriptionAttributesInfo}</dt>
                            <dd class="description">
                              <a href="${productFriendlyUrl}">${StringUtil.wrapString(productName!)}</a>
                            </dd>
			                 <#assign productFeatureAndAppls = product.getRelatedCache("ProductFeatureAndAppl") />
			                 <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureAndAppls,true)/>
			                 <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productFeatureAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
                            <#if productFeatureAndAppls?has_content>
                              <#list productFeatureAndAppls as productFeatureAndAppl>
                                <#assign productFeatureTypeLabel = ""/>
                                <#if productFeatureTypesMap?has_content>
                                  <#assign productFeatureTypeLabel = productFeatureTypesMap.get(productFeatureAndAppl.productFeatureTypeId)!"" />
                                </#if>
                                <dd>${productFeatureTypeLabel!}:${productFeatureAndAppl.description!}</dd>
                              </#list>
                            </#if>
                        </dl>
                    </td>
                    <#if orderHeader?has_content>
                      <#assign status = orderHeader.getRelatedOneCache("StatusItem") />
                      <td class="statusCol <#if !orderItem_has_next>lastRow</#if>">${status.get("description",locale)}</td>
                    </#if>
                    <td class="shipDateCol <#if !orderItem_has_next>lastRow</#if>">${shipDate!}</td>
                    <td class="carrierCol <#if !orderItem_has_next>lastRow</#if>">${carrierPartyGroupName!} ${description!}</td>                    
                    <td class="trackingIdCol <#if !orderItem_has_next>lastRow</#if>"><#if trackingURL?has_content><a href="JavaScript:newPopupWindow('${trackingURL!""}');">${trackingNumber!}</a><#else>${trackingNumber!}</#if></td>
                    <td class="quantity <#if !orderItem_has_next>lastRow</#if>">
                        ${orderItem.quantity?string.number}
                    </td>
                    <td class="price numberCol <#if !orderItem_has_next>lastRow</#if>">
                        <ul title="Price Information">
                            <li>
                            <div id="priceelement">
                                <ul>
                                    <li>
                                        <span class="price"><@ofbizCurrency amount=displayPrice rounding=globalContext.currencyRounding isoCode=currencyUom/></span>
                                    </li>
                                </ul>
                            </div>
    
                           </li>
                        </ul>
                    </td>
                    <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >
                        <td class="price numberCol <#if !orderItem_has_next>lastRow</#if>">
                            <ul title="Price Information">
                                <li>
                                <div id="priceelement">
                                    <ul>
                                        <li>
                                            <span class="price">
                                            <#if offerPrice?exists && offerPrice?has_content>
                                                <@ofbizCurrency amount=offerPrice rounding=globalContext.currencyRounding isoCode=currencyUom/>
                                            </#if>
                                            </span>
                                        </li>
                                    </ul>
                                </div>
        
                               </li>
                            </ul>
                        </td>
                    </#if>
                    <td class="total numberCol lastCol <#if !orderItem_has_next>lastRow</#if>">
                        <ul>
                            <li>
                                <span class="price"><@ofbizCurrency amount=localOrderReadHelper.getOrderItemSubTotal(orderItem,localOrderReadHelper.getAdjustments()) rounding=globalContext.currencyRounding isoCode=currencyUom/></span>
                            </li>
                        </ul>
                    </td>
    
                </tr>
            </#list>
          </tbody>
        </table>
      </div>
     </div>
    </div>
 </div>
</#if>
