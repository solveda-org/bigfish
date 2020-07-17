${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
<div class="displayBox generalInfo">
    <div class="header"><h2>${generalInfoBoxHeading}</h2></div>
    <div class="boxBody">
          ${sections.render('generalInfoBoxBody')}
    </div>
</div>
<#if title?has_content || gender?has_content || dob_MMDD?has_content || dob_MMDDYYYY?has_content || dob_DDMM?has_content || dob_DDMMYYYY?has_content>
	<#if personalInfoBoxHeading?exists && personalInfoBoxHeading?has_content>
	      ${sections.render('personalInfoBoxBody')!}
	</#if>
</#if>
${sections.render('addressInfoBoxBody')}
<div class="displayListBox noteInfo">
    <div class="header"><h2>${customerNoteInfoBoxHeading!}</h2></div>
    <div class="boxBody">
          ${sections.render('customerNoteBoxBody')!}
    </div>
</div>
<div class="displayBox footerInfo">
    <div>
          ${sections.render('footerBoxBody')}
    </div>
    <div class="infoDetailIcon">
      ${sections.render('commonDetailLinkButton')!}
    </div>
</div>
