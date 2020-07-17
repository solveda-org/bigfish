<tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.BigFishContentTypeLabel}</th>
    <th class="nameCol">${uiLabelMap.NameLabel}</th>
    <th class="statusCol">${uiLabelMap.StatusLabel}</th>
    <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
    <th class="dateCol">${uiLabelMap.ActiveDateLabel}</th>
    <th class="dateCol">${uiLabelMap.CreatedDateLabel}</th>
    <th class="actionColSmall"></th>
</tr>
            
<#if resultList?has_content>
    <#assign defaultStatusId = "CTNT_IN_PROGRESS" /> 
    <#assign defaultStatus = delegator.findOne("StatusItem", {"statusId" : defaultStatusId}, false)>
            
    <#assign rowClass = "1">          
    <#list resultList as content>
        <#assign thisContent  = delegator.findByPrimaryKey("Content",Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId",content.contentId))/>
        <#assign hasNext = content_has_next>
    	<#assign statusId = thisContent.statusId!"CTNT_DEACTIVATED" /> 			
           
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
            <td class="idCol firstCol" >
                <a href="<@ofbizUrl>${detailPage}?contentId=${thisContent.contentId}&productId=${productId}&productContentTypeId=${content.productContentTypeId!}</@ofbizUrl>">${content.productContentTypeId!"N/A"}</a>
            </td>
            
            <td class="nameCol <#if !hasNext>lastRow</#if>" >
			    ${thisContent.contentName!"N/A"}
 		    </td>
 		    
            <td class="statusCol <#if !hasNext>lastRow</#if>">
                <#if statusId != "CTNT_PUBLISHED">
                    <#assign statusId = "CTNT_DEACTIVATED">  	
                </#if>
                <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : statusId}, false)/>  	
        		${statusItem.description!statusItem.get("description",locale)!statusItem.statusId}	
            </td>
            
            <td class="descCol <#if !hasNext>lastRow</#if>">
                ${thisContent.description?if_exists}
            </td>
                     
            <td class="dateCol <#if !hasNext>lastRow</#if>">
                <#if statusId == "CTNT_PUBLISHED" >
                    <#assign lastModifiedDate = "" />
              		<#if thisContent.lastModifiedDate?has_content>
                        ${thisContent.lastModifiedDate?string(preferredDateFormat)}
              		</#if>
                </#if>
            </td>
                    
            <td class="dateCol <#if !hasNext>lastRow</#if> lastCol">
                ${(thisContent.createdDate?string(preferredDateFormat))!""}
            </td>
                    
            <td class="actionCol <#if !hasNext>lastRow</#if> lastCol">
		        <div class="actionIconMenu">
		            <a class="toolIcon" href="javascript:void(o);"></a>
	                <div class="actionIconBox" style="display:none">
	                    <div class="actionIcon">
		                    <ul>
                                <#if editAction?exists && editAction?has_content>
                                    <li><a href="<@ofbizUrl>${editAction}?contentId=${thisContent.contentId?if_exists}&productId=${productId}&productContentTypeId=${content.productContentTypeId!}</@ofbizUrl>"><span class="detailIcon"></span>${uiLabelMap.EditContentTooltip}</a></li>
                                </#if>
                                <#if previewAction?exists && previewAction?has_content>
                                    <li><a href="<@ofbizUrl>${previewAction}?contentId=${thisContent.contentId?if_exists}</@ofbizUrl>" target="_new"><span class="previewIcon"></span>${uiLabelMap.PreviewContentTooltip}</a></li>
                                </#if>
			                </ul>
		                </div>
		            </div>
			    </div>
            </td>
        </tr>
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>