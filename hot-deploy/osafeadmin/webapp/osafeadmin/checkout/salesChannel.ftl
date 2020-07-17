    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption"><label>${uiLabelMap.SalesChannelCaption}</label></div>
            <div class="infoValue">
	           <select name="salesChannel" id="salesChannel" class="small">
                    <option value="">${uiLabelMap.CommonSelectOne}</option>
    	            <#assign selectedOption = parameters.salesChannel!""/>
	                <#if salesChannelList?has_content>
	                    <#list salesChannelList as enumeration>
	                        <option value="${enumeration.enumId}" <#if selectedOption == enumeration.enumId> selected</#if>>${enumeration.description!enumeration.enumCode!enumeration.enumId}</option>
	                    </#list>
	                </#if>
	            </select>
            </div>
        </div>
    </div>
