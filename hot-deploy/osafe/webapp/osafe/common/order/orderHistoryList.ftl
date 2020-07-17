<div class="${request.getAttribute("attributeClass")!}">
 <#if orderHeaderList?has_content>
    <div class="boxList orderList">
		<#assign lineIndex=0?number/>
		<#assign rowClass = "1">
        <#list orderHeaderList as orderHeader>
	      ${setRequestAttribute("orderHeader", orderHeader)}
		    ${setRequestAttribute("lineIndex", lineIndex)}
	      ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderHistoryOrderDetailsDivSequence")}
	        <#if rowClass == "2">
	            <#assign rowClass = "1">
	        <#else>
	            <#assign rowClass = "2">
	        </#if>
	        <#assign lineIndex= lineIndex + 1/>
	    </#list>
	</div>
 <#else>
   <div class="displayBox">
    <h3>${uiLabelMap.OrderNoOrderFoundInfo}</h3>
   </div>
 </#if>
</div>

