<table class="osafe" id="productCategoryMemberTable">
  <thead>
    <tr class="heading">
      <th class="idCol firstCol">${uiLabelMap.NavBarLabel}</th>
      <th class="nameCol">${uiLabelMap.SubItemLabel}</th>
      <th class="actionCol">${uiLabelMap.ActionsLabel}</th>
    </tr>
  </thead>
  <tbody>
    <#assign rowClass = "1"/>
    <#assign rowNo = 1/>
    <input type="hidden" name="productId" id="productId" value="${parameters.productId!product.productId!}"/>
    <input type="hidden" name="rowNo" id="rowNo"/>
    <#if resultList?exists && resultList?has_content>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!resultList?size}"/>
    <#else>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!}"/>
    </#if>
    <input type="hidden" name="productCategoryId" id="productCategoryId"/>
    <input type="hidden" name="productCategoryName" id="productCategoryName" onchange="addCategoryMemberRow('productCategoryMemberTable');"/>
    <#if resultList?exists && resultList?has_content && !parameters.totalRows?exists>
        <#list resultList as productCategoryAndMember>
            <#assign primaryProductCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategoryAndMember.primaryParentCategoryId?if_exists), false)?if_exists/>
            <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
               <input type="hidden" name="productCategoryMemberId_${rowNo}" id="productCategoryMemberId" value="${productCategoryAndMember.productCategoryId!}"/>
               <td class="idCol firstCol">${primaryProductCategory.categoryName!""}</td>
               <td class="nameCol">${productCategoryAndMember.categoryName!""}</td>
               <td class="actionCol">
                   <#assign productCategoryAndMemberCategoryName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productCategoryAndMember.categoryName!}') />
                   <#assign primaryProductCategoryName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${primaryProductCategory.categoryName!}') />
                   <a href="javascript:setRowNo('${rowNo}');javascript:deleteCategoryMemberRow('${productCategoryAndMemberCategoryName!""}', '${primaryProductCategoryName!""}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteCategoryMemberTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
                   <a href="javascript:setRowNo('${rowNo}');javascript:openLookup(document.${detailFormName!}.productCategoryId,document.${detailFormName!}.productCategoryName,'lookupCategory','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertCategoryBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
                   <a href="javascript:setRowNo('${rowNo+1}');javascript:openLookup(document.${detailFormName!}.productCategoryId,document.${detailFormName!}.productCategoryName,'lookupCategory','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertCategoryAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>               </td>
            </tr>
            <#if rowClass == "2">
              <#assign rowClass = "1">
            <#else>
              <#assign rowClass = "2">
            </#if>
            <#assign rowNo = rowNo+1/>
        </#list>
    <#elseif parameters.totalRows?exists>
        <#assign minRow = parameters.totalRows?number/>
        <#if (minRow?exists && minRow &gt; 0) >
        <#list 1..minRow as x>
            <#assign productCategoryMemberId = request.getParameter("productCategoryMemberId_${x}")!/>
            <#assign productCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategoryMemberId?if_exists), false)?if_exists/>
            <#assign primaryProductCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategory.primaryParentCategoryId?if_exists), false)?if_exists/>
            <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
               <input type="hidden" name="productCategoryMemberId_${x}" id="productCategoryMemberId" value="${productCategoryMemberId!}"/>
               <td class="idCol firstCol">${primaryProductCategory.categoryName!""}</td>
               <td class="nameCol">${productCategory.categoryName!""}</td>
               <td class="actionCol">
                   <#assign productCategoryName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${productCategory.categoryName!}') />
                   <#assign primaryProductCategoryName = Static["com.osafe.util.OsafeAdminUtil"].formatSimpleText('${primaryProductCategory.categoryName!}') />
                   <a href="javascript:setRowNo(${x});javascript:deleteCategoryMemberRow('${productCategoryName!""}', '${primaryProductCategoryName!""}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteCategoryMemberTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
                   <a href="javascript:setRowNo(${x});javascript:openLookup(document.${detailFormName!}.productCategoryId,document.${detailFormName!}.productCategoryName,'lookupCategory','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertCategoryBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
                   <a href="javascript:setRowNo(${x+1});javascript:openLookup(document.${detailFormName!}.productCategoryId,document.${detailFormName!}.productCategoryName,'lookupCategory','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertCategoryAfterNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>               </td>
            </tr>
            <#if rowClass == "2">
              <#assign rowClass = "1">
            <#else>
              <#assign rowClass = "2">
            </#if>
        </#list>
        </#if>
    </#if>

    <tr class="dataRow" id="addIconRow" <#if (resultList?exists && resultList?has_content && !parameters.totalRows?exists) || (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
      <td colspan="2">&nbsp;</td>
      <td class="actionCol">
        <span class="noAction"></span>
        <a href="javascript:setRowNo('1');javascript:openLookup(document.${detailFormName!}.productCategoryId,document.${detailFormName!}.productCategoryName,'lookupCategory','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertCategoryBeforeNewRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
        <span class="noAction"></span>
      </td>
    </tr>
  </tbody>
</table>