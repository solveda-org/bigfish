/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashMap;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;


/* puts the following in the context: "contactMech", "contactMechId",
        "partyContactMech", "partyContactMechPurposes", "contactMechTypeId",
        "contactMechType", "purposeTypes", "postalAddress", "telecomNumber",
        "requestName", "donePage", "tryEntity", "contactMechTypes"
 */
target = [:];

userLogin = session.getAttribute("userLogin");
partyId="";
if (UtilValidate.isEmpty(userLogin)) 
{
    partyId = cart.getPartyId();
}
else
{
	partyId=userLogin.partyId;
}

ContactMechWorker.getContactMechAndRelated(request, partyId, target);
context.putAll(target);


if (!security.hasEntityPermission("PARTYMGR", "_VIEW", session) && !context.partyContactMech && context.contactMech) 
{
    context.canNotView = true;
} 
else 
{
    context.canNotView = false;
}

preContactMechTypeId = parameters.preContactMechTypeId;
if (UtilValidate.isNotEmpty(preContactMechTypeId)) 
 {
	context.preContactMechTypeId = preContactMechTypeId;
 }

paymentMethodId = parameters.paymentMethodId;
if (UtilValidate.isNotEmpty(paymentMethodId))
 {
	context.paymentMethodId = paymentMethodId;
 }

cmNewPurposeTypeId = parameters.contactMechPurposeTypeId;
if (UtilValidate.isNotEmpty(cmNewPurposeTypeId))
{
    contactMechPurposeType = delegator.findByPrimaryKey("ContactMechPurposeType", [contactMechPurposeTypeId : cmNewPurposeTypeId]);
    if (UtilValidate.isNotEmpty(contactMechPurposeType))
    {
        context.contactMechPurposeType = contactMechPurposeType;
    } 
    else 
    {
        cmNewPurposeTypeId = null;
    }
    context.cmNewPurposeTypeId = cmNewPurposeTypeId;
}

tryEntity = context.tryEntity;
contactMechData = context.contactMech;

if (UtilValidate.isEmpty(tryEntity))
 {
	contactMechData = parameters;
 }
if (UtilValidate.isEmpty(contactMechData))
 {
	contactMechData = [:];
 }
if (UtilValidate.isNotEmpty(contactMechData))
 {
	context.contactMechData = contactMechData;
 }

partyContactMechData = context.partyContactMech;
if (UtilValidate.isEmpty(tryEntity))
 {
	partyContactMechData = parameters;
 }
if (UtilValidate.isEmpty(partyContactMechData))
 {
	partyContactMechData = [:];
 }
if (UtilValidate.isNotEmpty(partyContactMechData))
 {
	context.partyContactMechData = partyContactMechData;
 }

postalAddressData = context.postalAddress;
if (UtilValidate.isEmpty(tryEntity))
 {
	postalAddressData = parameters;
 }
if (UtilValidate.isEmpty(postalAddressData))
 {
	postalAddressData = [:];
 }
if (UtilValidate.isNotEmpty(postalAddressData))
 {
	context.postalAddressData = postalAddressData;
 }

telecomNumberData = context.telecomNumber;
if (UtilValidate.isEmpty(tryEntity))
 {
	telecomNumberData = parameters;
 }
if (UtilValidate.isEmpty(telecomNumberData))
 {
	telecomNumberData = [:];
 }
if (UtilValidate.isNotEmpty(telecomNumberData))
 {
	context.telecomNumberData = telecomNumberData;
 }

// load the geo names for selected countries and states/regions
if (parameters.countryGeoId) 
{
    geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.countryGeoId]);
    if (UtilValidate.isNotEmpty(geoValue)) 
    {
        context.selectedCountryName = geoValue.geoName;
    }
} 
else if (postalAddressData?.countryGeoId) 
{
    geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : postalAddressData.countryGeoId]);
    if (UtilValidate.isNotEmpty(geoValue))
    {
        context.selectedCountryName = geoValue.geoName;
    }
}

if (parameters.stateProvinceGeoId) 
{
    geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : parameters.stateProvinceGeoId]);
    if (UtilValidate.isNotEmpty(geoValue))
    {
        context.selectedStateName = geoValue.geoName;
    }
} 
else if (postalAddressData?.stateProvinceGeoId) 
{
    geoValue = delegator.findByPrimaryKeyCache("Geo", [geoId : postalAddressData.stateProvinceGeoId]);
    if (UtilValidate.isNotEmpty(geoValue))
    {
        context.selectedStateName = geoValue.geoName;
    }
}
