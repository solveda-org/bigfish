import org.ofbiz.entity.condition.EntityCondition;
orderBy = ["sequenceNum", "productFeatureGroupId"];
productFeatureCatGrpApplList = delegator.findList("ProductFeatureCatGrpAppl", EntityCondition.makeCondition([productCategoryId: parameters.productCategoryId]), null, orderBy, null, false);
context.productFeatureCatGrpApplList = productFeatureCatGrpApplList;
context.resultList = productFeatureCatGrpApplList;
