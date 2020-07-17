<#if product?has_content>
    <input type="hidden" name="productTypeId" value="FINISHED_GOOD" />
    <#assign currencyUomId = CURRENCY_UOM_DEFAULT!currencyUomId />
    <input type="hidden" name="currencyUomId" value="${parameters.currencyUomId!currencyUomId!}" />
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
            </div>
        </div>
    </#if>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ProductIdCaption}</label>
            </div>
            <div class="infoValue">
                <#if !parameters.productIdTo?has_content>
                    <#assign productIdToSeqId = delegator.getNextSeqId("Product")/>
                </#if>
                <input type="hidden" name="productId" id="productId" maxlength="20" value="${parameters.productId!product.productId!""}"/>
                <input type="hidden" name="productIdTo" id="productIdTo" maxlength="20" value="${parameters.productIdTo!productIdToSeqId!""}"/>
                ${parameters.productIdTo!productIdToSeqId!""}
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ItemNoCaption}</label>
            </div>
            <div class="infoValue">
                <#assign internalName = product.internalName!"" />
                <input type="text" name="internalName" id="internalName" value="${parameters.internalName!internalName!""}" />
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.VirtualListPriceCaption}</label>
            </div>
            <div class="infoValue">
                <#if productListPrice?has_content>
                    <@ofbizCurrency amount=productListPrice.price! isoCode=productListPrice.currencyUomId!/>
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.VirtualSalePriceCaption}</label>
            </div>
            <div class="infoValue">
                <#if productDefaultPrice?has_content>
                    <@ofbizCurrency amount=productDefaultPrice.price! isoCode=productDefaultPrice.currencyUomId!/>
                </#if>
            </div>
         </div>
     </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.VariantListPriceCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" class="textEntry textAlignRight" name="variantListPrice" id="variantListPrice" value="${parameters.variantListPrice!""}"/>
            </div>
            <div class="infoIcon">
                <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.VariantListPriceInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.VariantSalePriceCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text"  class="textEntry textAlignRight" name="variantSalePrice" id="variantSalePrice" value="${parameters.variantSalePrice!""}"/>
            </div>
            <div class="infoIcon">
                <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.VariantSalePriceInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
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
                    <#if product.introductionDate?has_content>
                        <#assign introductionDate = (product.introductionDate)?string(preferredDateFormat)>
                    </#if>
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
                    <#if product.salesDiscontinuationDate?has_content>
                        <#assign salesDiscontinuationDate = (product.salesDiscontinuationDate)?string(preferredDateFormat)>
                    </#if>
                    <input class="dateEntry datePicker" type="text" id="salesDiscontinuationDate" name="salesDiscontinuationDate" maxlength="40" value="${parameters.salesDiscontinuationDate!salesDiscontinuationDate!""}"/>
                 </div>
            </div>
         </div>
    </div>

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

    <#list productFeatureTypes as productFeatureType>
        <#assign productFeatureAndApplList = delegator.findByAnd("ProductFeatureAndAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId" , product.productId, "productFeatureTypeId", productFeatureType.productFeatureTypeId), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
        <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureAndApplList!)/>
        <#if productFeatureAndAppls?has_content>
            <div class="infoRow column">
                <div class="infoEntry">
                    <div class="infoCaption">
                      <label>${productFeatureType.description!productFeatureType.productFeatureTypeId!}:</label>
                    </div>
                    <div class="infoValue">
                        <select id="productFeature_${productFeatureType.productFeatureTypeId!}" name="productFeature_${productFeatureType.productFeatureTypeId!}" class="short">
                            <option value="">Select</option>
                            <#assign selectedFeture = parameters.get("productFeature_${productFeatureType.productFeatureTypeId}")?if_exists>
                            <#assign usedFeatureIdList = Static["javolution.util.FastList"].newInstance()/>
                            <#list productFeatureAndAppls as productFeatureAndAppl>
                                <#if !usedFeatureIdList.contains(productFeatureAndAppl.productFeatureId!"")>
                                    <#assign optionValue = "${productFeatureAndAppl.productFeatureId!}@${productFeatureAndAppl.productFeatureApplTypeId!}">
                                    <option value="${optionValue!}" <#if selectedFeture?has_content && selectedFeture.equals(optionValue)>selected</#if>>${productFeatureAndAppl.description!productFeatureAndAppl.productFeatureId!""}</option>
                                    <#assign changed = usedFeatureIdList.add(productFeatureAndAppl.productFeatureId!"")/>
                                </#if>
                             </#list>
                        </select>
                    </div>
                </div>
             </div>
         </#if>
    </#list>
</#if>