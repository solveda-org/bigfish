<#if styleFileList?exists && styleFileList?has_content>
    <input type="hidden" name="styleFileName" id="styleFileName" value="" />
    <input type="hidden" name="visualThemeId" value="${parameters.visualThemeId!visualThemeId!""}" />
    <#list styleFileList as styleFile>
        <div class="infoRow">
            <div class="infoEntry long">
                 <div class="infoValue withOutCaption">
                     <input class="medium" type="text" id="fileName" name="fileName" readOnly="readonly" value="${styleFile.getName()}"/>
                 </div>
                 <#if styleFileName?has_content>
                     <#if !styleFileName.equalsIgnoreCase(styleFile.getName())>
                         <div class="statusButtons">
                             <a href="javascript:setStyleName('${styleFile.getName()}');" class="standardBtn secondary">${uiLabelMap.StyleMakeActiveBtn}</a>
                         </div>
                     </#if>
                 </#if>
            </div>
        </div>
    </#list>
</#if>
<div class="infoRow">
    <div class="infoEntry">
        <div class="infoValue withOutCaption">
            <input type="file" size="43" name="uploadedFile" accept="text/css"/>
        </div>
    </div>
</div>