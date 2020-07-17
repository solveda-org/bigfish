
<!-- start searchBox -->
  <div class="entryRow">
    <div class="entry long">
      <label>${uiLabelMap.ShowCaption}</label>
      <#assign intiCb = "${initializedCB}"/>
      <div class= "entryInput">
        <input type="checkbox" class="checkBoxEntry" name="searchKeysNotInUploadedFile" id="searchKeysNotInUploadedFile" value="Y" <#if parameters.searchKeysNotInUploadedFile?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>
        ${uiLabelMap.CompareLabelKeysNotInUploadedFileInfo}
      </div>
    </div>
  </div>

  <div class="entryRow">
    <div class="entry long">
      <label>&nbsp;</label>
      <div class= "entryInput">
        <input type="checkbox" class="checkBoxEntry" name="searchKeysNotInYourLabelFile" id="searchKeysNotInYourLabelFile" value="Y" <#if parameters.searchKeysNotInYourLabelFile?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>
        ${uiLabelMap.CompareLabelKeysNotInYourLabelFileInfo}
      </div>
    </div>
  </div>

  <div class="entryRow">
    <div class="entry long">
      <label>&nbsp;</label>
      <div class= "entryInput">
        <input type="checkbox" class="checkBoxEntry" name="searchKeysInBothFile" id="searchKeysInBothFile" value="Y" <#if parameters.searchKeysInBothFile?has_content>checked</#if>/>
        ${uiLabelMap.CompareLabelKeysInBothFileInfo}
      </div>
    </div>
    <div>
      <input type="submit" class="standardBtn action" name="refreshBtn" value="${uiLabelMap.RefreshBtn}"/>
    </div>
  </div>
<!-- end searchBox -->

