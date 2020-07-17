<#if product?has_content>
  <#if productContentWrapper?exists>
    <#assign altLargeImage = productContentWrapper.get("XTRA_IMG_${altImgNo}_LARGE")!""/>
    <#assign altThumbnailImage = productContentWrapper.get("ADDITIONAL_IMAGE_${altImgNo}")!""/>
    <#assign altDetailImage = productContentWrapper.get("XTRA_IMG_${altImgNo}_DETAIL")!""/>
  </#if>
  <#if altImgNo?number == 1 || altLargeImage !='' || altThumbnailImage != '' || altDetailImage != ''>
    <#assign imageExists = 'true'>
  <#else>
    <#assign prevAltImageNo = altImgNo?number - 1/>
    <#assign prevAltLargeImage = productContentWrapper.get("XTRA_IMG_${prevAltImageNo}_LARGE")!""/>
    <#assign prevAltThumbnailImage = productContentWrapper.get("ADDITIONAL_IMAGE_${prevAltImageNo}")!""/>
    <#assign prevAltDetailImage = productContentWrapper.get("XTRA_IMG_${prevAltImageNo}_DETAIL")!""/>
    
    <#if prevAltLargeImage !='' || prevAltThumbnailImage != '' || prevAltDetailImage != ''>
      <#assign imageExists = 'true'>
    <#else>
      <#assign imageExists = 'false'>
    </#if>
  </#if>
<div class="displayBox detailInfo <#if imageExists == 'false'>slidingClose</#if>">
  <div class="header"><h2>${detailInfoBoxHeading!}</h2></div>
  <div class="boxBody">
    <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
    
      <#-- Product Alt Large Image -->
      <#assign altLargeImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("XTRA_IMG_${altImgNo}_LARGE")! />
      <#if altLargeImage?has_content && altLargeImage != "">
        <#assign altLargeImageStr = altLargeImage.toString() />
        <#if altLargeImageStr?has_content && (altLargeImageStr.lastIndexOf("/") > 0)>
          <#assign altLargeImagePath = altLargeImageStr.substring(0, altLargeImageStr.lastIndexOf("/")+1) />
          <#assign altLargeImageName = altLargeImageStr.substring(altLargeImageStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.LargeImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if altLargeImage != "">
              <img src="<@ofbizContentUrl>${altLargeImage!}?${curDateTime!}</@ofbizContentUrl>" alt="${altLargeImage!}" height="${globalContext.IMG_SIZE_PDP_REG_H!""}" width="${globalContext.IMG_SIZE_PDP_REG_W!""}" class="imageBorder"/>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
        </div>
        <div class="infoIcon">
         <#if altLargeImage != "">
           <a href="javascript:setProdContentTypeId('XTRA_IMG_${altImgNo}_LARGE');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
         </#if>
           <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.LargeAltPDPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
        </div>
      </div>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${altLargeImageName!}
              </div>
          </div>
       </div>
     
      <#assign urlReferenceAltLargeImageExist = "false"/>
       <#if (altLargeImageStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(altLargeImageStr)>
         <#assign urlReferenceAltLargeImageExist = "true"/>
       </#if>
       
       <#if urlReferenceAltLargeImageExist = "false">
         <#assign altLargeImageFilePath = altLargeImagePath!altLargeImagePathOsafe! />
       <#else>
         <#assign altLargeImageUrlPath = altLargeImagePath! />
         <#assign altLargeImageUrlRef = altLargeImageStr />
       </#if>
       
       <#assign altLargeImageResourceTypeParm = parameters.get("altLargeImageResourceType_${altImgNo}")! />
       <#if altLargeImageResourceTypeParm?exists && altLargeImageResourceTypeParm?string =='file'>
         <#assign urlReferenceAltLargeImageExist = "false"/>
       <#elseif altLargeImageResourceTypeParm?exists && altLargeImageResourceTypeParm?string =='url'>
         <#assign urlReferenceAltLargeImageExist = "true"/>
       </#if>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="altLargeImagePathUrlDiv_${altImgNo}" <#if (urlReferenceAltLargeImageExist =='false')>style="display:none"</#if>>
                  ${altLargeImageUrlPath!}
              </div>
              <div class="infoValue" id="altLargeImagePathFileDiv_${altImgNo}" <#if (urlReferenceAltLargeImageExist =='true')>style="display:none"</#if>>
                  <#assign altLargeImageFilePathParm = parameters.get("altLargeImageFilePath_${altImgNo}")!altLargeImageFilePath!altLargeImagePathOsafe!""/>
                  <input type="text" name="altLargeImageFilePath_${altImgNo}" id="altLargeImageFilePath_${altImgNo}" class="large" value="${altLargeImageFilePathParm!}" />
              </div>
          </div>
       </div>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="altLargeImageResourceType_${altImgNo}" id="browseFileAltLargeImage_${altImgNo}"  value="file" <#if (urlReferenceAltLargeImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('altLargeImagePathFileDiv_${altImgNo}','altLargeImageFileDiv_${altImgNo}','altLargeImagePathUrlDiv_${altImgNo}','altLargeImageUrlRefDiv_${altImgNo}');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="altLargeImageResourceType_${altImgNo}" id="urlRefAltLargeImage_${altImgNo}" value="url" <#if (urlReferenceAltLargeImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('altLargeImagePathUrlDiv_${altImgNo}','altLargeImageUrlRefDiv_${altImgNo}', 'altLargeImagePathFileDiv_${altImgNo}','altLargeImageFileDiv_${altImgNo}');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
     
      <div class="infoRow bottomRow" id="altLargeImageFileDiv_${altImgNo}" <#if (urlReferenceAltLargeImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="altLargeImage_${altImgNo}" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="altLargeImageUrlRefDiv_${altImgNo}" <#if (urlReferenceAltLargeImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <#assign altLargeImageUrlRefParm = parameters.get("altLargeImageUrlRef_${altImgNo}")!altLargeImageUrlRef!/>
                  <input type="text" name="altLargeImageUrlRef_${altImgNo}" id="altLargeImageUrlRef_${altImgNo}" class="large" value="${altLargeImageUrlRefParm!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
     
     <#-- Product Alt Thumb Image -->
      
      <#assign altThumbnailImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("ADDITIONAL_IMAGE_${altImgNo}")! />
      <#if altThumbnailImage?has_content && altThumbnailImage != "">
        <#assign altThumbnailImageStr = altThumbnailImage.toString() />
        <#if altThumbnailImageStr?has_content && (altThumbnailImageStr.lastIndexOf("/") > 0)>
          <#assign altThumbnailImagePath = altThumbnailImageStr.substring(0, altThumbnailImageStr.lastIndexOf("/")+1) />
          <#assign altThumbnailImageName = altThumbnailImageStr.substring(altThumbnailImageStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
      
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.ThumbnailImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if altThumbnailImage != "">
              <img src="<@ofbizContentUrl>${altThumbnailImage}?${curDateTime!}</@ofbizContentUrl>" alt="${altThumbnailImage}" height="${globalContext.IMG_SIZE_PDP_THUMB_H!""}" width="${globalContext.IMG_SIZE_PDP_THUMB_W!""}" class="imageBorder"/>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
          <div class="infoIcon">
            <#if altThumbnailImage != "">
              <a href="javascript:setProdContentTypeId('ADDITIONAL_IMAGE_${altImgNo}');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
            </#if>
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.ThumbAltPDPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
        </div>
      </div>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${altThumbnailImageName!}
              </div>
          </div>
       </div>
     
      <#assign urlReferenceAltThumbnailImageExist = "false"/>
       <#if (altThumbnailImageStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(altThumbnailImageStr)>
         <#assign urlReferenceAltThumbnailImageExist = "true"/>
       </#if>
       
       <#if urlReferenceAltThumbnailImageExist = "false">
         <#assign altThumbnailImageFilePath = altThumbnailImagePath!altThumbnailImagePathOsafe! />
       <#else>
         <#assign altThumbnailImageUrlPath = altThumbnailImagePath! />
         <#assign altThumbnailImageUrlRef = altThumbnailImageStr />
       </#if>
       
       <#assign altThumbnailImageResourceTypeParm = parameters.get("altThumbnailImageResourceType_${altImgNo}")! />
       <#if altThumbnailImageResourceTypeParm?exists && altThumbnailImageResourceTypeParm?string =='file'>
         <#assign urlReferenceAltThumbnailImageExist = "false"/>
       <#elseif altThumbnailImageResourceTypeParm?exists && altThumbnailImageResourceTypeParm?string =='url'>
         <#assign urlReferenceAltThumbnailImageExist = "true"/>
       </#if>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="altThumbnailImagePathUrlDiv_${altImgNo}" <#if (urlReferenceAltThumbnailImageExist =='false')>style="display:none"</#if>>
                  ${altThumbnailImageUrlPath!}
              </div>
              <div class="infoValue" id="altThumbnailImagePathFileDiv_${altImgNo}" <#if (urlReferenceAltThumbnailImageExist =='true')>style="display:none"</#if>>
                  <#assign altThumbnailImageFilePathParm = parameters.get("altThumbnailImageFilePath_${altImgNo}")!altThumbnailImageFilePath!altThumbnailImagePathOsafe!""/>
                  <input type="text" name="altThumbnailImageFilePath_${altImgNo}" id="altThumbnailImageFilePath_${altImgNo}" class="large" value="${altThumbnailImageFilePathParm!}" />
              </div>
          </div>
       </div>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="altThumbnailImageResourceType_${altImgNo}" id="browseFileAltThumbnailImage_${altImgNo}"  value="file" <#if (urlReferenceAltThumbnailImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('altThumbnailImagePathFileDiv_${altImgNo}','altThumbnailImageFileDiv_${altImgNo}','altThumbnailImagePathUrlDiv_${altImgNo}','altThumbnailImageUrlRefDiv_${altImgNo}');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="altThumbnailImageResourceType_${altImgNo}" id="urlRefAltThumbnailImage_${altImgNo}" value="url" <#if (urlReferenceAltThumbnailImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('altThumbnailImagePathUrlDiv_${altImgNo}','altThumbnailImageUrlRefDiv_${altImgNo}', 'altThumbnailImagePathFileDiv_${altImgNo}','altThumbnailImageFileDiv_${altImgNo}');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
     
      <div class="infoRow bottomRow" id="altThumbnailImageFileDiv_${altImgNo}" <#if (urlReferenceAltThumbnailImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="altThumbImage_${altImgNo}" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="altThumbnailImageUrlRefDiv_${altImgNo}" <#if (urlReferenceAltThumbnailImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <#assign altThumbnailImageUrlRefParm = parameters.get("altThumbnailImageUrlRef_${altImgNo}")!altThumbnailImageUrlRef!/>
                  <input type="text" name="altThumbnailImageUrlRef_${altImgNo}" id="altThumbnailImageUrlRef_${altImgNo}" class="large" value="${altThumbnailImageUrlRefParm!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
     
      <#-- Product Alt Detail Image -->
      <#assign altDetailImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("XTRA_IMG_${altImgNo}_DETAIL")! />
      <#if altDetailImage?has_content && altDetailImage != "">
        <#assign altDetailImageStr = altDetailImage.toString() />
        <#if altDetailImageStr?has_content && (altDetailImageStr.lastIndexOf("/") > 0)>
          <#assign altDetailImagePath = altDetailImageStr.substring(0, altDetailImageStr.lastIndexOf("/")+1) />
          <#assign altDetailImageName = altDetailImageStr.substring(altDetailImageStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
     
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.PopUpDetailImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if altDetailImage != "">
              <img src="<@ofbizContentUrl>${altDetailImage}?${curDateTime!}</@ofbizContentUrl>" alt="${altDetailImage}" height="${globalContext.IMG_SIZE_PDP_POPUP_H!""}" width="${globalContext.IMG_SIZE_PDP_POPUP_W!""}" class="imageBorder"/>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
          <div class="infoIcon">
            <#if altDetailImage != "">
              <a href="javascript:setProdContentTypeId('XTRA_IMG_${altImgNo}_DETAIL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
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
                ${altDetailImageName!}
              </div>
          </div>
       </div>
     
      <#assign urlReferenceAltDetailImageExist = "false"/>
       <#if (altDetailImageStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(altDetailImageStr)>
         <#assign urlReferenceAltDetailImageExist = "true"/>
       </#if>
       
       <#if urlReferenceAltDetailImageExist = "false">
         <#assign altDetailImageFilePath = altDetailImagePath!altDetailImagePathOsafe! />
       <#else>
         <#assign altDetailImageUrlPath = altDetailImagePath! />
         <#assign altDetailImageUrlRef = altDetailImageStr />
       </#if>
       
       <#assign altDetailImageResourceTypeParm = parameters.get("altDetailImageResourceType_${altImgNo}")! />
       <#if altDetailImageResourceTypeParm?exists && altDetailImageResourceTypeParm?string =='file'>
         <#assign urlReferenceAltDetailImageExist = "false"/>
       <#elseif altDetailImageResourceTypeParm?exists && altDetailImageResourceTypeParm?string =='url'>
         <#assign urlReferenceAltDetailImageExist = "true"/>
       </#if>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="altDetailImagePathUrlDiv_${altImgNo}" <#if (urlReferenceAltDetailImageExist =='false')>style="display:none"</#if>>
                  ${altDetailImageUrlPath!}
              </div>
              <div class="infoValue" id="altDetailImagePathFileDiv_${altImgNo}" <#if (urlReferenceAltDetailImageExist =='true')>style="display:none"</#if>>
                  <#assign altDetailImageFilePathParm = parameters.get("altDetailImageFilePath_${altImgNo}")!altDetailImageFilePath!altDetailImagePathOsafe!""/>
                  <input type="text" name="altDetailImageFilePath_${altImgNo}" id="altDetailImageFilePath_${altImgNo}" class="large" value="${altDetailImageFilePathParm!}" />
              </div>
          </div>
       </div>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="altDetailImageResourceType_${altImgNo}" id="browseFileAltDetailImage_${altImgNo}"  value="file" <#if (urlReferenceAltDetailImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('altDetailImagePathFileDiv_${altImgNo}','altDetailImageFileDiv_${altImgNo}','altDetailImagePathUrlDiv_${altImgNo}','altDetailImageUrlRefDiv_${altImgNo}');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="altDetailImageResourceType_${altImgNo}" id="urlRefAltDetailImage_${altImgNo}" value="url" <#if (urlReferenceAltDetailImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('altDetailImagePathUrlDiv_${altImgNo}','altDetailImageUrlRefDiv_${altImgNo}', 'altDetailImagePathFileDiv_${altImgNo}','altDetailImageFileDiv_${altImgNo}');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
     
      <div class="infoRow bottomRow" id="altDetailImageFileDiv_${altImgNo}" <#if (urlReferenceAltDetailImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="altDetailImage_${altImgNo}" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="altDetailImageUrlRefDiv_${altImgNo}" <#if (urlReferenceAltDetailImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <#assign altDetailImageUrlRefParm = parameters.get("altDetailImageUrlRef_${altImgNo}")!altDetailImageUrlRef!/>
                  <input type="text" name="altDetailImageUrlRef_${altImgNo}" id="altDetailImageUrlRef_${altImgNo}" class="large" value="${altDetailImageUrlRefParm!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
     
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonDetailActionButton")}
      <div class="infoDetailIcon">
       ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonProductLinkButton")}
      </div>
    </#if>
  </div>
</div>