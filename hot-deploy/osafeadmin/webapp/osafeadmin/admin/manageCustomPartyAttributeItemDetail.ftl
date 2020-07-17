<#if mode?has_content>
  <#if customPartyAttribute?has_content>
    <#assign attrName = parameters.attrName!customPartyAttribute.AttrName!""/>
    <#assign sequenceNum = parameters.sequenceNum!customPartyAttribute.SequenceNum!"" />
    <#assign caption = parameters.caption!customPartyAttribute.Caption!"" />
    <#assign type = parameters.type!customPartyAttribute.Type!"" />
    <#assign entryFormat = parameters.entryFormat!customPartyAttribute.EntryFormat!"" />
    <#assign maxLength = parameters.maxLength!customPartyAttribute.MaxLength!"" />
    <#assign valueList = parameters.valueList!customPartyAttribute.ValueList!"" />
    <#assign mandatory = parameters.mandatory!customPartyAttribute.Mandatory!"" />
    <#assign reqMessage = parameters.reqMessage!customPartyAttribute.ReqMessage!"" />
  </#if>
  
    
     <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.AttrNameCaption}</label></div>
                <div class="infoValue">
                 <#if mode?has_content && mode == "add">
                    <input type="text" class="largeTextEntry" name="attrName" value="${parameters.attrName!attrName!""}" />
                  <#elseif mode?has_content && mode == "edit">
                    <input type="hidden" name="attrName" value="${attrName!""}" />${attrName!""}
                  </#if>
               </div>
            </div>
        </div>
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.SeqNoCaption}</label></div>
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <input type="text" class="small" name="sequenceNum" value="${parameters.sequenceNum!sequenceNum!""}" maxlength="9"/>
                  <#elseif mode?has_content && mode == "edit">
                    <input type="text" class="small" name="sequenceNum" value="${sequenceNum!""}" maxlength="9"/>
                  </#if>
               </div>
               <div class="infoIcon">
                   <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SeqIdHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
               </div>
            </div>
        </div>
        
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.CaptionCaption}</label></div>
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <input type="text" class="largeTextEntry" name="caption" value="${parameters.caption!caption!""}" />
                  <#elseif mode?has_content && mode == "edit">
                    <input type="text" class="largeTextEntry" name="caption" value="${caption!""}" />
                  </#if>
               </div>
            </div>
        </div>
        
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.TypeCaption}</label></div>
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <select name="type" id="type" class="small">
                        <option value="ENTRY" <#if parameters.type?has_content && parameters.type == "ENTRY" >selected=selected</#if>>${uiLabelMap.TypeEntrySingleLineEditFieldLabel}</option>
                        <option value="ENTRY_BOX" <#if parameters.type?has_content && parameters.type == "ENTRY_BOX" >selected=selected</#if>>${uiLabelMap.TypeEntryMultiLineEditFieldLabel}</option>
                        <option value="RADIO_BUTTON" <#if parameters.type?has_content && parameters.type == "RADIO_BUTTON" >selected=selected</#if>>${uiLabelMap.TypeRadioButtonLabel}</option>
                        <option value="CHECKBOX" <#if parameters.type?has_content && parameters.type == "CHECKBOX" >selected=selected</#if>>${uiLabelMap.TypeCheckBoxLabel}</option>
                        <option value="DROP_DOWN" <#if parameters.type?has_content && parameters.type == "DROP_DOWN" >selected=selected</#if>>${uiLabelMap.TypeDropdownSingleSelectLabel}</option>
                        <option value="DROP_DOWN_MULTI" <#if parameters.type?has_content && parameters.type == "DROP_DOWN_MULTI" >selected=selected</#if>>${uiLabelMap.TypeDropdownMultiLabel}</option>
                        <option value="DATE_MMDD" <#if parameters.type?has_content && parameters.type == "DATE_MMDD" >selected=selected</#if>>${uiLabelMap.TypeDateMMDDLabel}</option>
                        <option value="DATE_MMDDYYYY" <#if parameters.type?has_content && parameters.type == "DATE_MMDDYYYY" >selected=selected</#if>>${uiLabelMap.TypeDateMMDDYYYYLabel}</option>
                        <option value="DATE_DDMM" <#if parameters.type?has_content && parameters.type == "DATE_DDMM" >selected=selected</#if>>${uiLabelMap.TypeDateDDMMLabel}</option>
                        <option value="DATE_DDMMYYYY" <#if parameters.type?has_content && parameters.type == "DATE_DDMMYYYY" >selected=selected</#if>>${uiLabelMap.TypeDateDDMMYYYYLabel}</option>
                    </select>
                  <#elseif mode?has_content && mode == "edit">
                     <select name="type" id="type" class="small">
                        <option value="ENTRY" <#if type?has_content && type == "ENTRY" >selected=selected</#if>>${uiLabelMap.TypeEntrySingleLineEditFieldLabel}</option>
                        <option value="ENTRY_BOX" <#if type?has_content && type == "ENTRY_BOX" >selected=selected</#if>>${uiLabelMap.TypeEntryMultiLineEditFieldLabel}</option>
                        <option value="RADIO_BUTTON" <#if type?has_content && type == "RADIO_BUTTON" >selected=selected</#if>>${uiLabelMap.TypeRadioButtonLabel}</option>
                        <option value="CHECKBOX" <#if type?has_content && type == "CHECKBOX" >selected=selected</#if>>${uiLabelMap.TypeCheckBoxLabel}</option>
                        <option value="DROP_DOWN" <#if type?has_content && type == "DROP_DOWN" >selected=selected</#if>>${uiLabelMap.TypeDropdownSingleSelectLabel}</option>
                        <option value="DROP_DOWN_MULTI" <#if type?has_content && type == "DROP_DOWN_MULTI" >selected=selected</#if>>${uiLabelMap.TypeDropdownMultiLabel}</option>
                        <option value="DATE_MMDD" <#if type?has_content && type == "DATE_MMDD" >selected=selected</#if>>${uiLabelMap.TypeDateMMDDLabel}</option>
                        <option value="DATE_MMDDYYYY" <#if type?has_content && type == "DATE_MMDDYYYY" >selected=selected</#if>>${uiLabelMap.TypeDateMMDDYYYYLabel}</option>
                        <option value="DATE_DDMM" <#if type?has_content && type == "DATE_DDMM" >selected=selected</#if>>${uiLabelMap.TypeDateDDMMLabel}</option>
                        <option value="DATE_DDMMYYYY" <#if type?has_content && type == "DATE_DDMMYYYY" >selected=selected</#if>>${uiLabelMap.TypeDateDDMMYYYYLabel}</option>
                     </select>
                  </#if>
               </div>
            </div>
        </div>
       
       <div class="infoRow ENTRY_FORMAT">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.EntryFormatCaption}</label></div>
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <select name="entryFormat" id="entryFormat" class="short">
                        <option value="ANY" <#if parameters.entryFormat?has_content && parameters.entryFormat == "ANY" >selected=selected</#if>>${uiLabelMap.EntryFormatAnyLabel}</option>
                        <option value="NUMERIC" <#if parameters.entryFormat?has_content && parameters.entryFormat == "NUMERIC" >selected=selected</#if>>${uiLabelMap.EntryFormatNumericLabel}</option>
                        <option value="ALPHA_NUMERIC" <#if parameters.entryFormat?has_content && parameters.entryFormat == "ALPHA_NUMERIC" >selected=selected</#if>>${uiLabelMap.EntryFormatAlphaNumericLabel}</option>
                        <option value="MONEY" <#if parameters.entryFormat?has_content && parameters.entryFormat == "MONEY" >selected=selected</#if>>${uiLabelMap.EntryFormatMoneyLabel}</option>
                    </select>
                  <#elseif mode?has_content && mode == "edit">
                    <select name="entryFormat" id="entryFormat" class="short">
                        <option value="ANY" <#if entryFormat?has_content && entryFormat == "ANY" >selected=selected</#if>>${uiLabelMap.EntryFormatAnyLabel}</option>
                        <option value="NUMERIC" <#if entryFormat?has_content && entryFormat == "NUMERIC" >selected=selected</#if>>${uiLabelMap.EntryFormatNumericLabel}</option>
                        <option value="ALPHA_NUMERIC" <#if entryFormat?has_content && entryFormat == "ALPHA_NUMERIC" >selected=selected</#if>>${uiLabelMap.EntryFormatAlphaNumericLabel}</option>
                        <option value="MONEY" <#if entryFormat?has_content && entryFormat == "MONEY" >selected=selected</#if>>${uiLabelMap.EntryFormatMoneyLabel}</option>
                    </select>
                  </#if>
               </div>
            </div>
        </div>
        
        <div class="infoRow MAX_LENGTH">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.MaxLengthCaption}</label></div>
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <input type="text" class="small" name="maxLength" value="${parameters.maxLength!maxLength!""}" />
                  <#elseif mode?has_content && mode == "edit">
                    <input type="text" class="small" name="maxLength" value="${maxLength!""}" />
                  </#if>
               </div>
            </div>
        </div>
        
        <div class="infoRow VALUE_LIST">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.ValueListCaption}</label></div>
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <textarea id="valueList" class="smallArea" rows="1" cols="50" name="valueList">${parameters.valueList!valueList!""}</textarea>
                  <#elseif mode?has_content && mode == "edit">
                    <textarea id="valueList" class="smallArea" rows="1" cols="50" name="valueList">${valueList!""}</textarea>
                  </#if>
               </div>
               <div class="infoIcon">
                   <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.ValueListHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
               </div>
            </div>
        </div>
        
        
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.IsRequiredCaption}</label></div>
                <div class="infoValue">
                  <div class="entry checkbox medium">
                  <#if mode?has_content && mode == "add">
                    <input class="checkBoxEntry" type="radio" name="mandatory"  value="Y" <#if parameters.mandatory?exists && parameters.mandatory?string == "Y">checked="checked"</#if> onchange="javascript:setCustomAttributeRequiredMessageField(this)"/>${uiLabelMap.YesLabel}
                    <input class="checkBoxEntry" type="radio" name="mandatory" value="N" <#if  parameters.mandatory?exists && parameters.mandatory?string == "N">checked="checked" <#elseif !parameters.mandatory?exists>checked="checked"</#if> onchange="javascript:setCustomAttributeRequiredMessageField(this)"/> ${uiLabelMap.NoLabel}
                  <#elseif mode?has_content && mode == "edit">
                     <input class="checkBoxEntry" type="radio" name="mandatory"  value="Y" <#if mandatory?exists && mandatory?string == "Y">checked="checked"</#if> onchange="javascript:setCustomAttributeRequiredMessageField(this)"/>${uiLabelMap.YesLabel}
                     <input class="checkBoxEntry" type="radio" name="mandatory" value="N" <#if  mandatory?exists && mandatory?string == "N">checked="checked" <#elseif !mandatory?exists>checked="checked"</#if> onchange="javascript:setCustomAttributeRequiredMessageField(this)"/> ${uiLabelMap.NoLabel}
                  </#if>
                  </div>
               </div>
            </div>
        </div>
        
        <div class="infoRow REQ_MESSAGE">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.ReqMessageCaption}</label></div>
                <div class="infoValue">
                  <#if mode?has_content && mode == "add">
                    <textarea id="reqMessage" class="smallArea" rows="1" cols="50" name="reqMessage">${parameters.reqMessage!reqMessage!""}</textarea>
                  <#elseif mode?has_content && mode == "edit">
                    <textarea id="reqMessage" class="smallArea" rows="1" cols="50" name="reqMessage">${reqMessage!""}</textarea>
                  </#if>
               </div>
            </div>
        </div>
        
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
