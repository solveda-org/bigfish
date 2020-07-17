<#if (pagingListSize?has_content && pagingListSize > 0)>
  <#assign paramMap = Static["org.ofbiz.base.util.UtilHttp"].getParameterMap(request)!""/>
  <#if paramMap?has_content>
    <#assign previousParams = Static["org.ofbiz.base.util.UtilHttp"].urlEncodeArgs(paramMap)/>
  <#else>
    <#assign previousParams = ""/>
  </#if>
  <#if previousParams?has_content>
    <#assign previousParams = Static["org.ofbiz.base.util.UtilHttp"].stripNamedParamsFromQueryString(previousParams,Static["org.ofbiz.base.util.UtilMisc"].toSet("viewSize","viewIndex"))/>
  </#if>
  <div class="selectorPagingLinksBox">
    <ul class="pagingLinksBody">
      <#if (viewIndex > 1)>
        <li class="pagingLinks"><a href="javascript:lookupPaginationAjaxRequest('${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}&<#else>?</#if>viewSize=${viewSize}&viewIndex=${viewIndex-1}')">&lt;&lt;Previous Page</a></li>
      </#if>
      <#if (pagingListSize > viewSize)>
        <li class="selectorPagingLinksText <#if (pagingListSize > highIndex) && !(viewIndex > 1)>showpagesnext<#else>showpages</#if>">Showing Rows ${lowIndex} - ${highIndex} of ${pagingListSize}</li>
      <#else>
        <li class="selectorPagingLinksText">Showing Rows ${lowIndex} - ${highIndex}</li>
      </#if>
      <#if (pagingListSize > highIndex)>
        <li class="pagingLinks nextLink"><a href="javascript:lookupPaginationAjaxRequest('${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}&<#else>?</#if>viewSize=${viewSize}&viewIndex=${viewIndex+1}')">Next Page &gt;&gt;</a></li>
      </#if>
    </ul>
  </div>
</#if>
