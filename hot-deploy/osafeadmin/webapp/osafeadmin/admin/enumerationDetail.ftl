<#if mode?has_content>
    <#if enum?has_content>
        <#assign enumId = enum.enumId?if_exists />
        <#assign description = enum.description!"" />
        <#assign sequenceId = enum.sequenceId!"" />
        <#assign createdDate = enum.createdStamp!"" />
        <#assign lastUpdatedDate = enum.lastUpdatedStamp!"" />
     <#else>
        <#assign createdDate = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
        <#assign lastUpdatedDate = "" />
    </#if> 
      <input type="hidden" name="enumTypeId" value="${enumTypeId!""}" />
       <input type="hidden" name="createdDate" value="${createdDate}" />
		<div class="infoRow">
         <div class="infoEntry">
             <div class="infoCaption"><label>${enumTypeDisplay!""} ${uiLabelMap.IdCaption}</label></div>
               <div class="infoValue">
                   <#if mode?has_content && mode == "add">
                       <input name="enumId" type="text" id="enumId" value="${parameters.enumId?default("")}" class="medium"/>
                   <#elseif mode?has_content && mode == "edit">
                       <input type="hidden" name="enumId" value="${enumId!""}"/>${enumId!""}
                   </#if>
               </div>
         </div>
       </div>
  		<div class="infoRow">
   			<div class="infoEntry">
    			<div class="infoCaption"><label>${enumTypeDisplay!""} ${uiLabelMap.DescCaption}</label></div>
      		    <div class="infoValue">
      		     <input type="text" name="description" value="${parameters.description!description!""}" class="medium"/>
      		    </div>
     		</div>
     	</div>
  		<div class="infoRow">
   			<div class="infoEntry">
    			<div class="infoCaption"><label>${uiLabelMap.SeqNumberCaption}</label></div>
      		    <div class="infoValue">
      		     <input type="text" name="sequenceId" value="${parameters.sequenceId!sequenceId!""}" class="small"/>
      		    </div>
     		</div>
     	</div>
		<div class="infoRow">
	   		<div class="infoEntry">
	      		<div class="infoCaption"><label>${uiLabelMap.CreatedDateCaption}</label></div>
	     		<div class="infoValue">
          				<#if createdDate?has_content>
           	     		  ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(createdDate, preferredDateTimeFormat).toLowerCase())!"N/A"}</div>
          				</#if>
	   		</div>
	    </div>
		<div class="infoRow">
	   		<div class="infoEntry">
	      		<div class="infoCaption"><label>${uiLabelMap.UpdatedDateCaption}</label></div>
	     		<div class="infoValue">
          				<#if lastUpdatedDate?has_content>
          					${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(lastUpdatedDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
          				</#if>
	     		</div>
	   		</div>
	    </div>
<#else>
	${uiLabelMap.NoDataAvailableInfo}
</#if>
