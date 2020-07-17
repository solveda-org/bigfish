<tr class="heading">
  <th class="idCol firstCol">${uiLabelMap.GeoIdLabel}</th>
  <th class="nameCol">${uiLabelMap.RateTypeLabel}</th>
  <th class="nameCol">${uiLabelMap.GeoTypeIdLabel}</th>
  <th class="nameCol">${uiLabelMap.ProductCategoryLabel}</th>
  <th class="nameCol">${uiLabelMap.TaxOnShipLabel}</th>
  <th class="nameCol">${uiLabelMap.TaxOnItemsLabel}</th>
  <th class="dateCol">${uiLabelMap.FromDateLabel}</th>
  <th class="dateCol">${uiLabelMap.ThruDateLabel}</th>
  <th class="actionCol"></th>
</tr>
<#if resultList?exists && resultList?has_content>
  <#assign rowClass = "1">
  <#list resultList as taxAuthRateProduct>
    <#assign hasNext = taxAuthRateProduct_has_next>
    <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
      <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>taxDetail?taxAuthorityRateSeqId=${taxAuthRateProduct.taxAuthorityRateSeqId?if_exists}</@ofbizUrl>">${taxAuthRateProduct.taxAuthGeoId!}</a></td>
      <td class="nameCol <#if !hasNext>lastRow</#if>">${taxAuthRateProduct.taxAuthorityRateTypeId!}</td>
      <#assign geo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId" , taxAuthRateProduct.taxAuthGeoId!),false)?if_exists/>
      <td class="nameCol <#if !hasNext>lastRow</#if>">${geo.geoTypeId!}</td>
      <td class="nameCol <#if !hasNext>lastRow</#if>">${taxAuthRateProduct.productCategoryId!uiLabelMap.AllLabel}</td>
      <td class="nameCol <#if !hasNext>lastRow</#if>">${taxAuthRateProduct.taxShipping!}</td>
      <td class="nameCol <#if !hasNext>lastRow</#if>">${taxAuthRateProduct.taxPercentage!}</td>
      <td class="dateCol <#if !hasNext>lastRow</#if>">${(taxAuthRateProduct.fromDate?string(preferredDateFormat))!""}</td>
      <td class="dateCol <#if !hasNext>lastRow</#if>">${(taxAuthRateProduct.thruDate?string(preferredDateFormat))!""}</td>
      <#assign geoAssocList = delegator.findByAnd("GeoAssoc" Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", taxAuthRateProduct.taxAuthGeoId))!"" />
      <td class="actionCol <#if !hasNext>lastRow</#if>">
        <#if geoAssocList?has_content>
          <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${geoAssocList?size} ${uiLabelMap.GeoSubItemsAssocInfo}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
        </#if>
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
