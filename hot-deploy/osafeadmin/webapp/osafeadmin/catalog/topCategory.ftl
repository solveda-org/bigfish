<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if productCategoryRollupAndChild?exists>
  <#assign productCategoryId = productCategoryRollupAndChild.productCategoryId?if_exists />
  <#assign currentPrimaryParentCategoryId = productCategoryRollupAndChild.parentProductCategoryId?if_exists />
</#if>
<div class="displayListBox commonHide" id="moveTopCategory">
    <div class="header"><h2><#if productCategoryRollupAndChild?exists>${uiLabelMap.AdminMoveLabel} "${productCategoryRollupAndChild.categoryName?if_exists}" ${uiLabelMap.ToCaption}</#if>
    </h2></div>
    <div class="boxBody">
        <table class="osafe">
        <tr class="heading">
            <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
            <th class="nameCol">${uiLabelMap.TopNavigationLabel}</th>
        </tr>
        <#if topLevelList?has_content>

            <#assign rowClass = "1">
            <#list resultList as category>
              
              <#assign primaryParentCategoryId = category.productCategoryId?if_exists />
                <tr class="<#if rowClass == "2">even</#if>">
                  <#if productCategoryRollupAndChild?exists && productCategoryRollupAndChild.parentProductCategoryId == category.productCategoryId?string>
                    <td class="seqCol">${category.sequenceNum?if_exists}</td>
                  <#else>
                    <td class="seqCol"><em><a href="javascript:moveCategory('${category.productCategoryId}','${category.categoryName}','primaryParentCategoryId','primaryParentCategoryName');" rel='example1'>${category.sequenceNum?if_exists}</a></em></td>
                  </#if>
                  <#if productCategoryRollupAndChild?exists && productCategoryRollupAndChild.parentProductCategoryId == category.productCategoryId?string>
                    <td class="nameCol">${category.categoryName?if_exists}</td>
                  <#else>
                    <td class="nameCol"><em>${category.categoryName?if_exists}</em></td>
                  </#if>
                    
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        <#else>
             ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
        </#if>
        </table>
    </div>
</div>
