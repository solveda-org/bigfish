<li class="${request.getAttribute("attributeClass")!}">
	<div class="entryForm">
	  	<#if productAtrributeMap?has_content>
			<#assign fileUpload = productAtrributeMap.PT_FILE_UPLOAD!/>
			<#if fileUpload?has_content && fileUpload == "TRUE">
				<#assign personalizationDefaultMap = Static["org.ofbiz.base.util.UtilProperties"].getResourceBundleMap("parameters_personalization", locale)>
				<#assign fileUploadMap = sessionAttributes.fileUploadMap!/>
				<#if fileUploadMap?has_content>
					<#assign fileNameFromMap = fileUploadMap.get("fileName")!/>
					<#assign imageUploadUrlFromMap = fileUploadMap.get("imageUploadUrl")!/>
				</#if>
				<input type="hidden" id="fileUploadMapId" name="fileUploadMapId" value="${request.getAttribute("fileUploadMapId")!}"/>
				<div id="fileUploadEntry">
					<p class="instructions"><span>${uiLabelMap.UploadFileInstructions}</span></p>
					<div class="entry uploadFileNamePersonalized">
				        <label>${uiLabelMap.FileCaption}</label>
				        <div class="entryField">
				        	<#assign fileName = fileNameFromMap!parameters.uploadFileName!"" />
			                <input type="text" class="uploadFileName" id="uploadFileName" name="uploadFileName" value="${fileName!""}" readonly/>
			            	<#-- <a class="standardBtn action uploadButton" href="javaScript:void(0);" onClick="prepareActionDialog('uploadFile_'); displayActionDialogBox('uploadFile_',this);" > -->
			            	<a class="standardBtn action uploadButton" href="<@ofbizUrl>eCommerceUploadFile?productId=${parameters.productId!productId!}</@ofbizUrl>">
					        	<span><#if fileName?has_content>${uiLabelMap.ReplaceImageBtn}<#else>${uiLabelMap.UploadImageBtn}</#if></span>
					        </a>
				        </div>
					</div>
					
					<#if imageUploadUrlFromMap?exists && imageUploadUrlFromMap?has_content>
						<#if personalizationDefaultMap?has_content>
				  			<#assign uploadImageType = personalizationDefaultMap.PT_UPLOAD_IMAGE_TYPE!""/>
				  			<#if productAtrributeMap.PT_UPLOAD_IMAGE_TYPE?has_content>
								<#assign uploadImageType = productAtrributeMap.PT_UPLOAD_IMAGE_TYPE!""/>
							</#if>
				  			<#assign uploadImageH = personalizationDefaultMap.PT_UPLOAD_IMAGE_H!""/>
				  			<#if productAtrributeMap.PT_UPLOAD_IMAGE_H?has_content>
								<#assign uploadImageH = productAtrributeMap.PT_UPLOAD_IMAGE_H!""/>
							</#if>
				  			<#assign uploadImageW = personalizationDefaultMap.PT_UPLOAD_IMAGE_W!""/>
				  			<#if productAtrributeMap.PT_UPLOAD_IMAGE_W?has_content>
								<#assign uploadImageW = productAtrributeMap.PT_UPLOAD_IMAGE_W!""/>
							</#if>
				  		</#if>
					</#if>
					
					<div class="uploadedFile">
			  			<#if uploadImageType?exists && uploadImageType?has_content && uploadImageType == "IMAGE">
			  				<img src="<@ofbizContentUrl>${imageUploadUrlFromMap!}</@ofbizContentUrl>" class="uploadedFileImage" <#if uploadImageH?has_content> height="${uploadImageH!""}"</#if> <#if uploadImageW?has_content> width="${uploadImageW!""}"</#if>/>
			  			</#if>
					</div>
					
					<div class="uploadInfo">
						<#if imageUploadUrlFromMap?exists && imageUploadUrlFromMap?has_content>
							<p class="instructions"><span>${uiLabelMap.UploadFileInformation}</span></p>
						</#if>
					</div>
				</div>
			</#if>
		</#if>
	</div>
</li>
