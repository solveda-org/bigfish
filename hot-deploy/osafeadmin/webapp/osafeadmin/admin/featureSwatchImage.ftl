<#assign productFeatureDataResourcesPDP = delegator.findByAnd("ProductFeatureDataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId",parameters.productFeatureId!,"featureDataResourceTypeId","PDP_SWATCH_IMAGE_URL"))/>
<#if productFeatureDataResourcesPDP?has_content>
  <#assign productFeatureDataResourcePDP = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureDataResourcesPDP) />
  <#assign dataResourcePDP = productFeatureDataResourcePDP.getRelatedOne("DataResource")/>
  <#assign productFeatureResourcePDPUrl = dataResourcePDP.objectInfo!""/>
  <#if productFeatureResourcePDPUrl?has_content>
    <#assign productFeaturePDPSwatchURL=productFeatureResourcePDPUrl/>
  </#if>
</#if>

<#assign productFeatureDataResourcesPLP = delegator.findByAnd("ProductFeatureDataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId",parameters.productFeatureId!,"featureDataResourceTypeId","PLP_SWATCH_IMAGE_URL"))/>
<#if productFeatureDataResourcesPLP?has_content>
  <#assign productFeatureDataResourcePLP = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureDataResourcesPLP) />
  <#assign dataResourcePLP = productFeatureDataResourcePLP.getRelatedOne("DataResource")/>
  <#assign productFeatureResourcePLPUrl = dataResourcePLP.objectInfo!""/>
  <#if productFeatureResourcePLPUrl?has_content>
    <#assign productFeaturePLPSwatchURL=productFeatureResourcePLPUrl/>
  </#if>
</#if>

<#assign plpSwatchImageHeight= IMG_SIZE_PLP_SWATCH_H!""/>
<#assign plpSwatchImageWidth= IMG_SIZE_PLP_SWATCH_W!""/>
<#assign pdpSwatchImageHeight= IMG_SIZE_PDP_SWATCH_H!""/>
<#assign pdpSwatchImageWidth= IMG_SIZE_PDP_SWATCH_W!""/>

  <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
  <input type="hidden" name="productFeatureId" value="${parameters.productFeatureId!}"/>
  <input type="hidden" name="productFeatureGroupId" value="${parameters.productFeatureGroupId!}"/>
  
  <#-- Product Feature PLP Swatch Image -->
      <#assign featureSwatchImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("FEATURE_SWATCH_IMAGE_URL")! />
      <#if productFeaturePLPSwatchURL?has_content && productFeaturePLPSwatchURL != "">
        <#assign productFeaturePLPSwatchURLStr = productFeaturePLPSwatchURL.toString() />
        <#if productFeaturePLPSwatchURLStr?has_content && (productFeaturePLPSwatchURLStr.lastIndexOf("/") > 0)>
          <#assign plpSwatchImagePath = productFeaturePLPSwatchURLStr.substring(0, productFeaturePLPSwatchURLStr.lastIndexOf("/")+1) />
          <#assign plpSwatchImageName = productFeaturePLPSwatchURLStr.substring(productFeaturePLPSwatchURLStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.PLPSwatchImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productFeaturePLPSwatchURL?exists && productFeaturePLPSwatchURL != "">
                  <img src="<@ofbizContentUrl>${productFeaturePLPSwatchURL}?${curDateTime!}</@ofbizContentUrl>" alt="${productFeaturePLPSwatchURL!}" class="imageBorder" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if>/>
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
       <#if (productFeaturePLPSwatchURLStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productFeaturePLPSwatchURLStr)>
         <#assign urlReferencePlpSwatchImageExist = "true"/>
       </#if>
       
       <#if urlReferencePlpSwatchImageExist = "false">
         <#assign plpSwatchImageFilePath = plpSwatchImagePath!featureSwatchImagePathOsafe! />
       <#else>
         <#assign plpSwatchImageUrlPath = plpSwatchImagePath! />
         <#assign plpSwatchImageUrlRef = productFeaturePLPSwatchURLStr />
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
                  <input type="text" name="plpSwatchImageFilePath" id="plpSwatchImageFilePath" class="large" value="${parameters.plpSwatchImageFilePath!plpSwatchImageFilePath!featureSwatchImagePathOsafe!""}" />
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
       
       
       <#-- Product feature PDP Swatch Image -->
      <#if productFeaturePDPSwatchURL?has_content && productFeaturePDPSwatchURL != "">
        <#assign productFeaturePDPSwatchURLStr = productFeaturePDPSwatchURL.toString() />
        <#if productFeaturePDPSwatchURLStr?has_content && (productFeaturePDPSwatchURLStr.lastIndexOf("/") > 0)>
          <#assign pdpSwatchImagePath = productFeaturePDPSwatchURLStr.substring(0, productFeaturePDPSwatchURLStr.lastIndexOf("/")+1) />
          <#assign pdpSwatchImageName = productFeaturePDPSwatchURLStr.substring(productFeaturePDPSwatchURLStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.PDPSwatchImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productFeaturePDPSwatchURL?exists && productFeaturePDPSwatchURL != "">
                  <img src="<@ofbizContentUrl>${productFeaturePDPSwatchURL}?${curDateTime!}</@ofbizContentUrl>" alt="${productFeaturePDPSwatchURL!}" class="imageBorder" <#if pdpSwatchImageHeight != '0' && pdpSwatchImageHeight != ''>height = "${pdpSwatchImageHeight}"</#if> <#if pdpSwatchImageWidth != '0' && pdpSwatchImageWidth != ''>width = "${pdpSwatchImageWidth}"</#if>/>
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
       <#if (productFeaturePDPSwatchURLStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(productFeaturePDPSwatchURLStr)>
         <#assign urlReferencePdpSwatchImageExist = "true"/>
       </#if>
       
       <#if urlReferencePdpSwatchImageExist = "false">
         <#assign pdpSwatchImageFilePath = pdpSwatchImagePath!featureSwatchImagePathOsafe! />
       <#else>
         <#assign pdpSwatchImageUrlPath = pdpSwatchImagePath! />
         <#assign pdpSwatchImageUrlRef = productFeaturePDPSwatchURLStr />
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
                  <input type="text" name="pdpSwatchImageFilePath" id="pdpSwatchImageFilePath" class="large" value="${parameters.pdpSwatchImageFilePath!pdpSwatchImageFilePath!featureSwatchImagePathOsafe!""}" />
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
       
