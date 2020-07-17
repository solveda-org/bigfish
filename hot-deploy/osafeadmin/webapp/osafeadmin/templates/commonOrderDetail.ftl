<#if checkoutSuccessMessageList?has_content>
  <div class="content-messages eCommerceSuccessMessage">
    <span class="checkMarkIcon eventImage"></span>
    <#list checkoutSuccessMessageList as checkOutMsg>
      <p class="eventMessage">${checkOutMsg}</p>
    </#list>
  </div>
</#if>

<form method="post" name="${detailFormName!""}" <#if detailFormId?exists>id="${detailFormId!}"</#if>>
${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
<#if generalInfoBoxHeading?exists && generalInfoBoxHeading?has_content>
    <div class="displayBox generalInfo">
        <div class="header">
        	<h2>${generalInfoBoxHeading!}</h2>
        	<#if stores?has_content && (stores.size() > 1)>
        	  <#if (showProductStoreInfo?has_content) && (showProductStoreInfo == 'Y')>
                <div class="productStoreInfo">
                    ${uiLabelMap.ProductStoreInfoCaption}
                    <#if context.productStoreName?has_content>
                        ${context.productStoreName}
                    </#if>
                </div>
              </#if>
            </#if>
        </div>
        <div class="boxBody">
        	<#if parameters.orderId?exists && orderHeader?has_content>
              ${sections.render('generalInfoBoxBody')!}
            </#if>
        </div>
    </div>
</#if>
<#if customerInfoBoxHeading?exists && customerInfoBoxHeading?has_content>
    <div class="displayBox customerInfo">
        <div class="header"><h2 class="centerText">${customerInfoBoxHeading!}</h2></div>
        <div class="boxBody">
        	<#if parameters.orderId?exists && orderHeader?has_content>
              ${sections.render('customerInfoBoxBody')}
            </#if>
        </div>
    </div>
</#if>
<#if paymentInfoBoxHeading?exists && paymentInfoBoxHeading?has_content>
    <div class="displayBox paymentInfo">
        <div class="header"><h2 class="centerText">${paymentInfoBoxHeading!}</h2></div>
        <div class="boxBody">
        	<#if parameters.orderId?exists && orderHeader?has_content>
              ${sections.render('paymentInfoBoxBody')!}
            </#if>
        </div>
    </div>
</#if>
<#if shippingInfoBoxHeading?exists && shippingInfoBoxHeading?has_content>
    <div class="displayBox shippingInfo">
        <div class="header"><h2 class="centerText">${shippingInfoBoxHeading!}</h2></div>
        <div class="boxBody">
            <#if parameters.orderId?exists && orderHeader?has_content>
              ${sections.render('shippingInfoBoxBody')!}
            </#if>
        </div>
    </div>
</#if>
<#if orderStatusBoxHeading?exists && orderStatusBoxHeading?has_content>
    <div class="displayBox detailInfo">
        <div class="header"><h2>${orderStatusBoxHeading!}</h2></div>
        <div class="boxBody">
        	<#if parameters.orderId?exists && orderHeader?has_content>
              ${sections.render('orderStatusBoxBody')!}
            </#if>
        </div>
    </div>
</#if>
<#if orderItemInfoBoxHeading?exists && orderItemInfoBoxHeading?has_content>
    <div class="displayListBox orderItemInfo">
    	<#if parameters.orderId?exists && orderHeader?has_content>
          ${sections.render('listPagingBody')}
          ${sections.render('commonFormJS')?if_exists}
          ${sections.render('commonConfirm')}
        </#if>
        <div class="header"><h2>${orderItemInfoBoxHeading!}</h2></div>
        <div class="boxBody">
        	<#if parameters.orderId?exists && orderHeader?has_content>
              ${sections.render('orderItemBoxBody')!}
            </#if>
        </div>
    </div>
</#if>
<#if orderNoteInfoBoxHeading?exists && orderNoteInfoBoxHeading?has_content>
    <div class="displayListBox orderItemInfo">
        ${sections.render('listPagingBody')}
        <div class="header"><h2>${orderNoteInfoBoxHeading!}</h2></div>
        <div class="boxBody">
        	<#if parameters.orderId?exists && orderHeader?has_content>
              ${sections.render('orderNoteBoxBody')!}
            </#if>
        </div>
    </div>
</#if>
<#if orderAttributeInfoBoxHeading?exists && orderAttributeInfoBoxHeading?has_content>
    <div class="displayListBox orderItemInfo">
        <div class="header"><h2>${orderAttributeInfoBoxHeading!}</h2></div>
        <div class="boxBody">
        	<#if parameters.orderId?exists && orderHeader?has_content>
              ${sections.render('orderAttributeBoxBody')!}
            </#if>
        </div>
    </div>
</#if>
<#if orderRefundInfoBox?exists && orderRefundInfoBox?has_content>
    <div class="orderRefundInfoBox" id="orderRefundInfoBox">
      
    </div>
</#if>

<div class="displayBox footerInfo">
    <div>
          ${sections.render('footerBoxBody')}
    </div>
    <div class="infoDetailIcon">
      <#if parameters.orderId?exists && orderHeader?has_content>
        ${sections.render('commonDetailLinkButton')!}
      </#if>
    </div>
</div>
</form>
${sections.render('commonFormJS')?if_exists}