<#if enumTypes?has_content>
    <#list enumTypes as titleType>
        <option value="${titleType.description!}">${titleType.description!}</option>
    </#list>
</#if>