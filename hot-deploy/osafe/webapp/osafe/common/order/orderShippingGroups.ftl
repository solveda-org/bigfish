	<div class="${request.getAttribute("attributeClass")!}">
		<#if orderItemShipGroups?exists && orderItemShipGroups?has_content>
		
		   <#assign checkoutGiftMessage = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_GIFT_MESSAGE") />
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
	            		      <#assign orderItem = shipGroupAssoc.getRelatedOneCache("OrderItem")!""/>
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
						      <#assign product = orderItem.getRelatedOneCache("Product")!""/>
						      <#assign pdpGiftMessageAttributeValue = ""/>
						      <#assign showGiftMessage = "false"/>
						      <#if product?has_content>
						          <#assign productAttributes = product.getRelatedCache("ProductAttribute")!""/>
						          <#if productAttributes?has_content>
						              <#assign pdpGiftMessageAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", product.productId, "attrName", "CHECKOUT_GIFT_MESSAGE"))?if_exists />
						              <#if pdpGiftMessageAttributes?has_content>
						                  <#assign pdpGiftMessageAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(pdpGiftMessageAttributes)/>
							              <#if pdpGiftMessageAttribute?has_content>
							                  <#assign pdpGiftMessageAttributeValue = pdpGiftMessageAttribute.attrValue!""/>
							              </#if>
						              </#if>
						          </#if>
						      </#if>
						      
						      <#if (checkoutGiftMessage?has_content && checkoutGiftMessage) && (pdpGiftMessageAttributeValue?has_content && pdpGiftMessageAttributeValue != "FALSE")>
						          <#assign showGiftMessage = "true"/>
						      <#elseif (checkoutGiftMessage?has_content && checkoutGiftMessage) && !pdpGiftMessageAttributeValue?has_content>
						          <#assign showGiftMessage = "true"/>
						      <#elseif pdpGiftMessageAttributeValue?has_content && pdpGiftMessageAttributeValue == 'TRUE'>
						          <#assign showGiftMessage = "true"/>
						      <#else>
						          <#assign showGiftMessage = "false"/>    
						      </#if>
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
						      <#if showGiftMessage == "true">
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
							          <div class="shippingGroupCartItem groupRow giftMessageConfirm">
	                                     <ul class="displayList giftMessageItem shippingGroupSummary">
	                                      <li class="string giftMessage shippingGroupgiftMessage">
	                                       <div>
						                      <label>${uiLabelMap.GiftMessageLabel} <#if shipGroupAssocQty &gt; 1> ${count!}</#if>:</label>
							                  <#if to?has_content || giftMessageText?has_content || from?has_content>
								                  <span><#if to?has_content>${uiLabelMap.ToCaption} ${to!}</#if> ${giftMessageText!} <#if from?has_content>${uiLabelMap.FromCaption} ${from!}</#if></span>
							                  </#if>
						                      <a href="<@ofbizUrl>eCommerceOrderConfirmGiftMessage?orderId=${shipGroupAssoc.orderId!}&shipGroupSeqId=${shipGroupSeqId}&orderItemSeqId=${shipGroupAssoc.orderItemSeqId}</@ofbizUrl>">
	                                          <#if to?has_content || giftMessageText?has_content || from?has_content>
	                                              <span>${uiLabelMap.EditGiftMessageLink}</span>
	                                          <#else>
	                                              <span>${uiLabelMap.GiftMessageLink}</span>
	                                          </#if>
	                                          </a>
	                                       </div>
	                                      </li>
	                                     </ul>
						              </div>
							      </#list>
						      </#if>
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
