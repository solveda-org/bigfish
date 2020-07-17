<!-- start customerDetailGeneralInfo.ftl -->
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${uiLabelMap.CustomerNoCaption}</label>
            </div>
            <div class="infoValue">
            	${party?if_exists.partyId!""}
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.CustomerRoleCaption}</label>
            </div>
            <div class="infoValue">
                <#if partyRoles?has_content>
                    <#list partyRoles as partyRole>
                        <#assign roleType = partyRole.getRelatedOne("RoleType")>
                        <#if roleType.roleTypeId=="GUEST_CUSTOMER">
                            <#assign partyRoleType = roleType.description />
                            <#break>
                        <#elseif roleType.roleTypeId=="CUSTOMER" || roleType.roleTypeId=="EMAIL_SUBSCRIBER">
                            <#assign partyRoleType = roleType.description>
                        </#if>
                    </#list>
                </#if>
                ${partyRoleType!""}
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${uiLabelMap.FirstNameCaption}</label>
            </div>
            <div class="infoValue">
                 ${partyFirstName?if_exists!""}
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
                    <#assign userLoginId = userLogins.get(0).userLoginId>
                    <p>${userLoginId?if_exists}</p>
                    <div class="infoIcon">
                        <a href="userDetail?userLoginId=${userLoginId?if_exists}" onMouseover="showTooltip(event,'${uiLabelMap.UserLoginInfo}');" onMouseout="hideTooltip()"><span class="lockIcon"></span></a>
                    </div>
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${uiLabelMap.LastNameCaption}</label>
            </div>
            <div class="infoValue">
                ${partyLastName?if_exists!""}
            </div>
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
                    <#if (mode?has_content)>
                        <select name="statusId" class="short">
                            <#list partyStatusItem as statusItem>
                                <option value="${statusItem.statusId}" <#if statusItem.statusId.equals(party?if_exists.statusId!"") >selected=selected</#if>>
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

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.EmailAddressCaption}</label>
            </div>
            <div class="infoValue">
                ${userEmailAddress!""}
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ExportStatusCaption}</label>
            </div>
            <div class="infoValue">
                <#if IS_DOWNLOADED?has_content && IS_DOWNLOADED == 'Y'>
                   ${uiLabelMap.ExportStatusInfo}
                <#else>
                   ${uiLabelMap.DownloadNewInfo}
                </#if>
            </div>
        </div>
    </div>

    <#if phoneHomeTelecomNumber?has_content>
        <#assign formattedHomePhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneHomeTelecomNumber.areaCode?if_exists, phoneHomeTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
        <#-- Splits the contactNumber -->
        <#if phoneHomeTelecomNumber?exists && phoneHomeTelecomNumber?has_content>
            <#assign telecomHomeNoContactMechId = phoneHomeTelecomNumber.contactMechId!"" />
            <#assign areaCodeHome = phoneHomeTelecomNumber.areaCode?if_exists />
            <#assign contactNumberHome = phoneHomeTelecomNumber.contactNumber?if_exists />
            <#if (contactNumberHome?has_content) && (contactNumberHome?length gt 6)>
                <#assign contactNumber3Home = contactNumberHome.substring(0, 3) />
                <#assign contactNumber4Home = contactNumberHome.substring(3, 7) />
            </#if>
        </#if>
    </#if>
    <#if phoneMobileTelecomNumber?has_content>
        <#assign formattedCellPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneMobileTelecomNumber.areaCode?if_exists, phoneMobileTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
        <#-- Splits the contactNumber -->
        <#if phoneMobileTelecomNumber?exists && phoneMobileTelecomNumber?has_content>
            <#assign telecomMobileNoContactMechId = phoneMobileTelecomNumber.contactMechId!"" />
            <#assign areaCodeMobile = phoneMobileTelecomNumber.areaCode?if_exists />
            <#assign contactNumberMobile = phoneMobileTelecomNumber.contactNumber?if_exists />
            <#if (contactNumberMobile?has_content) && (contactNumberMobile?length gt 6)>
                <#assign contactNumber3Mobile = contactNumberMobile.substring(0, 3) />
                <#assign contactNumber4Mobile = contactNumberMobile.substring(3, 7) />
            </#if>
        </#if>
    </#if>
    <#if phoneWorkTelecomNumber?has_content>
        <#assign formattedWorkPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneWorkTelecomNumber.areaCode?if_exists, phoneWorkTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
        <#-- Splits the contactNumber -->
        <#if phoneWorkTelecomNumber?exists && phoneWorkTelecomNumber?has_content>
            <#if partyPurposeWorkPhone?has_content>
                <#assign extensionWork = partyPurposeWorkPhone.extension?if_exists />
            </#if>
            <#assign telecomWorkNoContactMechId = phoneWorkTelecomNumber.contactMechId!"" />
            <#assign areaCodeWork = phoneWorkTelecomNumber.areaCode?if_exists />
            <#assign contactNumberWork = phoneWorkTelecomNumber.contactNumber?if_exists />
            <#if (contactNumberWork?has_content) && (contactNumberWork?length gt 6)>
                <#assign contactNumber3Work = contactNumberWork.substring(0, 3) />
                <#assign contactNumber4Work = contactNumberWork.substring(3, 7) />
            </#if>
        </#if>
    </#if>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.HomePhoneCaption}</label>
            </div>
            <div class="infoValue">
                ${formattedHomePhone!}
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.OptInCaption}</label>
            </div>
            <div class="entry checkbox short">
                <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="Y" disabled="disabled" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "Y") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "Y") || (!parameters.requireCode?exists && mode?has_content && mode == "add"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="N" disabled="disabled" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "N") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "N"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.CellPhoneCaption}</label>
            </div>
            <div class="infoValue">
                ${formattedCellPhone!}
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.EmailPreferenceCaption}</label>
            </div>
            <div class="infoValue">
                <#if PARTY_EMAIL_PREFERENCE?has_content && PARTY_EMAIL_PREFERENCE.equals("HTML")>
                   ${uiLabelMap.HtmlEmailPreferenceInfo}
                <#elseif PARTY_EMAIL_PREFERENCE?has_content && PARTY_EMAIL_PREFERENCE.equals("TEXT")>
                   ${uiLabelMap.TextEmailPreferenceInfo}
                <#elseif PARTY_EMAIL_PREFERENCE?has_content>
                   ${PARTY_EMAIL_PREFERENCE}
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow ">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.WorkPhoneCaption}</label>
            </div>
            <div class="infoValue">
                ${formattedWorkPhone!}<#if extensionWork?has_content>&nbsp;x${extensionWork!}</#if>
            </div>
        </div>
    </div>
<!-- end customerDetailGeneralInfo.ftl -->


