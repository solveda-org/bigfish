<!-- start listBox -->
        <table class="osafe">
            <tr class="heading">
		        <th class="seqCol">${uiLabelMap.ItemSeqIdLabel}</th>
                <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="itemCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="statusCol">${uiLabelMap.ItemStatusLabel}</th>
                <th class="dollarCol">${uiLabelMap.QtyLabel}</th>
                <th class="dollarCol">${uiLabelMap.UnitPriceLabel}</th>
                <th class="dollarCol">${uiLabelMap.OfferPriceLabel}</th>
                <th class="dollarCol">${uiLabelMap.AdjustAmountLabel}</th>
                <th class="dollarCol total lastCol">${uiLabelMap.ItemTotalLabel}</th>
            </tr>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1"/>
                <#assign orderAdjustments = orderReadHelper.getAdjustments()>
                <#assign orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments()>
                <#assign headerAdjustmentsToShow = orderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false)/>
                <#assign orderSubTotal = orderReadHelper.getOrderItemsSubTotal()>
                <#assign currencyUomId = orderReadHelper.getCurrency()>
                <#assign otherAdjAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false)>
                <#assign shippingAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getAllOrderItemsAdjustmentsTotal(resultList, orderAdjustments, false, false, true)>
                <#assign shippingAmount = shippingAmount.add(Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true))>
                <#assign taxAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal>
                <#assign grandTotal = orderReadHelper.getOrderGrandTotal()/>
            <#list resultList as orderItem>
                <#assign hasNext = orderItem_has_next/>
                <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                <#assign productId = orderItem.productId?if_exists>
                <#assign itemProduct = orderItem.getRelatedOne("Product")/>
                <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
                <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                <#assign itemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)>
                <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(itemProduct,request)>
                <#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
                <#if productName="">
                	<#if itemProduct.isVariant?if_exists?upper_case == "Y">
                       	<#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(productId, delegator)?if_exists>
                   	</#if>
                   	<#assign productName = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(virtualProduct, 'PRODUCT_NAME', request)?if_exists>
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
                         </#if>
                      </#if>
                    </#list>
                </#if>
            <tbody>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">

		            <td class="seqCol <#if !hasNext>lastRow</#if>">${(orderItem.orderItemSeqId)!""}</td>
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
                    <td class="statusCol <#if !hasNext>lastRow</#if>">${itemStatus.get("description",locale)}</td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>">${orderItem.quantity?string.number}</td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>"><#if (itemPromoAdjustment < 0)><a onMouseover="showTooltip(event,'${toolTipData}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a><@ofbizCurrency amount=offerPrice isoCode=currencyUomId/></#if></td>
                    <td class="dollarCol <#if !hasNext>lastRow</#if>"><@ofbizCurrency amount=orderReadHelper.getOrderItemAdjustmentsTotal(orderItem) isoCode=currencyUomId/></td>
                    <td class="dollarCol total <#if !hasNext>lastRow</#if>"><@ofbizCurrency amount=orderReadHelper.getOrderItemSubTotal(orderItem,orderReadHelper.getAdjustments()) isoCode=currencyUomId/></td>
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
                <td colspan="10">
                    <table class="osafe orderSummary">
                        <tr>
                          <td class="totalCaption"><label>${uiLabelMap.SubtotalCaption}</label></td>
                          <td class="totalValue"><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></td>
                        </tr>
                        <#list headerAdjustmentsToShow as orderHeaderAdjustment>
                                  <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
                                  <#assign productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo")!"">
                                  <#if productPromo?has_content>
                                     <#assign promoText = productPromo.promoText?if_exists/>
                                     <#assign proomoCodeText = "" />
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
                                  </#if>
                          <tr>
                            <td class="totalCaption"><label><#if promoText?has_content>${promoText!""} <#if promoCodeText?has_content><a href="<@ofbizUrl>promotionCodeDetail?productPromoCodeId=${promoCodeText}</@ofbizUrl>">(${promoCodeText!})</a></#if><#else>${adjustmentType.get("description",locale)?if_exists}</#if>:</label></td>
                            <td class="totalValue"><@ofbizCurrency amount=orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/></td>
                          </tr>
                        </#list>
                        <tr>
                          <td class="totalCaption"><label>${uiLabelMap.ShipHandleCaption}</label></td>
                          <td class="totalValue"><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/></td>
                        </tr>
                        <#if (!Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO")) || (taxAmount?has_content && (taxAmount &gt; 0))>
                            <tr>
                              <td class="totalCaption"><label><#if (taxAmount?default(0)> 0)>${uiLabelMap.TaxTotalCaption}<#else>${uiLabelMap.SalesTaxCaption}</#if></label></td>
                              <td class="totalValue"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId/></td>
                            </tr>
                        </#if>
                        <tr>
                          <td class="totalCaption total"><label>${uiLabelMap.OrderTotalCaption}</label></td>
                          <td class="totalValue total">
                            <@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>
                          </td>
                        </tr>
                    </table>
                </td>
           </tr>
          </tfoot>
        <#else>
            <tbody>
                <tr>
                    <td colspan="9" class="boxNumber">${uiLabelMap.NoDataAvailableInfo}</td>
                </tr>
            </tbody>
        </#if>
        </table>

<!-- end listBox -->