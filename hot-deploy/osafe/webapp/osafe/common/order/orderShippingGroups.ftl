	<div class="${request.getAttribute("attributeClass")!}">
		<#if orderItemShipGroups?exists && orderItemShipGroups?has_content>
		   <#assign groupIndex=1?number/>
		   <#assign shipGroupIndex=0?int/>
	     <div class="displayBox">
	       
		   <#list orderItemShipGroups as cartShipInfo>
		      
		      <h4>${uiLabelMap.ShippingGroupHeading} ${groupIndex} of ${orderItemShipGroups.size()}</h4>
		       <#assign shipGroupIndex=0?int/>
		       <#assign orderItemShipGroupAssoc =cartShipInfo.getRelatedCache("OrderItemShipGroupAssoc")!""/>
    		   <#if orderItemShipGroupAssoc?has_content>
				   <div class="boxList cartList">
				  	 <#assign lineIndex=0?number/>
				  	 <#assign rowClass = "1">
			          <div class="boxListItemTabular shipItem shippingGroupSummary">
                       <div class="shippingGroupCartItem grouping grouping1">
				           <#list orderItemShipGroupAssoc as shipGroupAssoc>
                             <div class="shippingGroupCartItem groupRow">
	            		      <#assign orderItem =shipGroupAssoc.getRelatedOneCache("OrderItem")!""/>
					          ${setRequestAttribute("shipGroup", cartShipInfo)}
					          ${setRequestAttribute("shipGroupIndex", shipGroupIndex)}
					          ${setRequestAttribute("shipGroupAssoc", shipGroupAssoc)}
						      ${setRequestAttribute("orderItem", orderItem)}
							  ${setRequestAttribute("orderHeader", orderHeader)}
							  ${setRequestAttribute("localOrderReadHelper", localOrderReadHelper)}
							  ${setRequestAttribute("lineIndex", lineIndex)}
							  ${setRequestAttribute("rowClass", rowClass)}
						      ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#shippingGroupOrderItem")}
						        <#if rowClass == "2">
						            <#assign rowClass = "1">
						        <#else>
						            <#assign rowClass = "2">
						        </#if>
						        <#assign lineIndex= lineIndex + 1/>
						      </div>
						      
						      <#assign shipGroupAssocQty = shipGroupAssoc.quantity!/>
						      <#assign shipGroupSeqId = shipGroupAssoc.shipGroupSeqId!/>
						      <#assign alreadyProcessedOrderItemAttributes = Static["javolution.util.FastList"].newInstance()/>
						          <#assign orderItemAttributes = orderItem.getRelatedCache("OrderItemAttribute")!""/>
						          
								  <#-- filter the OrderItem attributes which are in the same shipgroup -->
								  <#assign orderItemAttributesChanged = Static["javolution.util.FastList"].newInstance()/>
								  <#list orderItemAttributes as orderItemAttribute>
									<#assign attrName = orderItemAttribute.attrName!""/>
									<#if (attrName.startsWith("GIFT_MSG_FROM_") || attrName.startsWith("GIFT_MSG_TO_") || attrName.startsWith("GIFT_MSG_TEXT_"))>
						              <#assign iShipIdItem = attrName.lastIndexOf("_")/>
						              <#if (iShipIdItem > -1 && attrName.substring(iShipIdItem+1).equals(shipGroupSeqId))>
						                  <#assign changedItemAttr = orderItemAttributesChanged.add(orderItemAttribute)>
						              </#if>
									</#if>  
								  </#list>
						      
						      <#--Iteration through the Qty is required because if there is no gift message exists then we can add as many as qty -->
						      <#list 1 .. shipGroupAssocQty as count>
							  
						          <#assign attrValue = ""/>
						          <#assign countString = "" + count />
					              <#assign from = ""/>
					              <#assign to = "" />
					              <#assign giftMessageText = ""/>
					              <#assign seqId = ""/>
					             
						          <#-- Get the SeqId like '01, 02...' so that we get correct AttrName value for TO_01 FROM_01 and TEXT_01 attributes -->
						          <#list orderItemAttributesChanged as orderItemAttribute>
						              <#assign attrName = orderItemAttribute.attrName!""/>
						              <#if !seqId?has_content>
							             <#if attrName.startsWith("GIFT_MSG_FROM_")>
								             <#assign iShipId = attrName.lastIndexOf("_")/>
								             <#assign seqId = attrName.substring("GIFT_MSG_FROM_"?length, iShipId)!""/>
								             <#if alreadyProcessedOrderItemAttributes.contains(seqId)>
								                 <#assign seqId = ""/>
								             </#if>
								         </#if>
							         </#if>
							         <#if !seqId?has_content>
							             <#if attrName.startsWith("GIFT_MSG_TO_")>
								             <#assign iShipId = attrName.lastIndexOf("_")/>
								             <#assign seqId = attrName.substring("GIFT_MSG_TO_"?length, iShipId)!""/>
								             <#if alreadyProcessedOrderItemAttributes.contains(seqId)>
								                 <#assign seqId = ""/>
								             </#if>
								         </#if>
							         </#if>
							         <#if !seqId?has_content>
							             <#if attrName.startsWith("GIFT_MSG_TEXT_")>
								             <#assign iShipId = attrName.lastIndexOf("_")/>
								             <#assign seqId = attrName.substring("GIFT_MSG_TEXT_"?length, iShipId)!""/>
								             <#if alreadyProcessedOrderItemAttributes.contains(seqId)>
								                 <#assign seqId = ""/>
								             </#if>
								         </#if>
							         </#if>
							      </#list>
							      
								  <#-- Here put the sequenceId in the  alreadyProcessedOrderItemAttributes list so we wouldn't be get the sequId again -->
						          <#assign changed = alreadyProcessedOrderItemAttributes.add(seqId)/>

						          <#list orderItemAttributesChanged as orderItemAttribute>
						             <#assign attrName = orderItemAttribute.attrName!""/>
						             <#if attrName.equals("GIFT_MSG_FROM_"+seqId+"_"+shipGroupSeqId)>
							             <#assign from = orderItemAttribute.attrValue! />
							         </#if>    
							         <#if attrName.equals("GIFT_MSG_TO_"+seqId+"_"+shipGroupSeqId)>
							             <#assign to = orderItemAttribute.attrValue! />
							         </#if>     
							         <#if attrName.equals("GIFT_MSG_TEXT_"+seqId+"_"+shipGroupSeqId)>
							             <#assign giftMessageText = orderItemAttribute.attrValue! />
							         </#if>
						          </#list>
						          <div class="giftMessageConfirm">
					                  ${uiLabelMap.GiftMessageLabel} <#if shipGroupAssocQty &gt; 1> ${count!}</#if>:
					                  <#if to?has_content || giftMessageText?has_content || from?has_content>
						                  ${to}  ${giftMessageText!}  ${from!}
					                  </#if>
					                  <a href="<@ofbizUrl>eCommerceOrderConfirmGiftMessage?orderId=${shipGroupAssoc.orderId!}&shipGroupSeqId=${shipGroupSeqId}&orderItemSeqId=${shipGroupAssoc.orderItemSeqId}</@ofbizUrl>">
                                          <#if to?has_content || giftMessageText?has_content || from?has_content>
                                              ${uiLabelMap.EditGiftMessageLink}
                                          <#else>
                                              ${uiLabelMap.GiftMessageLink}
                                          </#if>
                                      </a>
					              </div>
						      </#list>
						   </#list>
					   </div>
                	   <div class="shippingGroupCartItem grouping grouping2">
					          ${setRequestAttribute("shipGroup", cartShipInfo)}
					          ${setRequestAttribute("shipGroupIndex", shipGroupIndex)}
						      ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#shippingGroupOrderShipGroupItem")}
                	   </div>
					   
					 </div>
				   </div>
			   </#if>

   		       <#assign shipGroupIndex= shipGroupIndex + 1/>
		       <#assign groupIndex= groupIndex + 1/>
		   </#list>
         </div>
		</#if>
	</div>
