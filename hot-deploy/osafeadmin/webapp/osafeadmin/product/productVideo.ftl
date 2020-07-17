<#if product?has_content>
  <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
  <#if productContentWrapper?exists>
    <#assign productVideoUrl = productContentWrapper.get("PDP_VIDEO_URL")!""/>
    <#assign productVideo360Url = productContentWrapper.get("PDP_VIDEO_360_URL")!""/>
  </#if>
  <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
  <input type="hidden" name="productId" value="${product.productId!}"/>
  <input type="hidden" name="productContentTypeId" id="productContentTypeId" value="${parameters.productContentTypeId!}"/>
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.VideoURLCaption}</label>
              </div>
              <div class="infoValue">
                <#if productVideoUrl != "">
                  <object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
		            <param name="movie" value="<@ofbizContentUrl>${productVideoUrl}?${curDateTime!}</@ofbizContentUrl>">
		            <param name="wmode" value="transparent" />
                    <embed src="<@ofbizContentUrl>${productVideoUrl}?${curDateTime!}</@ofbizContentUrl>" height="${IMG_SIZE_PDP_VIDEO_H!""}" width="${IMG_SIZE_PDP_VIDEO_W!""}" class="imageBorder" wmode="transparent"/>
                  </object>
                  <a href="javascript:setProdContentTypeId('PDP_VIDEO_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
                <#else>
                  <span class="noVideo imageBorder"></span>
                </#if>
              </div>
          </div>
       </div>
       <hr/>
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.NewVideoCaption}</label>
              </div>
              <div class="infoValue">
                <input type="file" name="videoUrl" size="50" value=""/>
              </div>
          </div>
       </div>
       <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.VideoURL360Caption}</label>
              </div>
              <div class="infoValue">
                <#if productVideo360Url != "">
                  <object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
		            <param name="movie" value="<@ofbizContentUrl>${productVideo360Url}?${curDateTime!}</@ofbizContentUrl>">
		            <param name="wmode" value="transparent" />
                    <embed src="<@ofbizContentUrl>${productVideo360Url}?${curDateTime!}</@ofbizContentUrl>" height="${IMG_SIZE_PDP_VIDEO_H!""}" width="${IMG_SIZE_PDP_VIDEO_W!""}" class="imageBorder" wmode="transparent"/>
                  </object>
                  <a href="javascript:setProdContentTypeId('PDP_VIDEO_360_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
                <#else>
                  <span class="noVideo imageBorder"></span>
                </#if>
              </div>
          </div>
       </div>
     
       <div class="infoRow bottomRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.New360VideoCaption}</label>
              </div>
              <div class="infoValue">
                <input type="file" name="video360Url" size="50" value=""/>
              </div>
          </div>
       </div>
</#if>
