  <tr class="footer">
    <th colspan="0">
      <div>
        <#if backAction?exists && backAction?has_content>
          <a href="<@ofbizUrl>${backAction}?backActionFlag=Y</@ofbizUrl>"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
        <#else>
          <a href="${backHref!}"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
        </#if>
        <#if addAction?exists && addAction?has_content>
          <a href="<@ofbizUrl>${addAction}</@ofbizUrl>" class="buttontext standardBtn action">${addActionBtn!"${uiLabelMap.AddBtn}"}</a>
        </#if>
        <#if addAction2?exists && addAction2?has_content>
          <a href="<@ofbizUrl>${addAction2}</@ofbizUrl>" class="buttontext standardBtn action">${addActionBtn2!"${uiLabelMap.AddBtn}"}</a>
        </#if>
        <#if addAction3?exists && addAction3?has_content>
          <a href="<@ofbizUrl>${addAction3}</@ofbizUrl>" class="buttontext standardBtn action">${addActionBtn3!"${uiLabelMap.AddBtn}"}</a>
        </#if>
        <#if addAction4?exists && addAction4?has_content>
          <a href="<@ofbizUrl>${addAction4}</@ofbizUrl>" class="buttontext standardBtn action">${addActionBtn4!"${uiLabelMap.AddBtn}"}</a>
        </#if>
        <#if addAction5?exists && addAction5?has_content>
          <a href="<@ofbizUrl>${addAction5}</@ofbizUrl>" class="buttontext standardBtn action">${addActionBtn5!"${uiLabelMap.AddBtn}"}</a>
        </#if>
        <#if seoAction?exists && seoAction?has_content>
          <a href="<@ofbizUrl>${seoAction}</@ofbizUrl>" class="buttontext standardBtn action">${seoActionBtn!""}</a>
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
