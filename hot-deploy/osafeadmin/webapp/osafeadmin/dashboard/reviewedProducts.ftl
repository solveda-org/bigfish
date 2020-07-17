
<!-- start displayBox -->
<div class="displayBox bestReviewedProducts">
    <div class="header"><h2>${uiLabelMap.AnalysisBestReviewedHeading}</h2></div>
    <div class="subHeader"><h3>${uiLabelMap.BestReviewedHeading}</h3></div>
    <div class="boxBody">
        <table class = "osafe">
        <#if bestReviewedProductsList?has_content>
            <#assign rowClass = "1">
            <#list bestReviewedProductsList as product>
                <#assign productId = product.productId!"">
                <#assign internalName = bestReviewedProductContentWrappers[productId].get("INTERNAL_NAME")!"">
                <#assign averageCustomerRating = product.averageCustomerRating!"">
                <#assign productName = bestReviewedProductContentWrappers[productId].get("PRODUCT_NAME")>
                <#if !productName?string?has_content>
                    <#assign productName = productId>
                </#if>
                <tr class="<#if rowClass == "2">even</#if>">
                    <td class="boxCaption <#if !product_has_next>lastRow</#if>"><#if internalName?has_content>${internalName}:</#if>${productName}</td>
                    <td class="boxNumber <#if !product_has_next>lastRow</#if> lastCol">${averageCustomerRating?string("#.0")}</td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        <#else>
                <tr>
                    <td colspan="2" class="boxNumber">${uiLabelMap.NoDataAvailableInfo}</td>
                </tr>
        </#if>
        </table>
    </div>
</div>
<!-- end displayBox -->

