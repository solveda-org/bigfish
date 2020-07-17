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
        <div class="pagingLinksBox">
          <ul class="pagingLinksBody">
                        <#if (viewIndex > 1)>
                          <li class="pagingLinks"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}&<#else>?</#if>viewSize=${viewSize}&viewIndex=${viewIndex-1}">${uiLabelMap.PreviousPageLabel}</a></li>
                        </#if>
                        <#if (pagingListSize > viewSize)>
                            <li class="pagingLinksText <#if (pagingListSize > highIndex) && !(viewIndex > 1)>showpagesnext<#else>showpages</#if>">${uiLabelMap.ShowingRowsLabel} ${lowIndex} - ${highIndex} of ${pagingListSize}</li>
                        <#else>
                            <li class="pagingLinksText showpagesnext">${uiLabelMap.ShowingRowsLabel} ${lowIndex} - ${highIndex}</li>
                        </#if>
                        <#assign showAll = 1/>
                        <#if !detailRequest?exists>
                        <#if ((pagingListSize &gt; viewSize) && (viewSizeMax?has_content))>
                          <#if (pagingListSize &lt; viewSizeMax)>
                            <li class="pagingLinks"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}&<#else>?</#if>viewSize=${pagingListSize!}&viewIndex=${showAll!}&showPagesLink=Y"> ${uiLabelMap.ShowAllLinkLabel}</a></li>
                          <#else>
                            <form action="<@ofbizUrl>${confirmAction!""}</@ofbizUrl>" method="post" name="showAllForm">
                              <input type="hidden" name="showPagesLink" value="Y" />
                              <li class="pagingLinks"><a href="javascript:submitDetailForm('showAllForm', 'CF');">${uiLabelMap.ShowAllLinkLabel}</a></li>
                            </form>
                          </#if>
                        <#else>
                          <#if parameters.showPagesLink?exists && (pagingListSize == viewSize) >
                            <li class="pagingLinks showPagesLink"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}&<#else>?</#if>viewSize=${showPages!}&viewIndex=${showAll!}">${uiLabelMap.ShowPagesLabel}</a></li>
                          </#if>
                        </#if>
                        </#if>
                        <#if (pagingListSize > highIndex)>
                            <li class="pagingLinks nextLink"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}&<#else>?</#if>viewSize=${viewSize}&viewIndex=${viewIndex+1}">${uiLabelMap.NextPageLabel}</a></li>
                        </#if>
          </ul>
        </div>
    </#if>
