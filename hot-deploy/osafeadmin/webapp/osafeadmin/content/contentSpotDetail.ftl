<#if mode?has_content>
    <#if content?has_content>
        <#assign contentId = content.contentId?if_exists />
        <#assign contentName = content.contentName!"" />
        <#assign description = content.description!"" />
        <#assign statusId = content.statusId!"CTNT_DEACTIVATED" />
        <#if statusId != "CTNT_PUBLISHED">
            <#assign statusId = "CTNT_DEACTIVATED">  	
        </#if>
   		<input type="hidden" name="dataResourceId" value=${content.dataResourceId!""} />
        <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : statusId}, false)>
        <#assign statusDesc = statusItem.description!statusItem.get("description",locale)!statusItem.statusId>
        <#assign createdDate = content.createdDate!"" />
        <#assign lastModifiedDate = content.lastModifiedDate!"" />
    </#if> 
    <input type="hidden" name="spotListMenuId" value="${spotListMenuId!""}" />
       <#assign statusId = statusId!"CTNT_PUBLISHED">
       <#assign statusDesc = statusDesc!"Active">
       <input type="hidden" name="statusId" id="statusId" value="${statusId!""}" />
       <#if !(createdDate?has_content)>
           <#assign createdDate = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
       </#if>
       <input type="hidden" name="createdDate" value="${createdDate}" />
		<div class="infoRow">
		<#-- ==== Content ID === -->
         <div class="infoEntry">
             <div class="infoCaption"><label>${uiLabelMap.ContentIdCaption}</label></div>
                   <#-- ===== Content ID ==== -->
               <div class="infoValue">
                   <#if mode?has_content && mode == "add">
                       <input name="contentId" type="text" id="contentId" value="${parameters.contentId?default("")}"/>
                   <#elseif mode?has_content && mode == "edit">
                       <input type="hidden" name="contentId" value="${contentId!""}" />${contentId!""}
                   </#if>
               </div>
         </div>
		<#-- ==== Spot Name === -->
		  <div class="infoRow">
            <div class="infoEntry">
              <div class="infoCaption"><label>${uiLabelMap.NameCaption}</label></div>
                <#-- ===== Spot Name ==== -->
                <div class="infoValue">
                   <input name="contentName" type="text" id="contentName" value="${parameters.contentName?default(contentName!"")}" />
                </div>
            </div>
          </div>
  		<#-- ==== Spot Description === -->
  		<div class="infoRow">
   			<div class="infoEntry long">
    			<div class="infoCaption"><label>${uiLabelMap.DescriptionCaption}</label></div>
     			<#-- ===== Spot Description ==== -->
      		    <div class="infoValue">
      		     <textarea class="smallArea characterLimit" name="description" cols="50" rows="1" maxlength="255">${parameters.description!description!""}</textarea>
      		     <span class="textCounter"></span>
      		     </div>
     		</div>
     	</div>
   		<#-- ====== Spot Content ==== -->
		<div class="infoRow">
	   		<div class="infoEntry long">
	      		<div class="infoCaption"><label>${uiLabelMap.ContentCaption}</label></div>
	     		    <div class="infoValue">
	        		     <textarea class="largeArea" name="textData" cols="50" rows="5">${parameters.textData!eText!""}</textarea>
	     		    </div>
	 		</div>
	    </div>
	    <#-- ====== Created Date ==== -->
		<div class="infoRow">
	   		<div class="infoEntry">
	      		<div class="infoCaption"><label>${uiLabelMap.CreatedDateCaption}</label></div>
	     		<div class="infoValue">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(createdDate, preferredDateTimeFormat).toLowerCase())!"N/A"}</div>
	   		</div>
	    </div>
 		<#-- ===== Status Buttons ====== -->
		<div class="infoRow">
	 		<div class="infoEntry long">
	    		<div class="infoCaption">
	      			<label>${uiLabelMap.StatusCaption}</label>
	     		</div>
	     		<div class="infoValue statusItem">
	       			<span id="contentStatus">${statusDesc!""}</span>
	     		</div>
			    <div class="statusButtons">
			        <#if statusId != "CTNT_PUBLISHED">
			            <input id="contentStatusBtn" type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.ContentMakeActive}" idValue="CTNT_PUBLISHED" onClick="updateStatusBtn('${uiLabelMap.ContentMakeActive}', '${uiLabelMap.ContentSetInactive}', document.${detailFormName!""}, 'contentStatus', 'contentStatusBtn');"/>
			        <#else>
			            <input id="contentStatusBtn" type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.ContentSetInactive}" onClick="updateStatusBtn('${uiLabelMap.ContentMakeActive}', '${uiLabelMap.ContentSetInactive}', document.${detailFormName!""}, 'contentStatus', 'contentStatusBtn');"/>
			        </#if>
			   </div>
	        </div>
	    </div>

   		<#-- ====== Active Date ==== -->
		<div class="infoRow">
	   		<div class="infoEntry">
	      		<div class="infoCaption">
	      			<label>${uiLabelMap.ActiveDateCaption}</label>
	       		</div>
	     		<div class="infoValue">
                	<#if statusId == "CTNT_PUBLISHED" >
          				<#if lastModifiedDate?has_content>
          					${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(lastModifiedDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
          				</#if>
                	</#if>
                   
	     		</div>
	   		</div>
	    </div>
<#else>
	${uiLabelMap.NoDataAvailableInfo}
</#if>
