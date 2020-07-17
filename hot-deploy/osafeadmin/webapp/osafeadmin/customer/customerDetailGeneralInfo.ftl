<!-- start customerDetailGeneralInfo.ftl -->
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
                    <#if (method?has_content)>
                        <span class="required">*</span>
                    </#if>
                    ${uiLabelMap.CustomerNoCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content && method == "add")>
                    <#if !parameters.partyId?has_content>
                        <#assign partySeqId = Static["com.osafe.util.OsafeAdminUtil"].getNextSeqId(delegator, "Party", "Party", "partyId")!""/>
                    </#if>
                    <input type="text" name="partyId" id="partyId" maxlength="20" value="${parameters.partyId!partySeqId!""}"/>
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
                <label>${uiLabelMap.CustomerRoleCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content && method == "add")>
                    <input type="hidden" name="roleTypeId" id="roleTypeId" maxlength="20" value="CUSTOMER"/>
                    <#assign roleType = delegator.findOne("RoleType", {"roleTypeId" : "CUSTOMER"}, false)?if_exists />
                    ${roleType?if_exists.description!"CUSTOMER"}
                <#else>
                    <#if partyRoles?has_content>
                        <#list partyRoles as partyRole>
                            <#assign roleType = partyRole.getRelatedOne("RoleType")>
                            <#if roleType.roleTypeId !="_NA_">
                                <#assign partyRoleType = roleType.description>
			                    ${partyRoleType!""}
			                    <#if partyRole_has_next>,</#if>
                            </#if>
                        </#list>
                    </#if>
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.EmailAddressCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                    <input type="hidden" name="emailAddressContactMechId" id="emailAddressContactMechId" maxlength="100" value="${userEmailContactMech?if_exists.contactMechId!""}"/>
                    <input type="text" name="emailAddress" id="emailAddress" maxlength="100" value="${parameters.emailAddress!userEmailAddress!""}"/>
                <#else>
                    ${userEmailAddress!""}
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.UserLoginCaption}</label>
            </div>
            <div class="infoValue">
                <#if userLogins?has_content>
                    <#list userLogins as userLogin>
                        <#if userLogin.enabled?has_content && userLogin.enabled == 'Y'>
                            <#assign userLoginId = userLogin.userLoginId!"">
                            <#break>
                        </#if>
                    </#list>
                    <#if !userLoginId?has_content>
                        <#assign userLoginId = userLogins.get(0).userLoginId>
                    </#if>
                    <p>${userLoginId?if_exists}</p>
                    <div class="infoIcon">
                        <a href="userDetail?userLoginId=${userLoginId?if_exists}" onMouseover="showTooltip(event,'${uiLabelMap.UserLoginInfo}');" onMouseout="hideTooltip()"><span class="lockIcon"></span></a>
                        <#if userFbUser?has_content && userFbUser =="TRUE">
                        	<span class="facebookIcon"></span>
                        </#if>
                    </div>
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.OptInCaption}</label>
            </div>
            <#if (method?has_content)>
                <div class="infoValue checkbox short">
                    <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="Y" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "Y") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "Y") || (!parameters.allowSolicitation?exists && method?has_content && method == "add"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                    <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="N" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "N") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "N"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
                </div>
            <#else>
                <div class="infoValue checkbox short">
                    <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="Y" disabled="disabled" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "Y") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "Y"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                    <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="N" disabled="disabled" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "N") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "N"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
                </div>
            </#if>
        </div>
    </div>

    <#assign partyStatusItem = delegator.findByAnd("StatusItem", {"statusTypeId" : "PARTY_STATUS"}, ["sequenceId"])?if_exists />
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.CustomerStatusCaption}</label>
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
            <div class="infoIcon">
                <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.CustomerStatusChangeInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TextAlertCaption}</label>
            </div>
            <#if (method?has_content)>
                <div class="infoValue checkbox short">
                    <input class="checkBoxEntry" type="radio" id="textPreference" name="textPreference" value="Y" <#if ((parameters.textPreference?exists && parameters.textPreference?string == "Y") || (userTextPreference?exists && userTextPreference?string == "Y"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                    <input class="checkBoxEntry" type="radio" id="textPreference" name="textPreference" value="N" <#if ((parameters.textPreference?exists && parameters.textPreference?string == "N") || (userTextPreference?exists && userTextPreference?string == "N") || (!parameters.textPreference?exists && method?has_content && method == "add"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
                </div>
            <#else>
                <div class="infoValue checkbox short">
                    <input class="checkBoxEntry" type="radio" id="textPreference" name="textPreference" value="Y" disabled="disabled" <#if ((parameters.textPreference?exists && parameters.textPreference?string == "Y") || (userTextPreference?exists && userTextPreference?string == "Y"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                    <input class="checkBoxEntry" type="radio" id="textPreference" name="textPreference" value="N" disabled="disabled" <#if ((parameters.textPreference?exists && parameters.textPreference?string == "N") || (userTextPreference?exists && userTextPreference?string == "N"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
                </div>
            </#if>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ExportStatusCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                     <#assign selectedExportStatus = parameters.exportStatus!IS_DOWNLOADED!"N">
                    <select name="exportStatus" class="short">
                        <option value="N" <#if selectedExportStatus == "N" >selected=selected</#if>>${uiLabelMap.DownloadNewInfo}</option>
                        <option value="Y" <#if selectedExportStatus == "Y" >selected=selected</#if>>${uiLabelMap.ExportStatusInfo}</option>
                    </select>
                <#else>
                    <#if IS_DOWNLOADED?has_content && IS_DOWNLOADED == 'Y'>
                       ${uiLabelMap.ExportStatusInfo}
                    <#else>
                       ${uiLabelMap.DownloadNewInfo}
                    </#if>
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.EmailPreferenceCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                     <#assign selectedEmailPreference = parameters.emailPreference!PARTY_EMAIL_PREFERENCE!"">
                    <select name="emailPreference" class="short">
                        <option value="HTML" <#if selectedEmailPreference?has_content && selectedEmailPreference.equals("HTML") >selected=selected</#if>>${uiLabelMap.HtmlEmailPreferenceInfo}</option>
                        <option value="TEXT" <#if selectedEmailPreference?has_content && selectedEmailPreference.equals("TEXT") >selected=selected</#if>>${uiLabelMap.TextEmailPreferenceInfo}</option>
                    </select>
                <#else>
                    <#if PARTY_EMAIL_PREFERENCE?has_content && PARTY_EMAIL_PREFERENCE.equals("HTML")>
                       ${uiLabelMap.HtmlEmailPreferenceInfo}
                    <#elseif PARTY_EMAIL_PREFERENCE?has_content && PARTY_EMAIL_PREFERENCE.equals("TEXT")>
                       ${uiLabelMap.TextEmailPreferenceInfo}
                    <#elseif PARTY_EMAIL_PREFERENCE?has_content>
                       ${PARTY_EMAIL_PREFERENCE}
                    </#if>
                </#if>
            </div>
        </div>
    </div>
    
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.OrganizationCaption}</label>
            </div>
            <div class="infoValue">
                <#assign groupName = ""/>
                <#if parameters.organizationPartyId?has_content || partyOrganization?has_content>
                    <#assign organizationId = parameters.organizationPartyId!partyOrganization.partyIdFrom!""/>
                </#if>
                
                <#if organizationList?has_content>
                    <select name="organizationPartyId">
                    	<option value="" <#if !organizationId?has_content>selected=selected</#if>>${uiLabelMap.NALabel}</option>
                        <#list organizationList as organization>
                            <#assign partyGroup = delegator.findByPrimaryKey("PartyGroup", {"partyId", organization.partyId})/>
                            <option value="${organization.partyId}" <#if organizationId?has_content && organizationId == organization.partyId>selected=selected</#if>>${partyGroup.groupName!}</option>
                        </#list>
                    </select>
                </#if>
                <input type="hidden" name="existingOrganizationId" value="${parameters.existingOrganizationId!organizationId!}"/>
            </div>
        </div>
    </div>

<!-- end customerDetailGeneralInfo.ftl -->