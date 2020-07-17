${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
  <#if detailInfoBoxHeading?exists && detailInfoBoxHeading?has_content>
    <div class="displayBox detailInfo">
      <div class="header">
        <h2>${detailInfoBoxHeading!}</h2>
        <#if stores?has_content && (stores.size() > 1)>
          <#if (showProductStoreInfo?has_content) && (showProductStoreInfo == 'Y')>
            <div class="productStoreInfo">
                ${uiLabelMap.ProductStoreInfoCaption}
                <#if context.productStoreName?has_content>
                    ${context.productStoreName}
                </#if>
            </div>
          </#if>
        </#if>
      </div>
      <div class="boxBody">
        ${sections.render('detailInfoBoxBody')?if_exists}
      </div>
    </div>
  </#if>
  <#if detailListBoxHeading?exists && detailListBoxHeading?has_content>
    <div class="displayListBox detailInfo">
      <div class="header"><h2>${detailListBoxHeading!}</h2></div>
      <div class="boxBody">
        <table class="osafe">
            ${sections.render('detailEntryTableBody')!}
            ${sections.render('detailListBoxBody')?if_exists}
        </table>
      </div>
    </div>
  </#if>
  <div class="displayBox footerInfo">
    <div>
      ${sections.render('commonDetailActionButton')?if_exists}
    </div>
    <div class="infoDetailIcon">
      ${sections.render('commonDetailLinkButton')!}
      ${sections.render('commonDetailWarningIcon')!}
    </div>
  </div>
</form>
${sections.render('commonLookup')?if_exists}