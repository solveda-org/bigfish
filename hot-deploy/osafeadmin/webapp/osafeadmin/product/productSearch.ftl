<!-- start searchBox -->
<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
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
    <div class="entry.medium">
      <label>${uiLabelMap.WebSearchCaption}</label>
      <div class="entryInput">
        <input class="xlarge" type="text" id="searchText" name="searchText" maxlength="40" value="${parameters.searchText!""}"/>
      </div>
    </div>
  </div>
  
  <div class="entryRow">
    <div class="entry">
    <label>${uiLabelMap.CategoryCaption}</label>
      <div class="entryInput select">
        <select id="srchCategoryId" name="srchCategoryId">
          <option value="all" <#if (parameters.srchCategoryId!"") == "all">selected</#if>>All</option>
            <#if topLevelList?exists && topLevelList?has_content>
              <#list topLevelList as category>
                <#if catContentWrappers?exists>
                  <option value="${category.productCategoryId?if_exists}" <#if (parameters.srchCategoryId!"") == "${category.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;${catContentWrappers[category.productCategoryId].get("CATEGORY_NAME")?if_exists}</option>
				  <#if subCatRollUpMap?has_content>
				    <#assign subCatList = subCatRollUpMap.get(category.productCategoryId)!/>
				  </#if> 
                  <#if subCatList?exists && subCatList?has_content>
                    <#list subCatList as subCategory>
                      <option value="${subCategory.productCategoryId?if_exists}" <#if (parameters.srchCategoryId!"") == "${subCategory.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;&nbsp;&nbsp;${catContentWrappers[subCategory.productCategoryId].get("CATEGORY_NAME")?if_exists}</option>
                    </#list>
                  </#if>
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
	      <div class="entry">
	          <#assign intiCb = "${initializedCB!}"/>
	          <label>${uiLabelMap.VirtualProductsCaption}</label>
	          <div class="entryInput checkbox short">
                 <input class="checkBoxEntry" type="checkbox" id="srchVirtualOnly" name="srchVirtualOnly" value="Y" <#if parameters.srchVirtualOnly?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.VirtualOnlyLabel}
	          </div>
	     </div>
	     <div class="entry medium">
           <label>${uiLabelMap.VariantCaption}</label>
             <div class="entryInput">
               <div class="entryValue">N</div>
             </div>
         </div>
  </div>
<!-- end searchBox -->