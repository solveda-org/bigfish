<#assign geoAssocList = delegator.findByAnd("GeoAssoc" Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", taxAuthorityRateProduct.taxAuthGeoId))!"" />
  <#if geoAssocList?has_content>
    <table class="osafe" cellspacing="0">
      <thead>
        <tr class="heading">
          <th class="idCol firstCol">${uiLabelMap.GeoIdLabel}</th>
          <th class="valueCol">${uiLabelMap.AssocTypeLabel}</th>
          <th class="valueCol">${uiLabelMap.GeoTypeIdLabel}</th>
          <th class="valueCol">${uiLabelMap.GeoNameLabel}</th>
          <th class="valueCol">${uiLabelMap.GeoCodeLabel}</th>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list geoAssocList as geoAssoc>
          <#assign geo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId" , geoAssoc.geoIdTo!),false)?if_exists/>
          <tr <#if alt_row> class="alternate-row"</#if>>
            <td class="idCol firstCol">${geoAssoc.geoIdTo}</td>
            <td class="valueCol">${geoAssoc.geoAssocTypeId!}</td>
            <td class="valueCol">${geo.geoTypeId!}</td>
            <td class="valueCol">${geo.geoName!}</td>
            <td class="valueCol">${geo.geoCode!}</td>
          </tr>
          <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  <#else>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>