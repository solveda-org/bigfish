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
            ${sections.render('generalInfoBoxBody')!}
        </div>
    </div>
</#if>
<#if orderItemShipGroups?exists && orderItemShipGroups?has_content>
  <#list orderItemShipGroups as orderItemShipGroup>
    <div class="displayListBox generalInfo">
	    <div class="header"><h2>${uiLabelMap.OrderShippingGroupDetailBoxHeading!}# ${orderItemShipGroup.shipGroupSeqId!}</h2></div>
        ${setRequestAttribute("orderItemShipGroup",orderItemShipGroup)}
        <div class="boxBody">
            ${sections.render('orderShippingGroupsDetailInfoBoxBody')!}
            ${sections.render('orderShippingGroupItemsDetailInfoBoxBody')!}
        </div>
    </div>
    ${sections.render('orderShippingGroupShipmentsDetailInfoBoxBody')!}
  </#list>
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