<!-- start organizationDetailGeneralInfo.ftl -->
    <input type="hidden" name="roleTypeId" id="roleTypeId" maxlength="20" value="INTERNAL_ORGANIZATIO"/>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
                    <#if (method?has_content)>
                        <span class="required">*</span>
                    </#if>
                    ${uiLabelMap.OrgNoCaption}
                </label>
            </div>
            <div class="infoValue">
                <#if (method?has_content && method == "add")>
                    <input type="text" name="partyId" id="partyId" maxlength="20" value="${parameters.partyId!""}"/>
                <#else>
                    <input type="hidden" name="partyId" id="partyId" maxlength="20" value="${party?if_exists.partyId!parameters.partyId!""}" />
                    ${party?if_exists.partyId!parameters.partyId!""}
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if (method?has_content)><span class="required">*</span></#if>${uiLabelMap.NameCaption}</label>
            </div>
            <div class="infoValue">
                <#assign groupName = ""/>
                <#if partyGroup?has_content && !errorMessageList?has_content>
                    <#assign groupName = partyGroup.groupName!""/>
                </#if>
                <#if (method?has_content)>
                    <input type="text" name="groupName" id="groupName" maxlength="100" value="${parameters.groupName!groupName!""}"/>
                <#else>
                    ${parameters.groupName!groupName!""}
                </#if>
            </div>
        </div>
    </div>

    <#assign partyStatusItem = delegator.findByAnd("StatusItem", {"statusTypeId" : "PARTY_STATUS"}, ["sequenceId"])?if_exists />
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.StatusCaption}</label>
            </div>
            <div class="infoValue">
                <#if (partyStatusItem?has_content)>
                    <#if (method?has_content)>
                        <select name="statusId" class="short">
                            <#assign selectedStatusId = parameters.statusId!party?if_exists.statusId!"">
                            <#list partyStatusItem as statusItem>
                                <option value="${statusItem.statusId}" <#if statusItem.statusId.equals(selectedStatusId!"")>selected=selected</#if>>
                                    ${statusItem.description}
                                </option>
                            </#list>
                        </select>
                    <#else>
                        <#list partyStatusItem as statusItem>
                            <#if statusItem.statusId.equals(party?if_exists.statusId!"") >${statusItem.description}</#if>
                        </#list>
                    </#if>
                </#if>
            </div>
        </div>
    </div>

<!-- end organizationDetailGeneralInfo.ftl -->