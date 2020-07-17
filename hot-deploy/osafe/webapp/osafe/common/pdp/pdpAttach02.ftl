<li class="${request.getAttribute("attributeClass")!}">
  <#assign windowName = ""/>
    <#if seoPageUrl?has_content>
        <#assign windowName = "${seoPageUrl!}"/>
    <#elseif productName?has_content>
        <#assign windowName = "${productName!}"/>
  </#if>
  <#if ATTACH_URL_02?exists && ATTACH_URL_02?has_content>
    <#if productContentWrapper?exists>
        <#assign attachUrl02 = productContentWrapper.get("ATTACH_URL_02")?if_exists/>
    </#if>
  </#if>
    <div class="pdpAttach02" id="js_pdpAttach02">
        <#if attachUrl02?has_content>
           <div class="displayBox">
             <label>${uiLabelMap.ProductAttachmentCaption}</label>
             <span>
                 <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl02!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment02Link}</span></a>
             </span>
           </div>
        </#if>
    </div>
    <div class="pdpAttach02" id="js_pdpAttach02_Virtual" style="display:none">
        <#if attachUrl02?has_content>
           <div class="displayBox">
             <label>${uiLabelMap.ProductAttachmentCaption}</label>
             <span>
                 <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl02!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment02Link}</span></a>
             </span>
           </div>
        </#if>
    </div>
  
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantAttachUrl02 = ''/>
      <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')!/>
      <#if variantContentIdMap?has_content && variantProdCtntWrapper?has_content >
          <#assign variantContentId = variantContentIdMap.get("ATTACH_URL_02")!""/>
          <#if variantContentId?has_content>
              <#assign variantAttachUrl02 = variantProdCtntWrapper.get("ATTACH_URL_02")?if_exists/>
	      </#if>
      </#if>
      
      <#if variantAttachUrl02?has_content>
	      <div class="pdpAttach02" id="js_pdpAttach02_${key}" style="display:none">
	       <div class="displayBox">
	         <label>${uiLabelMap.ProductAttachmentCaption}</label>
	         <span>
	             <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${variantAttachUrl02!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment02Link}</span></a>
	         </span>
	       </div>
	      </div>
	  <#else>
	      <#if attachUrl02?has_content>
	          <div class="pdpAttach02" id="js_pdpAttach02_${key}" style="display:none">
		       <div class="displayBox">
		         <label>${uiLabelMap.ProductAttachmentCaption}</label>
		         <span>
		             <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl02!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment02Link}</span></a>
		         </span>
		       </div>
		      </div>
	      </#if>
      </#if>
    </#list>
  </#if>
</li>