<!-- start promotionsSearch.ftl -->

  <div class="entryRow">
    <div class="entry short">
      <label>${uiLabelMap.PromoNameCaption}</label>
      <div class="entryInput">
        <input class="nameEntry" type="text" id="srchPromoName" name="srchPromoName" value="${parameters.srchPromoName!""}"/>
      </div>
    </div>
    <div class="entry medium">
      <label class="largeLabel">${uiLabelMap.PromoDescriptionCaption}</label>
      <div class="entryInput">
        <input class="nameEntry" type="text" id="srchPromoDesc" name="srchPromoDesc" maxlength="40" value="${parameters.srchPromoDesc!""}"/>
      </div>
    </div>
  </div>

  <div class="entryRow">
    <div class="entry short">
      <label>${uiLabelMap.PromoCodeCaption}</label>
      <div class="entryInput">
        <input class="nameEntry" type="text" id="srchPromoCode" name="srchPromoCode" maxlength="40" value="${parameters.srchPromoCode!srchPromoCode!""}"/>
      </div>
    </div>
  </div>

  <div class="entryRow">
    <div class="entry long">
      <#assign intiCb = "${initializedCB}"/>
      <label>${uiLabelMap.StatusCaption}</label>
      <div class="entryInput checkbox extraSmall">
        <input type="checkbox" class="checkBoxEntry" name="srchall" id="srchall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','srch')" <#if parameters.srchall?has_content>checked</#if> />${uiLabelMap.AllLabel}
        <input class="srchPromoActive" type="checkbox" id="srchPromoActive" name="srchPromoActive" value="Y" <#if parameters.srchPromoActive?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.ActiveLabel}
        <input class="srchPromoExpired" type="checkbox" id="srchPromoExpired" name="srchPromoExpired" value="Y" <#if parameters.srchPromoExpired?has_content>checked</#if>/>${uiLabelMap.InActiveLabel}
      </div>
    </div>
  </div>
<!-- end promotionsSearch.ftl -->

