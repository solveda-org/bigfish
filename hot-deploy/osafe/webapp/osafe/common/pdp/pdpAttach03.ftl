<li class="${request.getAttribute("attributeClass")!}">
  <#assign windowName = ""/>
    <#if seoPageUrl?has_content>
        <#assign windowName = "${seoPageUrl!}"/>
    <#elseif productName?has_content>
        <#assign windowName = "${productName!}"/>
  </#if>
  <#if ATTACH_URL_03?exists && ATTACH_URL_03?has_content>
    <#if productContentWrapper?exists>
        <#assign attachUrl03 = productContentWrapper.get("ATTACH_URL_03")?if_exists/>
    </#if>
  </#if>
    <div class="pdpAttach03" id="js_pdpAttach03">
        <#if attachUrl03?has_content>
           <div class="displayBox">
             <label>${uiLabelMap.ProductAttachmentCaption}</label>
             <span>
                 <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl03!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment03Link}</span></a>
             </span>
           </div>
        </#if>
    </div>
    <div class="pdpAttach03" id="js_pdpAttach03_Virtual" style="display:none">
        <#if attachUrl03?has_content>
           <div class="displayBox">
             <label>${uiLabelMap.ProductAttachmentCaption}</label>
             <span>
                 <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl03!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment03Link}</span></a>
             </span>
           </div>
        </#if>
    </div>
  
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantAttachUrl03 = ''/>
      <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')!/>
      <#if variantContentIdMap?has_content && variantProdCtntWrapper?has_content >
          <#assign variantContentId = variantContentIdMap.get("ATTACH_URL_03")!""/>
          <#if variantContentId?has_content>
              <#assign variantAttachUrl03 = variantProdCtntWrapper.get("ATTACH_URL_03")?if_exists/>
	      </#if>
      </#if>
      
      <#if variantAttachUrl03?has_content>
	      <div class="pdpAttach03" id="js_pdpAttach03_${key}" style="display:none">
	       <div class="displayBox">
	         <label>${uiLabelMap.ProductAttachmentCaption}</label>
	         <span>
	             <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${variantAttachUrl03!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment03Link}</span></a>
	         </span>
	       </div>
	      </div>
	  <#else>
	      <#if attachUrl03?has_content>
	          <div class="pdpAttach03" id="js_pdpAttach03_${key}" style="display:none">
		       <div class="displayBox">
		         <label>${uiLabelMap.ProductAttachmentCaption}</label>
		         <span>
		             <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl03!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment03Link}</span></a>
		         </span>
		       </div>
		      </div>
	      </#if>
      </#if>
    </#list>
  </#if>
</li>