<div id ="processingOptions" class="commonDivHide">
  <input type="hidden" name="xlsFileName" value="${xlsFileName!parameters.xlsFileName!}"/>
  <input type="hidden" name="xlsFilePath" value="${xlsFilePath!parameters.xlsFilePath!}"/>
  <input type="hidden" name="productLoadImagesDir" value="${productLoadImagesDir!parameters.productLoadImagesDir!}"/>
  <input type="hidden" name="imageUrl" value="${imageUrl!parameters.imageUrl!}"/>
  <div class="heading">${uiLabelMap.ProcessingOptionsHeading}</div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ProductXLSFileCaption}</label>
      </div>
      <div class="infoValue">
        ${xlsFileName!}
      </div>
    </div>
  </div>
  
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ProcessingOptionCaption}</label>
      </div>
      <div class="entry checkbox large infoValue">
        <input class="checkBoxEntry" type="radio" id="reload" name="processingOpt"  value="reload" <#if parameters.processingOpt?exists && parameters.processingOpt == 'reload'>checked</#if>/>${uiLabelMap.RELOADInfoLabel}<br/>
        <input class="checkBoxEntry" type="radio" id="update" name="processingOpt" value="update" <#if parameters.processingOpt?exists && parameters.processingOpt == 'update'>checked</#if>/>${uiLabelMap.UPDATEInfoLabel}
      </div>
    </div>
  </div>
</div>