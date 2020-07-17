<#assign requestUri =  Static["org.ofbiz.base.util.UtilHttp"].getRequestUriFromTarget(request.getRequestURL())/> 
<div id="siteInfo">
   <div id="welcome">
      <p>${uiLabelMap.WelcomeCaption} <span class="name"><#if userLoginFullName?has_content>${userLoginFullName}<#else>${userLogin.userLoginId}</#if>&nbsp;[${(adminModuleName)?if_exists}]</span></p>
      
      <#if stores?has_content && (stores.size() > 1)>
		  <form name="chooseProductStore" method="post" action="<@ofbizUrl>main</@ofbizUrl>">
          <p>${uiLabelMap.ProductStoreCaption}
		    <select id="selectProductStore" name="selectedProductStoreId" onchange="submit()">
		      <#list stores as productStore>
		        <option value='${productStore.productStoreId}'<#if productStore.productStoreId == globalContext.productStoreId> selected</#if>>${productStore.storeName!""}</option>
		      </#list>
		    </select>
		 </p>
		  </form>
      <#else>
          <p>${uiLabelMap.ProductStoreCaption} <span class="name">[${globalContext.productStoreName?if_exists}]</span></p>
      </#if>
      <p>${uiLabelMap.CatalogCaption} <span class="name">[${globalContext.prodCatalogName?if_exists}]</span></p>
    </div>
   <div id="helperImages">

   			<div class="adminCheckoutCartContent">
		   		<#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request)! />
           		<#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
           		<#if shoppingCart?has_content >
	              	<#assign cartCount = shoppingCart.getTotalQuantity()!"0" />
	              	<#assign cartSubTotal = shoppingCart.getSubTotal()!"0" />
	            <#else>
	            	<#assign cartCount = "0" />
	              	<#assign cartSubTotal = "0" />
           		</#if>
		        <#if adminContext?exists>
		            <#if adminContext.CONTEXT_PARTY_ID?has_content && (cartCount?if_exists > 0) >
		                 <a class="standardBtn adminCartIcon" href="<@ofbizUrl>adminCheckout</@ofbizUrl>">${cartCount!} <#if cartCount == 1 >${uiLabelMap.CommonItem}<#else>${uiLabelMap.CommonItems}</#if> <@ofbizCurrency amount=cartSubTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></a>
		            <#else>
		                 <a class="standardBtn adminCartIcon" href="javascript:void(0);javascript:<#if !adminContext.CONTEXT_PARTY_ID?has_content>alert('${uiLabelMap.CheckoutNoCustomerError}');</#if><#if !(cartCount?if_exists > 0)>alert('${uiLabelMap.CheckoutNoItemsInCartError}');</#if>">${cartCount!} <#if cartCount == 1 >${uiLabelMap.CommonItem}<#else>${uiLabelMap.CommonItems}</#if> <@ofbizCurrency amount=cartSubTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></a>
		            </#if>
		        </#if>
		   </div>
   
		   <a class="standardBtn help" href="${ADM_HELP_URL!}${helperFileName!"index.htm"}" target="_blank" >${uiLabelMap.HelpBtn}</a>
		   <a class="standardBtn logout" href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.LogoutBtn}</a>
 
           <div class="context" onMouseover="javascript:showContextInfotip(event, this, 'contextInfoBox');" onMouseout="hideContextInfotip(event, this, 'contextInfoBox')">
              <#if adminContext?exists>
                          <a class="standardBtn contextIcon" href="javascript:void(0);">${uiLabelMap.ContextBtn}</a>
              </#if>
               <div class="contextInfoBox" style="display:none">
                   <#if adminContext?exists>
                       <#if adminContext.CONTEXT_PARTY_ID?has_content>
                           <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, adminContext.CONTEXT_PARTY_ID?if_exists, false)?if_exists>
                       </#if>
                       <div class=contextInfoRow>
                           <div class=contextInfoCaption>
                               <label>${uiLabelMap.ContextCustomerCaption}</label>
                           </div>
                           <div class=contextInfoValue>
                               <div class=contextId>
                                   <a href="<@ofbizUrl>customerDetail?partyId=${adminContext.CONTEXT_PARTY_ID?if_exists}</@ofbizUrl>">${adminContext.CONTEXT_PARTY_ID?if_exists}</a>
                               </div>
                               <div class=contextDesc>
                                   ${partyName?if_exists}
                               </div>
                           </div>
                       </div>
                       <#if adminContext.CONTEXT_ORDER_ID?has_content>
                           <#assign orderHeader = delegator.findOne("OrderHeader", {"orderId" : adminContext.CONTEXT_ORDER_ID?if_exists}, false)?if_exists />
                           <#if orderHeader?has_content>
                               <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader?if_exists)>
                               <#assign totalOrderItemsQuantity=orh.getTotalOrderItemsQuantity()?if_exists>
                               <#assign grandTotal = orh.getOrderGrandTotal()?if_exists>
                               <#if totalOrderItemsQuantity?has_content && totalOrderItemsQuantity &gt; 1>
                                   <#assign orderInfo = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "ContextOrderItemsInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${totalOrderItemsQuantity!}", "${globalContext.currencySymbol!}${grandTotal!}"), locale)/>
                               <#else>
                                   <#assign orderInfo = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "ContextOrderItemInfo", Static["org.ofbiz.base.util.UtilMisc"].toList("${totalOrderItemsQuantity!}", "${globalContext.currencySymbol!}${grandTotal!}"), locale)/>
                               </#if>
                           </#if>
                       </#if>
                       <div class=contextInfoRow>
                           <div class=contextInfoCaption>
                               <label>${uiLabelMap.ContextOrderCaption}</label>
                           </div>
                           <div class=contextInfoValue>
                               <div class=contextId>
                                   <a href="<@ofbizUrl>orderDetail?orderId=${adminContext.CONTEXT_ORDER_ID?if_exists}</@ofbizUrl>">${adminContext.CONTEXT_ORDER_ID?if_exists}</a>
                               </div>
                               <div class=contextDesc>
                                   ${StringUtil.wrapString(orderInfo!adminContext.CONTEXT_ORDER_ID?if_exists)}
                               </div>
                           </div>
                       </div>
                       <#if adminContext.CONTEXT_PRODUCT_ID?has_content>
                           <#assign product = delegator.findOne("Product", {"productId" : adminContext.CONTEXT_PRODUCT_ID?if_exists}, false)?if_exists/>
                           <#if product?has_content>
                               <#if product.isVariant?if_exists?upper_case == "Y">
                                   <#assign product = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(adminContext.CONTEXT_PRODUCT_ID, delegator)?if_exists>
                               </#if>
                               <#assign productName = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(product, 'PRODUCT_NAME', request)?if_exists>
                           </#if>
                       </#if>
                       <div class=contextInfoRow>
                           <div class=contextInfoCaption>
                               <label>${uiLabelMap.ContextProductCaption}</label>
                           </div>
                           <div class=contextInfoValue>
                               <div class=contextId>
                                   <#if product?has_content && product.isVirtual == 'Y'>
                                     <a href="<@ofbizUrl>virtualProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
                                   <#elseif product?has_content && product.isVariant == 'Y'>
                                     <a href="<@ofbizUrl>variantProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
                                   <#elseif product?has_content && product.isVirtual == 'N' && product.isVariant == 'N'>
                                     <a href="<@ofbizUrl>finishedProductDetail?productId=${product.productId!}</@ofbizUrl>">${product.productId!}</a>
                                   </#if>
                               </div>
                               <div class=contextDesc>
                                   ${StringUtil.wrapString(productName!adminContext.CONTEXT_PRODUCT_ID?if_exists)}
                               </div>
                           </div>
                       </div>
                       <#if adminContext.CONTEXT_STORE_PARTY_ID?has_content>
                           <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId": adminContext.CONTEXT_STORE_PARTY_ID?if_exists}, false)/>
                           <#if partyGroup?has_content>
                               <#assign groupName = partyGroup.groupName!""/>
                               <#assign groupNameLocal = partyGroup.groupNameLocal!""/>
                           </#if>
                       </#if>
                       <div class=contextInfoRow>
                           <div class=contextInfoCaption>
                               <label>${uiLabelMap.ContextStoreCaption}</label>
                           </div>
                           <div class=contextInfoValue>
                               <div class=contextId>
                                   <a href="<@ofbizUrl>storeLocationDetail?storePartyId=${adminContext.CONTEXT_STORE_PARTY_ID?if_exists}&groupNameLocal=${groupNameLocal!""}</@ofbizUrl>">${groupNameLocal!adminContext.CONTEXT_STORE_PARTY_ID?if_exists}</a>
                               </div>
                               <div class=contextDesc>
                                   ${groupName!adminContext.CONTEXT_STORE_PARTY_ID?if_exists}
                               </div>
                           </div>
                       </div>
                   </#if>
               </div>
           </div>
       
    </div>
</div>

