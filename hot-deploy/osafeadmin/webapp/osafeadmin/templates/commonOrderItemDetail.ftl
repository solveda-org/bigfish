<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
${sections.render('tooltipBody')?if_exists}
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

<#if orderItems?exists && orderItems?has_content>
  <#list orderItems as orderItem >
  ${setRequestAttribute("orderItem",orderItem)}
    <div class="displayListBox generalInfo">
        ${sections.render('orderItemInfoBoxBody')!}
        <div class="header"><h2>${orderItemNotesBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('orderItemNotesBoxBody')!}
        </div>
        <div class="header"><h2>${orderItemAttributeBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('orderItemAttributeBoxBody')!}
        </div>
    </div>
  </#list>
  <div class="displayBox orderItemsSummaryInfo">
  	<div class="boxBody">
        ${sections.render('orderItemsSummaryBoxBody')!}
    </div>
  </div>
</#if>

<div class="displayBox footerInfo">
    <div>
          ${sections.render('footerBoxBody')}
    </div>
    <div class="infoDetailIcon">
      ${sections.render('commonDetailLinkButton')!}
    </div>
</div>
</form>
${sections.render('commonFormJS')?if_exists}