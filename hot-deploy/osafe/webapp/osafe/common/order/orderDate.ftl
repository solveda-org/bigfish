<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.OrderDateCaption}</label>
    <span>${(Static["com.osafe.util.Util"].convertDateTimeFormat(orderHeader.orderDate, FORMAT_DATE))!"N/A"}</span>
  </div>
</li>