<#if (quantity > 0)>
<div id="giftMessage">
  <#-- Hidden fields -->
  <input type="hidden" name="cartLineIndex" value="${parameters.cartLineIndex!}"/>
  <#list 1 .. quantity as count>
    <#-- giftMessageEntry section -->
      <div class="displayBox giftMessageEntry">
        <div class="header"><h2>${giftMessageBoxHeading!}${count}</h2></div>
        <div class="boxBody">
          <#if cartAttrMap?exists && cartAttrMap?has_content >
            <#assign countString = count! />
            <#if (count < 10)>
              <#assign countString = count?string("00")! />
            </#if>
            <#assign from = cartAttrMap.get("GIFT_MSG_FROM_" + countString)! />
            <#assign to = cartAttrMap.get("GIFT_MSG_TO_" + countString)! />
            <#assign giftMessageText = cartAttrMap.get("GIFT_MSG_TEXT_" + countString)! />
          </#if>
          <div class="infoRow row">
            <div class="infoEntry long">
              <div class="infoCaption">
                <label>${uiLabelMap.FromCaption}</label>
              </div>
            <div class="infoValue">
              <input class="large" type="text" name="from_${count}" id="from" value="${parameters.from!from!""}"/>
            </div>
          </div>
        </div>
        <div class="infoRow row">
          <div class="infoEntry long">
            <div class="infoCaption">
              <label>${uiLabelMap.ToCaption}</label>
            </div>
            <div class="infoValue">
              <input class="large" type="text" name="to_${count}" id="to" value="${parameters.to!to!""}"/>
            </div>
          </div>
        </div>
        <div class="infoRow">
          <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.GiftMessageLetUsHelpCaption}</label>
            </div>
            <div class="infoValue">
              <select name="giftMessageEnum_${count}" id="giftMessageEnum_${count}" onChange="javascript:giftMessageHelpCopy('${count}');">
                  <option value="">${uiLabelMap.SelectOneLabel}</option>
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#giftMessageTypes")}
              </select>
            </div>
          </div>
        </div>
        <div class="infoRow row">
          <div class="infoEntry long">
            <div class="infoCaption">
              <label>${uiLabelMap.GiftMessageTextCaption}</label>
            </div>
            <div class="infoValue">
              <textarea class="shortArea" name="giftMessageText_${count}" id="giftMessageText_${count}">${parameters.giftMessageText!giftMessageText!""}</textarea>
            </div>
          </div>
        </div>
      </div>
    </div>
    <#-- End of giftMessageEntry section -->
  </#list>
</div>
</#if>
