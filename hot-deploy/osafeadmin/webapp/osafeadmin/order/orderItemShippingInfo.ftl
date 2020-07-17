<!-- start orderItemShippingInfo.ftl -->
<#if orderItemShipGroupAssocs?has_content>
 <#list orderItemShipGroupAssocs as shipGroupAssoc>
 	<#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup")/>
	<#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress")!""/>
	<#if shipGroup.carrierPartyId?has_content && shipGroup.shipmentMethodTypeId?has_content>
        <#assign orderItemCarrier = shipGroup.carrierPartyId + " " + shipGroup.shipmentMethodTypeId/>
    </#if>
    <#assign orderItemShipDate = shipGroup.estimatedShipDate!""/>
    <#assign orderItemTrackingNo = shipGroup.trackingNumber!""/>
	<div class="infoRow firstRow">
	   <div class="infoEntry">
	     <div class="infoCaption">
	      <label>${uiLabelMap.ShipToCaption}</label>
	     </div>
	     <div class="infoValue medium">
          ${setRequestAttribute("PostalAddress",shipGroupAddress)}
          ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
	     </div>
	   </div>
	</div>
	<div class="infoRow firstRow">
	   <div class="infoEntry">
	     <div class="infoCaption">
	      <label>${uiLabelMap.ShipDateCaption}</label>
	     </div>
	     <#if orderItemShipDate?has_content>
	          <#assign orderItemShipDate = orderItemShipDate?string(preferredDateFormat)!""/>
	      </#if>
	     <div class="infoValue medium">
	       <#if orderItemShipDate?has_content>${orderItemShipDate!""}</#if>
	     </div>
	     <div class="infoCaption">
	      <label>${uiLabelMap.CarrierCaption}</label>
	     </div>
	     <div class="infoValue">
	     	<span><#if orderItemCarrier?has_content>${orderItemCarrier!}</#if></span>
	     </div>
	   </div>
	</div>
	
	<div class="infoRow firstRow">
	   <div class="infoEntry">
	     <div class="infoCaption">
	      <label>${uiLabelMap.TrackingNoCaption}</label>
	     </div>
	     <div class="infoValue medium">
	       <#if orderItemTrackingNo?has_content><p>${orderItemTrackingNo!""}</p>
  	          <#if trackingURL?has_content>
  	            <a href="JavaScript:newPopupWindow('${trackingURL!""}');" ><span class="shipmentDetailIcon"></span></a>
  	          </#if>
  	       </#if>
	     </div>
	     <div class="infoCaption">
	      <label>${uiLabelMap.ShipQuantityCaption}</label>
	     </div>
	     <div class="infoValue">
	     	<span>${shipGroupAssoc.quantity!}</span>
	     </div>
	     
	   </div>
	</div>
</#list>
</#if>

<!-- end orderItemShippingInfo.ftl -->


