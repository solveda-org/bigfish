<li class="${request.getAttribute("attributeClass")!}">
    <#if PDP_SPC_CONTENT_01?exists && PDP_SPC_CONTENT_01?has_content>
        <#assign content = context.get("${PDP_SPC_CONTENT_01}")!/>
    </#if>
  
    <#if content?has_content && ((content.statusId)?if_exists == "CTNT_PUBLISHED")>
        <div class="pdpSpecificContent01" id="js_pdpSpecificContent01">
            <@renderContentAsText contentId="${PDP_SPC_CONTENT_01}" ignoreTemplate="true"/>
        </div>
    </#if>
    
    <#if content?has_content && ((content.statusId)?if_exists == "CTNT_PUBLISHED")>
	    <div class="pdpSpecificContent01" id="js_pdpSpecificContent01_Virtual" style="display:none">
	        <@renderContentAsText contentId="${PDP_SPC_CONTENT_01}" ignoreTemplate="true"/>
	    </div>
    </#if>
    
    <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
        <#list productVariantMapKeys as key>
            <#assign variantContentId = ''/>
            <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
            <#if variantContentIdMap?has_content>
      	        <#assign variantContentId = variantContentIdMap.get("PDP_SPC_CONTENT_01")!""/>
            </#if>
            <#if variantContentId?has_content>
                <#if productVariantContentMap?has_content>
                    <#assign variantContent = productVariantContentMap.get("${variantContentId}")!/>
                </#if>
                <#if variantContent?has_content && ((variantContent.statusId)?if_exists == "CTNT_PUBLISHED")>
	                <div class="pdpSpecificContent01" id="js_pdpSpecificContent01_${key}" style="display:none">
	                    <@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/>
	                </div>
	            </#if>
            <#else>
                <div class="pdpSpecificContent01" id="js_pdpSpecificContent01_${key}" style="display:none">
                    <#if content?has_content && ((content.statusId)?if_exists == "CTNT_PUBLISHED")>
                        <@renderContentAsText contentId="${PDP_SPC_CONTENT_01}" ignoreTemplate="true"/>
                    </#if>
                </div>
            </#if>
        </#list>
    </#if>
</li>

