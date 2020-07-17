import org.ofbiz.entity.condition.EntityCondition;
orderBy = ["sequenceNum ASC"];
productFeatureGrpApplList = delegator.findList("ProductFeatureGroupAppl", EntityCondition.makeCondition([productFeatureGroupId: parameters.productFeatureGroupId]), null, orderBy, null, false);
context.productFeatureGrpApplList = productFeatureGrpApplList;
context.resultList = productFeatureGrpApplList;