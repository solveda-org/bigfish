${sections.render('commonFormJS')}
${sections.render('commonFormDialog')?if_exists}

<#if searchBoxHeading?exists && searchBoxHeading?has_content>
<div class="displaySearchBox">
     <div class="header"><h2>${searchBoxHeading?if_exists}</h2></div>
     <div class="boxBody">
       <form action="<@ofbizUrl>${searchRequest!""}</@ofbizUrl>" method="post" name="${searchFormName!""}">
           <input type="hidden" name="initializedCB" value="Y"/>
           <input type="hidden" name="preRetrieved" value="Y"/>
           ${sections.render('searchBoxBody')?if_exists}
           ${sections.render('tooltipBody')?if_exists}
           ${sections.render('commonSearchButton')?if_exists}
       </form>
     </div>
</div>
</#if>
<#if generalInfoBoxHeading?exists && generalInfoBoxHeading?has_content>
<div class="displayBox generalInfo">
    <div class="header"><h2>${generalInfoBoxHeading!}</h2></div>
    <div class="boxBody">
          ${sections.render('generalInfoBoxBody')!}
    </div>
</div>
</#if>
<#if (parameters.showDetail?has_content && parameters.showDetail == "true") || (showDetail?has_content && showDetail == "true")>
<div class="displayListBox detailInfo">
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
         <div class="infoDetailIcon">
          ${sections.render('commonDetailLinkButton')!}
	      ${sections.render('commonListHelperText')!}
	      ${sections.render('commonDetailHelperIcon')!}
	      ${sections.render('commonDetailWarningIcon')!}
         </div>
      </form>
    </div>
</div>
${sections.render('commonConfirm')!}
${sections.render('commonLookup')!}
</#if>