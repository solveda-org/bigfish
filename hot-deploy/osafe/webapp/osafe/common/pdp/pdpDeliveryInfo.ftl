<#if DELIVERY_INFO?exists &&  DELIVERY_INFO?has_content>
  <div id="pdpDeliveryInfo">
       <div class="displayBox">
         <h3 class="displayBoxHeaderCaption">${uiLabelMap.PDPDeliveryInfoHeading}</h3>
        <p><@renderContentAsText contentId="${DELIVERY_INFO}" ignoreTemplate="true"/></p>
       </div>
  </div>
</#if>
