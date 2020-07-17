<form method="get" action="<@ofbizUrl>${formAction!""}${previousParams?if_exists}</@ofbizUrl>" id="${formName!"entryForm"}" name="${formName!"entryForm"}" onsubmit="return submitMultiSearchForm(document.${formName!"entryForm"});">
<div id="shoppingListSearch" class="displayBox">
    <#list 1..initialRow as x>
        <div class="entry" <#if x == initialRow>id="searchItemDiv"</#if>>
	        <label>${uiLabelMap.FindItemCaption}</label>
	        <div class="entryField">
		        <input type="text" name="searchItem${x}" value=""/>
	        </div>
	    </div>
    </#list>
    <#assign nextRowNo = (initialRow + 1)?number/>
    <div class="entry">
        <label>&nbsp;</label>
        <div class="action shoppingListSearchSubmitButton">
            <input type="submit" class="standardBtn action" value="SEARCH">
            <a href="javascript:void(0);" class="standardBtn action" onClick="javascript:addRow(this, ${nextRowNo});"><span>${uiLabelMap.AddRowBtn}</span></a>
        </div>
    </div>
    
</div>
</form>