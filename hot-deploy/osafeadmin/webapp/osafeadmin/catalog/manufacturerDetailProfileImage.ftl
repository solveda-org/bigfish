<#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
<#-- Product Large Image -->
<#assign largeImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("PROFILE_IMAGE_URL")! />
<div class="infoRow">
  <div class="infoEntry">
    <div class="infoCaption">
      <label>${uiLabelMap.ProfileImageCaption}</label>
    </div>
    <div class="infoValue">
      <#if profileImageUrl?has_content && profileImageUrl != "">
        <img src="<@ofbizContentUrl>${profileImageUrl!}?${curDateTime!}</@ofbizContentUrl>" alt="${profileImageUrlStr!}" height="${globalContext.IMG_SIZE_PROF_MFG_H!""}" width="${globalContext.IMG_SIZE_PROF_MFG_W!""}" class="imageBorder"/>
      <#else>
        <span class="noImage imageBorder"></span>
      </#if>
    </div>
  </div>
  <div class="infoIcon">
    <#if profileImageUrlStr?has_content && profileImageUrlStr != "">
      <a class="helper" href="javascript:javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
    </#if>
  </div>
</div>
<div class="infoRow">
  <div class="infoEntry">
    <div class="infoCaption">
      <label>${uiLabelMap.ImageNameCaption}</label>
    </div>
    <div class="infoValue">
      ${profileImageName!}
    </div>
  </div>
</div>
<#assign urlReferenceLargeImageExist = "false"/>
<#if (profileImageUrlStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(profileImageUrlStr)>
  <#assign urlReferenceLargeImageExist = "true"/>
</#if>
<#if urlReferenceLargeImageExist = "false">
  <#assign profileImageFilePath = profileImagePath!largeImagePathOsafe!"" />
  <#if !profileImageFilePath?has_content>
    <#assign profileImageFilePath = largeImagePathOsafe!"" />
  </#if>
<#else>
  <#assign profileImageUrlString = profileImageUrlStr />
  <#assign profileImageUrl= profileImageUrlStr />
  <#assign profileImageFilePath = largeImagePathOsafe!"" />
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
      ${profileImageUrlString!}
    </div>
    <div class="infoValue" id="largeImagePathFileDiv" <#if (urlReferenceLargeImageExist =='true')>style="display:none"</#if>>
      <input type="text" name="largeImageFilePath" id="largeImageFilePath" class="large" value="${parameters.largeImageFilePath!profileImageFilePath!""}" />
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
      <label>${uiLabelMap.NewProfileImageCaption}</label>
    </div>
    <div class="infoValue">
      <input type="file" name="largeImage" size="50" value=""/>
    </div>
  </div>
</div>
<div class="infoRow bottomRow" id="largeImageUrlRefDiv" <#if (urlReferenceLargeImageExist =='false')>style="display:none"</#if> >
  <div class="infoEntry">
    <div class="infoCaption">
      <label>${uiLabelMap.NewProfileImageCaption}</label>
    </div>
    <div class="infoValue">
      <input type="text" name="largeImageUrlRef" id="largeImageUrlRef" class="large" value="${parameters.largeImageUrlRef!profileImageUrlString!""}" />
    </div>
    <div class="infoIcon">
      <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
    </div>
  </div>
</div>
       
       
      
      
