<!-- start listBox -->
        <table class="osafe">
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="statusCol">${uiLabelMap.ItemStatusLabel}</th>
                <th class="dollarCol">${uiLabelMap.QtyLabel}</th>
                <th class="dollarCol">${uiLabelMap.UnitPriceLabel}</th>
                <th class="dollarCol">${uiLabelMap.AdjustAmountLabel}</th>
                <th class="dollarCol total lastCol">${uiLabelMap.ItemTotalLabel}</th>
            </tr>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
                <#assign orderAdjustments = orderReadHelper.getAdjustments()>
                <#assign orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments()>
                <#assign headerAdjustmentsToShow = orderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false)/>
                <#assign orderSubTotal = orderReadHelper.getOrderItemsSubTotal()>
                <#assign currencyUomId = orderReadHelper.getCurrency()>
                <#assign otherAdjAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false)>
                <#assign shippingAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getAllOrderItemsAdjustmentsTotal(resultList, orderAdjustments, false, false, true)>
                <#assign shippingAmount = shippingAmount.add(Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true))>
                <#assign taxAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal>
                <#assign grandTotal = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderGrandTotal(resultList, orderAdjustments)>
            <#list resultList as orderItem>
                <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                <#assign productId = orderItem.productId?if_exists>
                <#assign itemProduct = orderItem.getRelatedOne("Product")/>
                <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
                <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                <#assign itemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)>
                <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(itemProduct,request)>
                <#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
                <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
                    <td class="idCol <#if !orderItem_has_next>lastRow</#if> firstCol"><a href="<@ofbizUrl>productDetail?productId=${itemProduct.productId?if_exists}</@ofbizUrl>">${itemProduct.internalName!itemProduct.productId!"N/A"}</a></td>
                    <td class="itemCol <#if !orderItem_has_next>lastRow</#if> firstCol">
                      <#assign product = orderItem.getRelatedOne("Product") />${(product.internalName)!""}
                    </td>
                    <td class="nameCol <#if !orderItem_has_next>lastRow</#if>">${productName?if_exists} ${itemProduct.productId}</td>
                    <td class="statusCol <#if !orderItem_has_next>lastRow</#if>">${itemStatus.get("description",locale)}</td>
                    <td class="dollarCol <#if !orderItem_has_next>lastRow</#if>">${orderItem.quantity?string.number}</td>
                    <td class="dollarCol <#if !orderItem_has_next>lastRow</#if>"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></td>
                    <td class="dollarCol <#if !orderItem_has_next>lastRow</#if>"><@ofbizCurrency amount=orderReadHelper.getOrderItemAdjustmentsTotal(orderItem) isoCode=currencyUomId/></td>
                    <td class="dollarCol total <#if !orderItem_has_next>lastRow</#if>"><@ofbizCurrency amount=orderReadHelper.getOrderItemSubTotal(orderItem,orderReadHelper.getAdjustments()) isoCode=currencyUomId/></td>
                </tr>

                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
         <tfoot>
            <tr>
                <td colspan="8">
                    <table class="osafe orderSummary">
                        <tr>
                          <td class="totalCaption"><label>${uiLabelMap.SubtotalCaption}</label></td>
                          <td class="totalValue"><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></td>
                        </tr>
                        <#list headerAdjustmentsToShow as orderHeaderAdjustment>
                                  <#assign adjustmentType = orderHeaderAdjustment.getRelatedOneCache("OrderAdjustmentType")>
                                  <#assign productPromo = orderHeaderAdjustment.getRelatedOneCache("ProductPromo")!"">
                                  <#if productPromo?has_content>
                                     <#assign promoText = productPromo.promoText?if_exists/>
                                     <#assign proomoCodeText = "" />
                                     <#assign productPromoCode = productPromo.getRelatedCache("ProductPromoCode")>
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
                            <td class="totalCaption"><label><#if promoText?has_content>${promoText!""} <#if promoCodeText?has_content><a href="<@ofbizUrl>promotionCodeDetail?productPromoCodeId=${promoCodeText}</@ofbizUrl>">(${promoCodeText!})</a></#if><#else>${adjustmentType.get("description",locale)?if_exists}</#if>:</label></td>
                            <td class="totalValue"><@ofbizCurrency amount=orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/></td>
                          </tr>
                        </#list>
                        <tr>
                          <td class="totalCaption"><label>${uiLabelMap.ShipHandleCaption}</label></td>
                          <td class="totalValue"><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/></td>
                        </tr>
                        <tr>
                          <td class="totalCaption"><label><#if (taxAmount?default(0)> 0)>${uiLabelMap.TaxTotalCaption}<#else>${uiLabelMap.SalesTaxCaption}</#if></label></td>
                          <td class="totalValue"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId/></td>
                        </tr>
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
                    <td colspan="7" class="boxNumber">${uiLabelMap.NoDataAvailableInfo}</td>
                </tr>
            </tbody>
        </#if>
        </table>

<!-- end listBox -->