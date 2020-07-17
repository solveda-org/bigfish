<!-- start customerDetailPaymentInfo.ftl -->
<tr>
 <div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.CustomerIdCaption}</label>
     </div>
     <div class="infoValue">
       ${partyId!""}
     </div>
   </div>
  </div>
<#if partyPaymentContactMechValueMap?has_content>

    <#assign contactMech = partyPaymentContactMechValueMap.contactMech?if_exists />
    <#assign contactMechType = partyPaymentContactMechValueMap.contactMechType?if_exists />
    <#assign partyContactMech = partyPaymentContactMechValueMap.partyContactMech?if_exists />
    <#-- Start Addresses -->
    <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
    <div class="infoRow">
      <div class="infoEntry">
         <div class="infoCaption">
            <label>${uiLabelMap.AddressCaption}</label>
         </div>
         <div class="infoValue">
            <#assign postalAddress = partyPaymentContactMechValueMap.postalAddress>
            <#if postalAddress?has_content>
                <#if postalAddress.toName?has_content><p>${postalAddress.toName}</p></#if>
                <p>${postalAddress.address1}</p>
                <#if postalAddress.address2?has_content><p>${postalAddress.address2}</p></#if>
                <p>${postalAddress.city?if_exists}<#if postalAddress.stateProvinceGeoId?has_content>, ${postalAddress.stateProvinceGeoId} </#if>
                ${postalAddress.postalCode?if_exists}</p>
                ${postalAddress.countryGeoId?if_exists}</p>
            </#if>
         </div>
      </div> <#-- end infoEntry -->
    </div> <#-- end infoRow -->
    <#-- End Addresses -->
    </#if>

    <#-- Customer Email-->
    <#-- Start Email Address -->
          <div class="infoRow">
           <div class="infoEntry">
             <div class="infoCaption">
                <label>${uiLabelMap.EmailAddressCaption}</label>
             </div>
             <div class="infoValue">
                 <#if partyPrimaryEmailContactMechValueMap?has_content>
                     <#assign partyPrimaryEmailContactMech = partyPrimaryEmailContactMechValueMap.contactMech>
                      <p>${partyPrimaryEmailContactMech.infoString}</p>
                 </#if>
             </div>
           </div> <#-- end infoEntry -->
          </div> <#-- end infoRow -->
    <#-- End Email Address -->
    
    <#-- Start Phone Number -->
	 <#if partyPhoneHomeContactMechValueMap?has_content>
	    <#assign partyHomePhone = partyPhoneHomeContactMechValueMap.telecomNumber>
	 </#if>
	 <#if partyPhoneMobileContactMechValueMap?has_content>
	    <#assign partyMobilePhone = partyPhoneMobileContactMechValueMap.telecomNumber/>
	 </#if>
	 <div class="infoRow">
	   <#if partyHomePhone?has_content>
         <div class="infoEntry">
           <div class="infoCaption">
             <label>${uiLabelMap.HomePhoneCaption}</label>
           </div>
           <div class="infoValue">
             <#assign fullPhone = "${partyHomePhone.areaCode?if_exists}" + "${partyHomePhone.contactNumber?if_exists}"/>
             <#if fullPhone?has_content>
	             <#assign fullPhone = Static["org.ofbiz.base.util.UtilValidate"].stripCharsInBag(fullPhone,Static["org.ofbiz.base.util.UtilValidate"].phoneNumberDelimiters)/>
	             <#if (fullPhone?length > 9)>
	               ${fullPhone?substring(0,3)}-${fullPhone?substring(3,6)}-${fullPhone?substring(6)}
	             <#else>
	               ${fullPhone}
	             </#if>
             </#if>
           </div>
         </div> 
       </#if>
       <#if partyMobilePhone?has_content>
         <div class="infoEntry">
           <div class="infoCaption">
             <label>${uiLabelMap.MobilePhoneCaption}</label>
           </div>
           <div class="infoValue">
             <#assign fullPhone = "${partyMobilePhone.areaCode?if_exists}" + "${partyMobilePhone.contactNumber?if_exists}"/>
             <#if fullPhone?has_content>
	             <#assign fullPhone = Static["org.ofbiz.base.util.UtilValidate"].stripCharsInBag(fullPhone,Static["org.ofbiz.base.util.UtilValidate"].phoneNumberDelimiters)/>
	             <#if (fullPhone?length > 9)>
	               ${fullPhone?substring(0,3)}-${fullPhone?substring(3,6)}-${fullPhone?substring(6)}
	             <#else>
	               ${fullPhone}
	             </#if>
             </#if>
           </div>
         </div>
       </#if>
     </div>
    <#-- End Phone Number -->
    
</#if>
<!-- end customerDetailPaymentInfo.ftl -->