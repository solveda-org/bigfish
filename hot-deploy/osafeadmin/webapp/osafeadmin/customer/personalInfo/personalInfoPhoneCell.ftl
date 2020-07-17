<#if partyId?exists && partyId?has_content>
    <#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate")/>
    <#assign fieldsMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_MOBILE")/>
    <#assign mobilePhonePartyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", fieldsMap, orderByList)?if_exists/>
    <#if mobilePhonePartyContactDetails?has_content>
        <#assign mobilePhonePartyContactDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(mobilePhonePartyContactDetails?if_exists)?if_exists/>
        <#assign mobilePhonePartyContactDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(mobilePhonePartyContactDetails?if_exists)?if_exists/>
    </#if>
    <#-- Splits the contactNumber -->
    <#if mobilePhonePartyContactDetail?exists && mobilePhonePartyContactDetail?has_content>
        <#assign telecomMobileNoContactMechId = mobilePhonePartyContactDetail.contactMechId!"" />
        <#assign areaCodeMobile = mobilePhonePartyContactDetail.areaCode?if_exists />
        <#assign contactNumberMobile = mobilePhonePartyContactDetail.contactNumber?if_exists />
        <#if (contactNumberMobile?has_content) && (contactNumberMobile?length gt 6)>
            <#assign contactNumber3Mobile = contactNumberMobile.substring(0, 3) />
            <#assign contactNumber4Mobile = contactNumberMobile.substring(3, 7) />
        </#if>
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.CellPhoneCaption}</label>
            </div>
            <div class="infoValue">
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
                <input type="hidden" id="phoneMobile_mandatory" name="phoneMobile_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>