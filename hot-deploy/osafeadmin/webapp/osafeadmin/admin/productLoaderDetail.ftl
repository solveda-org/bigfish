  <div class="infoRow">
    <div class="infoDetail">
      <p>${uiLabelMap.ProductLoaderDetailInfo}</p>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ProductFileFormatCaption}</label>
      </div>
      <div class="infoValue">
        <input type="file" size="40" name="productFile" accept="*.xls"/>
      </div>
    </div>
  </div>
  
  <#--div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ImageLocationCaption}</label>
      </div>
      <div class="entry checkbox large">
        <input class="checkBoxEntry" type="radio" id="xlsFileImage" name="imageLocation"  value="xlsFileImage" <#if parameters.imageLocation?exists && parameters.imageLocation == 'xlsFileImage'>checked</#if> onClick="javascript:showImageField('')"/>${uiLabelMap.XlsImageLabel}<br/>
        <input class="checkBoxEntry" type="radio" id="mediaLibraryZip" name="imageLocation"  value="mediaLibraryZip" <#if parameters.imageLocation?exists && parameters.imageLocation == 'mediaLibraryZip'>checked</#if> onClick="javascript:showImageField('mediaLibraryZipField')"/>${uiLabelMap.MediaLibraryZipFileLabel}<br/>
        <input class="checkBoxEntry" type="radio" id="mediaLibraryIndividual" name="imageLocation" value="mediaLibraryIndividual" <#if parameters.imageLocation?exists && parameters.imageLocation == 'mediaLibraryIndividual'>checked</#if> onClick="javascript:showImageField('')"/>${uiLabelMap.MediaLibraryIndividualImagesLabel}<br/>
        <input class="checkBoxEntry" type="radio" id="serverZip" name="imageLocation" value="serverZip" <#if parameters.imageLocation?exists && parameters.imageLocation == 'serverZip'>checked</#if> onClick="javascript:showImageField('serverZipField')"/>${uiLabelMap.ServerZipFileLabel}<br/>
        <input class="checkBoxEntry" type="radio" id="serverIndividual" name="imageLocation" value="serverIndividual" <#if parameters.imageLocation?exists && parameters.imageLocation == 'serverIndividual'>checked</#if> onClick="javascript:showImageField('serverDirField')"/>${uiLabelMap.ServerIndividualImagesLabel}<br/>
        <input class="checkBoxEntry" type="radio" id="localZip" name="imageLocation" value="localZip" <#if parameters.imageLocation?exists && parameters.imageLocation == 'localZip'>checked</#if> onClick="javascript:showImageField('localZipField')"/>${uiLabelMap.LocalFileSystemZipFileLabel}<br/>
        <input class="checkBoxEntry" type="radio" id="url" name="imageLocation" value="url" <#if parameters.imageLocation?exists && parameters.imageLocation == 'url'>checked</#if> onClick="javascript:showImageField('urlPathField')"/>${uiLabelMap.URLLabel}<br/>
      </div>
    </div>
  </div>
  
  <div class="infoRow commonDivHide" id="mediaLibraryZipField" <#if parameters.imageLocation?exists && parameters.imageLocation == 'mediaLibraryZip'><#else>style="display:none"</#if>>
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.MediaLibraryZipFileCaption}</label>
      </div>
      <div class="infoValue">
        <input type="text" class="large" name="mediaLibraryZipFile" value="${parameters.mediaLibraryZipFile!""}"/>
      </div>
    </div>
  </div>
  
  <div class="infoRow commonDivHide" id="serverZipField" <#if parameters.imageLocation?exists && parameters.imageLocation == 'serverZip'><#else>style="display:none"</#if>>
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ServerZipFileCaption}</label>
      </div>
      <div class="infoValue">
        <input type="text" class="large" name="serverZipFile" value="${parameters.serverZipFile!""}"/>
      </div>
    </div>
  </div>
  
  <div class="infoRow commonDivHide" id="serverDirField" <#if parameters.imageLocation?exists && parameters.imageLocation == 'serverIndividual'><#else>style="display:none"</#if>>
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ServerDirectoryCaption}</label>
      </div>
      <div class="infoValue">
        <input type="text" class="large" name="serverDir" value="${parameters.serverDir!""}"/>
      </div>
    </div>
  </div>
  
  <div class="infoRow commonDivHide" id="localZipField" <#if parameters.imageLocation?exists && parameters.imageLocation == 'localZip'><#else>style="display:none"</#if>>
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.LocalZipFileCaption}</label>
      </div>
      <div class="infoValue">
        <input type="file" size="40" name="localZipFile"/>
      </div>
    </div>
  </div>
  
  <div class="infoRow commonDivHide" id="urlPathField" <#if parameters.imageLocation?exists && parameters.imageLocation == 'url'><#else>style="display:none"</#if>>
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.URLCaption}</label>
      </div>
      <div class="infoValue">
        <input type="text" class="large" name="imageUrl" value="${parameters.imageUrl!""}"/>
      </div>
    </div>
  </div>
  -->