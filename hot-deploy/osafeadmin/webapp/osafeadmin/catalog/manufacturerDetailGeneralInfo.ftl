<!-- start manufacturerDetailGeneralInfo.ftl -->
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
                    <span class="required">*</span>
                    ${uiLabelMap.ManufacturerNoCaption}
                </label>
            </div>
            <div class="infoValue">
                <#if (method?has_content && method == "add")>
                    <input type="text" name="manufacturerPartyId" id="manufacturerPartyId" maxlength="20" value="${parameters.manufacturerPartyId!""}"/>
                <#else>
                    <input type="hidden" name="manufacturerPartyId" id="manufacturerPartyId" maxlength="20" value="${party?if_exists.partyId!parameters.manufacturerPartyId!""}" />
                    ${party?if_exists.partyId!parameters.manufacturerPartyId!""}
                </#if>
            </div>
        </div>
    </div>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
                    <span class="required">*</span>
                    ${uiLabelMap.NameCaption}
                </label>
            </div>
            <div class="infoValue">
              <input type="text" name="profileName" id="profileName" maxlength="100" value="${parameters.profileName!profileName!""}"/>
            </div>
        </div>
    </div>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
                    <span class="required">*</span>
	                ${uiLabelMap.ShortDescriptionCaption}
                </label>
            </div>
            <div class="infoValue">
              <textarea class="shortArea" id="description" name="description" cols="50" rows="5">${parameters.description!description!""}</textarea>
            </div>
        </div>
    </div>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
	                ${uiLabelMap.LongDescriptionCaption}
                </label>
            </div>
            <div class="infoValue">
              <textarea class="mediumArea" id="longDescription" name="longDescription" cols="50" rows="5">${parameters.longDescription!longDescription!""}</textarea>
            </div>
        </div>
    </div>
<!-- end manufacturerDetailGeneralInfo.ftl -->


