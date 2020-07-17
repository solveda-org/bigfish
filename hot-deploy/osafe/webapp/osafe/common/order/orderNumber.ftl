<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.OrderNumberCaption}</label>
    <a href="<@ofbizUrl>eCommerceOrderDetail?orderId=${orderHeader.orderId}</@ofbizUrl>"><span>${orderHeader.orderId}</span></a>
  </div>
</li>