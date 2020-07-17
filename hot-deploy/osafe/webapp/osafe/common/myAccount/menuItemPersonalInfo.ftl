<div class="${request.getAttribute("attributeClass")!}">
    <h2>${uiLabelMap.CustomerPersonalHeading?if_exists}</h2>
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

