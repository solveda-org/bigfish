<#if mode?has_content>
  <#if productPriceRule?has_content>

    <#assign productPriceRuleId = productPriceRule.productPriceRuleId!"" />
    <#assign ruleName = productPriceRule.ruleName!"" />
    <#assign description = productPriceRule.description!"" />

    <#if productPriceRule.fromDate?has_content>
        <#assign fromDate = (productPriceRule.fromDate)?string(entryDateTimeFormat)>
    </#if>
    <#if productPriceRule.thruDate?has_content>
        <#assign thruDate = (productPriceRule.thruDate)?string(entryDateTimeFormat)>
    </#if>

  </#if>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PriceRuleIdCaption}</label>
      </div>
      <div class="infoValue">
          <#if (mode?has_content && mode == "add")>
            <#if !parameters.productPriceRuleId?has_content>
               <#assign productPriceRuleSeqId = Static["com.osafe.util.OsafeAdminUtil"].getNextSeqId(delegator, "ProductPriceRule", "ProductPriceRule", "productPriceRuleId")!""/>
            </#if>
            <input type="hidden" name="productPriceRuleId" id="productPriceRuleId" maxlength="20" value="${parameters.productPriceRuleId!productPriceRuleSeqId!""}"/>${parameters.productPriceRuleId!productPriceRuleSeqId!""}
          <#elseif mode?has_content && mode == "edit">
            <input type="hidden" name="productPriceRuleId" value="${parameters.productPriceRuleId!productPriceRuleId!""}" />${parameters.productPriceRuleId!productPriceRuleId!""}
          </#if>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PriceRuleNameCaption}</label>
      </div>
      <div class="infoValue">
          <input class="medium" name="ruleName" type="text" id="ruleName" maxlength="100" value="${parameters.ruleName!ruleName!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PriceRuleDescCaption}</label>
      </div>
      <div class="infoValue">
          <textarea class="shortArea" name="description" cols="50" rows="5" maxlength="255">${parameters.description!description!""}</textarea>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
          <div class="infoCaption">
            <label>${uiLabelMap.PriceRuleActiveFromCaption}</label>
          </div>
          <div class="infoValue small">
            <div class="entryInput from">
                <input class="dateEntry" type="text" id="fromDate" name="fromDate" maxlength="40" value="${parameters.fromDate!fromDate!nowTimestamp?string(entryDateTimeFormat)!""}"/>
            </div>
          </div>
    </div>
    <div class="infoEntry long">
          <div class="infoCaption">
            <label class="extraSmallLabel">${uiLabelMap.PriceRuleActiveThruCaption}</label>
          </div>
          <div class="infoValue small">
            <div class="entryInput from">
                <input class="dateEntry" type="text" id="thruDate" name="thruDate" maxlength="40" value="${parameters.thruDate!thruDate!""}"/>
            </div>
          </div>
    </div>
  </div>

<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
