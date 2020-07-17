<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if productCategoryRollupAndChild?exists && productCategoryRollupAndChild?has_content>
    <input type="hidden" name="productCategoryId" value="${productCategoryRollupAndChild.productCategoryId?if_exists}" />
    <input type="hidden" name="productCategoryTypeId" value="CATALOG_CATEGORY" />
    <input type="hidden" name="primaryParentCategoryId" id="primaryParentCategoryId" value="${productCategoryRollupAndChild.parentProductCategoryId?if_exists}" />
    <input type="hidden" name="currentPrimaryParentCategoryId" id="currentPrimaryParentCategoryId" value="${productCategoryRollupAndChild.parentProductCategoryId?if_exists}" />
    <input type="hidden" name="currentFromDate" id="currentFromDate" value="${productCategoryRollupAndChild.fromDate?if_exists}" />
    <input type="hidden" name="activeFromDate" id="activeFromDate" value="${productCategoryRollupAndChild.fromDate?if_exists}" />
    
    <#if productCategoryRollupAndChild.fromDate?has_content>
        <#assign fromDate = (productCategoryRollupAndChild.fromDate)?string(entryDateTimeFormat)>
    </#if>
    <#if productCategoryRollupAndChild.thruDate?has_content>
        <#assign thruDate = (productCategoryRollupAndChild.thruDate)?string(entryDateTimeFormat)>
    </#if>
    <#assign productCategoryId = productCategoryRollupAndChild.productCategoryId!>
    <#assign parentProductCategoryId = productCategoryRollupAndChild.parentProductCategoryId!>
    <#assign sequenceNum = productCategoryRollupAndChild.sequenceNum!>
    <#assign longDescription = productCategoryRollupAndChild.longDescription!>
    <#assign description = productCategoryRollupAndChild.description!>
    <#assign categoryName = productCategoryRollupAndChild.categoryName!>
</#if>
<input type="hidden" name="formattedFromDate" id="formattedFromDate" value="${fromDate?if_exists}" />

<#if (mode?has_content && (mode == "addTopNav" || mode == "addSubNav"))>
    <div class="infoRow">
        <div class="infoEntry">
            <#if mode == "addTopNav">
                <div class="infoCaption"><label>${uiLabelMap.TopNavIdCaption}</label></div>
                <div class="infoValue">
                    <input type="text" name="topNavId" value="${parameters.topNavId!topNavId?if_exists}" />
                </div>
            <#else>
                <div class="infoCaption"><label>${uiLabelMap.TopNavCaption}</label></div>
                <div class="infoValue">
                    <input type="hidden" name="topNavBarId" id="topNavBarId" value="${parameters.topNavBarId!}"/>
                    <input type="hidden" name="topNavBar" id="topNavBar" value="${parameters.topNavBar!}" onChange="javascript:setTopNav();"/>
                    <input type="text" name="topNav" id="topNav" value="${parameters.topNavBar!}" readOnly="readOnly"/>
                    <a href="javascript:openLookup(document.${detailFormName!}.topNavBarId,document.${detailFormName!}.topNavBar,'lookupTopCategory','500','700','center','true');" class="standardBtn secondary">${uiLabelMap.SelectBtn}</a>
                </div>
            </#if>
        </div>
    </div>
<#elseif (mode?has_content && mode == "edit")>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption"><label>${uiLabelMap.CategoryIDCaption}</label></div>
            <div class="infoValue">
                ${productCategoryId!}
            </div>
        </div>
    </div>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption"><label>${uiLabelMap.TopNavCaption}</label></div>
            <div class="infoValue" id="primaryParentCategoryName">
                <#if parentProductCategoryId?exists>
                    <#assign productCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", parentProductCategoryId?if_exists), false)/>
                    ${productCategory.categoryName!parameters.primaryParentCategoryId?if_exists}
                </#if>
            </div>
            <#if topLevelList?has_content><#assign topCategoryList = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(topLevelList, "productCategoryId", true)></#if>
            <#if topCategoryList?has_content>
                <#if parentProductCategoryId?exists && parentProductCategoryId != catalogTopCategoryId>
                    <input type="button" class="standardBtn dateSelect" name="moveButton" id="moveButton" value="${uiLabelMap.MoveBtn}" onClick="displayDialogBox('#moveTopCategory');"/>
                </#if>
            </#if>
        </div>
    </div>
</#if>

<#if (mode?has_content && (mode == "addTopNav" || mode == "addSubNav"))>
    <div class="infoRow">
        <div class="infoEntry">
            <#if mode == "addTopNav">
                <div class="infoCaption"><label>${uiLabelMap.TopNavNameCaption}</label></div>
                <div class="infoValue">
                    <input type="hidden" name="catalogTopCategoryId" value="${parameters.catalogTopCategoryId!catalogTopCategoryId!""}"/>
                    <input type="text" name="topNavBar" value="${parameters.topNavBar!topNavBar?if_exists}" />
                </div>
            <#else>
                <div class="infoCaption"><label>${uiLabelMap.SubNavIdCaption}</label></div>
                <div class="infoValue">
                    <input type="text" name="subNavId" id="subNavId" value="${parameters.subNavId!subNavId!}"/>
                </div>
            </#if>
        </div>
    </div>
</#if>

<#if (mode?has_content && (mode == "addTopNav" || mode == "addSubNav"))>
    <div class="infoRow">
        <div class="infoEntry">
            <#if mode == "addTopNav">
                <div class="infoCaption"><label>${uiLabelMap.SubNavCaption}</label></div>
                <div class="infoValue">
                    <input type="text" readOnly="readOnly"/>
                </div>
                <div class="infoIcon">
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SubNavBarInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </div>
            <#else>
                <div class="infoCaption"><label>${uiLabelMap.SubNavNameCaption}</label></div>
                <div class="infoValue">
                    <input type="text" name="subNavBar" value="${parameters.subNavBar!subNavBar?if_exists}" />
                </div>
            </#if>
        </div>
    </div>
<#elseif (mode?has_content && mode == "edit")>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption"><label>${uiLabelMap.SubNavCaption}</label></div>
            <input type="text" name="categoryName" value="${parameters.categoryName!categoryName?if_exists}"/>
        </div>
    </div>
</#if>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption"><label>${uiLabelMap.DescriptionCaption}</label></div>
        <div class="infoValue">
            <textarea class="smallArea" name="description" cols="50" rows="1">${parameters.description!description!""}</textarea>
        </div>
        <div class="infoIcon">
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.CategoryDescriptionInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption"><label>${uiLabelMap.LongDescriptionCaption}</label></div>
        <div class="infoValue">
            <textarea class="smallArea" name="longDescription" cols="50" rows="1">${parameters.longDescription!longDescription!""}</textarea>
        </div>
        <div class="infoIcon">
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.CategoryLongDescriptionInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption"><label>${uiLabelMap.SeqNumCaption}</label></div>
        <div class="infoValue">
            <input type="text" name="sequenceNum" value="${parameters.sequenceNum!sequenceNum?if_exists}" maxlength="9"/>
        </div>
        <div class="infoIcon">
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.CategorySequenceNumInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption"><label>${uiLabelMap.ActiveFromCaption}</label></div>
        <div class="infoValue">
            <div class="entryInput from">
                <input class="dateEntry" type="text" id="fromDate" name="fromDate" maxlength="40" value="${parameters.fromDate!fromDate!""}"/>
            </div>
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption"><label>${uiLabelMap.ActiveThruCaption}</label></div>
        <div class="infoValue">
            <div class="entryInput from">
                <input class="dateEntry" type="text" id="thruDate" name="thruDate" maxlength="40" value="${parameters.thruDate!thruDate!""}"/>
            </div>
        </div>
    </div>
</div>