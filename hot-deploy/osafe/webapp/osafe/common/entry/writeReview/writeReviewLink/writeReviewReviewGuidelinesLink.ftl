<#assign reviewGuidelineContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", "SP_REVIEW_GUIDELINES"), true) />
<#if ((reviewGuidelineContent.statusId)?if_exists == "CTNT_PUBLISHED")>
    <div class="writeReviewReviewGuidelinesLink">
        <span class="previewBarLink">
            <a name="reviewGuidelinesLink" id="reviewGuidelinesLink" href="javascript:displayDialogBox('reviewGuideLines_');"><span>${uiLabelMap.ReviewGuidelinesLabel}</span></a>
        </span>
    </div>
    ${screens.render("component://osafe/widget/DialogScreens.xml#reviewGuideLinesDialog")}
</#if>

