<!-- start customerDetailGeneralInfo.ftl -->
<#if visit?has_content>
	<#assign visitId = visit.visitId! >
	<#assign visitDateTime = visit.fromDate! >
	<#assign visitLoginId = visit.userLoginId! >
	<#assign serverIpAddrress = visit.serverIpAddress! >
	<#assign serverHostName = visit.serverHostName! >
	<#assign initialRequest= visit.initialRequest! >
	<#assign initialReferrer = visit.initialReferrer! >
	<#assign initialUserAgent = visit.initialUserAgent! >
	<#assign clientIpAddrress = visit.clientIpAddress! >
	<#assign clientHostName = visit.clientHostName! >

</#if>
<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.VisitIdCaption}</label>
     </div>
     <div class="infoValue">
       ${visitId!""}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.VisitDateTime}</label>
     </div>
     <div class="infoValue">
       ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(visitDateTime!, preferredDateTimeFormat))!"N/A"}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.VisitLoginId}</label>
     </div>
     <div class="infoValue">
       ${visitLoginId!""}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ServerIpAddrress}</label>
     </div>
     <div class="infoValue">
       ${serverIpAddrress!""}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ServerHostName}</label>
     </div>
     <div class="infoValue">
       ${serverHostName!""}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.InitialRequest}</label>
     </div>
     <div class="infoValue">
       ${initialRequest!""}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.InitialReferrer}</label>
     </div>
     <div class="infoValue">
       ${initialReferrer!""}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.InitialUserAgent}</label>
     </div>
     <div class="infoValue">
       ${initialUserAgent!""}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ClientIpAddrress}</label>
     </div>
     <div class="infoValue">
       ${clientIpAddrress!""}
     </div>
   </div>
</div>

<div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.ClientHostName}</label>
     </div>
     <div class="infoValue">
       ${clientHostName!""}
     </div>
   </div>
</div>


<!-- end customerDetailGeneralInfo.ftl -->


