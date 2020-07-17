<!-- start priceRuleList.ftl -->
  <thead>
    <tr class="heading">
        <th class="nameCol firstCol">${uiLabelMap.NameLabel}</th>
        <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
        <th class="dateCol">${uiLabelMap.ActiveFromLabel}</th>
        <th class="dateCol">${uiLabelMap.ActiveThruLabel}</th>
        <th class="statusCol lastCol">${uiLabelMap.StatusLabel}</th>
    </tr>
  </thead>
    <#if resultList?exists && resultList?has_content>
        <#assign rowClass = "1">
        <#list resultList as priceRule>
            <#assign hasNext = priceRule_has_next>
            <#assign isPriceRuleActive = Static["org.ofbiz.entity.util.EntityUtil"].isValueActive(priceRule,Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), "fromDate", "thruDate")/>
            <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                <td class="nameCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>priceRuleDetail?priceRuleId=${priceRule.productPriceRuleId!""}</@ofbizUrl>">${priceRule.ruleName!""}</a></td>
                <td class="descCol <#if !hasNext>lastRow</#if>">${priceRule.description!""}</td>
                <td class="dateCol <#if !hasNext>lastRow</#if>">${(priceRule.fromDate?string(preferredDateFormat))!""}</td>
                <td class="dateCol <#if !hasNext>lastRow</#if>">${(priceRule.thruDate?string(preferredDateFormat))!""}</td>
                <td class="statusCol <#if !hasNext>lastRow</#if> lastCol"><#if isPriceRuleActive>${uiLabelMap.ActiveLabel} <#else>${uiLabelMap.InActiveLabel}</#if></td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "2">
                <#assign rowClass = "1">
            <#else>
                <#assign rowClass = "2">
            </#if>
        </#list>
    </#if>
<!-- end priceRuleList.ftl -->