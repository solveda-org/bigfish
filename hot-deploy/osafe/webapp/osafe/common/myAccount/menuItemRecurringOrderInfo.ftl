<div class="${request.getAttribute("attributeClass")!}">
    <h2>${uiLabelMap.CustomerRecurringOrdersHeading?if_exists}</h2>
    <ul class="displayList">
     <li>
      <div>
       <p>${uiLabelMap.CustomerRecurringOrdersInfo}</p>
      </div>
     </li>
     <li>
      <div>
       <a href="<@ofbizUrl>eCommerceRecurringOrder</@ofbizUrl>"><span>${uiLabelMap.ClickViewRecurringOrdersInfo}</span></a>
      </div>
     </li>
    </ul>
</div>

