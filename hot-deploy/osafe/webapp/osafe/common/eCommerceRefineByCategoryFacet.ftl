<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>

<#assign FACET_VALUE_STYLE = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_VALUE_STYLE")!"NORMAL"/>
<#if !FACET_VALUE_STYLE?has_content>
	<#assign FACET_VALUE_STYLE = "NORMAL"/>
<#else>
	<#assign FACET_VALUE_STYLE = FACET_VALUE_STYLE?string?upper_case/>
</#if>

<#assign categoryId = ""/>
<#if currentProductCategory?exists>
    <#assign categoryId = currentProductCategory.productCategoryId!"">
</#if>
<#if !categoryId?has_content>
    <#assign categoryId = parameters.productCategoryId?if_exists />
</#if>
<#if (requestAttributes.multiFacetInitialType)?exists><#assign multiFacetInitialType = requestAttributes.multiFacetInitialType></#if>
<#if (requestAttributes.multiFacetGroupRefined)?exists><#assign multiFacetGroupRefined = requestAttributes.multiFacetGroupRefined></#if>
<#if (requestAttributes.multiFacetPriceRangeRefined)?exists><#assign multiFacetPriceRangeRefined = requestAttributes.multiFacetPriceRangeRefined></#if>
<#if (requestAttributes.multiFacetCustomerRatingRefined)?exists><#assign multiFacetCustomerRatingRefined = requestAttributes.multiFacetCustomerRatingRefined></#if>
<#if (requestAttributes.multiFacetListAll)?exists><#assign multiFacetListAll = requestAttributes.multiFacetListAll></#if>
<#if (requestAttributes.facetListAll)?exists><#assign facetListAll = requestAttributes.facetListAll></#if>

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

<input type="hidden" name="facetShowItemCnt" id="facetShowItemCnt" value="${FACET_SHOW_ITEM_CNT!}" />
<ul>

    <#assign filterGroupParms = StringUtil.wrapString(parameters.filterGroup!requestAttributes.filterGroup!) />
    <#assign filterGroupParamMap = parameters.filterGroupParamMap!requestAttributes.filterGroupParamMap! />

    <#assign facetShowItemCount = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"FACET_SHOW_ITEM_CNT")/>
	
	<#assign facetMultiSelect = false/>
	<#if FACET_VALUE_STYLE == "CHECKBOX" || FACET_VALUE_STYLE == "DROPDOWN">
		<#assign facetMultiSelect = true/>
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

    <#if facetMultiSelect>
        <#assign loopFacetList = multiFacetListAll!/>
    <#else>
        <#assign loopFacetList = facetListAll!/>
    </#if>

    <#if loopFacetList?has_content>
        <#list loopFacetList as facetResult>
            <#assign facet = facetResult />

            <#assign productFeatureCatGrpApplList=""/> 
            <#assign facetMinValue = ""/>
            <#assign facetMaxValue = ""/>

            <#if parameters.productCategoryId?has_content>
                <#assign productFeatureCatGrpApplList = delegator.findByAndCache("ProductFeatureCatGrpAppl", {"productCategoryId" : parameters.productCategoryId, "productFeatureGroupId" : facet.productFeatureGroupId!})>
                <#assign productFeatureCatGrpAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureCatGrpApplList!)/>
            </#if>
            <#if productFeatureCatGrpAppls?has_content>
                <#assign productFeatureCatGrpAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureCatGrpAppls) />
                <#assign facetMinValue = productFeatureCatGrpAppl.facetValueMin!/>
                <#assign facetMaxValue = (productFeatureCatGrpAppl.facetValueMax)!/>
             </#if>

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

            <#if facet.type?has_content && facet.type == 'productCategoryId'>
                <#if parameters._CURRENT_VIEW_ == 'eCommerceCategoryList' || Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"FACET_CAT_ON_PLP")>
                    <li class="facetGroup facetGroup_${facet.type}">
                    	<h3 class="facetGroup <#if facetMinValue == 0>js_showHideFacetGroupLink js_seeMoreFacetGroupLink</#if>">${facet.name}</h3>
                    	<#if facet.refinementValues?has_content>
                    		<#if FACET_VALUE_STYLE == "DROPDOWN">
                    			<select name="${facet.name?lower_case?replace(" ","_")}" id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup facetGroup_${facet.type}" onchange="solrSearch(this, '', '', 'select')">
                    				<#list facet.refinementValues as refinementValue>
                    					<#assign refinementValueName = refinementValue.displayName>
	                                    <#assign code = refinementValue.name>
	                                    <#assign refinementURL = refinementValue.refinementURL>
	                                    <#assign productCategoryUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${refinementURL}')/>
	                                    <option value='${productCategoryUrl!}'<#if code?has_content && parameters.productCategoryId?has_content && code == parameters.productCategoryId>selected=selected</#if>>${refinementValueName?html} <#if facetShowItemCount>(${refinementValue.scalarCount})</#if></option>
                    				</#list>
                    			</select>
                    		<#else>
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
	                                    <#assign refinementValueName = refinementValue.displayName>
	                                    <#assign code = refinementValue.name>
	                                    <#assign refinementURL = refinementValue.refinementURL>
	                                    <#assign productCategoryUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${refinementURL}')/>
		                                <li class="js_facetValue ${refinementValueName?html} ${hideClass}" <#if hideClass == "js_hideThem">style="display:none;"</#if>>
		                                    <a class="facetValueLink<#if code?has_content && parameters.productCategoryId?has_content && code == parameters.productCategoryId> selected</#if>" title="${refinementValueName?html}" href="${productCategoryUrl}">${refinementValueName?html} <#if facetShowItemCount>(${refinementValue.scalarCount})</#if></a>
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
		                                        <a class="js_seeLessLink" href="javascript:void(0);" style="display:none;">${uiLabelMap.FacetSeeLessLinkCaption}</a>
		                                        <#if showAllCount &gt; 0>
		                                            <a class="js_showAllLink" href="javascript:void(0);">${uiLabelMap.FacetShowAllCaption} (${showAllCount})</a>
		                                        </#if>
		                                    </li>
		                                </#if>
		                            </#list>
		                        </ul>
	                        </#if>
                        </#if>
                    </li>
                </#if>

            <#elseif facet.type?has_content && facet.type == 'price'>
                <#assign facetGroupParamList = Static["javolution.util.FastList"].newInstance()/>
                <#if filterGroupParamMap?has_content && filterGroupParamMap.get("PRICE")?has_content>
                    <#assign facetGroupParamList = Static["org.ofbiz.base.util.UtilGenerics"].checkList(filterGroupParamMap.get("PRICE"))/>
                </#if>

                <li class="facetGroup facetGroup_${facet.type}">
                    <h3 class="facetGroup">${facet.name}</h3>
                    <#if facet.refinementValues?has_content>
                    	<#assign facetTypeValue = "PRICE"/>
                    	<#if FACET_VALUE_STYLE == "DROPDOWN">
                    		<select name="facetSelector_${facet.type}" id="js_facetSelector_${facet.type}" class="facetGroup facetGroup_${facet.type} js_facetPriceSelector" onchange="solrSearch(this, '', '', 'select')">
	                    		
	                    		<#assign selectedPriceValue = ""/>
	                    		<#if facetGroups?has_content>
							        <#assign facetSize = facetGroups.size()/>
							        <#list facetGroups as facet>
									  <#assign facetPart1 = facet.facet/>
									  <#assign facetPart2 = facet.facetValue/>
									  <#assign facetPart2Desc = facetPart2!""/>
									  <#if facetPart1?lower_case?contains("price")>
									        <#assign facetPart2Desc = StringUtil.wrapString(facetPart2)/>
									        <#assign facetPart2Desc = facetPart2Desc?replace("[", "")/>
									        <#assign facetPart2Desc = facetPart2Desc?replace("]", "")/>
									        <#assign facetPart2Desc = facetPart2Desc?replace("+", " ")/>
									        <#assign facetPart2Prices = facetPart2Desc?split(" ")/>
									
									        <#-- Using special ftl syntax so we can call the ofbizCurrency macro -->
									        <#assign start><@ofbizCurrency amount=facetPart2Prices[0]?number isoCode=CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() rounding=0/></#assign>
									        <#assign end><@ofbizCurrency amount=facetPart2Prices[1]?number isoCode=CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() rounding=0/></#assign>
									
									        <#assign facetPart2Desc = start + " to " + end/>
									        <option class="js_selectedOptionDD" value=''>${facetPart2Desc}</option>
									        
									        <#assign selectedPriceValue = facetPart2Desc/>
									  </#if>
							        </#list>
							    </#if>

	                    		<#assign multiFacetRefinedExist = false/>
	                            <#if multiFacetPriceRangeRefined?exists>
	                                <#assign multiFacetRefinedExist = true/>
	                            </#if>
	                            
	                            <#assign numberOfOptionsToDisplay = 0/>
	                    		<#-- Determine if only one option is left to select.  If there is only one then we will not show this option in the drop down.  -->
	                    		<#list facet.refinementValues as refinementValue>
	                    			<#-- Determine count -->
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
								        <#if multiFacetPriceRangeRefined?has_content>
								            <#list multiFacetPriceRangeRefined as facetResultRefined>
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
								    <#if scalarCount?number &gt; 0>
								    	<#assign numberOfOptionsToDisplay = numberOfOptionsToDisplay + 1/>
								    </#if>
	                    		</#list>
	                    		
	                    		<#-- if we have one price range left after selecting one of the facet values then select the price range -->
	                    		<#if !selectedPriceValue?has_content && numberOfOptionsToDisplay?number == 1>
	                    			<#list facet.refinementValues as refinementValue>
			                            <#assign refinementValueName = refinementValue.name>
			                            <#assign refinementValueName = refinementValueName.replaceAll("price:", "")>
			                    		<@facetLine facet=facet facetType=facetTypeValue refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetPriceRangeRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N" showItemCount="N"/>
						        	</#list>
	                    		</#if>
	                    		
	                    		<#-- display all option -->
	                    		<@facetLine facet=facet facetType=facetTypeValue refinementValueName="" refinementValue="" multiFacetRefinedExist="" multiFacetRefined="" multiFacetInitialType="" facetGroupParamList="" allSelected="Y" showItemCount=""/>
	                    		
	                    		<#if numberOfOptionsToDisplay?number &gt; 1>
		                    		<#list facet.refinementValues as refinementValue>
			                            <#assign refinementValueName = refinementValue.name>
			                            <#assign refinementValueName = refinementValueName.replaceAll("price:", "")>
			                    		<@facetLine facet=facet facetType=facetTypeValue refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetPriceRangeRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N"  showItemCount=""/>
						        	</#list>
					        	</#if>
				        	</select>
                    	<#else>
	                        <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
	                            <#list facet.refinementValues as refinementValue>
	                            	<#assign multiFacetRefinedExist = false/>
	                                <#if multiFacetPriceRangeRefined?exists>
	                                    <#assign multiFacetRefinedExist = true/>
	                                </#if>
	                                <#assign refinementValueName = refinementValue.name>
	                                <#assign refinementValueName = refinementValueName.replaceAll("price:", "")>
	                                <li class="js_facetValue ${refinementValueName?html}">
	                                    <@facetLine facet=facet facetType=facetTypeValue refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetPriceRangeRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N" showItemCount=""/>
	                                </li>
	                            </#list>
	                        </ul>
                        </#if>
                    </#if>
                </li>

            <#elseif facet.type?has_content && facet.type == 'customerRating'>
                <#assign facetGroupParamList = Static["javolution.util.FastList"].newInstance()/>
                <#if filterGroupParamMap?has_content && filterGroupParamMap.get("CUSTOMER_RATING")?has_content>
                    <#assign facetGroupParamList = Static["org.ofbiz.base.util.UtilGenerics"].checkList(filterGroupParamMap.get("CUSTOMER_RATING"))/>
                </#if>

                <li class="facetGroup facetGroup_${facet.type}">
                    <h3 class="facetGroup">${facet.name}</h3>
                    <#if facet.refinementValues?has_content>
                    	<#assign facetTypeValue = "CUSTOMER_RATING"/>
                    	<#if FACET_VALUE_STYLE == "DROPDOWN">
                    		<select name="facetSelector_${facet.type}" id="js_facetSelector_${facet.type}" class="facetGroup facetGroup_${facet.type}" onchange="solrSearch(this, '', '', 'select')">
	                    		<@facetLine facet=facet facetType=facetTypeValue refinementValueName="" refinementValue="" multiFacetRefinedExist="" multiFacetRefined="" multiFacetInitialType="" facetGroupParamList="" allSelected="Y" showItemCount=""/>
	                    		<#list facet.refinementValues as refinementValue>
	                            	<#assign multiFacetRefinedExist = false/>
	                                <#if multiFacetCustomerRatingRefined?exists>
	                                    <#assign multiFacetRefinedExist = true/>
	                                </#if>
	                                <#assign refinementValueName = refinementValue.name>
	                                <#assign refinementValueName = refinementValueName.replaceAll("customerRating:", "")>
		                    		<@facetLine facet=facet facetType=facetTypeValue refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetCustomerRatingRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N" showItemCount=""/>
					        	</#list>
				        	</select>
                    	<#else>
	                        <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
	                            <#list facet.refinementValues as refinementValue>
	                            	<#assign multiFacetRefinedExist = false/>
	                                <#if multiFacetCustomerRatingRefined?exists>
	                                    <#assign multiFacetRefinedExist = true/>
	                                </#if>
	                                <#assign refinementValueName = refinementValue.name>
	                                <#assign refinementValueName = refinementValueName.replaceAll("customerRating:", "")>
	                                <li class="js_facetValue ${refinementValueName?html}">
	                                    <@facetLine facet=facet facetType=facetTypeValue refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetCustomerRatingRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N" showItemCount=""/>
	                                </li>
	                            </#list>
	                        </ul>
                        </#if>
                    </#if>
                </li>

            <#else>
                <#assign facetGroupParamList = Static["javolution.util.FastList"].newInstance()/>
                <#if filterGroupParamMap?has_content && filterGroupParamMap.get(facet.type)?has_content>
                    <#assign facetGroupParamList = Static["org.ofbiz.base.util.UtilGenerics"].checkList(filterGroupParamMap.get(facet.type))/>
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

                <#if parameters.searchText?has_content && !parameters.productCategoryId?has_content>
              
                    <#if includedSearchFacetGroup?has_content && includedSearchFacetGroup.contains(facet.productFeatureGroupId?upper_case!) && valueSize &gt; 0>
                        <li class="facetGroup_${facet.type}">
                            <h3 class="facetGroup <#if facetMinValue == 0>js_showHideFacetGroupLink<#if !facetGroupParamList?has_content> js_seeMoreFacetGroupLink<#else> js_seeLessFacetGroupLink</#if></#if>">${facet.name}</h3>
                            <#if facet.refinementValues?has_content>
                            	<#if FACET_VALUE_STYLE == "DROPDOWN">
		                    		<select name="facetSelector_${facet.type}" id="js_facetSelector_${facet.type}" class="facetGroup facetGroup_${facet.type}" onchange="solrSearch(this, '', '', 'select')">
		                    			<@facetLine facet=facet facetType=facet.type refinementValueName="" refinementValue="" multiFacetRefinedExist="" multiFacetRefined="" multiFacetInitialType="" facetGroupParamList="" allSelected="Y" showItemCount=""/>
			                    		<#list facet.refinementValues as refinementValue>
	                                        <#assign refinementGroupValue = facet.type+":"+refinementValue.name />
	                                        <#if !facetGroupParamList.contains(refinementValue.name) || facetMultiSelect>
	                                            <#assign multiFacetRefinedExist = false/>
	                                            <#if multiFacetGroupRefined?exists>
	                                                <#assign multiFacetRefinedExist = true/>
	                                            </#if>
	                                            <#assign refinementValueName = refinementValue.name>
	                                            <@facetLine facet=facet facetType=facet.type refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetGroupRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N" showItemCount=""/>
	                                        </#if>
	                                    </#list>
						        	</select>
		                    	<#else>
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
	                                            <#assign multiFacetRefinedExist = false/>
	                                            <#if multiFacetGroupRefined?exists>
	                                                <#assign multiFacetRefinedExist = true/>
	                                            </#if>
	                                            <#assign refinementValueName = refinementValue.name>
	                                            <li class="js_facetValue ${refinementValueName?html} ${hideClass}" <#if hideClass == "js_hideThem">style="display:none;"</#if>>
	                                                <@facetLine facet=facet facetType=facet.type refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetGroupRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N" showItemCount=""/>
	                                            </li>
	                                            <#if valueSize! lt facetMaxValue>
	                                                <#assign facetMaxValue = valueSize>
	                                            </#if>
	                                            <#if (indx > facetMinValue) && indx == valueSize>
	                                                <li class="js_facetValue" id="facet_${facet.productFeatureGroupId!}">
	                                                    <#assign remaining = (facetMaxValue?number - facetMinValue?number) + showAllCount />
	                                                    <a class="js_seeMoreLink" href="javascript:void(0);" <#if facetMinValue == 0>style="display:none;"</#if>>${uiLabelMap.FacetSeeMoreLinkCaption}<#if facetShowItemCount> (${remaining!})</#if></a>
	                                                    <a class="js_seeLessLink" href="javascript:void(0);" style="display:none;">${uiLabelMap.FacetSeeLessLinkCaption}</a>
	                                                    <#if showAllCount &gt; 0>
	                                                        <a class="js_showAllLink" href="javascript:void(0);">${uiLabelMap.FacetShowAllCaption} (${showAllCount})</a>
	                                                    </#if>
	                                                </li>
	                                            </#if>
	                                        </#if>
	                                    </#list>
	                                </ul>
                                </#if>
                            </#if>
                        </li>
                    </#if>
                <#else>
                    <#if valueSize &gt; 0>
                        <li class="facetGroup_${facet.type}">
                            <h3 class="facetGroup <#if facetMinValue == 0>js_showHideFacetGroupLink<#if !facetGroupParamList?has_content> js_seeMoreFacetGroupLink<#else> js_seeLessFacetGroupLink</#if></#if>">${facet.name}
                                <#if productFeatureCatGrpAppl?has_content && productFeatureCatGrpAppl.facetTooltip?has_content>
                                    <#assign facetTooltipTxt = Static["com.osafe.util.Util"].formatToolTipText("${productFeatureCatGrpAppl.facetTooltip}", "${productFeatureCatGrpAppl.facetTooltip?length}")/>
                                    <#if facetTooltipTxt?has_content >
                                      <a href="javascript:void(0);" onMouseover="javascript:showTooltip('${StringUtil.wrapString(facetTooltipTxt)!""}', this, 'icon');" onMouseout="hideTooltip()" class="toolTipLink">
                                        <span class="tooltipIcon"></span>
                                      </a>
                                    </#if>
                                </#if>
                            </h3>
                            <#if facet.refinementValues?has_content>
                            	<#if FACET_VALUE_STYLE == "DROPDOWN">
		                    		<select name="facetSelector_${facet.type}" id="js_facetSelector_${facet.type}" class="facetGroup facetGroup_${facet.type}" onchange="solrSearch(this, '', '', 'select')">
		                    			<@facetLine facet=facet facetType=facet.type refinementValueName="" refinementValue="" multiFacetRefinedExist="" multiFacetRefined="" multiFacetInitialType="" facetGroupParamList="" allSelected="Y" showItemCount=""/>
			                    		<#list facet.refinementValues as refinementValue>
	                                        <#assign refinementGroupValue = facet.type+":"+refinementValue.name />
	                                        <#if !facetGroupParamList.contains(refinementValue.name) || facetMultiSelect>
	                                            <#assign multiFacetRefinedExist = false/>
	                                            <#if multiFacetGroupRefined?exists>
	                                                <#assign multiFacetRefinedExist = true/>
	                                            </#if>
	                                            <#assign refinementValueName = refinementValue.name/>
	                                            <@facetLine facet=facet facetType=facet.type refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetGroupRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N" showItemCount=""/>
	                                        </#if>
	                                    </#list>
						        	</select>
		                    	<#else>
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
	                                            <#assign multiFacetRefinedExist = false/>
	                                            <#if multiFacetGroupRefined?exists>
	                                                <#assign multiFacetRefinedExist = true/>
	                                            </#if>
	                                            <#assign refinementValueName = refinementValue.name>
	                                            <li class="js_facetValue ${refinementValueName?html} ${hideClass}" <#if hideClass == "js_hideThem">style="display:none;"</#if>>
	                                                <@facetLine facet=facet facetType=facet.type refinementValueName=refinementValueName refinementValue=refinementValue multiFacetRefinedExist=multiFacetRefinedExist multiFacetRefined=multiFacetGroupRefined?if_exists multiFacetInitialType=multiFacetInitialType?if_exists facetGroupParamList=facetGroupParamList allSelected="N" showItemCount=""/>
	                                            </li>
	                                            <#if valueSize! lt facetMaxValue>
	                                                <#assign facetMaxValue = valueSize>
	                                            </#if>
	                                            <#if (indx > facetMinValue) && indx == valueSize>
	                                                <li class="js_facetValue" id="facet_${facet.productFeatureGroupId}">
	                                                    <#assign remaining = (facetMaxValue?number - facetMinValue?number) + showAllCount />
	                                                    <a class="js_seeMoreLink" href="javascript:void(0);" <#if facetMinValue == 0>style="display:none;"</#if>>${uiLabelMap.FacetSeeMoreLinkCaption}<#if facetShowItemCount> (${remaining!})</#if></a>
	                                                    <a class="js_seeLessLink" href="javascript:void(0);" style="display:none;">${uiLabelMap.FacetSeeLessLinkCaption}</a>
	                                                    <#if showAllCount &gt; 0>
	                                                        <a class="js_showAllLink" href="javascript:void(0);">${uiLabelMap.FacetShowAllCaption} (${showAllCount})</a>
	                                                    </#if>
	                                                </li>
	                                            </#if>
	                                        </#if>
	                                    </#list>
	                                </ul>
                                </#if>
                            </#if>
                        </li>
                    </#if>
                </#if>
            </#if>

        </#list><!-- mail loop loopFacetList -->
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