<!-- start promotionsList.ftl -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ReviewIdLabel}</th>
                <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="numberCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="dateCol">${uiLabelMap.DatePostedLabel}</th>
                <th class="nameCol">${uiLabelMap.ReviewerLabel}</th>
                <th class="nameCol">${uiLabelMap.StarsLabel}</th>
                <th class="nameCol">${uiLabelMap.ReviewTitleLabel}</th>
                <th class="actionReviewCol"></th>
                <th class="statusCol">${uiLabelMap.StatusLabel}</th>
                <th class="numberCol lastCol">${uiLabelMap.DaysSincePostingLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as review>
              <#assign hasNext = review_has_next>
              <#assign product = review.getRelatedOne("Product")>
              <#assign statusItem = review.getRelatedOne("StatusItem")>
              <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product,request)>
              <#assign productName = productContentWrapper.get("PRODUCT_NAME")!product.productName!"">
              <#assign rating=review.productRating!"">
              <#assign ratePercentage= ((rating / 5) * 100)>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                    <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>reviewDetail?productReviewId=${review.productReviewId}</@ofbizUrl>">${review.productReviewId}</a></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${productName!}</td>
                    <td class="numberCol <#if !hasNext>lastRow</#if>">${review.productId!""}</td>
                    <#-- td class="idCol <#if !hasNext>lastRow</#if>"><#if product.productId?has_content>${product.internalName}<#else>${review.productId!""}</#if></td -->
                    <td class="dateCol <#if !hasNext>lastRow</#if>">${review.postedDateTime?string(preferredDateFormat!)}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${review.reviewNickName!}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>"><div class="rating_bar"><div style="width:${ratePercentage}%"></div></div></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">
                       ${review.reviewTitle!}
                    </td>
                    <td class="actionReviewCol <#if !hasNext>lastRow</#if>">
                        <#assign tooltipData = review.productReview!""/>
                        <#assign tooltipData = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(tooltipData, ADM_TOOLTIP_MAX_CHAR!)/>
                        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
                    </td>
                    <td class="statusCol <#if !hasNext>lastRow</#if>">${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}</td>
                    <#assign postedInterval = Static["org.ofbiz.base.util.UtilDateTime"].getIntervalInDays(Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(review.postedDateTime), Static["org.ofbiz.base.util.UtilDateTime"].getDayEnd(nowTimestamp))/>
                    <td class="numberCol <#if !hasNext>lastRow</#if> lastCol">${postedInterval!}</td>
                </tr>

                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </#if>
<!-- end promotionsList.ftl -->