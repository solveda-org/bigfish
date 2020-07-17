import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.base.util.ObjectType;
import java.util.Date;
productCategoryRollupAndChild = null;

if(parameters.currentPrimaryParentCategoryId)
{
    parentProductCategoryId = parameters.currentPrimaryParentCategoryId;
}
else
{
    parentProductCategoryId = parameters.parentProductCategoryId;
}
if(parameters.activeFromDate)
{
    activeFromDate = (Timestamp) ObjectType.simpleTypeConvert(parameters.activeFromDate, "Timestamp", null, null);
}
if (parameters.productCategoryId) 
{

    exprBldr =  new EntityConditionBuilder();
    categoryCond = exprBldr.AND() 
    {
        EQUALS(productCategoryId: parameters.productCategoryId)
        EQUALS(parentProductCategoryId: parentProductCategoryId)
        EQUALS(fromDate: activeFromDate)
    }
    productCategoryRollupAndChilds = delegator.findList("ProductCategoryRollupAndChild", categoryCond, null, null, null, false);
    if (productCategoryRollupAndChilds) 
    {
        productCategoryRollupAndChild = EntityUtil.getFirst(productCategoryRollupAndChilds);
    }
    
    productCategory = delegator.findOne("ProductCategory", [productCategoryId : parameters.productCategoryId], false);
    context.productCategory = productCategory;
    context.productCategoryName = productCategory.categoryName?productCategory.categoryName:parameters.productCategoryId;
}
if(productCategoryRollupAndChild)
context.productCategoryRollupAndChild = productCategoryRollupAndChild;