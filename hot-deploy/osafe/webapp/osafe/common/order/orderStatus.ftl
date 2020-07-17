<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.StatusCaption}</label>
    <span>${status.get("description",locale)}</span>
  </div>
</li>