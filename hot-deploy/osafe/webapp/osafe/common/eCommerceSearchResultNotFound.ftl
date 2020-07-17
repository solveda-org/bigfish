<div class="displayBox">
  <h3>${uiLabelMap.SearchResultNotFoundHeading}</h3>
<#if requestAttributes.emptySearch?has_content>
    <p>${uiLabelMap.EmptySiteSearchInfo}</p>
<#else>
    <p>${uiLabelMap.AlternateSearchInfo}</p>
</#if>
</div>
