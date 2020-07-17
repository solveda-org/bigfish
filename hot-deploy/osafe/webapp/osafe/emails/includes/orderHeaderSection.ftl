<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#-- NOTE: this template is used for the orderstatus screen in ecommerce AND for order notification emails through the OrderNoticeEmail.ftl file -->
<#-- the "urlPrefix" value will be prepended to URLs by the ofbizUrl transform if/when there is no "request" object in the context -->
<#if baseEcommerceSecureUrl?exists><#assign urlPrefix = baseEcommerceSecureUrl/></#if>
<#if (orderHeader.externalId)?exists && (orderHeader.externalId)?has_content >
  <#assign externalOrder = "(" + orderHeader.externalId + ")"/>
</#if>

<table id="outerEmailLayout" cellpadding="0" cellspacing="0" style="font-family:${emailFontFamily};">
    <tr id="orderHeaderRow">
        <td style="padding-bottom:20px;">
        <table cellpadding="0" cellspacing="0">
            <tr>
                <td id="headerLeft"  style="vertical-align: top;">
                <table cellpadding="0" cellspacing="0">
                    <tr> <!-- row 1 -->
                        <td style="vertical-align: top;">
                            <table cellpadding="0" cellspacing="0"style="width: ${emailTableWidth}; border: 1px solid black; font-family:${emailFontFamily};">
                                <tr>
                                    <td style="padding-left:10px; font-size:${emailTableHeadingFontSize}; background-color: ${emailTableHeadingColor};">${uiLabelMap.OrderInformationHeading}</td>
                                </tr>
                                <#-- placing customer information -->
                                <tr>
                                    <td style="border-top: 1px solid black;">
                                        <table cellpadding="0" cellspacing="0"style="width: 90%; margin: 10px; font-family:${emailFontFamily};">
                                            <#-- order number -->
                                            <#if orderHeader?has_content>
                                            <tr>
                                                <td style="width: 150px; text-align: left; vertical-align: top;">${uiLabelMap.OrderNumberCaption}</td>
                                                <td>${orderHeader.orderId}</td>
                                            </tr>
                                            </#if>
                                            <#-- user information -->
                                            <#if localOrderReadHelper?exists && orderHeader?has_content>
                                              <#assign displayParty = localOrderReadHelper.getPlacingParty()?if_exists/>
                                              <#if displayParty?has_content>
                                                <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                                              </#if>
                                            <tr>
                                                <td>${uiLabelMap.NameCaption}</td>
                                                <td >${(displayPartyNameResult.fullName)?default("[Name Not Found]")}</td>
                                            </tr>
                                            </#if>
                                            <#-- ordered date -->
                                            <#if orderHeader?has_content>
                                            <tr>
                                                <td>${uiLabelMap.OrderDateCaption}</td>
                                                <td>${orderHeader.orderDate?string("yyyy-MM-dd")}</td>
                                            </tr>
                                            </#if>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr> <!-- row 2 -->
                        <td style="vertical-align: top; padding-top: 20px">
                            <#if paymentMethods?has_content || paymentMethodType?has_content || billingAccount?has_content>
                                <#-- credit card info -->
                            <table cellpadding="0" cellspacing="0"style="width: ${emailTableWidth}; border: 1px solid black; font-family:${emailFontFamily};">
                                <tr>
                                    <td style="padding-left:10px; font-size:${emailTableHeadingFontSize}; background-color: ${emailTableHeadingColor};">${uiLabelMap.PaymentInformationHeading}</td>
                                </tr>
                                <tr>
                                    <td style="border-top: 1px solid black;">
                                        <table cellpadding="0" cellspacing="0"style="width: 90%; margin: 10px; font-family:${emailFontFamily};">
                                        <#list paymentMethods as paymentMethod>
                                          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
                                            <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                                            <#assign cardType = creditCard.cardType!"">

                                            <#assign cardNumber = creditCard.cardNumber!"">
                                            <#assign cardNumber = cardNumber?substring(cardNumber?length - 4)>

                                            <#assign expireDate = creditCard.expireDate!"">
                                          </#if>
                                            <#-- credit card info -->
                                            <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId && creditCard?has_content>
                                                <#if outputted?default(false)>
                                                </#if>
                                                <#assign pmBillingAddress = creditCard.getRelatedOneCache("PostalAddress")?if_exists>
                                            <tr>
                                                <td style="width: 150px; text-align: left; vertical-align: top;">${uiLabelMap.CreditCardCaption}</td>
                                                <td>
                                                    ${cardType?if_exists}<br>
                                                    ${cardNumber?if_exists}<br>
                                                    ${expireDate?if_exists}<br>
                                                </td>
                                            </tr>
                                            </#if>

                                            <#assign outputted = true>
                                        </#list>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                            </#if>
                        </td>
                    </tr>

                </table>
                </td>
                <td style="padding-left: 20px;"> &nbsp;<!--  Gutter column --></td>
                <td id="headerRight"  style="vertical-align: top;">
                <table cellpadding="0" cellspacing="0">
                    <tr> <!-- row 1 -->
                        <td style="vertical-align: top;">
                            <#if orderItemShipGroups?has_content>
                            <table cellpadding="0" cellspacing="0"style="width: ${emailTableWidth}; border: 1px solid black; font-family:${emailFontFamily};">
                                <tr>
                                    <td style="padding-left:10px; font-size:${emailTableHeadingFontSize}; background-color: ${emailTableHeadingColor};">${uiLabelMap.ShippingInformationHeading}</td>
                                </tr>
                                <tr>
                                    <td style="border-top: 1px solid black;">
                                        <#-- shipping address -->
                                        <#assign groupIdx = 0>
                                        <#list orderItemShipGroups as shipGroup>
                                          <#if orderHeader?has_content>
                                            <#assign shippingAddress = shipGroup.getRelatedOneCache("PostalAddress")?if_exists>
                                            <#assign groupNumber = shipGroup.shipGroupSeqId?if_exists>
                                          <#else>
                                            <#assign shippingAddress = cart.getShippingAddress(groupIdx)?if_exists>
                                            <#assign groupNumber = groupIdx + 1>
                                          </#if>
                                            <table cellpadding="0" cellspacing="0"style="width: 90%; margin: 10px;font-family:${emailFontFamily};">
                                            <#if shippingAddress?has_content>
                                            <tr>
                                                <td style="width: 150px; text-align: left; vertical-align: top;">${uiLabelMap.DestinationCaption}</td>
                                                <td>
                                                <#if shippingAddress.toName?has_content>${shippingAddress.toName}<br></#if>
                                                ${shippingAddress.address1}<br>
                                                <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br></#if>
                                                <#assign shippingStateGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.stateProvinceGeoId?if_exists}, true))?if_exists />
                                                ${shippingAddress.city}<#if shippingStateGeo?has_content>, ${shippingStateGeo.geoName?if_exists}</#if> ${shippingAddress.postalCode?if_exists}<br>
                                                <#assign shippingCountryGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.countryGeoId?if_exists}, true))?if_exists />
                                                <#if shippingCountryGeo?has_content>${shippingCountryGeo.geoName?if_exists}<br></#if>
                                                </td>
                                            </tr>
                                            </#if>
                                            <tr>
                                                <td  style="padding-top: 10px;">${uiLabelMap.ShippingMethodCaption}</td>
                                                <td  style="padding-top: 10px;">
                                                  <#if orderHeader?has_content>
                                                    <#assign shipmentMethodType = shipGroup.getRelatedOneCache("ShipmentMethodType")?if_exists>
                                                    <#assign carrierPartyId = shipGroup.carrierPartyId?if_exists>
                                                  <#else>
                                                    <#assign shipmentMethodType = cart.getShipmentMethodType(groupIdx)?if_exists>
                                                    <#assign carrierPartyId = cart.getCarrierPartyId(groupIdx)?if_exists>
                                                  </#if>
                                                  <#if carrierPartyId?exists>
                                                     <#assign carrier =  delegator.findByPrimaryKeyCache("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", carrierPartyId))?if_exists />
                                                     <#if carrier?has_content>${carrier.groupName?default(carrier.partyId)}</#if>
                                                  </#if>
                                                  ${(shipmentMethodType.description)?default("N/A")}
                                                </td>
                                            </tr>
                                            <#-- tracking number -->
                                            <#if shipGroup.trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
                                            <tr>
                                                <td>${uiLabelMap.TrackingNumberCaption}</td>
                                                <td>
                                                    <#if shipGroup.trackingNumber?has_content>
                                                      ${shipGroup.trackingNumber}
                                                    </#if>
                                                    <#if orderShipmentInfoSummaryList?has_content>
                                                      <#list orderShipmentInfoSummaryList as orderShipmentInfoSummary>
                                                        <#if (orderShipmentInfoSummaryList?size > 1)>${orderShipmentInfoSummary.shipmentPackageSeqId}: </#if>
                                                        Code: ${orderShipmentInfoSummary.trackingCode?default("[Not Yet Known]")}
                                                        <#if orderShipmentInfoSummary.boxNumber?has_content>${uiLabelMap.OrderBoxNumber}${orderShipmentInfoSummary.boxNumber}</#if>
                                                        <#if orderShipmentInfoSummary.carrierPartyId?has_content>
                                                          <#assign carrier =  delegator.findByPrimaryKeyCache("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", orderShipmentInfoSummary.carrierPartyId))?if_exists />
                                                          <#if carrier?has_content>(${uiLabelMap.ProductCarrier}: ${carrier.groupName?default(carrier.carrierPartyId)})</#if>
                                                        </#if>
                                                      </#list>
                                                    </#if>
                                                </td>
                                            </tr>
                                            </#if>

                                            <#if shipGroup_has_next>
                                            </#if>
                                        </table>
                                        <#assign groupIdx = groupIdx + 1>
                                        </#list><#-- end list of orderItemShipGroups -->
                            </#if>
                                    </td>
                                </tr>
                            </table>

                        </td>
                    </tr>
                    <tr> <!-- row 2 -->
                        <td>
                            <!-- Row 2 right col-->
                        </td>
                    </tr>
                </table>
                </td>
            </tr>
        </table>
        </td>
    </tr>
</table>

