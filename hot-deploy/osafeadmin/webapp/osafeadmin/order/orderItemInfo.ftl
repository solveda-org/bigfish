<!-- start orderItemDetailInfo.ftl -->
<div class="header"><h2>${orderItemBoxHeading!}</h2></div>
<div class="boxBody">
<div class="infoRow firstRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ItemSeqNoCaption}</label>
     </div>
     <div class="infoValue medium">
       <#if orderItem?has_content>${orderItem.orderItemSeqId!""}</#if>
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.QuantityUpperCaption}</label>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ProductNoCaption}</label>
     </div>
     <div class="infoValue medium">
        <#if orderItem?has_content>${orderItem.productId!""}</#if>
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.OrderOrderedCaption}</label>
     </div>
     <div class="infoValue">
        <#if orderItem?has_content>${orderItem.quantity!"0"}</#if>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ProductNameCaption}</label>
     </div>
     <div class="infoValue medium">
        <#if orderItem?has_content>${orderItem.itemDescription!""}</#if>
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.OrderCancelledCaption}</label>
     </div>
     <div class="infoValue">
        ${itemCancelledAmmount!}
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ItemStatusCaption}</label>
     </div>
     <div class="infoValue medium">
        <#if statusItem?has_content>${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}</#if>
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.PickedQtyCaption}</label>
     </div>
     <div class="infoValue">
        ${pickedQty?default(0)?string.number}
        <a href="#TODO" >[${uiLabelMap.SeePickListLabel}]</a>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>&nbsp;</label>
     </div>
     <div class="infoValue medium">
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.PackedQtyCaption}</label>
     </div>
     <div class="infoValue">
        ${pickedQty?default(0)?string.number}
        <a href="#TODO" >[${uiLabelMap.SeePackingDetailsLabel}]</a>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ListPriceCaption}</label>
     </div>
     <div class="infoValue medium">
        <#if orderItem?has_content><@ofbizCurrency amount=orderItem.unitListPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></#if>
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.OrderReturnedCaption}</label>
     </div>
     <div class="infoValue">
        <#if statusItem?has_content>${returnQuantityMap.get(orderItem.orderItemSeqId)?default(0)}</#if>
        <a href="#TODO">[${uiLabelMap.SeeReturnsLabel}]</a>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.SalePriceCaption}</label>
     </div>
     <div class="infoValue medium">
        <#if orderItem?has_content><@ofbizCurrency amount=orderItem.unitPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></#if>
     </div>
     <div class="infoCaption">
      <label>&nbsp;</label>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.AdjustmentsCaption}</label>
     </div>
     <div class="infoValue medium">
        <@ofbizCurrency amount=orderReadHelper.getOrderItemAdjustmentsTotal(orderItem) rounding=globalContext.currencyRounding isoCode=currencyUomId/>
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.ShipGroupCaption}</label>
     </div>
     <div class="infoValue">
     	<span><#if shipGroupAssoc?has_content>${shipGroupAssoc.quantity}</#if></span>
     	<a href="<@ofbizUrl>orderShippingDetail?orderId=${orderId}</@ofbizUrl>" ><#if shipGroup?has_content>[${shipGroup.shipGroupSeqId}]</#if></a>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.OrderItemSubtotalCaption}</label>
     </div>
     <div class="infoValue medium">
        <#if orderItem.statusId != "ITEM_CANCELLED">
            <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) rounding=globalContext.currencyRounding isoCode=currencyUomId/>
        <#else>
            <@ofbizCurrency amount=0.00 rounding=globalContext.currencyRounding isoCode=currencyUomId/>
        </#if>
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.ShipmentPlannedCaption}</label>
     </div>
     <div class="infoValue">
     	<span><#if orderShipment?has_content>${orderShipment.quantity!}</#if></span>
     	<a href="#TODO" ><#if orderShipment?has_content>[${orderShipment.shipmentId!}]</#if></a>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>&nbsp;</label>
     </div>
     <div class="infoValue medium">
        
     </div>
     <div class="infoCaption">
      <label>${uiLabelMap.ShipmentIssuedCaption}</label>
     </div>
     <div class="infoValue">
        <span><#if itemIssuance?has_content>${itemIssuance.quantity!}</#if></span>
     	<a href="#TODO" ><#if itemIssuance?has_content>[${itemIssuance.shipmentId!}]</#if></a>
     </div>
   </div>
</div>
</div>
<!-- end orderItemDetailInfo.ftl -->


