<form method="get" class="entryForm" action="<@ofbizUrl>${formAction!""}${previousParams?if_exists}</@ofbizUrl>" id="${formName!"entryForm"}" name="${formName!"entryForm"}" onsubmit="return submitMultiSearchForm(document.${formName!"entryForm"});">
<div id="shoppingListSearch" class="displayBox">
    <div class="entry">
        <label>${uiLabelMap.FindItemCaption}</label>
        <div class="entryField">
	        <input type="text" name="searchItem1" value=""/>
        </div>
    </div>
    <div class="entry">
        <label>${uiLabelMap.FindItemCaption}</label>
        <div class="entryField">
	        <input type="text" name="searchItem2" value=""/>
        </div>
    </div>
    <div class="entry">
        <label>${uiLabelMap.FindItemCaption}</label>
        <div class="entryField">
	        <input type="text" name="searchItem3" value=""/>
        </div>
    </div>
    <div class="entry">
        <label>${uiLabelMap.FindItemCaption}</label>
        <div class="entryField">
	        <input type="text" name="searchItem4" value=""/>
        </div>
    </div>
    <div class="entry">
        <label>${uiLabelMap.FindItemCaption}</label>
        <div class="entryField">
	        <input type="text" name="searchItem5" value=""/>
        </div>
    </div>
    
    <div class="action submitButton shoppingListSearchSubmitButton">
        <input type="submit" class="standardBtn action" value="SEARCH">
    </div>
</div>
</form>