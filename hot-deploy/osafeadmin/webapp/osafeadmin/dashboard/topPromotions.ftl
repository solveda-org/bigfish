
<!-- start displayBox -->
<div class="displayBox topPromotions">
    <div class="header"><h2>${uiLabelMap.AnalysisTopPromotionsHeading}</h2></div>
    <div class="heading"><h3>${uiLabelMap.TopPromotionsHeading}</h3></div>
    <div class="boxBody">
        <table class = "osafe">
        <#if topPromotionsList?has_content>
            <#assign rowClass = "1">
            <#list topPromotionsList as promotion>
                <#assign productPromoCodeId = promotion.productPromoCodeId!"">
                <#assign totalOrders = promotion.totalOrders!"">
                <tr class="<#if rowClass == "2">even</#if>">
                    <td class="boxCaption <#if !promotion_has_next>lastRow</#if>">${productPromoCodeId}</td>
                    <td class="boxNumber <#if !promotion_has_next>lastRow</#if> lastCol" ><a href="<@ofbizUrl>promotionManagement?productPromoCodeId=${productPromoCodeId}</@ofbizUrl>">${totalOrders}</a></td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        <#else>
            ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}        
        </#if>
        </table>
    </div>
</div>
<!-- end displayBox -->

