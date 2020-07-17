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
           <p>${userLoginId}</p>
           <div class="infoIcon">
           		<a href="userDetail?userLoginId=${userLoginId}" onMouseover="showTooltip(event,'${uiLabelMap.UserLoginInfo}');" onMouseout="hideTooltip()"><span class="lockIcon"></span></a>
           </div>
        <#else>
           ${uiLabelMap.NoUserLoginIdInfo}
        </#if>
     </div>
     <div class="infoValue">
      <label>${uiLabelMap.ExportStatusCaption}</label>
     </div>
     <div class="infoValue">
        <#if party?has_content>
            <#assign partyAttrIsDownload = delegator.findOne("PartyAttribute", {"partyId" : party.partyId, "attrName" : "IS_DOWNLOADED"}, false)?if_exists>
            <#if partyAttrIsDownload?has_content>
              <#assign downloadStatus = partyAttrIsDownload.attrValue!"">
            </#if>
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

<#-- Start Phone Number -->
      <#assign partyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", party.partyId))/>
        <#-- Home Phone -->
      <#if partyContactDetails?has_content>
        <#assign partyHomePhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactDetails,{"contactMechPurposeTypeId" : "PHONE_HOME"}) />
      </#if>
      <#if partyHomePhoneDetails?has_content>
        <#assign partyHomePhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyHomePhoneDetails?if_exists)?if_exists />
        <#assign partyHomePhoneDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyHomePhoneDetails?if_exists)?if_exists />
        <#assign formattedHomePhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyHomePhoneDetail.areaCode?if_exists, partyHomePhoneDetail.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
      </#if>
      
        <#-- Work Phone -->
      <#if partyContactDetails?has_content>
        <#assign partyWorkPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactDetails,{"contactMechPurposeTypeId" : "PHONE_WORK"}) />
      </#if>
      <#if partyWorkPhoneDetails?has_content>
        <#assign partyWorkPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyWorkPhoneDetails?if_exists)?if_exists />
        <#assign partyWorkPhoneDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyWorkPhoneDetails?if_exists)?if_exists />
        <#assign formattedWorkPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyWorkPhoneDetail.areaCode?if_exists, partyWorkPhoneDetail.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
        <#if partyWorkPhoneDetail?has_content && partyWorkPhoneDetail.extension?has_content>
          <#assign partyWorkPhoneExt = partyWorkPhoneDetail.extension!/> 
        </#if>
      </#if>
        
        <#-- Cell Phone --> 
      <#if partyContactDetails?has_content>
        <#assign partyCellPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactDetails,{"contactMechPurposeTypeId" : "PHONE_MOBILE"}) />
      </#if>
      <#if partyCellPhoneDetails?has_content>
        <#assign partyCellPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyCellPhoneDetails?if_exists)?if_exists />
        <#assign partyCellPhoneDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyCellPhoneDetails?if_exists)?if_exists />
        <#assign formattedCellPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyCellPhoneDetail.areaCode?if_exists, partyCellPhoneDetail.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
      </#if>
      
<#if formattedHomePhone?has_content || formattedWorkPhone?has_content>
<div class="infoRow">
   <div class="infoEntry">
   <#if formattedHomePhone?has_content>
     <div class="infoCaption">
      <label>${uiLabelMap.HomePhoneCaption}</label>
     </div>
     <div class="infoValue medium">
       ${formattedHomePhone!}
     </div>
   </#if>
   <#if formattedWorkPhone?has_content>
     <div class="infoValue">
      <label>${uiLabelMap.WorkPhoneCaption}</label>
     </div>
     <div class="infoValue">
       ${formattedWorkPhone!}<#if partyWorkPhoneExt?has_content>&nbsp;x${partyWorkPhoneDetail.extension!}</#if>
     </div>
    </#if> 
   </div>
</div>
</#if>

<#if formattedCellPhone?has_content>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.CellPhoneCaption}</label>
     </div>
     <div class="infoValue medium">
       ${formattedCellPhone!}
     </div>
   </div>
</div>
</#if>
<!-- end customerDetailGeneralInfo.ftl -->


