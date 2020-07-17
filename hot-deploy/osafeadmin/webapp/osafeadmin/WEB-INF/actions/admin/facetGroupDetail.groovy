import org.ofbiz.entity.condition.EntityCondition;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;

productFeatureGroupId = StringUtils.trimToEmpty(parameters.facetGroupId);
productCategoryId = StringUtils.trimToEmpty(parameters.productCategoryId);

exprs = FastList.newInstance();
mainCond=null;

// productFeatureGroupId
if(UtilValidate.isNotEmpty(productFeatureGroupId))
{
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productFeatureGroupId"), EntityOperator.EQUALS, productFeatureGroupId.toUpperCase()));
}

// productCategoryId
if(UtilValidate.isNotEmpty(productCategoryId))
{
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCategoryId"), EntityOperator.EQUALS, productCategoryId.toUpperCase()));
}

if(UtilValidate.isNotEmpty(exprs))
{
	mainCond = EntityCondition.makeCondition(exprs, EntityOperator.AND);
}

orderBy = ["sequenceNum","productFeatureGroupId"];
productFeatureCatGrpApplList = delegator.findList("ProductFeatureCatGrpAppl", mainCond, null, orderBy, null, false);
context.productFeatureCatGrpApplList = productFeatureCatGrpApplList;
context.resultList = productFeatureCatGrpApplList;

if(UtilValidate.isNotEmpty(productFeatureCatGrpApplList))
{
	productFeatureCatGrpAppl = EntityUtil.getFirst(productFeatureCatGrpApplList);
	context.productFeatureCatGrpAppl = productFeatureCatGrpAppl;
}
