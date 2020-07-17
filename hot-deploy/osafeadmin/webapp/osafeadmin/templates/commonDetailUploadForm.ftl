${sections.render('commonFormJS')}
${sections.render('commonFormDialog')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" enctype="multipart/form-data" name="${detailFormName!""}" id="${detailFormName!""}">
  <#if detailInfoBoxHeading?has_content>
	  <div class="displayBox detailInfo">
	    <div class="header"><h2>${detailInfoBoxHeading!}</h2></div>
	    <div class="boxBody">
	      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
	      ${sections.render('detailInfoBoxBody')!}
	      ${sections.render('commonDetailActionButton')!}
	      <div class="infoDetailIcon">
	      ${sections.render('commonDetailLinkButton')!}
	      ${sections.render('commonDetailHelperIcon')!}
	      ${sections.render('commonDetailWarningIcon')!}
	     </div>
	    </div>
	  </div>
  </#if>
  ${sections.render('additionalDetailInfo')!}
</form>
${sections.render('commonConfirm')!}

