<form method="post" action="<@ofbizUrl>${uploadFormAction!'uploadFileFromPdp'}</@ofbizUrl>" name="${uploadFormName!'fileUploadForm'}"  style="margin: 0;" enctype="multipart/form-data">
	<div class="entryForm">
	  	<#if productAtrributeMap?has_content>
			<#assign fileUpload = productAtrributeMap.FILE_UPLOAD!/>
			<#if fileUpload?has_content && fileUpload == "TRUE">
			
				<input type="hidden" id="fileUpload_productId" name="fileUpload_productId" value="${productId?if_exists}"/>
								
				<div id="fileUploadEntry">
					<p class="instructions"><span>${uiLabelMap.UploadFileInstructions}</span></p>
					<div class="entry uploadFileNamePersonalized">
				        <label>${uiLabelMap.FileCaption}</label>
				        <div class="entryField">
			            	<input type="file" id="fileUpload" name="fileUpload" size="40" value=""/>
				        </div>
					</div>
				</div>
				
				<div class="standardBtn action cancelButton">
					<a href="javascript:void(0)" onClick="javascript:jQuery('.uploadFile_displayDialog').find('.ui-icon-closethick').click();" class="standardBtn negative"><span>${uiLabelMap.CancelBtn}</span></a>
				</div>
				
				<div class="standardBtn action uploadButton">
				     <input type="submit" value="${uiLabelMap.UploadImageBtn}" class="standardBtn action"/>
				</div>
	
			</#if>
		</#if>
	</div>
</form>
