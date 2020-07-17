package product;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;

import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.base.util.UtilMisc;
import org.apache.commons.lang.StringUtils;

productFeatureList = FastList.newInstance();
if (UtilValidate.isNotEmpty(parameters.featureTypeId)) 
{
	featureTypeId = StringUtils.trimToEmpty(parameters.featureTypeId);
	productFeatureList = delegator.findByAnd("ProductFeature", UtilMisc.toMap("productFeatureTypeId", featureTypeId), UtilMisc.toList("description"));
	
	productFeatureType = delegator.findByPrimaryKey("ProductFeatureType", UtilMisc.toMap("productFeatureTypeId", featureTypeId));
	context.featureTypeId = featureTypeId;
	context.featureTypeDescription = productFeatureType.description; 
	context.productFeatureList = productFeatureList;
}


productFeatureId = StringUtils.trimToEmpty(parameters.productFeatureId);
productFeatureTypeId = StringUtils.trimToEmpty(parameters.productFeatureTypeId);
description = StringUtils.trimToEmpty(parameters.description);
initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);

if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}

if (UtilValidate.isNotEmpty(initializedCB))
{
   context.initializedCB=initializedCB;
}

prodFeatureCond = null;
paramsExpr = FastList.newInstance();

if (UtilValidate.isNotEmpty(productFeatureId))
{
	paramsExpr.add(EntityCondition.makeCondition("productFeatureId", EntityOperator.EQUALS, productFeatureId));
}
if (UtilValidate.isNotEmpty(productFeatureTypeId))
{
	paramsExpr.add(EntityCondition.makeCondition("productFeatureTypeId", EntityOperator.EQUALS, productFeatureTypeId));
}
if (UtilValidate.isNotEmpty(description))
{
	paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, "%"+description.toUpperCase()+"%"));
}
if (UtilValidate.isNotEmpty(paramsExpr))
{
	prodFeatureCond=EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
}
eli = null;
// set distinct
productFeatureFindOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);	
productFeatureSearchList=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{
	// Product feature dynamic view entity
	DynamicViewEntity productFeatureDve = new DynamicViewEntity();
	productFeatureDve.addMemberEntity("PF", "ProductFeature");
	productFeatureDve.addAlias("PF", "productFeatureId", "productFeatureId", null, null, null, null);
	productFeatureDve.addAlias("PF", "productFeatureTypeId", "productFeatureTypeId", null, null, null, null);
	productFeatureDve.addAlias("PF", "productFeatureCategoryId", "productFeatureCategoryId", null, null, null, null);
	productFeatureDve.addAlias("PF", "description", "description", null, null, null, null);
	//make relation with ProductFeatureType
	productFeatureDve.addRelation("one", "", "ProductFeatureType", UtilMisc.toList(new ModelKeyMap("productFeatureTypeId", "productFeatureTypeId")));
	productFeatureDve.addMemberEntity("PFT", "ProductFeatureType");
	productFeatureDve.addAlias("PFT", "productFeatureTypeDescription", "description", null, null, null, null);
	productFeatureDve.addViewLink("PF", "PFT", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productFeatureTypeId", "productFeatureTypeId")));
	eli = delegator.findListIteratorByCondition(productFeatureDve, prodFeatureCond, null, null, null, productFeatureFindOpts);
	productFeatureSearchList = eli.getCompleteList();
}
if (UtilValidate.isNotEmpty(eli))
{
	try
	{
		eli.close();
	}
	catch (GenericEntityException e)
	{}
}

pagingListSize=productFeatureSearchList.size();
context.pagingListSize=pagingListSize;
context.pagingList = productFeatureSearchList;