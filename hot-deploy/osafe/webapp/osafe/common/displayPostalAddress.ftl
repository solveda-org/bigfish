<#if postalAddress?has_content>
 <#if displayFormat == "MULTI_LINE_FULL_ADDRESS">
    <ul class="displayList address">
        <#if postalAddress.toName?has_content>
            <li>
                <div>
                    <span>${postalAddress.toName}</span>
                </div>
            </li>
        </#if>
        <#if postalAddress.address1?has_content>
            <li>
                <div>
                    <span>${postalAddress.address1}</span>
                </div>
            </li>
        </#if>
        <#if postalAddress.address2?has_content>
            <li>
                <div>
                    <span>${postalAddress.address2}</span>
                </div>
            </li>
        </#if>
        <#if postalAddress.address3?has_content>
            <li>
                <div>
                    <span>${postalAddress.address3}</span>
                </div>
            </li>
        </#if>
        <li>
            <div>
                <#if postalAddress.city?has_content  && postalAddress.city != '_NA_'>
                    <span>${postalAddress.city!},</span>
                </#if>
                <#if postalAddress.stateProvinceGeoId?has_content && postalAddress.stateProvinceGeoId != '_NA_'>
                    <span>
                        <#if STATE?has_content && STATE == 'LONG'>
                            <#assign stateProvince = delegator.findByPrimaryKeyCache("Geo", {"geoId" : postalAddress.stateProvinceGeoId})?if_exists>
                            ${stateProvince.geoName!stateProvince.geoId!""}
                        <#else>
                            ${postalAddress.stateProvinceGeoId}
                        </#if>
                    </span>
                </#if>
                <#if postalAddress.postalCode?has_content && postalAddress.postalCode != '_NA_'>
                    <span>
                        <#if ZIP?has_content && ZIP == 'LONG'>
                            ${postalAddress.postalCode} 
                            <#if postalAddress.postalCodeExt?has_content && postalAddress.postalCodeExt != '_NA_'>
                            - ${postalAddress.postalCodeExt} 
                            </#if>
                        <#else>
                            ${postalAddress.postalCode}
                        </#if>
                    </span>
                </#if>
           </div>
        </li>
        <#if postalAddress.countryGeoId?has_content>
            <li>
                <div>
                    <span>
                        <#if COUNTRY?has_content && COUNTRY == 'LONG'>
                            <#assign country = delegator.findByPrimaryKeyCache("Geo", {"geoId" : postalAddress.countryGeoId})?if_exists>
                            ${country.geoName!country.geoId!""}
                        <#else>
                           ${postalAddress.countryGeoId}
                        </#if>
                    </span>
                </div>
            </li>
        </#if>
   </ul>
 <#elseif displayFormat == "SINGLE_LINE_FULL_ADDRESS">
    <ul class="displayList address">
      <li>
       <div>
        <#if postalAddress.toName?has_content>
                    <span>${postalAddress.toName}</span>
        </#if>
        <#if postalAddress.address1?has_content>
                    <span>${postalAddress.address1}</span>
        </#if>
        <#if postalAddress.address2?has_content>
                    <span>${postalAddress.address2}</span>
        </#if>
        <#if postalAddress.address3?has_content>
                    <span>${postalAddress.address3}</span>
        </#if>
        <#if postalAddress.city?has_content  && postalAddress.city != '_NA_'>
            <span>${postalAddress.city!},</span>
        </#if>
        <#if postalAddress.stateProvinceGeoId?has_content && postalAddress.stateProvinceGeoId != '_NA_'>
            <span>
                <#if STATE?has_content && STATE == 'LONG'>
                    <#assign stateProvince = delegator.findByPrimaryKeyCache("Geo", {"geoId" : postalAddress.stateProvinceGeoId})?if_exists>
                    ${stateProvince.geoName!stateProvince.geoId!""}
                <#else>
                    ${postalAddress.stateProvinceGeoId}
                </#if>
            </span>
        </#if>
        <#if postalAddress.postalCode?has_content && postalAddress.postalCode != '_NA_'>
            <span>
                <#if ZIP?has_content && ZIP == 'LONG'>
                    ${postalAddress.postalCode} 
                    <#if postalAddress.postalCodeExt?has_content && postalAddress.postalCodeExt != '_NA_'>
                    - ${postalAddress.postalCodeExt} 
                    </#if>
                <#else>
                    ${postalAddress.postalCode}
                </#if>
            </span>
        </#if>
        <#if postalAddress.countryGeoId?has_content>
            <span>
                <#if COUNTRY?has_content && COUNTRY == 'LONG'>
                    <#assign country = delegator.findByPrimaryKeyCache("Geo", {"geoId" : postalAddress.countryGeoId})?if_exists>
                    ${country.geoName!country.geoId!""}
                <#else>
                   ${postalAddress.countryGeoId}
                </#if>
            </span>
        </#if>
       </div>
      </li>
   </ul> 
 <#elseif displayFormat == "MULTI_LINE_STREET_CITY">
    <ul class="displayList address">
        <#if postalAddress.toName?has_content>
            <li>
                <div>
                    <span>${postalAddress.toName}</span>
                </div>
            </li>
        </#if>
        <#if postalAddress.address1?has_content>
            <li>
                <div>
                    <span>${postalAddress.address1},</span>
                </div>
            </li>
        </#if>
        <#if postalAddress.city?has_content  && postalAddress.city != '_NA_'>
	        <li>
	            <div>
	                    <span>${postalAddress.city!}</span>
	           </div>
	        </li>
        </#if>
   </ul> 
 <#elseif displayFormat == "SINGLE_LINE_STREET_CITY">
    <ul class="displayList address">
      <li>
       <div>
        <#if postalAddress.toName?has_content>
                    <span>${postalAddress.toName}</span>
        </#if>
        <#if postalAddress.address1?has_content>
                    <span>${postalAddress.address1},</span>
        </#if>
        <#if postalAddress.city?has_content && postalAddress.city != '_NA_'>
                    <span>${postalAddress.city!}</span>
        </#if>
       </div>
      </li>
   </ul> 
 <#elseif displayFormat == "MULTI_LINE_STREET_CITY_POSTAL_CODE">
    <ul class="displayList address">
        <#if postalAddress.address1?has_content>
            <li>
                <div>
                    <span>${postalAddress.address1},</span>
                </div>
            </li>
        </#if>
        <#if postalAddress.city?has_content  && postalAddress.city != '_NA_'>
	        <li>
	            <div>
	                    <span>${postalAddress.city!}</span>
	           </div>
	        </li>
        </#if>
        <#if postalAddress.postalCode?has_content && postalAddress.postalCode != '_NA_'>
	        <li>
	            <div>
                    <span>
                        <#if ZIP?has_content && ZIP == 'LONG'>
                            ${postalAddress.postalCode} 
                            <#if postalAddress.postalCodeExt?has_content && postalAddress.postalCodeExt != '_NA_'>
                            - ${postalAddress.postalCodeExt} 
                            </#if>
                        <#else>
                            ${postalAddress.postalCode}
                        </#if>
                    </span>
	           </div>
	        </li>
        </#if>
   </ul> 
 <#elseif displayFormat == "SINGLE_LINE_STREET_CITY_POSTAL_CODE">
    <ul class="displayList address">
      <li>
       <div>
        <#if postalAddress.address1?has_content>
                    <span>${postalAddress.address1},</span>
        </#if>
        <#if postalAddress.city?has_content && postalAddress.city != '_NA_'>
                    <span>${postalAddress.city!} </span>
        </#if>
        <#if postalAddress.postalCode?has_content && postalAddress.postalCode != '_NA_'>
            <span>
                <#if ZIP?has_content && ZIP == 'LONG'>
                    ${postalAddress.postalCode} 
                    <#if postalAddress.postalCodeExt?has_content && postalAddress.postalCodeExt != '_NA_'>
                    - ${postalAddress.postalCodeExt} 
                    </#if>
                <#else>
                    ${postalAddress.postalCode}
                </#if>
            </span>
        </#if>
       </div>
      </li>
   </ul> 
 <#elseif displayFormat == "SINGLE_LINE_NICKNAME">
    <ul class="displayList address">
      <li>
       <div>
         <#if postalAddress.attnName?has_content>
           <span>${postalAddress.attnName!}</span>
         <#else>
           <span>${postalAddress.address1!}</span>
        </#if>
       </div>
      </li>
   </ul> 
 </#if>
</#if>
