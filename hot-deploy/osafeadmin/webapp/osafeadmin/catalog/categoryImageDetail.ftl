<#if productCategory?has_content>

  <#-- Product Category Image -->
      <#assign categoryImageUrl = productCategory.categoryImageUrl!/>
      <#assign categoryImagePathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("CATEGORY_IMAGE_URL")! />
      <#if categoryImageUrl?has_content && categoryImageUrl != "">
        <#assign categoryImageUrlStr = categoryImageUrl.toString() />
        <#if categoryImageUrlStr?has_content && (categoryImageUrlStr.lastIndexOf("/") > 0)>
          <#assign categoryImagePath = categoryImageUrlStr.substring(0, categoryImageUrlStr.lastIndexOf("/")+1) />
          <#assign categoryImageName = categoryImageUrlStr.substring(categoryImageUrlStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
      
  <input type="hidden" name="productCategoryId" value="${productCategory.productCategoryId!}"/>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ImageCaption}</label>
      </div>
      <div class="infoValue">
        <#if categoryImageUrl?exists && categoryImageUrl != "">
          <img src="<@ofbizContentUrl>${categoryImageUrl}?${nowTimestamp!}</@ofbizContentUrl>" alt="${categoryImageUrl}" height="${IMG_SIZE_PLP_CAT_H!""}" width="${IMG_SIZE_PLP_CAT_W!""}" class="imageBorder"/>
        <#else>
          <span class="noImage imageBorder"></span>
        </#if>
      </div>
      <div class="infoIcon">
        <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.CategoryImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImageNameCaption}</label>
              </div>
              <div class="infoValue">
                ${categoryImageName!}
              </div>
          </div>
       </div>
       
       <#assign urlReferenceCategoryImageExist = "false"/>
       <#if (categoryImageUrlStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(categoryImageUrlStr)>
         <#assign urlReferenceCategoryImageExist = "true"/>
       </#if>
       
       <#if urlReferenceCategoryImageExist = "false">
         <#assign categoryImageFilePath = categoryImagePath!categoryImagePathOsafe! />
       <#else>
         <#assign categoryImageUrlPath = categoryImagePath! />
         <#assign categoryImageUrlRef = categoryImageUrlStr />
       </#if>
       
       <#if parameters.categoryImageResourceType?exists && parameters.categoryImageResourceType?string =='file'>
         <#assign urlReferenceCategoryImageExist = "false"/>
       <#elseif parameters.categoryImageResourceType?exists && parameters.categoryImageResourceType?string =='url'>
         <#assign urlReferenceCategoryImageExist = "true"/>
       </#if>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ImagePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="categoryImagePathUrlDiv" <#if (urlReferenceCategoryImageExist =='false')>style="display:none"</#if>>
                  ${categoryImageUrlPath!}
              </div>
              <div class="infoValue" id="categoryImagePathFileDiv" <#if (urlReferenceCategoryImageExist =='true')>style="display:none"</#if>>
                  <input type="text" name="categoryImageFilePath" id="categoryImageFilePath" class="large" value="${parameters.categoryImageFilePath!categoryImageFilePath!categoryImagePathOsafe!""}" />
              </div>
          </div>
       </div>
       
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="categoryImageResourceType" id="browseFileCategoryImage"  value="file" <#if (urlReferenceCategoryImageExist =='false')>checked="checked"</#if> onchange="changeImageRef('categoryImagePathFileDiv','categoryImageFileDiv','categoryImagePathUrlDiv','categoryImageUrlRefDiv');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="categoryImageResourceType" id="urlRefCategoryImage" value="url" <#if (urlReferenceCategoryImageExist =='true')>checked="checked"</#if> onchange="changeImageRef('categoryImagePathUrlDiv','categoryImageUrlRefDiv', 'categoryImagePathFileDiv','categoryImageFileDiv');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="categoryImageFileDiv" <#if (urlReferenceCategoryImageExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="categoryImage" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow bottomRow" id="categoryImageUrlRefDiv" <#if (urlReferenceCategoryImageExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewImageCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="text" name="categoryImageUrlRef" id="categoryImageUrlRef" class="large" value="${parameters.categoryImageUrlRef!categoryImageUrlRef!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
       
</#if>
