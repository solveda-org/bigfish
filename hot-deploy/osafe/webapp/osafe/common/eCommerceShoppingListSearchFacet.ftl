
<#if (requestAttributes.searchTermsMap)?exists><#assign searchTermsMap = requestAttributes.searchTermsMap></#if>
 
<ul>
    <#if searchTermsMap?has_content>
        <li>
            <h3 class="facetGroup">${uiLabelMap.ItemsTitle}</h3>
            <ul class="facetGroup">
		        <#list searchTermsMap.entrySet() as entry>
		            <li class="facetValue">
		                ${Static["org.apache.commons.lang.StringEscapeUtils"].unescapeHtml(entry.value)!}
		                <#assign currentSearchTerm = entry.key!/>
		                <a class="removeMultiSearchItem" href="<@ofbizUrl>searchShoppingList?<#list searchTermsMap.entrySet() as entryMap><#if entryMap.key != currentSearchTerm>${entryMap.key}=${Static["org.apache.commons.lang.StringEscapeUtils"].unescapeHtml(entryMap.value)}&</#if></#list></@ofbizUrl>"></a>
		            </li>
		        </#list>
	        </ul>
        </li>
    </#if>
</ul>



