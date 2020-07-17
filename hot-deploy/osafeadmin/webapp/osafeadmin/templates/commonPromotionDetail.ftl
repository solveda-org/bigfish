${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
<#if promotionDefinitionBoxHeading?exists && promotionDefinitionBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${promotionDefinitionBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('promotionDefinitionBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if promotionRuleConditionBoxHeading?exists && promotionRuleConditionBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${promotionRuleConditionBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('promotionRuleConditionBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if promotionRuleActionBoxHeading?exists && promotionRuleActionBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${promotionRuleActionBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('promotionRuleActionBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if promotionCodesBoxHeading?exists && promotionCodesBoxHeading?has_content>
    ${sections.render('listPagingBody')?if_exists}
    <div class="displayBox detailInfo">
        <div class="header"><h2>${promotionCodesBoxHeading!}</h2></div>
        <div class="boxBody">
            <table class="osafe">
                ${sections.render('promotionCodesBoxBody')?if_exists}
            </table>
        </div>
    </div>
</#if>
<#if promotionCodeDefinitionBoxHeading?exists && promotionCodeDefinitionBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${promotionCodeDefinitionBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('promotionCodeDefinitionBoxBody')?if_exists}
        </div>
    </div>
</#if>
<div class="displayBox footerInfo">
    <div>
        ${sections.render('commonDetailActionButton')?if_exists}
    </div>
</div>
</form>
${sections.render('commonLookup')?if_exists}