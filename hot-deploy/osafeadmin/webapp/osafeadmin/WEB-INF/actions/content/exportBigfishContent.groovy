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

import java.util.*;
import java.io.*;
import java.net.*;
import org.w3c.dom.*;
import org.ofbiz.security.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.webapp.pseudotag.*;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.transaction.*;
import org.ofbiz.entity.condition.*;
import com.osafe.util.OsafeAdminUtil;
import org.apache.commons.lang.StringUtils;
import javolution.util.FastList;

tmpDir = FileUtil.getFile("runtime/tmp");
exportFileName = "bigfish-content-export"+(OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyyMMdd-HHmm"))+".xml";
exportFile = new File(tmpDir, exportFileName);
if(!exportFile.exists()) 
{
    exportFile.createNewFile();
}

productStoreId = parameters.productStoreId;
exportContentLibrary = StringUtils.trimToEmpty(parameters.exportContentLibrary);
exportContentHomePage = StringUtils.trimToEmpty(parameters.exportContentHomePage);
exportContentSiteInfo = StringUtils.trimToEmpty(parameters.exportContentSiteInfo);
exportContentStaticPage = StringUtils.trimToEmpty(parameters.exportContentStaticPage);
exportContentPageTop = StringUtils.trimToEmpty(parameters.exportContentPageTop);
exportContentPDPSpot = StringUtils.trimToEmpty(parameters.exportContentPDPSpot);
exportContentEmail = StringUtils.trimToEmpty(parameters.exportContentEmail);
exportContentProdCat = StringUtils.trimToEmpty(parameters.exportContentProdCat);
exportContentTxt = StringUtils.trimToEmpty(parameters.exportContentTxtTemplate);
exportPageTagging = StringUtils.trimToEmpty(parameters.exportPageTagging);
exportPaymentGatewaySettings = StringUtils.trimToEmpty(parameters.exportPaymentGatewaySettings);
exportShippingCharges = StringUtils.trimToEmpty(parameters.exportShippingCharges);
exportSalesTaxes = StringUtils.trimToEmpty(parameters.exportSalesTaxes);
exportStores = StringUtils.trimToEmpty(parameters.exportStores);
exportPromotions = StringUtils.trimToEmpty(parameters.exportPromotions);

initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);

if (UtilValidate.isNotEmpty(initializedCB))
{
   context.initializedCB=initializedCB;
}

passedContentTypeIds = FastList.newInstance();
passedProdCatContentTypeIds = FastList.newInstance();

if(UtilValidate.isNotEmpty(exportContentLibrary)) 
{
    passedContentTypeIds.add("BF_CONTENT_LIBRARY");
    context.exportContentLibrary=exportContentLibrary;
}
if(UtilValidate.isNotEmpty(exportContentHomePage)) 
{
    passedContentTypeIds.add("BF_HOME_PAGE");
    context.exportContentHomePage=exportContentHomePage;
}
if(UtilValidate.isNotEmpty(exportContentSiteInfo)) 
{
    passedContentTypeIds.add("BF_SITE_INFO");
    context.exportContentSiteInfo=exportContentSiteInfo;
}
if(UtilValidate.isNotEmpty(exportContentStaticPage)) 
{
    passedContentTypeIds.add("BF_STATIC_PAGE");
    context.exportContentStaticPage=exportContentStaticPage;
}
if(UtilValidate.isNotEmpty(exportContentPageTop)) 
{
    passedContentTypeIds.add("BF_PAGE_TOP_SPOT");
    context.exportContentPageTop=exportContentPageTop;
}
if(UtilValidate.isNotEmpty(exportContentPDPSpot)) 
{
    passedContentTypeIds.add("BF_PDP_SPOT");
    context.exportContentPDPSpot=exportContentPDPSpot;
}
if(UtilValidate.isNotEmpty(exportContentEmail)) 
{
    passedContentTypeIds.add("BF_EMAIL_TEMPLATE");
    context.exportContentEmail=exportContentEmail;
}
if(UtilValidate.isNotEmpty(exportContentTxt)) 
{
    passedContentTypeIds.add("BF_TXT_TEMPLATE");
    context.exportContentTxt=exportContentTxt;
}

if(UtilValidate.isNotEmpty(exportContentProdCat)) 
{
    passedProdCatContentTypeIds.add("PDP_ADDITIONAL");
    passedProdCatContentTypeIds.add("PLP_ESPOT_PAGE_TOP");
    passedProdCatContentTypeIds.add("PLP_ESPOT_PAGE_END");
    passedProdCatContentTypeIds.add("PLP_ESPOT_FACET_TOP");
    passedProdCatContentTypeIds.add("PLP_ESPOT_FACET_END");
    passedProdCatContentTypeIds.add("PLP_ESPOT_MEGA_MENU");
    context.exportContentProdCat=exportContentProdCat;
}

if(UtilValidate.isNotEmpty(exportPageTagging)) 
{
    context.exportPageTagging = exportPageTagging;
}
if(UtilValidate.isNotEmpty(exportPaymentGatewaySettings)) 
{
    context.exportPaymentGatewaySettings = exportPaymentGatewaySettings;
}

if(UtilValidate.isNotEmpty(exportShippingCharges)) 
{
    context.exportShippingCharges = exportShippingCharges;
}

if(UtilValidate.isNotEmpty(exportSalesTaxes)) 
{
    context.exportSalesTaxes = exportSalesTaxes;
}

if(UtilValidate.isNotEmpty(exportStores)) 
{
    context.exportStores = exportStores;
}

if(UtilValidate.isNotEmpty(exportPromotions)) 
{
    context.exportPromotions = exportPromotions;
}

context.passedContentTypeIds = passedContentTypeIds;
context.passedProdCatContentTypeIds = passedProdCatContentTypeIds;

numberOfContentTypeIds = passedContentTypeIds?.size() ?: 0;
context.numberOfContentTypeIds = numberOfContentTypeIds;

partyContactMechPurposeTypeExpr = FastList.newInstance();
partyContactMechPurposeTypeExpr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "BILLING_LOCATION"));
partyContactMechPurposeTypeExpr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PAYMENT_LOCATION"));
partyContactMechPurposeTypeCond = EntityCondition.makeCondition(partyContactMechPurposeTypeExpr, EntityOperator.OR);

numberWritten = 0;
if (UtilValidate.isNotEmpty(exportFile)) 
{

    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile.getAbsolutePath()), "UTF-8")));
    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    writer.println("<entity-engine-xml>");

    for(contentTypeId in passedContentTypeIds) 
    {
        beganTransaction = TransactionUtil.begin(3600);
        try {
            TransactionUtil.commit(beganTransaction);
            contentType = delegator.findByPrimaryKey("ContentType", ["contentTypeId" : contentTypeId]);
            if (contentType!= null) 
            {
                contentType.writeXmlText(writer, "");
                numberWritten++;
                findXContentXrefMap = ["productStoreId" : productStoreId, "contentTypeId" : contentTypeId];
                xContentXrefList = delegator.findByAnd("XContentXref", findXContentXrefMap);
                for(xContentXref in xContentXrefList) 
                {
                    content = xContentXref.getRelatedOne("Content");
                    if (content != null) 
                    {
                        dataResource = content.getRelatedOne("DataResource");
                        if (dataResource != null) 
                        {
                            dataResource.writeXmlText(writer, "");
                            numberWritten++;
                            content.writeXmlText(writer, "");
                            numberWritten++;
                            xContentXref.writeXmlText(writer, "");
                            numberWritten++;
                            electronicText = dataResource.getRelatedOne("ElectronicText");
                            if (electronicText != null) 
                            {
                                electronicText.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                        else
                        {
                            content.writeXmlText(writer, "");
                            numberWritten++;
                            xContentXref.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        if (contentTypeId.equals("BF_STATIC_PAGE")) 
                        {
                            contentAttributes = content.getRelated("ContentAttribute");
                            for(contentAttribute in contentAttributes) {
                                contentAttribute.writeXmlText(writer, "");
                            }
                        }
                    }
                
                }
            }
        } catch (Exception exc) {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }finally {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    for(prodCatContentTypeId in passedProdCatContentTypeIds) 
    {
        beganTransaction = TransactionUtil.begin(3600);
        try {
            TransactionUtil.commit(beganTransaction);
            contentType = delegator.findByPrimaryKey("ProductCategoryContentType", ["prodCatContentTypeId" : prodCatContentTypeId]);
            if (contentType!= null) 
            {
                //contentType.writeXmlText(writer, "");
                numberWritten++;
                findProductCategoryContentMap = ["prodCatContentTypeId" : prodCatContentTypeId];
                productCategoryContentList = delegator.findByAnd("ProductCategoryContent", findProductCategoryContentMap);
                for(productCategoryContent in productCategoryContentList) 
                {
                    content = productCategoryContent.getRelatedOne("Content");
                    if (content != null) 
                    {
                        dataResource = content.getRelatedOne("DataResource");
                        if (dataResource != null) 
                        {
                            dataResource.writeXmlText(writer, "");
                            numberWritten++;
                            content.writeXmlText(writer, "");
                            numberWritten++;
                            productCategoryContent.writeXmlText(writer, "");
                            numberWritten++;
                            
                            electronicText = dataResource.getRelatedOne("ElectronicText");
                            if (electronicText != null) 
                            {
                                electronicText.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                        else
                        {
                            content.writeXmlText(writer, "");
                            numberWritten++;
                            productCategoryContent.writeXmlText(writer, "");
                            numberWritten++;
                        }
                    }
                
                }
            }
        } catch (Exception exc) {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }finally {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    
    if(UtilValidate.isNotEmpty(exportPageTagging)) 
    {
        beganTransaction = TransactionUtil.begin(3600);
        try 
        {
            TransactionUtil.commit(beganTransaction);
            List<GenericValue> pixelTrackingList = delegator.findByAnd("XPixelTracking", UtilMisc.toMap());
            for(GenericValue pixelTracking : pixelTrackingList)
            {
                content = pixelTracking.getRelatedOne("Content");
                if (content != null) 
                {
                    dataResource = content.getRelatedOne("DataResource");
                    if (dataResource != null) 
                    {
                        dataResource.writeXmlText(writer, "");
                        numberWritten++;
                        content.writeXmlText(writer, "");
                        numberWritten++;
                        pixelTracking.writeXmlText(writer, "");
                        numberWritten++;
                        
                        electronicText = dataResource.getRelatedOne("ElectronicText");
                        if (electronicText != null) 
                        {
                            electronicText.writeXmlText(writer, "");
                            numberWritten++;
                        }
                    }
                    else
                    {
                        content.writeXmlText(writer, "");
                        numberWritten++;
                        pixelTracking.writeXmlText(writer, "");
                        numberWritten++;
                    }
                }
            }
        } catch (Exception exc) {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }finally {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    
    
    if(UtilValidate.isNotEmpty(exportPaymentGatewaySettings)) 
    {
        beganTransaction = TransactionUtil.begin(3600);
        try {
            TransactionUtil.commit(beganTransaction);
            List<GenericValue> paymentGatewayConfigs = delegator.findByAnd("PaymentGatewayConfig", UtilMisc.toMap());
            for(GenericValue paymentGatewayConfig : paymentGatewayConfigs)
            {
                numberWritten++;
                paymentGatewayConfig.writeXmlText(writer, "");
                numberWritten++;
                paymentGatewayAuthorizeNet =  paymentGatewayConfig.getRelatedOne("PaymentGatewayAuthorizeNet");
                if(UtilValidate.isNotEmpty(paymentGatewayAuthorizeNet))
                {
                    paymentGatewayAuthorizeNet.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayClearCommerce =  paymentGatewayConfig.getRelatedOne("PaymentGatewayClearCommerce");
                if(UtilValidate.isNotEmpty(paymentGatewayClearCommerce))
                {
                    paymentGatewayClearCommerce.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayCyberSource =  paymentGatewayConfig.getRelatedOne("PaymentGatewayCyberSource");
                if(UtilValidate.isNotEmpty(paymentGatewayCyberSource))
                {
                    paymentGatewayCyberSource.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayEbs =  paymentGatewayConfig.getRelatedOne("PaymentGatewayEbs");
                if(UtilValidate.isNotEmpty(paymentGatewayEbs))
                {
                    paymentGatewayEbs.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayOrbital =  paymentGatewayConfig.getRelatedOne("PaymentGatewayOrbital");
                if(UtilValidate.isNotEmpty(paymentGatewayOrbital))
                {
                    paymentGatewayOrbital.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayPayNetz =  paymentGatewayConfig.getRelatedOne("PaymentGatewayPayNetz");
                if(UtilValidate.isNotEmpty(paymentGatewayPayNetz))
                {
                    paymentGatewayPayNetz.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayPayPal =  paymentGatewayConfig.getRelatedOne("PaymentGatewayPayPal");
                if(UtilValidate.isNotEmpty(paymentGatewayPayPal))
                {
                    paymentGatewayPayPal.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayPayflowPro =  paymentGatewayConfig.getRelatedOne("PaymentGatewayPayflowPro");
                if(UtilValidate.isNotEmpty(paymentGatewayPayflowPro))
                {
                    paymentGatewayPayflowPro.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewaySagePay =  paymentGatewayConfig.getRelatedOne("PaymentGatewaySagePay");
                if(UtilValidate.isNotEmpty(paymentGatewaySagePay))
                {
                    paymentGatewaySagePay.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewaySagePayToken =  paymentGatewayConfig.getRelatedOne("PaymentGatewaySagePayToken");
                if(UtilValidate.isNotEmpty(paymentGatewaySagePayToken))
                {
                    paymentGatewaySagePayToken.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayTenderCard =  paymentGatewayConfig.getRelatedOne("PaymentGatewayTenderCard");
                if(UtilValidate.isNotEmpty(paymentGatewayTenderCard))
                {
                    paymentGatewayTenderCard.writeXmlText(writer, "");
                    numberWritten++;
                }
                paymentGatewayWorldPay =  paymentGatewayConfig.getRelatedOne("PaymentGatewayWorldPay");
                if(UtilValidate.isNotEmpty(paymentGatewayWorldPay))
                {
                    paymentGatewayWorldPay.writeXmlText(writer, "");
                    numberWritten++;
                }
            }
            List<GenericValue> productStorePaymentSettings = delegator.findByAnd("ProductStorePaymentSetting", UtilMisc.toMap("productStoreId", productStoreId));
            if(UtilValidate.isNotEmpty(productStorePaymentSettings))
            {
                for(GenericValue productStorePaymentSetting : productStorePaymentSettings)
                {
                    customMethod = productStorePaymentSetting.getRelatedOne("CustomMethod");
                    if(UtilValidate.isNotEmpty(customMethod))
                    {
                        customMethod.writeXmlText(writer, "");
                        numberWritten++;
                    }
                    productStorePaymentSetting.writeXmlText(writer, "");
                    numberWritten++;
                }
            }
            
        } catch (Exception exc) {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }finally {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    
    if(UtilValidate.isNotEmpty(exportShippingCharges)) 
    {
        beganTransaction = TransactionUtil.begin(3600);
        try {
            TransactionUtil.commit(beganTransaction);
            List<GenericValue> partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", "CARRIER"));
            if(UtilValidate.isNotEmpty(partyRoles))
            {
                for(GenericValue partyRole : partyRoles)
                {
                    party = partyRole.getRelatedOne("Party");
                    if(UtilValidate.isNotEmpty(party))
                    {
                        party.writeXmlText(writer, "");
                        numberWritten++;
                        partyRole.writeXmlText(writer, "");
                        numberWritten++;
                        partyGroup = party.getRelatedOne("PartyGroup");
                        if(UtilValidate.isNotEmpty(partyGroup))
                        {
                            partyGroup.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        
                    }
                }  
            }
            
            List<GenericValue> shipmentMethodTypes = delegator.findByAnd("ShipmentMethodType", UtilMisc.toMap());
            if(UtilValidate.isNotEmpty(shipmentMethodTypes))
            {
                for(GenericValue shipmentMethodType : shipmentMethodTypes)
                {
                    shipmentMethodType.writeXmlText(writer, "");
                    numberWritten++;
                    List<GenericValue> carrierShipmentMethods = shipmentMethodType.getRelated("CarrierShipmentMethod");
                    if(UtilValidate.isNotEmpty(carrierShipmentMethods))
                    {
                        for(GenericValue carrierShipmentMethod : carrierShipmentMethods)
                        {
                            carrierShipmentMethod.writeXmlText(writer, "");
                            numberWritten++;
                        }
                    }
                }
            }
            
            List<GenericValue> productStoreShipmentMeths = delegator.findByAnd("ProductStoreShipmentMeth", UtilMisc.toMap("productStoreId",productStoreId));
            if(UtilValidate.isNotEmpty(productStoreShipmentMeths))
            {
                for(GenericValue productStoreShipmentMeth : productStoreShipmentMeths)
                {
                    shipmentGatewayConfig = productStoreShipmentMeth.getRelatedOne("ShipmentGatewayConfig");
                    if(UtilValidate.isNotEmpty(shipmentGatewayConfig))
                    {
                        shipmentGatewayConfig.writeXmlText(writer, "");
                        numberWritten++;
                        
                        shipmentGatewayBlueDart = shipmentGatewayConfig.getRelatedOne("ShipmentGatewayBlueDart");
                        if(UtilValidate.isNotEmpty(shipmentGatewayBlueDart))
                        {
                            shipmentGatewayBlueDart.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        shipmentGatewayDhl = shipmentGatewayConfig.getRelatedOne("ShipmentGatewayDhl");
                        if(UtilValidate.isNotEmpty(shipmentGatewayDhl))
                        {
                            shipmentGatewayDhl.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        shipmentGatewayFedex = shipmentGatewayConfig.getRelatedOne("ShipmentGatewayFedex");
                        if(UtilValidate.isNotEmpty(shipmentGatewayFedex))
                        {
                            shipmentGatewayFedex.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        shipmentGatewayUps = shipmentGatewayConfig.getRelatedOne("ShipmentGatewayUps");
                        if(UtilValidate.isNotEmpty(shipmentGatewayUps))
                        {
                            shipmentGatewayUps.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        shipmentGatewayUsps = shipmentGatewayConfig.getRelatedOne("ShipmentGatewayUsps");
                        if(UtilValidate.isNotEmpty(shipmentGatewayUsps))
                        {
                            shipmentGatewayUsps.writeXmlText(writer, "");
                            numberWritten++;
                        }
                    }
                    
                    customMethod = productStoreShipmentMeth.getRelatedOne("CustomMethod");
                    if(UtilValidate.isNotEmpty(customMethod))
                    {
                        customMethod.writeXmlText(writer, "");
                        numberWritten++;
                    }
                    productStoreShipmentMeth.writeXmlText(writer, "");
                    numberWritten++;
                }
            }
            List<GenericValue> shipmentCostEstimates = delegator.findByAnd("ShipmentCostEstimate", UtilMisc.toMap("productStoreId",productStoreId));
            if(UtilValidate.isNotEmpty(shipmentCostEstimates))
            {
                for(GenericValue shipmentCostEstimate : shipmentCostEstimates)
                {
                    shipmentCostEstimate.writeXmlText(writer, "");
                }
            }
        } 
        catch (Exception exc) 
        {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }
        finally 
        {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    
    if(UtilValidate.isNotEmpty(exportSalesTaxes)) 
    {
        beganTransaction = TransactionUtil.begin(3600);
        try 
        {
            TransactionUtil.commit(beganTransaction);
            List<GenericValue> partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", "TAX_AUTHORITY"));
            if(UtilValidate.isNotEmpty(partyRoles))
            {
                for(GenericValue partyRole : partyRoles)
                {
                    party = partyRole.getRelatedOne("Party");
                    if(UtilValidate.isNotEmpty(party))
                    {
                        party.writeXmlText(writer, "");
                        numberWritten++;
                        partyRole.writeXmlText(writer, "");
                        numberWritten++;
                        partyGroup = party.getRelatedOne("PartyGroup");
                        if(UtilValidate.isNotEmpty(partyGroup))
                        {
                            partyGroup.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        partyContactMechs = party.getRelated("PartyContactMech");
                        if(UtilValidate.isNotEmpty(partyContactMechs))
                        {
                            for(GenericValue partyContactMech : partyContactMechs)
                            {
                                contactMech = partyContactMech.getRelatedOne("ContactMech");
                                contactMech.writeXmlText(writer, "");
                                numberWritten++;
                                
                                partyContactMechPurposes = contactMech.getRelated("PartyContactMechPurpose");
                                partyContactMechPurposes = EntityUtil.filterByCondition(partyContactMechPurposes, partyContactMechPurposeTypeCond);
                                if(UtilValidate.isNotEmpty(partyContactMechPurposes))
                                {
                                    for(GenericValue partyContactMechPurpose : partyContactMechPurposes)
                                    {
                                        partyContactMechPurpose.writeXmlText(writer, "");
                                        numberWritten++;
                                        
                                        postalAddress = partyContactMechPurpose.getRelatedOne("PostalAddress");
                                        if(UtilValidate.isNotEmpty(postalAddress))
                                        {
                                            postalAddress.writeXmlText(writer, "");
                                            numberWritten++;
                                        } 
                                    }
                                }
                                partyContactMech.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                        taxAuthorities = party.getRelated("TaxAuthTaxAuthority");
                        if(UtilValidate.isNotEmpty(taxAuthorities))
                        {
                            for(GenericValue taxAuthority : taxAuthorities)
                            {
                                taxAuthGeo = taxAuthority.getRelatedOne("TaxAuthGeo");
                                if(UtilValidate.isNotEmpty(taxAuthGeo))
                                {
                                    taxAuthGeo.writeXmlText(writer, "");
                                    numberWritten++;
                                }
                                taxAuthority.writeXmlText(writer, "");
                                numberWritten++;
                                
                                taxAuthorityAssocs = taxAuthority.getRelated("TaxAuthorityAssoc");
                                if(UtilValidate.isNotEmpty(taxAuthorityAssocs))
                                {
                                    for(GenericValue taxAuthorityAssoc : taxAuthorityAssocs)
                                    {
                                        taxAuthorityAssoc.writeXmlText(writer, "");
                                        numberWritten++;
                                    }
                                }
                                taxAuthorityRateProducts = taxAuthority.getRelated("TaxAuthorityRateProduct");
                                taxAuthorityRateProducts = EntityUtil.filterByAnd(taxAuthorityRateProducts,UtilMisc.toMap("productStoreId",productStoreId));
                                if(UtilValidate.isNotEmpty(taxAuthorityRateProducts))
                                {
                                    for(GenericValue taxAuthorityRateProduct : taxAuthorityRateProducts)
                                    {
                                        taxAuthorityRateProduct.writeXmlText(writer, "");
                                        numberWritten++;
                                    }
                                }
                            }
                        }
                        
                    }
                }  
            }
            
        } 
        catch (Exception exc) 
        {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }
        finally 
        {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    
    if(UtilValidate.isNotEmpty(exportStores)) 
    {
        beganTransaction = TransactionUtil.begin(3600);
        try 
        {
            TransactionUtil.commit(beganTransaction);
            List<GenericValue> productStoreRoles = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStoreId, "roleTypeId", "STORE_LOCATION"));
            if(UtilValidate.isNotEmpty(productStoreRoles))
            {
                for(GenericValue productStoreRole : productStoreRoles)
                {
                    
                    party = productStoreRole.getRelatedOne("Party");
                    if(UtilValidate.isNotEmpty(party))
                    {
                        party.writeXmlText(writer, "");
                        numberWritten++;
                        partyRole = productStoreRole.getRelatedOne("PartyRole");
                        if(UtilValidate.isNotEmpty(partyRole))
                        {
                            partyRole.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        productStoreRole.writeXmlText(writer, "");
                        numberWritten++;
                        partyGroup = party.getRelatedOne("PartyGroup");
                        if(UtilValidate.isNotEmpty(partyGroup))
                        {
                            partyGroup.writeXmlText(writer, "");
                            numberWritten++;
                        }
                        partyContactMechs = party.getRelated("PartyContactMech");
                        if(UtilValidate.isNotEmpty(partyContactMechs))
                        {
                            for(GenericValue partyContactMech : partyContactMechs)
                            {
                                contactMech = partyContactMech.getRelatedOne("ContactMech");
                                contactMech.writeXmlText(writer, "");
                                numberWritten++;
                                
                                partyContactMechPurposes = contactMech.getRelated("PartyContactMechPurpose");
                                partyContactMechPurposes = EntityUtil.filterByDate(partyContactMechPurposes);
                                if(UtilValidate.isNotEmpty(partyContactMechPurposes))
                                {
                                    for(GenericValue partyContactMechPurpose : partyContactMechPurposes)
                                    {
                                        partyContactMechPurpose.writeXmlText(writer, "");
                                        numberWritten++;
                                        
                                        postalAddress = partyContactMechPurpose.getRelatedOne("PostalAddress");
                                        if(UtilValidate.isNotEmpty(postalAddress))
                                        {
                                            postalAddress.writeXmlText(writer, "");
                                            numberWritten++;
                                        } 
                                        
                                        telecomNumber = partyContactMechPurpose.getRelatedOne("TelecomNumber");
                                        if(UtilValidate.isNotEmpty(telecomNumber))
                                        {
                                            telecomNumber.writeXmlText(writer, "");
                                            numberWritten++;
                                        } 
                                    }
                                }
                                partyContactMech.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                        partyContents = party.getRelated("PartyContent");
                        if(UtilValidate.isNotEmpty(partyContents))
                        {
                            for(GenericValue partyContent : partyContents)
                            {
                                content = partyContent.getRelatedOne("Content");
                                if (content != null)
                                {
                                    dataResource = content.getRelatedOne("DataResource");
                                    if (dataResource != null)
                                    {
                                        dataResource.writeXmlText(writer, "");
                                        numberWritten++;
                                        content.writeXmlText(writer, "");
                                        numberWritten++;
                                        partyContent.writeXmlText(writer, "");
                                        numberWritten++;
                                        
                                        electronicText = dataResource.getRelatedOne("ElectronicText");
                                        if (electronicText != null)
                                        {
                                            electronicText.writeXmlText(writer, "");
                                            numberWritten++;
                                        }
                                    }
                                    else
                                    {
                                        content.writeXmlText(writer, "");
                                        numberWritten++;
                                        partyContent.writeXmlText(writer, "");
                                        numberWritten++;
                                    }
                                }
                            }
                        }
                        partyGeoPoints = party.getRelated("PartyGeoPoint");
                        if(UtilValidate.isNotEmpty(partyGeoPoints))
                        {
                            for(GenericValue partyGeoPoint : partyGeoPoints)
                            {
                                geoPoint = partyGeoPoint.getRelatedOne("GeoPoint");
                                if (geoPoint != null)
                                {
                                    geoPoint.writeXmlText(writer, "");
                                    numberWritten++;
                                    partyGeoPoint.writeXmlText(writer, "");
                                    numberWritten++;
                                }
                            }
                        }
                    }
                }
                
            }
            
        } 
        catch (Exception exc) 
        {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }
        finally 
        {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    
    if(UtilValidate.isNotEmpty(exportPromotions)) 
    {
        beganTransaction = TransactionUtil.begin(3600);
        try 
        {
            TransactionUtil.commit(beganTransaction);
            List<GenericValue> productStorePromoAppls = delegator.findByAnd("ProductStorePromoAppl", UtilMisc.toMap("productStoreId", productStoreId));
            if(UtilValidate.isNotEmpty(productStorePromoAppls))
            {
                for(GenericValue productStorePromoAppl : productStorePromoAppls)
                {
                    productPromo = productStorePromoAppl.getRelatedOne("ProductPromo");
                    if(UtilValidate.isNotEmpty(productPromo))
                    {
                        productPromo.writeXmlText(writer, "");
                        numberWritten++;
                        productStorePromoAppl.writeXmlText(writer, "");
                        numberWritten++;
                        productPromoConds = productPromo.getRelated("ProductPromoCond");
                        if(UtilValidate.isNotEmpty(productPromoConds))
                        {
                            for(GenericValue productPromoCond : productPromoConds)
                            {
	                            productPromoRule = productPromoCond.getRelatedOne("ProductPromoRule");
	                            if (productPromoRule != null)
	                            {
	                                productPromoRule.writeXmlText(writer, "");
	                                numberWritten++;
	                            }
                                productPromoCond.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                        productPromoActions = productPromo.getRelated("ProductPromoAction");
                        if(UtilValidate.isNotEmpty(productPromoActions))
                        {
                            for(GenericValue productPromoAction : productPromoActions)
                            {
                                productPromoRule = productPromoAction.getRelatedOne("ProductPromoRule");
                                if (productPromoRule != null)
                                {
                                    productPromoRule.writeXmlText(writer, "");
                                    numberWritten++;
                                }
                                productPromoAction.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                        productPromoCategories = productPromo.getRelated("ProductPromoCategory");
                        if(UtilValidate.isNotEmpty(productPromoCategories))
                        {
                            for(GenericValue productPromoCategory : productPromoCategories)
                            {
                                productPromoCategory.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                        productPromoProducts = productPromo.getRelated("ProductPromoProduct");
                        if(UtilValidate.isNotEmpty(productPromoProducts))
                        {
                            for(GenericValue productPromoProduct : productPromoProducts)
                            {
                                productPromoProduct.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                        productPromoCodes = productPromo.getRelated("ProductPromoCode");
                        if(UtilValidate.isNotEmpty(productPromoCodes))
                        {
                            for(GenericValue productPromoCode : productPromoCodes)
                            {
                                productPromoCode.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                    }
                }
            }
        } 
        catch (Exception exc) 
        {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }
        finally 
        {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    
    writer.println("</entity-engine-xml>");
    writer.close();
    Debug.log("Total records written from all entities: $numberWritten");
    context.numberWritten = numberWritten;

    /*Send xml for browser.*/
    response.setContentType("text/xml");
    response.setHeader("Content-Disposition","attachment; filename=\"" + exportFileName + "\";");

    InputStream inputStr = new FileInputStream(exportFile.getAbsolutePath());
    OutputStream out = response.getOutputStream();
    byte[] bytes = new byte[102400];
    int bytesRead;
    while ((bytesRead = inputStr.read(bytes)) != -1)
    {
        out.write(bytes, 0, bytesRead);
    }
    out.flush();
    out.close();
    inputStr.close();
}

