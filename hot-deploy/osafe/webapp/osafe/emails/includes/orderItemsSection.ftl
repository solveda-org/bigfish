<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#-- NOTE: this template is used for the orderstatus screen in ecommerce AND for order notification emails through the OrderNoticeEmail.ftl file -->
<#-- the "urlPrefix" value will be prepended to URLs by the ofbizUrl transform if/when there is no "request" object in the context -->
<#if baseEcommerceUrl?exists><#assign urlPrefix = baseEcommerceUrl/></#if>
<#-- Order Items Table-->
<table cellpadding="0" cellspacing="0" style="width: 100%; border: #000000 solid 1px; border-collapse: collapse; font-family:${emailFontFamily};">
    <tr>
        <td style="padding-left: 10px; font-size: ${emailTableHeadingFontSize}; background-color: ${emailTableHeadingColor}">${uiLabelMap.OrderItemsHeading}</td>
    </tr>
    <tr>
        <td style="border-top: #000000 solid 1px; border-collapse: collapse;">
        <table cellpadding="0" cellspacing="0" style="width: 100%; margin: 10px; font-family:${emailFontFamily};">
            <#-- Heading Row -->
            <tr>
                <td colspan="2" style="font-weight: bold;">${uiLabelMap.ProductLabel}</td>
                <td style="font-weight: bold;">${uiLabelMap.QuantityLabel}</td>
                <td style="font-weight: bold;">${uiLabelMap.PriceLabel}</td>
                <td style="font-weight: bold;">${uiLabelMap.TotalLabel}</td>
            </tr>

            <#-- Body Rows -->
            <#list orderItems as orderItem>
                <#assign product = orderItem.getRelatedOneCache("Product")?if_exists/> <#-- should always exist because of FK constraint, but just in case -->
                <#assign urlProductId = product.productId>
                <#if product.isVariant?if_exists?upper_case == "Y">
                    <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(product.productId, delegator)?if_exists>
                    <#assign urlProductId=virtualProduct.productId>
                </#if>
                <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
                <#if (!productImageUrl?has_content && !(productImageUrl == "null")) && virtualProduct?has_content>
                    <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
                </#if>
                <#-- If the string is a literal "null" make it an "" empty string then all normal logic can stay the same -->
                <#if (productImageUrl?string?has_content && (productImageUrl == "null"))>
                    <#assign productImageUrl = "">
                </#if>
                <#assign prodDescription = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "DESCRIPTION", locale, dispatcher)?if_exists>
                <#if !prodDescription?has_content && virtualProduct?has_content>
                    <#assign prodDescription = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "DESCRIPTION", locale, dispatcher)?if_exists>
                </#if>
                <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher)?if_exists>
                <#if !productName?has_content && virtualProduct?has_content>
                    <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher)?if_exists>
                </#if>
            <tr>
                <td <#if !orderItem_has_next><#-- Last Row Logic--></#if>><a href="<@ofbizUrl fullPath="true" secure="false">eCommerceProductDetail?productId=${urlProductId}</@ofbizUrl>" id="image_${urlProductId}">
                    <img border="0" style="width: 75px; height: 75px;" alt="${StringUtil.wrapString(productName)}" src="${baseUrl}/${productImageUrl}">
                </a>
                </td>
                <td <#if !orderItem_has_next><#-- Last Row Logic--></#if>>
                <table cellpadding="0" cellspacing="0" style="width: 100%; font-family:${emailFontFamily};">
                    <tr>
                        <td>
                            <a href="<@ofbizUrl fullPath="true" secure="false">eCommerceProductDetail?productId=${urlProductId}</@ofbizUrl>">${StringUtil.wrapString(productName)}</a>
                        </td>
                    </tr>
                   <#assign features = "">
                   <#assign standardFeatureList = product.getRelatedCache("ProductFeatureAndAppl")/>
                   <#assign standardFeatureList = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(standardFeatureList,Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureApplTypeId","STANDARD_FEATURE"))![]>
                   <#if standardFeatureList?has_content>
                       <#assign features = standardFeatureList>
                   </#if>
                   <#if features?has_content>
                     <#list features as feature>
                        <#assign productFeatureType = feature.getRelatedOneCache("ProductFeatureType")>
                        <tr>
                            <td>${productFeatureType.description!""}: ${feature.description?default("")}</td>
                        </tr>
                     </#list>
                   </#if>
                </table>
                </td>
                <td <#if !orderItem_has_next><#-- Last Row Logic--></#if>>${orderItem.quantity?string.number}</td>
                <td <#if !orderItem_has_next><#-- Last Row Logic--></#if>><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                <td <#if !orderItem_has_next><#-- Last Row Logic--></#if>><@ofbizCurrency amount=localOrderReadHelper.getOrderItemTotal(orderItem) isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
            </tr>
            </#list>
            <#-- Footer Rows -->
            <tr>
                <td colspan="5"></td>
            </tr>
            <tr>
                <td colspan="3" style="text-align: right; margin-top:0px; margin-bottom:0px; padding-top:0px; padding-right: 10px; padding-bottom:0px; padding-left:0px;"><span style="font-weight: bold;">${uiLabelMap.CommonSubtotal}</span></td>
                <td><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                <td>&nbsp;</td>
            </tr>
            <#list headerAdjustmentsToShow as orderHeaderAdjustment>
            <tr>
                <td colspan="3" style="text-align: right; margin-top:0px; margin-bottom:0px; padding-top:0px; padding-right: 10px; padding-bottom:0px; padding-left:0px;"><span style="font-weight: bold;">${localOrderReadHelper.getAdjustmentType(orderHeaderAdjustment)}</span></td>
                <td><@ofbizCurrency amount=localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                <td>&nbsp;</td>
            </tr>
            </#list>
            <tr>
                <td colspan="3" style="text-align: right; margin-top:0px; margin-bottom:0px; padding-top:0px; padding-right: 10px; padding-bottom:0px; padding-left:0px;"><span style="font-weight: bold;">${uiLabelMap.ShippingAndHandlingLabel}</span></td>
                <td><@ofbizCurrency amount=orderShippingTotal isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="3" style="text-align: right; margin-top:0px; margin-bottom:0px; padding-top:0px; padding-right: 10px; padding-bottom:0px; padding-left:0px;"><span style="font-weight: bold;">${uiLabelMap.TaxLabel}</span></td>
                <td><@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td colspan="3" style="text-align: right; margin-top:0px; margin-bottom:0px; padding-top:0px; padding-right: 10px; padding-bottom:0px; padding-left:0px;"><span style="font-weight: bold;">${uiLabelMap.TotalLabel}</span></td>
                <td><span style="font-weight: bold;"><@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId rounding=globalContext.currencyRounding/></span></td>
                <td>&nbsp;</td>
            </tr>
        </table>
        </td>
    </tr>
</table>

