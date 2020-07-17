<div class="displayBoxList">
	<div class="displayBox">
	    <h3>${uiLabelMap.CustomerPersonalHeading?if_exists}</h3>
	    <ul class="displayList">
	     <li>
	      <div>
	       <p>${uiLabelMap.CustomerPersonalInfo}</p>
	      </div>
	     </li>
	     <li>
	      <div>
	       <a href="<@ofbizUrl>eCommerceEditCustomerInfo</@ofbizUrl>"><span>${uiLabelMap.ClickPersonalDetailsInfo}</span></a>
	      </div>
	     </li>
	    </ul>
	</div>
	<div class="displayBox">
	    <h3>${uiLabelMap.CustomerLoginHeading?if_exists}</h3>
	    <ul class="displayList">
	     <li>
	      <div>
	       <p>${uiLabelMap.CustomerLoginInfo}</p>
	      </div>
	     </li>
	     <li>
	      <div>
	       <a href="<@ofbizUrl>eCommerceEditLoginInfo</@ofbizUrl>"><span>${uiLabelMap.ClickLoginDetailsInfo}</span></a>
	      </div>
	     </li>
	    </ul>
	</div>
	<div class="displayBox">
	    <h3>${uiLabelMap.CustomerAddressBookHeading?if_exists}</h3>
	    <ul class="displayList">
	     <li>
	      <div>
	       <p>${uiLabelMap.CustomerAddressBookInfo}</p>
	      </div>
	     </li>
	     <li>
	      <div>
	       <a href="<@ofbizUrl>eCommerceEditAddressBook</@ofbizUrl>"><span>${uiLabelMap.ClickAddressBookInfo}</span></a>
	      </div>
	     </li>
	    </ul>
	</div>
	<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
	 <div class="displayBox">
	    <h3>${uiLabelMap.PaymentMethodsHeading?if_exists}</h3>
	    <ul class="displayList">
	     <li>
	      <div>
	       <p>${uiLabelMap.PaymentMethodsInfo}</p>
	      </div>
	     </li>
	     <li>
	      <div>
	       <a href="<@ofbizUrl>eCommercePaymentMethodInfo</@ofbizUrl>"><span>${uiLabelMap.ClickViewPaymentMethodInfo}</span></a>
	      </div>
	     </li>
	    </ul>
	 </div>
	</#if>
	<div class="displayBox">
	    <h3>${uiLabelMap.CustomerOrderStatusHeading?if_exists}</h3>
	    <ul class="displayList">
	     <li>
	      <div>
	       <p>${uiLabelMap.CustomerOrderStatusInfo}</p>
	      </div>
	     </li>
	     <li>
	      <div>
	       <a href="<@ofbizUrl>eCommerceOrderHistory</@ofbizUrl>"><span>${uiLabelMap.ClickViewOrdersInfo}</span></a>
	      </div>
	     </li>
	    </ul>
	</div>
</div>	

