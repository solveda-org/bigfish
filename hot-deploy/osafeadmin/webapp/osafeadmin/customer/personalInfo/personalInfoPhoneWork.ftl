<#if partyId?exists && partyId?has_content>
    <#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate")/>
    <#assign fieldsMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_WORK")/>
    <#assign workPhonePartyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", fieldsMap, orderByList)?if_exists/>
    <#if workPhonePartyContactDetails?has_content>
        <#assign workPhonePartyContactDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(workPhonePartyContactDetails?if_exists)?if_exists/>
        <#assign workPhonePartyContactDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(workPhonePartyContactDetails?if_exists)?if_exists/>
    </#if>
    <#-- Splits the contactNumber -->
    <#if workPhonePartyContactDetail?exists && workPhonePartyContactDetail?has_content>
        <#assign extensionWork = workPhonePartyContactDetail.extension?if_exists />
        <#assign telecomWorkNoContactMechId = workPhonePartyContactDetail.contactMechId!"" />
        <#assign areaCodeWork = workPhonePartyContactDetail.areaCode?if_exists />
        <#assign contactNumberWork = workPhonePartyContactDetail.contactNumber?if_exists />
        <#if (contactNumberWork?has_content) && (contactNumberWork?length gt 6)>
            <#assign contactNumber3Work = contactNumberWork.substring(0, 3) />
            <#assign contactNumber4Work = contactNumberWork.substring(3, 7) />
        </#if>
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<#-- Displays the work phone entry -->
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.WorkPhoneCaption}</label>
            </div>
            <div class="infoValue">
                <input type="hidden" name="workPhoneContactMechId" value="${telecomWorkNoContactMechId!}" />
                <span class="USER_USA USER_CAN">
                    <input type="text" class="phone3" id="phoneWorkArea" name="phoneWorkArea" maxlength="3" value="${parameters.phoneWorkArea!areaCodeWork!""}" />
                    <input type="hidden" id="phoneWorkContact" name="phoneWorkContact" value="${parameters.phoneWorkContact!contactNumberWork!""}"/>
                    <input type="text" class="phone3" id="phoneWorkContact3" name="phoneWorkContact3" value="${parameters.phoneWorkContact3!contactNumber3Work!""}" maxlength="3"/>
                    <input type="text" class="phone4" id="phoneWorkContact4" name="phoneWorkContact4" value="${parameters.phoneWorkContact4!contactNumber4Work!""}" maxlength="4"/>
                    ${uiLabelMap.PhoneExtCaption}
                    <input type="text" class="phoneExt" id="phoneWorkExt" name="phoneWorkExt" value="${parameters.phoneWorkExt!extensionWork!""}" maxlength="4"/>
                </span>
                <span style="display:none" class="USER_OTHER">
                    <input type="text" class="address" id="phoneWorkContactOther" name="phoneWorkContactOther" value="${parameters.phoneWorkContactOther!contactNumberWork!""}" />
                    ${uiLabelMap.PhoneExtCaption}
                    <input type="text" class="phoneExt" id="phoneWorkExtOther" name="phoneWorkExtOther" value="${parameters.phoneWorkExtOther!extensionWork!""}" maxlength="4"/>
                </span>
                <input type="hidden" id="phoneWork_mandatory" name="phoneWork_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>