<div class="${request.getAttribute("attributeClass")!}">
    <h2>${uiLabelMap.CustomerOrderStatusHeading?if_exists}</h2>
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

