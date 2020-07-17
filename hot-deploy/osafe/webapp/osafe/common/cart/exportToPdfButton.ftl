<div class="${request.getAttribute("attributeClass")!}">
 <#if completedOrderId?exists && completedOrderId?has_content>
  <a href="<@ofbizUrl>EcommerceOrder.pdf?orderId=${completedOrderId!}</@ofbizUrl>" target="${uiLabelMap.ExportToPDFLabel}" class="standardBtn action" >
    <span>${uiLabelMap.ExportToPDFLabel}</span>
  </a>
 </#if>
</div>