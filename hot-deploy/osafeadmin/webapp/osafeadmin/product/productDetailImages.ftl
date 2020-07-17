<#if product?has_content>
  <#if productContentWrapper?exists>
    <#assign productLargeImage = productContentWrapper.get("LARGE_IMAGE_URL")!""/>
    <#assign productThumbnailImage = productContentWrapper.get("THUMBNAIL_IMAGE_URL")!""/>
    <#assign productDetailImage = productContentWrapper.get("DETAIL_IMAGE_URL")!""/>
    <#assign productSmallImage = productContentWrapper.get("SMALL_IMAGE_URL")!""/>
    <#assign productSmallAltImage = productContentWrapper.get("SMALL_IMAGE_ALT_URL")!""/>
    <#assign plpTitleText = productContentWrapper.get("SMALL_IMAGE_ALT")!""/>
  </#if>
  <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
  <input type="hidden" name="productId" value="${product.productId!}"/>
  <input type="hidden" name="productContentTypeId" id="productContentTypeId" value="${parameters.productContentTypeId!}"/>
      
      <#-- Product Large Image -->
      <#assign largeImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("LARGE_IMAGE_URL")! />
      <#if productLargeImage?has_content && productLargeImage != "">
        <#assign productLargeImageStr = productLargeImage.toString() />
        <#if productLargeImageStr?has_content && (productLargeImageStr.lastIndexOf("/") > 0)>
          <#assign largeImagePath = productLargeImageStr.substring(0, productLargeImageStr.lastIndexOf("/")+1) />
          <#assign largeImageName = productLargeImageStr.substring(productLargeImageStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.LargeImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productLargeImage != "">
                  <img src="<@ofbizContentUrl>${productLargeImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productLargeImage}" height="${globalContext.IMG_SIZE_PDP_REG_H!""}" width="${globalContext.IMG_SIZE_PDP_REG_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
          </div>
          <div class="infoIcon">
              <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.LargePDPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
       </div>
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${largeImageName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferenceLargeImageExist = "false"/>
       <#if (productLargeImageStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productLargeImageStr)>
         <#assign urlReferenceLargeImageExist = "true"/>
       </#if>
       
       <#if urlReferenceLargeImageExist = "false">
         <#assign largeImageFilePath = largeImagePath!largeImagePathOsafe! />
       <#else>
         <#assign largeImageUrlPath = largeImagePath! />
         <#assign largeImageUrlRef = productLargeImageStr />
       </#if>
       
       <#if parameters.largeImageResourceType?exists && parameters.largeImageResourceType?string =='file'>
         <#assign urlReferenceLargeImageExist = "false"/>
       <#elseif parameters.largeImageResourceType?exists && parameters.largeImageResourceType?string =='url'>
         <#assign urlReferenceLargeImageExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="largeImagePathUrlDiv" <#if (urlReferenceLargeImageExist =='false')>style="display:none"</#if>>
                  ${largeImageUrlPath!}
              </div>
              <div class="infoValue" id="largeImagePathFileDiv" <#if (urlReferenceLargeImageExist =='true')>style="display:none"</#if>>
                  <input type="text" name="largeImageFilePath" id="largeImageFilePath" class="large" value="${parameters.largeImageFilePath!largeImageFilePath!largeImagePathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="largeImageResourceType" id="browseFileLargeImage"  value="file" <#if (urlReferenceLargeImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('largeImagePathFileDiv','largeImageFileDiv','largeImagePathUrlDiv','largeImageUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="largeImageResourceType" id="urlRefLargeImage" value="url" <#if (urlReferenceLargeImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('largeImagePathUrlDiv','largeImageUrlRefDiv', 'largeImagePathFileDiv','largeImageFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="largeImageFileDiv" <#if (urlReferenceLargeImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="largeImage" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="largeImageUrlRefDiv" <#if (urlReferenceLargeImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="largeImageUrlRef" id="largeImageUrlRef" class="large" value="${parameters.largeImageUrlRef!largeImageUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
       
       <#-- Product Thumbnail Image -->
       <#assign thumbnailImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("THUMBNAIL_IMAGE_URL")! />
       <#if productThumbnailImage?has_content && productThumbnailImage != "">
        <#assign productThumbnailImageStr = productThumbnailImage.toString() />
        <#if productThumbnailImageStr?has_content && (productThumbnailImageStr.lastIndexOf("/") > 0)>
          <#assign thumbnailImagePath = productThumbnailImageStr.substring(0, productThumbnailImageStr.lastIndexOf("/")+1) />
          <#assign thumbnailImageName = productThumbnailImageStr.substring(productThumbnailImageStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ThumbnailImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productThumbnailImage != "">
                  <img src="<@ofbizContentUrl>${productThumbnailImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productThumbnailImage}" height="${globalContext.IMG_SIZE_PDP_THUMB_H!""}" width="${globalContext.IMG_SIZE_PDP_THUMB_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
	          <div class="infoIcon">
                <#if productThumbnailImage != "">
	              <a class="helper" href="javascript:setProdContentTypeId('THUMBNAIL_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
	            </#if>
	            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.ThumbPDPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	          </div>
          </div>
       </div>
     
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${thumbnailImageName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferenceThumbnailImageExist = "false"/>
       <#if (productThumbnailImageStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productThumbnailImageStr)>
         <#assign urlReferenceThumbnailImageExist = "true"/>
       </#if>
       
       <#if urlReferenceThumbnailImageExist = "false">
         <#assign thumbnailImageFilePath = thumbnailImagePath!thumbnailImagePathOsafe! />
       <#else>
         <#assign thumbnailImageUrlPath = thumbnailImagePath! />
         <#assign thumbnailImageUrlRef = productThumbnailImageStr />
       </#if>
       
       <#if parameters.thumbnailImageResourceType?exists && parameters.thumbnailImageResourceType?string =='file'>
         <#assign urlReferenceThumbnailImageExist = "false"/>
       <#elseif parameters.thumbnailImageResourceType?exists && parameters.thumbnailImageResourceType?string =='url'>
         <#assign urlReferenceThumbnailImageExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="thumbnailImagePathUrlDiv" <#if (urlReferenceThumbnailImageExist =='false')>style="display:none"</#if>>
                  ${thumbnailImageUrlPath!}
              </div>
              <div class="infoValue" id="thumbnailImagePathFileDiv" <#if (urlReferenceThumbnailImageExist =='true')>style="display:none"</#if>>
                  <input type="text" name="thumbnailImageFilePath" id="thumbnailImageFilePath" class="large" value="${parameters.thumbnailImageFilePath!thumbnailImageFilePath!thumbnailImagePathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="thumbnailImageResourceType" id="browseFileThumbnailImage"  value="file" <#if (urlReferenceThumbnailImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('thumbnailImagePathFileDiv','thumbnailImageFileDiv','thumbnailImagePathUrlDiv','thumbnailImageUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="thumbnailImageResourceType" id="urlRefThumbnailImage" value="url" <#if (urlReferenceThumbnailImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('thumbnailImagePathUrlDiv','thumbnailImageUrlRefDiv', 'thumbnailImagePathFileDiv','thumbnailImageFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="thumbnailImageFileDiv" <#if (urlReferenceThumbnailImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="thumbnailImage" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="thumbnailImageUrlRefDiv" <#if (urlReferenceThumbnailImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="thumbnailImageUrlRef" id="thumbnailImageUrlRef" class="large" value="${parameters.thumbnailImageUrlRef!thumbnailImageUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
      
       <#-- Product Popup Detail Image -->
       <#assign detailImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("DETAIL_IMAGE_URL")! />
       <#if productDetailImage?has_content && productDetailImage != "">
         <#assign productDetailImageStr = productDetailImage.toString() />
         <#if productDetailImageStr?has_content && (productDetailImageStr.lastIndexOf("/") > 0)>
           <#assign detailImagePath = productDetailImageStr.substring(0, productDetailImageStr.lastIndexOf("/")+1) />
           <#assign detailImageName = productDetailImageStr.substring(productDetailImageStr.lastIndexOf("/")+1) />
         </#if>
       </#if> 
     
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.PopUpDetailImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productDetailImage != "">
                  <img src="<@ofbizContentUrl>${productDetailImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productDetailImage}" height="${globalContext.IMG_SIZE_PDP_POPUP_H!""}" width="${globalContext.IMG_SIZE_PDP_POPUP_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
	          <div class="infoIcon">
                <#if productDetailImage != "">
	              <a class="helper" href="javascript:setProdContentTypeId('DETAIL_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
	            </#if>
	              <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.DetailPOPUPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	          </div>
          </div>
       </div>
     
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${detailImageName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferenceDetailImageExist = "false"/>
       <#if (productDetailImageStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productDetailImageStr)>
         <#assign urlReferenceDetailImageExist = "true"/>
       </#if>
       
       <#if urlReferenceDetailImageExist = "false">
         <#assign detailImageFilePath = detailImagePath!detailImagePathOsafe! />
       <#else>
         <#assign detailImageUrlPath = detailImagePath! />
         <#assign detailImageUrlRef = productDetailImageStr />
       </#if>
       
       <#if parameters.detailImageResourceType?exists && parameters.detailImageResourceType?string =='file'>
         <#assign urlReferenceDetailImageExist = "false"/>
       <#elseif parameters.detailImageResourceType?exists && parameters.detailImageResourceType?string =='url'>
         <#assign urlReferenceDetailImageExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="detailImagePathUrlDiv" <#if (urlReferenceDetailImageExist =='false')>style="display:none"</#if>>
                  ${detailImageUrlPath!}
              </div>
              <div class="infoValue" id="detailImagePathFileDiv" <#if (urlReferenceDetailImageExist =='true')>style="display:none"</#if>>
                  <input type="text" name="detailImageFilePath" id="detailImageFilePath" class="large" value="${parameters.detailImageFilePath!detailImageFilePath!detailImagePathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="detailImageResourceType" id="browseFileDetailImage"  value="file" <#if (urlReferenceDetailImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('detailImagePathFileDiv','detailImageFileDiv','detailImagePathUrlDiv','detailImageUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="detailImageResourceType" id="urlRefDetailImage" value="url" <#if (urlReferenceDetailImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('detailImagePathUrlDiv','detailImageUrlRefDiv', 'detailImagePathFileDiv','detailImageFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="detailImageFileDiv" <#if (urlReferenceDetailImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="detailImage" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="detailImageUrlRefDiv" <#if (urlReferenceDetailImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="detailImageUrlRef" id="detailImageUrlRef" class="large" value="${parameters.detailImageUrlRef!detailImageUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
     
       <#-- Product Small Image -->
      <#assign smallImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("LARGE_IMAGE_URL")! />
      <#if productSmallImage?has_content && productSmallImage != "">
        <#assign productSmallImageStr = productSmallImage.toString() />
        <#if productSmallImageStr?has_content && (productSmallImageStr.lastIndexOf("/") > 0)>
          <#assign smallImagePath = productSmallImageStr.substring(0, productSmallImageStr.lastIndexOf("/")+1) />
          <#assign smallImageName = productSmallImageStr.substring(productSmallImageStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
     
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.SmallImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productSmallImage != "">
                 <img src="<@ofbizContentUrl>${productSmallImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productSmallImage}" height="${globalContext.IMG_SIZE_PLP_H!""}" width="${globalContext.IMG_SIZE_PLP_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
          </div>
          <div class="infoIcon">
             <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SmallPLPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
       </div>
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${smallImageName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferenceSmallImageExist = "false"/>
       <#if (productSmallImageStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productSmallImageStr)>
         <#assign urlReferenceSmallImageExist = "true"/>
       </#if>
       
       <#if urlReferenceSmallImageExist = "false">
         <#assign smallImageFilePath = smallImagePath!smallImagePathOsafe! />
       <#else>
         <#assign smallImageUrlPath = smallImagePath! />
         <#assign smallImageUrlRef = productSmallImageStr />
       </#if>
       
       <#if parameters.smallImageResourceType?exists && parameters.smallImageResourceType?string =='file'>
         <#assign urlReferenceSmallImageExist = "false"/>
       <#elseif parameters.smallImageResourceType?exists && parameters.smallImageResourceType?string =='url'>
         <#assign urlReferenceSmallImageExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="smallImagePathUrlDiv" <#if (urlReferenceSmallImageExist =='false')>style="display:none"</#if>>
                  ${smallImageUrlPath!}
              </div>
              <div class="infoValue" id="smallImagePathFileDiv" <#if (urlReferenceSmallImageExist =='true')>style="display:none"</#if>>
                  <input type="text" name="smallImageFilePath" id="smallImageFilePath" class="large" value="${parameters.smallImageFilePath!smallImageFilePath!smallImagePathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="smallImageResourceType" id="browseFileSmallImage"  value="file" <#if (urlReferenceSmallImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('smallImagePathFileDiv','smallImageFileDiv','smallImagePathUrlDiv','smallImageUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="smallImageResourceType" id="urlRefSmallImage" value="url" <#if (urlReferenceSmallImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('smallImagePathUrlDiv','smallImageUrlRefDiv', 'smallImagePathFileDiv','smallImageFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="smallImageFileDiv" <#if (urlReferenceSmallImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="smallImage" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="smallImageUrlRefDiv" <#if (urlReferenceSmallImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="smallImageUrlRef" id="smallImageUrlRef" class="large" value="${parameters.smallImageUrlRef!smallImageUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
       
    
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.PLPImageTitleTextCaption}</label>
              </div>
              <div class="infoValue">
                <input type="text" name="plpTitleText" id="plpTitleText" class="medium" value="${parameters.plpTitleText!plpTitleText!""}" />
              </div>
          </div>
          <div class="infoIcon">
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.PLPTitleTextInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
       </div>
       
       <#-- Product Small Alt Image -->
      <#assign smallAltImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("SMALL_IMAGE_ALT_URL")! />
      <#if productSmallAltImage?has_content && productSmallAltImage != "">
        <#assign productSmallAltImageStr = productSmallAltImage.toString() />
        <#if productSmallAltImageStr?has_content && (productSmallAltImageStr.lastIndexOf("/") > 0)>
          <#assign smallAltImagePath = productSmallAltImageStr.substring(0, productSmallAltImageStr.lastIndexOf("/")+1) />
          <#assign smallAltImageName = productSmallAltImageStr.substring(productSmallAltImageStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.SmallAltImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productSmallAltImage != "">
                  <img src="<@ofbizContentUrl>${productSmallAltImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productSmallAltImage}" height="${globalContext.IMG_SIZE_PLP_H!""}" width="${globalContext.IMG_SIZE_PLP_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
          </div>
          <div class="infoIcon">
            <#if productSmallAltImage != "">
             <a class="cross" href="javascript:setProdContentTypeId('SMALL_IMAGE_ALT_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
            </#if>
             <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SmallAltImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
       </div>
     
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${smallAltImageName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferenceSmallAltImageExist = "false"/>
       <#if (productSmallAltImageStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productSmallAltImageStr)>
         <#assign urlReferenceSmallAltImageExist = "true"/>
       </#if>
       
       <#if urlReferenceSmallAltImageExist = "false">
         <#assign smallAltImageFilePath = smallAltImagePath!smallAltImagePathOsafe! />
       <#else>
         <#assign smallAltImageUrlPath = smallAltImagePath! />
         <#assign smallAltImageUrlRef = productSmallAltImageStr />
       </#if>
       
       <#if parameters.smallAltImageResourceType?exists && parameters.smallAltImageResourceType?string =='file'>
         <#assign urlReferenceSmallAltImageExist = "false"/>
       <#elseif parameters.smallAltImageResourceType?exists && parameters.smallAltImageResourceType?string =='url'>
         <#assign urlReferenceSmallAltImageExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="smallAltImagePathUrlDiv" <#if (urlReferenceSmallAltImageExist =='false')>style="display:none"</#if>>
                  ${smallAltImageUrlPath!}
              </div>
              <div class="infoValue" id="smallAltImagePathFileDiv" <#if (urlReferenceSmallAltImageExist =='true')>style="display:none"</#if>>
                  <input type="text" name="smallAltImageFilePath" id="smallAltImageFilePath" class="large" value="${parameters.smallAltImageFilePath!smallAltImageFilePath!smallAltImagePathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="smallAltImageResourceType" id="browseFileSmallAltImage"  value="file" <#if (urlReferenceSmallAltImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('smallAltImagePathFileDiv','smallAltImageFileDiv','smallAltImagePathUrlDiv','smallAltImageUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="smallAltImageResourceType" id="urlRefSmallAltImage" value="url" <#if (urlReferenceSmallAltImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('smallAltImagePathUrlDiv','smallAltImageUrlRefDiv', 'smallAltImagePathFileDiv','smallAltImageFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="smallAltImageFileDiv" <#if (urlReferenceSmallAltImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="smallAltImage" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="smallAltImageUrlRefDiv" <#if (urlReferenceSmallAltImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="smallAltImageUrlRef" id="smallAltImageUrlRef" class="large" value="${parameters.smallAltImageUrlRef!smallAltImageUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
</#if>
