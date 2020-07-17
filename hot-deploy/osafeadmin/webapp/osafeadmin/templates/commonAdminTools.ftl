<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
<#if toolsBoxHeading?exists && toolsBoxHeading?has_content>
    <div class="displayListBox toolsBoxBody">
        <div class="header"><h2>${toolsBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('toolsBoxBody')!}
        </div>
    </div>
</#if>
<#if compareToolsBoxHeading?exists && compareToolsBoxHeading?has_content>
    <div class="displayListBox compareToolsBoxBody">
        <div class="header"><h2>${compareToolsBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('compareToolsBoxBody')!}
        </div>
    </div>
</#if>
<#if seoToolsBoxHeading?exists && seoToolsBoxHeading?has_content>
    <div class="displayListBox seoToolsBoxBody">
        <div class="header"><h2>${seoToolsBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('seoToolsBoxBody')!}
        </div>
    </div>
</#if>
<#if utilitiesBoxHeading?exists && utilitiesBoxHeading?has_content>
    <div class="displayListBox utilitiesBoxBody">
        <div class="header"><h2>${utilitiesBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('utilitiesBoxBody')!}
        </div>
    </div>
</#if>
<div class="displayBox footerInfo">
    <div>
          ${sections.render('footerBoxBody')}
    </div>
</div>
</form>
${sections.render('commonFormJS')?if_exists}