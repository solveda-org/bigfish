<div id ="productData" class="commonDivHide" style="display:none">
<#assign maxSelectableFeatureFile = 0?number/>
<#assign maxDescriptiveFeatureFile = 0?number/>
<#assign maxPdpAdditionalThumbImageFile = 0?number/>
<#assign maxPdpAdditionalLargeImageFile = 0?number/>
<#assign maxPdpAdditionalDetailImageFile = 0?number/>

<#if productDataList?exists && productDataList?has_content>
<#list productDataList as product>
    <#assign totSelectableFeatures = 5?number/>
    <#if product.totSelectableFeatures?has_content>
        <#assign totSelectableFeatures = product.totSelectableFeatures?number/>
    </#if>
    <#assign maxSelectableFeatureRow = 0?number/>
    <#list 1..totSelectableFeatures as featureNo>
        <#assign selFeature = product.get("selectabeFeature_${featureNo}")!""/>
        <#if selFeature != "">
            <#assign maxSelectableFeatureRow = maxSelectableFeatureRow + 1/>
        <#else>
            <#break>
        </#if>
    </#list>
    <#if (maxSelectableFeatureRow > maxSelectableFeatureFile)>
        <#assign maxSelectableFeatureFile = maxSelectableFeatureRow/>
    </#if>
    
    <#assign totDescriptiveFeatures = 5?number/>
    <#if product.totDescriptiveFeatures?has_content>
        <#assign totDescriptiveFeatures = product.totDescriptiveFeatures?number/>
    </#if>
    <#assign maxDescriptiveFeatureRow = 0?number/>
    <#list 1..totDescriptiveFeatures as featureNo>
        <#assign descFeature = product.get("descriptiveFeature_${featureNo}")!""/>
        <#if descFeature != "">
            <#assign maxDescriptiveFeatureRow = maxDescriptiveFeatureRow + 1/>
        <#else>
            <#break>
        </#if>
    </#list>
    <#if (maxDescriptiveFeatureRow > maxDescriptiveFeatureFile)>
        <#assign maxDescriptiveFeatureFile = maxDescriptiveFeatureRow/>
    </#if>
    
    <#assign totPdpAdditionalThumbImage = 10?number/>
    <#if product.totPdpAdditionalThumbImage?has_content>
        <#assign totPdpAdditionalThumbImage = product.totPdpAdditionalThumbImage?number/>
    </#if>
    <#assign maxPdpAdditionalThumbImageRow = 0?number/>
    <#list 1..totPdpAdditionalThumbImage as thumbImageNo>
        <#assign thumbImage = product.get("addImage${thumbImageNo}")!""/>
        <#if thumbImage != "">
            <#assign maxPdpAdditionalThumbImageRow = maxPdpAdditionalThumbImageRow + 1/>
        <#else>
            <#break>
        </#if>
    </#list>
    <#if (maxPdpAdditionalThumbImageRow > maxPdpAdditionalThumbImageFile)>
        <#assign maxPdpAdditionalThumbImageFile = maxPdpAdditionalThumbImageRow/>
    </#if>
    
    <#assign totPdpAdditionalLargeImage = 10?number/>
    <#if product.totPdpAdditionalLargeImage?has_content>
        <#assign totPdpAdditionalLargeImage = product.totPdpAdditionalLargeImage?number/>
    </#if>
    <#assign maxPdpAdditionalLargeImageRow = 0?number/>
    <#list 1..totPdpAdditionalLargeImage as largeImageNo>
        <#assign largeImage = product.get("xtraLargeImage${largeImageNo}")!""/>
        <#if largeImage != "">
            <#assign maxPdpAdditionalLargeImageRow = maxPdpAdditionalLargeImageRow + 1/>
        <#else>
            <#break>
        </#if>
    </#list>
    <#if (maxPdpAdditionalLargeImageRow > maxPdpAdditionalLargeImageFile)>
        <#assign maxPdpAdditionalLargeImageFile = maxPdpAdditionalLargeImageRow/>
    </#if>
    
    <#assign totPdpAdditionalDetailImage = 10?number/>
    <#if product.totPdpAdditionalDetailImage?has_content>
        <#assign totPdpAdditionalDetailImage = product.totPdpAdditionalDetailImage?number/>
    </#if>
    <#assign maxPdpAdditionalDetailImageRow = 0?number/>
    <#list 1..totPdpAdditionalDetailImage as detailImageNo>
        <#assign detailImage = product.get("xtraDetailImage${detailImageNo}")!""/>
        <#if detailImage != "">
            <#assign maxPdpAdditionalDetailImageRow = maxPdpAdditionalDetailImageRow + 1/>
        <#else>
            <#break>
        </#if>
    </#list>
    <#if (maxPdpAdditionalDetailImageRow > maxPdpAdditionalDetailImageFile)>
        <#assign maxPdpAdditionalDetailImageFile = maxPdpAdditionalDetailImageRow/>
    </#if>
</#list>
</#if>
<#assign maxAltImages = Static["java.lang.Math"].max(maxPdpAdditionalThumbImageFile, Static["java.lang.Math"].max(maxPdpAdditionalLargeImageFile, maxPdpAdditionalDetailImageFile))/>

<table class="osafe">
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.RowNoLabel}</th>
    <th class="idCol">${uiLabelMap.MasterProductIdLabel}</th>
    <th class="idCol">${uiLabelMap.ProductIdVaraintLabel}</th>
    <th class="nameCol">${uiLabelMap.CategoryIdLabel}</th>
    <th class="descCol">${uiLabelMap.InternalNameLabel}</th>
    <th class="descCol">${uiLabelMap.ProductNameLabel}</th>
    <th class="nameCol">${uiLabelMap.SalesPitchLabel}</th>
    <th class="descCol">${uiLabelMap.LongDescLabel}</th>
    <th class="nameCol">${uiLabelMap.SpecialInstrLabel}</th>
    <th class="nameCol">${uiLabelMap.DeliveryInfoLabel}</th>
    <th class="nameCol">${uiLabelMap.DirectionsLabel}</th>
    <th class="nameCol">${uiLabelMap.TermsAndCondLabel}</th>
    <th class="nameCol">${uiLabelMap.IngredientsLabel}</th>
    <th class="nameCol">${uiLabelMap.WarningsLabel}</th>
    <th class="nameCol">${uiLabelMap.PLPLabelLabel}</th>
    <th class="nameCol">${uiLabelMap.PDPLabelLabel}</th>
    <th class="nameCol">${uiLabelMap.ListPriceLabel}</th>
    <th class="nameCol">${uiLabelMap.SalesPriceLabel}</th>
    <#if (maxSelectableFeatureFile > 0)>
        <#list 1..maxSelectableFeatureFile as selFeatureNo>
            <th class="nameCol">
                ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "SelectableFeaturesNoLabel", Static["org.ofbiz.base.util.UtilMisc"].toMap("selFeatureNo", selFeatureNo), locale)!} 
            </th>
        </#list>
    </#if>
    <#if (maxDescriptiveFeatureFile > 0)>
        <#list 1..maxDescriptiveFeatureFile as descFeatureNo>
            <th class="nameCol">
                ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "DescriptiveFeaturesNoLabel", Static["org.ofbiz.base.util.UtilMisc"].toMap("descFeatureNo", descFeatureNo), locale)!} 
            </th>
        </#list>
    </#if>
    <th class="nameCol">${uiLabelMap.PLPSwatchImageLabel}</th>
    <th class="nameCol">${uiLabelMap.PDPSwatchImageLabel}</th>
    <th class="nameCol">${uiLabelMap.PLPImageLabel}</th>
    <th class="nameCol">${uiLabelMap.PLPAltImageLabel}</th>
    <th class="nameCol">${uiLabelMap.PDPThumbnailImageLabel}</th>
    <th class="nameCol">${uiLabelMap.PDPRegularImageLabel}</th>
    <th class="nameCol">${uiLabelMap.PDPLargeImageLabel}</th>
    
    <#if (maxAltImages > 0)>
        <#list 1..maxAltImages as imageNo>
            <th class="nameCol">
                ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "PDPAltThumbnailImageLabel", Static["org.ofbiz.base.util.UtilMisc"].toMap("thumbImageNo", imageNo), locale)!} 
            </th>
            <th class="nameCol">
                ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "PDPAltRegularImageLabel", Static["org.ofbiz.base.util.UtilMisc"].toMap("largeImageNo", imageNo), locale)!} 
            </th>
            <th class="nameCol">
                ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "PDPAltLargeImageLabel", Static["org.ofbiz.base.util.UtilMisc"].toMap("detailImageNo", imageNo), locale)!} 
            </th>
        </#list>
    </#if>
       
    <th class="nameCol">${uiLabelMap.ProductHeightLabel}</th>
    <th class="nameCol">${uiLabelMap.ProductWidthLabel}</th>
    <th class="nameCol">${uiLabelMap.ProductDepthLabel}</th>
    <th class="nameCol">${uiLabelMap.ProductWeightLabel}</th>
    <th class="nameCol">${uiLabelMap.ReturnableLabel}</th>
    <th class="nameCol">${uiLabelMap.TaxableLabel}</th>
    <th class="nameCol">${uiLabelMap.ChargeShippingLabel}</th>
    <th class="nameCol">${uiLabelMap.IntroDateLabel}</th>
    <th class="nameCol">${uiLabelMap.DiscoDateLabel}</th>
    <th class="nameCol">${uiLabelMap.ManufacturerLabel}</th>
    <th class="nameCol">${uiLabelMap.SKUNoLabel}</th>
    <th class="nameCol">${uiLabelMap.GoogleIDLabel}</th>
    <th class="nameCol">${uiLabelMap.ISBNLabel}</th>
    <th class="nameCol">${uiLabelMap.ManufacturerIDLabel}</th>
    <th class="nameCol">${uiLabelMap.ProductVideoLabel}</th>
    <th class="nameCol">${uiLabelMap.Product360VideoLabel}</th>
    <th class="nameCol">${uiLabelMap.SequenceNumberLabel}</th>
    <th class="nameCol">${uiLabelMap.BFInventoryTotalLabel}</th>
    <th class="nameCol">${uiLabelMap.BFInventoryWarehouseLabel}</th>
    <th class="descCol">${uiLabelMap.MultiVariantLabel}</th>
    <th class="descCol">${uiLabelMap.CheckOutGiftMessageLabel}</th>
    <th class="descCol">${uiLabelMap.PdpQtyMinLabel}</th>
    <th class="descCol">${uiLabelMap.PdpQtyMaxLabel}</th>
    <th class="descCol">${uiLabelMap.PdpQtyDefaultLabel}</th>
    <th class="descCol">${uiLabelMap.PdpInStoreOnlyLabel}</th>
    <th class="descCol">${uiLabelMap.ProductAttachment1Label}</th>
    <th class="descCol">${uiLabelMap.ProductAttachment2Label}</th>
    <th class="descCol">${uiLabelMap.ProductAttachment3Label}</th>
  </tr>
  <#if productDataList?exists && productDataList?has_content>
    <#assign rowClass = "1">
    <#assign rowNo = 1>
    <#list productDataList as product>
      <tr class="<#if rowClass == "2">even</#if>">
        <td class="idCol firstCol" >${rowNo!""}</td>
        <td class="idCol" >${product.masterProductId!""}</td>
        <td class="idCol" >${product.productId!""}</td>
        <td class="nameCol">${product.productCategoryId!""}</td>        
        <td class="descCol">${product.internalName!""}</td>
        <td class="descCol">${product.productName!""}</td>
        <td class="nameCol">${product.salesPitch!""}</td>
        <td class="descCol">
          <#assign longDescription = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${product.longDescription!""}')/>
          <#if longDescription !="">
            <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${longDescription!}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
          </#if>
        </td>
        <td class="nameCol">${product.specialInstructions!""}</td>
        <td class="nameCol">${product.deliveryInfo!""}</td>
        <td class="nameCol">${product.directions!""}</td>
        <td class="nameCol">${product.termsConditions!""}</td>
        <td class="nameCol">${product.ingredients!""}</td>
        <td class="nameCol">${product.warnings!""}</td>
        <td class="nameCol">${product.plpLabel!""}</td>
        <td class="nameCol">${product.pdpLabel!""}</td>
        <td class="nameCol">${product.listPrice!""}</td>
        <td class="nameCol">${product.defaultPrice!""}</td>
        <#if (maxSelectableFeatureFile > 0)>
            <#list 1..maxSelectableFeatureFile as selFeatureNo>
                <td class="nameCol">
                    ${product.get("selectabeFeature_"+selFeatureNo)!}
                </td>
            </#list>
        </#if>
        
        <#if (maxDescriptiveFeatureFile > 0)>
            <#list 1..maxDescriptiveFeatureFile as descFeatureNo>
                <td class="nameCol">
                    ${product.get("descriptiveFeature_"+descFeatureNo)!}
                </td>
            </#list>
        </#if>
        <td class="nameCol">${product.plpSwatchImage!""}</td>
        <td class="nameCol">${product.pdpSwatchImage!""}</td>
        <td class="nameCol">${product.smallImage!""}</td>
        <td class="nameCol">${product.smallImageAlt!""}</td>
        <td class="nameCol">${product.thumbImage!""}</td>
        <td class="nameCol">${product.largeImage!""}</td>
        <td class="nameCol">${product.detailImage!""}</td>
        
        <#if (maxAltImages > 0)>
	        <#list 1..maxAltImages as imageNo>
	            <td class="nameCol">
	              <#if product.get("addImage"+imageNo)?has_content>
                    ${product.get("addImage"+imageNo)}
                  </#if>
                </td>
	            <td class="nameCol">
	              <#if product.get("xtraLargeImage"+imageNo)?has_content>
                    ${product.get("xtraLargeImage"+imageNo)} 
                  </#if>
                </td>
	            <td class="nameCol">
	              <#if product.get("xtraDetailImage"+imageNo)?has_content>
	                ${product.get("xtraDetailImage"+imageNo)} 
	              </#if>
	            </td>
	        </#list>
        </#if>
        
        <td class="nameCol">${product.productHeight!""}</td>
        <td class="nameCol">${product.productWidth!""}</td>
        <td class="nameCol">${product.productDepth!""}</td>
        <td class="nameCol">${product.weight!""}</td>
        <td class="nameCol">${product.returnable!""}</td>
        <td class="nameCol">${product.taxable!""}</td>
        <td class="nameCol">${product.chargeShipping!""}</td>
        <td class="nameCol">${product.introDate!""}</td>
        <td class="nameCol">${product.discoDate!""}</td>
        <td class="nameCol">${product.manufacturerId!""}</td>
        <td class="nameCol">${product.goodIdentificationSkuId!""}</td>
        <td class="nameCol">${product.goodIdentificationGoogleId!""}</td>
        <td class="nameCol">${product.goodIdentificationIsbnId!""}</td>
        <td class="nameCol">${product.goodIdentificationManufacturerId!""}</td>
        <td class="nameCol">${product.pdpVideoUrl!""}</td>
        <td class="nameCol">${product.pdpVideo360Url!""}</td>
        <td class="nameCol">${product.sequenceNum!""}</td>
        <td class="nameCol">${product.bfInventoryTot!""}</td>
        <td class="nameCol">${product.bfInventoryWhs!""}</td>
        <td class="nameCol">${product.multiVariant!""}</td>
        <td class="nameCol">${product.giftMessage!""}</td>
        <td class="nameCol">${product.pdpQtyMin!""}</td>
        <td class="nameCol">${product.pdpQtyMax!""}</td>
        <td class="nameCol">${product.pdpQtyDefault!""}</td>
        <td class="nameCol">${product.pdpInStoreOnly!""}</td>
        <td class="nameCol">${product.productAttachment1!""}</td>
        <td class="nameCol">${product.productAttachment2!""}</td>
        <td class="nameCol">${product.productAttachment3!""}</td>
      </tr>
      <#-- toggle the row color -->
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
      <#assign rowNo = rowNo+1/>
    </#list>
  <#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>
</table>
</div>