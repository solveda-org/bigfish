<#if plpProductInternalName?exists &&  plpProductInternalName?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
	 <div>
	  <label>${uiLabelMap.PLPInternalNameLabel}</label>
	  <span>${plpProductInternalName!""}</span>
	 </div>
	</li> 
</#if>  


