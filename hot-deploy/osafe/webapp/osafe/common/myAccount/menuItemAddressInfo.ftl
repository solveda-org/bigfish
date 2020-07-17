<div class="${request.getAttribute("attributeClass")!}">
    <h2>${uiLabelMap.CustomerAddressBookHeading?if_exists}</h2>
    <ul class="displayList">
     <li>
      <div>
       <p>${uiLabelMap.CustomerAddressBookInfo}</p>
      </div>
     </li>
     <li>
      <div>
       <a href="<@ofbizUrl>eCommerceEditAddressBook</@ofbizUrl>"><span>${uiLabelMap.ClickAddressBookInfo}</span></a>
      </div>
     </li>
    </ul>
</div>
