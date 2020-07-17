<#if product?has_content>
  <#if productContentWrapper?exists>
    <#assign altLargeImage = productContentWrapper.get("XTRA_IMG_${altImgNo}_LARGE")!""/>
    <#assign altThumbnailImage = productContentWrapper.get("ADDITIONAL_IMAGE_${altImgNo}")!""/>
    <#assign altDetailImage = productContentWrapper.get("XTRA_IMG_${altImgNo}_DETAIL")!""/>
  </#if>
  <#if altImgNo?number == 1 || altLargeImage !='' || altThumbnailImage != '' || altDetailImage != ''>
    <#assign imageExists = 'true'>
  <#else>
    <#assign prevAltImageNo = altImgNo?number - 1/>
    <#assign prevAltLargeImage = productContentWrapper.get("XTRA_IMG_${prevAltImageNo}_LARGE")!""/>
    <#assign prevAltThumbnailImage = productContentWrapper.get("ADDITIONAL_IMAGE_${prevAltImageNo}")!""/>
    <#assign prevAltDetailImage = productContentWrapper.get("XTRA_IMG_${prevAltImageNo}_DETAIL")!""/>
    
    <#if prevAltLargeImage !='' || prevAltThumbnailImage != '' || prevAltDetailImage != ''>
      <#assign imageExists = 'true'>
    <#else>
      <#assign imageExists = 'false'>
    </#if>
  </#if>
<div class="displayBox detailInfo <#if imageExists == 'false'>slidingClose</#if>">
  <div class="header"><h2>${detailInfoBoxHeading!}</h2></div>
  <div class="boxBody">
    <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.LargeImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if altLargeImage != "">
              <img src="<@ofbizContentUrl>${altLargeImage!}?${curDateTime!}</@ofbizContentUrl>" alt="${altLargeImage!}" height="${IMG_SIZE_PDP_REG_H!""}" width="${IMG_SIZE_PDP_REG_W!""}" class="imageBorder"/>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
        </div>
        <div class="infoIcon">
         <#if altLargeImage != "">
           <a href="javascript:setProdContentTypeId('XTRA_IMG_${altImgNo}_LARGE');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
         </#if>
           <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.LargeAltPDPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
        </div>
      </div>
     
      <div class="infoRow bottomRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.NewLargeImageCaption}</label>
          </div>
          <div class="infoValue">
            <input type="file" name="altLargeImage_${altImgNo}" size="50" value=""/>
          </div>
        </div>
      </div>
       
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.ThumbnailImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if altThumbnailImage != "">
              <img src="<@ofbizContentUrl>${altThumbnailImage}?${curDateTime!}</@ofbizContentUrl>" alt="${altThumbnailImage}" height="${IMG_SIZE_PDP_THUMB_H!""}" width="${IMG_SIZE_PDP_THUMB_W!""}" class="imageBorder"/>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
          <div class="infoIcon">
            <#if altThumbnailImage != "">
              <a href="javascript:setProdContentTypeId('ADDITIONAL_IMAGE_${altImgNo}');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
            </#if>
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.ThumbAltPDPImageInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
          </div>
        </div>
      </div>
     
      <div class="infoRow bottomRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.NewThumbnailImageCaption}</label>
          </div>
          <div class="infoValue">
            <input type="file" name="altThumbImage_${altImgNo}" size="50" value=""/>
          </div>
        </div>
      </div>
     
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.PopUpDetailImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if altDetailImage != "">
              <img src="<@ofbizContentUrl>${altDetailImage}?${curDateTime!}</@ofbizContentUrl>" alt="${altDetailImage}" height="${IMG_SIZE_PDP_POPUP_H!""}" width="${IMG_SIZE_PDP_POPUP_W!""}" class="imageBorder"/>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
          <div class="infoIcon">
            <#if altDetailImage != "">
              <a href="javascript:setProdContentTypeId('XTRA_IMG_${altImgNo}_DETAIL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
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
            <input type="file" name="altDetailImage_${altImgNo}" size="50" value=""/>
          </div>
        </div>
      </div>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonDetailActionButton")}
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonProductLinkButton")}
    </#if>
  </div>
</div>