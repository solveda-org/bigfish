<#if bigfishParamList?has_content>
    <input type="hidden" name="parameterFileName" value=${parameters.parameterFileName!parameterFileName}></input>
    <input type="hidden" name="showDetail" value="false"/>
    <table class="osafe" cellspacing="0">
       <thead>
         <tr class="heading">
           <th class="nameCol firstCol">${uiLabelMap.KeyLabel}</th>
           <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
           <th class="valueCol">${uiLabelMap.ValueLabel}</th>
         </tr>
       </thead>
       <tbody>
            <#assign rowClass = "1">
            <#list bigfishParamList  as bigfishParam>
                <#assign hasNext = bigfishParam_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                     <td class="nameCol <#if !hasNext>lastRow</#if>" >
                       <a href="<@ofbizUrl>manageBigfishParameterDetail?key=${bigfishParam.key?if_exists}&amp;parameterFileName=${parameters.parameterFileName?if_exists}&amp;showDetail=true</@ofbizUrl>">${bigfishParam.key!}</a>
                       <input type="hidden" name="key_${bigfishParam_index}" value="${bigfishParam.key!}"></input>
                     </td>
                     <td class="descCol <#if !hasNext>lastRow</#if>">
                       ${bigfishParam.description!}
                       <input type="hidden" name="description_${bigfishParam_index}" value="${bigfishParam.description!""}"></input>
                     </td>
                     <td class="valueCol <#if !hasNext>lastRow</#if>">
                       <input type="text" name="value_${bigfishParam_index}" class="large" id="value_${bigfishParam_index}" value="${parameters.get("value_${bigfishParam_index}")!bigfishParam.value!}" ></input>
                     </td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </tbody>
        </table>
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
