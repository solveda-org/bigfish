<div class="infoRow row">
  <div class="infoEntry long">
    <div class="infoCaption">
      <label>${uiLabelMap.TypeCaption}</label>
    </div>
    <div class="entryInput checkbox medium">
    <#if mode == 'edit'>
      <input class="checkBoxEntry" type="radio" id="images" name="mediaType"  value="images" <#if parameters.mediaType?exists && parameters.mediaType?string == "images">checked="checked"</#if>/>${uiLabelMap.ImageLabel}
      <input class="checkBoxEntry" type="radio" id="flash" name="mediaType" value="flash" <#if parameters.mediaType?exists && parameters.mediaType?string == "flash">checked="checked"</#if>/>${uiLabelMap.FlashLabel}
      <input class="checkBoxEntry" type="radio" id="document" name="mediaType" value="document" <#if parameters.mediaType?exists && parameters.mediaType?string == "document">checked="checked"</#if>/>${uiLabelMap.DocumentLabel}
    <#else>
      <input class="checkBoxEntry" type="radio" id="images" name="mediaType"  value="images" checked="checked" onClick="javascript:setUploadUrl('images')" />${uiLabelMap.ImageLabel}
      <input class="checkBoxEntry" type="radio" id="flash" name="mediaType" value="flash" <#if parameters.mediaType?exists && parameters.mediaType?string == "flash">checked="checked"</#if> onClick="javascript:setUploadUrl('flash')" />${uiLabelMap.FlashLabel}
      <input class="checkBoxEntry" type="radio" id="document" name="mediaType" value="document" <#if parameters.mediaType?exists && parameters.mediaType?string == "document">checked="checked"</#if> onClick="javascript:setUploadUrl('document')" />${uiLabelMap.DocumentLabel}
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
      <label>${uiLabelMap.LoadMediaCaption}</label>
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
      <label>${uiLabelMap.ChangeMediaCaption}</label>
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
      <label>${uiLabelMap.WidthCaption}</label>
    </div>
    <div class="infoValue">
      ${fileAttrMap.originalWidth!}px 
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
</#if>
