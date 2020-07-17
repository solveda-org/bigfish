<#if enumTypes?has_content>
    <#list enumTypes as ccType>
        <option value="${ccType.enumCode!}">${ccType.description!}</option>
    </#list>
</#if>