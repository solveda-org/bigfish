package product;

import javolution.util.FastList;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.GenericEntityException;

initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);

String productId = StringUtils.trimToEmpty(parameters.productId);
String productName = StringUtils.trimToEmpty(parameters.productName);
String internalName = StringUtils.trimToEmpty(parameters.internalName);

srchVirtualOnly=StringUtils.trimToEmpty(parameters.srchVirtualOnly);
srchFinishedGoodOnly=StringUtils.trimToEmpty(parameters.srchFinishedGoodOnly);
srchVariantOnly=StringUtils.trimToEmpty(parameters.srchVariantOnly);

atTime = UtilDateTime.nowTimestamp();

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

// Product dynamic view entity
DynamicViewEntity productDve = new DynamicViewEntity();
productDve.addMemberEntity("PR", "Product");
productDve.addAlias("PR", "productId", "productId", null, null, null, null);
productDve.addAlias("PR", "internalName", "internalName", null, null, null, null);
productDve.addAlias("PR", "isVirtual", "isVirtual", null, null, null, null);
productDve.addAlias("PR", "isVariant", "isVariant", null, null, null, null);
productDve.addAlias("PR", "introductionDate", "introductionDate", null, null, null, null);
productDve.addAlias("PR", "salesDiscontinuationDate", "salesDiscontinuationDate", null, null, null, null);

//FieldsToSelect
List productFields = FastList.newInstance();
productFields.add("productId");
productFields.add("internalName");
productFields.add("isVirtual");
productFields.add("isVariant");
productFields.add("introductionDate");
productFields.add("salesDiscontinuationDate");
// set distinct
productFindOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
prodCond = FastList.newInstance();
paramsExpr = FastList.newInstance();
prodCtntExprDesc = FastList.newInstance();
prodCtntExprName = FastList.newInstance();
prodCtntCondDesc = null;

if (UtilValidate.isNotEmpty(productId))
{
    paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.EQUALS, productId.toUpperCase()));
    context.productId=productId;
}
if (UtilValidate.isNotEmpty(productName))
{
    prodNameList = delegator.findByAnd("ProductContentAndText", [productContentTypeId : "PRODUCT_NAME"]);
    productIdListName = FastList.newInstance();
    if (UtilValidate.isNotEmpty(prodNameList))
    {
        for (GenericValue prodName : prodNameList)
        {
            prodNameTextData = prodName.textData;
            if (UtilValidate.isNotEmpty(prodNameTextData) && (prodNameTextData.toUpperCase()).contains(productName.toUpperCase()))
            {
                productIdListName.add(prodName.productId);
            }
        }
    }
    paramsExpr.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdListName));
    context.productName=productName;
}
if (UtilValidate.isNotEmpty(internalName))
{
    paramsExpr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),
            EntityOperator.LIKE, "%"+internalName.toUpperCase()+"%"));
    context.internalName=internalName;
}

// Reterive Only Virtual Product with CheckBox implementation
virtualExpr= FastList.newInstance();
virtSrchCond = null;
mainCond = null;

//When Virtual is checked.
if (UtilValidate.isNotEmpty(srchVirtualOnly) && UtilValidate.isEmpty(srchFinishedGoodOnly) && UtilValidate.isEmpty(srchVariantOnly))
{
    virtualExpr.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "Y"));
    context.srchVirtualOnly=srchVirtualOnly
}
//When Finished Good is checked.
else if(UtilValidate.isNotEmpty(srchFinishedGoodOnly) && UtilValidate.isEmpty(srchVirtualOnly) && UtilValidate.isEmpty(srchVariantOnly))
{
    virtualExpr.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
    virtualExpr.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, "N"));
}
//When Variant is checked.
else if(UtilValidate.isNotEmpty(srchVariantOnly) && UtilValidate.isEmpty(srchVirtualOnly) && UtilValidate.isEmpty(srchFinishedGoodOnly))
{
    virtualExpr.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, "Y"));
}
//When Finished Good and Variant are checked.
else if(UtilValidate.isNotEmpty(srchFinishedGoodOnly) && UtilValidate.isNotEmpty(srchVariantOnly) && UtilValidate.isEmpty(srchVirtualOnly))
{
    virtualExpr.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
}
//When Finished Good and Virtual are checked.
else if((UtilValidate.isNotEmpty(srchFinishedGoodOnly) && UtilValidate.isNotEmpty(srchVirtualOnly)) && (UtilValidate.isEmpty(srchVariantOnly)))
{
    virtualExpr.add(EntityCondition.makeCondition("isVariant", EntityOperator.NOT_EQUAL, "Y"));
}
//When Virtual and Variant are checked.
else if(UtilValidate.isNotEmpty(srchVariantOnly) && UtilValidate.isNotEmpty(srchVirtualOnly) && UtilValidate.isEmpty(srchFinishedGoodOnly))
{
    condExpr= FastList.newInstance();
    condExpr.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "Y"));
    condExpr.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, "Y"));
    virtualExpr.add(EntityCondition.makeCondition(condExpr, EntityOperator.OR));
}

if (UtilValidate.isNotEmpty(virtualExpr))
{
    virtSrchCond = EntityCondition.makeCondition(virtualExpr, EntityOperator.AND);
}

dateExpr= FastList.newInstance();
introDateExpr= FastList.newInstance();
introDateExpr.add(EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, atTime));
introDateExpr.add(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null));
dateExpr.add(EntityCondition.makeCondition(introDateExpr, EntityOperator.OR));

discoDateExpr= FastList.newInstance();
discoDateExpr.add(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, atTime));
discoDateExpr.add(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null));
dateExpr.add(EntityCondition.makeCondition(discoDateExpr, EntityOperator.OR));

dateCond = null;
if(UtilValidate.isNotEmpty(dateExpr))
{
    dateCond = EntityCondition.makeCondition(dateExpr, EntityOperator.AND);
}

prodCond=null;
if (UtilValidate.isNotEmpty(paramsExpr))
{
    prodCond=EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
    mainCond=prodCond;
}

if (UtilValidate.isNotEmpty(virtSrchCond))
{
    if (UtilValidate.isNotEmpty(prodCond))
    {
        mainCond = EntityCondition.makeCondition([prodCond, virtSrchCond], EntityOperator.AND);
    }
    else
    {
        mainCond=virtSrchCond;
    }
}

if (UtilValidate.isNotEmpty(dateCond))
{
    if (UtilValidate.isNotEmpty(mainCond))
    {
        mainCond = EntityCondition.makeCondition([mainCond, dateCond], EntityOperator.AND);
    }
    else
    {
        mainCond=dateCond;
    }
}

eli = null;
productSearchList=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{
    eli = delegator.findListIteratorByCondition(productDve, mainCond, null, productFields, null, productFindOpts);
    productSearchList = eli.getCompleteList();
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

pagingListSize=productSearchList.size();
context.pagingListSize=pagingListSize;
context.pagingList = productSearchList;
