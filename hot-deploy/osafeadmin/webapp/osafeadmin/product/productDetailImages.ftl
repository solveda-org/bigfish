<#if product?has_content>
  <#if productContentWrapper?exists>
    <#assign productLargeImage = productContentWrapper.get("LARGE_IMAGE_URL")!""/>
    <#assign productThumbnailImage = productContentWrapper.get("THUMBNAIL_IMAGE_URL")!""/>
    <#assign productDetailImage = productContentWrapper.get("DETAIL_IMAGE_URL")!""/>
    <#assign productSmallImage = productContentWrapper.get("SMALL_IMAGE_URL")!""/>
    <#assign productSmallAltImage = productContentWrapper.get("SMALL_IMAGE_ALT_URL")!""/>
    <#assign plpTitleText = productContentWrapper.get("SMALL_IMAGE_ALT")!""/>
  </#if>
  <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
  <input type="hidden" name="productId" value="${product.productId!}"/>
  <input type="hidden" name="productContentTypeId" id="productContentTypeId" value="${parameters.productContentTypeId!}"/>
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.LargeImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productLargeImage != "">
                  <img src="<@ofbizContentUrl>${productLargeImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productLargeImage}" height="${IMG_SIZE_PDP_REG_H!""}" width="${IMG_SIZE_PDP_REG_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
          </div>
          <div class="infoIcon">
              <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.LargePDPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
       </div>
       <hr/>
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewLargeImageCaption}</label>
              </div>
              <div class="infoValue">
                <input type="file" name="largeImage" size="50" value=""/>
              </div>
          </div>
       </div>
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.ThumbnailImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productThumbnailImage != "">
                  <img src="<@ofbizContentUrl>${productThumbnailImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productThumbnailImage}" height="${IMG_SIZE_PDP_THUMB_H!""}" width="${IMG_SIZE_PDP_THUMB_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
	          <div class="infoIcon">
                <#if productThumbnailImage != "">
	              <a class="helper" href="javascript:setProdContentTypeId('THUMBNAIL_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
	            </#if>
	              <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.ThumbPDPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	          </div>
          </div>
       </div>
     
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewThumbnailImageCaption}</label>
              </div>
              <div class="infoValue">
                <input type="file" name="thumbnailImage" size="50" value=""/>
              </div>
          </div>
       </div>
     
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.PopUpDetailImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productDetailImage != "">
                  <img src="<@ofbizContentUrl>${productDetailImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productDetailImage}" height="${IMG_SIZE_PDP_POPUP_H!""}" width="${IMG_SIZE_PDP_POPUP_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
	          <div class="infoIcon">
                <#if productDetailImage != "">
	              <a class="helper" href="javascript:setProdContentTypeId('DETAIL_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
	            </#if>
	              <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.DetailPOPUPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	          </div>
          </div>
       </div>
     
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewPopUpDetailImageCaption}</label>
              </div>
              <div class="infoValue">
                <input type="file" name="detailImage" size="50" value=""/>
              </div>
          </div>
       </div>
     
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.SmallImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productSmallImage != "">
                 <img src="<@ofbizContentUrl>${productSmallImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productSmallImage}" height="${IMG_SIZE_PLP_H!""}" width="${IMG_SIZE_PLP_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
          </div>
          <div class="infoIcon">
             <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SmallPLPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
       </div>
       
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewSmallImageCaption}</label>
              </div>
              <div class="infoValue">
                <input type="file" name="smallImage" size="50" value=""/>
              </div>
          </div>
       </div>
    
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.PLPImageTitleTextCaption}</label>
              </div>
              <div class="infoValue">
                <input type="text" name="plpTitleText" id="plpTitleText" class="medium" value="${parameters.plpTitleText!plpTitleText!""}" />
              </div>
          </div>
          <div class="infoIcon">
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.PLPTitleTextInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
       </div>
       
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.SmallAltImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productSmallAltImage != "">
                  <img src="<@ofbizContentUrl>${productSmallAltImage}?${curDateTime!}</@ofbizContentUrl>" alt="${productSmallAltImage}" height="${IMG_SIZE_PLP_H!""}" width="${IMG_SIZE_PLP_W!""}" class="imageBorder"/>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
          </div>
          <div class="infoIcon">
            <#if productSmallAltImage != "">
             <a class="cross" href="javascript:setProdContentTypeId('SMALL_IMAGE_ALT_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
            </#if>
             <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SmallAltImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
       </div>
     
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewSmallAltImageCaption}</label>
              </div>
              <div class="infoValue">
                <input type="file" name="smallAltImage" size="50" value=""/>
              </div>
          </div>
       </div>
</#if>
