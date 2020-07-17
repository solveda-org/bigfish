<#if taxAuthorityRateProduct?has_content>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.GeoIdCaption}</label>
      </div>
      <div class="infoValue">
        ${taxAuthorityRateProduct.taxAuthGeoId!}
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.SeqIdCaption}</label>
      </div>
      <div class="infoValue">
          ${taxAuthorityRateProduct.taxAuthorityRateSeqId!}
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.RateTypeCaption}</label>
      </div>
      <div class="infoValue">
        ${taxAuthorityRateProduct.taxAuthorityRateTypeId!}
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.GeoTypeIdCaption}</label>
      </div>
      <div class="infoValue">
        <#assign geo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId" , taxAuthorityRateProduct.taxAuthGeoId!),false)?if_exists/>
        ${geo.geoTypeId!}
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ProductCategoryCaption}</label>
      </div>
      <div class="infoValue">
        ${taxAuthorityRateProduct.productCategoryId!uiLabelMap.AllLabel!}
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.TaxOnShipCaption}</label>
      </div>
      <div class="infoValue">
        ${taxAuthorityRateProduct.taxShipping!}
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.TaxOnItemsCaption}</label>
      </div>
      <div class="infoValue">
        ${taxAuthorityRateProduct.taxPercentage!}
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.FromDateCaption}</label>
      </div>
      <div class="infoValue">
        ${(taxAuthorityRateProduct.fromDate?string(preferredDateFormat))!""}
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ThruDateCaption}</label>
      </div>
      <div class="infoValue">
        ${(taxAuthorityRateProduct.thruDate?string(preferredDateFormat))!""}
      </div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.DescriptionCaption}</label>
      </div>
      <div class="infoValue">
        ${taxAuthorityRateProduct.description!}
      </div>
    </div>
  </div>
<#else>
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>