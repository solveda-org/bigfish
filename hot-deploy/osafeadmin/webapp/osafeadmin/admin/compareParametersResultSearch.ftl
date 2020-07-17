<!-- start searchBox -->
  <div class="entryRow">
    <div class="entry long">
      <label>${uiLabelMap.ShowCaption}</label>
      <#assign intiCb = "${initializedCB}"/>
      <div class= "entryInput">
        <input type="checkbox" class="checkBoxEntry" name="searchParamKeysNotInUploadedFile" id="searchParamKeysNotInUploadedFile" value="Y" <#if parameters.searchParamKeysNotInUploadedFile?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>
        ${uiLabelMap.CompareParamKeysNotInUploadedFileInfo}
      </div>
    </div>
  </div>

  <div class="entryRow">
    <div class="entry long">
      <label>&nbsp;</label>
      <div class= "entryInput">
        <input type="checkbox" class="checkBoxEntry" name="searchParamKeysNotInYourDatabase" id="searchParamKeysNotInYourDatabase" value="Y" <#if parameters.searchParamKeysNotInYourDatabase?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>
        ${uiLabelMap.CompareParamKeysNotInYourDatabaseInfo}
      </div>
    </div>
  </div>

  <div class="entryRow">
    <div class="entry long">
      <label>&nbsp;</label>
      <div class= "entryInput">
        <input type="checkbox" class="checkBoxEntry" name="searchParamKeysInBoth" id="searchParamKeysInBoth" value="Y" <#if parameters.searchParamKeysInBoth?has_content>checked</#if>/>
        ${uiLabelMap.CompareParamKeysInBothInfo}
      </div>
    </div>
    <div>
      <input type="submit" class="standardBtn action" name="refreshBtn" value="${uiLabelMap.RefreshBtn}"/>
    </div>
  </div>
<!-- end searchBox -->

