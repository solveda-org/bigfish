<#if enumTypes?has_content>
    <#list enumTypes as recurrenceFreqType>
        <option value="${recurrenceFreqType.enumCode!}">${recurrenceFreqType.description!}</option>
    </#list>
</#if>