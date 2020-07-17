<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if product?has_content>
    <#assign productDetailName = ""/>
    <#if (PRODUCT_NAME?exists && PRODUCT_NAME?has_content)>
      <#assign content = PRODUCT_NAME.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign productDetailName = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign plpLabel = ""/>
    <#if (PLP_LABEL?exists && PLP_LABEL?has_content)>
      <#assign content = PLP_LABEL.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign plpLabel = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign pdpLabel = ""/>
    <#if (PDP_LABEL?exists && PDP_LABEL?has_content)>
      <#assign content = PDP_LABEL.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign pdpLabel = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign shortSalesPitch = ""/>
    <#if (SHORT_SALES_PITCH?exists && SHORT_SALES_PITCH?has_content)>
      <#assign content = SHORT_SALES_PITCH.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign shortSalesPitch = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign longDescription = ""/>
    <#if (LONG_DESCRIPTION?exists && LONG_DESCRIPTION?has_content)>
      <#assign content = LONG_DESCRIPTION.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign longDescription = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign specialInstruction = ""/>
    <#if (SPECIALINSTRUCTIONS?exists && SPECIALINSTRUCTIONS?has_content)>
      <#assign content = SPECIALINSTRUCTIONS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign specialInstruction = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign deliveryInfo = ""/>
    <#if (DELIVERY_INFO?exists && DELIVERY_INFO?has_content)>
      <#assign content = DELIVERY_INFO.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign deliveryInfo = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign directions = ""/>
    <#if (DIRECTIONS?exists && DIRECTIONS?has_content)>
      <#assign content = DIRECTIONS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign directions = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign termsAndConds = ""/>
    <#if (TERMS_AND_CONDS?exists && TERMS_AND_CONDS?has_content)>
      <#assign content = TERMS_AND_CONDS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign termsAndConds = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign ingredients = ""/>
    <#if (INGREDIENTS?exists && INGREDIENTS?has_content)>
      <#assign content = INGREDIENTS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign ingredients = electronicText.textData!""/>
	  </#if>
    </#if>
    <#assign warnings = ""/>
    <#if (WARNINGS?exists && WARNINGS?has_content)>
      <#assign content = WARNINGS.getRelatedOne("Content")!""/>
      <#assign dataResource = content.getRelatedOne("DataResource")!""/>
      <#if dataResource?has_content>
	      <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
	      <#assign warnings = electronicText.textData!""/>
	  </#if>
    </#if>

  <#assign isVariant = product.isVariant!"" />
  <#assign internalName = product.internalName!"" />
  <#assign isVirtual = product.isVirtual!"" />
  <#if productListPrice?exists && productListPrice?has_content>
    <#assign listPrice = productListPrice.price!"" />
  </#if>
  <#if productDefaultPrice?exists && productDefaultPrice?has_content>
    <#assign defaultPrice = productDefaultPrice.price!"" />
  </#if>
  
  <#if product.introductionDate?has_content>
    <#assign introductionDate = (product.introductionDate)?string(preferredDateFormat)>
  </#if>
  <#if product.salesDiscontinuationDate?has_content>
    <#assign salesDiscontinuationDate = (product.salesDiscontinuationDate)?string(preferredDateFormat)>
  </#if>
</#if>
      
      <input type="hidden" name="productTypeId" value="FINISHED_GOOD" />
      <#assign currencyUomId = CURRENCY_UOM_DEFAULT!currencyUomId />
      <input type="hidden" name="currencyUomId" value="${parameters.currencyUomId!currencyUomId!}" />
      <#if (mode?has_content && mode == "add")>
        <div class="infoRow row">
          <div class="infoEntry">
            <div class="infoCaption">
              <label>${uiLabelMap.CategoryCaption}</label>
            </div>
            <div class="infoValue">
              <select id="productCategoryId" name="productCategoryId">
          <option value="" <#if (parameters.productCategoryId!"") == "">selected</#if>>${uiLabelMap.SelectOneLabel}</option>
            <#if topLevelList?exists && topLevelList?has_content>
              <#list topLevelList as category>
                <#if catContentWrappers?exists>
                  <option value="${category.productCategoryId?if_exists}" <#if (parameters.productCategoryId!"") == "${category.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;${catContentWrappers[category.productCategoryId].get("CATEGORY_NAME")?if_exists}</option>
                  <#assign subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
                  <#if subCatList?exists && subCatList?has_content>
                    <#list subCatList as subCategory>
                      <option value="${subCategory.productCategoryId?if_exists}" <#if (parameters.productCategoryId!"") == "${subCategory.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;&nbsp;&nbsp;${catContentWrappers[subCategory.productCategoryId].get("CATEGORY_NAME")?if_exists}</option>
                    </#list>
                  </#if>
                </#if>
              </#list>
            </#if>
          </select>
            </div>
          </div>
        </div>
      <#elseif mode?has_content && mode == "edit">
        <#if productCategory?exists>
            <#assign primaryProdCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategory.primaryParentCategoryId?if_exists), true)/>
            <#if primaryProdCategory?exists>
              <div class="infoRow column">
                <div class="infoEntry">
                  <div class="infoCaption">
                    <label>${uiLabelMap.NavBarCaption}</label>
                  </div>
                  <div class="infoValue">
                    ${primaryProdCategory.categoryName!""}
                    <input type="hidden" name="primaryParentCategoryId" id="primaryParentCategoryId" value="${primaryProdCategory.productCategoryId!""}"/>
                  </div>
                  <div>
                    <input type="button" class="standardBtn secondary" name="moveButton" id="moveButton" value="${uiLabelMap.MoveBtn}" onClick="javascript:void(0);"/>
                  </div>
                </div>
              </div>
            </#if>
            <div class="infoRow column">
              <div class="infoEntry">
                <div class="infoCaption">
                  <label>${uiLabelMap.SubItemCaption}</label>
                </div>
                <div class="infoValue">
                  ${productCategory.categoryName!""}
                  <input type="hidden" name="productCategoryId" id="productCategoryId" value="${productCategory.productCategoryId!""}"/>
                </div>
                <div>
                  <input type="button" class="standardBtn secondary" name="moveButton" id="moveButton" value="${uiLabelMap.MoveBtn}" onClick="javascript:void(0);"/>
                </div>
              </div>
            </div>
      </#if>
      </#if>
      <div class="infoRow column">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ProductIdCaption}</label>
              </div>
              <div class="infoValue">
                  <#if (mode?has_content && mode == "add")>
                    <#if !parameters.productId?has_content>
                      <#assign productSeqId = delegator.getNextSeqId("Product")/>
                    </#if>
                    <input type="hidden" name="productId" id="productId" maxlength="20" value="${parameters.productId!productSeqId!""}"/>${parameters.productId!productSeqId!""}
                  <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="productId" value="${parameters.productId!product.productId?if_exists}" />${parameters.productId!product.productId?if_exists}
                  </#if>
              </div>
          </div>
       </div>
       <div class="infoRow column">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.ItemNoCaption}</label>
               </div>
               <div class="infoValue">
                 <input type="text" name="internalName" id="internalName" value="${parameters.internalName!internalName!""}" />
               </div>
           </div>
       </div>
       <div class="infoRow column">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.VirtualCaption}</label>
               </div>
               <div class="infoValue">
                 <#if (mode?has_content && mode == "add")>
                   <div class="entry checkbox short">
                     <input class="checkBoxEntry" type="radio" name="isVirtual"  value="Y" <#if parameters.isVirtual?exists && parameters.isVirtual?string == "Y">checked="checked"<#elseif !parameters.isVirtual?exists>checked="checked"</#if>/>${uiLabelMap.YesLabel}
                     <input class="checkBoxEntry" type="radio" name="isVirtual" value="N" <#if  parameters.isVirtual?exists && parameters.isVirtual?string == "N">checked="checked"</#if>/>${uiLabelMap.NoLabel}
                   </div>
                 <#elseif mode?has_content && mode == "edit">
                   ${isVirtual!""}
                 </#if>
               </div>
           </div>
       </div>
       <#if (mode?has_content && mode == "edit")>
         <div class="infoRow column">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.VariantCaption}</label>
               </div>
               <div class="infoValue">
                   ${product.isVariant!""}
                   <input type="hidden" name="isVariant" id="isVariant" value="${product.isVariant!""}"/>
               </div>
           </div>
         </div>
       </#if>
       
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.ProductNameCaption}</label>
               </div>
               <div class="infoValue">
               <#if isVariant?exists && isVariant == 'Y'>
                 ${parameters.productDetailName!productDetailName!""}
               <#else>
                 <textarea class="shortArea" name="productDetailName" id="productDetailName" cols="50" rows="1">${parameters.productDetailName!productDetailName!""}</textarea>
               </#if>
               </div>
           </div>
       </div>
       
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PLPLabelCaption}</label>
               </div>
               <div class="infoValue">
               <#if isVariant?exists && isVariant == 'Y'>
                 ${parameters.plpLabel!plpLabel!""}
               <#else>
                 <textarea class="shortArea" name="plpLabel" id="plpLabel" cols="50" rows="1">${parameters.plpLabel!plpLabel!""}</textarea>
               </#if>
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPLabelCaption}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.pdpLabel!pdpLabel!""}
                 <#else>
                   <textarea class="shortArea" name="pdpLabel" id="pdpLabel" cols="50" rows="1">${parameters.pdpLabel!pdpLabel!""}</textarea>
                 </#if>
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPLongDescriptionHeading}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.longDescription!longDescription!""}
                 <#else>
                   <textarea name="longDescription" cols="50" rows="5">${parameters.longDescription!longDescription!""}</textarea>
                 </#if>
               </div>
           </div>
       </div>

       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPSalesPitchHeading}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.shortSalesPitch!shortSalesPitch!""}
                 <#else>
                   <textarea class="shortArea" name="shortSalesPitch" id="shortSalesPitch" cols="50" rows="1">${parameters.shortSalesPitch!shortSalesPitch!""}</textarea>
                 </#if>
               </div>
           </div>
       </div>
       
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPSpecialInstructionsHeading}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.specialInstruction!specialInstruction!""}
                 <#else>
                   <textarea class="shortArea" name="specialInstruction" cols="50" rows="5">${parameters.specialInstruction!specialInstruction!""}</textarea>
                 </#if>
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPDeliveryInfoHeading}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.deliveryInfo!deliveryInfo!""}
                 <#else>
                   <textarea class="shortArea" name="deliveryInfo" cols="50" rows="5">${parameters.deliveryInfo!deliveryInfo!""}</textarea>
                 </#if>
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPDirectionsHeading}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.directions!directions!""}
                 <#else>
                   <textarea class="shortArea" name="directions" cols="50" rows="5">${parameters.directions!directions!""}</textarea>
                 </#if>
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPTermsConditionsHeading}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.termsAndConds!termsAndConds!""}
                 <#else>
                   <textarea class="shortArea" name="termsAndConds" cols="50" rows="5">${parameters.termsAndConds!termsAndConds!""}</textarea>
                 </#if>
                   
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPIngredientsHeading}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.ingredients!ingredients!""}
                 <#else>
                   <textarea class="shortArea" name="ingredients" cols="50" rows="5">${parameters.ingredients!ingredients!""}</textarea>
                 </#if>
               </div>
           </div>
       </div>
       <div class="infoRow row">
           <div class="infoEntry long">
               <div class="infoCaption">
                   <label>${uiLabelMap.PDPWarningsHeading}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   ${parameters.warnings!warnings!""}
                 <#else>
                   <textarea class="shortArea" name="warnings" cols="50" rows="5">${parameters.warnings!warnings!""}</textarea>
                 </#if>
               </div>
           </div>
       </div>
       <div class="infoRow column">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.ListPriceCaption}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   <@ofbizCurrency amount=parameters.listPrice!productListPrice.price! isoCode=productListPrice.currencyUomId!/>
                 <#else>
                   <#if (mode?has_content && mode == "add")>
                     <input type="text" class="textEntry textAlignRight" name="listPrice" id="listPrice" value="${parameters.listPrice!listPrice!}"/>
                   <#elseif mode?has_content && mode == "edit">
                     <input type="text" class="textEntry textAlignRight" name="listPrice" id="listPrice" value="${parameters.listPrice!listPrice?string("0.00")!}"/>
                   </#if>
                 </#if>
               </div>
           </div>
       </div>
       <div class="infoRow column">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.SalePriceCaption}</label>
               </div>
               <div class="infoValue">
                 <#if isVariant?exists && isVariant == 'Y'>
                   <@ofbizCurrency amount=parameters.defaultPrice!productDefaultPrice.price! isoCode=productDefaultPrice.currencyUomId!/>
                 <#else>
                   <#if (mode?has_content && mode == "add")>
                     <input type="text"  class="textEntry textAlignRight" name="defaultPrice" id="defaultPrice" value="${parameters.defaultPrice!defaultPrice!}"/>
                   <#elseif mode?has_content && mode == "edit">
                     <input type="text"  class="textEntry textAlignRight" name="defaultPrice" id="defaultPrice" value="${parameters.defaultPrice!defaultPrice?string("0.00")!}"/>
                     <#if productPriceCondList?has_content><span class="pricingInfo">${uiLabelMap.PricingRulesApplyInfo}</span></#if>
                   </#if>
                 </#if>
               </div>
            </div>
        </div>
        <div class="infoRow column">
            <div class="infoEntry">
                <div class="infoCaption">
                    <label>${uiLabelMap.IntroducedDateCaption}</label>
                </div>
                <div class="infoValue">
                    <div class="entryInput from">
                        <input class="dateEntry datePicker" type="text" id="introductionDate" name="introductionDate" maxlength="40" value="${parameters.introductionDate!introductionDate!""}"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="infoRow column">
            <div class="infoEntry">
                <div class="infoCaption">
                    <label>${uiLabelMap.DiscontinuedDateCaption}</label>
                </div>
                <div class="infoValue">
                    <div class="entryInput from">
                        <input class="dateEntry datePicker" type="text" id="salesDiscontinuationDate" name="salesDiscontinuationDate" maxlength="40" value="${parameters.salesDiscontinuationDate!salesDiscontinuationDate!""}"/>
                     </div>
                </div>
             </div>
        </div>
        <#if product?exists>
          <#assign bfTotalInventoryProductAttribute = delegator.findOne("ProductAttribute", {"productId" : product.productId, "attrName" : "BF_INVENTORY_TOT"}, true)?if_exists/> 
          <#if bfTotalInventoryProductAttribute?exists>
            <#assign bfTotalInventory = bfTotalInventoryProductAttribute.attrValue!>
          </#if>
          
          <#assign bfWHInventoryProductAttribute = delegator.findOne("ProductAttribute", {"productId" : product.productId, "attrName" : "BF_INVENTORY_WHS"}, true)?if_exists/> 
          <#if bfWHInventoryProductAttribute?exists>
            <#assign bfWHInventory = bfWHInventoryProductAttribute.attrValue!>
          </#if>
        </#if>
        <div class="infoRow column">
          <div class="infoEntry long">
            <div class="infoCaption">
              <label>${uiLabelMap.BFTotalInventoryCaption}</label>
            </div>
            <div class="infoValue">
              <input type="text" class="textEntry" name="bfTotalInventory" id="bfTotalInventory" value="${parameters.bfTotalInventory!bfTotalInventory!""}" />
            </div>
          </div>
        </div>
        
        <div class="infoRow column">
           <div class="infoEntry">
               <div class="infoCaption">
                   <label>${uiLabelMap.BFWareHouseInventoryCaption}</label>
               </div>
               <div class="infoValue">
                 <input type="text" class="textEntry" name="bfWHInventory" id="bfWHInventory" value="${parameters.bfWHInventory!bfWHInventory!""}" />
               </div>
           </div>
       </div>
<#include "component://osafeadmin/webapp/osafeadmin/product/productIdentificationDetail.ftl"/>