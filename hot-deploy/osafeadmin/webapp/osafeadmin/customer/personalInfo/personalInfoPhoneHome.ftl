<#if partyId?exists && partyId?has_content>
    <#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate")/>
    <#assign fieldsMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_HOME")/>
    <#assign homePhonePartyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", fieldsMap, orderByList)?if_exists/>
    <#if homePhonePartyContactDetails?has_content>
         <#assign homePhonePartyContactDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(homePhonePartyContactDetails?if_exists)?if_exists/>
        <#assign homePhonePartyContactDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(homePhonePartyContactDetails?if_exists)?if_exists/>
    </#if>
    <#-- Splits the contactNumber -->
    <#if homePhonePartyContactDetail?exists && homePhonePartyContactDetail?has_content>
        <#assign telecomHomeNoContactMechId = homePhonePartyContactDetail.contactMechId!"" />
        <#assign areaCodeHome = homePhonePartyContactDetail.areaCode?if_exists />
        <#assign contactNumberHome = homePhonePartyContactDetail.contactNumber?if_exists />
        <#if (contactNumberHome?has_content) && (contactNumberHome?length gt 6)>
            <#assign contactNumber3Home = contactNumberHome.substring(0, 3) />
            <#assign contactNumber4Home = contactNumberHome.substring(3, 7) />
        </#if>
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<#-- Displays the home phone entry -->
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.HomePhoneCaption}</label>
            </div>
            <div class="infoValue">
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
                <input type="hidden" id="phoneHome_mandatory" name="phoneHome_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>