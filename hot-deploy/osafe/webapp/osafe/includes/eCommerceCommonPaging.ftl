<#if (pagingListSize?has_content && pagingListSize > 0)>
   <#assign showAll = 1/>
   <#assign paramMap = Static["org.ofbiz.base.util.UtilHttp"].getParameterMap(request)!""/>
      <#if paramMap?has_content>
           <#assign previousParams = Static["org.ofbiz.base.util.UtilHttp"].urlEncodeArgs(paramMap)/>
      <#else>
         <#assign previousParams = ""/>
      </#if>
      <#if previousParams?has_content>
         <#assign previousParams = Static["org.ofbiz.base.util.UtilHttp"].stripNamedParamsFromQueryString(previousParams,Static["org.ofbiz.base.util.UtilMisc"].toSet("viewSize","viewIndex","showPagesLink"))/>
      </#if>
      <#assign param=previousParams/>   
   <div class="pagingLinks">
   <ul class="pageingControls">
      <#if (lastIndex > 1)>    
      <#if !parameters.showPagesLink?exists>
      <#if (viewIndex > 1)>
      	<li class="pagingBtn first"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}<#if !previousParams.contains('&')>&</#if><#else>?</#if>viewSize=${showPages!}&viewIndex=${showAll!}">${uiLabelMap.FirstLabel}</a></li>
        <li class="pagingBtn previous"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}<#if !previousParams.contains('&')>&</#if><#else>?</#if>viewSize=${viewSize}&viewIndex=${viewIndex-1}">${uiLabelMap.PreviousLabel}</a></li>
      <#else>
        <li class="pagingBtn first disabled">${uiLabelMap.FirstLabel}</a></li>
        <li class="pagingBtn first disabled">${uiLabelMap.PreviousLabel}</a></li>
      </#if>
      <#if (pagingListSize > viewSize)>                        
        <li class="pages">${uiLabelMap.ShowingRowsLabel} ${viewIndex} ${uiLabelMap.OfLabel} ${lastIndex}</li>
      </#if>
      <#if (pagingListSize > highIndex)>  
        <li class="pagingBtn next"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}<#if !previousParams.contains('&')>&</#if><#else>?</#if>viewSize=${viewSize}&viewIndex=${viewIndex+1}">${uiLabelMap.NextLabel}</a></li>
        <li class="pagingBtn last"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}<#if !previousParams.contains('&')>&</#if><#else>?</#if>viewSize=${showPages!}&viewIndex=${lastIndex!}">${uiLabelMap.LastLabel}</a></li>
      <#else>
        <li class="pagingBtn last disabled">${uiLabelMap.NextLabel}</a></li>
        <li class="pagingBtn next disabled">${uiLabelMap.LastLabel}</a></li>
      </#if>
       <li class="pagingBtn showall"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}<#if !previousParams.contains('&')>&</#if><#else>?</#if>viewSize=${pagingListSize!}&viewIndex=${showAll!}&showPagesLink=Y"> ${uiLabelMap.ViewAllLabel}</a></li>
      </#if>
      </#if>
      <#if parameters.showPagesLink?exists && ((pagingListSize == viewSize) || (pagingListSize &gt; PLP_NUM_ITEMS_PER_PAGE?number)) >      
        <li class="pagingBtn showall"><a href="${request.getRequestURI()!""}<#if previousParams?has_content>?${previousParams}<#if !previousParams.contains('&')>&</#if><#else>?</#if>viewSize=${showPages!}&viewIndex=${showAll!}">${uiLabelMap.ShowLessLabel}</a></li>
      </#if>
    </ul>
  </div>
</#if>
