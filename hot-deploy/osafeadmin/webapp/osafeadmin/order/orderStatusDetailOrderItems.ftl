<table class="osafe">
    <tr class="heading">
        <th class="actionOrderCol firstCol selectOrderItem">
            <input type="checkbox" class="checkBoxEntry" name="orderItemSeqIdall" id="orderItemSeqIdall" value="Y" onclick="javascript:setCheckboxes('${detailFormName!""}','orderItemSeqId')"  <#if parameters.orderItemSeqIdall?has_content>checked</#if>/>
        </th>
        <th class="idCol">${uiLabelMap.ItemSeqNoLabel}</th>
        <th class="idCol">${uiLabelMap.ProductNoLabel}</th>
        <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
        <th class="statusCol">${uiLabelMap.ItemStatusLabel}</th>
        <th class="nameCol">${uiLabelMap.OrderQtyLabel}</th>
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
            <#assign isReturnable = itemProduct.returnable!"N">
            <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
            <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(itemProduct,request)>
            <#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
            <#assign orderItemPrice = (orderItem.unitPrice)*(orderItem.quantity)/>
            <#assign shippedQuantity = 0?number>
            <#assign orderItemShipments = delegator.findByAnd("OrderShipment", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId",orderItem.orderId, "orderItemSeqId", orderItem.orderItemSeqId))/>
            <#if orderItemShipments?has_content>
                <#list orderItemShipments as orderItemShipment>
                    <#assign shippedQuantity = shippedQuantity + orderItemShipment.quantity/>
                </#list>
            </#if>
            <#assign returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities()>
            
            <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
                  
	            <td class="actionOrderCol <#if !orderItem_has_next>lastRow</#if> firstCol">
	              <#if orderItem.statusId == "ITEM_APPROVED">
	                <div class="statusChangeOrderCheckbox">
	                  <input type="checkbox" class="checkBoxEntry" name="orderItemSeqId-${orderItem_index}" id="orderItemSeqId-${rowNo}" value="${orderItem.orderItemSeqId!}" <#if parameters.get("orderItemSeqId-${rowNo}")?has_content>checked</#if> onchange="javascript:getOrderRefundData();"/>
	                </div>
	              </#if>
	              
	              <#if orderItem.statusId == "ITEM_COMPLETED">
	                <#assign returnedQty = returnQuantityMap.get(orderItem.orderItemSeqId) />
	                <#if (returnedQty < orderItem.quantity) && (isReturnable == 'Y')>
	                  <div class="productReturnOrderCheckbox">
	                    <input type="checkbox" class="checkBoxEntry" name="orderItemSeqId-${orderItem_index}" id="orderItemSeqId-${rowNo}" value="${orderItem.orderItemSeqId!}" <#if parameters.get("orderItemSeqId-${rowNo}")?has_content>checked</#if> onchange="javascript:getOrderRefundData();"/>
	                  </div>
	                </#if>
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
                <td class="nameCol <#if !orderItem_has_next>lastRow</#if>">${orderItem.quantity!} </td>
                <td class="dollarCol <#if !orderItem_has_next>lastRow</#if>"><@ofbizCurrency amount = orderItemPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                <td class="qtyCol <#if !orderItem_has_next>lastRow</#if>">${returnedQty?default(0)}</td>
                <td class="qtyCol <#if !orderItem_has_next>lastRow</#if>">${shippedQuantity!}</td>
                
                <#-- RENDER FOR PRODUCT RETURN ONLY -->
                <td class="nameCol <#if !orderItem_has_next>lastRow</#if> returnItemData" style="display:none">
                  <div class="orderItemReturningQty">
                    <#if (orderItem.statusId == "ITEM_COMPLETED") && (returnedQty < orderItem.quantity) && (isReturnable == 'Y')>
                      <input type="hidden" value="${orderItem.unitPrice!}" class="small" name="returnPrice_${orderItem_index}" id="returnPrice_${orderItem_index}" />
                      
                      <#assign shipQuantity = parameters.get("shipQuantity_${orderItem_index}")!shippedQuantity!""/>
                      <input type="hidden" class="shipQuantity small" name="shipQuantity_${orderItem_index}" id="shipQuantity_${orderItem_index}" value="${shipQuantity!""}" />
                      <#assign returnQuantity = parameters.get("returnQuantity_${orderItem_index}")!""/>
                      <input type="text" class="small" name="returnQuantity_${orderItem_index}" id="returnQuantity_${orderItem_index}" value="${returnQuantity!""}" />
                    <#else>
                      &nbsp;
                    </#if>
                  </div>
                </td>
                
                <td class="nameCol returnItemData" style="display:none">
                  <div class="orderItemReturnReason">
                    <#if (orderItem.statusId == "ITEM_COMPLETED") && (returnedQty < orderItem.quantity) && (isReturnable == 'Y')>
                      <select name="returnReasonId_${orderItem_index}" id="returnReasonId_${orderItem_index}">
                        <#list returnReasons as reason>
                          <option value="${reason.returnReasonId}">${reason.description!reason.returnReasonId!}</option>
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