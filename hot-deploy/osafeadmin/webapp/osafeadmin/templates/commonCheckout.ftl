<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
    <div class="displayBox salesChannel">
        <div class="header"><h2>${uiLabelMap.SalesChannelBoxHeading!}</h2></div>
        <div class="boxBody">
            ${sections.render('salesChannelBoxBody')!}
        </div>
    </div>
    <div class="displayListBox shoppingCart">
        <div class="header"><h2>${uiLabelMap.ShoppingCartHeading!}</h2></div>
        <div class="boxBody">
              ${sections.render('shoppingCartBoxBody')!}
        </div>
    </div>   
    <div class="displayBox customerInformation">
        <div class="header"><h2>${CustomerInformationHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('customerInformationBoxBody')!}
        </div>
    </div>
    <div class="displayBox billingAddress entry">
        <div class="header"><h2>${uiLabelMap.BillingAddressHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('billingAddressBoxBody')!}
        </div>
    </div>
    <div class="displayBox shippingAddress entry">
        <div class="header"><h2>${uiLabelMap.ShippingAddressHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('shippingAddressBoxBody')!}
        </div>
    </div>
    <div class="displayBox shippingOption">
        <div class="header"><h2>${uiLabelMap.ShippingOptionHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('shippingOptionBoxBody')!}
        </div>
    </div>
    <div class="displayBox promotionOption">
        <div class="header"><h2>${uiLabelMap.PromotionOptionHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('promotionOptionBoxBody')!}
        </div>
    </div>
    <div class="displayListBox shoppingCart">
        <div class="header"><h2>${uiLabelMap.ShoppingCartHeading!}</h2></div>
        <div class="boxBody">
              ${sections.render('shoppingCartBottomBoxBody')!}
        </div>
    </div>  
    <div class="displayBox paymentOption">
        <div class="header"><h2>${uiLabelMap.PaymentOptionHeading!}</h2></div>
        <div class="boxBody">
             ${sections.render('paymentOptionBoxBody')!}
        </div>
    </div>
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
