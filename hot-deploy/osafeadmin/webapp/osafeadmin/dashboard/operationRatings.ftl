<!-- start displayBox -->
<div class="displayBox pendingReviews">
    <div class="header"><h2>${uiLabelMap.OperationsHeading}</h2></div>
    <div class="subHeader"><h3>${uiLabelMap.PendingRatingsReviewsHeading}</h3></div>
    <div class="boxBody">
        <table class = "osafe">
            <tr>
                <td class="boxCaption">${uiLabelMap.PendingReviewCaption}</td>
                <td class="boxNumber lastCol">
                    <#if ((pendingReviewCount!0)) != 0>
                        <a href="<@ofbizUrl>reviewManagement?srchReviewPend=Y&initializedCB=Y</@ofbizUrl>">${pendingReviewCount}</a>
                    <#else>
                        ${pendingReviewCount!0}
                    </#if>
                </td>
            </tr>
            <tr class="even">
                <td class="boxCaption">${uiLabelMap.OneToFiveDaysCaption}</td>
                <td class="boxNumber lastCol">
                    <#if (oneToFiveDaysCount!0) != 0>
                        <a href="<@ofbizUrl>reviewManagement?srchReviewPend=Y&initializedCB=Y&srchDays=oneToFive</@ofbizUrl>">${oneToFiveDaysCount}</a>
                    <#else>
                        ${oneToFiveDaysCount!0}
                    </#if>
                </td>
            </tr>
            <tr>
                <td class="boxCaption">${uiLabelMap.FiveToTenDaysCaption}</td>
                <td class="boxNumber lastCol">
                    <#if (fiveToTenDaysCount!0) != 0>
                        <a href="<@ofbizUrl>reviewManagement?srchReviewPend=Y&initializedCB=Y&srchDays=fiveToTen</@ofbizUrl>">${fiveToTenDaysCount}</a>
                    <#else>
                        ${fiveToTenDaysCount!0}
                    </#if>
                </td>
            </tr>
            <tr class="even">
                <td class="boxCaption lastRow">${uiLabelMap.TenPlusDayCaption}</td>
                <td class="boxNumber lastCol">
                    <#if (tenPlusDayCount!0) != 0>
                        <a href="<@ofbizUrl>reviewManagement?srchReviewPend=Y&initializedCB=Y&srchDays=tenPlus</@ofbizUrl>">${tenPlusDayCount}</a>
                    <#else>
                        ${tenPlusDayCount!0}
                    </#if>
                </td>
            </tr>
        </table>
    </div>
</div>
<!-- end displayBox -->