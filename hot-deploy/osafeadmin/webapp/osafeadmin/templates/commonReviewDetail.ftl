${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
${sections.render('commonFormDialog')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}

<input type="hidden" name="productReviewId" value="${review.productReviewId}">
<input type="hidden" name="userLoginId" value="${userLogin.userLoginId}">
<input type="hidden" name="productId" value="${review.productId}">
<input type="hidden" name="statusId" value="${parameters.statusId!review.statusId}">
<input type="hidden" name="productReviewHidden" value="${review.productReview!""}">

<#if generalInfoBoxHeading?exists && generalInfoBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${generalInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('generalInfoBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if reviewerInfoBoxHeading?exists && reviewerInfoBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${reviewerInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('reviewerInfoBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if ratingInfoBoxHeading?exists && ratingInfoBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${ratingInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('ratingInfoBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if detailInfoBoxHeading?exists && detailInfoBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${detailInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('detailInfoBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if statusInfoBoxHeading?exists && statusInfoBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${statusInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('statusInfoBoxBody')?if_exists}
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