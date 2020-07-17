<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
	    <div>
	      <label>${uiLabelMap.QuantityCaption}</label>
	      <span>${shipQty?if_exists?string.number}</span>
	    </div>
</li>