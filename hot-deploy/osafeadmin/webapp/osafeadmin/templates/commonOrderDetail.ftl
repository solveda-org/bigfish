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
<#if customerInfoBoxHeading?exists && customerInfoBoxHeading?has_content>
    <div class="displayBox customerInfo">
        <div class="header"><h2 class="centerText">${customerInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('customerInfoBoxBody')}
        </div>
    </div>
</#if>
<#if paymentInfoBoxHeading?exists && paymentInfoBoxHeading?has_content>
    <div class="displayBox paymentInfo">
        <div class="header"><h2 class="centerText">${paymentInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('paymentInfoBoxBody')!}
        </div>
    </div>
</#if>
<#if orderStatusBoxHeading?exists && orderStatusBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${orderStatusBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('orderStatusBoxBody')!}
        </div>
    </div>
</#if>
<#if orderItemInfoBoxHeading?exists && orderItemInfoBoxHeading?has_content>
    <div class="displayListBox orderItemInfo">
        ${sections.render('listPagingBody')}
        ${sections.render('commonFormJS')?if_exists}
        ${sections.render('commonConfirm')}
        <div class="header"><h2>${orderItemInfoBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('orderItemBoxBody')!}
        </div>
    </div>
</#if>
<#if orderNoteInfoBoxHeading?exists && orderNoteInfoBoxHeading?has_content>
    <div class="displayListBox orderItemInfo">
        <div class="header"><h2>${orderNoteInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('orderNoteBoxBody')!}
        </div>
    </div>
</#if>
<div class="displayBox footerInfo">
    <div class="boxBody">
        ${sections.render('footerBoxBody')!}
    </div>
</div>
</form>
${sections.render('commonFormJS')?if_exists}