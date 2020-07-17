<#if mode == 'edit'>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.FullNameCaption}</label>
      </div>
      <div class="infoValue">
        <#if fileAttrMap?exists && fileAttrMap?has_content>
          ${fileAttrMap.imagePath}
        </#if>
      </div>
    </div>
  </div>
</#if>

<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.MediaFolderCaption}</label>
    </div>
    <div class="infoValue">
      <div class="entry checkbox medium">
      <#if mode == 'edit'>
        <input class="checkBoxEntry" type="radio" id="newFolder" name="mediaType"  value="newFolder" <#if parameters.mediaType?exists && parameters.mediaType?string == "newFolder">checked="checked"</#if> disabled/>${uiLabelMap.NewFolderLabel}
        <#list directoryNameList as directoryName>
          <input class="checkBoxEntry" type="radio" id="${directoryName}" name="mediaType"  value="${directoryName}" <#if parameters.mediaType?exists && parameters.mediaType?string == "${directoryName}">checked="checked"</#if> disabled/>${directoryName}
        </#list>
        <input name="mediaType" type="hidden" id="mediaType" value="${parameters.mediaType!""}" />
      <#else>
        <input class="checkBoxEntry" type="radio" id="newFolder" name="mediaType"  value="newFolder" checked="checked" onClick="javascript:setUploadUrl('newFolder'); disableNewFolderName('newFolder');" />${uiLabelMap.NewFolderLabel}
        <#list directoryNameList as directoryName>
          <input class="checkBoxEntry" type="radio" id="${directoryName}" name="mediaType" value="${directoryName}" <#if parameters.mediaType?exists && parameters.mediaType?string == "${directoryName}">checked="checked"</#if> onClick="javascript:setUploadUrl('${directoryName}'); disableNewFolderName('${directoryName}');" />${directoryName}
        </#list>
      </#if>
      </div>
    </div>
  </div>
</div>

<div class="infoRow">
  <div class="infoEntry">
    <div class="infoCaption">
      <label>${uiLabelMap.FolderNameCaption}</label>
    </div>
    <div class="infoValue">
      <#if mode="add">
        <input name="newFolderName" type="text" id="newFolderName" value="${parameters.folderName!""}" />
      <#else>
        <input name="newFolderName" type="text" id="newFolderName" value="${parameters.folderName!""}" disabled/>
      </#if>
    </div>
  </div>
</div>

<input type="hidden" name="currentMediaType" id="currentMediaType" value="${currentMediaType!}"/>
<input type="hidden" name="createAction" id="createAction" value=""/>
<#if mode == 'add'>
<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.LoadFileLabel}</label>
    </div>
    <div class="infoValue">
        <input type="file" name="mediaName" size="40" value=""/>
    </div>
  </div>
</div>
</#if>
<#if mode == 'edit'>
  <input type="hidden" name="currentMediaName" id="currentMediaName" value="${mediaName!}"/>
</#if>
<#if fileAttrMap?exists && fileAttrMap?has_content>
<#if (fileAttrMap.height > 0 && fileAttrMap.width > 0)>
<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.PreviewCaption}</label>
    </div>
    <div class="infoValue">
      <#assign fileWidth = fileAttrMap.width!/>
      <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
      <img src="<@ofbizContentUrl>${fileAttrMap.imagePath}?${curDateTime!}</@ofbizContentUrl>" alt="${uiLabelMap.PreviewImageIconShownHereLabel}" width="${fileWidth!}px" class="imageBorder"/>
      <#if (fileAttrMap.originalWidth >= 700)>
        <div>${optionalResizeNoteInfo!}</div>
      </#if>
    </div>
  </div>
</div>
</#if>
<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.ChangeFileCaption}</label>
    </div>
    <div class="infoValue">
      <input type="file" name="uploadedMediaFile" size="40" value=""/>
    </div>
  </div>
</div>
<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.SizeCaption}</label>
    </div>
    <div class="infoValue">
      ${fileAttrMap.fileSize!}kb 
    </div>
  </div>
</div>

<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.HeightCaption}</label>
    </div>
    <div class="infoValue">
      ${fileAttrMap.height!}px 
    </div>
  </div>
</div>

<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.WidthCaption}</label>
    </div>
    <div class="infoValue">
      ${fileAttrMap.width!}px 
    </div>
  </div>
</div>
</#if>
