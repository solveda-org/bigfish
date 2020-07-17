<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
	<#if salesChannelBoxHeading?exists && salesChannelBoxHeading?has_content>
    <div class="displayBox salesChannel">
        <div class="header"><h2>${salesChannelBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('salesChannelBoxBody')!}
        </div>
    </div>
    </#if>
    <#if shoppingCartBoxHeading?exists && shoppingCartBoxHeading?has_content>
    <div class="displayListBox shoppingCart">
        <div class="header"><h2>${shoppingCartBoxHeading!}</h2></div>
        <div class="boxBody">
              ${sections.render('shoppingCartBoxBody')!}
        </div>
    </div>   
    </#if>
    <#if shoppingCartItemBoxHeading?exists && shoppingCartItemBoxHeading?has_content>
    <div class="displayListBox shoppingCart">
        <div class="header"><h2>${shoppingCartItemBoxHeading!}</h2></div>
        <div class="boxBody">
              ${sections.render('shoppingCartItemBoxBody')!}
        </div>
    </div>   
    </#if>
    <#if customerInformationBoxHeading?exists && customerInformationBoxHeading?has_content>
    <div class="displayBox customerInformation">
        <div class="header"><h2>${customerInformationHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('customerInformationBoxBody')!}
        </div>
    </div>
    </#if>
    <#if billingAddressBoxHeading?exists && billingAddressBoxHeading?has_content>
    <div class="displayBox billingAddress entry">
        <div class="header"><h2>${billingAddressBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('billingAddressBoxBody')!}
        </div>
    </div>
    </#if>
    <#if shippingAddressBoxHeading?exists && shippingAddressBoxHeading?has_content>
    <div class="displayBox shippingAddress entry">
      <div class="header"><h2>${shippingAddressBoxHeading!}</h2></div>
      <div class="boxBody">
           ${sections.render('shippingAddressBoxBody')!}
      </div>
    </div>
    </#if>
    <#if shippingApplies?has_content && shippingApplies?exists && shippingApplies>
    <#if shippingOptionBoxHeading?exists && shippingOptionBoxHeading?has_content>
      <div class="displayBox shippingOption">
        <div class="header"><h2>${shippingOptionBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('shippingOptionBoxBody')!}
        </div>
      </div>
      </#if>
      <#if shippingInstructionBoxHeading?exists && shippingInstructionBoxHeading?has_content>
      <div class="displayBox shippingOption">
        <div class="header"><h2>${shippingInstructionBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('shippingInstructionBoxBody')!}
        </div>
      </div>
      </#if>
    </#if>
    <#if promotionOptionBoxHeading?exists && promotionOptionBoxHeading?has_content>
    <div class="displayBox promotionOption">
        <div class="header"><h2>${promotionOptionBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('promotionOptionBoxBody')!}
        </div>
    </div>
    </#if>
    <#if loyaltyPointsOptionBoxHeading?exists && loyaltyPointsOptionBoxHeading?has_content>
    <div class="displayBox loyaltyPointsOption">
        <div class="header"><h2>${loyaltyPointsOptionBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('loyaltyPointsOptionBoxBody')!}
        </div>
    </div>
    </#if>
    <#if orderAdjustmentOptionBoxHeading?exists && orderAdjustmentOptionBoxHeading?has_content>
    <div class="displayBox orderAdjustmentOption">
        <div class="header"><h2>${orderAdjustmentOptionBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('orderAdjustmentOptionBoxBody')!}
        </div>
    </div>
    </#if>
    <#if shoppingCartBoxHeading?exists && shoppingCartBoxHeading?has_content>
    <div class="displayListBox shoppingCart">
        <div class="header"><h2>${shoppingCartBoxHeading!}</h2></div>
        <div class="boxBody">
              ${sections.render('shoppingCartBottomBoxBody')!}
        </div>
    </div>  
    </#if>
    <#if giftCardOptionBoxHeading?exists && giftCardOptionBoxHeading?has_content && CHECKOUT_GIFTCARD_METHOD?has_content>
    <div class="displayBox promotionOption">
        <div class="header"><h2>${giftCardOptionBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('giftCardOptionBoxBody')!}
        </div>
    </div>
    </#if>
    <#if paymentOptionBoxHeading?exists && paymentOptionBoxHeading?has_content>
    <div class="displayBox paymentOption">
        <div class="header"><h2>${paymentOptionBoxHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('paymentOptionBoxBody')!}
        </div>
    </div>
    </#if>
    <#if giftMessageBoxHeading?exists && giftMessageBoxHeading?has_content>
      ${sections.render('giftMessageBoxBody')!}
    </#if>
	<div class="displayBox footerInfo">
	    <div>
	          ${sections.render('footerBoxBody')}
	    </div>
	    <div class="infoDetailIcon">
	      ${sections.render('commonDetailLinkButton')!}
	    </div>
	</div>
</form>
${sections.render('commonFormJS')?if_exists}
${sections.render('tooltipBody')?if_exists}
${sections.render('commonConfirm')!}
${sections.render('commonLookup')!}
