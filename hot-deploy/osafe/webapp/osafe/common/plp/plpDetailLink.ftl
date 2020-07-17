<#if plpProductFriendlyUrl?exists &&  plpProductFriendlyUrl?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
	   <div>
	     <a class="eCommerceProductLink pdpUrl" title="${plpProductName!""}" href="${plpProductFriendlyUrl!""}" id="detailLink_${plpProductId!}"><span>${plpProductName!""}</span></a>
	   </div>
	</li>   
</#if>
