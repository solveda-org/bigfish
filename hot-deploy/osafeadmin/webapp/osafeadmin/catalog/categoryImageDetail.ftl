<#if productCategory?has_content>
  <input type="hidden" name="productCategoryId" value="${productCategory.productCategoryId!}"/>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ImageCaption}</label>
      </div>
      <div class="infoValue">
        <#if productCategory.categoryImageUrl?exists && productCategory.categoryImageUrl != "">
          <img src="<@ofbizContentUrl>${productCategory.categoryImageUrl}?${nowTimestamp!}</@ofbizContentUrl>" alt="${productCategory.categoryImageUrl}" height="${IMG_SIZE_PLP_CAT_H!""}" width="${IMG_SIZE_PLP_CAT_W!""}" class="imageBorder"/>
        <#else>
          <span class="noImage imageBorder"></span>
        </#if>
      </div>
      <div class="infoText">
        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.CategoryImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
       
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.NewImageCaption}</label>
      </div>
      <div class="infoValue">
        <input type="file" name="categoryImage" size="50" value=""/>
      </div>
    </div>
  </div>
</#if>
