<script type="text/javascript">
    var productPromoActionCategoryObject = new Object();
    productPromoActionCategoryObject.divId = "productPromoActionCategoryDiv";
    productPromoActionCategoryObject.dataRows = "productPromoActionCategoryDataRows";
    productPromoActionCategoryObject.processTypeSelectId = "productPromoActionCategoryApplEnumId";
    productPromoActionCategoryObject.processTypeHiddenId1 = "productPromoActionCategoryId";
    productPromoActionCategoryObject.processTypeHiddenId2 = "productPromoActionCategoryName";
    productPromoActionCategoryObject.newTypeHiddenNamePrefix1 = "productPromoActionCategoryApplEnumId_";
    productPromoActionCategoryObject.newTypeHiddenNamePrefix2 = "productPromoActionCategoryId_";

    var productPromoActionProductObject = new Object();
    productPromoActionProductObject.divId = "productPromoActionProductDiv";
    productPromoActionProductObject.dataRows = "productPromoActionProductDataRows";
    productPromoActionProductObject.processTypeSelectId = "productPromoActionProductApplEnumId";
    productPromoActionProductObject.processTypeHiddenId1 = "productPromoActionProductId";
    productPromoActionProductObject.processTypeHiddenId2 = "productPromoActionProductName";
    productPromoActionProductObject.newTypeHiddenNamePrefix1 = "productPromoActionProductApplEnumId_";
    productPromoActionProductObject.newTypeHiddenNamePrefix2 = "productPromoActionProductId_";

</script>
<#if mode?has_content>
  <#if productPromoAction?has_content>
    <#assign quantity = productPromoAction.quantity!"" />
    <#assign amount = productPromoAction.amount!"" />
    <#assign productId = productPromoAction.productId!"" />
    <#assign productPromoActionEnumId = productPromoAction.productPromoActionEnumId!"" />
    <#assign productPromoActionSeqId = productPromoAction.productPromoActionSeqId!"" />
  </#if> 

  <#assign selectedProductPromoActionEnum = parameters.productPromoActionEnumId!productPromoActionEnumId!selectedProductPromoActionEnum!""/>

  <input type="hidden" name="productPromoActionSeqId" value="${parameters.productPromoActionSeqId!productPromoActionSeqId!""}" />

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionActionCaption}</label>
      </div>
      <div class="infoValue">
        <select name="productPromoActionEnumId" id="productPromoActionEnumId" class="extraSmall">
          <#if productPromoActionEnums?has_content>
            <#list productPromoActionEnums as productPromoActionEnum>
              <option value='${productPromoActionEnum.enumId!}'  <#if selectedProductPromoActionEnum == productPromoActionEnum.enumId >selected=selected</#if>>${productPromoActionEnum.description?default(productPromoActionEnum.enumId!)}</option>
            </#list>
          </#if>
        </select>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">

      <div class="QTYDIV">
        <div class="infoCaption">
          <label>
            <span class="QTY">${uiLabelMap.PromotionQuantityCaption}</span>
            <span class="MINQTY">${uiLabelMap.PromotionQuantityMinCaption}</span>
          </label>
        </div>
        <div class="infoValue short">
          <input type="text"  class="textEntry" name="quantity" id="quantity" maxlength="24" value='${parameters.quantity!quantity!""}'/>
        </div>
      </div>

      <div class="AMOUNTDIV">
        <div class="infoCaption">
          <label>
            <span class="PRICE">${uiLabelMap.PromotionPriceCaption}</span>
            <span class="DISC">${uiLabelMap.PromotionDiscCaption}</span>
            <span class="DISCPER">${uiLabelMap.PromotionDiscPercentCaption}</span>
            <span class="SHIPDISCPER">${uiLabelMap.PromotionShipDiscPercentCaption}</span>
            <span class="TAXDISCPER">${uiLabelMap.PromotionTaxDiscPercentCaption}</span>
          </label>
        </div>
        <div class="infoValue short">
          <input type="text"  class="textEntry" name="amount" id="amount" maxlength="24" value='${parameters.amount!amount!""}'/>
        </div>
      </div>

      <div class="ITEMDIV">
        <div class="infoCaption">
          <label>
            <span>${uiLabelMap.PromotionItemIdCaption}</span>
          </label>
        </div>
        <div class="infoValue short">
          <input type="text" readonly="readonly" class="textEntry" name="productId" id="productId" maxlength="20" value='${parameters.productId!productId!""}' onchange="javascript:changeColor('productId');"/>
          <input type="hidden" name="productName" id="productName" value="" />&nbsp;
          <a href="javascript:openLookup(document.${detailFormName!}.productId,document.${detailFormName!}.productName,'lookupProduct','500','700','center','true');" class="standardBtn secondary">${uiLabelMap.SelectBtn}</a>
        </div>
      </div>

    </div>
  </div>

  <div class="infoRow row promoActionCategory">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionActionCategoryCaption}</label>
      </div>
        <div class="infoValue" id="productPromoActionCategoryDiv">
          <input type="hidden" name="productPromoActionCategoryId" id="productPromoActionCategoryId" value="" />
          <input type="hidden" name="productPromoActionCategoryName" id="productPromoActionCategoryName" value="" onchange="addDivRow(productPromoActionCategoryObject);"/>

          <#if parameters.productPromoActionCategoryDataRows?exists>
            <#assign rowNo = 0/>
            <#assign productPromoActionCategoryDataRows = parameters.productPromoActionCategoryDataRows?number - 1/>
            <#if productPromoActionCategoryDataRows gt -1>
              <#list 0..productPromoActionCategoryDataRows as row>
                <#assign rowNo = row/>
                <#assign productPromoActionCategoryApplEnumId = request.getParameter("productPromoActionCategoryApplEnumId_${rowNo}")!/>
                <#assign productPromoActionCategoryApplEnum = delegator.findByPrimaryKey("Enumeration", {"enumId" : productPromoActionCategoryApplEnumId})/>
                <#assign productPromoActionCategoryId = request.getParameter("productPromoActionCategoryId_${rowNo}")!/>
                <#assign productCategory = delegator.findByPrimaryKey("ProductCategory", {"productCategoryId" : productPromoActionCategoryId})/>
                <div class="dataRow">
                  <div class="dataColumn operDataColumn">
                    ${productPromoActionCategoryApplEnum.description!productPromoActionCategoryApplEnum.enumId!}
                  </div><div class="dataColumn nameDataColumn">
                    ${productCategory.categoryName!""}
                  </div><div class="dataColumn actionDataColumn">
                    <a href="javascript:deleteDivRow('productPromoActionCategoryDiv', 'productPromoActionCategoryDataRows', ${rowNo})" class="standardBtn secondary">${uiLabelMap.RemoveBtn}</a>
                    <input type="hidden" name="productPromoActionCategoryApplEnumId_${rowNo}" id="productPromoActionCategoryApplEnumId_${rowNo}" value="${productPromoActionCategoryApplEnumId!}"/>
                    <input type="hidden" name="productPromoActionCategoryId_${rowNo}" id="productPromoActionCategoryId_${rowNo}" value="${productPromoActionCategoryId!}"/>
                  </div>
                </div>
              </#list>
            </#if>
            <input type="hidden" name="productPromoActionCategoryDataRows" id="productPromoActionCategoryDataRows" value="${parameters.productPromoActionCategoryDataRows?number!}"/>
          <#else>
            <#if productPromoActionCategoryList?exists && productPromoActionCategoryList?has_content>
              <#assign rowNo = 0/>
              <#list productPromoActionCategoryList as productPromoActionCategory>
                <div class="dataRow">
                  <div class="dataColumn operDataColumn">
                    <#assign productPromoActionCategoryApplEnum = delegator.findByPrimaryKey("Enumeration", {"enumId" : productPromoActionCategory.productPromoApplEnumId})/>
                    ${productPromoActionCategoryApplEnum.description!productPromoActionCategoryApplEnum.enumId!}
                  </div><div class="dataColumn nameDataColumn">
                    <#assign productCategory = delegator.findByPrimaryKey("ProductCategory", {"productCategoryId" : productPromoActionCategory.productCategoryId})/>
                    ${productCategory.categoryName!""}
                  </div><div class="dataColumn actionDataColumn">
                    <a href="javascript:deleteDivRow('productPromoActionCategoryDiv', 'productPromoActionCategoryDataRows', ${rowNo})" class="standardBtn secondary">${uiLabelMap.RemoveBtn}</a>
                    <input type="hidden" name="productPromoActionCategoryApplEnumId_${rowNo}" id="productPromoActionCategoryApplEnumId_${rowNo}" value="${productPromoActionCategory.productPromoApplEnumId!}"/>
                    <input type="hidden" name="productPromoActionCategoryId_${rowNo}" id="productPromoActionCategoryId_${rowNo}" value="${productPromoActionCategory.productCategoryId!}"/>
                  </div>
                </div>
                <#assign rowNo = rowNo+1/>
              </#list>
              <input type="hidden" name="productPromoActionCategoryDataRows" id="productPromoActionCategoryDataRows" value="${rowNo}"/>
            <#else>
              <input type="hidden" name="productPromoActionCategoryDataRows" id="productPromoActionCategoryDataRows" value="0"/>
            </#if>
          </#if>
          <div class="dataRow lastDataRow">
            <div class="dataColumn operDataColumn">
              <select name="productPromoActionCategoryApplEnumId" id="productPromoActionCategoryApplEnumId" class="short">
                <#if productPromoApplEnums?has_content>
                  <#list productPromoApplEnums as productPromoApplEnum>
                    <option value='${productPromoApplEnum.enumId!}'  <#if selectedProductPromoApplEnum == productPromoApplEnum.enumId >selected=selected</#if>>${productPromoApplEnum.description?default(productPromoApplEnum.enumId!)}</option>
                  </#list>
                </#if>
              </select>
            </div><div class="dataColumn nameDataColumn"></div><div class="dataColumn actionDataColumn">
              <a href="javascript:openLookup(document.${detailFormName!}.productPromoActionCategoryId,document.${detailFormName!}.productPromoActionCategoryName,'lookupCategory','500','700','center','true');" class="standardBtn secondary">${uiLabelMap.SelectBtn}</a>
            </div>
          </div>
        </div>
    </div>
  </div>

  <div class="infoRow row promoActionProduct">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionActionProductCaption}</label>
      </div>
      <div class="infoValue" id="productPromoActionProductDiv">
        <input type="hidden" name="productPromoActionProductId" id="productPromoActionProductId" value="" />
        <input type="hidden" name="productPromoActionProductName" id="productPromoActionProductName" value="" onchange="addDivRow(productPromoActionProductObject);"//>

        <#if parameters.productPromoActionProductDataRows?exists>
          <#assign rowNo = 0/>
          <#assign productPromoActionProductDataRows = parameters.productPromoActionProductDataRows?number - 1/>
          <#if productPromoActionProductDataRows gt -1>
            <#list 0..productPromoActionProductDataRows as row>
              <#assign rowNo = row/>
              <#assign productPromoActionProductApplEnumId = request.getParameter("productPromoActionProductApplEnumId_${rowNo}")!/>
              <#assign productPromoActionProductApplEnum = delegator.findByPrimaryKey("Enumeration", {"enumId" : productPromoActionProductApplEnumId})/>
              <#assign productPromoActionProductId = request.getParameter("productPromoActionProductId_${rowNo}")!/>
              <#assign product = delegator.findByPrimaryKey("Product", {"productId" : productPromoActionProductId})/>
              <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
              <div class="dataRow">
                <div class="dataColumn operDataColumn">
                  ${productPromoActionProductApplEnum.description!productPromoActionProductApplEnum.enumId!}
                </div><div class="dataColumn nameDataColumn">
                  ${productContentWrapper.get("PRODUCT_NAME")!""}
                </div><div class="dataColumn actionDataColumn">
                  <a href="javascript:deleteDivRow('productPromoActionProductDiv', 'productPromoCondProductDataRows', ${rowNo})" class="standardBtn secondary">${uiLabelMap.RemoveBtn}</a>
                  <input type="hidden" name="productPromoActionProductApplEnumId_${rowNo}" id="productPromoActionProductApplEnumId_${rowNo}" value="${productPromoActionProductApplEnumId!}"/>
                  <input type="hidden" name="productPromoActionProductId_${rowNo}" id="productPromoActionProductId_${rowNo}" value="${productPromoActionProductId!}"/>
                </div>
              </div>
            </#list>
          </#if>
          <input type="hidden" name="productPromoActionProductDataRows" id="productPromoActionProductDataRows" value="${parameters.productPromoActionProductDataRows?number!}"/>
        <#else>
          <#if productPromoActionProductList?exists && productPromoActionProductList?has_content>
            <#assign rowNo = 0/>
            <#list productPromoActionProductList as productPromoActionProduct>
              <div class="dataRow">
                <div class="dataColumn operDataColumn">
                  <#assign productPromoActionProductApplEnum = delegator.findByPrimaryKey("Enumeration", {"enumId" : productPromoActionProduct.productPromoApplEnumId})/>
                  ${productPromoActionProductApplEnum.description!productPromoActionProductApplEnum.enumId!}
                </div><div class="dataColumn nameDataColumn">
                  <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(productPromoActionProduct.getRelatedOne("Product"), request)!""/>
                  ${productContentWrapper.get("PRODUCT_NAME")!""}
                </div><div class="dataColumn actionDataColumn">
                  <a href="javascript:deleteDivRow('productPromoActionProductDiv', 'productPromoCondProductDataRows', ${rowNo})" class="standardBtn secondary">${uiLabelMap.RemoveBtn}</a>
                  <input type="hidden" name="productPromoActionProductApplEnumId_${rowNo}" id="productPromoActionProductApplEnumId_${rowNo}" value="${productPromoActionProduct.productPromoApplEnumId!}"/>
                  <input type="hidden" name="productPromoActionProductId_${rowNo}" id="productPromoActionProductId_${rowNo}" value="${productPromoActionProduct.productId!}"/>
                </div>
              </div>
              <#assign rowNo = rowNo+1/>
            </#list>
            <input type="hidden" name="productPromoActionProductDataRows" id="productPromoActionProductDataRows" value="${rowNo}"/>
          <#else>
            <input type="hidden" name="productPromoActionProductDataRows" id="productPromoActionProductDataRows" value="0"/>
          </#if>
        </#if>

          <div class="dataRow lastDataRow">
            <div class="dataColumn operDataColumn">
              <select name="productPromoActionProductApplEnumId" id="productPromoActionProductApplEnumId" class="short">
                <#if productPromoApplEnums?has_content>
                  <#list productPromoApplEnums as productPromoApplEnum>
                    <option value='${productPromoApplEnum.enumId!}'  <#if selectedProductPromoApplEnum == productPromoApplEnum.enumId >selected=selected</#if>>${productPromoApplEnum.description?default(productPromoApplEnum.enumId!)}</option>
                  </#list>
                </#if>
              </select>
            </div><div class="dataColumn nameDataColumn"></div><div class="dataColumn actionDataColumn">
              <a href="javascript:openLookup(document.${detailFormName!}.productPromoActionProductId,document.${detailFormName!}.productPromoActionProductName,'lookupProduct','500','700','center','true');" class="standardBtn secondary">${uiLabelMap.SelectBtn}</a>
            </div>
          </div>
      </div>
      
    </div>
  </div>
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
