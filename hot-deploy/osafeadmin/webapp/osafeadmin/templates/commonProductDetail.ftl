${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
<#if productInfoBoxHeading?exists && productInfoBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${productInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('productInfoBoxBody')?if_exists}
        </div>
    </div>
</#if>

<#if productVirtualInfoBoxHeading?exists && productVirtualInfoBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${productVirtualInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('productVirtualInfoBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if (mode?has_content && mode =='edit' && isVariant != 'Y') || (mode?has_content && mode =='add' && !virtualProduct?has_content)>
	<#if productCategoryMembershipBoxHeading?exists && productCategoryMembershipBoxHeading?has_content>
	    <div class="displayBox detailInfo">
	        <div class="header"><h2>${productCategoryMembershipBoxHeading!}</h2></div>
	        <div class="boxBody">
	            ${sections.render('productCategoryMembershipBoxBody')?if_exists}
	        </div>
	    </div>
	</#if>
</#if>

<#if productDescriptionBoxHeading?exists && productDescriptionBoxHeading?has_content>
  <#assign sliding = "close"/>
  <#if (mode?has_content && mode =='edit' && isVariant != 'Y') || (mode?has_content && mode =='add' && !virtualProduct?has_content)>
     <#assign sliding = "open"/>
  <#else>
     <#if productContentList?has_content>
       <#list productContentList as productContent>
       <#if productContent.productContentTypeId != "PRODUCT_NAME" && productContent.productContentTypeId != "PLP_LABEL" && productContent.productContentTypeId != "PDP_LABEL">
         <#assign contentData = ""/>
         <#assign content = productContent.getRelatedOne("Content")!""/>
          <#assign dataResource = content.getRelatedOne("DataResource")!""/>
          <#if dataResource?has_content>
            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
            <#if electronicText?has_content>
              <#assign contentData = electronicText.textData!""/>
            </#if>
          </#if>
          <#if contentData?exists && contentData!="">
            <#assign sliding = "open"/>
            <#break>
          </#if>
        </#if>
       </#list>
     </#if>
  </#if>
    <div class="displayBox detailInfo <#if sliding = 'close'>slidingClose<#else>slidingOpen</#if>">
        <div class="header"><h2>${productDescriptionBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('productDescriptionBoxBody')?if_exists}
        </div>
    </div>
</#if>
<#if productAttributeBoxHeading?exists && productAttributeBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${productAttributeBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('productAttributeBoxBody')?if_exists}
        </div>
    </div>
</#if>

<#if (passedVariantProductIds?has_content || parameters.variantProductIds?has_content)>
    <#assign isVariant = "Y" />
</#if>
<#if !(isVariant?exists && isVariant == 'Y')>
    <#if productFeatureBoxHeading?exists && productFeatureBoxHeading?has_content>
        <div class="displayBox detailInfo">
            <div class="header"><h2>${productFeatureBoxHeading!}</h2></div>
            <div class="boxBody">
               ${sections.render('productFeatureBoxBody')?if_exists}
            </div>
        </div>
    </#if>
</#if>
<#if productVariantFeatureBoxHeading?exists && productVariantFeatureBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${productVariantFeatureBoxHeading!}</h2></div>
        <div class="boxBody">
           ${sections.render('productVariantFeatureBoxBody')?if_exists}
        </div>
    </div>
</#if>

<#if productIdentificationBoxHeading?exists && productIdentificationBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${productIdentificationBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('productIdentificationBoxBody')?if_exists}
        </div>
    </div>
</#if>
<div class="displayBox footerInfo">
    <div>
        ${sections.render('commonDetailActionButton')?if_exists}
    </div>
    <div class="infoDetailIcon">
      ${sections.render('commonDetailLinkButton')?if_exists}
      ${sections.render('commonDetailHelperIcon')?if_exists}
      ${sections.render('commonDetailWarningIcon')?if_exists}
    </div>
</div>
${sections.render('commonLookup')!}
</form>
