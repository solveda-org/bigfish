<#if plpProduct.isVirtual?if_exists?upper_case == "Y">
  <li class="${request.getAttribute("attributeClass")!}">
   <div class="plpSelectableFeature">
	<#assign inStock = true />
	<#assign isSellable = Static["org.ofbiz.product.product.ProductWorker"].isSellable(plpProduct?if_exists) />
	<#if !isSellable>
	 <#assign inStock=false/>
	</#if>
	      
	    <#-- If PLP_SELECT_MULTI_VARIANT does not equal QTY or CHECKBOX then display the appropriate display on PLP -->
	    <#if !plpProduct.virtualVariantMethodEnum?exists || plpProduct.virtualVariantMethodEnum == "VV_VARIANTTREE">
	      <#assign plpSwatchImageHeight = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_SWATCH_H")!""/>
	      <#assign plpSwatchImageWidth = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_SWATCH_W")!""/>
	      <#if plpVariantTree?exists && (plpVariantTree.size() > 0)>
	        <#assign plpFeatureOrderSize = plpFeatureOrder?size>
	        <#assign featureIdx=0/>
	        <#list plpFeatureSet as productFeatureTypeId>
	          <#assign featureIdx=featureIdx + 1/>
	          <div class="selectableFeatures ${productFeatureTypeId}" id='${uiSequenceScreen}_${plpProduct.productId}'>
	            <#assign productFeatureTypeLabel = ""/>
	            <#if plpProductFeatureTypesMap?has_content>
	              <#assign productFeatureTypeLabel = plpProductFeatureTypesMap.get(productFeatureTypeId)!"" />
	            </#if>
	            <label>${productFeatureTypeLabel!productFeatureTypeId.toLowerCase()?replace("_"," ")}:</label>
	            <select class="js_selectableFeature_${featureIdx} FT${productFeatureTypeId}_${uiSequenceScreen}_${plpProduct.productId}" name="FT${productFeatureTypeId}_${uiSequenceScreen}_${plpProduct.productId}" onchange="javascript:getListPlp(this.name,(this.selectedIndex-1), 1,'${uiSequenceScreen}_${plpProduct.productId}');">
	              <option></option>
	            </select>
	            <#assign productFeatureAndApplsSelects = plpProductFeatureAndApplSelectMap.get(productFeatureTypeId)!""/>
	            <#assign selectedIdx=0/>
	            <#assign alreadyShownProductFeatureId = Static["javolution.util.FastList"].newInstance()/>
	            <#assign productFeatureSize = productFeatureAndApplsSelects?size/>
	            <ul class="js_selectableFeature_${featureIdx}" id="LiFT${productFeatureTypeId}_${uiSequenceScreen}_${plpProduct.productId}" name="LiFT${productFeatureTypeId}_${uiSequenceScreen}_${plpProduct.productId}">
	              <#list productFeatureAndApplsSelects as productFeatureAndApplsSelect>
	                <#assign productFeatureDescription = productFeatureAndApplsSelect.description/>
                    <#assign featureClass=  Static["com.osafe.util.Util"].removeNonAlphaNumeric(productFeatureDescription)!""/> 
	                <#assign productFeatureSelectableId =productFeatureAndApplsSelect.productFeatureId/>
	                <#if PLP_FACET_GROUP_VARIANT_SWATCH?has_content && productFeatureTypeId.equalsIgnoreCase(PLP_FACET_GROUP_VARIANT_SWATCH)>
	                  <#assign productFeatureSelectVariantId= plpProductFeatureFirstVariantIdMap.get(productFeatureSelectableId)!""/>
	                  <#assign productFeatureId = productFeatureSelectableId />
	                  <#if productFeatureSelectVariantId?has_content>
	                    <#if !alreadyShownProductFeatureId.contains(productFeatureSelectVariantId)>
	                      <#assign variantProdCtntWrapper = plpProductVariantContentWrapperMap.get(productFeatureSelectVariantId)!""/>
	                      <#assign variantContentIdMap = plpProductVariantProductContentIdMap.get(productFeatureSelectVariantId)!""/>
	                      <#assign productVariantPlpSwatchURL=""/>                               
	                      <#if variantContentIdMap?has_content>
	                        <#assign variantContentId = variantContentIdMap.get("PLP_SWATCH_IMAGE_URL")!""/>
	                        <#if variantContentId?has_content>
	                          <#assign productVariantPlpSwatchURL = variantProdCtntWrapper.get("PLP_SWATCH_IMAGE_URL")!"">
	                        </#if>
	                      </#if>
	                      <#if (productVariantPlpSwatchURL?string?has_content)>
	                        <#assign productFeatureSwatchURL=productVariantPlpSwatchURL/>
	                      <#else>
	                        <#if plpProductFeatureDataResourceMap?has_content>
	                          <#assign productFeatureResourceUrl = plpProductFeatureDataResourceMap.get(productFeatureId)!""/>
	                          <#if productFeatureResourceUrl?has_content>
	                            <#assign productFeatureSwatchURL=productFeatureResourceUrl/>
	                          </#if>
	                        </#if>
	                      </#if>
	                      <#if plpFeatureOrderSize == 1>
	                        <#assign variantProductInventoryLevel = plpProductVariantInventoryMap.get(productFeatureSelectVariantId)!/>
							<#if variantProductInventoryLevel?has_content>
	                            <#assign inventoryLevel = variantProductInventoryLevel.get("inventoryLevel")/>
	                            <#assign inventoryInStockFrom = variantProductInventoryLevel.get("inventoryLevelInStockFrom")/>
	                            <#assign inventoryOutOfStockTo = variantProductInventoryLevel.get("inventoryLevelOutOfStockTo")/>
						    </#if>
	                        <#if inventoryLevel?has_content && inventoryOutOfStockTo?has_content && (inventoryLevel?number <= inventoryOutOfStockTo?number)>
	                          <#assign stockClass = "outOfStock"/>
	                        <#else>
	                          <#if inventoryLevel?has_content && inventoryInStockFrom?has_content && (inventoryLevel?number >= inventoryInStockFrom?number)>
	                            <#assign stockClass = "inStock"/>
	                          <#else>
	                            <#assign stockClass = "lowStock"/>
	                          </#if>
	                        </#if>
	                      </#if>
	                      <#assign productFeatureType = "${productFeatureTypeId!}:${productFeatureDescription!}"/>
	                      <#assign variantProductUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request, "eCommerceProductDetail?productId=${productId!}&productCategoryId=${productCategoryId!}&productFeatureType=${productFeatureTypeId!}:${productFeatureDescription!}") />
	                      <input type="hidden" id="${jqueryIdPrefix!}Url_${productFeatureDescription!}" value="${variantProductUrl!}"/>
	                      <#assign selectedClass="false"/>
	                      <#if parameters.productFeatureType?exists>
	                        <#assign productFeatureTypeIdParm = parameters.productFeatureType.split(":")/>
	                        <#if parameters.productFeatureType.equals(productFeatureType)>
	                          <#assign productFeatureIdx = selectedIdx/>
	                          <#assign selectedClass="true"/>
	                        </#if>
	                      </#if>
	                      <#if !parameters.productFeatureType?exists || productFeatureTypeId != productFeatureTypeIdParm[0]!"">
	                        <#if selectedIdx == 0>
	                          <#assign selectedClass="true"/>
	                        </#if>
	                      </#if>
	                      <li class="${featureClass!} <#if selectedClass == "true">selected</#if><#if stockClass?exists> ${stockClass}</#if>">
	                        <a href="javascript:void(0);" class="plpFeatureSwatchLink" onclick="javascript:getListPlp('FT${productFeatureTypeId}_${uiSequenceScreen}_${plpProduct.productId}','${selectedIdx}', 1,'${uiSequenceScreen}_${plpProduct.productId}');">
	                          <img src="<@ofbizContentUrl>${productFeatureSwatchURL!""}</@ofbizContentUrl>" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="FT${productFeatureTypeId}_${uiSequenceScreen}_${plpProduct.productId}" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
	                        </a>
	                      </li>
	                      <#assign changed = alreadyShownProductFeatureId.add(productFeatureSelectVariantId)/>
	                    </#if>
	                  </#if>
	                <#else>
	                  <li class="${featureClass!}">
	                    <a href="javascript:void(0);" onclick="javascript:getListPlp('FT${productFeatureTypeId}_${uiSequenceScreen}_${plpProduct.productId}','${selectedIdx}', 1,'${uiSequenceScreen}_${plpProduct.productId}');">
	                      ${productFeatureDescription!""}
	                    </a>
	                  </li>
	                </#if>
	                <#assign selectedIdx=selectedIdx + 1/>
	              </#list>
	            </ul>
	            <#--<select name="FT${productFeatureTypeId}" id="FT${productFeatureTypeId}">
	            <option></option>
	            </select> -->
	          </div>
	        </#list>
	        <input type="hidden" name="${uiSequenceScreen}_${plpProduct.productId}_product_id" value="${plpProduct.productId}"/>
	        <input type="hidden" name="${uiSequenceScreen}_${plpProduct.productId}_add_product_id" id="${uiSequenceScreen}_${plpProduct.productId}_add_product_id" value="NULL"/>
	        <div class="selectableFeatureAddProductId">
	          <span id="product_id_display"> </span>
	          <div id="variant_price_display"> </div>
	        </div>
	      <#else>
	        <input type="hidden" name="${uiSequenceScreen}_${plpProduct.productId}_product_id" value="${plpProduct.productId}"/>
	        <input type="hidden" name="${uiSequenceScreen}_${plpProduct.productId}_add_product_id" id="${uiSequenceScreen}_${plpProduct.productId}_add_product_id" value="NULL"/>
	        <#assign inStock = false>
	      </#if>
	  </#if>
   </div>
  </li>
</#if>