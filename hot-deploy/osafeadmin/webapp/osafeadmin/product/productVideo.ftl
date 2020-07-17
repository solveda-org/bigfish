<#if product?has_content>
  <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
  <#if productContentWrapper?exists>
    <#assign productVideoUrl = productContentWrapper.get("PDP_VIDEO_URL")!""/>
    <#assign productVideo360Url = productContentWrapper.get("PDP_VIDEO_360_URL")!""/>
  </#if>
  <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>

      <#-- Product Video Image -->
      <#assign videoUrlPathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("PDP_VIDEO_URL")! />
      <#if productVideoUrl?has_content && productVideoUrl != "">
        <#assign productVideoUrlStr = productVideoUrl.toString() />
        <#if productVideoUrlStr?has_content && (productVideoUrlStr.lastIndexOf("/") > 0)>
          <#assign videoUrlPath = productVideoUrlStr.substring(0, productVideoUrlStr.lastIndexOf("/")+1) />
          <#assign videoUrlName = productVideoUrlStr.substring(productVideoUrlStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
  <input type="hidden" name="productId" value="${product.productId!}"/>
  <input type="hidden" name="productContentTypeId" id="productContentTypeId" value="${parameters.productContentTypeId!}"/>
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.VideoURLCaption}</label>
              </div>
              <div class="infoValue">
                <#if productVideoUrl != "">
                  <object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
		            <param name="movie" value="<@ofbizContentUrl>${productVideoUrl}?${curDateTime!}</@ofbizContentUrl>">
		            <param name="wmode" value="transparent" />
                    <embed src="<@ofbizContentUrl>${productVideoUrl}?${curDateTime!}</@ofbizContentUrl>" height="${IMG_SIZE_PDP_VIDEO_H!""}" width="${IMG_SIZE_PDP_VIDEO_W!""}" class="imageBorder" wmode="transparent"/>
                  </object>
                  <a href="javascript:setProdContentTypeId('PDP_VIDEO_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
                <#else>
                  <span class="noVideo imageBorder"></span>
                </#if>
              </div>
          </div>
       </div>
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.VideoNameCaption}</label>
              </div>
              <div class="infoValue">
                ${videoUrlName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferenceVideoUrlExist = "false"/>
       <#if (productVideoUrlStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productVideoUrlStr)>
         <#assign urlReferenceVideoUrlExist = "true" />
       </#if>
       
       <#if urlReferenceVideoUrlExist = "false">
         <#assign videoUrlFilePath = videoUrlPath!videoUrlPathOsafe! />
       <#else>
         <#assign videoUrlUrlPath = videoUrlPath! />
         <#assign videoUrlUrlRef = productVideoUrlStr />
       </#if>
       
       <#if parameters.videoUrlResourceType?exists && parameters.videoUrlResourceType?string =='file'>
         <#assign urlReferenceVideoUrlExist = "false"/>
       <#elseif parameters.videoUrlResourceType?exists && parameters.videoUrlResourceType?string =='url'>
         <#assign urlReferenceVideoUrlExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="videoUrlPathUrlDiv" <#if (urlReferenceVideoUrlExist =='false')>style="display:none"</#if>>
                  ${videoUrlUrlPath!}
              </div>
              <div class="infoValue" id="videoUrlPathFileDiv" <#if (urlReferenceVideoUrlExist =='true')>style="display:none"</#if>>
                  <input type="text" name="videoUrlFilePath" id="videoUrlFilePath" class="large" value="${parameters.videoUrlFilePath!videoUrlFilePath!videoUrlPathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="videoUrlResourceType" id="browseFileVideoUrl"  value="file" <#if (urlReferenceVideoUrlExist =='false')>checked="checked"</#if> onchange="changeImageRef('videoUrlPathFileDiv','videoUrlFileDiv','videoUrlPathUrlDiv','videoUrlUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="videoUrlResourceType" id="urlRefVideoUrl" value="url" <#if (urlReferenceVideoUrlExist =='true')>checked="checked"</#if> onchange="changeImageRef('videoUrlPathUrlDiv','videoUrlUrlRefDiv', 'videoUrlPathFileDiv','videoUrlFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="videoUrlFileDiv" <#if (urlReferenceVideoUrlExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewVideoCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="videoUrl" size="50" value=""/>
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.VideoFormatInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="videoUrlUrlRefDiv" <#if (urlReferenceVideoUrlExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewVideoCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="videoUrlUrlRef" id="videoUrlUrlRef" class="large" value="${parameters.videoUrlUrlRef!videoUrlUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.VideoFormatInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
       
       <#-- Product Video 360 Image -->
      <#assign video360UrlPathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("PDP_VIDEO_360_URL")! />
      <#if productVideo360Url?has_content && productVideo360Url != "">
        <#assign productVideo360UrlStr = productVideo360Url.toString() />
        <#if productVideo360UrlStr?has_content && (productVideo360UrlStr.lastIndexOf("/") > 0)>
          <#assign video360UrlPath = productVideo360UrlStr.substring(0, productVideo360UrlStr.lastIndexOf("/")+1) />
          <#assign video360UrlName = productVideo360UrlStr.substring(productVideo360UrlStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.VideoURL360Caption}</label>
              </div>
              <div class="infoValue">
                <#if productVideo360Url != "">
                  <object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
		            <param name="movie" value="<@ofbizContentUrl>${productVideo360Url}?${curDateTime!}</@ofbizContentUrl>">
		            <param name="wmode" value="transparent" />
                    <embed src="<@ofbizContentUrl>${productVideo360Url}?${curDateTime!}</@ofbizContentUrl>" height="${IMG_SIZE_PDP_VIDEO_H!""}" width="${IMG_SIZE_PDP_VIDEO_W!""}" class="imageBorder" wmode="transparent"/>
                  </object>
                  <a href="javascript:setProdContentTypeId('PDP_VIDEO_360_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
                <#else>
                  <span class="noVideo imageBorder"></span>
                </#if>
              </div>
          </div>
       </div>
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.VideoNameCaption}</label>
              </div>
              <div class="infoValue">
                ${video360UrlName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferenceVideo360UrlExist = "false"/>
       <#if (productVideo360UrlStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productVideo360UrlStr)>
         <#assign urlReferenceVideo360UrlExist = "true" />
       </#if>
       
       <#if urlReferenceVideo360UrlExist = "false">
         <#assign video360UrlFilePath = video360UrlPath!video360UrlPathOsafe! />
       <#else>
         <#assign video360UrlUrlPath = video360UrlPath! />
         <#assign video360UrlUrlRef = productVideo360UrlStr />
       </#if>
       
       <#if parameters.video360UrlResourceType?exists && parameters.video360UrlResourceType?string =='file'>
         <#assign urlReferenceVideo360UrlExist = "false"/>
       <#elseif parameters.video360UrlResourceType?exists && parameters.video360UrlResourceType?string =='url'>
         <#assign urlReferenceVideo360UrlExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="video360UrlPathUrlDiv" <#if (urlReferenceVideo360UrlExist =='false')>style="display:none"</#if>>
                  ${video360UrlUrlPath!}
              </div>
              <div class="infoValue" id="video360UrlPathFileDiv" <#if (urlReferenceVideo360UrlExist =='true')>style="display:none"</#if>>
                  <input type="text" name="video360UrlFilePath" id="video360UrlFilePath" class="large" value="${parameters.video360UrlFilePath!video360UrlFilePath!video360UrlPathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="video360UrlResourceType" id="browseFileVideo360Url"  value="file" <#if (urlReferenceVideo360UrlExist =='false')>checked="checked"</#if> onchange="changeImageRef('video360UrlPathFileDiv','video360UrlFileDiv','video360UrlPathUrlDiv','video360UrlUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="video360UrlResourceType" id="urlRefVideo360Url" value="url" <#if (urlReferenceVideo360UrlExist =='true')>checked="checked"</#if> onchange="changeImageRef('video360UrlPathUrlDiv','video360UrlUrlRefDiv', 'video360UrlPathFileDiv','video360UrlFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="video360UrlFileDiv" <#if (urlReferenceVideo360UrlExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.New360VideoCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="video360Url" size="50" value=""/>
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.VideoFormatInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="video360UrlUrlRefDiv" <#if (urlReferenceVideo360UrlExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.New360VideoCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="video360UrlUrlRef" id="video360UrlUrlRef" class="large" value="${parameters.video360UrlUrlRef!video360UrlUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.VideoFormatInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
     
</#if>
