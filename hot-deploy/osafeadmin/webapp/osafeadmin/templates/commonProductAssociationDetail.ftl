${sections.render('commonFormJS')}
${sections.render('commonFormDialog')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
    
    <#if complementProductInfoBoxHeading?exists && complementProductInfoBoxHeading?has_content>
      <div class="displayBox detailInfo">
        <div class="header"><h2>${complementProductInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('complementProductInfoBoxBody')!}
        </div>
      </div>
    </#if>
    <#if accessoryProductInfoBoxHeading?exists && accessoryProductInfoBoxHeading?has_content>
      <div class="displayBox detailInfo">
        <div class="header"><h2>${accessoryProductInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('accessoryProductInfoBoxBody')!}
        </div>
      </div>
    </#if>
    <div class="displayBox footerInfo">
      <div>
          ${sections.render('commonDetailActionButton')}
      </div>
      <div class="infoDetailIcon">
          ${sections.render('commonDetailLinkButton')!}
      </div>
    </div>
</form>
${sections.render('commonConfirm')!}
${sections.render('commonLookup')!}

