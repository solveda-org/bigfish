<div class="displayBox detailInfo">
  <div class="header"><h2>${detailInfoBoxHeading!}</h2></div>
  <div class="boxBody">
    <#if product?has_content>
      <#if productContentWrapper?exists>
        <#assign plpSwatchImageURL = productContentWrapper.get("PLP_SWATCH_IMAGE_URL")!""/>
        <#assign pdpSwatchImageURL = productContentWrapper.get("PDP_SWATCH_IMAGE_URL")!""/>
      </#if>
      <#assign plpSwatchImageHeight= IMG_SIZE_PLP_SWATCH_H!""/>
      <#assign plpSwatchImageWidth= IMG_SIZE_PLP_SWATCH_W!""/>
      <#assign pdpSwatchImageHeight= IMG_SIZE_PDP_SWATCH_H!""/>
      <#assign pdpSwatchImageWidth= IMG_SIZE_PDP_SWATCH_W!""/>
      <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.PLPSwatchImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if plpSwatchImageURL?exists && plpSwatchImageURL != "">
              <img src="<@ofbizContentUrl>${plpSwatchImageURL}?${curDateTime!}</@ofbizContentUrl>" alt="${plpSwatchImageURL!}" class="imageBorder" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if>/>
              <a href="javascript:setProdContentTypeId('PLP_SWATCH_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
        </div>
      </div>
      
      <div class="infoRow bottomRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.NewPLPSwatchImageCaption}</label>
          </div>
          <div class="infoValue">
            <input type="file" name="plpSwatchImage" size="50" value=""/>
          </div>
        </div>
      </div>
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.PDPSwatchImageCaption}</label>
          </div>
          <div class="infoValue">
            <#if pdpSwatchImageURL?exists && pdpSwatchImageURL != "">
              <img src="<@ofbizContentUrl>${pdpSwatchImageURL}?${curDateTime!}</@ofbizContentUrl>" alt="${pdpSwatchImageURL!}" class="imageBorder" <#if pdpSwatchImageHeight != '0' && pdpSwatchImageHeight != ''>height = "${pdpSwatchImageHeight}"</#if> <#if pdpSwatchImageWidth != '0' && pdpSwatchImageWidth != ''>width = "${pdpSwatchImageWidth}"</#if>/>
              <a href="javascript:setProdContentTypeId('PDP_SWATCH_IMAGE_URL');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');"><span class="crossIcon"></span></a>
            <#else>
              <span class="noImage imageBorder"></span>
            </#if>
          </div>
        </div>
      </div>
     
      <div class="infoRow bottomRow">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.NewPDPSwatchImageCaption}</label>
          </div>
          <div class="infoValue">
            <input type="file" name="pdpSwatchImage" size="50" value=""/>
          </div>
        </div>
      </div>
    </#if>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonDetailActionButton")}
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonProductLinkButton")}
  </div>
</div>
