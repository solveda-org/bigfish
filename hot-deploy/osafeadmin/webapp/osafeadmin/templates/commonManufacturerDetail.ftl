${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" enctype="multipart/form-data" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
<input type="hidden" name="USER_country" id="USER_country" value="${COUNTRY_DEFAULT!}"/>
<#if generalInfoBoxHeading?exists && generalInfoBoxHeading?has_content>
    <div class="displayBox generalInfo">
        <div class="header">
            <h2>${generalInfoBoxHeading}</h2>
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
              ${sections.render('generalInfoBoxBody')}
        </div>
    </div>
</#if>
<#if profileImageInfoBoxHeading?exists && profileImageInfoBoxHeading?has_content>
    <div class="displayBox profileImageInfo">
        <div class="header">
            <h2>
                ${profileImageInfoBoxHeading}
            </h2>
        </div>
        <div class="boxBody">
              ${sections.render('profileImageInfoBoxBody')}
        </div>
    </div>
</#if>
<#if addressInfoBoxHeading?exists && addressInfoBoxHeading?has_content>
    <div class="displayBox addressInfo">
        <div class="header">
            <h2>
                ${addressInfoBoxHeading}
            </h2>
        </div>
        <div class="boxBody">
              ${sections.render('addressInfoBoxBody')}
        </div>
    </div>
</#if>

<div class="displayBox footerInfo">
    <div>
          ${sections.render('footerBoxBody')}
    </div>
</div>
${sections.render('capturePlusJs')?if_exists}
</form>
${sections.render('commonConfirm')!}
