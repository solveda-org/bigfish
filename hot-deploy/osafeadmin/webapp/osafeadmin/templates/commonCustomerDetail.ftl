${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
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
<#if personalInfoBoxHeading?exists && personalInfoBoxHeading?has_content>
    <div class="displayBox personalInfo">
        <div class="header">
            <h2>
                ${personalInfoBoxHeading}
                <#if (method?has_content && method == "edit")>
                    <span class="headingHelperText">
                        ${uiLabelMap.CustomerDetailPersonalInfoHeadingHelperInfo}
                    </span>
                </#if>
            </h2>
        </div>
        <div class="boxBody">
              ${sections.render('personalInfoBoxBody')}
        </div>
    </div>
</#if>
<#if addressInfoBoxHeading?exists && addressInfoBoxHeading?has_content>
    <div class="displayListBox addressInfo">
        <div class="header">
            <h2>
                ${addressInfoBoxHeading}
                <a href="<@ofbizUrl>${customerAddressDetailAction!}?partyId=${parameters.partyId!}</@ofbizUrl>"><span class="addIcon"></span></a>
            </h2>
        </div>
        <div class="boxBody">${sections.render('addressInfoBoxBody')?if_exists}</div>
    </div>
</#if>
<#if addAddressInfoBoxHeading?exists && addAddressInfoBoxHeading?has_content>
    <div class="displayBox addressInfo">
        <div class="header"><h2>${addAddressInfoBoxHeading}</h2></div>
        <div class="boxBody">
            ${sections.render('addAddressInfoBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if websiteLoginBoxHeading?exists && websiteLoginBoxHeading?has_content>
    <div class="displayBox loginInfo">
        <div class="header"><h2>${websiteLoginBoxHeading}</h2></div>
        <div class="boxBody">
              ${sections.render('websiteLoginBoxBody')}
        </div>
    </div>
</#if>
<#if customerAttributesInfoBoxHeading?exists && customerAttributesInfoBoxHeading?has_content>
    <div class="displayListBox customerAttributesInfo">
        <div class="header"><h2>${customerAttributesInfoBoxHeading}</h2></div>
        <div class="boxBody">
            ${sections.render('customerAttributesInfoBoxBody')?if_exists}
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
${sections.render('capturePlusJs')?if_exists}
</form>
