${sections.render('commonFormJS')}
${sections.render('tooltipBody')?if_exists}

<div class="displayListBox">
    <div class="header"><h2>${productLoaderListBoxHeading!}</h2>
    </div>
    <div class="boxBody">
        <table class="osafe">
            ${sections.render('productLoaderListBoxBody')?if_exists}
        </table>
    </div>
</div>
<div class="displayListBox">
    <div class="header"><h2>${feedsImportListBoxHeading!}</h2>
    </div>
    <div class="boxBody">
        <table class="osafe">
            ${sections.render('feedsImportListBoxBody')?if_exists}
        </table>
    </div>
</div>
<div class="displayListBox">
    <div class="header"><h2>${feedsExportListBoxHeading!}</h2>
    </div>
    <div class="boxBody">
        <table class="osafe">
            ${sections.render('feedsExportListBoxBody')?if_exists}
        </table>
    </div>
</div>
<div class="displayListBox">
    <div class="header"><h2>${bluedartFeedsImportListBoxHeading!}</h2>
    </div>
    <div class="boxBody">
        <table class="osafe">
            ${sections.render('bluedartFeedsImportListBoxBody')?if_exists}
        </table>
    </div>
</div>
<div class="displayListBox">
    <div class="header"><h2>${feedConverterListBoxHeading!}</h2>
    </div>
    <div class="boxBody">
        <table class="osafe">
            ${sections.render('feedConverterListBoxBody')?if_exists}
        </table>
    </div>
</div>
<div class="displayBox footerInfo">
    ${sections.render('commonListButton')?if_exists}
</div>