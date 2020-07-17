<div class="displaySearchBox">
    <div class="header"><h2>${searchBoxHeading?if_exists}</h2></div>
    <div class="boxBody">
      <form action="<@ofbizUrl>${searchRequest!""}</@ofbizUrl>" method="post" name="${searchFormName!""}">
          <input type="hidden" name="initializedCB" value="Y"/>
          <input type="hidden" name="preRetrieved" value="Y"/>
          ${sections.render('searchBoxBody')?if_exists}
          ${sections.render('commonSearchButton')?if_exists}
      </form>
    </div>
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
            ${sections.render('listNoResultBody')?if_exists}
        </table>
        <table class="osafe">
           ${sections.render('commonListButton')?if_exists}
           ${sections.render('commonDetailLinkButton')?if_exists}
           ${sections.render('commonOrderLinkButton')?if_exists}
        </table>
    </div>
      
</div>
