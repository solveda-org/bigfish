<!-- start listBox -->
        <table class="osafe">
            <tr class="heading">
		        <th class="seqCol">${uiLabelMap.ItemSeqIdLabel}</th>
                <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="itemCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="statusCol">${uiLabelMap.ItemStatusLabel}</th>
                <th class="dollarCol">${uiLabelMap.QtyLabel}</th>
                <th class="dollarCol">${uiLabelMap.ReturnQtyLabel}</th>
                <th class="dollarCol">${uiLabelMap.CancelQtyLabel}</th>
                <th class="dollarCol">${uiLabelMap.UnitPriceLabel}</th>
                <th class="dollarCol">${uiLabelMap.OfferPriceLabel}</th>
                <th class="dollarCol">${uiLabelMap.AdjustAmountLabel}</th>
                <th class="dollarCol total lastCol">${uiLabelMap.ItemTotalLabel}</th>
            </tr>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1"/>
                <#assign currencyUomId = orderReadHelper.getCurrency()>
                <#assign orderValidItems = orderReadHelper.getOrderItems()/>
                <#assign orderAdjustments = orderReadHelper.getAdjustments()>
                <#assign orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments()>
                <#assign otherOrderHeaderAdjustments = orderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false)/>
                <#assign orderSubTotal = orderReadHelper.getOrderItemsSubTotal(orderValidItems,orderAdjustments)/>
                <#assign otherOrderHeaderAdjustmentsList = Static["javolution.util.FastList"].newInstance()/>
                <#list otherOrderHeaderAdjustments as orderHeaderAdjustment>
                  <#if orderHeaderAdjustment.orderAdjustmentTypeId != "LOYALTY_POINTS" && !orderHeaderAdjustment.productPromoId?has_content>
        		    <#assign changed = otherOrderHeaderAdjustmentsList.add(orderHeaderAdjustment)/>
        		   </#if>
        		</#list>
                <#assign otherAdjustmentsAmount = orderReadHelper.calcOrderAdjustments(otherOrderHeaderAdjustmentsList, orderSubTotal, true, false, false)/>
                <#assign shippingAmount = orderReadHelper.calcOrderAdjustments(orderAdjustments, orderSubTotal, false, false, true)> 
                <#assign taxAmount = orderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal!"0.00"/>
                <#assign grandTotal = orderReadHelper.getOrderGrandTotal(orderValidItems, orderAdjustments)/>
                <#assign returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities()>
            <#list resultList as orderItem>
                <#assign hasNext = orderItem_has_next/>
                <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                <#assign productId = orderItem.productId?if_exists>
                <#assign itemProduct = orderItem.getRelatedOne("Product")/>
                <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
                <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                <#assign returnedQty = returnQuantityMap.get(orderItem.orderItemSeqId)?default(0) />
                <#assign itemAdjustment = orderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)>
                <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(itemProduct,request)>
                <#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
                <#if productName="">
                	<#if itemProduct.isVariant?if_exists?upper_case == "Y">
                       	<#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(productId, delegator)?if_exists>
                        <#assign productName = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(virtualProduct, 'PRODUCT_NAME', request)?if_exists>
                   	</#if>
                </#if>
                <!-- offer price from promo -->
                <#assign itemPromoAdjustment = (orderReadHelper.getOrderItemAdjustmentsTotal(orderItem, true, false, false)/orderItem.quantity)/>
                <#assign offerPrice = orderItem.unitPrice + itemPromoAdjustment/>
                <#assign orderItemAdjustments = orderReadHelper.getOrderItemAdjustments(orderItem) />
                <#if orderItemAdjustments?has_content>
                	<#list orderItemAdjustments as orderItemAdjustment>
                		<#assign productPromo = orderItemAdjustment.getRelatedOne("ProductPromo")!"">
                        <#if productPromo?has_content>
                         <#assign promoText = productPromo.promoText?if_exists/>
                         <#assign productPromoCode = productPromo.getRelated("ProductPromoCode")>
                         <#assign promoCodesEntered = orderReadHelper.getProductPromoCodesEntered()!""/>
                         <#if promoCodesEntered?has_content>
                            <#list promoCodesEntered as promoCodeEntered>
                              <#if productPromoCode?has_content>
                                <#list productPromoCode as promoCode>
                                  <#assign promoCodeEnteredId = promoCodeEntered/>
                                  <#assign promoCodeId = promoCode.productPromoCodeId!""/>
                                  <#if promoCodeEnteredId?has_content>
                                      <#if promoCodeId == promoCodeEnteredId>
                                      <#assign promoCodeText = promoCode.productPromoCodeId?if_exists/>
                                      <#assign toolTipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "OfferPriceHelperInfo", Static["org.ofbiz.base.util.UtilMisc"].toList(promoCodeText!""), locale)/>
                                      </#if>
                                  </#if>
                                </#list>
                              </#if>
                             </#list>
						 <#else>
						     <#assign promoCodeText = productPromo.promoName?if_exists/>
							 <#assign toolTipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "OfferPriceHelperInfo", Static["org.ofbiz.base.util.UtilMisc"].toList(promoCodeText!""), locale)/>
                         </#if>
                      </#if>
                    </#list>
                </#if>
                <!-- Tracking URL for each order item-->
                <#if orderItem.orderId?exists && orderItem.orderId?has_content && shipGroupsSize != 1 >
                    <#assign trackingURL = ""/>
                    <#assign shipGroupAssocs = delegator.findByAnd("OrderItemShipGroupAssoc", {"orderId": orderItem.orderId, "orderItemSeqId": orderItem.orderItemSeqId})/>
                    <#if shipGroupAssocs?has_content>
                    	<#assign shipGroupAssoc = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipGroupAssocs)/>
                    </#if>
                    <#if shipGroupAssoc?exists && shipGroupAssoc?has_content>
                        <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("OrderItemShipGroup", {"orderId": orderItem.orderId, "shipGroupSeqId": shipGroupAssoc.shipGroupSeqId}))/> 
                        <#if shipGroup?has_content>
                          <#assign orderHeader = delegator.findByPrimaryKey("OrderHeader", {"orderId": orderItem.orderId})/>
                          <#if orderHeader?has_content && (orderHeader.statusId == "ORDER_COMPLETED" || orderItem.statusId == "ITEM_COMPLETED") >
                              <#assign trackingNumber = shipGroup.trackingNumber!""/>
                              <#if (shipGroup.carrierPartyId?has_content && shipGroup.carrierPartyId != "_NA_")>
                                  <#assign trackingURLPartyContents = delegator.findByAnd("PartyContent", {"partyId": shipGroup.carrierPartyId, "partyContentTypeId": "TRACKING_URL"})/>
                                  <#if trackingURLPartyContents?has_content>
                                      <#assign trackingURLPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(trackingURLPartyContents)/>
                                      <#if trackingURLPartyContent?has_content>
                                          <#assign content = trackingURLPartyContent.getRelatedOne("Content")/>
                                          <#if content?has_content>
                                              <#assign dataResource = content.getRelatedOne("DataResource")!""/>
                                              <#if dataResource?has_content>
                                                  <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
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
                </#if>
            <tbody>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">

		            <td class="seqCol <#if !hasNext>lastRow</#if>"><a href="<@ofbizUrl>orderItemDetail?orderId=${orderItem.orderId!}</@ofbizUrl>">${(orderItem.orderItemSeqId)!""}</a></td>
                    <td class="idCol <#if !hasNext>lastRow</#if> firstCol">
                      <#if itemProduct?has_content && itemProduct.isVirtual == 'Y'>
                        <a href="<@ofbizUrl>virtualProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
                      <#elseif itemProduct?has_content && itemProduct.isVariant == 'Y'>
                        <a href="<@ofbizUrl>variantProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
                      <#elseif itemProduct?has_content && itemProduct.isVirtual == 'N' && itemProduct.isVariant == 'N'>
                        <a href="<@ofbizUrl>finishedProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
                      </#if>
                    </td>
                    <td class="itemCol <#if !hasNext>lastRow</#if>">
                      <#assign product = orderItem.getRelatedOne("Product") />${(product.internalName)!""}
                    </td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${productName?if_exists}</td>
                    <td class="statusCol <#if !hasNext>lastRow</#if>">
                      ${itemStatus.get("description",locale)}
                      <#if orderItem.statusId == "ITEM_COMPLETED">
                          <a href="<@ofbizUrl>orderShippingDetail?orderId=${parameters.orderId}</@ofbizUrl>"><span class="shipmentDetailIcon"></span></a>
                          <#-- <a href="JavaScript:newPopupWindow('${trackingURL!""}');"><span class="shipmentDetailIcon"></span></a> -->
                      </#if>
                    </td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>">${orderItem.quantity?string.number}</td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>">${returnedQty?string.number}</td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>">${orderItem.cancelQuantity?default(0)?string.number}</td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>"><@ofbizCurrency amount=orderItem.unitPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>"><#if (itemPromoAdjustment < 0)><a onMouseover="showTooltip(event,'${toolTipData!}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a><@ofbizCurrency amount=offerPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></#if></td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>"><@ofbizCurrency amount=orderReadHelper.getOrderItemAdjustmentsTotal(orderItem) rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                    <td class="dollarCol total <#if !hasNext>lastRow</#if>">
                            <@ofbizCurrency amount=orderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/>
                    </td>
                </tr>

                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
         </tbody>
         <tfoot>
            <tr>
                <td colspan="12">
                    <table class="osafe orderSummary">
                        <tr>
                          <td class="totalCaption"><label>${uiLabelMap.SubtotalCaption}</label></td>
                          <td class="totalValue"><@ofbizCurrency amount=orderSubTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                        </tr>
                        <#list otherOrderHeaderAdjustments as orderHeaderAdjustment>
                          <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
                          <#assign productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo")!"">
                          <#if productPromo?has_content>
                             <#assign promoText = productPromo.promoText?if_exists/>
                             <#assign promoCodeText = "" />
                             <#assign productPromoCode = productPromo.getRelated("ProductPromoCode")>
                             <#assign promoCodesEntered = orderReadHelper.getProductPromoCodesEntered()!""/>
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
	                         <tr>
	                            <#assign orderAdjTotal = orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)!0/>
	                            <td class="totalCaption"><label><#if promoText?has_content>${promoText!""} <#if promoCodeText?has_content><a href="<@ofbizUrl>promotionCodeDetail?productPromoCodeId=${promoCodeText!}</@ofbizUrl>">(${promoCodeText!})</a></#if><#else>${adjustmentType.get("description",locale)?if_exists}</#if>:</label></td>
	                            <td class="totalValue"><@ofbizCurrency amount=orderAdjTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
	                         </tr>
                          </#if>
                        </#list>
                        <#list otherOrderHeaderAdjustments as orderHeaderAdjustment>
                          <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")/>
                          <#assign loyaltyOrderAdjustmentTypeId = adjustmentType.orderAdjustmentTypeId/>
                          <#if loyaltyOrderAdjustmentTypeId == "LOYALTY_POINTS">
	                           <tr>
	                            <#assign orderAdjTotal = orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)!0/>
	                            <td class="totalCaption"><label>${adjustmentType.get("description",locale)?if_exists}:</label></td>
	                            <td class="totalValue"><@ofbizCurrency amount=orderAdjTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
	                          </tr>
                          </#if>
                        </#list>
                        <tr>
                          <td class="totalCaption"><label>${uiLabelMap.ShipHandleCaption}</label></td>
                          <td class="totalValue"><@ofbizCurrency amount=shippingAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                        </tr>
                        <#if (!Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO")) || (taxAmount?has_content && (taxAmount &gt; 0))>
                          <#if !Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_SHOW_SALES_TAX_MULTI")>
                            <tr>
					      	  <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", totalTaxPercent)>
					          <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxCaption",taxInfoStringMap, locale ) />
                              <td class="totalCaption"><label><#if shipGroupSalesTaxSame>${salesTaxCaption!}<#else>${uiLabelMap.SummarySalesTaxShortCaption!}</#if></label></td>
                              <td class="totalValue"><@ofbizCurrency amount=taxAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                            </tr>
                          <#else>
                            <#if appliedTaxList?exists && appliedTaxList?has_content && shipGroupSalesTaxSame>
						      <#list appliedTaxList as appliedTax >  
						        <tr>
						          <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", appliedTax.sourcePercentage, "description", appliedTax.description)>
						             <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxMultiCaption",taxInfoStringMap, locale ) />
	                              <td class="totalCaption"><label>${salesTaxCaption!}</label></td>
	                              <td class="totalValue"><@ofbizCurrency amount=appliedTax.amount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
	                            </tr>
						      </#list>
						    <#else>
						      <tr>
					      	    <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", totalTaxPercent)>
					            <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxCaption",taxInfoStringMap, locale ) />
                                <td class="totalCaption"><label><#if shipGroupSalesTaxSame>${salesTaxCaption!}<#else>${uiLabelMap.SummarySalesTaxShortCaption!}</#if></label></td>
                                <td class="totalValue"><@ofbizCurrency amount=taxAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                              </tr>
						    </#if>
                          </#if>
                        </#if>
                        <tr>
				          <td class="totalCaption"><label>${uiLabelMap.AdjustmentsCaption}</label></td>
				          <td class="totalValue"><@ofbizCurrency amount=otherAdjustmentsAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
				        </tr>
                        <tr>
                          <td class="totalCaption total"><label>${uiLabelMap.OrderTotalCaption}</label></td>
                          <td class="totalValue total">
                            <@ofbizCurrency amount=grandTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/>
                          </td>
                        </tr>
                    </table>
                </td>
           </tr>
          </tfoot>
        <#else>
            <tbody>
                 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
            </tbody>
        </#if>
        </table>

<!-- end listBox -->