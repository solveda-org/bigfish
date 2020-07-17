<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
<#if generalInfoBoxHeading?exists && generalInfoBoxHeading?has_content>
    <div class="displayBox generalInfo">
        <div class="header"><h2>${generalInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('generalInfoBoxBody')!}
        </div>
    </div>
</#if>
<#if paymentMethodInfoHeading?exists && paymentMethodInfoHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${paymentMethodInfoHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('paymentMethodInfoBoxBody')!}
        </div>
    </div>
</#if>
<#if orderPaymentPreferenceHeading?exists && orderPaymentPreferenceHeading?has_content>
    <div class="displayListBox orderItemInfo">
        <div class="header"><h2>${orderPaymentPreferenceHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('orderPaymentPreferenceBoxBody')!}
        </div>
    </div>
</#if>
<#if paymentInfoHeading?exists && paymentInfoHeading?has_content>
    <div class="displayListBox orderItemInfo">
        <div class="header"><h2>${paymentInfoHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('paymentInfoBoxBody')!}
        </div>
    </div>
</#if>
<#if paymentGatewayResponseHeading?exists && paymentGatewayResponseHeading?has_content>
    <div class="displayListBox orderItemInfo">
        <div class="header"><h2>${paymentGatewayResponseHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('paymentGatewayResponseBoxBody')!}
        </div>
    </div>
</#if>
<div class="displayBox footerInfo">
    <div>
        ${sections.render('footerBoxBody')!}
    </div>
</div>
</form>
${sections.render('commonFormJS')?if_exists}