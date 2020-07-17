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
              <div class="infoText">
                  <label>${uiLabelMap.LargePDPImageInfo}</label>
              </div>
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
                  <a href="javascript:setProdContentTypeId('THUMBNAIL_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
              <div class="infoText">
                  <label>${uiLabelMap.ThumbPDPImageInfo}</label>
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
                  <a href="javascript:setProdContentTypeId('DETAIL_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
              <div class="infoText">
                  <label>${uiLabelMap.DetailPOPUPImageInfo}</label>
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
              <div class="infoText">
                  <label>${uiLabelMap.SmallPLPImageInfo}</label>
              </div>
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
              <div class="infoText">
                  <label>${uiLabelMap.PLPTitleTextInfo}</label>
              </div>
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
                  <a href="javascript:setProdContentTypeId('SMALL_IMAGE_ALT_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
                <#else>
                  <span class="noImage imageBorder"></span>
                </#if>
              </div>
              <div class="infoText">
                  <label>${uiLabelMap.SmallAltImageInfo}</label>
              </div>
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
