<#if spotsLists?exists?has_content>
	<#list spotsList as subItem >
			<#assign sequenceNum = subItem.sequenceNum/>
          <div id="eCommerceHomeSpot_${sequenceNum}" class="eCommerceHotSpot">
	        <@renderSubContent subContentId=subItem.contentIdTo/>
	      </div>
	</#list>
</#if>
