package product;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;

if (UtilValidate.isNotEmpty(context.virtualProduct)) 
{
    virtualProduct = context.virtualProduct;
    allProductFeatureAndAppls = virtualProduct.getRelated("ProductFeatureAndAppl");
    allProductFeatureAndAppls = EntityUtil.filterByDate(allProductFeatureAndAppls,true);
    allProductFeatureAndAppls = EntityUtil.orderBy(allProductFeatureAndAppls,UtilMisc.toList("sequenceNum"));
    
    allProductSelectableFeatureAndAppls = EntityUtil.filterByAnd(allProductFeatureAndAppls, UtilMisc.toMap("productFeatureApplTypeId", "SELECTABLE_FEATURE"));
    
    productSelFeatureTypes = FastList.newInstance();
    productSelFeaturesByType = new LinkedHashMap();
    for (GenericValue feature: allProductSelectableFeatureAndAppls) 
    {
       featureType = feature.getString("productFeatureTypeId");
       if (!productSelFeatureTypes.contains(featureType)) 
       {
          productSelFeatureTypes.add(featureType);
       }
       features = productSelFeaturesByType.get(featureType);
       if (UtilValidate.isEmpty(features)) 
       {
          features = FastList.newInstance();
          productSelFeaturesByType.put(featureType, features);
       }
       features.add(feature);
    }
    context.selFeatureTypesList = productSelFeatureTypes;
    context.selFeatureByTypeMap = productSelFeaturesByType;
    
    allProductDescFeatureAndAppls = EntityUtil.filterByAnd(allProductFeatureAndAppls, UtilMisc.toMap("productFeatureApplTypeId", "DISTINGUISHING_FEAT"));
    
    productDescFeatureTypes = FastList.newInstance();
    productDescFeaturesByType = new LinkedHashMap();
    for (GenericValue feature: allProductDescFeatureAndAppls) 
    {
       featureType = feature.getString("productFeatureTypeId");
       if (!productDescFeatureTypes.contains(featureType)) 
       {
          productDescFeatureTypes.add(featureType);
       }
       features = productDescFeaturesByType.get(featureType);
       if (UtilValidate.isEmpty(features)) 
       {
          features = FastList.newInstance();
          productDescFeaturesByType.put(featureType, features);
       }
       features.add(feature);
    }
    context.descFeatureTypesList = productDescFeatureTypes;
    context.descFeatureByTypeMap = productDescFeaturesByType; 
}

//BUILD CONTEXT MAP FOR PRODUCT_FEATURE_TYPE_ID and DESCRIPTION(EITHER FROM PRODUCT_FEATURE_GROUP OR PRODUCT_FEATURE_TYPE)
Map productFeatureTypesMap = FastMap.newInstance();
productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, false);

//get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
productFeatureGroupList = delegator.findList("ProductFeatureGroup", null, null, null, null, false);
productFeatureGroupAndApplList = delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, false);
productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);

if(UtilValidate.isNotEmpty(productFeatureTypesList))
{
    for (GenericValue productFeatureType : productFeatureTypesList)
    {
    	//filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
    	productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.productFeatureTypeId));
    	description = "";
    	if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
    	{
    		productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
        	productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.productFeatureGroupId));
        	productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
        	description = productFeatureGroup.description;
    	}
    	else
    	{
    		description = productFeatureType.description;
    	}
    	productFeatureTypesMap.put(productFeatureType.productFeatureTypeId,description);
    }
	
}
context.productFeatureTypesMap = productFeatureTypesMap;

if (UtilValidate.isNotEmpty(context.variantProduct)) 
{
	
    variantProduct = context.variantProduct;
    allVariantProductFeatureAndAppls = variantProduct.getRelated("ProductFeatureAndAppl");
    allVariantProductFeatureAndAppls = EntityUtil.filterByDate(allVariantProductFeatureAndAppls,true);
    allVariantProductFeatureAndAppls = EntityUtil.orderBy(allVariantProductFeatureAndAppls,UtilMisc.toList("sequenceNum"));
    
    variantProductStandardFeatureAndAppls = EntityUtil.filterByAnd(allVariantProductFeatureAndAppls, UtilMisc.toMap("productFeatureApplTypeId", "STANDARD_FEATURE"));
    context.variantProductStandardFeatureAndAppls = variantProductStandardFeatureAndAppls;
    variantProductStdFeatureTypes = FastList.newInstance();
    variantProductStdFeaturesByType = new LinkedHashMap();
    for (GenericValue feature: variantProductStandardFeatureAndAppls) 
    {
       featureType = feature.getString("productFeatureTypeId");
       if (!variantProductStdFeatureTypes.contains(featureType)) 
       {
          variantProductStdFeatureTypes.add(featureType);
       }
       features = variantProductStdFeaturesByType.get(featureType);
       if (UtilValidate.isEmpty(features)) 
       {
          features = FastList.newInstance();
          variantProductStdFeaturesByType.put(featureType, features);
       }
       features.add(feature);
    }
    variantProductStdFeatureTypesList = variantProductStdFeatureTypes;
    variantProductStdFeaturesByTypeMap = variantProductStdFeaturesByType;
    if (UtilValidate.isNotEmpty(variantProductStdFeatureTypesList))
        {
            for(String variantProductStdFeatureType: variantProductStdFeatureTypesList)
            {
                if (UtilValidate.isNotEmpty(variantProductStdFeaturesByTypeMap))
                {
                    variantProductFeatureAndApplList = variantProductStdFeaturesByTypeMap.get(variantProductStdFeatureType);
                    if (UtilValidate.isNotEmpty(variantProductFeatureAndApplList))
                    {
                        variantProductFeatureAndAppl = EntityUtil.getFirst(variantProductFeatureAndApplList);
                        productFeatureTypeLabel = productFeatureTypesMap.get(variantProductFeatureAndAppl.productFeatureTypeId);
                        if(UtilValidate.isNotEmpty(productFeatureTypeLabel))
                        {
                            if(UtilValidate.isNotEmpty(context.productDetailHeading))
                            {
                                context.productDetailHeading = context.productDetailHeading + ", " + productFeatureTypeLabel + ": " + variantProductFeatureAndAppl.description;
                            }
                        }
                    } 
                }
            }
        }
    
    variantProductDescFeatureAndAppls = EntityUtil.filterByAnd(allVariantProductFeatureAndAppls, UtilMisc.toMap("productFeatureApplTypeId", "DISTINGUISHING_FEAT"));
    context.variantProductDescFeatureAndAppls = variantProductDescFeatureAndAppls;
}


