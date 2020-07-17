<li class="${request.getAttribute("attributeClass")!}">
  <#assign windowName = ""/>
    <#if seoPageUrl?has_content>
        <#assign windowName = "${seoPageUrl!}"/>
    <#elseif productName?has_content>
        <#assign windowName = "${productName!}"/>
  </#if>
  <#if ATTACH_URL_01?exists && ATTACH_URL_01?has_content>
    <#if productContentWrapper?exists>
        <#assign attachUrl01 = productContentWrapper.get("ATTACH_URL_01")?if_exists/>
    </#if>
  </#if>
    <div class="pdpAttach01" id="js_pdpAttach01">
        <#if attachUrl01?has_content>
	       <div class="displayBox">
	         <label>${uiLabelMap.ProductAttachmentCaption}</label>
	         <span>
	             <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl01!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment01Link}</span></a>
	         </span>
	       </div>
        </#if>
    </div>
    <div class="pdpAttach01" id="js_pdpAttach01_Virtual" style="display:none">
        <#if attachUrl01?has_content>
           <div class="displayBox">
             <label>${uiLabelMap.ProductAttachmentCaption}</label>
             <span>
                 <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl01!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment01Link}</span></a>
             </span>
           </div>
        </#if>
    </div>
  
  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign variantContentId = ''/>
      <#assign variantAttachUrl01 = ''/>
      <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
      <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')!/>
      <#if variantContentIdMap?has_content && variantProdCtntWrapper?has_content >
          <#assign variantContentId = variantContentIdMap.get("ATTACH_URL_01")!""/>
          <#if variantContentId?has_content>
              <#assign variantAttachUrl01 = variantProdCtntWrapper.get("ATTACH_URL_01")?if_exists/>
	      </#if>
      </#if>
      
      <#if variantAttachUrl01?has_content>
	      <div class="pdpAttach01" id="js_pdpAttach01_${key}" style="display:none">
	       <div class="displayBox">
	         <label>${uiLabelMap.ProductAttachmentCaption}</label>
	         <span>
	             <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${variantAttachUrl01!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment01Link}</span></a>
	         </span>
	       </div>
	      </div>
	  <#else>
	      <#if attachUrl01?has_content>
	          <div class="pdpAttach01" id="js_pdpAttach01_${key}" style="display:none">
		       <div class="displayBox">
		         <label>${uiLabelMap.ProductAttachmentCaption}</label>
		         <span>
		             <a href="javascript:void(0);" onclick="javascript:newPopupWindow('<@ofbizContentUrl>${attachUrl01!}</@ofbizContentUrl>', '${windowName}')"><span>${uiLabelMap.ProductAttachment01Link}</span></a>
		         </span>
		       </div>
		      </div>
	      </#if>
      </#if>
    </#list>
  </#if>
</li>
