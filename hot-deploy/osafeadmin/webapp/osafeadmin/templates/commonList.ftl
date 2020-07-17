${sections.render('commonFormJS')}
<div class="displayListBox">
    ${sections.render('tooltipBody')?if_exists}
    ${sections.render('listPagingBody')}
    <div class="header"><h2>${listBoxHeading}</h2>
    </div>
    <div class="boxBody">
        <table class="osafe">
            ${sections.render('listBoxBody')?if_exists}
        </table>
        ${sections.render('commonListButton')?if_exists}
         <div class="infoDetailIcon">
          ${sections.render('commonListLinkButton')!}
         </div>
    </div>
</div>
