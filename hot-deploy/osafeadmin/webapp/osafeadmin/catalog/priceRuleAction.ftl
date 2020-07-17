<#if mode?has_content>
  <#if productPriceAction?has_content>
    <#assign productPriceActionSeqId = productPriceAction.productPriceActionSeqId!"" />
    <#assign productPriceActionTypeId = productPriceAction.productPriceActionTypeId!"" />
    <#assign amount = productPriceAction.amount!"" />
  </#if> 

  <#assign selectedProductPriceActionTypeId = parameters.productPriceActionTypeId!productPriceActionTypeId!""/>
  <input type="hidden" name="productPriceActionSeqId" value="${parameters.productPriceActionSeqId!productPriceActionSeqId!""}" />

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PriceRuleActionCaption}</label>
      </div>
      <div class="infoValue">
        <select name="productPriceActionTypeId" id="productPriceActionTypeId" class="extraSmall">
          <#if productPriceActionTypes?has_content>
            <#list productPriceActionTypes as productPriceActionType>
              <option value='${productPriceActionType.productPriceActionTypeId!}'  <#if selectedProductPriceActionTypeId == productPriceActionType.productPriceActionTypeId >selected=selected</#if>>${productPriceActionType.description?default(productPriceActionType.productPriceActionTypeId!)}</option>
            </#list>
          </#if>
        </select>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PriceRuleValueCaption}</label>
      </div>
      <div class="infoValue">
        <input type="text"  class="textEntry" name="amount" id="amount" maxlength="18" value='${parameters.amount!amount!""}'/>
      </div>
    </div>
  </div>

</#if>