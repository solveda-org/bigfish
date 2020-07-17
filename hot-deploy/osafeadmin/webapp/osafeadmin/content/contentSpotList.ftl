<#-- Note, for now we are always going to say the owner is admin. At some point, we will check for owner Id -->
<#assign ownerId = context.userLoginId />
<!-- start contentList.ftl -->
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.BigFishContentIdLabel}</th>
                <#if contentTypeId?exists && libraryContentTypeId?exists && contentTypeId == libraryContentTypeId>
                  <th class="actionCol"></th>
                </#if>
            	<th class="nameCol">${uiLabelMap.NameLabel}</th>
                <th class="statusCol">${uiLabelMap.StatusLabel}</th>
                <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
                <#if contentTypeId?exists && staticPageContentTypeId?exists && contentTypeId == staticPageContentTypeId>
                  <th class="actionCol"></th>
                </#if>
                <th class="dateCol">${uiLabelMap.ActiveDateLabel}</th>
                <th class="dateCol">${uiLabelMap.CreatedDateLabel}</th>
                <th class="actionColSmall">
                  <#if previewHomeSpotAction?exists>
                    <input type="hidden" id="previewHomeSpotAction" value="<@ofbizUrl>${previewHomeSpotAction}</@ofbizUrl>" />
                    <a href="<@ofbizUrl>${previewHomeSpotAction}</@ofbizUrl>" id="previewHomeSpot" onMouseover="showTooltip(event,'${uiLabelMap.PreviewContentTooltip}');" onMouseout="hideTooltip()"><span class="previewIcon"></span></a>
                  </#if>
                </th>
            </tr>
            
    	<#assign now = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
        <#if resultList?has_content>
        	<#assign defaultStatusId = "CTNT_IN_PROGRESS" /> 
        	<#assign defaultStatus = delegator.findOne("StatusItem", {"statusId" : defaultStatusId}, false)>
        	<#assign defaultStatusLabel = defaultStatus.description!"" />
            
            <#assign rowClass = "1">          
            <#list resultList as content>
            	<#-- ==== Get the various content labels -->		
                <#assign thisContent  = delegator.findByPrimaryKey("Content",Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId",content.contentId))/>
                <#assign hasNext = content_has_next>
    	        <#assign statusId = thisContent.statusId!"CTNT_DEACTIVATED" /> 			
           
            	<tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
            	    <#-- Content Id-->
                    <td class="idCol firstCol" >
                        <a href="<@ofbizUrl>${detailPage}?contentId=${content.bfContentId}</@ofbizUrl>">${content.bfContentId!"N/A"}</a>
                     </td>
                     <#if contentTypeId?exists && libraryContentTypeId?exists && contentTypeId == libraryContentTypeId>
                     <td class="actionCol <#if !hasNext>lastRow</#if> lastCol">
                         <div class="infoIcon">
                           <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SyntaxHelperInfo}${thisContent.contentId}${uiLabelMap.EndTag}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                         </div>
                     </td>
                     </#if>
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
                     
                    <#if contentTypeId?exists && staticPageContentTypeId?exists && contentTypeId == staticPageContentTypeId>
                    <#assign eText = ""/>
                    <#if thisContent?exists>
                      <#assign dataResource = thisContent.getRelatedOne("DataResource")>
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
                    <td class="actionCol <#if !hasNext>lastRow</#if> lastCol">
                     <#if (contentTypeId?exists && staticPageContentTypeId?exists) || (previewAction?exists && previewAction?has_content)>
			          <div class="actionIconMenu">
			            <a class="toolIcon" href="javascript:void(o);"></a>
			            <div class="actionIconBox" style="display:none">
			            <div class="actionIcon">
		              <#if productLargeImageUrl?has_content>
		                  <img class="actionIconMenuImage" src="<@ofbizContentUrl>${productLargeImageUrl}</@ofbizContentUrl>" alt="${productLargeImageUrl}"/>
		              </#if>            
				            <ul>
		                      <#if contentTypeId?exists && staticPageContentTypeId?exists && contentTypeId == staticPageContentTypeId>
		                        <li><a href="<@ofbizUrl>staticPageMetatag?contentId=${thisContent.contentId!}</@ofbizUrl>"><span class="metatagIcon"></span>${uiLabelMap.HtmlMetatagTooltip}</a></li>
		                      </#if>
		                      <#if editAction?exists && editAction?has_content>
		                        <li><a href="<@ofbizUrl>${editAction}?contentId=${thisContent.contentId?if_exists}</@ofbizUrl>"><span class="detailIcon"></span>${uiLabelMap.EditContentTooltip}</a></li>
		                      </#if>
		                      <#if previewAction?exists && previewAction?has_content>
		                        <li><a href="<@ofbizUrl>${previewAction}?contentId=${thisContent.contentId?if_exists}</@ofbizUrl>" target="_new"><span class="previewIcon"></span>${uiLabelMap.PreviewContentTooltip}</a></li>
		                      </#if>
					        </ul>
			       
				        </div>
				        </div>
				      </div>
                     </#if>
                      <#if previewHomeSpotAction?exists>
                        <input type="checkbox" value="${thisContent.contentId?if_exists}" class="homeSpotCheck" name="contentId" />
                      </#if>
                      <#if contentTypeId?exists && adminToolDetailScreen?exists>
                          <a href="<@ofbizUrl>adminToolDetail?detailScreen=${adminToolDetailScreen}&simpleTest=N&templateId=${content.bfContentId}</@ofbizUrl>" onMouseover="showTooltip(event,'${adminToolDetailToolTip}');" onMouseout="hideTooltip()"><span class="emailIcon"></span></a>
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
           ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
        </#if>
<!-- end promotionsList.ftl -->