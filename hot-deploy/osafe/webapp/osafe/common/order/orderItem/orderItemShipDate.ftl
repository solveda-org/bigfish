<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
   <div>
      <label>${uiLabelMap.ShipDateCaption}</label>
      <#if shipDate?exists && shipDate?has_content>
	      <span>${(Static["com.osafe.util.Util"].convertDateTimeFormat(shipDate, FORMAT_DATE))!"N/A"}</span>
	  </#if>
   </div>
</li>