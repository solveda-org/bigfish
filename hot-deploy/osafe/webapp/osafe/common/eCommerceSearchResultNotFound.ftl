<div class="displayBox">
 <div class="displayBoxHeader">
  <span class="displayBoxHeaderCaption">${uiLabelMap.SearchResultNotFoundHeading}</span>
 </div>
<#if requestAttributes.emptySearch?has_content>
    <p class="instructions">${uiLabelMap.EmptySiteSearchInfo}</p>
<#else>
    <p class="instructions">${uiLabelMap.YouSearchedForInfo}&nbsp;<span class="searchResultText">${Static["com.osafe.util.Util"].stripHTML(parameters.searchText)?if_exists}</span></p>
    <p class="instructions">${uiLabelMap.AlternateSearchInfo}</p>
</#if>
</div>
