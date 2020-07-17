<div class="changeOrderStatus">    
    <table class="osafe">
    <tr class="heading">
        <th class="actionOrderCol firstCol selectOrderItem">
            <input type="checkbox" class="checkBoxEntry" name="orderItemSeqIdall" id="orderItemSeqIdall" value="Y" onclick="javascript:setCheckboxes('${detailFormName!""}','orderItemSeqId')"  <#if parameters.orderItemSeqIdall?has_content>checked</#if>/>
        </th>
        <th class="itemCol">${uiLabelMap.ItemSeqNoLabel}</th>
        <th class="idCol">${uiLabelMap.ProductNoLabel}</th>
        <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
        <th class="statusCol">${uiLabelMap.ItemStatusLabel}</th>
        <th class="qtyCol">${uiLabelMap.OrderQtyLabel}</th>
        <th class="dollarCol">${uiLabelMap.ItemPriceLabel}</th>
        <th class="qtyCol">${uiLabelMap.ReturnedQtyLabel}</th>
        <th class="qtyCol">${uiLabelMap.ShippedQtyLabel}</th>
        
        <#-- RENDER FOR PRODUCT RETURN ONLY -->
        <th class="nameCol returnItemHead" style="display:none">${uiLabelMap.ReturningQtyLabel}</th>
        <th class="nameCol returnItemHead" style="display:none">${uiLabelMap.ReturnReasonLabel}</th>
    </tr>
    <#if orderItems?exists && orderItems?has_content>
        <#assign rowClass = "1">
        <#assign rowNo = 0/>
        <#list orderItems as orderItem>
            <#assign productId = orderItem.productId?if_exists>
            <#assign itemProduct = orderItem.getRelatedOne("Product")/>
            <#assign isReturnable = itemProduct.returnable!"">
            <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
            <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(itemProduct,request)>
            <#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
            <#if productName="">
                <#if itemProduct.isVariant?if_exists?upper_case == "Y">
                    <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(productId, delegator)?if_exists>
                    <#assign productName = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(virtualProduct, 'PRODUCT_NAME', request)?if_exists>
                </#if>
            </#if>
            <#assign orderItemPrice = (orderItem.unitPrice)*(orderItem.quantity)/>
            <#assign shippedQuantity = 0?number>
            <#assign orderItemShipments = delegator.findByAnd("OrderShipment", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId",orderItem.orderId, "orderItemSeqId", orderItem.orderItemSeqId))/>
            <#if orderItemShipments?has_content>
                <#list orderItemShipments as orderItemShipment>
                    <#if orderItemShipment.quantity?has_content>
                        <#assign shippedQuantity = shippedQuantity + orderItemShipment.quantity/>
                    </#if>
                </#list>
            </#if>
            <#assign returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities()>
            
            <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
                  
	            <td class="actionOrderCol <#if !orderItem_has_next>lastRow</#if> firstCol">
	              <#if (orderItem.statusId == "ITEM_APPROVED" || orderItem.statusId == "ITEM_HOLD")>
	                <div class="statusChangeOrderCheckbox">
	                  <input type="checkbox" class="orderItemSeqId checkBoxEntry" name="orderItemSeqId-${orderItem_index}" id="orderItemSeqId-${rowNo}" value="${orderItem.orderItemSeqId!}" <#if parameters.get("orderItemSeqId-${rowNo}")?has_content>checked</#if> onchange="javascript:getOrderRefundData();"/>
	                </div>
	              </#if>
	              <#if (orderItem.statusId != "ITEM_APPROVED" && orderItem.statusId != "ITEM_HOLD")>
	                  <div class="itemCancelCheckboxInfo" style="display:none">
	                      <#assign toolTipData = uiLabelMap.ItemsCancelInfo />
	                      <a onMouseover="showTooltip(event,'${toolTipData!}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a>
	                  </div>
	                  <div class="itemCompleteCheckboxInfo" style="display:none">
		                  <#assign toolTipData = uiLabelMap.ItemCompletedInfo />
		                  <a onMouseover="showTooltip(event,'${toolTipData!}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a>
	                  </div>
	              </#if>
	              
	              <#assign returnedQty = returnQuantityMap.get(orderItem.orderItemSeqId)?default(0) />
	              <#if orderItem.statusId == "ITEM_COMPLETED">
	                <#if (returnedQty < orderItem.quantity) && (isReturnable == "" || isReturnable.toUpperCase() != 'N')>
	                    <div class="productReturnOrderCheckbox">
	                        <input type="checkbox" class="orderItemSeqId checkBoxEntry" name="orderItemSeqId-${orderItem_index}" id="orderItemSeqId-${rowNo}" value="${orderItem.orderItemSeqId!}" <#if parameters.get("orderItemSeqId-${rowNo}")?has_content>checked</#if> onchange="javascript:getOrderRefundData();"/>
	                    </div>
	                </#if>
		            
		            <#if (returnedQty >= orderItem.quantity)>
		                <div class="productReturnCheckboxInfo" style="display:none">
		                    <#assign toolTipData = uiLabelMap.AllItemsReturnedInfo />
		                    <a onMouseover="showTooltip(event,'${toolTipData!}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a>
		                </div>
		            </#if>
		            <#if isReturnable!= "" && isReturnable.toUpperCase() == 'N'>
		                <div class="productReturnCheckboxInfo" style="display:none">
		                    <#assign toolTipData = uiLabelMap.ItemNotReturnableInfo />
		                    <a onMouseover="showTooltip(event,'${toolTipData!}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a>
		                </div>
	                </#if>
	              </#if>
	              <#if (orderItem.statusId != "ITEM_COMPLETED")>
	                  <div class="productReturnCheckboxInfo" style="display:none">
	                      <#assign toolTipData = uiLabelMap.ItemsNotCompletedInfo />
	                      <a onMouseover="showTooltip(event,'${toolTipData!}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a>
	                  </div>
	              </#if>
	            </td>
                
                <td class="itemCol <#if !orderItem_has_next>lastRow</#if>"><a href="<@ofbizUrl>orderItemDetail?orderId=${orderItem.orderId!}</@ofbizUrl>">${(orderItem.orderItemSeqId)!""}</a></td>
                <td class="idCol <#if !orderItem_has_next>lastRow</#if>">
                  <#if itemProduct?has_content && itemProduct.isVirtual == 'Y'>
                    <a href="<@ofbizUrl>virtualProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
                  <#elseif itemProduct?has_content && itemProduct.isVariant == 'Y'>
                    <a href="<@ofbizUrl>variantProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
                  <#elseif itemProduct?has_content && itemProduct.isVirtual == 'N' && itemProduct.isVariant == 'N'>
                    <a href="<@ofbizUrl>finishedProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
                  </#if>
                </td>
                <td class="nameCol <#if !orderItem_has_next>lastRow</#if>">${productName?if_exists}</td>
                <td class="statusCol <#if !orderItem_has_next>lastRow</#if>">${itemStatus.get("description",locale)}</td>
                <td class="qtyCol <#if !orderItem_has_next>lastRow</#if>">${orderItem.quantity!} </td>
                <td class="dollarCol <#if !orderItem_has_next>lastRow</#if>"><@ofbizCurrency amount = orderItemPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                <td class="qtyCol <#if !orderItem_has_next>lastRow</#if>">${returnedQty?default(0)}</td>
                <td class="qtyCol <#if !orderItem_has_next>lastRow</#if>">${shippedQuantity!}</td>
                
                <#-- RENDER FOR PRODUCT RETURN ONLY -->
                <td class="nameCol <#if !orderItem_has_next>lastRow</#if> returnItemData" style="display:none">
                  <div class="orderItemReturningQty">
                    <#if (orderItem.statusId == "ITEM_COMPLETED") && (returnedQty < orderItem.quantity) && (isReturnable == "" || isReturnable.toUpperCase() != 'N')>
                      <input type="hidden" value="${orderItem.unitPrice!}" class="small" name="returnPrice_${orderItem_index}" id="returnPrice_${orderItem_index}" />
                      
                      <#assign shipQuantity = parameters.get("shipQuantity_${orderItem_index}")!shippedQuantity!""/>
                      <#assign returnQuantity = parameters.get("returnQuantity_${orderItem_index}")!""/>
                      
                      <#assign remainingQuantity = shipQuantity?number - returnedQty?number />
                      <input type="hidden" class="shipQuantity small" name="shipQuantity_${orderItem_index}" id="shipQuantity_${orderItem_index}" value="${remainingQuantity!""}" />
                      <input type="text" class="small" name="returnQuantity_${orderItem_index}" id="returnQuantity_${orderItem_index}" value="${returnQuantity!""}" />
                    <#else>
                      &nbsp;
                    </#if>
                  </div>
                </td>
                
                <td class="nameCol returnItemData" style="display:none">
                  <div class="orderItemReturnReason">
                    <#if (orderItem.statusId == "ITEM_COMPLETED") && (returnedQty < orderItem.quantity) && (isReturnable== "" || isReturnable.toUpperCase() != 'N')>
                      <select name="returnReasonId_${orderItem_index}" id="returnReasonId_${orderItem_index}">
                        <#assign reasonId = parameters.get("returnReasonId_${orderItem_index}")!""/>
                        <#list returnReasons as reason>
                          <option value="${reason.returnReasonId}" <#if reasonId?has_content && reasonId == reason.returnReasonId>selected</#if>>${reason.description!reason.returnReasonId!}</option>
                        </#list>
                      </select>
                    </#if>
                  </div>
                </td>
                
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "2">
                <#assign rowClass = "1">
            <#else>
                <#assign rowClass = "2">
            </#if>
            <#assign rowNo = rowNo+1/>
        </#list>
    </#if>
    </table>    
</div>
<div class="completeMultiShipGroups" style="display:none">
    <table class="osafe">
        <tr class="heading">
	        <th class="actionOrderCol firstCol selectOrderItem">
	            <input type="checkbox" class="orderItemAndShipGroupSeqId checkBoxEntry" name="orderItemAndShipGroupSeqIdall" id="orderItemAndShipGroupSeqIdall" value="Y" onclick="javascript:setCheckboxes('${detailFormName!""}','orderItemAndShipGroupSeqId')"  <#if parameters.orderItemSeqIdallMulti?has_content>checked</#if>/>
	        </th>
	        <th class="itemCol">${uiLabelMap.ItemSeqNoLabel}</th>
	        <th class="idCol">${uiLabelMap.ProductNoLabel}</th>
	        <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
	        <th class="statusCol">${uiLabelMap.ItemStatusLabel}</th>
	        <th class="qtyCol">${uiLabelMap.OrderQtyLabel}</th>
	        <th class="dollarCol">${uiLabelMap.ItemPriceLabel}</th>
	        <th class="qtyCol">${uiLabelMap.ReturnedQtyLabel}</th>
	        <th class="qtyCol">${uiLabelMap.ShippedQtyLabel}</th>
	        <th class="qtyCol">${uiLabelMap.ShipQtyLabel}</th>
        </tr>
        <#if shipGroups?has_content>
            <#assign rowNo = 0/>
            <#list shipGroups as orderItemShipGroup>
                <input type="hidden" name="orderItemShipGroupSeqId_${orderItemShipGroup.shipGroupSeqId}" id="orderItemShipGroupSeqId_${orderItemShipGroup.shipGroupSeqId}" value="${orderItemShipGroup.shipGroupSeqId}"/>
                <#assign postalAddress = orderItemShipGroup.getRelatedOne("PostalAddress") />
                <tr class="dataRow">
                    <td class="actionOrderCol firstCol selectOrderItem">&nbsp;</td>
                    <td class="orderItemShippingAddress">
                        <span>${uiLabelMap.ShipGroupCaption} ${orderItemShipGroup.shipGroupSeqId}</span>
                    </td>
                    <td class="orderItemShippingAddress">
                        <span>${uiLabelMap.ShipToCaption}</span>
                    </td>
                    <td colspan="7" class="orderItemShippingAddress">
                        ${setRequestAttribute("PostalAddress", postalAddress)}
			            ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_FULL_ADDRESS")}
			            ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
                    </td>
                </tr>
                <#assign orderItemShipGroupAssocs = orderItemShipGroup.getRelated("OrderItemShipGroupAssoc") />
                <#if orderItemShipGroupAssocs?has_content>
                    <#list orderItemShipGroupAssocs as orderItemShipGroupAssoc>
                        <#assign orderItem = orderItemShipGroupAssoc.getRelatedOne("OrderItem")/>
                        <#assign productId = orderItem.productId?if_exists>
			            <#assign itemProduct = orderItem.getRelatedOne("Product")/>
			            <#assign isReturnable = itemProduct.returnable!"N">
			            <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
			            <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(itemProduct,request)>
			            <#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
			            <#if productName="">
			                <#if itemProduct.isVariant?if_exists?upper_case == "Y">
			                    <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(productId, delegator)?if_exists>
			                    <#assign productName = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(virtualProduct, 'PRODUCT_NAME', request)?if_exists>
			                </#if>
			            </#if>
			            <#assign orderItemPrice = (orderItem.unitPrice)*(orderItemShipGroupAssoc.quantity)/>
			            <#assign shippedQuantity = 0?number>
			            <#assign orderItemShipments = orderItem.getRelated("OrderShipment")/>
			            
			            <#if orderItemShipments?has_content>
			                <#list orderItemShipments as orderItemShipment>
			                    <#if orderItemShipment.quantity?has_content>
			                        <#assign shippedQuantity = shippedQuantity + orderItemShipment.quantity/>
			                    </#if>
			                </#list>
			            </#if>
			            
			            <#assign shippedGroupQuantity = 0?number>
			            <#assign orderItemShipmentsGroup = orderItemShipGroupAssoc.getRelated("OrderShipment")/>
			            
			            <#if orderItemShipmentsGroup?has_content>
			                <#list orderItemShipmentsGroup as orderItemShipmentGroup>
			                    <#assign shippedGroupQuantity = shippedGroupQuantity + orderItemShipmentGroup.quantity/>
			                </#list>
			            </#if>
			            
			            <#assign returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities()>
			            <#if shippedGroupQuantity == orderItemShipGroupAssoc.quantity>
			                <#assign orderItemShipGroupAssocStatus = "Completed"/>
			            <#elseif orderItemShipGroupAssoc.cancelQuantity?has_content && orderItemShipGroupAssoc.quantity == orderItemShipGroupAssoc.cancelQuantity>
			                <#assign orderItemShipGroupAssocStatus = "Cancelled"/>
			            <#else>
			                <#assign orderItemShipGroupAssocStatus = "Approved"/>
			            </#if>
			            <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
			                  
				            <td class="actionOrderCol firstCol">
				              <#if orderItemShipGroupAssocStatus == "Approved">
				                  <input type="checkbox" class="checkBoxEntry" name="orderItemAndShipGroupSeqId_${orderItemShipGroupAssoc.orderItemSeqId}_${orderItemShipGroupAssoc.shipGroupSeqId}" id="orderItemAndShipGroupSeqId_${orderItemShipGroupAssoc.orderItemSeqId}_${orderItemShipGroupAssoc.shipGroupSeqId}" value="${orderItem.orderItemSeqId!}" <#if parameters.get("orderItemAndShipGroupSeqId_${orderItemShipGroupAssoc.orderItemSeqId}_${orderItemShipGroupAssoc.shipGroupSeqId}")?has_content>checked</#if>/>
				              </#if>
				              <#if (orderItemShipGroupAssocStatus != "Approved")>
				                  <div class="itemCompleteCheckboxInfo" style="display:none">
					                  <#assign toolTipData = uiLabelMap.ItemCompletedInfo />
					                  <a onMouseover="showTooltip(event,'${toolTipData!}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a>
				                  </div>
	                          </#if>
				            </td>
				            
			                
			                <td class="itemCol"><a href="<@ofbizUrl>orderItemDetail?orderId=${orderItem.orderId!}</@ofbizUrl>">${(orderItem.orderItemSeqId)!""}</a></td>
			                <td class="idCol">
			                  <#if itemProduct?has_content && itemProduct.isVirtual == 'Y'>
			                    <a href="<@ofbizUrl>virtualProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
			                  <#elseif itemProduct?has_content && itemProduct.isVariant == 'Y'>
			                    <a href="<@ofbizUrl>variantProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
			                  <#elseif itemProduct?has_content && itemProduct.isVirtual == 'N' && itemProduct.isVariant == 'N'>
			                    <a href="<@ofbizUrl>finishedProductDetail?productId=${itemProduct.productId!}</@ofbizUrl>">${itemProduct.productId!"N/A"}</a>
			                  </#if>
			                </td>
			                <td class="nameCol">${productName?if_exists}</td>
			                <td class="statusCol">
			                    ${orderItemShipGroupAssocStatus!} 
			                </td>
			                <td class="qtyCol">
			                	${orderItemShipGroupAssoc.quantity!}
			                	<#if orderItemShipGroupAssocStatus == "Approved">
			                		<input type="hidden" class="orderedQuantity" name="orderedQuantity_${orderItemShipGroupAssoc.orderItemSeqId}" id="orderedQuantity_${orderItemShipGroupAssoc.orderItemSeqId}" value="${orderItemShipGroupAssoc.quantity!""}" />
			                	</#if>
			                </td>
			                <td class="dollarCol"><@ofbizCurrency amount = orderItemPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
			                <td class="qtyCol">${returnedQty?default(0)}</td>
			                <td class="qtyCol">${shippedGroupQuantity!}</td>
			                <td class="qtyCol">
			                    <#if orderItemShipGroupAssocStatus == "Approved">
			                        <#assign toShipQuantity = parameters.get("toShipQuantity_${orderItemShipGroupAssoc.orderItemSeqId}_${orderItemShipGroupAssoc.shipGroupSeqId}")!""/>
			                        <input type="text" class="small toShipQuantity" name="toShipQuantity_${orderItemShipGroupAssoc.orderItemSeqId}_${orderItemShipGroupAssoc.shipGroupSeqId}" id="toShipQuantity_${orderItemShipGroupAssoc.orderItemSeqId}_${orderItemShipGroupAssoc.shipGroupSeqId}" value="${toShipQuantity!}" />
			                    <#else>
			                        &nbsp;
			                    </#if>
			                </td>
			            </tr>
			            <#-- toggle the row color -->
			            <#if rowClass == "2">
			                <#assign rowClass = "1">
			            <#else>
			                <#assign rowClass = "2">
			            </#if>
			            <#assign rowNo = rowNo+1/>
                    </#list>
                </#if>
                <#assign rowNo = rowNo+1/>
            </#list>
        </#if>
    </table>
</div>