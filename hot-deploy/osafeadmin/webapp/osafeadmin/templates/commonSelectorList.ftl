${sections.render('commonLookupJS')?if_exists}
<span class="dialogClose" id="lookupCloseButton" ></span>
<div class="selectorHeader"><h2>${listBoxHeading}</h2></div>
<div>${sections.render('selectorListPagingBody')?if_exists}</div>
<div class="selectorListBox">
  <div class="boxBody">
    <table class="osafe">
      ${sections.render('listBoxBody')?if_exists}
    </table>
  </div>
</div>
