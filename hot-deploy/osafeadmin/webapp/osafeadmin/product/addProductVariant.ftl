<#if product?has_content>
    <input type="hidden" name="productTypeId" value="FINISHED_GOOD" />
    <#assign currencyUomId = CURRENCY_UOM_DEFAULT!currencyUomId />
    <input type="hidden" name="currencyUomId" value="${parameters.currencyUomId!currencyUomId!}" />
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
                <#if productPriceCondList?has_content><span class="pricingInfo">${uiLabelMap.PricingRulesApplyInfo}</span></#if>
            </div>
         </div>
     </div>

    <#if productDistinguishingFeatureTypes?has_content>
        <#list productDistinguishingFeatureTypes as productFeatureGroupView>
            <#assign productFeatureGroup = delegator.findByPrimaryKey("ProductFeatureGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureGroupView.productFeatureGroupId))>
            <div class="infoRow column">
                <div class="infoEntry">
                    <div class="infoCaption">
                      <label>${productFeatureGroup.description!productFeatureGroup.productFeatureGroupId!}:</label>
                    </div>
                    <div class="infoValue">
                        <#assign productFeatureGroupAndAppls = delegator.findByAnd("ProdFeaGrpAppAndProdFeaApp", {"productId" : parameters.productId!"", "productFeatureGroupId" : productFeatureGroupView.productFeatureGroupId!""})>
                        <#if productFeatureGroupAndAppls?has_content>
                            <#assign productFeatureGroupAndAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureGroupAndAppls) />
                            <#assign productFeatureApplType = productFeatureGroupAndAppl.productFeatureApplTypeId! />
                            <#assign productFeatureId = productFeatureGroupAndAppl.productFeatureId! />
                            <#assign productFeature = delegator.findByPrimaryKey("ProductFeature", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId" , productFeatureId!))>
                            ${productFeature.description!productFeature.productFeatureId!}
                        </#if>
                    </div>
                </div>
             </div>
        </#list>
    </#if>

    <div class="infoRow row">
        <div class="header"><h2>${uiLabelMap.ProductVariantAttributesHeading}</h2></div>
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
                <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VariantListPriceInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${globalContext.currencySymbol!}${productListPrice.price!}"), locale)/>
                <a href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
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
                <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "VariantSalePriceInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${globalContext.currencySymbol!}${productDefaultPrice.price!}"), locale)/>
                <a href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
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


    <div class="infoRow row">
        <div class="header"><h2>${uiLabelMap.ProductSelectableFeaturesHeading}</h2></div>
    </div>

    <#if productSelectableFeatureTypes?has_content>
        <#list productSelectableFeatureTypes as productFeatureGroupView>
            <#assign productSelectableFeatureList = Static["javolution.util.FastList"].newInstance()/>
            <#assign productFeatureGroupApplList = delegator.findByAnd("ProductFeatureGroupAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureGroupView.productFeatureGroupId), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
            <#assign productFeatureGroupAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureGroupApplList!)/>
            <#if productFeatureGroupAppls?has_content>
                <#assign productFeatureGroup = delegator.findByPrimaryKey("ProductFeatureGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureGroupView.productFeatureGroupId))>
                <div class="infoRow column">
                    <div class="infoEntry">
                        <div class="infoCaption">
                          <label>${productFeatureGroup.description!productFeatureGroup.productFeatureGroupId!}:</label>
                        </div>
                        <div class="infoValue">
                            <select id="selectableProductFeature_${productFeatureGroup.productFeatureGroupId!}" name="selectableProductFeature_${productFeatureGroup.productFeatureGroupId!}" class="short">
                                <option value="">Select</option>
                                <#assign selectedFeture = parameters.get("selectableProductFeature_${productFeatureGroup.productFeatureGroupId}")?if_exists>
                                <#list productFeatureGroupAppls as productFeatureGroupAppl>
                                    <#assign productFeature = productFeatureGroupAppl.getRelatedOne("ProductFeature")/>
                                    <#-- Prepared the list Product Feature to sort based on Description -->
                                    <#assign changed = productSelectableFeatureList.add(productFeature)/>
                                 </#list>
                                 <#-- Sort the Product Feature List based on Description -->
                                 <#assign productSelectableFeatureList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productSelectableFeatureList,Static["org.ofbiz.base.util.UtilMisc"].toList("description"))/>
                                 <#list productSelectableFeatureList as productFeature>
                                    <#assign productFeatureName = productFeature.description?trim/>
                                    <#assign optionValue = "${productFeature.productFeatureId!}@SELECTABLE_FEATURE">
                                    <option value="${optionValue!}" <#if selectedFeture?has_content && selectedFeture.equals(optionValue)>selected</#if>><#if productFeatureName?has_content>${productFeatureName?if_exists}<#else>${productFeature.productFeatureId?if_exists}</#if></option>                                     
                                 </#list>
                            </select>
                        </div>
                    </div>
                 </div>
             </#if>
        </#list>
    </#if>

    <div class="infoRow row">
        <div class="header"><h2>${uiLabelMap.ProductDistinguishingFeaturesHeading}</h2></div>
    </div>

    <#if productDistinguishingFeatureTypes?has_content>
        <#list productDistinguishingFeatureTypes as productFeatureGroupView>
            <#assign productDescriptiveFeatureList = Static["javolution.util.FastList"].newInstance()/>
            <#assign productFeatureGroupApplList = delegator.findByAnd("ProductFeatureGroupAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureGroupView.productFeatureGroupId), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
            <#assign productFeatureGroupAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureGroupApplList!)/>
            <#if productFeatureGroupAppls?has_content>
                <#assign productFeatureGroup = delegator.findByPrimaryKey("ProductFeatureGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureGroupView.productFeatureGroupId))>
                <div class="infoRow column">
                    <div class="infoEntry">
                        <div class="infoCaption">
                          <label>${productFeatureGroup.description!productFeatureGroup.productFeatureGroupId!}:</label>
                        </div>
                        <div class="infoValue">
                            <select id="distinguishProductFeature_${productFeatureGroup.productFeatureGroupId!}" name="distinguishProductFeature_${productFeatureGroup.productFeatureGroupId!}" class="short">
                                <option value="">Select</option>
                                <#assign selectedFeture = parameters.get("distinguishProductFeature_${productFeatureGroup.productFeatureGroupId}")?if_exists>
                                <#list productFeatureGroupAppls as productFeatureGroupAppl>
                                    <#assign productFeature = productFeatureGroupAppl.getRelatedOne("ProductFeature")/>
                                    <#-- Prepared the list Product Feature to sort based on Description -->
                                    <#assign changed = productDescriptiveFeatureList.add(productFeature)/>
                                 </#list>
                                 <#-- Sort the Product Feature List based on Description -->
                                 <#assign productDescriptiveFeatureList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productDescriptiveFeatureList,Static["org.ofbiz.base.util.UtilMisc"].toList("description"))/>
                                 <#list productDescriptiveFeatureList as productFeature>
                                    <#assign productFeatureName = productFeature.description?trim/>
                                    <#assign optionValue = "${productFeature.productFeatureId!}@DISTINGUISHING_FEAT">
                                    <option value="${optionValue!}" <#if selectedFeture?has_content && selectedFeture.equals(optionValue)>selected</#if>><#if productFeatureName?has_content>${productFeatureName?if_exists}<#else>${productFeature.productFeatureId?if_exists}</#if></option>                                     
                                 </#list>
                            </select>
                        </div>
                        <div class="infoIcon">
                            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.AddDescriptiveFeatureHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                        </div>
                    </div>
                 </div>
             </#if>
        </#list>
    </#if>
</#if>