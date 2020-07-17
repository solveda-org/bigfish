<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
    <div>
      <label>${uiLabelMap.LastOrderDateCaption}</label>
      <span>${(Static["com.osafe.util.Util"].convertDateTimeFormat(orderDate, preferredDateFormat))!"N/A"}</span>
    </div>
</li>