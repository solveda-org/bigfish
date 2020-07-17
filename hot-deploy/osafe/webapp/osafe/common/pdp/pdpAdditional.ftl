<li class="${request.getAttribute("attributeClass")!}">
  <#if (pdpEspotContent?exists && pdpEspotContent?has_content)>
    <#if ((pdpEspotContent.statusId)?if_exists == "CTNT_PUBLISHED")>
		 <div class="pdpAdditional">
		        <@renderContentAsText contentId="${pdpEspotContent.contentId}" ignoreTemplate="true"/>
		 </div>
    </#if>
  </#if>
</li>
