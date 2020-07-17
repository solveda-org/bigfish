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
    <#if mode == "add">
    	<input type="hidden" name="productCategoryId" value="${parameters.productCategoryId!""}" />
    	<input type="hidden" name="prodCatContentTypeId" value="${parameters.prodCatContentTypeId!""}" />
    </#if>
    <input type="hidden" name="contentTypeId" value="${contentTypeId!content.contentTypeId!""}" />
       <#assign statusId = statusId!"CTNT_PUBLISHED">
       <#assign statusDesc = statusDesc!"Active">
       <input type="hidden" name="statusId" id="statusId" value="${statusId!""}" />
       <#if !(createdDate?has_content)>
           <#assign createdDate = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
       </#if>
       <input type="hidden" name="createdDate" value="${createdDate}" />
		
		<#-- ==== Content ID === -->
		<#-- showBFContentId and showBFContentIdInput are for special case where BF content ID input is not displayed on Product Category Content detail screen. The system will generate a Content ID in this case -->
		<#if !showBFContentIdInput?exists || (showBFContentIdInput?has_content && showBFContentIdInput != "N")>
		  <div class="infoRow">
            <div class="infoEntry">
              <#if !showBFContentId?exists || (showBFContentId?has_content && showBFContentId != "N")>
                <div class="infoCaption"><label>${uiLabelMap.BigFishContentIdCaption}</label></div>
              <#else>
                <div class="infoCaption"><label>${uiLabelMap.ContentIdCaption}</label></div>
              </#if>
              <#-- ===== Content ID ==== -->
              <div class="infoValue">
              	<#if mode?has_content && mode == "add">
                  <input name="contentId" type="text" id="contentId" value="${parameters.contentId?default("")}"/>
                <#elseif mode == "edit">
                  <input type="hidden" name="contentId" value="${contentId!""}" />${bfContentId!""}
                </#if>
              </div>
            </div>
          </div>
        </#if>
          
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
      		     <textarea class="smallArea characterLimit" name="description" cols="50" rows="1" maxlength="${maxLengthDescription!255}">${parameters.description!description!""}</textarea>
      		     <span class="textCounter"></span>
      		     </div>
     		</div>
     	</div>
   		<#-- ====== Spot Content ==== -->
		<div class="infoRow">
	   		<div class="infoEntry long">
	      		<div class="infoCaption"><label>${uiLabelMap.ContentCaption}</label></div>
	     		    <div class="infoValue">
	        		     <textarea class="largeArea <#if maxLengthContent?has_content>characterLimit</#if>" name="textData" cols="50" rows="5" <#if maxLengthContent?has_content> maxlength="${maxLengthContent}"</#if>>${parameters.textData!eText!""}</textarea>
                         <#if maxLengthContent?has_content>
            		       <span class="textCounter"></span>
            		     </#if>
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
	${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
