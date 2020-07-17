<#if parameters.productCategoryId?has_content>
    <#assign productCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", parameters.productCategoryId?if_exists), false)?if_exists/>
    <#assign primaryProductCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategory.primaryParentCategoryId?if_exists), false)?if_exists/>
    <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
       <input type="hidden" name="productCategoryMemberId_${rowNo}" id="productCategoryMemberId" value="${parameters.productCategoryId!}"/>
       <td class="idCol firstCol">${primaryProductCategory.categoryName!""}</td>
       <td class="nameCol">${productCategory.categoryName!""}</td>
       <td class="actionCol">
           <#assign productCategoryName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productCategory.categoryName!}') />
           <#assign primaryProductCategoryName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${primaryProductCategory.categoryName!}') />
           <a href="javascript:setRowNo('');javascript:deleteCategoryMemberRow('${productCategoryName!""}', '${primaryProductCategoryName!""}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteCategoryMemberTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
           <a href="javascript:setRowNo('');javascript:openLookup(document.${detailFormName!}.productCategoryId,document.${detailFormName!}.productCategoryName,'lookupCategory','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertCategoryBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
           <a href="javascript:setRowNo('');javascript:openLookup(document.${detailFormName!}.productCategoryId,document.${detailFormName!}.productCategoryName,'lookupCategory','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertCategoryAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>	          
           <div class="actionIconMenu">
       </td>
    </tr>
</#if>