<!-- start searchBox -->
  <input type="hidden" id="lookupProduct_noConditionFind" value="Y" name="noConditionFind">
  <div class="entryRow">
    <div class="entry">
      <label class="largeLabel">${uiLabelMap.ProductFeatureCaption}</label>
      <div class="entryInput">
        <input class="textEntry" type="text" id="productFeatureId" name="productFeatureId" maxlength="40" value="${parameters.productFeatureId!""}"/>
      </div>
    </div>
  </div>
  <#assign productFeatureTypes = delegator.findByAnd("ProductFeatureType", {}, ["description"]) />
  <div class="entryRow">
    <div class="entry">
      <label class="largeLabel">${uiLabelMap.ProductFeatureTypeCaption}</label>
      <div class="entryInput">
        <select id="productFeatureTypeId" name="productFeatureTypeId">
            <option value="">${uiLabelMap.SelectOneLabel}</option>
            <#if productFeatureTypes?exists && productFeatureTypes?has_content>
              <#list productFeatureTypes as productFeatureType>
                  <option value="${productFeatureType.productFeatureTypeId?if_exists}" <#if (parameters.productFeatureTypeId!"") == "${productFeatureType.productFeatureTypeId?if_exists}">selected</#if>>${productFeatureType.description!productFeatureType.productFeatureTypeId!""}</option>
              </#list>
            </#if>
          </select>
      </div>
    </div>
  </div>
  <div class="entryRow">
    <div class="entry medium">
      <label class="largeLabel">${uiLabelMap.DescriptionCaption}</label>
      <div class="entryInput">
        <input class="largeTextEntry" type="text" id="description" name="description" value="${parameters.description!""}"/>
      </div>
    </div>
  </div>
  <!-- end searchBox -->