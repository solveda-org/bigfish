<#assign isCompressed = isCompressed!false />
 
 <#if textData?exists && textData?has_content>
    <input type="hidden" name="visualThemeId" value="${parameters.visualThemeId!visualThemeId!""}" />
    <input type="hidden" name="fileName" value="${parameters.fileName!fileName!""}" />
    <input type="hidden" name="activeStyleFilePath" value="${parameters.styleFilePath!styleFilePath!""}" />
    
    <div class="infoRow">
        <div class="infoEntry long">
             <div class="infoValue withOutCaption">
                 <textarea class="largeArea" name="textData" cols="50" rows="5" <#if isCompressed == true>readonly</#if> >${textData!""}</textarea>
             </div>
        </div>
    </div>

    <#if isCompressed == false>
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ReplaceWithCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" size="80" name="uploadedFile" accept="text/css"/>
                  <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'RW');" class="standardBtn">${uiLabelMap.ReplaceWithBtn}</a>
              </div>
          </div>
      </div>
    </#if>
  <#else>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>