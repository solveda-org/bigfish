<#if entityRowSizeAnalysisList?has_content && parameters.showDetail?has_content && parameters.showDetail == 'true'>
    <table class="osafe" cellspacing="0">
       <thead>
         <tr class="heading">
           <th class="nameCol firstCol">${uiLabelMap.DBEntityLabel}</th>
           <th class="seqCol">${uiLabelMap.EntityRowsLabel}
           <th class="actionCol"></th>
           <!--th class="actionCol"></th -->
         </tr>
       </thead>
       <tbody>
            
            <#assign rowClass = "1">
            <input type="hidden" name="entityDBName" value="" id="entityDBName"/>
            <#list entityRowSizeAnalysisList as entityInfo>
                <#assign hasNext = entityInfo_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                     <td class="nameCol <#if !hasNext>lastRow</#if>" >
                         ${entityInfo.entityDisplayName!}
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                         ${entityInfo.entityRowCount!}
                     </td>
                     <td class="actionCol <#if !hasNext>lastRow</#if>">
                         <div class="infoText">
                             <a href="javascript:void(0);" onMouseover="showTooltip(event,'${entityInfo.helperText!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                         </div>
                     </td>
                     <!-- td class="actionCol <#if !hasNext>lastRow</#if>">
                         <a href="javascript:deleteConfirmTxt('${entityInfo.entityDisplayName!}');" class="standardBtn" onclick="void(document.getElementById('entityDBName').value='${entityInfo.entityDBName}');">${uiLabelMap.TruncateBtn}</a>
                     </td -->
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </tbody>
        </table>
    <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
    </#if>
