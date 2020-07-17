  <#assign paramMap = Static["org.ofbiz.base.util.UtilHttp"].getParameterMap(request)!""/>
  <#if paramMap?has_content>
      <#assign previousParams = Static["org.ofbiz.base.util.UtilHttp"].urlEncodeArgs(paramMap)/>
  </#if>

     <div class="entryButton footer">
        <#if backAction?exists && backAction?has_content>
          <a href="<@ofbizUrl>${backAction}?backActionFlag=Y<#if previousParams?has_content>&${StringUtil.wrapString(previousParams)}</#if></@ofbizUrl>"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
        <#else>
          <a href="${backHref!}"  class="buttontext standardBtn action">${uiLabelMap.BackBtn}</a>
        </#if>
        <#if addAction?exists && addAction?has_content>
          <a href="<@ofbizUrl>${StringUtil.wrapString(addAction)}</@ofbizUrl>" class="buttontext standardBtn action">${addActionBtn!"${uiLabelMap.AddBtn}"}</a>
        </#if>
        <#if addAction2?exists && addAction2?has_content>
          <a href="<@ofbizUrl>${addAction2}<#if detailParamKey2?has_content>?${detailParamKey2!}=${detailId2!}</#if></@ofbizUrl>" class="buttontext standardBtn action">${addActionBtn2!"${uiLabelMap.AddBtn}"}</a>
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
     </div>
     <#if resultList?exists && resultList?has_content>
     <div class="infoDetailIcon">
          <#if ExportToPdfAction?exists && ExportToPdfAction?has_content>
            <a href="<@ofbizUrl>${ExportToPdfAction}</@ofbizUrl>" target="Download PDF" class="buttontext action" onMouseover="showTooltip(event,'${ExportToPdfTooltipText!""}');" onMouseout="hideTooltip()"><span class="exportToPdfIcon"></span></a>
          </#if>
          <#if ExportToFileAction?exists && ExportToFileAction?has_content>
            <a href="<@ofbizUrl>${ExportToFileAction}</@ofbizUrl>" target="Download FILE" class="buttontext action" onMouseover="showTooltip(event,'${ExportToFileTooltipText!"${uiLabelMap.ExportToCSVTooltipText}"}');" onMouseout="hideTooltip()"><span class="exportToCsvIcon"></span></a>
          </#if>
          <#if ExportToXMLAction?exists && ExportToXMLAction?has_content>
            <#if ExportToXMLParam?exists && ExportToXMLAction?has_content>
                <a href="<@ofbizUrl>${ExportToXMLAction}?${ExportToXMLParam!}=${ExportToXMLParamValue}</@ofbizUrl>" <#if !execInNewTab?has_content>target="Download XML"</#if>class="buttontext action" onMouseover="showTooltip(event,'${ExportToXmlTooltipText!"${uiLabelMap.ExportToXMLTooltipText}"}')" onMouseout="hideTooltip()"><span class="exportToXmlIcon"></span></a>
            <#else>
                <a href="<@ofbizUrl>${ExportToXMLAction}</@ofbizUrl>" <#if !execInNewTab?has_content>target="Download XML"</#if>class="buttontext action" onMouseover="showTooltip(event,'${ExportToXmlTooltipText!"${uiLabelMap.ExportToXMLTooltipText}"}');" onMouseout="hideTooltip()"><span class="exportToXmlIcon"></span></a>
            </#if>
          </#if>
     </div>
     </#if>

