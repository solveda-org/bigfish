<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.CartItemNameCaption}</label>
	<a href="${productFriendlyUrl}" id="image_${urlProductId}">
	  <span>${wrappedProductName}</span>
	</a>
  </div>
</li>