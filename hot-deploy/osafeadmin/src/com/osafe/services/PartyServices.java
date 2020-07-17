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

package com.osafe.services;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Services for Party/Person/Group maintenance
 */
public class PartyServices {

    public static final String module = PartyServices.class.getName();
    public static final String resource = "PartyErrorUiLabels";

    public static Map<String, Object> findParty(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String extInfo = (String) context.get("extInfo");

        // get the role types
        try {
            List<GenericValue> roleTypes = delegator.findList("RoleType", null, null, UtilMisc.toList("description"), null, false);
            result.put("roleTypes", roleTypes);
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up RoleTypes: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // current role type
        String roleTypeId;
        try {
            roleTypeId = (String) context.get("roleTypeId");
            if (UtilValidate.isNotEmpty(roleTypeId)) {
                GenericValue currentRole = delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId));
                result.put("currentRole", currentRole);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current RoleType: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        //get party types
        try {
            List<GenericValue> partyTypes = delegator.findList("PartyType", null, null, UtilMisc.toList("description"), null, false);
            result.put("partyTypes", partyTypes);
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up PartyTypes: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // current party type
        String partyTypeId;
        try {
            partyTypeId = (String) context.get("partyTypeId");
            if (UtilValidate.isNotEmpty(partyTypeId)) {
                GenericValue currentPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", partyTypeId));
                result.put("currentPartyType", currentPartyType);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current RoleType: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // current state
        String stateProvinceGeoId;
        try {
            stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
            if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
                GenericValue currentStateGeo = delegator.findByPrimaryKeyCache("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId));
                result.put("currentStateGeo", currentStateGeo);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current stateProvinceGeo: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // set the page parameters
        int viewIndex = 1;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 1;
        }
        result.put("viewIndex", Integer.valueOf(viewIndex));

        int viewSize = 20;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 20;
        }
        result.put("viewSize", Integer.valueOf(viewSize));

        // get the lookup flag
        String lookupFlag = (String) context.get("lookupFlag");

        // blank param list
        String paramList = "";

        List<GenericValue> partyList = null;
        List completePartyList = FastList.newInstance();
        
        int partyListSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        if ("Y".equals(lookupFlag)) {
            String showAll = (context.get("showAll") != null ? (String) context.get("showAll") : "N");
            paramList = paramList + "&lookupFlag=" + lookupFlag + "&showAll=" + showAll + "&extInfo=" + extInfo;

            // create the dynamic view entity
            DynamicViewEntity dynamicView = new DynamicViewEntity();

            // default view settings
            dynamicView.addMemberEntity("PT", "Party");
            dynamicView.addAlias("PT", "partyId");
            dynamicView.addAlias("PT", "statusId");
            dynamicView.addAlias("PT", "partyTypeId");
            //dynamicView.addAlias("PT", "isDownloaded");
            dynamicView.addRelation("one-nofk", "", "PartyType", ModelKeyMap.makeKeyMapList("partyTypeId"));
            dynamicView.addRelation("many", "", "UserLogin", ModelKeyMap.makeKeyMapList("partyId"));

            // define the main condition & expression list
            List<EntityCondition> andExprs = FastList.newInstance();
            EntityCondition mainCond = null;

            List<String> orderBy = FastList.newInstance();
            List<String> fieldsToSelect = FastList.newInstance();
            // fields we need to select; will be used to set distinct
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("statusId");
            fieldsToSelect.add("partyTypeId");
            //fieldsToSelect.add("isDownloaded");

            // filter on parties that have relationship with logged in user
            String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");
            if (UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
                // add relation to view
                dynamicView.addMemberEntity("PRSHP", "PartyRelationship");
                dynamicView.addAlias("PRSHP", "partyIdTo");
                dynamicView.addAlias("PRSHP", "partyRelationshipTypeId");
                dynamicView.addViewLink("PT", "PRSHP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId", "partyIdTo"));
                List<String> ownerPartyIds = UtilGenerics.cast(context.get("ownerPartyIds"));
                EntityCondition relationshipCond = null;
                if (UtilValidate.isEmpty(ownerPartyIds)) {
                    String partyIdFrom = userLogin.getString("partyId");
                    paramList = paramList + "&partyIdFrom=" + partyIdFrom;
                    relationshipCond = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyIdFrom"), EntityOperator.EQUALS, EntityFunction.UPPER(partyIdFrom));
                } else {
                    relationshipCond = EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, ownerPartyIds);
                }
                dynamicView.addAlias("PRSHP", "partyIdFrom");
                // add the expr
                andExprs.add(EntityCondition.makeCondition(
                        relationshipCond, EntityOperator.AND,
                        EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyRelationshipTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(partyRelationshipTypeId))));
                fieldsToSelect.add("partyIdTo");
            }

            // get the params
            String partyId = (String) context.get("partyId");
            String statusId = (String) context.get("statusId");
            String userLoginId = (String) context.get("userLoginId");
            String firstName = (String) context.get("firstName");
            String lastName = (String) context.get("lastName");
            String groupName = (String) context.get("groupName");
            String groupNameLocal = (String) context.get("groupNameLocal");
            String groupNameStartWithFlag = (String) context.get("groupNameStartWithFlag");
            String productStoreId = (String) context.get("productStoreId");
            String personName = (String) context.get("personName");
            String attrName = (String) context.get("attrName");
            String attrValue = (String) context.get("attrValue");
            String isDownloaded = (String) context.get("isDownloaded");

            if (!"Y".equals(showAll)) {
                // check for a partyId
                if (UtilValidate.isNotEmpty(partyId)) {
                    paramList = paramList + "&partyId=" + partyId;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.EQUALS, EntityFunction.UPPER(partyId)));
                }

                // now the statusId - send ANY for all statuses; leave null for just enabled; or pass a specific status
                if (statusId != null) {
                    paramList = paramList + "&statusId=" + statusId;
                    if (!"ANY".equalsIgnoreCase(statusId)) {
                        andExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
                    }
                } else {
                    // NOTE: _must_ explicitly allow null as it is not included in a not equal in many databases... odd but true
                    andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")));
                }
                // check for partyTypeId
                if (partyTypeId != null && !"ANY".equals(partyTypeId)) {
                    paramList = paramList + "&partyTypeId=" + partyTypeId;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyTypeId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+partyTypeId+"%")));
                }
                // PARTY attribute for downloadable stuff
                if ((UtilValidate.isNotEmpty(attrName) && UtilValidate.isNotEmpty(attrValue)) || UtilValidate.isNotEmpty(isDownloaded)) {
                    dynamicView.addMemberEntity("PATT", "PartyAttribute");
                    dynamicView.addAlias("PATT", "attrName");
                    dynamicView.addAlias("PATT", "attrValue");
                    dynamicView.addViewLink("PT", "PATT", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("partyId", "partyId")));
                }
                // ----
                // UserLogin Fields
                // ----

                // filter on user login
                if (UtilValidate.isNotEmpty(userLoginId)) {
                    paramList = paramList + "&userLoginId=" + userLoginId;

                    // modify the dynamic view
                    dynamicView.addMemberEntity("UL", "UserLogin");
                    dynamicView.addAlias("UL", "userLoginId");
                    dynamicView.addViewLink("PT", "UL", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+userLoginId+"%")));

                    fieldsToSelect.add("userLoginId");
                }

                // ----
                // PartyGroup Fields
                // ----

                // modify the dynamic view
                if (UtilValidate.isNotEmpty(groupName) || UtilValidate.isNotEmpty(groupNameLocal)) {
                    dynamicView.addMemberEntity("PG", "PartyGroup");
                    dynamicView.addAlias("PG", "groupName");
                    dynamicView.addAlias("PG", "groupNameLocal");
                    dynamicView.addViewLink("PT", "PG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    fieldsToSelect.add("groupName");
                    fieldsToSelect.add("groupNameLocal");
                }
                // filter on groupName
                if (UtilValidate.isNotEmpty(groupName)) 
                { 
                	paramList = paramList + "&groupName=" + groupName;
                	if (UtilValidate.isNotEmpty(groupNameStartWithFlag) && groupNameStartWithFlag.equalsIgnoreCase("Y")) 
                	{
                		andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, EntityFunction.UPPER(""+groupName+"%")));
                	}
                	else
                	{
                		andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+groupName+"%")));
                	}
                }

                // filter on groupNameLocal
                if (UtilValidate.isNotEmpty(groupNameLocal)) {
                    paramList = paramList + "&groupNameLocal=" + groupNameLocal;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupNameLocal"), EntityOperator.LIKE, EntityFunction.UPPER("%"+groupNameLocal+"%")));
                }
                if (UtilValidate.isNotEmpty(attrName) && UtilValidate.isNotEmpty(attrValue)) 
                {

                    List attrExprs = FastList.newInstance();
                	paramList = paramList + "&attrName=" + attrName;
                	paramList = paramList + "&attrValue=" + attrValue;
                    attrExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+attrName+"%")));
                    attrExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrValue"), EntityOperator.LIKE, EntityFunction.UPPER(attrValue)));
                	andExprs.add(EntityCondition.makeCondition(attrExprs, EntityOperator.AND));
                }
                
                if (UtilValidate.isNotEmpty(isDownloaded)) 
                {

                    List attrExprs = FastList.newInstance();
                	paramList = paramList + "&isDownloaded=" + attrName;
                    if ("Y".equals(isDownloaded))
                    {
                        attrExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrName"), EntityOperator.LIKE, EntityFunction.UPPER("IS_DOWNLOADED")));
                        attrExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrValue"), EntityOperator.LIKE, EntityFunction.UPPER("Y")));
                    	andExprs.add(EntityCondition.makeCondition(attrExprs, EntityOperator.AND));
                    }
                    else
                    {
                        attrExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrName"), EntityOperator.LIKE, EntityFunction.UPPER("IS_DOWNLOADED")));
                        List orExprs = FastList.newInstance();
                        orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrValue"), EntityOperator.LIKE, EntityFunction.UPPER("N")));
                        orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrValue"), EntityOperator.EQUALS, null));
                        attrExprs.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
                    	andExprs.add(EntityCondition.makeCondition(attrExprs, EntityOperator.AND));
                    }

                }
                // ----
                // Person Fields
                // ----

                // modify the dynamic view
                if (UtilValidate.isNotEmpty(firstName) || UtilValidate.isNotEmpty(lastName)|| UtilValidate.isNotEmpty(personName)) 
                {
                    dynamicView.addMemberEntity("PE", "Person");
                    dynamicView.addAlias("PE", "firstName");
                    dynamicView.addAlias("PE", "lastName");
                    dynamicView.addViewLink("PT", "PE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    fieldsToSelect.add("firstName");
                    fieldsToSelect.add("lastName");
                    orderBy.add("lastName");
                    orderBy.add("firstName");
                }

                if (UtilValidate.isNotEmpty(personName)) 
                {
                    
                    paramList = paramList + "&personName=" + personName;
                    List orExprs = FastList.newInstance();
                    orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+personName+"%")));
                    orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+personName+"%")));
                    andExprs.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
                }

                // filter on firstName
                if (UtilValidate.isNotEmpty(firstName)) {
                    paramList = paramList + "&firstName=" + firstName;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+firstName+"%")));
                }

                // filter on lastName
                if (UtilValidate.isNotEmpty(lastName)) {
                    paramList = paramList + "&lastName=" + lastName;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+lastName+"%")));
                }

                // ----
                // RoleType Fields
                // ----

                // filter on role member
                List roleTypes = (List) context.get("roleTypeIds");
                if (UtilValidate.isNotEmpty(roleTypes)) 
                {
                    Iterator i = roleTypes.iterator();
                    List orExprs = FastList.newInstance();
                    while (i.hasNext()) {
                        String roleTypesId = (String) i.next();
                        orExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypesId));
                    }
                    andExprs.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
                }
                
                if (roleTypeId != null && !"ANY".equals(roleTypeId)) {
                    paramList = paramList + "&roleTypeId=" + roleTypeId;
                    // add the expr
                    andExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));

                    fieldsToSelect.add("roleTypeId");
                }
                if ((roleTypeId != null && !"ANY".equals(roleTypeId)) || (roleTypes != null && roleTypes.size() > 0)) 
                {
                	// add role and productStoreId to view
                    dynamicView.addMemberEntity("PR", "ProductStoreRole");
                    dynamicView.addAlias("PR", "roleTypeId");
                    dynamicView.addAlias("PR", "productStoreId");
                    dynamicView.addViewLink("PT", "PR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                	
                }

                // ----
                // QualType Fields
                // ----

                // filter on QualType
                List qualTypes = (List) context.get("qualTypeIds");
                if (qualTypes != null) {
                    Iterator i = qualTypes.iterator();
                    List orExprs = FastList.newInstance();
                    while (i.hasNext()) {
                        String qualTypesId = (String) i.next();
                        orExprs.add(EntityCondition.makeCondition("partyQualTypeId", EntityOperator.EQUALS, qualTypesId));
                    }
                    andExprs.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
                }
                
                if (qualTypes != null && qualTypes.size() > 0) 
                {
                    // add QualType to view
                    dynamicView.addMemberEntity("PQ", "PartyQual");
                    dynamicView.addAlias("PQ", "partyQualTypeId");
                    dynamicView.addViewLink("PT", "PQ", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                	
                }
                // ----
                // SkillTypes Fields
                // ----
                // filter on SkillTypes
                
                List skillTypes = (List) context.get("skillTypeIds");
                if (skillTypes != null) {
                    Iterator i = skillTypes.iterator();
                    List orExprs = FastList.newInstance();
                    while (i.hasNext()) {
                        String skillTypeId = (String) i.next();
                        orExprs.add(EntityCondition.makeCondition("skillTypeId", EntityOperator.EQUALS, skillTypeId));
                    }
                    andExprs.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
                }
                if (skillTypes != null && skillTypes.size() > 0) 
                {
                    // add SkillType to view
                    dynamicView.addMemberEntity("PS", "PartySkill");
                    dynamicView.addAlias("PS", "skillTypeId");
                    dynamicView.addViewLink("PT", "PS", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                	
                }

                // ----
                // IdentificationType Fields
                // ----

                // filter on IdentificationType
                List identificationTypes = (List) context.get("identificationTypeIds");
                if (identificationTypes != null) {
                    Iterator i = identificationTypes.iterator();
                    List orExprs = FastList.newInstance();
                    while (i.hasNext()) {
                        String identificationTypesId = (String) i.next();
                        orExprs.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, identificationTypesId));
                    }
                    andExprs.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
                }
                
                if (identificationTypes != null && identificationTypes.size() > 0) 
                {
                    // add IdentificationType view
                    dynamicView.addMemberEntity("PI", "PartyIdentification");
                    dynamicView.addAlias("PI", "partyIdentificationTypeId");
                    dynamicView.addViewLink("PT", "PI", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                	
                }
                
                //filter by productStoreId
                if (UtilValidate.isNotEmpty(productStoreId) && ((roleTypes != null && roleTypes.size() > 0) || (roleTypeId != null))) 
                {
                	paramList = paramList + "&productStoreId=" + productStoreId;
                	andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productStoreId"), EntityOperator.EQUALS, EntityFunction.UPPER(productStoreId)));	
                }


                // ----
                // InventoryItem Fields
                // ----

                // filter on inventory item's fields
                String inventoryItemId = (String) context.get("inventoryItemId");
                String serialNumber = (String) context.get("serialNumber");
                String softIdentifier = (String) context.get("softIdentifier");
                if (UtilValidate.isNotEmpty(inventoryItemId) ||
                    UtilValidate.isNotEmpty(serialNumber) ||
                    UtilValidate.isNotEmpty(softIdentifier)) {

                    // add role to view
                    dynamicView.addMemberEntity("II", "InventoryItem");
                    dynamicView.addAlias("II", "ownerPartyId");
                    dynamicView.addViewLink("PT", "II", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId", "ownerPartyId"));
                }
                if (UtilValidate.isNotEmpty(inventoryItemId)) {
                    paramList = paramList + "&inventoryItemId=" + inventoryItemId;
                    dynamicView.addAlias("II", "inventoryItemId");
                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("inventoryItemId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + inventoryItemId + "%")));
                    fieldsToSelect.add("inventoryItemId");
                }
                if (UtilValidate.isNotEmpty(serialNumber)) {
                    paramList = paramList + "&serialNumber=" + serialNumber;
                    dynamicView.addAlias("II", "serialNumber");
                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("serialNumber"), EntityOperator.LIKE, EntityFunction.UPPER("%" + serialNumber + "%")));
                    fieldsToSelect.add("serialNumber");
                }
                if (UtilValidate.isNotEmpty(softIdentifier)) {
                    paramList = paramList + "&softIdentifier=" + softIdentifier;
                    dynamicView.addAlias("II", "softIdentifier");
                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("softIdentifier"), EntityOperator.LIKE, EntityFunction.UPPER("%" + softIdentifier + "%")));
                    fieldsToSelect.add("softIdentifier");
                }

                // ----
                // PostalAddress fields
                // ----
                if ("P".equals(extInfo)) {
                    // add address to dynamic view
                    dynamicView.addMemberEntity("PC", "PartyContactMech");
                    dynamicView.addMemberEntity("PA", "PostalAddress");
                    dynamicView.addAlias("PC", "contactMechId");
                    dynamicView.addAlias("PA", "address1");
                    dynamicView.addAlias("PA", "address2");
                    dynamicView.addAlias("PA", "city");
                    dynamicView.addAlias("PA", "stateProvinceGeoId");
                    dynamicView.addAlias("PA", "countryGeoId");
                    dynamicView.addAlias("PA", "postalCode");
                    dynamicView.addViewLink("PT", "PC", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                    dynamicView.addViewLink("PC", "PA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));

                    // filter on address1
                    String address1 = (String) context.get("address1");
                    if (UtilValidate.isNotEmpty(address1)) {
                        paramList = paramList + "&address1=" + address1;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("address1"), EntityOperator.LIKE, EntityFunction.UPPER("%" + address1 + "%")));
                    }

                    // filter on address2
                    String address2 = (String) context.get("address2");
                    if (UtilValidate.isNotEmpty(address2)) {
                        paramList = paramList + "&address2=" + address2;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("address2"), EntityOperator.LIKE, EntityFunction.UPPER("%" + address2 + "%")));
                    }

                    // filter on city
                    String city = (String) context.get("city");
                    if (UtilValidate.isNotEmpty(city)) {
                        paramList = paramList + "&city=" + city;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("city"), EntityOperator.LIKE, EntityFunction.UPPER("%" + city + "%")));
                    }

                    // filter on state geo
                    if (stateProvinceGeoId != null && !"ANY".equals(stateProvinceGeoId)) {
                        paramList = paramList + "&stateProvinceGeoId=" + stateProvinceGeoId;
                        andExprs.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, stateProvinceGeoId));
                    }

                    // filter on postal code
                    String postalCode = (String) context.get("postalCode");
                    if (UtilValidate.isNotEmpty(postalCode)) {
                        paramList = paramList + "&postalCode=" + postalCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("postalCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + postalCode + "%")));
                    }

                    fieldsToSelect.add("postalCode");
                    fieldsToSelect.add("city");
                    fieldsToSelect.add("stateProvinceGeoId");
                }

                // ----
                // Generic CM Fields
                // ----
                if ("O".equals(extInfo)) {
                    // add info to dynamic view
                    dynamicView.addMemberEntity("PC", "PartyContactMechPurpose");
                    dynamicView.addMemberEntity("CM", "ContactMech");
                    dynamicView.addAlias("PC", "contactMechId");
                    dynamicView.addAlias("PC", "contactMechPurposeTypeId");
                    dynamicView.addAlias("PC", "fromDate");
                    dynamicView.addAlias("PC", "thruDate");
                    dynamicView.addAlias("CM", "infoString");
                    dynamicView.addViewLink("PT", "PC", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                    dynamicView.addViewLink("PC", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));

                    // filter on infoString
                    String infoString = (String) context.get("infoString");
                    if (UtilValidate.isNotEmpty(infoString)) {
                        paramList = paramList + "&infoString=" + infoString;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("infoString"), EntityOperator.LIKE, EntityFunction.UPPER("%"+infoString+"%")));
                        andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())));
                        andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())));
                        andExprs.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
                        fieldsToSelect.add("infoString");
                    }

                }

                // ----
                // TelecomNumber Fields
                // ----
                if ("T".equals(extInfo)) {
                    // add telecom to dynamic view
                    dynamicView.addMemberEntity("PC", "PartyContactMech");
                    dynamicView.addMemberEntity("TM", "TelecomNumber");
                    dynamicView.addAlias("PC", "contactMechId");
                    dynamicView.addAlias("TM", "countryCode");
                    dynamicView.addAlias("TM", "areaCode");
                    dynamicView.addAlias("TM", "contactNumber");
                    dynamicView.addViewLink("PT", "PC", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                    dynamicView.addViewLink("PC", "TM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));

                    // filter on countryCode
                    String countryCode = (String) context.get("countryCode");
                    if (UtilValidate.isNotEmpty(countryCode)) {
                        paramList = paramList + "&countryCode=" + countryCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("countryCode"), EntityOperator.EQUALS, EntityFunction.UPPER(countryCode)));
                    }

                    // filter on areaCode
                    String areaCode = (String) context.get("areaCode");
                    if (UtilValidate.isNotEmpty(areaCode)) {
                        paramList = paramList + "&areaCode=" + areaCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("areaCode"), EntityOperator.EQUALS, EntityFunction.UPPER(areaCode)));
                    }

                    // filter on contact number
                    String contactNumber = (String) context.get("contactNumber");
                    if (UtilValidate.isNotEmpty(contactNumber)) {
                        paramList = paramList + "&contactNumber=" + contactNumber;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactNumber"), EntityOperator.EQUALS, EntityFunction.UPPER(contactNumber)));
                    }

                    fieldsToSelect.add("contactNumber");
                    fieldsToSelect.add("areaCode");
                }
                
                // ---- End of Dynamic View Creation

                // build the main condition
                if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
            }

            Debug.logInfo("In findParty mainCond=" + mainCond, module);

            // do the lookup
            if (mainCond != null || "Y".equals(showAll)) {
                try {
                    // get the indexes for the partial list
                    lowIndex = (((viewIndex - 1) * viewSize) + 1);
                    highIndex = viewIndex * viewSize;

                    // set distinct on so we only get one row per order
                    EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
//                    findOpts.setMaxRows(highIndex);
                    // using list iterator
                    EntityListIterator pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);

                    // attempt to get the full size
                    partyListSize = pli.getResultsSizeAfterPartialList();
                    
                    // Always return the complete list
                    // The next branch of logic will factor in the view size
                    if (partyListSize > 0)
                    {
                        completePartyList = pli.getCompleteList();
                    }
                    
                    pli.beforeFirst();
                    if (partyListSize > viewSize) {
                        partyList = pli.getPartialList(lowIndex, viewSize);
                    } else if (partyListSize > 0) {
                    	partyList = pli.getCompleteList();
                    }
                    
                    // get the partial list for this page

                    if (highIndex > partyListSize) {
                        highIndex = partyListSize;
                    }

                    // close the list iterator
                    pli.close();
                } catch (GenericEntityException e) {
                    String errMsg = "Failure in party find operation, rolling back transaction: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            } else {
                partyListSize = 0;
            }
        }

        if (partyList == null) partyList = FastList.newInstance();
        result.put("partyList", partyList);
        result.put("completePartyList", completePartyList);
        result.put("partyListSize", Integer.valueOf(partyListSize));
        result.put("paramList", paramList);
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("lowIndex", Integer.valueOf(lowIndex));

        return result;
    }

}
