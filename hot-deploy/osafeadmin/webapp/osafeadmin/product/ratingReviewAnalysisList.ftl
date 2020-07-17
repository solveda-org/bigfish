<tr class="heading">
    <th class="nameCol firstCol">${uiLabelMap.BestReviewedProductsLabel!""}</th>
    <th class="seqCol">${uiLabelMap.TotalReviewsLabel!""}</th>
    <th class="seqCol">${uiLabelMap.AvgRatingLabel}</th>
</tr>
    
<#if resultList?has_content>
    <#assign rowClass = "1">
    <#list resultList as reviewRating>
        <#assign hasNext = reviewRating_has_next>
        <#assign averageCustomerRating = reviewRating.avgRating!"">
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
            <td class="nameCol firstCol" >${reviewRating.productName!""}</td>
            <td class="seqCol <#if !hasNext>lastRow</#if>">${reviewRating.count!""}</td>
            <td class="seqCol <#if !hasNext>lastRow</#if>">${averageCustomerRating!""}</td>
        </tr>
        <#-- toggle the row color -->
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
</#if>
