<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
    <div>
      <label>${uiLabelMap.CarrierCaption}</label>
      <span>${carrierPartyGroupName!} ${shipMethodDescription!}</span>
    </div>
</li>