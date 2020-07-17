<!-- start promotionsList.ftl -->
    <tr class="heading">
        <th class="nameCol firstCol">${uiLabelMap.PromotionCodeLabel}</th>
        <th class="usageCol">${uiLabelMap.UserEnteredLabel}</th>
        <th class="usageCol">${uiLabelMap.LimitPerCodeLabel}</th>
        <th class="usageCol">${uiLabelMap.LimitPerCustomerLabel}</th>
        <th class="dateCol">${uiLabelMap.ActiveFromLabel}</th>
        <th class="dateCol">${uiLabelMap.ActiveThruLabel}</th>
        <th class="statusCol">${uiLabelMap.StatusLabel}</th>
        <th class="usageCol">${uiLabelMap.UsagesLabel}</th>
        <th class="actionPromoCol lastCol"></th>
    </tr>
    <#if productPromoList?exists && productPromoList?has_content>
        <#assign rowClass = "1">
        <#list productPromoList as productPromo>
          <#if productPromo.productPromoCodeId?has_content>
            <#assign isProductPromoCodeActive = Static["org.ofbiz.entity.util.EntityUtil"].isValueActive(productPromo,Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "productPromoCodeFromDate", "productPromoCodeThruDate")/>
          </#if>
          <#if productPromoUsage?has_content && productPromo.productPromoCodeId?has_content>
               <#assign usageCount = productPromoUsage.get(productPromo.productPromoCodeId)>
          </#if>
          <#assign hasNext = productPromo_has_next>
            <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                <td class="nameCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>promotionCodeDetail?productPromoCodeId=${productPromo.productPromoCodeId!""}</@ofbizUrl>">${productPromo.productPromoCodeId!""}</a></td>
                <td class="usageCol <#if !hasNext>lastRow</#if>">${productPromo.productPromoCodeUserEntered!""}</td>
                <td class="usageCol <#if !hasNext>lastRow</#if>">${productPromo.useLimitPerCode!""}</td>
                <td class="usageCol <#if !hasNext>lastRow</#if>">${productPromo.productPromoCodeUseLimitPerCustomer!""}</td>
                <td class="dateCol <#if !hasNext>lastRow</#if>">${(productPromo.productPromoCodeFromDate?string(preferredDateFormat))!""}</td>
                <td class="dateCol <#if !hasNext>lastRow</#if>">${(productPromo.productPromoCodeThruDate?string(preferredDateFormat))!""}</td>
                <td class="statusCol <#if !hasNext>lastRow</#if>"><#if isProductPromoCodeActive?exists && productPromo.productPromoCodeId?has_content><#if isProductPromoCodeActive>${uiLabelMap.ActiveLabel} <#else>${uiLabelMap.InActiveLabel}</#if></#if></td>
                <td class="usageCol <#if !hasNext>lastRow</#if>">
                  <#if productPromo.productPromoCodeId?has_content>
                    <#if usageCount?has_content && (usageCount > 0)>${usageCount!"0"}
                    <#else>0
                    </#if>
                  </#if>
                </td>
                <td class="actionPromoCol <#if !hasNext>lastRow</#if> lastCol">
                    <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "AddPromoCodeInfo", Static["org.ofbiz.base.util.UtilMisc"].toList(productPromo.promoName!""), locale)/>
                    <a href="<@ofbizUrl>addPromotionCode?productPromotionId=${productPromo.productPromoId!""}</@ofbizUrl>" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="addIcon"></span></a>
                </td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "2">
                <#assign rowClass = "1">
            <#else>
                <#assign rowClass = "2">
            </#if><#assign usageCount = 0/>
        </#list>
    </#if>
<!-- end promotionsList.ftl -->