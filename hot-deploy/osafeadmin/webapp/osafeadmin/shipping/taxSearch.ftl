<!-- start searchBox -->
  <#assign taxAuthorityRateProductList = delegator.findByAnd("TaxAuthorityRateProduct" Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", globalContext.productStoreId))!"" />
  <#if taxAuthorityRateProductList?has_content>
    <#assign taxAuthPartyIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(taxAuthorityRateProductList, "taxAuthPartyId", true) />
  </#if>
  <input type="hidden" name="productStoreId" value="${globalContext.productStoreId!}" />
  <div class="entryRow">
    <div class="entry">
    <label>${uiLabelMap.TaxAuthorityCaption}</label>
      <div class="entryInput select">
        <select id="taxAuthPartyId" name="taxAuthPartyId">
          <#if taxAuthPartyIds?exists && taxAuthPartyIds?has_content>
            <#list taxAuthPartyIds as taxAuthPartyId>
              <option value="${taxAuthPartyId?if_exists}" <#if (parameters.taxAuthPartyId!"") == "${taxAuthPartyId?if_exists}">selected</#if>>&nbsp;&nbsp;${taxAuthPartyId?if_exists}</option>
            </#list>
          </#if>
        </select>
      </div>
    </div>
  </div>
<!-- end searchBox -->