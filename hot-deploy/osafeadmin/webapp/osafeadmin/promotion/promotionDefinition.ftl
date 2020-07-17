<#if mode?has_content>
  <#if productPromo?has_content>

    <#assign productPromoId = productPromo.productPromoId!"" />
    <#assign promoName = productPromo.promoName!"" />
    <#assign promoText = productPromo.promoText!"" />
    <#assign requireCode = productPromo.requireCode!"" />
    <#assign useLimitPerOrder = productPromo.useLimitPerOrder!"" />
    <#assign useLimitPerCustomer = productPromo.useLimitPerCustomer!"" />
    <#assign useLimitPerPromotion = productPromo.useLimitPerPromotion!"" />
    <#assign userEntered = productPromo.userEntered!"" />
    <#assign showToCustomer = productPromo.showToCustomer!"" />

    <#if productStorePromoAppl?has_content>
      <#if productStorePromoAppl.fromDate?has_content>
        <#assign productStorePromoApplFromDate = productStorePromoAppl.fromDate>
        <#assign fromDate = (productStorePromoAppl.fromDate)?string(preferredDateFormat)>
      </#if>
      <#if productStorePromoAppl.thruDate?has_content>
        <#assign thruDate = (productStorePromoAppl.thruDate)?string(preferredDateFormat)>
      </#if>
    </#if>

  </#if>
  <#if productPromoRule?has_content>
    <#assign productPromoRuleId = productPromoRule.productPromoRuleId!"" />
    <#assign ruleName = productPromoRule.ruleName!"" />
  </#if> 

  <#assign isPromotionDetail = true>
  <#if productPromoCodeId?has_content || productPromotionId?has_content>
    <#assign isPromotionDetail = false>
  </#if> 

  <input type="hidden" name="productStorePromoApplFromDate" value="${parameters.productStorePromoApplFromDate!productStorePromoApplFromDate!""}" />
  <input type="hidden" name="productPromoRuleId" value="${parameters.productPromoRuleId!productPromoRuleId!""}" />
  <input type="hidden" name="ruleName" value="${parameters.ruleName!ruleName!""}" />
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionIdCaption}</label>
      </div>
      <div class="infoValue">
        <#if isPromotionDetail>
          <#if (mode?has_content && mode == "add")>
            <#if !parameters.productPromoId?has_content>
              <#assign productPromoSeqId = delegator.getNextSeqId("ProductPromo")/>
            </#if>
            <input type="hidden" name="productPromoId" id="productPromoId" maxlength="20" value="${parameters.productPromoId!productPromoSeqId!""}"/>${parameters.productPromoId!productPromoSeqId!""}
          <#elseif mode?has_content && mode == "edit">
            <input type="hidden" name="productPromoId" value="${parameters.productPromoId!productPromoId!""}" />${productPromoId!""}
          </#if>
        <#else>
          <input type="hidden" name="productPromoId" value="${parameters.productPromoId!productPromoId!""}" />${productPromoId!""}
        </#if>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionNameCaption}</label>
      </div>
      <div class="infoValue">
        <#if isPromotionDetail>
          <input class="medium" name="promoName" type="text" id="promoName" maxlength="20" value="${parameters.promoName!promoName!""}"/>
        <#else>
          ${promoName!""}
        </#if>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionDescCaption}</label>
      </div>
      <div class="infoValue">
        <#if isPromotionDetail>
          <textarea class="shortArea" name="promoText" cols="50" rows="5" maxlength="255">${parameters.promoText!promoText!""}</textarea>
        <#else>
          ${promoText!""}
        </#if>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionRequireCodeCaption}</label>
      </div>
      <#if isPromotionDetail>
        <div class="entry checkbox medium">
          <input class="checkBoxEntry" type="radio" id="requireCodeNo" name="requireCode"  value="N" <#if ((parameters.requireCode?exists && parameters.requireCode?string == "N") || (requireCode?exists && requireCode?string == "N"))>checked="checked"</#if>/>${uiLabelMap.RequireCodeAutoLabel}
          <input class="checkBoxEntry" type="radio" id="requireCodeYes" name="requireCode" value="Y" <#if ((parameters.requireCode?exists && parameters.requireCode?string == "Y") || (requireCode?exists && requireCode?string == "Y") || (!parameters.requireCode?exists && mode?has_content && mode == "add"))>checked="checked"</#if>/>${uiLabelMap.RequireCodeManualLabel}
        </div>
      <#else>
        <div class="entryInput checkbox medium">
          <input class="checkBoxEntry" type="radio" id="requireCodeNo" name="requireCode"  value="N" disabled="disabled" <#if ((parameters.requireCode?exists && parameters.requireCode?string == "N") || (requireCode?exists && requireCode?string == "N"))>checked="checked"</#if>/>${uiLabelMap.RequireCodeAutoLabel}
          <input class="checkBoxEntry" type="radio" id="requireCodeYes" name="requireCode" value="Y" disabled="disabled" <#if ((parameters.requireCode?exists && parameters.requireCode?string == "Y") || (requireCode?exists && requireCode?string == "Y") || (!parameters.requireCode?exists && mode?has_content && mode == "add"))>checked="checked"</#if>/>${uiLabelMap.RequireCodeManualLabel}
        </div>
      </#if>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionLimitPerOrderCaption}</label>
      </div>
      <div class="infoValue small">
        <#if isPromotionDetail>
          <input type="text" class="textEntry" name="useLimitPerOrder" maxlength="20" value='${parameters.useLimitPerOrder!useLimitPerOrder!""}'/>
        <#else>
          ${useLimitPerOrder!""}
        </#if>
      </div>

      <div>
        <label>${uiLabelMap.PromotionLimitPerCustomerCaption}</label>
      </div>
      <div class="infoValue small">
        <#if isPromotionDetail>
          <input type="text"  class="textEntry" name="useLimitPerCustomer" maxlength="20" value='${parameters.useLimitPerCustomer!useLimitPerCustomer!""}'/>
        <#else>
          ${useLimitPerCustomer!""}
        </#if>
      </div>

      <div>
        <label>${uiLabelMap.PromotionLimitPerPromotionCaption}</label>
      </div>
      <div class="infoValue small">
        <#if isPromotionDetail>
          <input type="text"  class="textEntry" name="useLimitPerPromotion" maxlength="20" value='${parameters.useLimitPerPromotion!useLimitPerPromotion!""}'/>
        <#else>
          ${useLimitPerCustomer!""}
        </#if>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionUserEnterCaption}</label>
      </div>
      <div class="infoValue small">
        <#if isPromotionDetail>
          <input type="hidden" name="userEntered" value="${parameters.userEntered!userEntered!"Y"}" />${userEntered!"Y"}
        <#else>
          ${userEntered!""}
        </#if>
      </div>

      <div>
        <label>${uiLabelMap.PromotionShowToCustomerCaption}</label>
      </div>
      <div class="infoValue small">
        <#if isPromotionDetail>
          <input type="hidden" name="showToCustomer" value="${parameters.showToCustomer!showToCustomer!"Y"}" />${showToCustomer!"Y"}
        <#else>
          ${showToCustomer!""}
        </#if>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PromotionActiveFromCaption}</label>
      </div>
      <div class="infoValue small">
        <div class="entryInput from">
          <#if isPromotionDetail>
            <input class="dateEntry" type="text" id="fromDate" name="fromDate" maxlength="40" value="${parameters.fromDate!fromDate!""}"/>
            <#else>
              ${fromDate!""}
          </#if>
        </div>
      </div>

      <div>
        <label>${uiLabelMap.PromotionActiveThruCaption}</label>
      </div>
      <div class="infoValue small">
        <div class="entryInput from">
          <#if isPromotionDetail>
            <input class="dateEntry" type="text" id="thruDate" name="thruDate" maxlength="40" value="${parameters.thruDate!thruDate!""}"/>
            <#else>
            ${thruDate!""}
          </#if>
        </div>
      </div>
    </div>
  </div>

<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>
