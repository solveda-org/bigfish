<script type="text/javascript">
    var productPromoCondCategoryObject = new Object();
    productPromoCondCategoryObject.divId = "productPromoCondCategoryDiv";
    productPromoCondCategoryObject.dataRows = "productPromoCondCategoryDataRows";
    productPromoCondCategoryObject.processTypeSelectId = "productPromoCondCategoryApplEnumId";
    productPromoCondCategoryObject.processTypeHiddenId1 = "productPromoCondCategoryId";
    productPromoCondCategoryObject.processTypeHiddenId2 = "productPromoCondCategoryName";
    productPromoCondCategoryObject.newTypeHiddenNamePrefix1 = "productPromoCondCategoryApplEnumId_";
    productPromoCondCategoryObject.newTypeHiddenNamePrefix2 = "productPromoCondCategoryId_";

    var productPromoCondProductObject = new Object();
    productPromoCondProductObject.divId = "productPromoCondProductDiv";
    productPromoCondProductObject.dataRows = "productPromoCondProductDataRows";
    productPromoCondProductObject.processTypeSelectId = "productPromoCondProductApplEnumId";
    productPromoCondProductObject.processTypeHiddenId1 = "productPromoCondProductId";
    productPromoCondProductObject.processTypeHiddenId2 = "productPromoCondProductName";
    productPromoCondProductObject.newTypeHiddenNamePrefix1 = "productPromoCondProductApplEnumId_";
    productPromoCondProductObject.newTypeHiddenNamePrefix2 = "productPromoCondProductId_";

</script>
<#if mode?has_content>
  <#if productPromoCond?has_content>
    <#assign condValue = productPromoCond.condValue!"" />
    <#assign otherValue = productPromoCond.otherValue!"" />
    <#assign inputParamEnumId = productPromoCond.inputParamEnumId!"" />
    <#assign operatorEnumId = productPromoCond.operatorEnumId!"" />
    <#assign productPromoCondSeqId = productPromoCond.productPromoCondSeqId!"" />
  </#if>

  <#assign selectedInputParamEnum = parameters.inputParamEnumId!inputParamEnumId!selectedInputParamEnum!""/>
  <#assign selectedCondOperEnum = parameters.operatorEnumId!operatorEnumId!selectedCondOperEnum!""/>
  <#assign selectedShippingMethod = parameters.otherValue!otherValue!""/>

  <input type="hidden" name="productPromoCondSeqId" value="${parameters.productPromoCondSeqId!productPromoCondSeqId!""}" />

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionCondCaption}</label>
      </div>
      <div class="infoValue">
        <select name="inputParamEnumId" id="inputParamEnumId" class="small">
          <#if inputParamEnums?has_content>
            <#list inputParamEnums as inputParamEnum>
              <option value='${inputParamEnum.enumId!}' <#if selectedInputParamEnum == inputParamEnum.enumId >selected=selected</#if>>${inputParamEnum.description?default(inputParamEnum.enumId!)}</option>
            </#list>
          </#if>
        </select>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionOperCaption}</label>
      </div>
      <div class="infoValue">
        <select name="operatorEnumId" id="operatorEnumId" class="small">
          <#if condOperEnums?has_content>
            <#list condOperEnums as condOperEnum>
              <option value='${condOperEnum.enumId!}' <#if selectedCondOperEnum == condOperEnum.enumId >selected=selected</#if>>${condOperEnum.description?default(condOperEnum.enumId!)}</option>
            </#list>
          </#if>
        </select>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionCondValueCaption}</label>
      </div>
      <div class="infoValue small">
        <input type="text"  class="textEntry" name="condValue" maxlength="255" value='${parameters.condValue!condValue!""}'/>
      </div>
    </div>
  </div>

  <div class="infoRow row promoConditionCategory">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionCondCategoryCaption}</label>
      </div>
        <div class="infoValue" id="productPromoCondCategoryDiv">
          <input type="hidden" name="productPromoCondCategoryId" id="productPromoCondCategoryId" value="" />
          <input type="hidden" name="productPromoCondCategoryName" id="productPromoCondCategoryName" value="" onchange="addDivRow(productPromoCondCategoryObject);" />

          <#if parameters.productPromoCondCategoryDataRows?exists>
            <#assign rowNo = 0/>
            <#assign productPromoCondCategoryDataRows = parameters.productPromoCondCategoryDataRows?number - 1/>
            <#if productPromoCondCategoryDataRows gt -1>
              <#list 0..productPromoCondCategoryDataRows as row>
                <#assign rowNo = row/>
                <#assign productPromoCondCategoryApplEnumId = request.getParameter("productPromoCondCategoryApplEnumId_${rowNo}")!/>
                <#assign productPromoCondCategoryApplEnum = delegator.findByPrimaryKey("Enumeration", {"enumId" : productPromoCondCategoryApplEnumId})/>
                <#assign productPromoCondCategoryId = request.getParameter("productPromoCondCategoryId_${rowNo}")!/>
                <#assign productCategory = delegator.findByPrimaryKey("ProductCategory", {"productCategoryId" : productPromoCondCategoryId})/>
                <div class="dataRow">
                  <div class="dataColumn operDataColumn">
                    ${productPromoCondCategoryApplEnum.description!productPromoCondCategoryApplEnum.enumId!}
                  </div><div class="dataColumn nameDataColumn">
                    ${productCategory.categoryName!""}
                  </div><div class="dataColumn actionDataColumn">
                    <a href="javascript:deleteDivRow('productPromoCondCategoryDiv', 'productPromoCondCategoryDataRows', ${rowNo})" class="standardBtn secondary">${uiLabelMap.RemoveBtn}</a>
                    <input type="hidden" name="productPromoCondCategoryApplEnumId_${rowNo}" id="productPromoCondCategoryApplEnumId_${rowNo}" value="${productPromoCondCategoryApplEnumId!}"/>
                    <input type="hidden" name="productPromoCondCategoryId_${rowNo}" id="productPromoCondCategoryId_${rowNo}" value="${productPromoCondCategoryId!}"/>
                  </div>
                </div>
              </#list>
            </#if>
            <input type="hidden" name="productPromoCondCategoryDataRows" id="productPromoCondCategoryDataRows" value="${parameters.productPromoCondCategoryDataRows?number!}"/>
          <#else>
            <#if productPromoCondCategoryList?exists && productPromoCondCategoryList?has_content>
              <#assign rowNo = 0/>
              <#list productPromoCondCategoryList as productPromoCondCategory>
                <div class="dataRow">
                  <div class="dataColumn operDataColumn">
                    <#assign productPromoCondCategoryApplEnum = delegator.findByPrimaryKey("Enumeration", {"enumId" : productPromoCondCategory.productPromoApplEnumId})/>
                    ${productPromoCondCategoryApplEnum.description!productPromoCondCategoryApplEnum.enumId!}
                  </div><div class="dataColumn nameDataColumn">
                    <#assign productCategory = delegator.findByPrimaryKey("ProductCategory", {"productCategoryId" : productPromoCondCategory.productCategoryId})/>
                    ${productCategory.categoryName!""}
                  </div><div class="dataColumn actionDataColumn">
                    <a href="javascript:deleteDivRow('productPromoCondCategoryDiv', 'productPromoCondCategoryDataRows', ${rowNo})" class="standardBtn secondary">${uiLabelMap.RemoveBtn}</a>
                    <input type="hidden" name="productPromoCondCategoryApplEnumId_${rowNo}" id="productPromoCondCategoryApplEnumId_${rowNo}" value="${productPromoCondCategory.productPromoApplEnumId!}"/>
                    <input type="hidden" name="productPromoCondCategoryId_${rowNo}" id="productPromoCondCategoryId_${rowNo}" value="${productPromoCondCategory.productCategoryId!}"/>
                  </div>
                </div>
                <#assign rowNo = rowNo+1/>
              </#list>
              <input type="hidden" name="productPromoCondCategoryDataRows" id="productPromoCondCategoryDataRows" value="${rowNo}"/>
            <#else>
              <input type="hidden" name="productPromoCondCategoryDataRows" id="productPromoCondCategoryDataRows" value="0"/>
            </#if>
          </#if>

          <div class="dataRow lastDataRow">
            <div class="dataColumn operDataColumn">
              <select name="productPromoCondCategoryApplEnumId" id="productPromoCondCategoryApplEnumId" class="short">
                <#if productPromoApplEnums?has_content>
                  <#list productPromoApplEnums as productPromoApplEnum>
                    <option value='${productPromoApplEnum.enumId!}'  <#if selectedProductPromoApplEnum == productPromoApplEnum.enumId >selected=selected</#if>>${productPromoApplEnum.description?default(productPromoApplEnum.enumId!)}</option>
                  </#list>
                </#if>
              </select>
            </div><div class="dataColumn nameDataColumn"></div><div class="dataColumn actionDataColumn">
              <a href="javascript:openLookup(document.${detailFormName!}.productPromoCondCategoryId,document.${detailFormName!}.productPromoCondCategoryName,'lookupCategory','500','700','center','true');" class="standardBtn secondary">${uiLabelMap.SelectBtn}</a>
            </div>
          </div>
        </div>
    </div>
  </div>

  <div class="infoRow row promoConditionProduct">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionCondProductCaption}</label>
      </div>
      <div class="infoValue" id="productPromoCondProductDiv">
        <input type="hidden" name="productPromoCondProductId" id="productPromoCondProductId" value="" />
        <input type="hidden" name="productPromoCondProductName" id="productPromoCondProductName" value="" onchange="addDivRow(productPromoCondProductObject);"/>

        <#if parameters.productPromoCondProductDataRows?exists>
          <#assign rowNo = 0/>
          <#assign productPromoCondProductDataRows = parameters.productPromoCondProductDataRows?number - 1/>
          <#if productPromoCondProductDataRows gt -1>
            <#list 0..productPromoCondProductDataRows as row>
              <#assign rowNo = row/>
              <#assign productPromoCondProductApplEnumId = request.getParameter("productPromoCondProductApplEnumId_${rowNo}")!/>
              <#assign productPromoCondProductApplEnum = delegator.findByPrimaryKey("Enumeration", {"enumId" : productPromoCondProductApplEnumId})/>
              <#assign productPromoCondProductId = request.getParameter("productPromoCondProductId_${rowNo}")!/>
              <#assign product = delegator.findByPrimaryKey("Product", {"productId" : productPromoCondProductId})/>
              <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
              <div class="dataRow">
                <div class="dataColumn operDataColumn">
                  ${productPromoCondProductApplEnum.description!productPromoCondProductApplEnum.enumId!}
                </div><div class="dataColumn nameDataColumn">
                  ${productContentWrapper.get("PRODUCT_NAME")!""}
                </div><div class="dataColumn actionDataColumn">
                  <a href="javascript:deleteDivRow('productPromoCondProductDiv', 'productPromoCondProductDataRows', ${rowNo})" class="standardBtn secondary">${uiLabelMap.RemoveBtn}</a>
                  <input type="hidden" name="productPromoCondProductApplEnumId_${rowNo}" id="productPromoCondProductApplEnumId_${rowNo}" value="${productPromoCondProductApplEnumId!}"/>
                  <input type="hidden" name="productPromoCondProductId_${rowNo}" id="productPromoCondProductId_${rowNo}" value="${productPromoCondProductId!}"/>
                </div>
              </div>
            </#list>
          </#if>
          <input type="hidden" name="productPromoCondProductDataRows" id="productPromoCondProductDataRows" value="${parameters.productPromoCondProductDataRows?number!}"/>
        <#else>
          <#if productPromoCondProductList?exists && productPromoCondProductList?has_content>
            <#assign rowNo = 0/>
            <#list productPromoCondProductList as productPromoCondProduct>
              <div class="dataRow">
                <div class="dataColumn operDataColumn">
                  <#assign productPromoCondProductApplEnum = delegator.findByPrimaryKey("Enumeration", {"enumId" : productPromoCondProduct.productPromoApplEnumId})/>
                  ${productPromoCondProductApplEnum.description!productPromoCondProductApplEnum.enumId!}
                </div><div class="dataColumn nameDataColumn">
                  <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(productPromoCondProduct.getRelatedOne("Product"), request)!""/>
                  ${productContentWrapper.get("PRODUCT_NAME")!""}
                </div><div class="dataColumn actionDataColumn">
                  <a href="javascript:deleteDivRow('productPromoCondProductDiv', 'productPromoCondCategoryDataRows', ${rowNo})" class="standardBtn secondary">${uiLabelMap.RemoveBtn}</a>
                  <input type="hidden" name="productPromoCondProductApplEnumId_${rowNo}" id="productPromoCondProductApplEnumId_${rowNo}" value="${productPromoCondProduct.productPromoApplEnumId!}"/>
                  <input type="hidden" name="productPromoCondProductId_${rowNo}" id="productPromoCondProductId_${rowNo}" value="${productPromoCondProduct.productId!}"/>
                </div>
              </div>
              <#assign rowNo = rowNo+1/>
            </#list>
            <input type="hidden" name="productPromoCondProductDataRows" id="productPromoCondProductDataRows" value="${rowNo}"/>
          <#else>
            <input type="hidden" name="productPromoCondProductDataRows" id="productPromoCondProductDataRows" value="0"/>
          </#if>
        </#if>

          <div class="dataRow lastDataRow">
            <div class="dataColumn operDataColumn">
              <select name="productPromoCondProductApplEnumId" id="productPromoCondProductApplEnumId" class="short">
                <#if productPromoApplEnums?has_content>
                  <#list productPromoApplEnums as productPromoApplEnum>
                    <option value='${productPromoApplEnum.enumId!}'  <#if selectedProductPromoApplEnum == productPromoApplEnum.enumId >selected=selected</#if>>${productPromoApplEnum.description?default(productPromoApplEnum.enumId!)}</option>
                  </#list>
                </#if>
              </select>
            </div><div class="dataColumn nameDataColumn"></div><div class="dataColumn actionDataColumn">
              <a href="javascript:openLookup(document.${detailFormName!}.productPromoCondProductId,document.${detailFormName!}.productPromoCondProductName,'lookupProduct','500','700','center','true');" class="standardBtn secondary">${uiLabelMap.SelectBtn}</a>
            </div>
          </div>
      </div>
      
    </div>
  </div>

  <div class="infoRow row promoConditionShippingMethod">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionCondShippingMethodCaption}</label>
      </div>
      <div class="infoValue small">
        <#if productStoreShipmentMethList?has_content>
          <select name="otherValue" id="otherValue" class="small">
            <#list productStoreShipmentMethList as carrierShipmentMethod>
              <#assign shippingMethod = "${carrierShipmentMethod.partyId?if_exists}@${carrierShipmentMethod.shipmentMethodTypeId?if_exists}">
              <#assign carrierPartyGroupName = ""/>
              <#if carrierShipmentMethod.partyId != "_NA_">
                <#assign findCarrierShipMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", carrierShipmentMethod.shipmentMethodTypeId, "partyId", carrierShipmentMethod.partyId,"roleTypeId" ,"CARRIER")>
                <#assign carrierShipMethod = delegator.findByPrimaryKey("CarrierShipmentMethod", findCarrierShipMethodMap)>
                <#assign carrierParty = carrierShipMethod.getRelatedOne("Party")/>
                <#assign carrierPartyGroup = carrierParty.getRelatedOne("PartyGroup")/>
                <#assign carrierPartyGroupName = carrierPartyGroup.groupName?trim/>
              </#if>
              <option value='${shippingMethod}' <#if selectedShippingMethod?has_content && selectedShippingMethod.equals(shippingMethod)>selected=selected</#if>><#if carrierPartyGroupName?has_content>${carrierPartyGroupName?if_exists}&nbsp;<#else>${carrierShipmentMethod.partyId?if_exists}&nbsp;</#if>${carrierShipmentMethod.description?if_exists}</option>
            </#list>
          </select>
        </#if>
      </div>
    </div>
  </div>
<#else>
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>