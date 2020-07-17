
<div class="displaySearchBox">
  <form action="<@ofbizUrl>${searchRequest!""}</@ofbizUrl>" method="post" name="${searchFormName!""}">
    <div class="header">
        <h2>${searchBoxHeading?if_exists}</h2>
        <#if stores?has_content && (stores.size() > 1)>
          <#if (useProductStoreSearch?has_content) && (useProductStoreSearch == 'Y')>
            <div class="productStoreEntry">
                <input type="checkbox" class="checkBoxEntry" name="productStoreall" id="productStoreall" value="Y" <#if parameters.productStoreall?has_content>checked</#if>/>${uiLabelMap.AllProductStoreLabel}
            </div>
          </#if>
        </#if>
    </div>
    <div class="boxBody">
          <input type="hidden" name="initializedCB" value="Y"/>
          <input type="hidden" name="preRetrieved" value="Y"/>
          ${sections.render('searchBoxBody')?if_exists}
          ${sections.render('commonSearchButton')?if_exists}
    </div>
  </form>
</div>

<div class="displayListBox">
    ${sections.render('tooltipBody')?if_exists}
    ${sections.render('listPagingBody')?if_exists}
    ${sections.render('commonFormJS')?if_exists}
    ${sections.render('commonConfirm')?if_exists}
    <div class="header"><h2>${listBoxHeading?if_exists}</h2>
    </div>
    <div class="boxBody">
        <table class="osafe <#if (resultList?has_content && resultList.size() > 0)>tablesorter</#if>" <#if (resultList?has_content && resultList.size() > 0)>id="sortTable"</#if>>
            ${sections.render('listBoxBody')?if_exists}
           <#if (!resultList?has_content)> 
            ${sections.render('listNoResultBody')?if_exists}
           </#if>
        </table>
        ${sections.render('commonListButton')?if_exists}
    </div>
</div>
