<div class="${request.getAttribute("attributeClass")!}">
 <div class="displayBox reOrder">
  <h3>${uiLabelMap.ItemsPreviouslyPurchasedHeading}</h3>
  <form id="reOrderItemForm" name="reOrderItemForm" action="/online/control/" method="post">
    <#assign rowClass = "1">
	<#assign lineIndex=0?number/>
    <#assign renderedOrderItemProductIdList = Static["javolution.util.FastList"].newInstance()/>
    <#assign listSize=renderedOrderItemProductIdList.size()/>
    <div class="boxList reOrderList">
    <#list orderedItemsList as orderedItem>
	    <#if !renderedOrderItemProductIdList.contains(orderedItem.productId)>
		    <#assign changed = renderedOrderItemProductIdList.add(orderedItem.productId)/>
		    ${setRequestAttribute("orderItem", orderedItem)}
		    ${setRequestAttribute("lineIndex", lineIndex)}
		    ${setRequestAttribute("rowClass", rowClass)}
		    ${setRequestAttribute("listSize", listSize)}
	        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#reorderItemsDivSequence")}
	        <#if rowClass == "2">
	            <#assign rowClass = "1">
	        <#else>
	            <#assign rowClass = "2">
	        </#if>
	        <#assign lineIndex= lineIndex + 1/>
	    </#if>
    </#list>
    </div>
  </form>
 </div>
</div>
