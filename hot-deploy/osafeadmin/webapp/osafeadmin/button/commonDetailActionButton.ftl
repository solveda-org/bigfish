
  <div class="entryButton">
    <#if backAction?exists && backAction?has_content>
        <a href="<@ofbizUrl>${backAction}?backActionFlag=Y</@ofbizUrl>"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
    <#else>
        <a href="${backHref!}"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
    </#if>
    <#if ExportToFileAction?exists && ExportToFileAction?has_content>
      <a href="<@ofbizUrl>${ExportToFileAction}?${detailParamKey!}=${detailId!}</@ofbizUrl>" target="Download FILE" class="buttontext standardBtn action">${uiLabelMap.ExportToFileBtn}</a>
    </#if>
    <#if ExportToXMLAction?exists && ExportToXMLAction?has_content>
            <a href="<@ofbizUrl>${ExportToXMLAction}?${detailParamKey}=${detailId}</@ofbizUrl>" target="Download XML" class="buttontext standardBtn action">${uiLabelMap.ExportToXMLBtn}</a>
        </#if>
    <#if execAction?exists && execAction?has_content>
        <a href="<#if !execInNewTab?has_content>javascript:submitDetailForm(document.${detailFormName!""}, 'EX');<#else><@ofbizUrl>${execAction}</@ofbizUrl></#if>" class="buttontext standardBtn action" <#if execInNewTab?has_content>target="_${execInNewTab!}"</#if>>${execActionBtn!"${uiLabelMap.execActionBtn}"}</a>
    </#if>
    <#if updateAction?exists && updateAction?has_content>
        <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'ED');" class="buttontext standardBtn action">${updateActionBtn!"${uiLabelMap.SaveBtn}"}</a>
    <#elseif createAction?exists && createAction?has_content>
        <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'NE');" class="buttontext standardBtn action">${uiLabelMap.SaveBtn}</a>
    </#if>
    <#if uploadAction?exists && uploadAction?has_content>
        <a href="javascript:submitDetailUploadForm(document.${detailFormName!""});" class="buttontext standardBtn action">${uiLabelMap.SaveBtn}</a>
    </#if>
    
    <#if deleteAction?exists && deleteAction?has_content>
        <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'DE');" class="buttontext standardBtn action">${uiLabelMap.DeleteBtn}</a>
    </#if>
    <#if confirmAction?exists && confirmAction?has_content>
      <#if confirmActionBtn?exists>
        <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'CF');" class="buttontext standardBtn action">${confirmActionBtn}</a>
      </#if>
    </#if>
    <#if downloadFileAction?exists && downloadFileAction?has_content>
      <a href="<@ofbizUrl>${downloadFileAction}</@ofbizUrl>" target="Download FILE" class="buttontext standardBtn action">${downloadFileBtn!"${uiLabelMap.downloadFileBtn}"}</a>
    </#if>
    <#if commonAction?exists && commonAction?has_content>
      <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'CO');" class="buttontext standardBtn action">${commonActionBtn}</a>
    </#if>
  </div>
