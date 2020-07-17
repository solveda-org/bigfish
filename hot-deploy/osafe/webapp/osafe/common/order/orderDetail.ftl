<#if orderHeader?has_content>
 <div class="${request.getAttribute("attributeClass")!}">
  <div class="displayBox">
	<ul class="displayDetail order">
	    <li>
	    	<div>
	     		<label>${uiLabelMap.OrderNumberCaption}</label>
	     		<span>${orderHeader.orderId}</span>
	    	</div>
	    	<div>
	     		<label>${uiLabelMap.CustomerCaption}</label>
	     		<span>
	     		<#if localOrderReadHelper?exists && orderHeader?has_content>
                  <#assign displayParty = localOrderReadHelper.getPlacingParty()?if_exists/>
                  <#if displayParty?has_content>
                    <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                   </#if>
                   ${(displayPartyNameResult.fullName)?default("[Name Not Found]")}
                </#if>
	     		</span>
	    	</div>
	    </li>
	    <li>
	    	<div>
	     		<label>${uiLabelMap.StatusCaption}</label>
	     		<span>
	                <#if orderHeader?has_content>
	                   <span>${localOrderReadHelper.getCurrentStatusString()}</span>
	                <#else>
	                    <span>${uiLabelMap.OrderNotYetOrdered}</span>
	                </#if>
	     		</span>
	    	</div>
	    	<div>
	     		<label>${uiLabelMap.OrderDateCaption}</label>
	     		<span>
                    <#if orderHeader?has_content>
                        <#assign FORMAT_DATE = Static["com.osafe.util.Util"].getProductStoreParm(request,"FORMAT_DATE")!""/>
                        ${(Static["com.osafe.util.Util"].convertDateTimeFormat(orderHeader.orderDate, FORMAT_DATE))!"N/A"}
                    </#if>
	     		</span>
	    	</div>
	    </li>
	 </ul>
  </div>
 </div>
<#else>
 <div class="${request.getAttribute("attributeClass")!}">
  <h3>${uiLabelMap.OrderSpecifiedNotFound}.</h3>
 </div>
</#if>
