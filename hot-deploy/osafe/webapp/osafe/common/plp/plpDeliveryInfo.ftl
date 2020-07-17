<#if PLP_DELIVERY_INFO?exists &&  PLP_DELIVERY_INFO?has_content>
  <li class="${request.getAttribute("attributeClass")!}">
   <div>
     <label>${uiLabelMap.PLPDeliveryInfoLabel}</label>
     <span><@renderContentAsText contentId="${PLP_DELIVERY_INFO}" ignoreTemplate="true"/></span>
   </div>
  </li>   
</#if>
