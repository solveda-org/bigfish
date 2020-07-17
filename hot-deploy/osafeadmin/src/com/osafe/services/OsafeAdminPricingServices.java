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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import com.osafe.util.OsafeAdminUtil;
import java.math.BigDecimal;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.MessageString;

public class OsafeAdminPricingServices {
    public static final String module = OsafeAdminUtil.class.getName();
    private static final String resource = "OSafeAdminUiLabels";
    
    public static Map<String, Object> createUpdateProdPriceRule(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher =dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        String productId = (String) context.get("productId");
        String priceType = (String) context.get("PriceTypeRadio");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map<String, String> fromQuantityInfo = UtilGenerics.checkMap(context.get("fromQuantityInfo"));
        Map<String, String> toQuantityInfo = UtilGenerics.checkMap(context.get("toQuantityInfo"));
        Map<String, String> priceInfo = UtilGenerics.checkMap(context.get("priceInfo"));
        Map<String, String> descriptionInfo = UtilGenerics.checkMap(context.get("descriptionInfo"));
              
        List<GenericValue> variantList = new ArrayList<GenericValue>();
        List<String> productIdList = new ArrayList<String>();
        
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        try {
            //variantList = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"));
            if(UtilValidate.isNotEmpty(variantList)) {
                productIdList = EntityUtil.getFieldListFromEntityList(variantList, "productIdTo", true);
            }
            productIdList.add(productId);
        } catch (Exception e) {
            Debug.logWarning(e, module);
        }
          //Delete all existing product price rules
        try {
            for(String productIds : productIdList) {
                List<GenericValue> productPriceCondList = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PRODUCT_ID", "condValue", productIds));
                if(UtilValidate.isNotEmpty(productPriceCondList)) {
                    List<String> productPriceRuleIdList = EntityUtil.getFieldListFromEntityList(productPriceCondList, "productPriceRuleId", true);
                    for(String productPriceRuleId : productPriceRuleIdList) {
                        Map<String, Object> updateProdPriceRuleCtx =null;
                        List<GenericValue> prdQtyBreakIdCondList = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("inputParamEnumId","PRIP_QUANTITY", "productPriceRuleId",productPriceRuleId));
                        if (UtilValidate.isNotEmpty(prdQtyBreakIdCondList)) {
                          //Check for Active Price Rule
                            List<GenericValue> productPriceRuleList = delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productPriceRuleId",productPriceRuleId));
                            productPriceRuleList = EntityUtil.filterByDate(productPriceRuleList);
                            if(UtilValidate.isNotEmpty(productPriceRuleList)) {
                                updateProdPriceRuleCtx = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "thruDate", fromDate, "userLogin", userLogin);
                                try {
                                    dispatcher.runSync("updateProductPriceRule", updateProdPriceRuleCtx);
                                } catch (GenericServiceException e) {
                                    Debug.logWarning(e, module);
                                }
                            }
                            
                        }
                    }
                }
            }
        } catch (Exception e) {
            Debug.logWarning(e, module);
        }
        //Update Product Volume Price
        if(priceType.equals("VolumePrice")) {
            if (fromQuantityInfo != null) {
                
                List<String> keyList = new ArrayList<String>();
                for (String rowKey: fromQuantityInfo.keySet()) {
                    keyList.add(rowKey);
                }
                Collections.sort(keyList);
                
                //Perform Validations
                boolean isBlankFromQty, isBlankToQty , isBlankPrice;
                isBlankFromQty = isBlankToQty = isBlankPrice = false;
                boolean isGap = false;
                boolean isValidFromQty, isValidToQty, isValidPrice, isValidToQtyValue;
                isValidFromQty = isValidToQty = isValidPrice = isValidToQtyValue= true;
                int lastRowKey = keyList.size();
                List<MessageString> error_list = new ArrayList<MessageString>();
                
                //This will validate each row of the volume price structure 
                for (String rowKey: keyList) {
                    String fromQuantity = (String)fromQuantityInfo.get(rowKey);
                    String toQuantity = (String)toQuantityInfo.get(rowKey);
                    String price = (String)priceInfo.get(rowKey);
                    int frmQty = 0;
                    int toQty = 0;
                    if(UtilValidate.isEmpty(fromQuantity)) {
                        isBlankFromQty = true;
                    } else {
                        try {
                            frmQty = Integer.parseInt(fromQuantity);
                        } catch (NumberFormatException nfe) {
                            isValidFromQty = false;
                            Debug.logWarning(nfe, module);
                        }
                    }
                    if(!rowKey.equals(Integer.toString(lastRowKey)) && UtilValidate.isEmpty(toQuantity)){
                        isBlankToQty = true;
                    } else {
                        if(UtilValidate.isNotEmpty(toQuantity)) {
                            try {
                                toQty = Integer.parseInt(toQuantity);
                            } catch (NumberFormatException nfe) {
                                isValidToQty = false;
                                Debug.logWarning(nfe, module);
                            }
                        }
                    }
                    if(UtilValidate.isEmpty(price)){
                        isBlankPrice = true;
                    } else {
                        boolean isFloatPrice = OsafeAdminUtil.isFloat(price);
                        if(isFloatPrice == false){
                            isValidPrice = false;
                        }
                    }
                    if(rowKey.equals(Integer.toString(lastRowKey)) && UtilValidate.isEmpty(toQuantity)) {
                        //Nothing to do
                    } else if((isValidToQty && !isBlankToQty) && (toQty < frmQty)) {
                        isValidToQtyValue = false;
                    }
                    if(Integer.parseInt(rowKey)>1) {
                        int prevRowKey = Integer.parseInt(rowKey)-1;
                        String prevToQuantity = (String)toQuantityInfo.get(Integer.toString(prevRowKey));
                        if(UtilValidate.isNotEmpty(prevToQuantity) && UtilValidate.isInteger(prevToQuantity)) {
                            int prevToQty = Integer.parseInt(prevToQuantity);
                            if((isValidFromQty && !isBlankFromQty) && (frmQty - prevToQty != 1)) {
                                isGap = true;
                            }
                        }
                    }
                }
                
                MessageString tmp = null;
                
                if(isBlankFromQty == true){
                	
                    //error_list.add(UtilProperties.getMessage(resource, "BlankFromQtyError", locale));
                	tmp = new MessageString(UtilProperties.getMessage(resource, "BlankFromQtyError", locale),"fromQty",true);
                	error_list.add(tmp);
                }
                if(isValidFromQty == false) {
                  //  error_list.add(UtilProperties.getMessage(resource, "ValidFromQtyError", locale));
                	tmp = new MessageString(UtilProperties.getMessage(resource, "ValidFromQtyError", locale),"fromQty",true);
                	error_list.add(tmp);
                }
                if(isBlankToQty == true){
                    //error_list.add(UtilProperties.getMessage(resource, "BlankToQtyError", locale));
                	tmp = new MessageString(UtilProperties.getMessage(resource, "BlankToQtyError", locale),"toQty",true);
                	error_list.add(tmp);
                }
                if(isValidToQty == false) {
                    //error_list.add(UtilProperties.getMessage(resource, "ValidToQtyError", locale));
                	tmp = new MessageString(UtilProperties.getMessage(resource, "ValidToQtyError", locale),"toQty",true);
                	error_list.add(tmp);
                }
                if(isBlankPrice == true){
                    //error_list.add(UtilProperties.getMessage(resource, "BlankPriceError", locale));
                	tmp = new MessageString(UtilProperties.getMessage(resource, "BlankPriceError", locale),"price",true);
                	error_list.add(tmp);
                }
                if(isValidPrice == false) {
                    //error_list.add(UtilProperties.getMessage(resource, "ValidPriceError", locale));
                	tmp = new MessageString(UtilProperties.getMessage(resource, "ValidPriceError", locale),"price",true);
                	error_list.add(tmp);
                }
                if(isValidToQtyValue == false){
                    //error_list.add(UtilProperties.getMessage(resource, "ValidToQtyValueError", locale));
                	tmp = new MessageString(UtilProperties.getMessage(resource, "ValidToQtyValueError", locale),"toQty",true);
                	error_list.add(tmp);
                }
                if(isGap == true){
                    //error_list.add(UtilProperties.getMessage(resource, "VolumePricingGapError", locale));
                	tmp = new MessageString(UtilProperties.getMessage(resource, "VolumePricingGapError", locale),"fromQty",true);
                	error_list.add(tmp);
                }
                if(error_list.size() > 0) {
                    return ServiceUtil.returnError(error_list);
                } else {
                                      
                    for (String rowKey: keyList) {
                        String fromQuantity = (String)fromQuantityInfo.get(rowKey);
                        String toQuantity = (String)toQuantityInfo.get(rowKey);
                        String price = (String)priceInfo.get(rowKey);
                        String description = (String)descriptionInfo.get(rowKey);
                        for(String productIds : productIdList) {
                            String ruleName = productIds+"-Qty Break-"+rowKey;
                            
                            //create Product Price Rule
                            Map<String, Object> priceRuleCtx =null;
                            Map priceRuleMap = null;
                            String productPriceRuleId = null;
                            priceRuleCtx = UtilMisc.toMap("ruleName", ruleName, "description", description, "fromDate", fromDate, "userLogin", userLogin);
                            try {
                                priceRuleMap = dispatcher.runSync("createProductPriceRule", priceRuleCtx);
                                productPriceRuleId = (String)priceRuleMap.get("productPriceRuleId");
                            } catch (GenericServiceException e) {
                                Debug.logWarning(e, module);
                            }
                            
                            //create Product Price Condition
                            Map<String, Object> priceCondCtx1 =null;
                            Map<String, Object> priceCondCtx2 =null;
                            Map<String, Object> priceCondCtx3 =null;
                            priceCondCtx1 = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "inputParamEnumId", "PRIP_QUANTITY","operatorEnumId","PRC_GTE","condValue",fromQuantity,"userLogin",userLogin);
                            priceCondCtx2 = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "inputParamEnumId", "PRIP_QUANTITY","operatorEnumId","PRC_LTE","condValue",toQuantity,"userLogin",userLogin);
                            try {
                                    priceCondCtx3 = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "inputParamEnumId", "PRIP_PRODUCT_ID","operatorEnumId","PRC_EQ","condValue",productIds,"userLogin",userLogin);
                                    dispatcher.runSync("createProductPriceCond", priceCondCtx1);
                                    if(UtilValidate.isNotEmpty(toQuantity)) {
                                        dispatcher.runSync("createProductPriceCond", priceCondCtx2);
                                    }
                                    dispatcher.runSync("createProductPriceCond", priceCondCtx3);
                            } catch (GenericServiceException e) {
                                Debug.logWarning(e, module);
                            }
                            
                          //create Product Price Action
                            Map<String, Object> priceActCtx =null;
                            BigDecimal priceBD = new BigDecimal(price);
                            priceActCtx = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "productPriceActionTypeId", "PRICE_FLAT","amount",priceBD,"userLogin",userLogin);
                            try {
                                dispatcher.runSync("createProductPriceAction", priceActCtx);
                            } catch (GenericServiceException e) {
                                Debug.logWarning(e, module);
                            }
                        }
                    }
                    return ServiceUtil.returnSuccess();
                }
            }
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> deleteProdPriceRule(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher =dctx.getDispatcher();
        String productPriceRuleId = (String) context.get("productPriceRuleId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        try {
            //Delete Record From ProductPriceCond
            List<GenericValue> productPriceCondList = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
            Map<String,Object> deletePriceRuleCondCtx = null;
            for(GenericValue prodPriceCond: productPriceCondList) {
                deletePriceRuleCondCtx = UtilMisc.toMap("productPriceRuleId", (String)prodPriceCond.getString("productPriceRuleId"), "productPriceCondSeqId", (String)prodPriceCond.getString("productPriceCondSeqId"), "userLogin", userLogin);
                dispatcher.runSync("deleteProductPriceCond", deletePriceRuleCondCtx);
            }
            
          //Delete Record From ProductPriceAction
            List<GenericValue> productPriceActionList = delegator.findByAnd("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
            Map<String,Object> deletePriceRuleActCtx = null;
            for(GenericValue prodPriceAct: productPriceActionList) {
                deletePriceRuleActCtx = UtilMisc.toMap("productPriceRuleId", (String)prodPriceAct.getString("productPriceRuleId"), "productPriceActionSeqId", (String)prodPriceAct.getString("productPriceActionSeqId"), "userLogin", userLogin);
                dispatcher.runSync("deleteProductPriceAction", deletePriceRuleActCtx);
            }
            
          //Delete Record From ProductPriceRule
            GenericValue prodPriceRule = delegator.findByPrimaryKey("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
            Map<String,Object> deletePriceRuleCtx = null;
            deletePriceRuleCtx = UtilMisc.toMap("productPriceRuleId", (String)prodPriceRule.getString("productPriceRuleId"), "userLogin", userLogin); 
            dispatcher.runSync("deleteProductPriceRule", deletePriceRuleCtx);
            
        } catch (Exception e) {
            Debug.logWarning(e, module);
        }
        return ServiceUtil.returnSuccess();
    }
}
