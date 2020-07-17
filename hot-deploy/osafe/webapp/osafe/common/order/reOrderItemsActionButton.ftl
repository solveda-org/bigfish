<div class="${request.getAttribute("attributeClass")!}">
    <#if request.getAttribute("displayReOrderItemButton")?exists && request.getAttribute("displayReOrderItemButton") == 'true'> 
        <a class="standardBtn action" href="javascript:addMultiOrderItems();" id="addMultiToCart"><span>${uiLabelMap.ReOrderSelectedItemsBtn}</span></a>
    </#if>
</div>
