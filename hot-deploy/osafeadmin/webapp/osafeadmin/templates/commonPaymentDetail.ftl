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
<#if summaryInfoBoxHeading?exists && summaryInfoBoxHeading?has_content>
    <div class="displayListBox orderItemInfo">
        <div class="header"><h2>${summaryInfoBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('summaryInfoBoxBody')!}
        </div>
    </div>
</#if>

<#if detailInfoBoxHeading?exists && detailInfoBoxHeading?has_content>
             ${sections.render('detailInfoBoxBody')!}
</#if>

<div class="displayBox footerInfo">
    <div>
        ${sections.render('footerBoxBody')!}
    </div>
</div>
</form>
${sections.render('commonFormJS')?if_exists}