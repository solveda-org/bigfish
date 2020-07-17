
<#if detailId?has_content>
 <table class="osafe">
    <tr class="footer">
    <th colspan="0">
      <div>
        <#if backAction?exists && backAction?has_content>
          <a href="<@ofbizUrl>${backAction}</@ofbizUrl>"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
        <#else>
          <a href="${backHref!}"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
        </#if>
        <#if ExportToPdfAction?exists && ExportToPdfAction?has_content>
          <a href="<@ofbizUrl>${ExportToPdfAction}?${detailParamKey}=${detailId}</@ofbizUrl>" target="Download PDF" class="buttontext standardBtn action">${uiLabelMap.ExportToPdfBtn}</a>
        </#if>
        <#if ExportToFileAction?exists && ExportToFileAction?has_content>
          <a href="<@ofbizUrl>${ExportToFileAction}?${detailParamKey}=${detailId}</@ofbizUrl>" target="Download FILE" class="buttontext standardBtn action">${uiLabelMap.ExportToFileBtn}</a>
        </#if>
        <#if ExportToXMLAction?exists && ExportToXMLAction?has_content>
            <a href="<@ofbizUrl>${ExportToXMLAction}?${detailParamKey}=${detailId}</@ofbizUrl>" target="Download XML" class="buttontext standardBtn action">${uiLabelMap.ExportToXMLBtn}</a>
        </#if>
        <#if OrderAction?exists && OrderAction?has_content>
          <a href="<@ofbizUrl>${OrderAction}?${detailParamKey}=${detailId}&preRetrieved=Y</@ofbizUrl>" class="buttontext standardBtn action">${uiLabelMap.OrdersBtn}</a>
        </#if>
        <#if CommunicationAction?exists && CommunicationAction?has_content>
          <a href="<@ofbizUrl>${CommunicationAction}?${detailParamKey}=${detailId}&preRetrieved=Y</@ofbizUrl>" class="buttontext standardBtn action">${uiLabelMap.CommunicationBtn}</a>
        </#if>
        <#if addAction?exists && addAction?has_content>
          <a href="<@ofbizUrl>${addAction}?${detailParamKey}=${detailId}</@ofbizUrl>" class="buttontext standardBtn action">${addBtnText!uiLabelMap.AddBtn}</a>
        </#if>
      </div>
    </th>
    </tr>
  </table>
</#if>
