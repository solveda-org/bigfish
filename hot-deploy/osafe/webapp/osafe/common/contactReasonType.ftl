<#if enumTypes?has_content>
    <#list enumTypes as reasonType>
        <option value="${reasonType.description!}">${reasonType.description!}</option>
    </#list>
</#if>