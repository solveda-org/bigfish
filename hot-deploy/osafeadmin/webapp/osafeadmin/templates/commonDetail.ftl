${sections.render('commonFormJS')}
${sections.render('commonFormDialog')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
    <#if generalInfoBoxHeading?exists && generalInfoBoxHeading?has_content>
    <div class="displayBox generalInfo">
        <div class="header">
            <h2>${generalInfoBoxHeading!}</h2>
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
              ${sections.render('generalInfoBoxBody')!}
        </div>
    </div>
    </#if>
    <div class="displayBox detailInfo">
        <div class="header">
            <h2>${detailInfoBoxHeading!}</h2>
            <#if !generalInfoBoxHeading?exists || !generalInfoBoxHeading?has_content>
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
            </#if>
        </div>
        <div class="boxBody">
              ${sections.render('tooltipBody')?if_exists}
              ${sections.render('detailInfoBoxBody')!}
              <#if showEntryTableSection?exists && showEntryTableSection?has_content>
              <div class="infoRow">
                ${sections.render('detailEntryTableBody')!}
                ${sections.render('commonDetailEntryButton')!}
              </div>
              </#if>
              ${sections.render('commonDetailActionButton')!}
              
          <div class="infoDetailIcon">
          ${sections.render('commonDetailLinkButton')!}
          ${sections.render('commonDetailHelperText')!}
          ${sections.render('commonDetailHelperIcon')!}
          ${sections.render('commonDetailWarningIcon')!}
          ${sections.render('commonDetailHelperInfoIcon')!}
         </div>
        </div>
    </div>
</form>
${sections.render('commonConfirm')!}
${sections.render('commonLookup')!}
${sections.render('capturePlusJs')?if_exists}

