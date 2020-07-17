<!-- start orderDetailGeneralInfo.ftl -->
<div class="infoRow firstRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.OrderNoCaption}</label>
     </div>
     <div class="infoValue medium">
       <#if orderHeader?has_content>${orderHeader.orderId!""}</#if>
     </div>
     <div class="infoValue">
      <label>${uiLabelMap.OrderStatusCaption}</label>
     </div>
     <div class="infoValue">
        <#if orderHeader?has_content>
            <#assign statusItem = orderHeader.getRelatedOneCache("StatusItem")>
            ${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}
            <#if orderStatusChangeBtnVisible?has_content && orderStatusChangeBtnVisible =="Y" && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_REJECTED" && orderHeader.statusId != "ORDER_COMPLETED">
                <a href="<@ofbizUrl>${orderStatusChangeAction}?orderId=${orderHeader.orderId!}</@ofbizUrl>" class="standardBtn secondary">${uiLabelMap.ChangeBtn}</a>
            </#if>
        </#if>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.OrderDateCaption}</label>
     </div>
     <div class="infoValue medium">
        <#if orderHeader?has_content>
            ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(orderHeader.orderDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
        </#if>
     </div>
     <div class="infoValue">
      <label>${uiLabelMap.ExportStatusCaption}</label>
     </div>
     <div class="infoValue">
        <#if orderHeader?has_content>
          <#assign orderAttribute = delegator.findOne("OrderAttribute", {"orderId" : orderHeader.orderId, "attrName" : "IS_DOWNLOADED"}, true)!"" />
          <#if orderAttribute?has_content>
            <#assign downloadStatus = orderAttribute.attrValue!"">
          </#if>
            <#--assign downloadStatus = orderHeader.isDownloaded!""-->
            <#if downloadStatus?has_content && downloadStatus == "Y">
               ${uiLabelMap.ExportStatusInfo}
            <#else>
               ${uiLabelMap.DownloadNewInfo}
            </#if>
        </#if>
     </div>
   </div>
</div>

<!-- end orderDetailGeneralInfo.ftl -->


