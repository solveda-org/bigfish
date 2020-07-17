<#if plpManufacturerDescription?exists &&  plpManufacturerDescription?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
	 <div>
	  <span>${plpManufacturerDescription!""}</span>
	 </div>
	</li>   
</#if>
