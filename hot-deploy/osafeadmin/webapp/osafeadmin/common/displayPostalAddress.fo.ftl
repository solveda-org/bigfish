<#if postalAddress?has_content>
    <#if displayFormat == "MULTI_LINE_FULL_ADDRESS">
        <#if postalAddress.toName?has_content>
            <fo:block>${postalAddress.toName?if_exists}</fo:block>
        </#if>
        <fo:block>
            <#if postalAddress.address1?has_content>${postalAddress.address1?if_exists}</#if>
            <#if postalAddress.address2?has_content>${postalAddress.address2?if_exists}</#if>
            <#if postalAddress.address3?has_content>${postalAddress.address3?if_exists}</#if>
        </fo:block>
         
        <fo:block>
            <#if postalAddress.city?has_content>${postalAddress.city?if_exists}, </#if>
            <#if postalAddress.stateProvinceGeoId?has_content && postalAddress.stateProvinceGeoId != '_NA_'>
                <#if STATE?has_content && STATE == 'LONG'>
                    <#assign stateProvince = delegator.findByPrimaryKey("Geo", {"geoId" : postalAddress.stateProvinceGeoId})?if_exists>
                    ${stateProvince.geoName!stateProvince.geoId!""}
                <#else>
                    ${postalAddress.stateProvinceGeoId}
                </#if>
            </#if>
            <#if postalAddress.postalCode?has_content && postalAddress.postalCode != '_NA_'>
                <#if ZIP?has_content && ZIP == 'LONG'>
                    ${postalAddress.postalCode} 
                    <#if postalAddress.postalCodeExt?has_content && postalAddress.postalCodeExt != '_NA_'>
                    - ${postalAddress.postalCodeExt} 
                    </#if>
                <#else>
                    ${postalAddress.postalCode}
                </#if>
            </#if>
            <#if postalAddress.countryGeoId?has_content>
                <#if COUNTRY?has_content && COUNTRY == 'LONG'>
                    <#assign country = delegator.findByPrimaryKey("Geo", {"geoId" : postalAddress.countryGeoId})?if_exists>
                    ${country.geoName!country.geoId!""}
                <#else>
                   ${postalAddress.countryGeoId}
                </#if>
            </#if>
        </fo:block>
    <#elseif displayFormat == "SINGLE_LINE_FULL_ADDRESS">
        <fo:block>
            <#if postalAddress.toName?has_content>${postalAddress.toName?if_exists}</#if>
            <#if postalAddress.address1?has_content>${postalAddress.address1?if_exists}</#if>
            <#if postalAddress.address2?has_content>${postalAddress.address2?if_exists}</#if>
            <#if postalAddress.address3?has_content>${postalAddress.address3?if_exists}</#if>
            <#if postalAddress.city?has_content  && postalAddress.city != '_NA_'>${postalAddress.city?if_exists}, </#if>
            <#if postalAddress.stateProvinceGeoId?has_content && postalAddress.stateProvinceGeoId != '_NA_'>
                <#if STATE?has_content && STATE == 'LONG'>
                    <#assign stateProvince = delegator.findByPrimaryKey("Geo", {"geoId" : postalAddress.stateProvinceGeoId})?if_exists>
                    ${stateProvince.geoName!stateProvince.geoId!""}
                <#else>
                    ${postalAddress.stateProvinceGeoId}
                </#if>
            </#if>
            <#if postalAddress.postalCode?has_content && postalAddress.postalCode != '_NA_'>
                <#if ZIP?has_content && ZIP == 'LONG'>
                    ${postalAddress.postalCode} 
                    <#if postalAddress.postalCodeExt?has_content && postalAddress.postalCodeExt != '_NA_'>
                    - ${postalAddress.postalCodeExt} 
                    </#if>
                <#else>
                    ${postalAddress.postalCode}
                </#if>
            </#if>
            <#if postalAddress.countryGeoId?has_content>
                <#if COUNTRY?has_content && COUNTRY == 'LONG'>
                    <#assign country = delegator.findByPrimaryKey("Geo", {"geoId" : postalAddress.countryGeoId})?if_exists>
                    ${country.geoName!country.geoId!""}
                <#else>
                   ${postalAddress.countryGeoId}
                </#if>
            </#if>
        </fo:block>
    <#elseif displayFormat == "MULTI_LINE_STREET_CITY">
        <#if postalAddress.toName?has_content>
            <fo:block>${postalAddress.toName?if_exists}</fo:block>
        </#if>
        <#if postalAddress.address1?has_content>
            <fo:block>
                ${postalAddress.address1?if_exists}
            </fo:block>
        </#if>
        <#if postalAddress.city?has_content>
            <fo:block>
                ${postalAddress.city?if_exists}
            </fo:block>
        </#if>
    <#elseif displayFormat == "SINGLE_LINE_STREET_CITY">
        <fo:block>
            <#if postalAddress.toName?has_content>${postalAddress.toName?if_exists}</#if>
            <#if postalAddress.address1?has_content>${postalAddress.address1?if_exists}</#if>
            <#if postalAddress.city?has_content  && postalAddress.city != '_NA_'>${postalAddress.city?if_exists}, </#if>
        </fo:block>
    </#if>
</#if>