<!-- start searchBox -->
  <div class="entryRow">
    <div class="entry">
      <label>${uiLabelMap.ProductNoCaption}</label>
      <div class="entryInput">
        <input class="textEntry" type="text" id="productId" name="productId" maxlength="40" value="${parameters.productId!""}"/>
      </div>
    </div>
    <div class="entry medium">
      <label>${uiLabelMap.NameCaption}</label>
      <div class="entryInput">
        <input class="largeTextEntry" type="text" id="productName" name="productName" value="${parameters.productName!""}"/>
      </div>
    </div>
  </div>
  <div class="entryRow">
    <div class="entry">
      <label>${uiLabelMap.ItemNoCaption}</label>
      <div class="entryInput">
        <input class="textEntry" type="text" id="internalName" name="internalName" maxlength="40" value="${parameters.internalName!""}"/>
      </div>
    </div>
    <div class="entry medium">
      <label>${uiLabelMap.DescriptionCaption}</label>
      <div class="entryInput">
        <input class="largeTextEntry" type="text" id="description" name="description" maxlength="40" value="${parameters.description!""}"/>
      </div>
    </div>
  </div>
  
  <div class="entryRow">
    <div class="entry long">
      <label>${uiLabelMap.WebSearchCaption}</label>
      <div class="entryInput">
        <input class="xlarge" type="text" id="searchText" name="searchText" maxlength="40" value="${parameters.searchText!""}"/>
      </div>
    </div>
  </div>
  
  <#if rootProductCategoryId?has_content>
      <#assign topLevelList = delegator.findByAnd("ProductCategoryRollupAndChild", {"parentProductCategoryId" : rootProductCategoryId}, Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum')) />
      <#assign topLevelList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(topLevelList) />
  </#if>
  <div class="entryRow">
    <div class="entry">
      <label>${uiLabelMap.CategoryCaption}</label>
      <div class="entryInput select">
        <select id="srchCategoryId" name="srchCategoryId">
          <option value="all" <#if (parameters.srchCategoryId!"") == "all">selected</#if>>All</option>
            <#if topLevelList?exists && topLevelList?has_content>
              <#list topLevelList as category>
                  <option value="${category.productCategoryId?if_exists}" <#if (parameters.srchCategoryId!"") == "${category.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;${category.categoryName?if_exists}</option>
                  <#assign subCatList = delegator.findByAnd("ProductCategoryRollupAndChild", {"parentProductCategoryId" : category.getString("productCategoryId")}, Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum')) />
                  <#assign subCatList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(subCatList) />
                  <#if subCatList?exists && subCatList?has_content>
                    <#list subCatList as subCategory>
                      <option value="${subCategory.productCategoryId?if_exists}" <#if (parameters.srchCategoryId!"") == "${subCategory.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;&nbsp;&nbsp;${subCategory.categoryName?if_exists}</option>
                    </#list>
                  </#if>
              </#list>
            </#if>
          </select>
      </div>
    </div>
    <div class="entry medium">
      <label>${uiLabelMap.DatesCaption}</label>
      <div class="entryInput checkbox medium">
         <input class="checkBoxEntry" type="checkbox" id="notYetIntroduced" name="notYetIntroduced" value="Y" <#if parameters.notYetIntroduced?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.IncludeNotYetIntroLabel}
         <input class="checkBoxEntry" type="checkbox" id="discontinued" name="discontinued" value="Y" <#if parameters.discontinued?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.IncludeDiscontinuedLabel}
      </div>
    </div>
  </div>
  <div class="entryRow">
   <div class="entry medium">
      <#assign intiCb = "${initializedCB!}"/>
      <label>${uiLabelMap.VirtualProductsCaption}</label>
      <div class="entryInput checkbox short">
         <input class="checkBoxEntry" type="checkbox" id="srchAll" name="srchall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','srch')" <#if parameters.srchall?has_content|| ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.AllLabel}
         <input class="checkBoxEntry" type="checkbox" id="srchVirtualOnly" name="srchVirtualOnly" value="Y" <#if parameters.srchVirtualOnly?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.VirtualLabel}
         <input class="checkBoxEntry" type="checkbox" id="srchFinishedGoodOnly" name="srchFinishedGoodOnly" value="Y" <#if parameters.srchFinishedGoodOnly?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.FinishedGoodLabel}
      </div>
   </div>
  </div>
<!-- end searchBox -->