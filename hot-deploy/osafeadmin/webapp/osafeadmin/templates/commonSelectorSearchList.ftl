${sections.render('commonLookupJS')?if_exists}
<span class="dialogClose" id="lookupCloseButton" ></span>
<div class="selectorHeader"><h2>${searchBoxHeading?if_exists}</h2></div>
<div class="selectorSearchBox">
  <div class="boxBody">
    <form action="<@ofbizUrl>${searchRequest!""}</@ofbizUrl>" method="post" name="${searchFormName!""}" id="${searchFormName!""}">
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
      <input type="hidden" name="initializedCB" value="Y"/>
      <input type="hidden" name="preRetrieved" value="Y"/>
      ${sections.render('searchBoxBody')?if_exists}
      ${sections.render('commonSearchButton')?if_exists}
    </form>
  </div>
</div>
<div>${sections.render('selectorListPagingBody')?if_exists}</div>
<div class="selectorListBox">
  <div class="header"><h2>${listBoxHeading?if_exists}</h2></div>
  <div class="boxBody">
    <table class="osafe">
      ${sections.render('listBoxBody')?if_exists}
      ${sections.render('listNoResultBody')?if_exists}
    </table>
  </div>
</div>
