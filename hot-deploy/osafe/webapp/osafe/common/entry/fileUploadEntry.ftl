<div class="entryForm">
	<input type="hidden" id="fileUpload_productId" name="fileUpload_productId" value="${parameters.productId!productId!}"/>
					
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
		<a href="<@ofbizUrl>eCommerceProductDetail?productId=${parameters.productId!productId!}</@ofbizUrl>" class="standardBtn negative"/>
			<span>${uiLabelMap.CancelBtn}</span>
		</a>
	</div>
	
	<div class="standardBtn action continueButton uploadButton">
	     <input type="submit" value="${uiLabelMap.UploadImageBtn}" class="standardBtn action positive"/>
	</div>
</div>
