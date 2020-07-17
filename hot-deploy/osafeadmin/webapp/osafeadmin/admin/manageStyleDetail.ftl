<#if styleFileList?exists && styleFileList?has_content>
    <input type="hidden" name="styleFileName" id="${cssPurpose}" value="" />
    <input type="hidden" name="visualThemeId" value="${parameters.visualThemeId!visualThemeId!""}" />
    <#list styleFileList as styleFile>
        <div class="infoRow">
            <div class="infoEntry long">
                 <div class="infoValue withOutCaption">
                     <input class="medium" type="text" id="fileName" name="fileName" readOnly="readonly" value="${styleFile.getName()}"/>
                 </div>
                 <#if styleFileName?has_content>
                     <div class="statusButtons">
                     <#if !styleFileName.equalsIgnoreCase(styleFile.getName())>
                         <a href="javascript:setStyleName('${styleFile.getName()}','${cssPurpose}', '${detailFormName!}');" class="standardBtn secondary">${uiLabelMap.StyleMakeActiveBtn}</a>
                     <#elseif styleFileList.size() gt 1>
                         <span class="spacer"></span>
                     </#if>
                         <a href="<@ofbizUrl>${editAction!"adminToolDetail"}?detailScreen=${cssType!""}&fileName=${styleFile.getName()!""}</@ofbizUrl>" class="standardBtn secondary">${uiLabelMap.AdminEditCSSLabel}</a>
                     </div>
                 </#if>
            </div>
        </div>
    </#list>
</#if>
<div class="infoRow">
    <div class="infoEntry">
        <div class="infoValue withOutCaption">
            <input type="file" size="43" name="uploadedFile" accept="text/css" />
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.BrowseCSSHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
        </div>
    </div>
</div>