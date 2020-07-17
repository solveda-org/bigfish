<div class ="${request.getAttribute("attributeClass")!}">
<#if shoppingCart?exists && shoppingCart?has_content >
  <#if (quantity > 0)>
      <#-- Hidden fields -->
      <input type="hidden" name="cartLineIndex" value="${parameters.cartLineIndex!}"/>
       <#assign shipGroupSeqId = "_1" />
      <#list 1 .. quantity as count>
        <#-- giftMessageEntry section -->
        <div class="giftMessageEntry displayBox">
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

	          <div class="entry fromName">
	            <label>${uiLabelMap.FromCaption}</label>
	            <input type="text" maxlength="100" name="from_${count}" id="from" value="${parameters.from!from!""}"/>
	          </div>
	          <div class="entry toName">
	            <label>${uiLabelMap.ToCaption}</label>
	            <input type="text" maxlength="100" name="to_${count}" id="to" value="${parameters.to!to!""}"/>
	          </div>
	          <div class="entry giftType">
	            <label>${uiLabelMap.GiftMessageLetUsHelpCaption}</label>
	            <select name="giftMessageEnum_${count}" id="js_giftMessageEnum_${count}" onChange="javascript:giftMessageHelpCopy('${count}');">
	              <option value="">${uiLabelMap.SelectOneLabel}</option>
	              ${screens.render("component://osafe/widget/CommonScreens.xml#giftMessageTypes")}
	            </select>
	          </div>
	          <div class="entry giftMessage">
	            <label>${uiLabelMap.GiftMessageTextCaption}</label>
	            <div class="entryField">
		            <textarea name="giftMessageText_${count}" id="js_giftMessageText_${count}" class="content" id="js_content" cols="35" rows="5">${parameters.giftMessageText!giftMessageText!""}</textarea>
		            <div class="counter">
		              <span class="js_textCounter textCounter" id="js_textCounter"></span>
		            </div>
		        </div>
	          </div>
	        </div>
        <#-- End of giftMessageEntry section -->
      </#list>
  </#if>
</#if>
</div>

