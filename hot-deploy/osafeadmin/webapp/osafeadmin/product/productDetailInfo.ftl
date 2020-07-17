<#if currentProduct?has_content>
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

    <#assign internalName = currentProduct.internalName!"" />
    <#if passedVariantProductIds?has_content || parameters.variantProductIds?has_content>
      <#assign isVariant = "Y" />
      <#assign isVirtual = "N" />
    </#if>

    <#if passedVariantProductIds?has_content>
        <#assign variantProductIds = ""/>
        <#list passedVariantProductIds as variantProductId>
            <#assign variantProductIds = variantProductIds+variantProductId/>
            <#if variantProductId_has_next?if_exists>
                <#assign variantProductIds = variantProductIds+"|"/>
            </#if>
        </#list>
    </#if>
</#if>


    <input type="hidden" name="variantProductIds" value="${parameters.variantProductIds!variantProductIds!""}" />
    <input type="hidden" name="productTypeId" value="FINISHED_GOOD" />
    <#assign currencyUomId = CURRENCY_UOM_DEFAULT!currencyUomId />
    <input type="hidden" name="currencyUomId" value="${parameters.currencyUomId!currencyUomId!}" />
    <#if (mode?has_content && mode == "edit")>
      <input type="hidden" name="isVariant" id="isVariant" value="${isVariant!""}"/>
      <input type="hidden" name="isVirtual" id="isVirtual" value="${isVirtual!""}"/>
    </#if>
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ProductIDCaption}</label>
            </div>
            <div class="infoValue">
                <#if (mode?has_content && mode == "add") && (!virtualProduct?has_content)>
                    <input type="text" name="productId" id="productId" maxlength="20" value="${parameters.productId!""}"/>
                <#elseif (mode?has_content && mode == "add") && (virtualProduct?has_content)>
                    <input type="hidden" name="productId" id="productId" maxlength="20" value="${virtualProduct.productId!parameters.productId!""}"/>
                    <input type="text" name="productIdTo" id="productIdTo" maxlength="20" value="${parameters.productIdTo!""}"/>    
                <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="productId" id="productId" value="${parameters.productId!currentProduct.productId?if_exists}" />${parameters.productId!currentProduct.productId?if_exists}
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
             <#if (mode?has_content && mode == "add")>
               <input type="text" name="internalName" id="internalName" value="${parameters.internalName!""}" />
             <#elseif mode?has_content && mode == "edit">
               <input type="text" name="internalName" id="internalName" value="${parameters.internalName!internalName!""}" />
             </#if>
           </div>
       </div>
   </div>
    
    <#if (mode?has_content && mode == "add") && (!virtualProduct?has_content)>
        <#if rootProductCategoryId?has_content>
          <#assign topLevelList = delegator.findByAnd("ProductCategoryRollupAndChild", {"parentProductCategoryId" : rootProductCategoryId}, Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum')) />
          <#assign topLevelList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(topLevelList) />
        </#if>
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
                                <option value="${category.productCategoryId?if_exists}" <#if (parameters.productCategoryId!"") == "${category.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;${category.categoryName?if_exists}</option>
                                <#assign subCatList = delegator.findByAnd("ProductCategoryRollupAndChild", {"parentProductCategoryId" : category.getString("productCategoryId")}, Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum')) />
                                <#assign subCatList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(subCatList) />
                                <#if subCatList?exists && subCatList?has_content>
                                    <#list subCatList as subCategory>
                                        <option value="${subCategory.productCategoryId?if_exists}" <#if (parameters.productCategoryId!"") == "${subCategory.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;&nbsp;&nbsp;${subCategory.categoryName?if_exists}</option>
                                    </#list>
                                </#if>
                            </#list>
                        </#if>
                    </select>
                </div>
            </div>
        </div>
    <#elseif (mode?has_content && mode == "edit") && (isVirtual = 'Y' || isFinished = 'Y')>
        <#if productCategory?exists>
            <#assign primaryProdCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategory.primaryParentCategoryId?if_exists), false)/>
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
                    <div class="infoIcon">
                      <a href="<@ofbizUrl>productCategoryMembershipDetail?productId=${parameters.productId!currentProduct.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductcategoryMembershipTooltip}');" onMouseout="hideTooltip()"><span class="membershipIcon"></span></a>
                    </div>
                </div>
            </div>
        </#if>
    </#if>

    

    <div class="infoRow <#if (mode?has_content && mode == "add")>row<#else>column</#if>">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TypeOfProductCaption}</label>
            </div>
            <div class="infoValue">
               <#if (mode?has_content && mode == "add") && (!virtualProduct?has_content)>
                 <div class="entry checkbox medium">
                   <input type="hidden" name="isVariant" id="isVariant" value="N"/>
                   <input class="checkBoxEntry" type="radio" name="isVirtual"  value="Y" <#if parameters.isVirtual?exists && parameters.isVirtual?string == "Y">checked="checked"<#elseif !parameters.isVirtual?exists>checked="checked"</#if> onChange="javascript:selectFinishedProduct(this)"/>${uiLabelMap.VirtualProductLabel}<br/>
                   <input class="checkBoxEntry" type="radio" name="isVirtual" value="N" <#if  parameters.isVirtual?exists && parameters.isVirtual?string == "N">checked="checked"</#if> onChange="javascript:selectFinishedProduct(this)"/>${uiLabelMap.FinishedGoodLabel}
                 </div>
               <#elseif (mode?has_content && mode == "add") && (virtualProduct?has_content)>
                 <div class="entry checkbox medium">
                   <input type="hidden" name="isVirtual" id="isVirtual" value="N"/>
                   <input class="checkBoxEntry" type="radio" name="isVariant"  value="Y" checked="checked"/>${uiLabelMap.VariantProductLabel}<br/>
                 </div>
               <#elseif mode?has_content && mode == "edit">
                 <#if isVirtual=='Y' && isVariant=='N'>
                   ${uiLabelMap.VirtualLabel}
                 <#elseif isVirtual=='N' && isVariant=='Y'>
                   ${uiLabelMap.VariantLabel}
                 <#elseif isVirtual=='N' && isVariant=='N'>
                   ${uiLabelMap.FinishedGoodLabel}
                 <#else>
                   ${uiLabelMap.UnknownLabel}
                 </#if>
               </#if>
            </div>
            <div class="infoIcon">
              <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.TypeOfProductInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
        </div>
    </div>

    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.ProductNameCaption}</label>
            </div>
            <div class="infoValue">
                <#if (isVariant?exists && isVariant == 'Y') ||  (mode?has_content && mode == "add" && virtualProduct?has_content)>
                    <#assign productVariantName = Static["org.apache.commons.lang.StringEscapeUtils"].unescapeHtml(productDetailName) >
                    <input type="hidden" name="productDetailName" id="productDetailName" value="${productVariantName!""}"/>
                    ${productVariantName!""}
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
                <#if (isVariant?exists && isVariant == 'Y') ||  (mode?has_content && mode == "add" && virtualProduct?has_content)>
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
               <#if (isVariant?exists && isVariant == 'Y') ||  (mode?has_content && mode == "add" && virtualProduct?has_content)>
                   ${parameters.pdpLabel!pdpLabel!""}
               <#else>
                   <textarea class="shortArea" name="pdpLabel" id="pdpLabel" cols="50" rows="1">${parameters.pdpLabel!pdpLabel!""}</textarea>
               </#if>
           </div>
       </div>
   </div>
