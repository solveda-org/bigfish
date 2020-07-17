  <#assign paramMap = Static["org.ofbiz.base.util.UtilHttp"].getParameterMap(request, Static["org.ofbiz.base.util.UtilMisc"].toSet("productStoreId","browseRootProductCategoryId","preferredDateFormat", "preferredDateTimeFormat", "entryDateTimeFormat"), false)!""/>
  <#if paramMap?has_content>
       <#assign previousParams = Static["org.ofbiz.base.util.UtilHttp"].urlEncodeArgs(paramMap)/>
  </#if>
  <div class="entryButton footer">
    <#if backAction?exists && backAction?has_content>
        <a href="<@ofbizUrl>${backAction}?backActionFlag=Y<#if usePreviousParams?has_content &&  usePreviousParams = "Y" && previousParams?has_content>&${StringUtil.wrapString(previousParams)}</#if></@ofbizUrl>"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
    <#else>
        <a href="${backHref!}"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
    </#if>
    <#if OrderAction?exists && OrderAction?has_content>
        <a href="<@ofbizUrl>${OrderAction}?${detailParamKey}=${detailId}&preRetrieved=Y</@ofbizUrl>" class="buttontext standardBtn action">${uiLabelMap.OrdersBtn}</a>
    </#if>
    <#if CommunicationAction?exists && CommunicationAction?has_content>
       <a href="<@ofbizUrl>${CommunicationAction}?${detailParamKey}=${detailId}&preRetrieved=Y</@ofbizUrl>" class="buttontext standardBtn action">${uiLabelMap.CommunicationBtn}</a>
    </#if>
    <#if updateAction?exists && updateAction?has_content>
        <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'ED');" class="buttontext standardBtn action">${updateActionBtn!"${uiLabelMap.SaveBtn}"}</a>
    <#elseif createAction?exists && createAction?has_content>
        <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'NE');" class="buttontext standardBtn action">${uiLabelMap.SaveBtn}</a>
    </#if>
    <#if execAction?exists && execAction?has_content>
        <a href="<#if !execInNewTab?has_content>javascript:submitDetailForm(document.${detailFormName!""}, 'EX');<#else><@ofbizUrl>${execAction}</@ofbizUrl></#if>" class="buttontext standardBtn action" <#if execInNewTab?has_content>target="_${execInNewTab!}"</#if>>${execActionBtn!"${uiLabelMap.execActionBtn}"}</a>
    </#if>
    <#if execAjaxAction?exists && execAjaxAction?has_content>
        <a href="<#if !execInNewTab?has_content>javascript:submitDetailForm(document.${detailFormName!""}, 'EXA');<#else><@ofbizUrl>${execAjaxAction}</@ofbizUrl></#if>" class="buttontext standardBtn action">${execAjaxActionBtn!"${uiLabelMap.execActionBtn}"}</a>
    </#if>
    <#-- displays Action button only if certain conditions are satisfied -->
    <#assign showButton = false>
    <#-- for scheduled jobs: 'schedJob' is from scheduledJobDetail.groovy -->
    <#if schedJob?has_content && (schedJob.statusId="SERVICE_PENDING" || schedJob.statusId="SERVICE_QUEUED" || schedJob.statusId="SERVICE_RUNNING")>
    	<#assign showButton = true>
    </#if>
    <#if conditionedExecAction?exists && conditionedExecAction?has_content && (showButton=true)>
        <a href="<#if !execInNewTab?has_content>javascript:submitDetailForm(document.${detailFormName!""}, 'CEX');<#else><@ofbizUrl>${conditionedExecAction}</@ofbizUrl></#if>" class="buttontext standardBtn action" <#if execInNewTab?has_content>target="_${execInNewTab!}"</#if>>${conditionedExecActionBtn!"${uiLabelMap.execActionBtn}"}</a>
    </#if>
    <#-- end of conditioned exec button-->
    <#if execCacheAction?exists && execCacheAction?has_content>
        <a href="<#if !execCacheInNewTab?has_content>javascript:submitDetailForm(document.${detailFormName!""}, 'EXC');<#else><@ofbizUrl>${execCacheAction}</@ofbizUrl></#if>" class="buttontext standardBtn action" <#if execCacheInNewTab?has_content>target="_${execCacheInNewTab!}"</#if>>${execCacheActionBtn!"${uiLabelMap.execCacheActionBtn}"}</a>
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
    <#if addAction?exists && addAction?has_content>
      <a href="<@ofbizUrl>${addAction}?${detailParamKey!}=${detailId!}</@ofbizUrl>" class="buttontext standardBtn action">${addBtnText!uiLabelMap.AddBtn}</a>
    </#if>
    <#if statusUpdateAction?exists && statusUpdateAction?has_content>
      <a href="<@ofbizUrl>${statusUpdateAction}?${detailParamKey!}=${detailId!}</@ofbizUrl>" class="buttontext standardBtn action">${statusUpdateBtnText!}</a>
    </#if>
    <#if submitFormAction?exists && submitFormAction?has_content>
        <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'SF');" class="buttontext standardBtn action">${submitFormActionBtn}</a>
    </#if>
  </div>
