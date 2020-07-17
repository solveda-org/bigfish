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
             <#assign countString = "" + count />
            <#assign from = ""/>
            <#assign to = "" />
            <#assign giftMessageText = ""/>
             <#list cartAttrMap.keySet() as attrName>
	            <#if attrName.startsWith("GIFT_MSG_FROM_" + countString)>
	                 <#assign from = cartAttrMap.get(attrName)! />
	            </#if>
	         </#list>
             <#list cartAttrMap.keySet() as attrName>
	            <#if attrName.startsWith("GIFT_MSG_TO_" + countString)>
	                 <#assign to = cartAttrMap.get(attrName)! />
	            </#if>
	         </#list>
             <#list cartAttrMap.keySet() as attrName>
	            <#if attrName.startsWith("GIFT_MSG_TEXT_" + countString)>
	                 <#assign giftMessageText = cartAttrMap.get(attrName)! />
	            </#if>
	         </#list>
          </#if>
          <div class="infoRow row">
            <div class="infoEntry long">
              <div class="infoCaption">
                <label>${uiLabelMap.FromCaption}</label>
              </div>
            <div class="infoValue">
              <input class="large characterLimit" maxlength="${GIFT_MESSAGE_FROM_MAX_CHAR!"50"}" onblur="restrictTextLength(this);" type="text" name="from_${count}" id="from" value="${parameters.from!from!""}"/>
              <span class="js_textCounter textCounter"></span>
            </div>
          </div>
        </div>
        <div class="infoRow row">
          <div class="infoEntry long">
            <div class="infoCaption">
              <label>${uiLabelMap.ToCaption}</label>
            </div>
            <div class="infoValue">
              <input class="large characterLimit" maxlength="${GIFT_MESSAGE_TO_MAX_CHAR!"50"}" onblur="restrictTextLength(this);" type="text" name="to_${count}" id="to" value="${parameters.to!to!""}"/>
              <span class="js_textCounter textCounter"></span>
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
              <textarea class="shortArea characterLimit" name="giftMessageText_${count}" id="giftMessageText_${count}" maxlength="${GIFT_MESSAGE_TEXT_MAX_CHAR!"255"}">${parameters.giftMessageText!giftMessageText!""}</textarea>
              <span class="js_textCounter textCounter"></span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <#-- End of giftMessageEntry section -->
  </#list>
</div>
</#if>
