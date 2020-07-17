<div class="${request.getAttribute("attributeClass")!}">
 <#if shoppingLists?has_content>
    <div class="boxList orderList">
		<#assign lineIndex=0?number/>
		<#assign rowClass = "1">
        <#list shoppingLists as shoppingList> 
          ${setRequestAttribute("shoppingList", shoppingList)}
		  ${setRequestAttribute("lineIndex", lineIndex)}
		  ${setRequestAttribute("rowClass", rowClass)}
	      ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#recurringOrderItemsDivSequence")}
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
    <h3>${uiLabelMap.OrderNoRecurringOrderFoundInfo}</h3>
   </div>
 </#if>
</div>

