<!-- start resultsNavigation.ftl -->
<#if (requestAttributes.numFound)?exists><#assign numFound = requestAttributes.numFound!0></#if>
<#if (requestAttributes.start)?exists><#assign start = requestAttributes.start!0></#if>
<#if (requestAttributes.size)?exists><#assign size = requestAttributes.size!0></#if>
<#if (requestAttributes.pageSize)?exists><#assign pageSize = requestAttributes.pageSize!10></#if>
<#if (requestAttributes.solrPageSize)?exists><#assign solrPageSize = requestAttributes.solrPageSize!10></#if>
<#if (requestAttributes.productCategoryId)?exists><#assign productCategoryId = requestAttributes.productCategoryId!""></#if>
<#if (requestAttributes.filterGroup)?exists><#assign filterGroup = requestAttributes.filterGroup!""></#if>
<#if (requestAttributes.sortResults)?exists><#assign sortResults = requestAttributes.sortResults!""></#if>
<#if !pageSize?exists>
     <#assign pageSize =10/>
</#if>
<#assign sortUrl = request.getRequestURI() >
<#if productCategoryId?exists && productCategoryId?has_content>
    <#assign sortUrl = "?productCategoryId=" + productCategoryId/>
    <#assign sortUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'eCommerceProductList${sortUrl}')/>
</#if>  

<div class="resultsNavigation">
    <div class="sortingOptions">
        <form method="get" id="frmSortResults" name="frmSortResults" action="${sortUrl}">
        <#if filterGroup?exists && filterGroup?has_content>
         <input type="hidden" name="filterGroup"  value="${filterGroup}">
       </#if>
       <#if PLP_AVAILABLE_SORT?has_content && request.getAttribute("sortOptions")?has_content>
        <label for="sortResults">${uiLabelMap.SortByLabel}</label>
        <select id="sortResults" name="sortResults" class="sortOptions" onchange="this.form.submit();">
          <#assign sortOptions = request.getAttribute("sortOptions")>
            <#list sortOptions as sortOption>
                <#assign sortOptionLabel = uiLabelMap.get(sortOption.SORT_OPTION_LABEL)/>
                <option value="${sortOption.SOLR_VALUE}" <#if (parameters.sortResults!requestAttributes.sortResults!"") == sortOption.SOLR_VALUE?replace("|","&#124;")>selected</#if>>${sortOptionLabel!""}</option>
             </#list>
        </select>
        </#if>
        <#--  If you add a sort button you will have to change the width of the sortingOptions style -->
        <#--  Sort Button
        <input type="submit" class="standardBtn action" name="sortBtn" value="Sort" />
        -->
        <#list previousParamsList as param>
           <input type="hidden" name="${param}"  value="${previousParamsMap[param]!""}">
        </#list>
        </form>
    </div>
    <div class="pagingLinks">
			<#if productCategoryId?exists && productCategoryId?has_content>
			  <#assign nextPrevUrl = "?productCategoryId=" + productCategoryId/>
			  <#if filterGroup?exists && filterGroup?has_content>
			    <#assign nextPrevUrl = nextPrevUrl+ "&filterGroup=" + filterGroup/>
			  <#else>
			    <#-- if there is no filter group then get the SEO friendly 'base' URL -->
			    <#assign nextPrevUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'eCommerceProductList${nextPrevUrl}')/>
			  </#if>
			  <#if sortResults?exists && sortResults?has_content>
			  	<#if filterGroup?exists && filterGroup?has_content>
			    	<#assign nextPrevUrl = nextPrevUrl+ "&sortResults=" + sortResults/>
			    <#else>
			        <#-- if there is no filter group then the SEO friendly 'base' URL is being used so make sure we have a '?' to start appending parameters-->
			    	<#assign nextPrevUrl = nextPrevUrl+ "?sortResults=" + sortResults/>
			    </#if>
			  </#if>
			  <#if nextPrevUrl?contains("?")>
                <#assign nextPrevUrl = nextPrevUrl + "&sortResults=" + parameters.sortResults!"" >
              <#else>
                <#assign nextPrevUrl = nextPrevUrl + "?sortResults=" + parameters.sortResults!"" >
              </#if>
              <#if filterGroup?exists && filterGroup?has_content>
              		<#-- if there is filter group then call makeCatalogFriendlyURL -->
	          		<#assign nextPrevUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'eCommerceProductList${nextPrevUrl}')/>
	          </#if>
			<#else>
                <#assign nextPrevUrl = request.getRequestURI() + previousParams >
                <#assign nextPrevUrl = nextPrevUrl + "&sortResults=" + parameters.sortResults!"" >
  		    </#if>


            <#assign pageIndex = (start/pageSize) + 1>
            <#assign totalPages = numFound / pageSize>
            <#assign pageIndex = pageIndex?int>
            <#assign totalPages = totalPages?int>
            <#if (numFound % pageSize gt 0) >
                <#assign totalPages = totalPages +1 >
            </#if>
            <#-- Always show the paging links -->
                <ul class="pageingControls">
                <#if pageSize lt numFound> 
                    <#--  First / Previous-->
                    <#if ((start - pageSize) gte 0) >
                        <li class="pagingBtn first"><a href="${nextPrevUrl}&start=0">${uiLabelMap.FirstLabel}</a></li>
                        <li class="pagingBtn previous"><a href="${nextPrevUrl}&start=${start - pageSize}">${uiLabelMap.PreviousLabel}</a></li>
                    <#else>
                        <li class="pagingBtn first disabled">${uiLabelMap.FirstLabel}</li>
                        <li class="pagingBtn previous disabled">${uiLabelMap.PreviousLabel}</li>
                    </#if>
                    <#--  X to Y of Z -->
                    <#assign highIndex = start + pageSize>
                    <#if (highIndex gt numFound) >
                        <#assign highIndex = numFound >
                    </#if>

                        <#-- X to Y of Z
                        <li class="pages">${start + 1} to ${highIndex} of ${numFound}</li>
                        -->
                        <li class="pages">Page ${pageIndex} of ${totalPages}</li>
                    <#--  Next / Last -->
                    <#if ((start + pageSize) lt numFound) >
                        <li class="pagingBtn next"><a title="next" href="${nextPrevUrl}&start=${start + pageSize}">${uiLabelMap.NextLabel}</a></li>
                        <li class="pagingBtn last"><a title="next" href="${nextPrevUrl}&start=${(totalPages-1) * pageSize}">${uiLabelMap.LastLabel}</a></li>
                    <#else>
                        <li class="pagingBtn next disabled">${uiLabelMap.NextLabel}</li>
                        <li class="pagingBtn last disabled">${uiLabelMap.LastLabel}</li>
                    </#if>
                    <#if (((start + pageSize) lt numFound) || (pageIndex != 1 && pageIndex ==  totalPages))>
                         <li class="pagingBtn showall"><a href="${nextPrevUrl}&rows=${numFound!0}">${uiLabelMap.ViewAllLabel}</a></li>
                    </#if>
                    </#if>
                    <#if parameters.rows?has_content>
                        <li class="pagingBtn showall"><a href="${nextPrevUrl}">${uiLabelMap.ShowLessLabel} </a></li>
                    </#if>
                </ul>
    </div>
 </div>
    



<!-- end resultsNavigation.ftl -->