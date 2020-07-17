<!-- start promotionsList.ftl -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ShipChargeIdLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipMethodTypeLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipCarrierLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipMinTotalLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipMaxTotalLabel}</th>
                <th class="numberCol">${uiLabelMap.ShipSeqLabel}</th>
                <th class="nameCol">${uiLabelMap.ShipFlatRateLabel}</th>
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
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${review.productId!""}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${review.postedDateTime?string(preferredDateFormat!)}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${review.reviewNickName!}</td>
                    <td class="numberCol <#if !hasNext>lastRow</#if>"><div class="rating_bar"><div style="width:${ratePercentage}%"></div></div></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">
                       ${review.reviewTitle!}
                    </td>
                    
                    
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