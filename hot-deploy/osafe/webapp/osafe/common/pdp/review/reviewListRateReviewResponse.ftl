<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <#if (reviewResponse)?has_content>
	  <label>${uiLabelMap.ReviewResponseCaption}</label>
	  <span>${reviewResponse!}</span>
	</#if>
  </div>
</li>



        