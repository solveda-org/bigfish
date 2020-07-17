<#-- Note, for now we are always going to say the owner is admin. At some point, we will check for owner Id -->
<#assign ownerId = context.userLoginId />
<!-- start contentList.ftl -->
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ContentIDLabel}</th>
            	<th class="nameCol">${uiLabelMap.NameLabel}</th>
                <th class="statusCol">${uiLabelMap.StatusLabel}</th>
                <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
                <#if spotListMenuId?exists && spotListMenuId == 'STATIC_PAGE_MENU'>
                  <th class="actionCol"></th>
                </#if>
                <th class="dateCol">${uiLabelMap.ActiveDateLabel}</th>
                <th class="dateCol">${uiLabelMap.CreatedDateLabel}</th>
                <th class="actionColSmall">
                  <#if previewHomeSpotAction?exists>
                    <input type="hidden" id="previewHomeSpotAction" value="<@ofbizUrl>${previewHomeSpotAction}</@ofbizUrl>" />
                    <a href="<@ofbizUrl>${previewHomeSpotAction}</@ofbizUrl>" id="previewHomeSpot" onMouseover="showTooltip(event,'${uiLabelMap.PreviewContentTooltip}');" onMouseout="hideTooltip()" class="previewIcon"></a>
                  </#if>
                </th>
            </tr>
            
    	<#assign now = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
        <#if resultList?has_content>
        	<#assign defaultStatusId = "CTNT_IN_PROGRESS" /> 
        	<#assign defaultStatus = delegator.findOne("StatusItem", {"statusId" : defaultStatusId}, false)>
        	<#assign defaultStatusLabel = defaultStatus.description!"" />
            
            <#assign rowClass = "1">          
            <#list resultList as thisContent>
            	<#-- ==== Get the various content labels -->		
            	<#assign hasNext = thisContent_has_next>
    	        <#assign statusId = thisContent.statusId!"CTNT_DEACTIVATED" /> 			
           
            	<tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
            	    <#-- Content Id-->
                    <td class="idCol firstCol" >
                        <a href="<@ofbizUrl>${detailPage}?contentId=${thisContent.contentId}</@ofbizUrl>">${thisContent.contentId!"N/A"}</a>
                     </td>
            		<#-- ==== Content Name ===== -->
                    <td class="nameCol <#if !hasNext>lastRow</#if>" >
						${thisContent.contentName!"N/A"}
 		             </td>
                    <#-- ==== Status ID === -->
                  
                    <td class="statusCol <#if !hasNext>lastRow</#if>">
                    	<#if statusId != "CTNT_PUBLISHED">
                            <#assign statusId = "CTNT_DEACTIVATED">  	
                    	</#if>
                     <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : statusId}, false)>  	
        			  ${statusItem.description!statusItem.get("description",locale)!statusItem.statusId}	
                    		                   		
                    </td>
                    <#-- ==== Description === -->
                    
                     <td class="descCol <#if !hasNext>lastRow</#if>">
                       ${thisContent.description?if_exists}
                     </td>
                     
                    <#if spotListMenuId?exists && spotListMenuId == 'STATIC_PAGE_MENU'>
                    <#assign eText = ""/>
                    <#assign content = delegator.findOne("Content", {"contentId", thisContent.contentId?if_exists}, false)/>
                    <#if content?exists>
                      <#assign dataResource = content.getRelatedOne("DataResource")>
                      <#if dataResource?exists>
                        <#assign electronicText = dataResource.getRelatedOne("ElectronicText")>
                        <#assign eText = electronicText.textData!/>
                        <#assign eText = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(eText, ADM_TOOLTIP_MAX_CHAR!, false)/>
                      </#if>
                    </#if>
                      <td class="actionCol <#if !hasNext>lastRow</#if> lastCol">
                        <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${eText?html}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
                      </td>
                    </#if> 
                    <#-- ==== Date Published ==== -->
                    <#-- === Only display this date if this Spot has been published ==== -->
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
                    <td class="actionColSmall <#if !hasNext>lastRow</#if> lastCol">
                      <#if spotListMenuId?exists && spotListMenuId == 'STATIC_PAGE_MENU'>
                        <a href="<@ofbizUrl>staticPageMetatag?contentId=${thisContent.contentId!}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.HtmlMetatagTooltip}');" onMouseout="hideTooltip()"><span class="metatagIcon"></span></a>
                      </#if>
                      <#if previewAction?exists && previewAction?has_content>
                        <a href="<@ofbizUrl>${previewAction}?contentId=${thisContent.contentId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.PreviewContentTooltip}');" onMouseout="hideTooltip()" class="previewIcon" target="_new"></a>
                      </#if>
                      <#if previewHomeSpotAction?exists>
                        <input type="checkbox" value="${thisContent.contentId?if_exists}" class="homeSpotCheck" name="contentId" />
                      </#if>
                    </td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        <#else>
           <tr>
               <td colspan="4" class="boxNumber">${uiLabelMap.NoDataAvailableInfo}</td>
            </tr>
        </#if>
<!-- end promotionsList.ftl -->