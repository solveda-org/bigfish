${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}

<#if errorMessageList?has_content>
    <#if errorMessageList?has_content>
      <div class="content-messages eCommerceErrorMessage">
        <span class="errorImageIcon errorImage"></span>
        <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
        <#if errorMessageList?has_content>
          <#list errorMessageList as errorMsg>
		    <#if errorMsg?exists && errorMsg != null>
            <p class="errorMessage">${StringUtil.wrapString(errorMsg)}</p>
			</#if>
          </#list>
        </#if>
      </div>
    </#if>
</#if>

<#if prodCatErrorList?has_content || prodCatWarningList?has_content>
<div id="productCatError" class="content-messages eCommerceErrorMessage commonDivHide" style="display:none">
  <span class="errorImageIcon errorImage"></span>
  <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
  <#if prodCatErrorList?exists && prodCatErrorList?has_content>
    <#list prodCatErrorList as prodCatError>
      <p class="errorMessage">${prodCatError!}</p>
    </#list>
  </#if>
  <#if prodCatWarningList?exists && prodCatWarningList?has_content>
    <#list prodCatWarningList as prodCatWarning>
      <p class="errorMessage">${prodCatWarning!}</p>
    </#list>
  </#if>
</div>
</#if>

<#if productErrorList?has_content || productWarningList?has_content>
<div id="productError" class="content-messages eCommerceErrorMessage commonDivHide" style="display:none">
  <span class="errorImageIcon errorImage"></span>
  <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
  <#if productErrorList?exists && productErrorList?has_content>
    <#list productErrorList as productError>
      <p class="errorMessage">${productError!}</p>
    </#list>
  </#if>
  <#if productWarningList?exists && productWarningList?has_content>
    <#list productWarningList as productWarning>
      <p class="errorMessage">${productWarning!}</p>
    </#list>
  </#if>
</div>
</#if>

<#if productAssocErrorList?has_content || productAssocWarningList?has_content>
<div id="productAssocError" class="content-messages eCommerceErrorMessage commonDivHide" style="display:none">
  <span class="errorImageIcon errorImage"></span>
  <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
  <#if productAssocErrorList?exists && productAssocErrorList?has_content>
    <#list productAssocErrorList as productAssocError>
      <p class="errorMessage">${productAssocError!}</p>
    </#list>
  </#if>
  <#if productAssocWarningList?exists && productAssocWarningList?has_content>
    <#list productAssocWarningList as productAssocWarning>
      <p class="errorMessage">${productAssocWarning!}</p>
    </#list>
  </#if>
</div>
</#if>

<#if productFacetGroupErrorList?has_content>
<div id="productFacetGroupError" class="content-messages eCommerceErrorMessage commonDivHide" style="display:none">
  <span class="errorImageIcon errorImage"></span>
  <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
  <#if productFacetGroupErrorList?exists && productFacetGroupErrorList?has_content>
    <#list productFacetGroupErrorList as productFacetGroupError>
      <p class="errorMessage">${productFacetGroupError!}</p>
    </#list>
  </#if>
</div>
</#if>

<#if productFacetValueErrorList?has_content>
<div id="productFacetValueError" class="content-messages eCommerceErrorMessage commonDivHide" style="display:none">
  <span class="errorImageIcon errorImage"></span>
  <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
  <#if productFacetValueErrorList?exists && productFacetValueErrorList?has_content>
    <#list productFacetValueErrorList as productFacetValueError>
      <p class="errorMessage">${productFacetValueError!}</p>
    </#list>
  </#if>
</div>
</#if>

<#if productManufacturerErrorList?has_content>
<div id="productManufacturerError" class="content-messages eCommerceErrorMessage commonDivHide" style="display:none">
  <span class="errorImageIcon errorImage"></span>
  <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
  <#if productManufacturerErrorList?exists && productManufacturerErrorList?has_content>
    <#list productManufacturerErrorList as productManufacturerError>
      <p class="errorMessage">${productManufacturerError!}</p>
    </#list>
  </#if>
</div>
</#if>

<#if productAttributesErrorList?has_content>
<div id="productAttributesError" class="content-messages eCommerceErrorMessage commonDivHide" style="display:none">
  <span class="errorImageIcon errorImage"></span>
  <p class="errorMessage">${uiLabelMap.FollowingErrorsOccurredError}</p>
  <#if productAttributesErrorList?exists && productAttributesErrorList?has_content>
    <#list productAttributesErrorList as productAttributesError>
      <p class="errorMessage">${productAttributesError!}</p>
    </#list>
  </#if>
</div>
</#if>

<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
  <#if (prodCatErrorList?exists && prodCatErrorList?has_content) || (productErrorList?exists && productErrorList?has_content) || (productAssocErrorList?exists && productAssocErrorList?has_content) || (productManufacturerErrorList?exists && productManufacturerErrorList?has_content) || (errorMessageList?exists && errorMessageList?has_content)>
      <input type="hidden" name="errorExists" value="${parameters.errorExists!"yes"}"/> 
  <#else>
      <input type="hidden" name="errorExists" value="${parameters.errorExists!"no"}"/>
  </#if>
  <input type="hidden" name="xlsFileName" value="${xlsFileName!parameters.xlsFileName!}"/>
  <input type="hidden" name="xlsFilePath" value="${xlsFilePath!parameters.xlsFilePath!}"/>
  <input type="hidden" name="productLoadImagesDir" value="${productLoadImagesDir!parameters.productLoadImagesDir!}"/>
  <input type="hidden" name="imageUrl" value="${imageUrl!parameters.imageUrl!}"/>
  <div class="displayBox detailInfo productLoaderData">
    <div class="header" id="productLoaderHeader"><h2>${detailInfoBoxHeading!}: ${uiLabelMap.ProcessingOptionsHeading}</h2></div>
      ${sections.render('productLoaderTabBoxBody')?if_exists}
    <div class="boxBody productLoaderDataOverflow">
        ${sections.render('productLoaderDataBoxBody')?if_exists}
    </div>
  </div>
  <div>
    <div class="boxBody">
        <table class="osafe">
           ${sections.render('commonDetailActionButton')?if_exists}
        </table>
    </div>
  </div>
</form>
