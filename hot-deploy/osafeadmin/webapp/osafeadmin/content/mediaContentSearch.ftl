<input type="hidden" class="confirmHiddenFields" name="currentMediaName" id="currentMediaName" value=""/>
<input type="hidden" class="confirmHiddenFields" name="currentMediaType" id="currentMediaType" value=""/>
<div class="entryRow">
    <div class="entry long">
      <label>${uiLabelMap.TypeCaption}</label>
      <div class="entryInput checkbox medium">
        <input class="checkBoxEntry" type="radio" id="images" name="mediaType"  value="images" <#if parameters.mediaType?exists && parameters.mediaType?string == "images">checked="checked"</#if>/>${uiLabelMap.ImageLabel}
        <input class="checkBoxEntry" type="radio" id="flash" name="mediaType" value="flash" <#if parameters.mediaType?exists && parameters.mediaType?string == "flash">checked="checked"</#if>/>${uiLabelMap.FlashLabel}
        <input class="checkBoxEntry" type="radio" id="document" name="mediaType" value="document" <#if parameters.mediaType?exists && parameters.mediaType?string == "document">checked="checked"</#if>/>${uiLabelMap.DocumentLabel}
      </div>
    </div>
</div>
<div class="entryRow">
    <div class="entry long">
      <label>${uiLabelMap.MediaNameCaption}</label>
      <div class="entryInput">
        <input class="largeTextEntry" type="text" id="mediaName" name="mediaName" value="${parameters.mediaName!""}"/>
      </div>
    </div>
  </div>
