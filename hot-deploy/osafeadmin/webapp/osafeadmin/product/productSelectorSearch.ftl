<!-- start searchBox -->
    <input type="hidden" id="lookupProduct_noConditionFind" value="Y" name="noConditionFind">
  <div class="entryRow">
    <div class="entry">
      <label>${uiLabelMap.ProductNoCaption}</label>
      <div class="entryInput">
        <input class="textEntry" type="text" id="productId" name="productId" maxlength="40" value="${parameters.productId!""}"/>
      </div>
    </div>
  </div>
  <div class="entryRow">
    <div class="entry">
      <label>${uiLabelMap.ItemNoCaption}</label>
      <div class="entryInput">
        <input class="textEntry" type="text" id="internalName" name="internalName" maxlength="40" value="${parameters.internalName!""}"/>
      </div>
    </div>
  </div>
  <div class="entryRow">
    <div class="entry medium">
      <label>${uiLabelMap.ProductNameCaption}</label>
      <div class="entryInput">
        <input class="largeTextEntry" type="text" id="productName" name="productName" value="${parameters.productName!""}"/>
      </div>
    </div>
  </div>
  <!-- end searchBox -->