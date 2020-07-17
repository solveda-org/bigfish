
<#macro facetLine facet facetType refinementValueName refinementValue multiFacetRefinedExist multiFacetRefined multiFacetInitialType facetGroupParamList>

    <#assign facetShowItemCount = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"FACET_SHOW_ITEM_CNT")/>
    <#assign facetMultiSelect = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"FACET_MULTI_SELECT")/>
    <#assign filterGroupParamMap = parameters.filterGroupParamMap!requestAttributes.filterGroupParamMap! />
    <#assign categoryId = ""/>
    <#if currentProductCategory?exists>
        <#assign categoryId = currentProductCategory.productCategoryId!"">
    </#if>
    <#if !categoryId?has_content>
        <#assign categoryId = parameters.productCategoryId?if_exists />
    </#if>
    <#assign catOrSearchText = ""/>
    <#if parameters.searchText?has_content>
        <#assign catOrSearchText = "eCommerceProductList?searchText=" + parameters.searchText/>
    <#else>
        <#assign catOrSearchText = "siteSearch?productCategoryId=" + categoryId/>
    </#if>
    <#assign refinementValueDisplayName = refinementValue.displayName>
    <#assign refinementURL = refinementValue.refinementURL>
    <#assign productCategoryUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${refinementURL}')/>
    <span class="facetValue">
        <#if facetMultiSelect>
            <#assign removeUrl = catOrSearchText/>
            <#assign removeFilterGroupValue = ""/>
            <#if filterGroupParamMap?has_content>
                  <#list filterGroupParamMap.entrySet() as entry>
                      <#list entry.value as value>
                          <#if !(refinementValueName.equals(value))>
                              <#assign removeFilterGroupValue = removeFilterGroupValue+entry.key+":"+StringUtil.wrapString(value)+"|"/>
                          </#if>
                      </#list>
                  </#list>
                  <#if removeFilterGroupValue?has_content>
                      <#assign removeUrl = removeUrl+"&filterGroup=${removeFilterGroupValue}"/>
                  </#if>
            </#if>
            <#assign removeUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${removeUrl}')/>
    
            <#assign scalarCount = refinementValue.scalarCount/>
            <#assign useDisable = true/>
            <#if multiFacetInitialType?has_content && multiFacetInitialType.equalsIgnoreCase(facetType)>
                <#assign useDisable = false/>
            </#if>
            <#if useDisable>
                <#assign disabled = true/>
            <#else>
                <#assign disabled = false/>
            </#if>
            <#if multiFacetRefinedExist>
                <#if multiFacetRefined?has_content>
                    <#list multiFacetRefined as facetResultRefined>
                        <#if facetResultRefined.refinementValues?has_content>
                            <#list facetResultRefined.refinementValues as refinementValueRefined>
                                <#if refinementValueRefined.name == refinementValue.name && facet.type == facetResultRefined.type>
                                    <#assign disabled = false/>
                                    <#assign scalarCount = refinementValueRefined.scalarCount/>
                                </#if>
                            </#list>
                        </#if>
                    </#list>
                </#if>
            <#else>
                <#assign disabled = false/>
            </#if>
            <#if disabled>
                <#assign scalarCount = 0/>
            </#if>
    
            <input type="checkbox" name="facetValue_${refinementValueName}" id="facetValue_${refinementValueName}" value="Y" onclick="solrSearch(this, '${productCategoryUrl!}', '${removeUrl!}')" <#if facetGroupParamList.contains(refinementValueName)>checked</#if> <#if disabled>disabled='true'</#if>/>
            <span class="facetValueName <#if disabled>disabled</#if>">
                <#if facetType?exists && facetType == "CUSTOMER_RATING">
                    <img alt="${refinementValueDisplayName}" src="/osafe_theme/images/user_content/images/rating_facet_bar_${refinementValue.start}.gif" class="ratingFacetBar">
                </#if>
                ${refinementValueDisplayName} <#if facetShowItemCount>(${scalarCount!})</#if>
            </span>
        <#else>
            <#if facet.refinementValues?size == 1>
                <#if facetType?exists && facetType == "CUSTOMER_RATING">
                    <img alt="${refinementValueDisplayName}" src="/osafe_theme/images/user_content/images/rating_facet_bar_${refinementValue.start}.gif" class="ratingFacetBar">
                </#if>
                ${refinementValueDisplayName} <#if facetShowItemCount>(${refinementValue.scalarCount})</#if>
            <#else>
                <a class="facetValueLink" title="${refinementValueName}" href="${productCategoryUrl}">
                    <#if facetType?exists && facetType == "CUSTOMER_RATING">
                        <img alt="${refinementValueDisplayName}" src="/osafe_theme/images/user_content/images/rating_facet_bar_${refinementValue.start}.gif" class="ratingFacetBar">
                    </#if>
                    ${refinementValueDisplayName} <#if facetShowItemCount>(${refinementValue.scalarCount})</#if>
                </a>
            </#if>
        </#if>
    </span>
</#macro>

<#assign categoryId = ""/>
<#if currentProductCategory?exists>
    <#assign categoryId = currentProductCategory.productCategoryId!"">
</#if>
<#if !categoryId?has_content>
    <#assign categoryId = parameters.productCategoryId?if_exists />
</#if>
<#if (requestAttributes.facetList)?exists><#assign facetList = requestAttributes.facetList></#if>
<#if (requestAttributes.multiFacetInitialType)?exists><#assign multiFacetInitialType = requestAttributes.multiFacetInitialType></#if>
<#if (requestAttributes.multiFacetGroup)?exists><#assign multiFacetGroup = requestAttributes.multiFacetGroup></#if>
<#if (requestAttributes.multiFacetGroupRefined)?exists><#assign multiFacetGroupRefined = requestAttributes.multiFacetGroupRefined></#if>
<#if (requestAttributes.multiFacetPriceRange)?exists><#assign multiFacetPriceRange = requestAttributes.multiFacetPriceRange></#if>
<#if (requestAttributes.multiFacetPriceRangeRefined)?exists><#assign multiFacetPriceRangeRefined = requestAttributes.multiFacetPriceRangeRefined></#if>
<#if (requestAttributes.multiFacetCustomerRating)?exists><#assign multiFacetCustomerRating = requestAttributes.multiFacetCustomerRating></#if>
<#if (requestAttributes.multiFacetCustomerRatingRefined)?exists><#assign multiFacetCustomerRatingRefined = requestAttributes.multiFacetCustomerRatingRefined></#if>
<#if (requestAttributes.facetListPriceRange)?exists><#assign facetListPriceRange = requestAttributes.facetListPriceRange></#if>
<#if (requestAttributes.facetListCustomerRating)?exists><#assign facetListCustomerRating = requestAttributes.facetListCustomerRating></#if>
<#if (requestAttributes.facetCatList)?exists><#assign facetCatList = requestAttributes.facetCatList></#if>
<#assign facetTopProdCatContentTypeId = 'PLP_ESPOT_FACET_TOP'/>
<#if facetTopProdCatContentTypeId?exists && facetTopProdCatContentTypeId?has_content>
    <#assign facetTopProductCategoryContentList = delegator.findByAndCache("ProductCategoryContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , categoryId?string, "prodCatContentTypeId" , facetTopProdCatContentTypeId?if_exists)) />
  
    <#if facetTopProductCategoryContentList?has_content>
        <#assign facetTopProductCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(facetTopProductCategoryContentList?if_exists) />
        <#assign facetTopProdCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(facetTopProductCategoryContentList) />
        <#assign facetTopContentId = facetTopProdCategoryContent.contentId?if_exists />
    </#if>
    <#if facetTopContentId?exists >
        <#assign facetTopPlpEspotContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", facetTopContentId), true) />
    </#if>
</#if>

<#if facetTopPlpEspotContent?has_content>
    <#if ((facetTopPlpEspotContent.statusId)?if_exists == "CTNT_PUBLISHED")>
        <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
            <@renderContentAsText contentId="${facetTopContentId}" ignoreTemplate="true"/>
        </div>
    </#if>
</#if>
 
<h3 class="CategoryFacetTitle">${CategoryFacetTitle}</h3>
<#assign facetMinValue = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_VALUE_MIN")?if_exists />
<#assign facetMaxValue = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_VALUE_MAX")?if_exists />
<#if facetMaxValue?has_content>
    <#assign facetMaxValue = facetMaxValue?number />
</#if>
<#assign facetShowItemCount = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"FACET_SHOW_ITEM_CNT")/>
<#assign facetMultiSelect = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"FACET_MULTI_SELECT")/>

<input type="hidden" name="facetShowItemCnt" id="facetShowItemCnt" value="${FACET_SHOW_ITEM_CNT!}" />
<ul>
    <#if facetCatList?has_content>
        <#if facetMinValue?has_content>
            <#assign facetMinValue = facetMinValue?number />
        <#else>
            <#assign facetMinValue = facetShowValueCnt!0 />
        </#if>
        <#if !facetMaxValue?has_content>
            <#assign facetMaxValue = '99'?number />
        </#if>
        <#if parameters._CURRENT_VIEW_ == 'eCommerceCategoryList' || Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"FACET_CAT_ON_PLP")>
            <#list facetCatList as facet>
                <li>
                    <h3 class="facetGroup <#if facetMinValue == 0>js_showHideFacetGroupLink js_seeMoreFacetGroupLink</#if>">${facet.name}</h3>
                    <#if facet.refinementValues?has_content>
                        <#assign indx=0/>
                            <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
                                <#assign valueSize = facet.refinementValues.size()/>
                                <#-- This is the amount of items that will be displayed next to More Link -->
                                <#assign showAllCount=0/>
                                <#if (valueSize > facetMaxValue)>
                                    <#assign showAllCount=(valueSize - facetMaxValue)/>
                                </#if>
                                <#list facet.refinementValues as refinementValue>
                                    <#assign indx = indx + 1/>
                                    <#assign hideClass="showThem"/>
                                    <#-- items exposed when Show More Link is clicked -->
                                    <#if (indx > facetMinValue)>
                                        <#assign hideClass="js_hideThem"/>
                                    </#if>
                                    <#-- items exposed when Show All Link is clicked -->
                                    <#if (indx > (facetMaxValue))>
                                        <#assign hideClass="js_showAllOfThem"/>
                                    </#if>
                                    <li class="js_facetValue ${hideClass}">
                                        <#assign refinementValueName = refinementValue.displayName>
                                        <#assign code = refinementValue.name>
                                        <#assign refinementURL = refinementValue.refinementURL>
                                        <#assign productCategoryUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${refinementURL}')/>
                                        <a class="facetValueLink<#if code?has_content && parameters.productCategoryId?has_content && code == parameters.productCategoryId> selected</#if>" title="${refinementValueName}" href="${productCategoryUrl}">${refinementValueName} <#if facetShowItemCount>(${refinementValue.scalarCount})</#if></a>
                                    </li>
                                    <#if valueSize! lt facetMaxValue>
                                        <#assign facetMaxValue = valueSize>
                                    </#if>
                                    <#if (indx > facetMinValue) && indx == valueSize>
                                        <li class="js_facetValue" id="facet_${facet.name!}">
                                            <#assign remaining = (facetMaxValue?number - facetMinValue?number) + showAllCount />
                                            <input type="hidden" id="less_${facet.name}" value="${facetMinValue!}" />
                                            <input type="hidden" id="remaining_${facet.name!}" value="${remaining!}" />
                                            <a class="js_seeMoreLink" href="javascript:void(0);" <#if facetMinValue == 0>style="display:none;"</#if> >${uiLabelMap.FacetSeeMoreLinkCaption}<#if facetShowItemCount> (${remaining!})</#if></a>
                                            <a class="js_seeLessLink" href="javascript:void(0);" <#if facetMinValue == 0>style="display:none;"</#if> >${uiLabelMap.FacetSeeLessLinkCaption}</a>
                                            <#if showAllCount &gt; 0>
                                                <a class="js_showAllLink" href="javascript:void(0);">${uiLabelMap.FacetShowAllCaption} (${showAllCount})</a>
                                            </#if>
                                        </li>
                                    </#if>
                                </#list>
                            </ul>
                        </#if>
                    </li>
                </#list>
            </#if>
        </#if>
  
        <#assign includedSearchFacetGroup = Static["javolution.util.FastList"].newInstance()!""/>
        <#assign SEARCH_FACET_GROUP_INCLUDE = Static["com.osafe.util.Util"].getProductStoreParm(request,"SEARCH_FACET_GROUP_INCLUDE")!"">
        <#if SEARCH_FACET_GROUP_INCLUDE?has_content>
            <#assign includedSearchFacetGroupList = SEARCH_FACET_GROUP_INCLUDE?split(",") />
        </#if>
        <#if includedSearchFacetGroupList?has_content>
            <#list includedSearchFacetGroupList as searchFacetGroup>
                <#assign newSearchFacetGroup = includedSearchFacetGroup.add(searchFacetGroup?trim?upper_case)/>
            </#list>
        </#if>

        <#assign filterGroupParms = StringUtil.wrapString(parameters.filterGroup!requestAttributes.filterGroup!) />
        <#assign filterGroupParamMap = parameters.filterGroupParamMap!requestAttributes.filterGroupParamMap! />

        <#if facetMultiSelect>
            <#assign loopFacetListPriceRange = multiFacetPriceRange!/>
        <#else>
            <#assign loopFacetListPriceRange = facetListPriceRange!/>
        </#if>

        <#if loopFacetListPriceRange?has_content>
            <#list loopFacetListPriceRange as facet>

                <#assign facetGroupParamList = Static["javolution.util.FastList"].newInstance()/>
                <#if filterGroupParamMap?has_content && filterGroupParamMap.get("PRICE")?has_content>
                    <#assign facetGroupParamList = Static["org.ofbiz.base.util.UtilGenerics"].checkList(filterGroupParamMap.get("PRICE"))/>
                </#if>

                <li>
                    <h3 class="facetGroup">${facet.name}</h3>
                    <#if facet.refinementValues?has_content>
                        <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
                            <#list facet.refinementValues as refinementValue>
                                <li class="js_facetValue">
                                    <#assign multiFacetRefinedExist = false/>
                                    <#if multiFacetPriceRangeRefined?exists>
                                        <#assign multiFacetRefinedExist = true/>
                                    </#if>
                                    <#assign refinementValueName = refinementValue.name>
                                    <#assign refinementValueName = refinementValueName.replaceAll("price:", "")>
                                    <@facetLine facet=facet facetType="PRICE" refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetPriceRangeRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList/>
                                </li>
                            </#list>
                        </ul>
                    </#if>
                </li>
            </#list>
        </#if>

        <#if facetMultiSelect>
            <#assign loopFacetListCustomerRating = multiFacetCustomerRating!/>
        <#else>
            <#assign loopFacetListCustomerRating = facetListCustomerRating!/>
        </#if>
  
        <#if loopFacetListCustomerRating?has_content>
            <#list loopFacetListCustomerRating as facet>

                <#assign facetGroupParamList = Static["javolution.util.FastList"].newInstance()/>
                <#if filterGroupParamMap?has_content && filterGroupParamMap.get("CUSTOMER_RATING")?has_content>
                    <#assign facetGroupParamList = Static["org.ofbiz.base.util.UtilGenerics"].checkList(filterGroupParamMap.get("CUSTOMER_RATING"))/>
                </#if>

                <li>
                    <h3 class="facetGroup">${facet.name}</h3>
                    <#if facet.refinementValues?has_content>
                        <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
                            <#list facet.refinementValues as refinementValue>
                                <li class="js_facetValue">
                                    <#assign multiFacetRefinedExist = false/>
                                    <#if multiFacetCustomerRatingRefined?exists>
                                        <#assign multiFacetRefinedExist = true/>
                                    </#if>
                                    <#assign refinementValueName = refinementValue.name>
                                    <#assign refinementValueName = refinementValueName.replaceAll("customerRating:", "")>
                                    <@facetLine facet=facet facetType="CUSTOMER_RATING" refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetCustomerRatingRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList/>
                                </li>
                            </#list>
                        </ul>
                    </#if>
                </li>
            </#list>
        </#if>

        <#if facetMultiSelect>
            <#assign loopFacetList = multiFacetGroup!/>
        <#else>
            <#assign loopFacetList = facetList!/>
        </#if>

        <#if loopFacetList?has_content>
            <#list loopFacetList as facetResult>
                <#assign facet = facetResult /> 

                <#assign facetGroupParamList = Static["javolution.util.FastList"].newInstance()/>
                <#if filterGroupParamMap?has_content && filterGroupParamMap.get(facet.type)?has_content>
                    <#assign facetGroupParamList = Static["org.ofbiz.base.util.UtilGenerics"].checkList(filterGroupParamMap.get(facet.type))/>
                </#if>

                <#assign facetMinValue = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_VALUE_MIN")?if_exists/>
                <#assign facetMaxValue = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_VALUE_MAX")?if_exists/>
                <#if facetMinValue?has_content>
                    <#assign facetMinValue = facetMinValue?number />
                </#if>
                <#if facetMaxValue?has_content>
                    <#assign facetMaxValue = facetMaxValue?number />
                </#if>
                
                <#if parameters.searchText?has_content && !parameters.productCategoryId?has_content>
                    <#if facetMultiSelect>
                        <#assign valueSize = facet.refinementValues.size()/>
                    <#else>
                        <#assign valueSize = facet.refinementValues.size() - facetGroupParamList.size()/>
                    </#if>
                    <#-- This is the amount of items that will be displayed next to More Link -->
                    <#assign showAllCount=0/>
                    <#if (valueSize > facetMaxValue)>
                        <#assign showAllCount=(valueSize - facetMaxValue)/>
                    </#if>
              
                    <#if includedSearchFacetGroup?has_content && includedSearchFacetGroup.contains(facet.productFeatureGroupId?upper_case!) && valueSize &gt; 0>
                        <li>
                            <h3 class="facetGroup <#if facetMinValue == 0>js_showHideFacetGroupLink<#if !facetGroupParamList?has_content> js_seeMoreFacetGroupLink<#else> js_seeLessFacetGroupLink</#if></#if>">${facet.name}</h3>
                            <#if facet.refinementValues?has_content>
                                <#assign indx=0/>
                                <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">

                                    <#list facet.refinementValues as refinementValue>
                                        <#assign refinementGroupValue = facet.type+":"+refinementValue.name />
                                        <#if !facetGroupParamList.contains(refinementValue.name) || facetMultiSelect>
                                            <#assign indx = indx + 1/>
                                            <#assign hideClass="showThem"/>
                                            <#-- items exposed when Show More Link is clicked -->
                                            <#if (indx > facetMinValue)>
                                                <#assign hideClass="js_hideThem"/>
                                            </#if>
                                            <#if facetMinValue == 0 && facetGroupParamList?has_content>
                                                <#assign hideClass="showThem"/>
                                            </#if>
                                            <#-- items exposed when Show All Link is clicked -->
                                            <#if (indx > (facetMaxValue))>
                                                <#assign hideClass="js_showAllOfThem"/>
                                            </#if>
                                            <li class="js_facetValue ${hideClass}">
                                                <#assign multiFacetRefinedExist = false/>
                                                <#if multiFacetGroupRefined?exists>
                                                    <#assign multiFacetRefinedExist = true/>
                                                </#if>
                                                <#assign refinementValueName = refinementValue.name>
                                                <@facetLine facet=facet facetType=facet.type refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetGroupRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList/>
                                            </li>
                                            <#if valueSize! lt facetMaxValue>
                                                <#assign facetMaxValue = valueSize>
                                            </#if>
                                            <#if (indx > facetMinValue) && indx == valueSize>
                                                <li class="js_facetValue" id="facet_${facet.productFeatureGroupId!}">
                                                    <#assign remaining = (facetMaxValue?number - facetMinValue?number) + showAllCount />
                                                    <a class="js_seeMoreLink" href="javascript:void(0);" <#if facetMinValue == 0>style="display:none;"</#if>>${uiLabelMap.FacetSeeMoreLinkCaption}<#if facetShowItemCount> (${remaining!})</#if></a>
                                                    <a class="js_seeLessLink" href="javascript:void(0);" <#if facetMinValue == 0>style="display:none;"</#if>>${uiLabelMap.FacetSeeLessLinkCaption}</a>
                                                    <#if showAllCount &gt; 0>
                                                        <a class="js_showAllLink" href="javascript:void(0);">${uiLabelMap.FacetShowAllCaption} (${showAllCount})</a>
                                                    </#if>
                                                </li>
                                            </#if>
                                        </#if>
                                    </#list>
                                </ul>
                            </#if>
                        </li>
                    </#if>
                </#if>
      
                <#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum")/>
                <#assign productFeatureCatGrpApplList=""/> 
                <#if parameters.productCategoryId?has_content>
                    <#assign productFeatureCatGrpApplList = delegator.findByAndCache("ProductFeatureCatGrpAppl", {"productCategoryId" : parameters.productCategoryId, "productFeatureGroupId" : facet.productFeatureGroupId!})>
                    <#assign productFeatureCatGrpAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureCatGrpApplList!)/>
                </#if>
                <#if productFeatureCatGrpAppls?has_content>
                    <#assign productFeatureCatGrpAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureCatGrpAppls) />
                    <#assign facetMinValue = productFeatureCatGrpAppl.facetValueMin!/>
                    <#assign facetMaxValue = (productFeatureCatGrpAppl.facetValueMax)!/>
        
                    <#if !facetMinValue?has_content>
                        <#assign facetMinValue = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_VALUE_MIN")!0 />
                    </#if>
                    <#if !facetMaxValue?has_content>
                        <#assign facetMaxValue = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_VALUE_MAX")!99 />
                    </#if>
                    <#if facetMinValue?has_content>
                        <#assign facetMinValue = facetMinValue?number />
                    </#if>
                    <#if facetMaxValue?has_content>
                        <#assign facetMaxValue = facetMaxValue?number/>
                    </#if>
                    <#if facetMultiSelect>
                        <#assign valueSize = facet.refinementValues.size()/>
                    <#else>
                        <#assign valueSize = facet.refinementValues.size() - facetGroupParamList.size()/>
                    </#if>
        
                    <#-- This is the amount of items that will be displayed next to More Link -->
                    <#assign showAllCount=0/>
                    <#if (valueSize > facetMaxValue)>
                        <#assign showAllCount=(valueSize - facetMaxValue)/>
                    </#if>
               
                    <#if ((productFeatureCatGrpAppl.getTimestamp("fromDate"))?exists && (!nowTimestamp.before(productFeatureCatGrpAppl.getTimestamp("fromDate")))) && (!productFeatureCatGrpAppl.getTimestamp("thruDate")?has_content || (!nowTimestamp.after(productFeatureCatGrpAppl.getTimestamp("thruDate")!))) && valueSize &gt; 0>
                        <li>
                            <h3 class="facetGroup <#if facetMinValue == 0>js_showHideFacetGroupLink<#if !facetGroupParamList?has_content> js_seeMoreFacetGroupLink<#else> js_seeLessFacetGroupLink</#if></#if>">${facet.name}
                                <#if productFeatureCatGrpAppl.facetTooltip?has_content>
                                    <#assign facetTooltipTxt = Static["com.osafe.util.Util"].formatToolTipText("${productFeatureCatGrpAppl.facetTooltip}", "${productFeatureCatGrpAppl.facetTooltip?length}")/>
                                    <#if facetTooltipTxt?has_content >
                                      <a href="javascript:void(0);" onMouseover="javascript:showTooltip('${StringUtil.wrapString(facetTooltipTxt)!""}', this);" onMouseout="hideTooltip()" class="toolTipLink">
                                        <span class="tooltipIcon"></span>
                                      </a>
                                    </#if>
                                </#if>
                            </h3>
                            <#if facet.refinementValues?has_content>
                                <#assign indx=0/>
                                <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
                
                                    <#list facet.refinementValues as refinementValue>
                                        <#assign refinementGroupValue = facet.type+":"+refinementValue.name />
                                        <#if !facetGroupParamList.contains(refinementValue.name) || facetMultiSelect>
                                            <#assign indx = indx + 1/>
                                            <#assign hideClass="showThem"/>
                                            <#-- items exposed when Show More Link is clicked -->
                                            <#if (indx > facetMinValue)>
                                                <#assign hideClass="js_hideThem"/>
                                            </#if>
                                            <#if facetMinValue == 0 && facetGroupParamList?has_content>
                                                <#assign hideClass="showThem"/>
                                            </#if>
                                            <#-- items exposed when Show All Link is clicked -->
                                            <#if (indx > (facetMaxValue))>
                                                <#assign hideClass="js_showAllOfThem"/>
                                            </#if>
                                            <li class="js_facetValue ${hideClass}">
                                                <#assign multiFacetRefinedExist = false/>
                                                <#if multiFacetGroupRefined?exists>
                                                    <#assign multiFacetRefinedExist = true/>
                                                </#if>
                                                <#assign refinementValueName = refinementValue.name>
                                                <@facetLine facet=facet facetType=facet.type refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetGroupRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList/>
                                            </li>
                                            <#if valueSize! lt facetMaxValue>
                                                <#assign facetMaxValue = valueSize>
                                            </#if>
                                            <#if (indx > facetMinValue) && indx == valueSize>
                                                <li class="js_facetValue" id="facet_${facet.productFeatureGroupId}">
                                                    <#assign remaining = (facetMaxValue?number - facetMinValue?number) + showAllCount />
                                                    <a class="js_seeMoreLink" href="javascript:void(0);" <#if facetMinValue == 0>style="display:none;"</#if>>${uiLabelMap.FacetSeeMoreLinkCaption}<#if facetShowItemCount> (${remaining!})</#if></a>
                                                    <a class="js_seeLessLink" href="javascript:void(0);" <#if facetMinValue == 0>style="display:none;"</#if>>${uiLabelMap.FacetSeeLessLinkCaption}</a>
                                                    <#if showAllCount &gt; 0>
                                                        <a class="js_showAllLink" href="javascript:void(0);">${uiLabelMap.FacetShowAllCaption} (${showAllCount})</a>
                                                    </#if>
                                                </li>
                                            </#if>
                                        </#if>
                                    </#list>
                                </ul>
                            </#if>
                        </li>
                    </#if>
                </#if>
            </#list>
        </#if>

    </ul>
    <#assign facetEndProdCatContentTypeId = 'PLP_ESPOT_FACET_END'/>
    <#if facetEndProdCatContentTypeId?exists && facetEndProdCatContentTypeId?has_content>
        <#assign facetEndProductCategoryContentList = delegator.findByAndCache("ProductCategoryContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , categoryId?string, "prodCatContentTypeId" , facetEndProdCatContentTypeId?if_exists)) />
        <#if facetEndProductCategoryContentList?has_content>
            <#assign facetEndProductCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(facetEndProductCategoryContentList?if_exists) />
            <#assign facetEndProdCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(facetEndProductCategoryContentList) />
            <#assign facetEndContentId = facetEndProdCategoryContent.contentId?if_exists />
        </#if>
        <#if facetEndContentId?exists >
            <#assign facetEndPlpEspotContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", facetEndContentId), true) />
        </#if>
    </#if>

    <#if facetEndPlpEspotContent?has_content>
        <#if ((facetEndPlpEspotContent.statusId)?if_exists == "CTNT_PUBLISHED")>
            <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
                <@renderContentAsText contentId="${facetEndContentId}" ignoreTemplate="true"/>
            </div>
        </#if>
    </#if>