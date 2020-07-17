<div class="${request.getAttribute("attributeClass")!}">
  <#if orderHeaderList?has_content>
      <a class="standardBtn action" href="<@ofbizUrl>eCommerceReOrderItems</@ofbizUrl>"><span>${uiLabelMap.ReOrderItemsBtn}</span></a>
  </#if>
</div>
