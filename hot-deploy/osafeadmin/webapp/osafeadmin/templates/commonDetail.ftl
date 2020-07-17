${sections.render('commonFormJS')}
${sections.render('commonFormDialog')?if_exists}
<#if generalInfoBoxHeading?exists && generalInfoBoxHeading?has_content>
<div class="displayBox generalInfo">
    <div class="header"><h2>${generalInfoBoxHeading!}</h2></div>
    <div class="boxBody">
          ${sections.render('generalInfoBoxBody')!}
    </div>
</div>
</#if>
<div class="displayBox detailInfo">
    <div class="header"><h2>${detailInfoBoxHeading!}</h2></div>
    <div class="boxBody">
      <form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
          ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
          ${sections.render('tooltipBody')?if_exists}
          ${sections.render('detailInfoBoxBody')!}
          <div class="infoRow">
            ${sections.render('detailEntryTableBody')!}
            ${sections.render('commonDetailEntryButton')!}
          </div>
          ${sections.render('commonDetailActionButton')!}
          ${sections.render('commonDetailLinkButton')!}
      </form>
    </div>
</div>
${sections.render('commonConfirm')!}
${sections.render('commonLookup')!}
