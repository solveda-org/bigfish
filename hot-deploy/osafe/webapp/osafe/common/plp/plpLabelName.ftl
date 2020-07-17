<#if plpLabel?exists &&  plpLabel?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
	 <div>
	  <label>${uiLabelMap.PLPLabelNameLabel}</label>
	  <span class="labelName">${plpLabel!""}</span>
	 </div>
	</li>   
</#if>

