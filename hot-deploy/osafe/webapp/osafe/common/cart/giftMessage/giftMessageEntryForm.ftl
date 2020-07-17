<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class ="${request.getAttribute("attributeClass")!}">
<#if shoppingCart?exists && shoppingCart?has_content >
  <#if (quantity > 0)>
      <#-- Hidden fields -->
      <input type="hidden" name="cartLineIndex" value="${parameters.cartLineIndex!}"/>
       <#assign shipGroupSeqId = "_1" />
      <#list 1 .. quantity as count>
        <#-- giftMessageEntry section -->
        <div class="displayBox giftMessageEntry">
          <#-- Check Cart to see if any of these values are already populated -->
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

           <div class="entryForm giftMessageEntry">
	          <div class="entry fromName">
	            <label>${uiLabelMap.FromCaption}</label>
	            <div class="entryField">
	            	<#assign giftMessageFromNameVal = parameters.from!from!""/>
	              	<input type="text" class="characterLimit js_giftMessageFrom" maxlength="${GIFT_MESSAGE_FROM_MAX_CHAR!"50"}" onblur="restrictTextLength(this);" name="from_${count}" id="from" value="${StringUtil.wrapString(giftMessageFromNameVal)}"/><span class="js_textCounter textCounter"></span>
	            </div>
	          </div>
	          <div class="entry toName">
	            <label>${uiLabelMap.ToCaption}</label>
	            <div class="entryField">
	            	<#assign giftMessageToNameVal = parameters.to!to!""/>
 	              	<input type="text" class="characterLimit js_giftMessageTo" maxlength="${GIFT_MESSAGE_TO_MAX_CHAR!"50"}" onblur="restrictTextLength(this);" name="to_${count}" id="to" value="${StringUtil.wrapString(giftMessageToNameVal)}"/><span class="js_textCounter textCounter"></span>
 	            </div>
	          </div>
	          <div class="entry giftType">
	            <label>${uiLabelMap.GiftMessageLetUsHelpCaption}</label>
	            <div class="entryField">
		            <select name="giftMessageEnum_${count}" id="js_giftMessageEnum_${count}" onChange="javascript:giftMessageHelpCopy('${count}');">
		              <option value="">${uiLabelMap.SelectOneLabel}</option>
		              ${screens.render("component://osafe/widget/CommonScreens.xml#giftMessageTypes")}
		            </select>
		        </div>
	          </div>
	          <div class="entry giftMessage">
	            <label>${uiLabelMap.GiftMessageTextCaption}</label>
	            <div class="entryField">
	            	<#assign giftMessageTextVal = parameters.giftMessageText!giftMessageText!""/>
		            <textarea name="giftMessageText_${count}" id="js_giftMessageText_${count}" class="content characterLimit js_giftMessageText" cols="35" rows="5" maxlength="${GIFT_MESSAGE_TEXT_MAX_CHAR!"255"}">${StringUtil.wrapString(giftMessageTextVal)}</textarea>
		            <span class="js_textCounter textCounter"></span>
		        </div>
	          </div>
	       </div>
	    </div>
        <#-- End of giftMessageEntry section -->
      </#list>
  </#if>
</#if>
</div>

