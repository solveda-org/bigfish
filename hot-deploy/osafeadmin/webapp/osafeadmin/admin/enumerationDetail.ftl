<#if mode?has_content>
    <#if enum?has_content>
        <#assign enumId = enum.enumId?if_exists />
        <#assign description = enum.description!"" />
        <#assign enumCode = enum.enumCode?if_exists />
        <#assign sequenceId = enum.sequenceId!"" />
        <#assign createdDate = enum.createdStamp!"" />
        <#assign lastUpdatedDate = enum.lastUpdatedStamp!"" />
     <#else>
        <#assign createdDate = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
        <#assign lastUpdatedDate = "" />
    </#if> 
      <input type="hidden" name="enumTypeId" value="${enumTypeId!""}" />
      <input type="hidden" name="createdDate" value="${createdDate!""}" />

       <div class="infoRow">
         <div class="infoEntry">
             <div class="infoCaption"><label>${enumIdCaption!""}</label></div>
               <div class="infoValue">
                   <#if mode?has_content && mode == "add">
                       <input name="enumId" type="text" id="enumId" value="${parameters.enumId?default("")}" class="medium"/>
                   <#elseif mode?has_content && mode == "edit">
                       <input type="hidden" name="enumId" value="${parameters.enumId!enumId!""}"/>${enumId!""}
                   </#if>
               </div>
         </div>
       </div>
       
       <#if enumCodeCaption?exists && enumCodeCaption?has_content && enumTypeId == "RECURRENCE_FREQUENCY">
            <div class="infoRow">
                <div class="infoEntry">
                    <div class="infoCaption"><label>${enumCodeCaption!}</label></div>
                    <div class="infoValue">
                      <input name="enumCode" type="text" id="enumCode" value="${parameters.enumCode!enumCode!""}" class="medium"/>
                    </div>
                </div>
            </div>
        </#if>

        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${enumDescCaption!""}</label></div>
                <div class="infoValue">
                 <input type="text" name="description" value="${parameters.description!description!""}" class="medium"/>
                </div>
            </div>
        </div>

        <#if enumCodeCaption?exists && enumCodeCaption?has_content && enumTypeId == "CREDIT_CARD_TYPE">
            <div class="infoRow">
                <div class="infoEntry">
                    <div class="infoCaption"><label>${enumCodeCaption!}</label></div>
                    <div class="infoValue">
                     <select name="enumCode" class="small">
                         <#assign cardType = parameters.enumCode!enumCode!"">
                         <#if cardType?has_content>
                           <option value="${cardType?if_exists}">${cardType?if_exists}</option>
                         </#if>
                         <option value="">${uiLabelMap.CommonSelectOne}</option>
                         ${screens.render("component://osafeadmin/widget/CommonScreens.xml#creditCardTypes")}
                      </select>
                    </div>
                    <div class="infoIcon">
                      <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.CreditCardTypeHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                    </div>
                </div>
            </div>
        </#if>

        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.SeqNumberCaption}</label></div>
                <div class="infoValue">
                 <input type="text" name="sequenceId" value="${parameters.sequenceId!sequenceId!""}" class="small"/>
                </div>
               <div class="infoIcon">
                 <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SeqHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
               </div>
            </div>
        </div>

        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.CreatedDateCaption}</label></div>
                <div class="infoValue">
                    <#if createdDate?has_content>
                      ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(createdDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
                    </#if>
                </div>
            </div>
        </div>
 
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.UpdatedDateCaption}</label></div>
                <div class="infoValue">
                    <#if lastUpdatedDate?has_content>
                        ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(lastUpdatedDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
                    </#if>
                </div>
            </div>
        </div>
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
