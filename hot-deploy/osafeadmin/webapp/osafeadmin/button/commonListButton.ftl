  <tr class="footer">
    <th colspan="0">
      <div>
        <#if backAction?exists && backAction?has_content>
          <a href="<@ofbizUrl>${backAction}</@ofbizUrl>"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
        <#else>
          <a href="${backHref!}"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
        </#if>
        <#if addAction?exists && addAction?has_content>
          <a href="<@ofbizUrl>${addAction}</@ofbizUrl>" class="buttontext standardBtn action">${addActionBtn!"${uiLabelMap.AddBtn}"}</a>
        </#if>
        <#if addAnotherAction?exists && addAnotherAction?has_content>
          <a href="<@ofbizUrl>${addAnotherAction}</@ofbizUrl>" class="buttontext standardBtn action">${addAnotherActionBtn!"${uiLabelMap.AddBtn}"}</a>
        </#if>
        <#if resultList?exists && resultList?has_content>
          <#if ExportToPdfAction?exists && ExportToPdfAction?has_content>
            <a href="<@ofbizUrl>${ExportToPdfAction}</@ofbizUrl>" target="Download PDF" class="buttontext standardBtn action">${uiLabelMap.ExportToPdfBtn}</a>
          </#if>
          <#if ExportToFileAction?exists && ExportToFileAction?has_content>
            <a href="<@ofbizUrl>${ExportToFileAction}</@ofbizUrl>" target="Download FILE" class="buttontext standardBtn action">${uiLabelMap.ExportToFileBtn}</a>
          </#if>
          <#if ExportToXMLAction?exists && ExportToXMLAction?has_content>
            <a href="<@ofbizUrl>${ExportToXMLAction}</@ofbizUrl>" target="Download XML" class="buttontext standardBtn action">${uiLabelMap.ExportToXMLBtn}</a>
          </#if>
        </#if>
     </div>
    </th>
  </tr>
