<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <a class="standardBtn action" href="<@ofbizUrl>eCommerceReOrderItems?orderId=${orderHeader.orderId}</@ofbizUrl>"><span>${uiLabelMap.ReOrderBtn}</span></a>
  </div>
</li>