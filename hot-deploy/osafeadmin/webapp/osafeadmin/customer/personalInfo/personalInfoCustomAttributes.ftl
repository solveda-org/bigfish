<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttributes = delegator.findByAnd("PartyAttribute", {"partyId" : partyId})?if_exists />
</#if>

<#if customPartyAttributeList?has_content>
   
      <div class ="personalInfoCustomAttributes">
      <#list customPartyAttributeList as customPartyAttribute>
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption">
                    <label><#if customPartyAttribute.Required == 'Y'><span class="required">*</span></#if> ${customPartyAttribute.Caption!}</label>
                </div>
                <div class="<#if customPartyAttribute.Type == 'RADIO_BUTTON' || customPartyAttribute.Type == 'CHECKBOX'>entry checkbox<#else>infoValue</#if>">
                    <#assign attrValue =  request.getParameter("${customPartyAttribute.AttrName}")! />
                    <#if !attrValue?has_content && partyAttributes?has_content>
                        <#list partyAttributes as partyAttribute>
                            <#if partyAttribute.attrName == customPartyAttribute.AttrName>
                                <#assign attrValue = partyAttribute.attrValue!"">
                                <#break>
                            </#if>
                        </#list>
                    </#if>
                    <#if customPartyAttribute.Type == 'ENTRY'>
                        <input type="text" name = "${customPartyAttribute.AttrName}" maxLength = "${customPartyAttribute.MaxLength!}" value="${attrValue!}" />
                    <#elseif customPartyAttribute.Type == 'ENTRY_BOX'>
                        <textarea name = "${customPartyAttribute.AttrName}" maxLength = "${customPartyAttribute.MaxLength!}">${attrValue!}</textarea>
                    <#elseif customPartyAttribute.Type == 'RADIO_BUTTON'>
                        <#assign valueList =  customPartyAttribute.ValueList! />
                        <#if valueList?has_content>
                            <#assign values = valueList?split(',')>
                            <#list values as value>
                                 <#assign valueTrim = value?string?trim />
                                 <input type="radio" name = "${customPartyAttribute.AttrName}" <#if valueTrim?upper_case == attrValue>checked</#if> value="${valueTrim?upper_case}"/>${valueTrim}
                            </#list>
                        </#if>
                    <#elseif customPartyAttribute.Type == 'CHECKBOX'>
                        <#assign valueList =  customPartyAttribute.ValueList! />
                        <#if valueList?has_content>
                            <#assign values = valueList?split(',')>
                            <#list values as value>
                                 <#assign value = value?string?trim />
                                 <#assign valueTrim = value?string?trim />
                                 <input type="checkbox" name="${customPartyAttribute.AttrName}" value="${valueTrim?upper_case}" <#if attrValue.contains(valueTrim?upper_case)>checked</#if>/>${valueTrim}
                            </#list>
                        </#if>
                    <#elseif customPartyAttribute.Type == 'DROP_DOWN'>
                        <#assign valueList =  customPartyAttribute.ValueList! />
                        <#if valueList?has_content>
                            <select id="${customPartyAttribute.AttrName}" name="${customPartyAttribute.AttrName}">
                                <#assign values = valueList?split(',')>
                                <#list values as value>
                                    <#assign value = value?string?trim />
                                    <#assign valueTrim = value?string?trim />
                                    <option value="${valueTrim?upper_case}" <#if valueTrim?upper_case == attrValue>selected</#if>>${valueTrim!}</option>
                                </#list>
                            </select>
                        </#if>
                    <#elseif customPartyAttribute.Type == 'DROP_DOWN_MULTI'>
                        <#assign valueList =  customPartyAttribute.ValueList! />
                        <#if valueList?has_content>
                            <select id="${customPartyAttribute.AttrName}" name="${customPartyAttribute.AttrName}" multiple>
                                <#assign values = valueList?split(',')>
                                <#list values as value>
                                    <#assign value = value?string?trim />
                                    <#assign valueTrim = value?string?trim />
                                    <option value="${valueTrim?upper_case}" <#if attrValue.contains(valueTrim?upper_case)>selected</#if>>${valueTrim!}</option>
                                </#list>
                            </select>
                        </#if>
                    <#elseif customPartyAttribute.Type == 'DATE'>
                        <input class="dateEntry" type="text" name="${customPartyAttribute.AttrName}" value="${attrValue!}"/>
                    </#if>
                </div>
            </div>
        </div>
        <@fieldErrors fieldName="${customPartyAttribute.AttrName}"/>
        </#list>
    </div>
   
</#if>
