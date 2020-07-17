<div class="displayBox detailInfo">
  <div class="header"><h2>${detailInfoBoxHeading!}</h2></div>
  <div class="boxBody">
    <#if product?has_content>
      <#if productContentWrapper?exists>
        <#assign plpSwatchImageURL = productContentWrapper.get("PLP_SWATCH_IMAGE_URL")!""/>
        <#assign pdpSwatchImageURL = productContentWrapper.get("PDP_SWATCH_IMAGE_URL")!""/>
      </#if>
      <#assign plpSwatchImageHeight= globalContext.IMG_SIZE_PLP_SWATCH_H!""/>
      <#assign plpSwatchImageWidth= globalContext.IMG_SIZE_PLP_SWATCH_W!""/>
      <#assign pdpSwatchImageHeight= globalContext.IMG_SIZE_PDP_SWATCH_H!""/>
      <#assign pdpSwatchImageWidth= globalContext.IMG_SIZE_PDP_SWATCH_W!""/>
      <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
      
      <#-- Product PLP Swatch Image -->
      <#assign plpSwatchImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("PLP_SWATCH_IMAGE_URL")! />
      <#if plpSwatchImageURL?has_content && plpSwatchImageURL != "">
        <#assign plpSwatchImageURLStr = plpSwatchImageURL.toString() />
        <#if plpSwatchImageURLStr?has_content && (plpSwatchImageURLStr.lastIndexOf("/") > 0)>
          <#assign plpSwatchImagePath = plpSwatchImageURLStr.substring(0, plpSwatchImageURLStr.lastIndexOf("/")+1) />
          <#assign plpSwatchImageName = plpSwatchImageURLStr.substring(plpSwatchImageURLStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
      
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.PLPSwatchImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if plpSwatchImageURL?exists && plpSwatchImageURL != "">
              <img src="<@ofbizContentUrl>${plpSwatchImageURL}?${curDateTime!}</@ofbizContentUrl>" alt="${plpSwatchImageURL!}" class="imageBorder" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if>/>
              <a href="javascript:setProdContentTypeId('PLP_SWATCH_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
        </div>
      </div>
      
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${plpSwatchImageName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferencePlpSwatchImageExist = "false"/>
       <#if (plpSwatchImageURLStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(plpSwatchImageURLStr)>
         <#assign urlReferencePlpSwatchImageExist = "true"/>
       </#if>
       
       <#if urlReferencePlpSwatchImageExist = "false">
         <#assign plpSwatchImageFilePath = plpSwatchImagePath!plpSwatchImagePathOsafe! />
       <#else>
         <#assign plpSwatchImageUrlPath = plpSwatchImagePath! />
         <#assign plpSwatchImageUrlRef = plpSwatchImageURLStr />
       </#if>
       
       <#if parameters.plpSwatchImageResourceType?exists && parameters.plpSwatchImageResourceType?string =='file'>
         <#assign urlReferencePlpSwatchImageExist = "false"/>
       <#elseif parameters.plpSwatchImageResourceType?exists && parameters.plpSwatchImageResourceType?string =='url'>
         <#assign urlReferencePlpSwatchImageExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="plpSwatchImagePathUrlDiv" <#if (urlReferencePlpSwatchImageExist =='false')>style="display:none"</#if>>
                  ${plpSwatchImageUrlPath!}
              </div>
              <div class="infoValue" id="plpSwatchImagePathFileDiv" <#if (urlReferencePlpSwatchImageExist =='true')>style="display:none"</#if>>
                  <input type="text" name="plpSwatchImageFilePath" id="plpSwatchImageFilePath" class="large" value="${parameters.plpSwatchImageFilePath!plpSwatchImageFilePath!plpSwatchImagePathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="plpSwatchImageResourceType" id="browseFilePlpSwatchImage"  value="file" <#if (urlReferencePlpSwatchImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('plpSwatchImagePathFileDiv','plpSwatchImageFileDiv','plpSwatchImagePathUrlDiv','plpSwatchImageUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="plpSwatchImageResourceType" id="urlRefPlpSwatchImage" value="url" <#if (urlReferencePlpSwatchImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('plpSwatchImagePathUrlDiv','plpSwatchImageUrlRefDiv', 'plpSwatchImagePathFileDiv','plpSwatchImageFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="plpSwatchImageFileDiv" <#if (urlReferencePlpSwatchImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewPLPSwatchImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="plpSwatchImage" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="plpSwatchImageUrlRefDiv" <#if (urlReferencePlpSwatchImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewPLPSwatchImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="plpSwatchImageUrlRef" id="plpSwatchImageUrlRef" class="large" value="${parameters.plpSwatchImageUrlRef!plpSwatchImageUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
      
      <#-- Product PDP Swatch Image -->
      <#assign pdpSwatchImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("PDP_SWATCH_IMAGE_URL")! />
      <#if pdpSwatchImageURL?has_content && pdpSwatchImageURL != "">
        <#assign pdpSwatchImageURLStr = pdpSwatchImageURL.toString() />
        <#if pdpSwatchImageURLStr?has_content && (pdpSwatchImageURLStr.lastIndexOf("/") > 0)>
          <#assign pdpSwatchImagePath = pdpSwatchImageURLStr.substring(0, pdpSwatchImageURLStr.lastIndexOf("/")+1) />
          <#assign pdpSwatchImageName = pdpSwatchImageURLStr.substring(pdpSwatchImageURLStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.PDPSwatchImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if pdpSwatchImageURL?exists && pdpSwatchImageURL != "">
              <img src="<@ofbizContentUrl>${pdpSwatchImageURL}?${curDateTime!}</@ofbizContentUrl>" alt="${pdpSwatchImageURL!}" class="imageBorder" <#if pdpSwatchImageHeight != '0' && pdpSwatchImageHeight != ''>height = "${pdpSwatchImageHeight}"</#if> <#if pdpSwatchImageWidth != '0' && pdpSwatchImageWidth != ''>width = "${pdpSwatchImageWidth}"</#if>/>
              <a href="javascript:setProdContentTypeId('PDP_SWATCH_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
        </div>
      </div>
     
     <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${pdpSwatchImageName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferencePdpSwatchImageExist = "false"/>
       <#if (pdpSwatchImageURLStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(pdpSwatchImageURLStr)>
         <#assign urlReferencePdpSwatchImageExist = "true"/>
       </#if>
       
       <#if urlReferencePdpSwatchImageExist = "false">
         <#assign pdpSwatchImageFilePath = pdpSwatchImagePath!pdpSwatchImagePathOsafe! />
       <#else>
         <#assign pdpSwatchImageUrlPath = pdpSwatchImagePath! />
         <#assign pdpSwatchImageUrlRef = pdpSwatchImageURLStr />
       </#if>
       
       <#if parameters.pdpSwatchImageResourceType?exists && parameters.pdpSwatchImageResourceType?string =='file'>
         <#assign urlReferencePdpSwatchImageExist = "false"/>
       <#elseif parameters.pdpSwatchImageResourceType?exists && parameters.pdpSwatchImageResourceType?string =='url'>
         <#assign urlReferencePdpSwatchImageExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="pdpSwatchImagePathUrlDiv" <#if (urlReferencePdpSwatchImageExist =='false')>style="display:none"</#if>>
                  ${pdpSwatchImageUrlPath!}
              </div>
              <div class="infoValue" id="pdpSwatchImagePathFileDiv" <#if (urlReferencePdpSwatchImageExist =='true')>style="display:none"</#if>>
                  <input type="text" name="pdpSwatchImageFilePath" id="pdpSwatchImageFilePath" class="large" value="${parameters.pdpSwatchImageFilePath!pdpSwatchImageFilePath!pdpSwatchImagePathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="pdpSwatchImageResourceType" id="browseFilePdpSwatchImage"  value="file" <#if (urlReferencePdpSwatchImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('pdpSwatchImagePathFileDiv','pdpSwatchImageFileDiv','pdpSwatchImagePathUrlDiv','pdpSwatchImageUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="pdpSwatchImageResourceType" id="urlRefPdpSwatchImage" value="url" <#if (urlReferencePdpSwatchImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('pdpSwatchImagePathUrlDiv','pdpSwatchImageUrlRefDiv', 'pdpSwatchImagePathFileDiv','pdpSwatchImageFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="pdpSwatchImageFileDiv" <#if (urlReferencePdpSwatchImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewPDPSwatchImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="pdpSwatchImage" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="pdpSwatchImageUrlRefDiv" <#if (urlReferencePdpSwatchImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewPDPSwatchImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="pdpSwatchImageUrlRef" id="pdpSwatchImageUrlRef" class="large" value="${parameters.pdpSwatchImageUrlRef!pdpSwatchImageUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
       
      
    </#if>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonDetailActionButton")}
    <div class="infoDetailIcon">
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonProductLinkButton")}
    </div>
  </div>
</div>
