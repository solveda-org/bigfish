<#if preRetrieved?has_content && preRetrieved != "N">
<tr class="heading">
    <th class="idCol">${uiLabelMap.CartIdLabel}</th>
    <th class="dateCol">${uiLabelMap.LastUpdatedLabel}</th>
    <th class="nameCol">${uiLabelMap.CustNoLabel}</th>
    <th class="nameCol">${uiLabelMap.LastNameLabel}</th>
    <th class="nameCol">${uiLabelMap.FirstNameLabel}</th>
    <th class="qtyCol">${uiLabelMap.NoItemsInCartLabel}</th>
    <th class="qtyCol">${uiLabelMap.TotalQtyInCartLabel}</th>
</tr>
    
<#if resultList?has_content>
    <#assign rowClass = "1">
    <#list resultList as cart>
        <#assign hasNext = cart_has_next>
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
             <td class="idCol" ><a href="abandonedCartDetail?cartId=${cart.shoppingListId!}&partyId=${cart.partyId!}">${cart.shoppingListId!}</a></td>
             <#assign lastUpdatedDate = Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(cart.lastUpdatedStamp, preferredDateFormat)!>
             <td class="dateCol <#if !hasNext>lastRow</#if>">${lastUpdatedDate!}</td>
             <td class="nameCol <#if !hasNext>lastRow</#if>"><a href="customerDetail?partyId=${cart.partyId!}">${cart.partyId!}</a></td>
             <#if cart.partyId?has_content >
             	<#assign party = delegator.findByPrimaryKey("Party", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", cart.partyId!))/>
             </#if>
             <#if party?has_content>
             	<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(party)>
             	<#if partyName?has_content>
	             	<#assign partyName = partyName?split(" ") />
		    	 	<#assign firstName = partyName[0] />
		    	 	<#assign lastName = partyName[1] />
	    	 	</#if>
             </#if>
             <td class="nameCol <#if !hasNext>lastRow</#if>">${lastName!}</td>
             <td class="nameCol <#if !hasNext>lastRow</#if>">${firstName!}</td>
             <#assign shoppingListItems = delegator.findByAnd("ShoppingListItem", {"shoppingListId" : cart.shoppingListId!})>
             <#assign shoppingListSize = shoppingListItems.size() >
             <td class="qtyCol <#if !hasNext>lastRow</#if>">${shoppingListSize!}</td>
             <#assign shoppingListItemsQty = delegator.findByAnd("ShoppingListItem", {"shoppingListId" : cart.shoppingListId!})>
             <#assign qtyCount = 0>
             <#if (shoppingListSize &gt; 0) >
	             <#list shoppingListItemsQty as qty>
	             	<#assign qtyMult = qty.quantity >
	             	<#assign qtyCount = qtyCount + (qty.quantity)>
	             </#list>
             </#if>
             <#assign shoppingListSize = shoppingListItems.size() >
             <td class="qtyCol <#if !hasNext>lastRow</#if>">${qtyCount!}</td>
        </tr>
        <#-- toggle the row color -->
        <#if rowClass == "2">
            <#assign rowClass = "1">
        <#else>
            <#assign rowClass = "2">
        </#if>
    </#list>
</#if>
</#if>