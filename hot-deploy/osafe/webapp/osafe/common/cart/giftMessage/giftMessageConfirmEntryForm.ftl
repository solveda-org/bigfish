<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class ="${request.getAttribute("attributeClass")!}">
  <#if (quantity > 0)>
      <#-- Hidden fields -->
      <input type="hidden" name="orderId" value="${parameters.orderId!}"/>
      <input type="hidden" name="orderItemSeqId" value="${parameters.orderItemSeqId!}"/>
      <input type="hidden" name="shipGroupSeqId" value="${parameters.shipGroupSeqId!}"/>
      
      <#assign alreadyProcessedOrderItemAttributes = Static["javolution.util.FastList"].newInstance()/>
      <#list 1 .. quantity as count>
        <#-- giftMessageEntry section -->
        <div class="giftMessageEntry displayBox">
          <#-- Check Cart to see if any of these values are already populated -->
          <#if cartAttrMap?exists && cartAttrMap?has_content >
             <#assign countString = "" + count />
             <#assign from = ""/>
             <#assign to = "" />
             <#assign giftMessageText = ""/>
             
             <#assign seqId = ""/>
             
             <#list cartAttrMap.keySet() as attrName>
                 <#if !seqId?has_content>
		             <#if attrName.startsWith("GIFT_MSG_FROM_")>
			             <#assign iShipId = attrName.lastIndexOf("_")/>
			             <#if (iShipId > -1) && (attrName.substring(iShipId+1) == shipGroupSeqId )>
			                 <#assign seqId = attrName.substring("GIFT_MSG_FROM_"?length, iShipId)!""/>
			             </#if>
			             <#if alreadyProcessedOrderItemAttributes.contains(seqId)>
			                 <#assign seqId = ""/>
			             </#if>
			         </#if>
		         </#if>
		         <#if !seqId?has_content>
		             <#if attrName.startsWith("GIFT_MSG_TO_")>
			             <#assign iShipId = attrName.lastIndexOf("_")/>
			             <#if (iShipId > -1) && (attrName.substring(iShipId+1) == shipGroupSeqId)>
			                 <#assign seqId = attrName.substring("GIFT_MSG_TO_"?length, iShipId)!""/>
			             </#if>
			             <#if alreadyProcessedOrderItemAttributes.contains(seqId)>
			                 <#assign seqId = ""/>
			             </#if>
			         </#if>
		         </#if>
		         <#if !seqId?has_content>
		             <#if attrName.startsWith("GIFT_MSG_TEXT_")>
			             <#assign iShipId = attrName.lastIndexOf("_")/>
			             <#if (iShipId > -1) && (attrName.substring(iShipId+1).equals(shipGroupSeqId))>
			                 <#assign seqId = attrName.substring("GIFT_MSG_TEXT_"?length, iShipId)!""/>
			             </#if>
			             <#if alreadyProcessedOrderItemAttributes.contains(seqId)>
			                 <#assign seqId = ""/>
			             </#if>
			         </#if>
		         </#if>
		     </#list>
		     
		     <#assign changed = alreadyProcessedOrderItemAttributes.add(seqId)/>
		     
		     <#list cartAttrMap.keySet() as attrName>    
		         <#if attrName.startsWith("GIFT_MSG_FROM_")>
		             <#assign from = cartAttrMap.get("GIFT_MSG_FROM_"+seqId+"_"+shipGroupSeqId)! />
		         </#if>    
		         <#if attrName.startsWith("GIFT_MSG_TO_")>
		             <#assign to = cartAttrMap.get("GIFT_MSG_TO_"+seqId+"_"+shipGroupSeqId)! />
		         </#if>     
		         <#if attrName.startsWith("GIFT_MSG_TEXT_")>
		             <#assign giftMessageText = cartAttrMap.get("GIFT_MSG_TEXT_"+seqId+"_"+shipGroupSeqId)! />
		         </#if>
	         </#list>
          </#if>

	          <div class="entry fromName">
	            <label>${uiLabelMap.FromCaption}</label>
	            <#assign giftMessageFromNameVal = parameters.from!from!""/>
	            <input type="text" class="characterLimit js_giftMessageFrom" maxlength="${GIFT_MESSAGE_FROM_MAX_CHAR!"50"}" onblur="restrictTextLength(this);" name="from_${count}" id="from" value="${StringUtil.wrapString(giftMessageFromNameVal)}"/><span class="js_textCounter textCounter"></span>
	          </div>
	          <div class="entry toName">
	            <label>${uiLabelMap.ToCaption}</label>
	            <#assign giftMessageToNameVal = parameters.to!to!""/>
	            <input type="text" class="characterLimit js_giftMessageTo" maxlength="${GIFT_MESSAGE_TO_MAX_CHAR!"50"}" onblur="restrictTextLength(this);" name="to_${count}" id="to" value="${StringUtil.wrapString(giftMessageToNameVal)}"/><span class="js_textCounter textCounter"></span>
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
	            	<#assign giftMessageTextVal = parameters.giftMessageText!giftMessageText!""/>
		            <textarea name="giftMessageText_${count}" id="js_giftMessageText_${count}" class="content characterLimit js_giftMessageText" id="js_content" cols="35" rows="5" maxlength="${GIFT_MESSAGE_TEXT_MAX_CHAR!"255"}">${StringUtil.wrapString(giftMessageTextVal)}</textarea>
		            <span class="js_textCounter textCounter"></span>
		        </div>
	          </div>
	        </div>
        <#-- End of giftMessageEntry section -->
      </#list>
  </#if>
</div>

