<#if plpManufacturerProfileImageUrl?exists &&  plpManufacturerProfileImageUrl?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
	 <div>
	     <label>${uiLabelMap.ManufacturerNameLabel}</label>
	    <span>${plpManufacturerProfileName!""}</span>
	 </div>
	</li>
</#if>   
