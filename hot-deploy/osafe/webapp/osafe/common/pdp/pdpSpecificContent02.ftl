<li class="${request.getAttribute("attributeClass")!}">
    <#if PDP_SPC_CONTENT_02?exists && PDP_SPC_CONTENT_02?has_content>
        <#assign content = context.get("${PDP_SPC_CONTENT_02}")!/>
    </#if>
  
    <#if content?has_content && ((content.statusId)?if_exists == "CTNT_PUBLISHED")>
        <div class="pdpSpecificContent02" id="js_pdpSpecificContent02">
            <@renderContentAsText contentId="${PDP_SPC_CONTENT_02}" ignoreTemplate="true"/>
        </div>
    </#if>
    
    <#if content?has_content && ((content.statusId)?if_exists == "CTNT_PUBLISHED")>
	    <div class="pdpSpecificContent02" id="js_pdpSpecificContent02_Virtual" style="display:none">
	        <@renderContentAsText contentId="${PDP_SPC_CONTENT_02}" ignoreTemplate="true"/>
	    </div>
    </#if>
    
    <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
        <#list productVariantMapKeys as key>
            <#assign variantContentId = ''/>
            <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
            <#if variantContentIdMap?has_content>
      	        <#assign variantContentId = variantContentIdMap.get("PDP_SPC_CONTENT_02")!""/>
            </#if>
            <#if variantContentId?has_content>
                <#if productVariantContentMap?has_content>
                    <#assign variantContent = productVariantContentMap.get("${variantContentId}")!/>
                </#if>
                <#if variantContent?has_content && ((variantContent.statusId)?if_exists == "CTNT_PUBLISHED")>
	                <div class="pdpSpecificContent02" id="js_pdpSpecificContent02_${key}" style="display:none">
	                    <@renderContentAsText contentId="${variantContentId}" ignoreTemplate="true"/>
	                </div>
	            </#if>
            <#else>
                <div class="pdpSpecificContent02" id="js_pdpSpecificContent02_${key}" style="display:none">
                    <#if content?has_content && ((content.statusId)?if_exists == "CTNT_PUBLISHED")>
                        <@renderContentAsText contentId="${PDP_SPC_CONTENT_02}" ignoreTemplate="true"/>
                    </#if>
                </div>
            </#if>
        </#list>
    </#if>
</li>

