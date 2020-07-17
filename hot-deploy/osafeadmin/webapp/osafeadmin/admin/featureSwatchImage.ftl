<#assign productFeatureDataResourcesPDP = delegator.findByAnd("ProductFeatureDataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId",parameters.productFeatureId!,"featureDataResourceTypeId","PDP_SWATCH_IMAGE_URL"))/>
<#if productFeatureDataResourcesPDP?has_content>
  <#assign productFeatureDataResourcePDP = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureDataResourcesPDP) />
  <#assign dataResourcePDP = productFeatureDataResourcePDP.getRelatedOne("DataResource")/>
  <#assign productFeatureResourcePDPUrl = dataResourcePDP.objectInfo!""/>
  <#if productFeatureResourcePDPUrl?has_content>
    <#assign productFeaturePDPSwatchURL=productFeatureResourcePDPUrl/>
  </#if>
</#if>

<#assign productFeatureDataResourcesPLP = delegator.findByAnd("ProductFeatureDataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId",parameters.productFeatureId!,"featureDataResourceTypeId","PLP_SWATCH_IMAGE_URL"))/>
<#if productFeatureDataResourcesPLP?has_content>
  <#assign productFeatureDataResourcePLP = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureDataResourcesPLP) />
  <#assign dataResourcePLP = productFeatureDataResourcePLP.getRelatedOne("DataResource")/>
  <#assign productFeatureResourcePLPUrl = dataResourcePLP.objectInfo!""/>
  <#if productFeatureResourcePLPUrl?has_content>
    <#assign productFeaturePLPSwatchURL=productFeatureResourcePLPUrl/>
  </#if>
</#if>

<#assign plpSwatchImageHeight= IMG_SIZE_PLP_SWATCH_H!""/>
<#assign plpSwatchImageWidth= IMG_SIZE_PLP_SWATCH_W!""/>
<#assign pdpSwatchImageHeight= IMG_SIZE_PDP_SWATCH_H!""/>
<#assign pdpSwatchImageWidth= IMG_SIZE_PDP_SWATCH_W!""/>

  <#assign curDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()/>
  <input type="hidden" name="productFeatureId" value="${parameters.productFeatureId!}"/>
  <input type="hidden" name="productFeatureGroupId" value="${parameters.productFeatureGroupId!}"/>
      <div class="infoRow">
          <div class="infoEntry">
              <div class="infoCaption">
                  <label>${uiLabelMap.PLPSwatchImageCaption}</label>
              </div>
              <div class="infoValue">
                <#if productFeaturePLPSwatchURL?exists && productFeaturePLPSwatchURL != "">
                  <img src="<@ofbizContentUrl>${productFeaturePLPSwatchURL}?${curDateTime!}</@ofbizContentUrl>" alt="${productFeaturePLPSwatchURL!}" class="imageBorder" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if>/>
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
                <#if productFeaturePDPSwatchURL?exists && productFeaturePDPSwatchURL != "">
                  <img src="<@ofbizContentUrl>${productFeaturePDPSwatchURL}?${curDateTime!}</@ofbizContentUrl>" alt="${productFeaturePDPSwatchURL!}" class="imageBorder" <#if pdpSwatchImageHeight != '0' && pdpSwatchImageHeight != ''>height = "${pdpSwatchImageHeight}"</#if> <#if pdpSwatchImageWidth != '0' && pdpSwatchImageWidth != ''>width = "${pdpSwatchImageWidth}"</#if>/>
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

