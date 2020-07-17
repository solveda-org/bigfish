<#if plpProductName?exists &&  plpProductName?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
	 <div>
	  <span>${plpProductName!""}</span>
	 </div>
	</li> 
</#if>  
           