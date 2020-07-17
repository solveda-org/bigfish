<!-- start promotionsList.ftl -->
  <thead>
   <tr class="heading">
       <th class="headingCol" colspan="5">${uiLabelMap.PromotionHeading}</th>
       <th class="headingCol promoCodeCol" colspan="6">${uiLabelMap.PromotionCodeHeading}</th>
   </tr>
    <tr class="heading">
        <th class="nameCol firstCol">${uiLabelMap.NameLabel}</th>
        <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
        <th class="dateCol">${uiLabelMap.ActiveFromLabel}</th>
        <th class="dateCol">${uiLabelMap.ActiveThruLabel}</th>
        <th class="statusCol">${uiLabelMap.StatusLabel}</th>
        <th class="nameCol promoCodeCol">${uiLabelMap.PromotionCodeLabel}</th>
        <th class="dateCol promoCodeCol">${uiLabelMap.ActiveFromLabel}</th>
        <th class="dateCol promoCodeCol">${uiLabelMap.ActiveThruLabel}</th>
        <th class="statusCol promoCodeCol">${uiLabelMap.StatusLabel}</th>
        <th class="usageCol promoCodeCol">${uiLabelMap.UsagesLabel}</th>
        <th class="actionPromoCol promoCodeCol lastCol"></th>
    </tr>
  </thead>
    <#if resultList?exists && resultList?has_content>
        <#assign rowClass = "1">
        <#assign alreadyShownProductPromoIdList = Static["javolution.util.FastList"].newInstance()/>
        <#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("productPromoId","promoName")/>
        <#assign resultList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(resultList,orderByList) />
        <#list resultList as productPromo>
          <#if productPromo.productStoreId?has_content>
            <#assign isProductStorePromoActive = Static["org.ofbiz.entity.util.EntityUtil"].isValueActive(productPromo,Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "productStorePromoApplFromDate", "productStorePromoApplThruDate")/>
          </#if>
          <#if productPromo.productPromoCodeId?has_content>
            <#assign isProductPromoCodeActive = Static["org.ofbiz.entity.util.EntityUtil"].isValueActive(productPromo,Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "productPromoCodeFromDate", "productPromoCodeThruDate")/>
          </#if>
          <#if productPromoUsage?has_content && productPromo.productPromoCodeId?has_content>
               <#assign usageCount = productPromoUsage.get(productPromo.productPromoCodeId)>
          </#if>
          <#assign hasNext = productPromo_has_next>
            <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                <td class="nameCol <#if !hasNext>lastRow</#if> firstCol" ><#if !alreadyShownProductPromoIdList.contains(productPromo.productPromoId!"")><a href="<@ofbizUrl>promotionDetail?productPromoId=${productPromo.productPromoId!""}</@ofbizUrl>">${productPromo.promoName!""}</a></#if></td>
                <td class="descCol <#if !hasNext>lastRow</#if>"><#if !alreadyShownProductPromoIdList.contains(productPromo.productPromoId!"")>${productPromo.promoText!""}</#if></td>
                <td class="dateCol <#if !hasNext>lastRow</#if>"><#if !alreadyShownProductPromoIdList.contains(productPromo.productPromoId!"")>${(productPromo.productStorePromoApplFromDate?string(preferredDateFormat))!""}</#if></td>
                <td class="dateCol <#if !hasNext>lastRow</#if>"><#if !alreadyShownProductPromoIdList.contains(productPromo.productPromoId!"")>${(productPromo.productStorePromoApplThruDate?string(preferredDateFormat))!""}</#if></td>
                <td class="statusCol <#if !hasNext>lastRow</#if>"><#if !alreadyShownProductPromoIdList.contains(productPromo.productPromoId!"")><#if isProductStorePromoActive?exists><#if isProductStorePromoActive>${uiLabelMap.ActiveLabel} <#else>${uiLabelMap.InActiveLabel}</#if></#if></#if></td>
                <td class="nameCol <#if !hasNext>lastRow</#if>" ><a href="<@ofbizUrl>promotionCodeDetail?productPromoCodeId=${productPromo.productPromoCodeId!""}</@ofbizUrl>">${productPromo.productPromoCodeId!""}</a></td>
                <td class="dateCol <#if !hasNext>lastRow</#if>">${(productPromo.productPromoCodeFromDate?string(preferredDateFormat))!""}</td>
                <td class="dateCol <#if !hasNext>lastRow</#if>">${(productPromo.productPromoCodeThruDate?string(preferredDateFormat))!""}</td>
                <td class="statusCol <#if !hasNext>lastRow</#if>"><#if isProductPromoCodeActive?exists && productPromo.productPromoCodeId?has_content><#if isProductPromoCodeActive>${uiLabelMap.ActiveLabel} <#else>${uiLabelMap.InActiveLabel}</#if></#if></td>
                <td class="usageCol <#if !hasNext>lastRow</#if>">
                  <#if productPromo.productPromoCodeId?has_content>
                    <#if usageCount?has_content && (usageCount > 0)>
                      <#assign defaultStatusList = "viewcreated" + "=" + "Y"/>
                      <#assign defaultStatusList = defaultStatusList + "&" + "viewprocessing" + "=" + "Y"/>
                      <#assign defaultStatusList = defaultStatusList + "&" + "viewapproved" + "=" + "Y"/>
                      <#assign defaultStatusList = defaultStatusList + "&" + "viewhold" + "=" + "Y"/>
                      <#assign defaultStatusList = defaultStatusList + "&" + "viewcompleted" + "=" + "Y"/>
  
                      <#assign initializedCB = "&initializedCB" + "=" + "Y"/>
                      <#assign preRetrieved = "&preRetrieved" + "=" + "Y"/>
                      <#assign productPromoCodeId = "&productPromoCodeId" + "=" + productPromo.productPromoCodeId />
  
                      <#assign orderManagementParams = defaultStatusList + productPromoCodeId + initializedCB+ preRetrieved/>
                      <a href="<@ofbizUrl>orderManagement?${orderManagementParams}</@ofbizUrl>">${usageCount!"0"}</a>
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
            </#if>
            <#assign usageCount = 0/>
            <#assign changed = alreadyShownProductPromoIdList.add(productPromo.productPromoId!"")/>
        </#list>
    </#if>
<!-- end promotionsList.ftl -->