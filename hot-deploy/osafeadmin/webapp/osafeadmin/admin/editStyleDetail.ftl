  <#if textData?exists && textData?has_content>
    <input type="hidden" name="visualThemeId" value="${parameters.visualThemeId!visualThemeId!""}" />
    <div class="infoRow">
        <div class="infoEntry long">
             <div class="infoValue withOutCaption">
                 <textarea class="largeArea" name="textData" cols="50" rows="5">${parameters.textData!textData!""}</textarea>
             </div>
        </div>
    </div>

    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ReplaceWithCaption}</label>
            </div>
            <div class="infoValue">
                <input type="file" size="80" name="replaceFile" accept="text/css"/>
                <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'RW');" class="standardBtn">${uiLabelMap.ReplaceWithBtn}</a>
            </div>
        </div>
    </div>

  <#else>
      <div class="infoRow">
          <div class="infoEntry long">
              <div class="infoValue withOutCaption">
                  ${uiLabelMap.NoDataAvailableInfo}
              </div>
          </div>
      </div>
  </#if>