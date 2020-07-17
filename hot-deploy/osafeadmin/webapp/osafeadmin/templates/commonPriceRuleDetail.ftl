${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
<#if priceRuleDefinitionBoxHeading?exists && priceRuleDefinitionBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${priceRuleDefinitionBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('priceRuleDefinitionBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if priceRuleConditionBoxHeading?exists && priceRuleConditionBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header">
            <h2>
                ${priceRuleConditionBoxHeading!}
                <div class="infoIcon">
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.PriceRuleConditionHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            </h2>
        </div>
        <div class="boxBody">
            ${sections.render('priceRuleConditionBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if PriceRuleActionBoxHeading?exists && PriceRuleActionBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${PriceRuleActionBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('priceRuleActionBoxBody')?if_exists}
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