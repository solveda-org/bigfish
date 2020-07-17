<!-- start listBox -->
<thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.CustomerNoLabel}</th>
    <th class="nameCol">${uiLabelMap.CustomerNameLabel}</th>
    <th class="statusCol">${uiLabelMap.StatusLabel}</th>
    <th class="typeCol">${uiLabelMap.RoleLabel}</th>
    <#if stores?has_content && (stores.size() > 1)>
      <th class="nameCol">${uiLabelMap.StoreLabel}</th>
    </#if>
    </tr>
</thead>
<#if resultList?exists && resultList?has_content>
  <#assign rowClass = "1">
  <#list resultList as partyRow>
    <#assign person = delegator.findByPrimaryKey("Person", {"partyId", partyRow.partyId})/>
    <#assign productStoreRoles = delegator.findByAnd("ProductStoreRole", {"partyId", partyRow.partyId, "roleTypeId", "CUSTOMER"})/>
    <#if productStoreRoles?has_content >
      <#assign productStoreRole = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productStoreRoles) /> 
      <#assign roleType = productStoreRole.getRelatedOne("RoleType") />
      <#if roleType?has_content>
        <#assign roleTypeId = roleType.roleTypeId />
        <#assign partyRoleType = roleType.description />
      </#if>
    </#if>
    <#if productStoreRole?has_content >
      <#assign hasNext = partyRow_has_next/>
      <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
        <td class="idCol <#if !partyRow_has_next>lastRow</#if> firstCol" ><a href="javascript:set_values('${partyRow.partyId?if_exists}', '${person.lastName?if_exists}')">${partyRow.partyId}</a></td>
        <td class="nameCol <#if !partyRow_has_next>lastRow</#if>"><#if person?has_content>${person.firstName!""} ${person.lastName!""}</#if></td>
        <td class="statusCol <#if !partyRow_has_next>lastRow</#if>">
          <#if (partyRow.statusId?has_content && partyRow.statusId=="PARTY_ENABLED")>
            ${uiLabelMap.CustomerEnabledInfo}
          <#else>
            ${uiLabelMap.CustomerDisabledInfo}
          </#if>
        </td>
        <td class="typeCol <#if !partyRow_has_next>lastRow</#if>">${partyRoleType?if_exists}</td>
        <#if stores?has_content && (stores.size() > 1)>
        	<#assign partyProductStore = productStoreRole.getRelatedOne("ProductStore") />
        	<#assign productStoreName = "" />
        	<#if partyProductStore?has_content >
        	  <#if partyProductStore.storeName?has_content >
        	    <#assign productStoreName = partyProductStore.storeName />
        	  <#else>
        	    <#assign productStoreName = partyProductStore.productStoreId />
        	  </#if>
        	</#if>
        
          <td class="nameCol <#if !partyRow_has_next>lastRow</#if>">${productStoreName!}</td>
        </#if>
      </tr>
      <#-- toggle the row color -->
      <#if rowClass == "2">
       <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#if>
  </#list>
</#if>
<!-- end listBox -->