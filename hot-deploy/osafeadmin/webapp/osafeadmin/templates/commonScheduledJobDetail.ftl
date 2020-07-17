${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
<#if scheduledJobDetailBoxHeading?exists && scheduledJobDetailBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${scheduledJobDetailBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('scheduledJobDetailBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if scheduledJobRuleBoxHeading?exists && scheduledJobRuleBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${scheduledJobRuleBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('scheduledJobRuleBoxBody')?if_exists}
        </div>
    </div>
</#if>

<div class="displayBox footerInfo">
    <div class="boxBody">
        ${sections.render('commonDetailActionButton')?if_exists}
    </div>
</div>
</form>
${sections.render('commonLookup')?if_exists}
${sections.render('commonConfirm')?if_exists}