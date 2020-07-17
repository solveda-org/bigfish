<input type="hidden" class="confirmHiddenFields" name="currentMediaName" id="currentMediaName" value=""/>
<input type="hidden" class="confirmHiddenFields" name="currentMediaType" id="currentMediaType" value=""/>
<div class="entryRow">
    <div class="entry long">
      <label>${uiLabelMap.MediaFolderCaption}</label>
      <#assign intiCb = "${initializedCB}"/>  
      <div class="entryInput checkbox medium">
        <input type="checkbox" class="checkBoxEntry" name="viewall" id="viewall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','view')" <#if parameters.viewall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
        <#list directoryNameList as directoryName>
          <#assign inputName = StringUtil.removeSpaces(directoryName)!""/>  
          <input class="checkBoxEntry" type="checkbox" id="view${directoryName}" name="${inputName}"  value="${directoryName}" <#if (parameters.get(inputName)?has_content) || ((intiCb?exists) && (intiCb == "N"))>checked="checked"</#if>/>${directoryName}
        </#list>
      </div>
    </div>
</div>
<div class="entryRow">
    <div class="entry long">
      <label>${uiLabelMap.FileNameCaption}</label>
      <div class="entryInput">
        <input class="largeTextEntry" type="text" id="mediaName" name="mediaName" value="${parameters.mediaName!""}"/>
      </div>
    </div>
  </div>
