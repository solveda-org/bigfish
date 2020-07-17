	<div class="${request.getAttribute("attributeClass")!}">
		<#if orderItemShipGroups?exists && orderItemShipGroups?has_content>
		   <#assign groupIndex=1?number/>
		   <#assign shipGroupIndex=0?int/>
	     <div class="displayBox">
		   <#list orderItemShipGroups as cartShipInfo>
		      <h4>${uiLabelMap.ShippingGroupHeading} ${groupIndex} of ${orderItemShipGroups.size()}</h3>
		       <#assign shipGroupIndex=0?int/>
		       <#assign orderItemShipGroupAssoc =cartShipInfo.getRelated("OrderItemShipGroupAssoc")!""/>
    		   <#if orderItemShipGroupAssoc?has_content>
				   <div class="boxList cartList">
				  	 <#assign lineIndex=0?number/>
				  	 <#assign rowClass = "1">
			          <div class="boxListItemTabular shipItem shippingGroupSummary">
                       <div class="shippingGroupCartItem grouping grouping1">
				           <#list orderItemShipGroupAssoc as shipGroupAssoc>
                             <div class="shippingGroupCartItem groupRow">
	            		      <#assign orderItem =shipGroupAssoc.getRelatedOne("OrderItem")!""/>
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
