<div id="pdpSelectableFeature">
<fieldset>
<#assign inStock = true />
<#assign isSellable = Static["org.ofbiz.product.product.ProductWorker"].isSellable(currentProduct?if_exists) />
<#if !isSellable>
 <#assign inStock=false/>
</#if>

<#if currentProduct.isVirtual?if_exists?upper_case == "Y">
  <#if !currentProduct.virtualVariantMethodEnum?exists || currentProduct.virtualVariantMethodEnum == "VV_VARIANTTREE">
   <#if variantTree?exists && (variantTree.size() > 0)>
   <#assign featureOrderSize = featureOrder?size>
    <#assign featureIdx=0/>
    <#list featureSet as productFeatureTypeId>
    <#assign featureIdx=featureIdx + 1/>
      <div class="selectableFeatures ${productFeatureTypeId}">
        <#assign productFeatureType = delegator.findOne("ProductFeatureType", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureTypeId", productFeatureTypeId), true) />
        <#assign productFeatureTypeLabel = ""/>
        <#if productFeatureType?has_content>
         <#assign productFeatureTypeLabel = productFeatureType.description!""/>
        </#if>
            <label>${productFeatureTypeLabel!productFeatureTypeId.toLowerCase()?replace("_"," ")}:</label>
            <#--
            <select class="selectableFeature_${featureIdx}" name="FT${productFeatureTypeId}" onchange="javascript:getList(this.name,(this.selectedIndex-1), 1,'${productLargeImageUrl}','${productDetailImageUrl}');">
              <option></option>
            </select>
            -->
            <#assign productFeatureAndApplsSelects = productFeatureAndApplSelectMap.get('${productFeatureTypeId}')/>
            <#assign selectedIdx=0/>
            <#assign alreadyShownProductFeatureId = Static["javolution.util.FastList"].newInstance()/>
            <#assign productFeatureSize = productFeatureAndApplsSelects?size/>
            <ul class="selectableFeature_${featureIdx}" id="LiFT${productFeatureTypeId}" name="LiFT${productFeatureTypeId}">
            <#list productFeatureAndApplsSelects as productFeatureAndApplsSelect>
                 <#assign productFeatureDescription =productFeatureAndApplsSelect.description/>
                 <#assign productFeatureSelectableId =productFeatureAndApplsSelect.productFeatureId/>
                 <#if PRODUCT_STORE_PARM_FACET?has_content && productFeatureTypeId ==PRODUCT_STORE_PARM_FACET>
                     <#assign productFeatureSelectVariantId=""/>
	                 <#list productVariantMapKeys as pAssoc>
		                 <#assign productFeatureAndAppl = delegator.findByAnd("ProductFeatureAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId" ,pAssoc,'productFeatureApplTypeId','STANDARD_FEATURE')) />
                         <#list productFeatureAndAppl as pFeatureAndAppl>
       	                    <#assign productFeatureStandardId =pFeatureAndAppl.productFeatureId/>
       	                    <#if productFeatureStandardId ==productFeatureSelectableId && !productFeatureSelectVariantId?has_content>
                               <#assign productFeatureId=productFeatureStandardId/>
                               <#assign productFeatureSelectVariantId=pFeatureAndAppl.productId/>
       	                    </#if>
                         </#list>
		             </#list>
                     <#if productFeatureSelectVariantId?has_content>
                           <#if !alreadyShownProductFeatureId.contains(productFeatureId)>
	 	                       <#assign variantProdCtntWrapper = productVariantMap.get('${productFeatureSelectVariantId}')/>
	  	                       <#assign productVariantPlpSwatchURL = variantProdCtntWrapper.get("PDP_SWATCH_IMAGE_URL")!"">
		                       <#if (productVariantPlpSwatchURL?string?has_content)>
		                         <#assign productFeatureSwatchURL=productVariantPlpSwatchURL/>
		                       <#else>
	                             <#assign productFeatureDataResources = delegator.findByAnd("ProductFeatureDataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId",productFeatureId,"prodFeatureDataResourceTypeId","PDP_SWATCH_IMAGE_URL"))/>
				                 <#if productFeatureDataResources?has_content>
	                               <#assign productFeatureDataResource = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureDataResources) />
					               <#assign dataResource = productFeatureDataResource.getRelatedOne("DataResource")/>
					               <#assign productFeatureResourceUrl = dataResource.objectInfo!""/>
					               <#if productFeatureResourceUrl?has_content>
	     	                         <#assign productFeatureSwatchURL=productFeatureResourceUrl/>
					               </#if>
					             </#if>
		 					   </#if>
		 					   
		 					   <#if featureOrderSize == 1>
		 					     <#assign variantProductInventoryLevel = Static["com.osafe.services.InventoryServices"].getProductInventoryLevel(productFeatureSelectVariantId?if_exists, request) />
		 					     <#assign inventoryLevel = variantProductInventoryLevel.get("inventoryLevel")/>
		 					     <#assign inventoryInStockFrom = variantProductInventoryLevel.get("inventoryLevelInStockFrom")/>
		 					     <#assign inventoryOutOfStockTo = variantProductInventoryLevel.get("inventoryLevelOutOfStockTo")/>
		 					     
		 					     <#if (inventoryLevel <= inventoryOutOfStockTo)>
		 					       <#assign stockClass = "outOfStock"/>
		 					     <#else>
		 					       <#if (inventoryLevel >= inventoryInStockFrom)>
		 					         <#assign stockClass = "inStock"/>
		 					       <#else>
		 					         <#assign stockClass = "lowStock"/>
		 					       </#if>
                                 </#if>
		 					   </#if>
		 					   <#assign productFeatureType = "${productFeatureTypeId!}:${productFeatureDescription!}"/>

		 					   <#assign selectedClass="false"/>
		 					   <#if parameters.productFeatureType?exists>
		 					     <#assign productFeatureTypeIdParm = parameters.productFeatureType.split(":")/>
		 					     <#if parameters.productFeatureType.equals(productFeatureType)>
		 					       <#assign productFeatureIdx = selectedIdx/>
		 					       <#assign selectedClass="true"/>
		 					     </#if>
		 					   </#if>
		 					   <#assign pdpSwatchImageHeight= IMG_SIZE_PDP_SWATCH_H!""/>
                               <#assign pdpSwatchImageWidth= IMG_SIZE_PDP_SWATCH_W!""/>
                               <#if !parameters.productFeatureType?exists || productFeatureTypeId != productFeatureTypeIdParm[0]!"">
                                 <#if selectedIdx == 0>
                                   <#assign selectedClass="true"/>
                                 </#if>
                               </#if>
		 					   <li class="<#if selectedClass == "true">selected</#if><#if stockClass?exists> ${stockClass}</#if>"><a href="javascript:void(0);" class="pdpFeatureSwatchLink" onclick="javascript:getList('FT${productFeatureTypeId}','${selectedIdx}', 1);"><img src="<@ofbizContentUrl>${productFeatureSwatchURL!""}</@ofbizContentUrl>" class="pdpFeatureSwatchImage" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="FT${productFeatureTypeId}" <#if pdpSwatchImageHeight != '0' && pdpSwatchImageHeight != ''>height = "${pdpSwatchImageHeight}"</#if> <#if pdpSwatchImageWidth != '0' && pdpSwatchImageWidth != ''>width = "${pdpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PDP-Swatch');"/></a></li>
                               <#assign changed = alreadyShownProductFeatureId.add(productFeatureId)/>
                           </#if>
                     </#if>
                 <#else>
                   <li><a href="javascript:void(0);" onclick="javascript:getList('FT${productFeatureTypeId}','${selectedIdx}', 1);">${productFeatureDescription!""}</a></li>
                 </#if>
                 <#assign selectedIdx=selectedIdx + 1/>
            </#list>
            </ul>
            <select name="FT${productFeatureTypeId}">
              <option></option>
            </select>
      </div>
    </#list>
    <input type="hidden" name="product_id" value="${currentProduct.productId}"/>
    <input type="hidden" name="add_product_id" id="add_product_id" value="NULL"/>
    <div>
      <b><span id="product_id_display"> </span></b>
      <b><div id="variant_price_display"> </div></b>
    </div>
  <#else>
    <input type="hidden" name="product_id" value="${currentProduct.productId}"/>
    <input type="hidden" name="add_product_id" value="NULL"/>
    <#assign inStock = false>
  </#if>
 </#if>
<#else>
  <input type="hidden" name="add_product_id" value="${currentProduct.productId}" />
</#if>
</fieldset>
</div>
<#-- Prefill first select box (virtual products only) -->
<#if variantTree?exists && 0 < variantTree.size()>
  <script language="JavaScript" type="text/javascript">eval("list" + "${featureOrderFirst}" + "()");</script>
</#if>
