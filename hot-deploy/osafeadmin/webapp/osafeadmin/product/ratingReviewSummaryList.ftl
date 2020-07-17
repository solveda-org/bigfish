<#if isValidDate?has_content && isValidDate == true>
    <tr class="heading">
        <th class="nameCol firstCol">${uiLabelMap.StatusLabel!""}</th>
        <th class="seqCol">${uiLabelMap.ReviewsCountLabel!""}</th>
    </tr>
        
    <#if resultList?has_content>
        <#assign rowClass = "1">
        <#list resultList as reviewRating>
            <#assign hasNext = reviewRating_has_next>
            <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                <td class="nameCol firstCol" >${reviewRating.status!""}</td>
                <td class="seqCol <#if !hasNext>lastRow</#if>"><#if reviewRating.count != 0 ><a href="javascript:void(0);" onclick="setReviewSearchParams('${reviewRating.statusId}','${parameters.dateFrom}','${parameters.dateTo}','',${reviewRating.count!""});"></#if>${reviewRating.count!""}<#if reviewRating.count != 0 ></a></#if></td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "2">
                <#assign rowClass = "1">
            <#else>
                <#assign rowClass = "2">
            </#if>
        </#list>
        <!-- Pending Reviews Section. -->
        <tr class="dataRow even">
            <td class="nameCol" ><em>${uiLabelMap.ReviewsTotalLabel!""}</em></td>
            <td class="seqCol <#if !hasNext>lastRow</#if>"><#if total != 0 ><a href="javascript:void(0);" onclick="setReviewSearchParams('','${parameters.dateFrom}','${parameters.dateTo}','',${total!""});"></#if><em>${total!""}</em><#if total != 0 ></a></#if></td>
        </tr>
        <tr class="dataRow odd">
            <td class="nameCol" >&nbsp;</td>
            <td class="seqCol" >&nbsp;</td>
        </tr>
        <tr class="dataRow even">
            <td class="nameCol"  colspan="2" >${uiLabelMap.PendingDaysSincePostedLabel!""}</td>
        </tr>
        <tr class="dataRow odd">
            <td class="nameCol pendingReview" >${uiLabelMap.OneToFiveDaysLabel!""}</td>
            <td class="seqCol <#if !hasNext>lastRow</#if>"><#if countOneToFive != 0 ><a href="javascript:void(0);" onclick="setReviewSearchParams('PRR_PENDING','','${parameters.dateTo}','oneToFive',${countOneToFive!""});"></#if>${countOneToFive!""}<#if countOneToFive != 0 ></a></#if></td>
        </tr>
        <tr class="dataRow even">
            <td class="nameCol pendingReview">${uiLabelMap.SixToTenDaysLabel!""}</td>
            <td class="seqCol <#if !hasNext>lastRow</#if>"><#if countSixToTen != 0 ><a href="javascript:void(0);" onclick="setReviewSearchParams('PRR_PENDING','','${parameters.dateTo}','sixToTen',${countSixToTen!""});"></#if>${countSixToTen!""}<#if countSixToTen != 0 ></a></#if></td>
        </tr>
        <tr class="dataRow odd">
            <td class="nameCol pendingReview" >${uiLabelMap.ElevenToTwentyDaysLabel!""}</td>
            <td class="seqCol <#if !hasNext>lastRow</#if>"><#if countElevenToTwenty != 0 ><a href="javascript:void(0);" onclick="setReviewSearchParams('PRR_PENDING','','${parameters.dateTo}','elevenToTwenty',${countElevenToTwenty!""});"></#if>${countElevenToTwenty!""}<#if countElevenToTwenty != 0 ></a></#if></td>
        </tr>
        <tr class="dataRow even">
            <td class="nameCol pendingReview" >${uiLabelMap.MoreThanTwentyDaysLabel!""}</td>
            <td class="seqCol <#if !hasNext>lastRow</#if>"><#if countTwentyPlus != 0 ><a href="javascript:void(0);" onclick="setReviewSearchParams('PRR_PENDING','','${parameters.dateTo}','twentyPlus',${countTwentyPlus!""});"></#if>${countTwentyPlus!""}<#if countTwentyPlus != 0 ></a></#if></td>
        </tr>
    </#if>
    <form method="post" action="<@ofbizUrl>reviewManagement</@ofbizUrl>" name="${detailFormName!"detailForm"}" id="reviewSummary">
        <input type="hidden" name="srchDays" value="" id="srchDays"/>
        <input type="hidden" name="from" value="" id="from"/>
        <input type="hidden" name="to" value="" id="to"/>
        <input type="hidden" name="status" value="" id="status"/>
    </form>
<#else>
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
</#if>
