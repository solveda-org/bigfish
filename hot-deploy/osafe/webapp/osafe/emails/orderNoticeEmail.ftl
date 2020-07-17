

<p class="info">${uiLabelMap.ThankYouForShoppingEmailInfo}</p>

<#if bodyContentId?has_content>
    <#-- Additional Info Block -->
    <@renderContentAsText contentId=bodyContentId />
</#if>

<#if !isDemoStore?exists || isDemoStore><p>${uiLabelMap.OrderDemoFrontNote}.</p></#if>
<#if note?exists><p class="tabletext">${note}</p></#if>
<#if orderHeader?exists>
${screens.render("component://osafe/widget/EmailScreens.xml#orderHeaderSection")}
<br />
${screens.render("component://osafe/widget/EmailScreens.xml#orderItemsSection")}

<#else>
<h1>Order not found with ID [${orderId?if_exists}], or not allowed to view.</h1>
</#if>
