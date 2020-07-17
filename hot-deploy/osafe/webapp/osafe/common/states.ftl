
<#-- Here is some alternate code to get states limited to a region -->
<#if requestParameters.CUSTOMER_COUNTRY?exists>
    <#assign stateAssocs = Static["org.ofbiz.common.CommonWorkers"].getAssociatedStateList(delegator,requestParameters.CUSTOMER_COUNTRY)>
<#else>
    <#assign stateAssocs = Static["org.ofbiz.common.CommonWorkers"].getAssociatedStateList(delegator, "")>
</#if>

<#list stateAssocs as stateAssoc>
    <option value='${stateAssoc.geoId}'>${stateAssoc.geoName?default(stateAssoc.geoId)}</option>
</#list>

