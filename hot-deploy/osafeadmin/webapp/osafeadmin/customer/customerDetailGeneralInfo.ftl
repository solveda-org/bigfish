<!-- start customerDetailGeneralInfo.ftl -->
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.CustomerNoCaption}</label>
     </div>
     <div class="infoValue medium">
       <#if party?has_content>${party.partyId!""}</#if>
     </div>
     <div class="infoValue">
      <label>${uiLabelMap.CustomerStatusCaption}</label>
     </div>
     <div class="infoValue">
        <#if party?has_content>
           <#assign statusItem = party.getString("statusId")>
           <#if statusItem?has_content && statusItem=="PARTY_ENABLED">
              ${uiLabelMap.CustomerEnabledInfo}
           <#else>
              ${uiLabelMap.CustomerDisabledInfo}
           </#if>
        </#if>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.CustomerNameCaption}</label>
     </div>
     <div class="infoValue medium">
        <#if party?has_content>
               <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(party, true)>
               <#if partyName?has_content>
                  ${partyName}
               <#else>
                  (${uiLabelMap.PartyNoNameFound})
               </#if>
        </#if>
     </div>
     <div class="infoValue">
      <label>${uiLabelMap.CustomerRoleCaption}</label>
     </div>
     <div class="infoValue">
         <#assign partyRoles = delegator.findByAnd("PartyRole", {"partyId", party.partyId})>
         <#if partyRoles?has_content>
          <#list partyRoles as partyRole>
              <#assign roleType = partyRole.getRelatedOne("RoleType")>
              <#if roleType.roleTypeId=="GUEST_CUSTOMER">
                <#assign partyRoleType = roleType.description />
                <#break>
              </#if>
              <#if roleType.roleTypeId=="CUSTOMER" || roleType.roleTypeId=="EMAIL_SUBSCRIBER">
                 <#assign partyRoleType = roleType.description>
               </#if>
          </#list>
         <#else>
          <#assign partyRoleType = "">
         </#if>
         ${partyRoleType!""}
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.UserLoginCaption}</label>
     </div>
     <div class="infoValue medium">
        <#assign userLogins = party.getRelated("UserLogin")>
        <#if userLogins?has_content>
          <#assign userLoginId = userLogins.get(0).userLoginId>
        </#if>
        <#if userLoginId?has_content>
           ${userLoginId}
        <#else>
           ${uiLabelMap.NoUserLoginIdInfo}
        </#if>
     </div>
     <div class="infoValue">
      <label>${uiLabelMap.ExportStatusCaption}</label>
     </div>
     <div class="infoValue">
        <#if party?has_content>
            <#assign partyAttrIsDownload = delegator.findOne("PartyAttribute", {"partyId" : party.partyId, "attrName" : "IS_DOWNLOADED"}, true)>
            <#assign downloadStatus = partyAttrIsDownload.attrValue!"">
            <#if downloadStatus?has_content && downloadStatus == 'Y'>
               ${uiLabelMap.ExportStatusInfo}
            <#else>
               ${uiLabelMap.DownloadNewInfo}
            </#if>
        </#if>
     </div>
   </div>
</div>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.EmailAddressCaption}</label>
     </div>
     <div class="infoValue medium">
         <#if partyPrimaryEmailContactMechValueMap?has_content>
             <#assign partyPrimaryEmailContactMech = partyPrimaryEmailContactMechValueMap.contactMech>
             <p>${partyPrimaryEmailContactMech.infoString}</p>
         </#if>
     </div>
     <div class="infoValue">
      <label>${uiLabelMap.OptInCaption}</label>
     </div>
     <div class="infoValue">
        <#if partyPrimaryEmailContactMechValueMap?has_content>
            <#assign partyContactMech = partyPrimaryEmailContactMechValueMap.partyContactMech?if_exists />
            <#assign allowSolicitation = partyContactMech.allowSolicitation?if_exists />
            <#if allowSolicitation?has_content && allowSolicitation=='N'>
               ${uiLabelMap.NoInfo}
            <#else>
               ${uiLabelMap.YesInfo}
            </#if>
        </#if>
     </div> 
   </div>
</div>

<!-- end customerDetailGeneralInfo.ftl -->


