package product;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastList;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;

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