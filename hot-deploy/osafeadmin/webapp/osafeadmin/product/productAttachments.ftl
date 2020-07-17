<#if product?has_content>
  <#assign attachment = ""/>
  
  <#assign productContents = delegator.findByAnd("ProductContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",product.productId,"productContentTypeId", "ATTACH_URL_0${attachNo}"))/>
  <#if productContents?has_content>
      <#assign productContents = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productContents) />
	  
	  <#if productContents?has_content>
	      <#assign productContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productContents) />
		  <#assign content = productContent.getRelatedOne("Content")!""/>
		  <#assign dataResource = content.getRelatedOne("DataResource")!""/>
		  <#if dataResource?has_content>
			  <#--assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/ -->
			  <#assign attachment = dataResource.objectInfo!""/>
		  </#if>
	  </#if>
  </#if>
  
  
  <#if attachNo?number == 1>
      <input type="hidden" name="productId" value="${product.productId!}"/>
      <input type="hidden" name="productContentTypeId" id="productContentTypeId" value="${parameters.productContentTypeId!}"/>
  </#if>
  
 <div class="displayBox detailInfo">
  <div class="header"><h2>${detailInfoBoxHeading!}</h2></div>
  <div class="boxBody">
    <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
    
      <#-- Product Alt Large Image -->
      <#assign attachmentPathOsafe = Static["com.osafe.util.OsafeAdminUtil"].buildProductImagePathExt("ATTACH_URL_0${attachNo}")! />
      <#if attachment?has_content && attachment != "">
        <#assign attachmentStr = attachment.toString() />
        <#if attachmentStr?has_content && (attachmentStr.lastIndexOf("/") > 0)>
          <#assign attachmentPath = attachmentStr.substring(0, attachmentStr.lastIndexOf("/")+1) />
          <#assign attachmentName = attachmentStr.substring(attachmentStr.lastIndexOf("/")+1) />
        </#if>
      </#if>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.FileNameCaption}</label>
              </div>
              <div class="infoValue">
                ${attachmentName!}
              </div>
              <div class="infoIcon">
	              <#if attachment != "">
	                  <a href="javascript:setProdContentTypeId('ATTACH_URL_0${attachNo}');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
	              </#if>
	          </div>
          </div>
       </div>
     
      <#assign urlReferenceAttachmentExist = "false"/>
       <#if (attachmentStr?has_content) && Static["com.osafe.util.OsafeAdminUtil"].isValidURL(attachmentStr)>
         <#assign urlReferenceAttachmentExist = "true"/>
       </#if>
       
       <#if urlReferenceAttachmentExist = "false">
         <#assign attachmentFilePath = attachmentPath!attachmentPathOsafe! />
       <#else>
         <#assign attachmentUrlPath = attachmentPath! />
         <#assign attachmentUrlRef = attachmentStr />
       </#if>
       
       <#assign attachmentResourceTypeParm = parameters.get("attachmentResourceType_${attachNo}")! />
       <#if attachmentResourceTypeParm?exists && attachmentResourceTypeParm?string =='file'>
         <#assign urlReferenceAttachmentExist = "false"/>
       <#elseif attachmentResourceTypeParm?exists && attachmentResourceTypeParm?string =='url'>
         <#assign urlReferenceAttachmentExist = "true"/>
       </#if>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.FilePathOrUrlCaption}</label>
              </div>
              <div class="infoValue" id="attachmentPathUrlDiv_${attachNo}" <#if (urlReferenceAttachmentExist =='false')>style="display:none"</#if>>
                  ${attachmentUrlPath!}
              </div>
              <div class="infoValue" id="attachmentPathFileDiv_${attachNo}" <#if (urlReferenceAttachmentExist =='true')>style="display:none"</#if>>
                  <#assign attachmentFilePathParm = parameters.get("attachmentFilePath_${attachNo}")!attachmentFilePath!attachmentPathOsafe!""/>
                  <input type="text" name="attachmentFilePath_${attachNo}" id="attachmentFilePath_${attachNo}" class="large" value="${attachmentFilePathParm!}" />
              </div>
          </div>
       </div>
     
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.TypeCaption}</label>
              </div>
              <div class="entry checkbox medium">
                <input class="checkBoxEntry" type="radio" name="attachmentResourceType_${attachNo}" id="browseFileAttachment_${attachNo}"  value="file" <#if (urlReferenceAttachmentExist =='false')>checked="checked"</#if> onchange="changeImageRef('attachmentPathFileDiv_${attachNo}','attachmentFileDiv_${attachNo}','attachmentPathUrlDiv_${attachNo}','attachmentUrlRefDiv_${attachNo}');"/>${uiLabelMap.BrowseAndUploadFileLabel}
                <input class="checkBoxEntry" type="radio" name="attachmentResourceType_${attachNo}" id="urlRefAttachment_${attachNo}" value="url" <#if (urlReferenceAttachmentExist =='true')>checked="checked"</#if> onchange="changeImageRef('attachmentPathUrlDiv_${attachNo}','attachmentUrlRefDiv_${attachNo}', 'attachmentPathFileDiv_${attachNo}','attachmentFileDiv_${attachNo}');"/>${uiLabelMap.URLReferenceLabel}
              </div>
          </div>
       </div>
     
      <div class="infoRow" id="attachmentFileDiv_${attachNo}" <#if (urlReferenceAttachmentExist =='true')>style="display:none"</#if>>
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewFileCaption}</label>
              </div>
              <div class="infoValue">
                  <input type="file" name="attachment_${attachNo}" size="50" value=""/>
              </div>
          </div>
       </div>
       
       <div class="infoRow" id="attachmentUrlRefDiv_${attachNo}" <#if (urlReferenceAttachmentExist =='false')>style="display:none"</#if> >
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewFileCaption}</label>
              </div>
              <div class="infoValue">
                  <#assign attachmentUrlRefParm = parameters.get("attachmentUrlRef_${attachNo}")!attachmentUrlRef!/>
                  <input type="text" name="attachmentUrlRef_${attachNo}" id="attachmentUrlRef_${attachNo}" class="large" value="${attachmentUrlRefParm!""}" />
              </div>
              <div class="infoIcon">
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UrlReferenceInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
              </div>
          </div>
       </div>
    
  </div>
</div>

<#if attachNo?number == 3>
<div class="displayBox footerInfo">
    <div>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonDetailActionButton")}
    </div>
    <div class="infoDetailIcon">
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonProductLinkButton")}
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonDetailHelperIcon")}
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonDetailWarningIcon")}
    </div>
</div>
</#if>
</#if>