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
                <label>
                    <#if (method?has_content)>
                        <span class="required">*</span>
                    </#if>
                    ${uiLabelMap.FirstNameCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                    <input type="text" name="firstName" id="firstName" maxlength="100" value="${parameters.firstName!person?if_exists.firstName!""}"/>
                <#else>
                    ${person?if_exists.firstName!""}
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
                <label>
                    <#if (method?has_content)>
                        <span class="required">*</span>
                    </#if>
                    ${uiLabelMap.LastNameCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                    <input type="text" name="lastName" id="lastName" maxlength="100" value="${parameters.lastName!person?if_exists.lastName!""}"/>
                <#else>
                    ${person?if_exists.lastName!""}
                </#if>
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
                    <#if (method?has_content)>
                        <select name="statusId" class="short">
                            <#assign selectedStatusId = parameters.statusId!party.statusId!"">
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
                <label>${uiLabelMap.ExportStatusCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                     <#assign selectedExportStatus = parameters.exportStatus!IS_DOWNLOADED!"N">
                    <select name="exportStatus" class="short">
                        <option value="N" <#if selectedExportStatus.equals("N") >selected=selected</#if>>${uiLabelMap.DownloadNewInfo}</option>
                        <option value="Y" <#if selectedExportStatus.equals("Y") >selected=selected</#if>>${uiLabelMap.ExportStatusInfo}</option>
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

    <#if (method?has_content) && (method=='add')>
        <input type="hidden" name="method" value="add"/>
    <#elseif (method?has_content) && (method=='edit')>
        <input type="hidden" name="method" value="edit"/>
    </#if>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.HomePhoneCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                    <input type="hidden" name="homePhoneContactMechId" value="${telecomHomeNoContactMechId!}" />
                    <span class="USER_USA USER_CAN">
                        <input type="text" class="phone3" id="phoneHomeArea" name="phoneHomeArea" maxlength="3" value="${parameters.phoneHomeArea!areaCodeHome!""}" />
                        <input type="hidden" id="phoneHomeContact" name="phoneHomeContact" value="${parameters.phoneHomeContact!contactNumberHome!""}"/>
                        <input type="text" class="phone3" id="phoneHomeContact3" name="phoneHomeContact3" value="${parameters.phoneHomeContact3!contactNumber3Home!""}" maxlength="3" />
                        <input type="text" class="phone4" id="phoneHomeContact4" name="phoneHomeContact4" value="${parameters.phoneHomeContact4!contactNumber4Home!""}" maxlength="4" />
                    </span>
                    <span style="display:none" class="USER_OTHER">
                        <input type="text" class="address" id="phoneHomeContactOther" name="phoneHomeContactOther" value="${parameters.phoneHomeContactOther!contactNumberHome!""}" />
                    </span>
                <#else>
                    ${formattedHomePhone!}
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
                    <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="Y" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "Y") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "Y") || (!parameters.requireCode?exists && method?has_content && method == "add"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                    <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="N" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "N") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "N"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
                </div>
            <#else>
                <div class="infoValue checkbox short">
                    <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="Y" disabled="disabled" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "Y") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "Y") || (!parameters.requireCode?exists && method?has_content && method == "add"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                    <input class="checkBoxEntry" type="radio" id="allowSolicitation" name="allowSolicitation" value="N" disabled="disabled" <#if ((parameters.allowSolicitation?exists && parameters.allowSolicitation?string == "N") || (userEmailAllowSolicitation?exists && userEmailAllowSolicitation?string == "N"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
                </div>
            </#if>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.CellPhoneCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                    <input type="hidden" name="mobilePhoneContactMechId" value="${telecomMobileNoContactMechId!}" />
                    <span class="USER_USA USER_CAN">
                        <input type="text" class="phone3" id="phoneMobileArea" name="phoneMobileArea" maxlength="3" value="${parameters.phoneMobileArea!areaCodeMobile!""}" />
                        <input type="hidden" id="phoneMobileContact" name="phoneMobileContact" value="${parameters.phoneMobileContact!contactNumberMobile!""}"/>
                        <input type="text" class="phone3" id="phoneMobileContact3" name="phoneMobileContact3" value="${parameters.phoneMobileContact3!contactNumber3Mobile!""}" maxlength="3"/>
                        <input type="text" class="phone4" id="phoneMobileContact4" name="phoneMobileContact4" value="${parameters.phoneMobileContact4!contactNumber4Mobile!""}" maxlength="4"/>
                    </span>
                    <span style="display:none" class="USER_OTHER">
                        <input type="text" class="address" id="phoneMobileContactOther" name="phoneMobileContactOther" value="${parameters.phoneMobileContactOther!contactNumberMobile!""}" />
                    </span>
                <#else>
                    ${formattedCellPhone!}
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
                <label>${uiLabelMap.WorkPhoneCaption}</label>
            </div>
            <div class="infoValue">
                <#if (method?has_content)>
                    <input type="hidden" name="workPhoneContactMechId" value="${telecomWorkNoContactMechId!}" />
                    <span class="USER_USA USER_CAN">
                        <input type="text" class="phone3" id="phoneWorkArea" name="phoneWorkArea" maxlength="3" value="${parameters.phoneWorkArea!areaCodeWork!""}" />
                        <input type="hidden" id="phoneWorkContact" name="phoneWorkContact" value="${parameters.phoneWorkContact!contactNumberWork!""}"/>
                        <input type="text" class="phone3" id="phoneWorkContact3" name="phoneWorkContact3" value="${parameters.phoneWorkContact3!contactNumber3Work!""}" maxlength="3"/>
                        <input type="text" class="phone4" id="phoneWorkContact4" name="phoneWorkContact4" value="${parameters.phoneWorkContact4!contactNumber4Work!""}" maxlength="4"/>
                        ${uiLabelMap.PhoneExtCaption}
                        <input type="text" class="phoneExt" id="phoneWorkExt" name="phoneWorkExt" value="${parameters.phoneWorkExt!extensionWork!""}" maxlength="10"/>
                    </span>
                    <span style="display:none" class="USER_OTHER">
                        <input type="text" class="address" id="phoneWorkContactOther" name="phoneWorkContactOther" value="${parameters.phoneWorkContactOther!contactNumberWork!""}" />
                        ${uiLabelMap.PhoneExtCaption}
                        <input type="text" class="phoneExt" id="phoneWorkExtOther" name="phoneWorkExtOther" value="${parameters.phoneWorkExtOther!extensionWork!""}" maxlength="10"/>
                    </span>
                <#else>
                    ${formattedWorkPhone!}<#if extensionWork?has_content>&nbsp;x${extensionWork!}</#if>
                </#if>
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
                    <input class="checkBoxEntry" type="radio" id="textPreference" name="textPreference" value="Y" <#if ((parameters.textPreference?exists && parameters.textPreference?string == "Y") || (userTextPreference?exists && userTextPreference?string == "Y") || (!parameters.requireCode?exists && method?has_content && method == "add"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                    <input class="checkBoxEntry" type="radio" id="textPreference" name="textPreference" value="N" <#if ((parameters.textPreference?exists && parameters.textPreference?string == "N") || (userTextPreference?exists && userTextPreference?string == "N"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
                </div>
            <#else>
                <div class="infoValue checkbox short">
                    <input class="checkBoxEntry" type="radio" id="textPreference" name="textPreference" value="Y" disabled="disabled" <#if ((parameters.textPreference?exists && parameters.textPreference?string == "Y") || (userTextPreference?exists && userTextPreference?string == "Y") || (!parameters.requireCode?exists && method?has_content && method == "add"))>checked="checked"</#if>/>${uiLabelMap.YesInfo}
                    <input class="checkBoxEntry" type="radio" id="textPreference" name="textPreference" value="N" disabled="disabled" <#if ((parameters.textPreference?exists && parameters.textPreference?string == "N") || (userTextPreference?exists && userTextPreference?string == "N"))>checked="checked"</#if>/>${uiLabelMap.NoInfo}
                </div>
            </#if>
        </div>
    </div>
<!-- end customerDetailGeneralInfo.ftl -->


