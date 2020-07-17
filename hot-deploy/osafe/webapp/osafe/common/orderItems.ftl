<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#assign shoppingCartItems = shoppingCart.items() />

<#if (orderItems.size() > 0)>
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
	    <table id="cart_display" summary="CurrentOrder_TABLE_SUMMARY">
	        <thead>
	            <tr class="cart_headers"><th class="firstCol lastCol" colspan="<#if (useAvailability?has_content) && useAvailability == "Y" >7<#else>6</#if>"><span class="headerCaption">${uiLabelMap.OrderDetailsHeading}</span></th></tr>
	            <tr class="cart_headers">
	                <th class="product firstCol" scope="col" colspan="2">${uiLabelMap.ProductLabel}</th>
	                <th class="quantity" scope="col">${uiLabelMap.QuantityLabel}</th>
	                <#if (useAvailability?has_content) && useAvailability == "Y" >
	                    <th class="availability" scope="col">${uiLabelMap.AvailabilityLabel}</th>
	                </#if>
	                <th class="priceCol numberCol" scope="col">${uiLabelMap.PriceLabel}</th>
	                <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >
	                    <th class="priceCol numberCol" scope="col">${uiLabelMap.OfferPriceLabel}</th>
	                </#if>
	                <th class="total numberCol lastCol" scope="col">${uiLabelMap.TotalLabel}</th>
	            </tr>
	        </thead>
	        <tfoot>
	            <tr>
	                <td id="summaryCell" <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >colspan="7"<#else>colspan="6"</#if>>
	                    <table class="summary">
	                        <tr>
	                          <th class="caption"><label>${uiLabelMap.SubTotalLabel}</label></th>
	                          <td class="value numberCol"><@ofbizCurrency amount=orderSubTotal rounding=2 isoCode=currencyUom/></td>
	                        </tr>
	                        <#-- Shipping Method -->
	                        <#-- This section is for when we are on order status otherwsie shipping method will be on the shopping cart -->
	                        <#if orderItemShipGroups?has_content>
	                            <#list orderItemShipGroups as shipGroup>
	                              <#if orderHeader?has_content>
	                                <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
	                                <#assign carrierPartyId = shipGroup.carrierPartyId?if_exists>
	                                <#assign chosenShippingMethodDescription = carrierPartyId + " " + shipmentMethodType.description >
	                                <#assign orderAttrPickupStoreList = orderHeader.getRelatedByAnd("OrderAttribute", Static["org.ofbiz.base.util.UtilMisc"].toMap("attrName", "STORE_LOCATION")) />
	                                <#if orderAttrPickupStoreList?has_content>
	                                  <#assign orderAttrPickupStore = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderAttrPickupStoreList) />
	                                  <#assign selectedStoreId = (orderAttrPickupStore.attrValue)?if_exists />
	                                </#if>
	                              <#elseif shoppingCartItems?has_content && (shoppingCartItems.size() &gt; 0)>
	                                <#if shoppingCart.getShipmentMethodTypeId()?has_content && shoppingCart.getCarrierPartyId()?has_content>
	                                  <#assign chosenShippingMethodDescription = shoppingCart.getCarrierPartyId() + " " + shoppingCart.getShipmentMethodType(0).description />
	                                  <#assign selectedStoreId = shoppingCart.getOrderAttribute("STORE_LOCATION")?if_exists />
	                                </#if>
	                              </#if>
	                            </#list><#-- end list of orderItemShipGroups -->
	                        </#if>
	                        <tr>
	                          <th class="caption"><label>${uiLabelMap.ShippingMethodLabel}</label></th>
	                          <td class="shippingMethod"><#if selectedStoreId?has_content>${uiLabelMap.StorePickupCaption} <#else> ${chosenShippingMethodDescription!""}</#if>
	                          </td>
	                        </tr>
	                        <#list headerAdjustmentsToShow as orderHeaderAdjustment>
	                                  <#assign adjustmentType = orderHeaderAdjustment.getRelatedOneCache("OrderAdjustmentType")>
	                                  <#assign productPromo = orderHeaderAdjustment.getRelatedOneCache("ProductPromo")!"">
	                                  <#assign promoCodeText = ""/>
	                                  <#if productPromo?has_content>
	                                     <#assign promoText = productPromo.promoText?if_exists/>
	                                     <#assign productPromoCode = productPromo.getRelatedCache("ProductPromoCode")>
	                                     <#if shoppingCartItems?has_content && (shoppingCartItems.size() &gt; 0)>
	                                       <#assign promoCodesEntered = shoppingCart.getProductPromoCodesEntered()!"">
	                                     <#else>
	                                       <#assign promoCodesEntered = localOrderReadHelper.getProductPromoCodesEntered()!""/>
	                                     </#if>
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
	                            <td class="value numberCol"><@ofbizCurrency amount=localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) rounding=2 isoCode=currencyUom/></td>
	                          </tr>
	                        </#list>
	                        <tr>
	                          <th class="caption"><label>${uiLabelMap.ShippingAndHandlingLabel}</label></th>
	                          <td class="value numberCol"><@ofbizCurrency amount=orderShippingTotal rounding=2 isoCode=currencyUom/></td>
	                        </tr>
	                        <tr>
	                          <th class="caption"><label>${uiLabelMap.SalesTaxLabel}</label></th>
	                          <td class="value numberCol"><@ofbizCurrency amount=orderTaxTotal rounding=2 isoCode=currencyUom/></td>
	                        </tr>
	                        <tr>
	                          <td></td>
	                        </tr>
	                        <tr>
	                          <th class="caption"><label>${uiLabelMap.TotalLabel}</label></th>
	                          <td class="total numberCol">
	                            <@ofbizCurrency amount=orderGrandTotal rounding=2 isoCode=currencyUom/>
	                          </td>
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
	          <#if !productCategoryId?has_content>
	              <#assign currentProductCategories = Static["org.ofbiz.product.product.ProductWorker"].getCurrentProductCategories(product)![]>
	              <#if currentProductCategories?has_content>
	                  <#assign productCategoryId = currentProductCategories[0].productCategoryId!"">
	              </#if>
	          </#if>
	          <#if product.isVariant?if_exists?upper_case == "Y">
	             <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(product.productId, delegator)?if_exists>
	             <#assign urlProductId=virtualProduct.productId>
		         <#assign productCategoryId = virtualProduct.primaryProductCategoryId!""/>
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
	
	          <#assign prodDescription = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "DESCRIPTION", locale, dispatcher)?if_exists>
	          <#if !prodDescription?has_content && virtualProduct?has_content>
	               <#assign prodDescription = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "DESCRIPTION", locale, dispatcher)?if_exists>
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
	         <#assign productUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${urlProductId}&productCategoryId=${productCategoryId!""}')/>
	
	        <#assign availability = uiLabelMap.InStockLabel>
	            <tr class="cart_contents">
	                <td class="image firstCol <#if !orderItem_has_next>lastRow</#if>" scope="row">
	                    <a href="${productUrl}" id="image_${urlProductId}">
	                        <img alt="${StringUtil.wrapString(productName)}" src="${productImageUrl}" class="productCartListImage" height="${IMG_SIZE_CART_H!""}" width="${IMG_SIZE_CART_W!""}">
	                    </a>
	                </td>
	                <td class="description <#if !orderItem_has_next>lastRow</#if>">
	                    <dl>
	                        <dt>${uiLabelMap.ProductDescriptionAttributesInfo}</dt>
	                        <dd class="description">
	                          <a href="${productUrl}">${StringUtil.wrapString(productName!)}</a>
	                        </dd>
	                        <#assign productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId" , orderItem.productId)) />
	                        <#if productFeatureAndAppls?has_content>
	                          <#list productFeatureAndAppls as productFeatureAndAppl>
	                            <#assign productFeature = productFeatureAndAppl.getRelatedOne("ProductFeatureCategory")?if_exists />
	                            <dd>${productFeature.description!}: ${productFeatureAndAppl.description!}</dd>
	                          </#list>
	                        </#if>
	                    </dl>
	                </td>
	                <td class="quantity <#if !orderItem_has_next>lastRow</#if>">
	                    ${orderItem.quantity?string.number}
	                </td>
	                <#if (useAvailability?has_content) && useAvailability == "Y" >
	                    <td class="availability <#if !orderItem_has_next>lastRow</#if>">
	                        ${availability}
	                    </td>
	                </#if>
	                <td class="price numberCol <#if !orderItem_has_next>lastRow</#if>">
	                    <ul title="Price Information">
	                        <li>
	                        <div id="priceelement">
	                            <ul>
	                                <li>
	                                    <span class="price"><@ofbizCurrency amount=displayPrice rounding=2 isoCode=currencyUom/></span>
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
	                                            <@ofbizCurrency amount=offerPrice rounding=2 isoCode=currencyUom/>
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
	                                    <span class="price"><@ofbizCurrency amount=localOrderReadHelper.getOrderItemSubTotal(orderItem,localOrderReadHelper.getAdjustments()) rounding=2 isoCode=currencyUom/></span>
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
