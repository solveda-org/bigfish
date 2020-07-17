<div class="productLoaderTab">
  <input type="button" class="standardBtn" name="processinOptionsButton" id="processinOptionsButton" value="${uiLabelMap.ProcessingOptionsBtn}" onClick="javascript:showXLSData('processingOptions','','${detailInfoBoxHeading!}: ${uiLabelMap.ProcessingOptionsHeading}');"/>

  <input type="button" class="standardBtn <#if (prodCatErrorList?has_content) || (prodCatErrorList?has_content && prodCatWarningList?has_content)>errorMark<#elseif prodCatWarningList?has_content>warningMark</#if>" name="productCategoryButton" id="productCategoryButton" value="${uiLabelMap.ProductCategoryBtn}" onClick="javascript:showXLSData('productCatData','productCatError','${detailInfoBoxHeading!}: ${uiLabelMap.ProductCategoryHeading}')"/>
   
  <input type="button" class="standardBtn <#if (productErrorList?has_content) || (productErrorList?has_content && productWarningList?has_content)>errorMark<#elseif productWarningList?has_content>warningMark</#if>" name="productButton" id="productButton" value="${uiLabelMap.ProductsBtn}" onClick="javascript:showXLSData('productData','productError','${detailInfoBoxHeading!}: ${uiLabelMap.ProductsHeading}');"/>
    
  <input type="button" class="standardBtn <#if (productAssocErrorList?has_content) || (productAssocErrorList?has_content && productAssocWarningList?has_content)>errorMark<#elseif productAssocWarningList?has_content>warningMark</#if>" name="productAssocButton" id="productAssocButton" value="${uiLabelMap.ProductAssociationsBtn}" onClick="javascript:showXLSData('productAssocData','productAssocError','${detailInfoBoxHeading!}: ${uiLabelMap.ProductAssociationsHeading}');"/>
  
  <input type="button" class="standardBtn <#if (productFeatureSwatchErrorList?has_content)>errorMark</#if>" name="featureSwatchesButton" id="featureSwatchesButton" value="${uiLabelMap.FeatureSwatchesButton}" onClick="javascript:showXLSData('featureSwatchesData','productFeatureSwatchError','${detailInfoBoxHeading!}: ${uiLabelMap.FeatureSwatchesHeading}');"/>
  
  <input type="button" class="standardBtn <#if (productManufacturerErrorList?has_content)>errorMark</#if>" name="manufacturerButton" id="manufacturerButton" value="${uiLabelMap.ManufacturerBtn}" onClick="javascript:showXLSData('manufacturerData','productManufacturerError','${detailInfoBoxHeading!}: ${uiLabelMap.ManufacturerHeading}');"/>
</div>