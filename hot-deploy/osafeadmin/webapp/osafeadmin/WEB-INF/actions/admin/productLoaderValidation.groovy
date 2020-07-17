package admin;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import com.osafe.util.OsafeAdminUtil;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.string.*
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.product.ProductWorker;

String productLoadImagesDir = parameters.productLoadImagesDir;

List prodCatErrorList = FastList.newInstance();
List prodCatWarningList = FastList.newInstance();

List productErrorList = FastList.newInstance();
List productWarningList = FastList.newInstance();

List productAssocErrorList = FastList.newInstance();
List productAssocWarningList = FastList.newInstance();

List productManufacturerErrorList = FastList.newInstance();

List productCatDataList = context.productCatDataList;
List productDataList = context.productDataList;
List productAssocDataList = context.productAssocDataList;
List manufacturerDataList = context.manufacturerDataList;

String osafeThemeServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "osafe.theme.server"), context);
String osafeThemeImagePath = osafeThemeServerPath; 
//Get the DEFAULT_IMAGE_DIRECTORY path from OsafeImagePath.xml

String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
	
List<Map<Object, Object>> imageLocationPrefList = OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);

Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();

for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {

    imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
    
}

String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
if(UtilValidate.isNotEmpty(defaultImageDirectory)) {
    osafeThemeImagePath = osafeThemeImagePath + defaultImageDirectory;
}

//Validation

List newProdCatIdList = FastList.newInstance();
List existingProdCatIdList = FastList.newInstance();
List itemNoList = FastList.newInstance();

//Validation for Product Category
int rowNo = 1;
for(Map productCategory : productCatDataList) {
    String parentCategoryId = (String)productCategory.get("parentCategoryId");
    String productCategoryId = (String)productCategory.get("productCategoryId");
    String categoryName = (String)productCategory.get("categoryName");
    String description = (String)productCategory.get("description");
    String longDescription = (String)productCategory.get("longDescription");
    String plpImageName = (String)productCategory.get("plpImageName");
    
    if(UtilValidate.isNotEmpty(productCategoryId))
    {
        if(!OsafeAdminUtil.isValidId(productCategoryId))
        {
            prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "CategoryId"), locale));
        }
    }
    
    if(UtilValidate.isNotEmpty(parentCategoryId))
    {
        if(UtilValidate.isEmpty(productCategoryId))
        {
            prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ParentCategoryIdAssociationError", UtilMisc.toMap("rowNo", rowNo), locale));
        } else {
            newProdCatIdList.add(productCategoryId);
        }
    }
    if(UtilValidate.isEmpty(categoryName))
    {
        prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankCategoryNameError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    if(UtilValidate.isEmpty(description))
    {
        prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankDescriptionError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    if(UtilValidate.isEmpty(longDescription))
    {
        prodCatWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankLongDescWarning", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    if(UtilValidate.isNotEmpty(plpImageName))
    {
        boolean isFileExist = OsafeAdminUtil.isFileExist(osafeThemeImagePath, plpImageName);
        if(!isFileExist)
        {
            prodCatWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PLPImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo), locale));
        }
    }
        
    rowNo++;
}

List newManufacturerIdList = FastList.newInstance();
List existingManufacturerIdList = FastList.newInstance();
for(Map manufacturerData : manufacturerDataList) {
    String manufacturerId = (String)manufacturerData.get("partyId")
    if(UtilValidate.isNotEmpty(manufacturerId)) {
        newManufacturerIdList.add(manufacturerId);
    } 
}
partyManufacturers = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId","MANUFACTURER"),UtilMisc.toList("partyId"));
for (GenericValue partyManufacturer : partyManufacturers) 
{
    party = (GenericValue) partyManufacturer.getRelatedOne("Party");
    partyId=party.getString("partyId");
    existingManufacturerIdList.add(partyId);
}

//Validation for Product
Map longDescMap = FastMap.newInstance();
for(Map product : productDataList) {
    String masterProductId = (String)product.get("masterProductId");
    String longDescription = (String)product.get("longDescription");
    if(UtilValidate.isNotEmpty(longDescription) && UtilValidate.isNotEmpty(masterProductId)) {
        longDescMap.put(masterProductId,longDescription);
    }
}

List newProductIdList = FastList.newInstance();
List existingProductIdList = FastList.newInstance();
rowNo = 1;
paramsExpr = FastList.newInstance();
paramsExpr.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_EQUAL, null));
if (UtilValidate.isNotEmpty(paramsExpr)) {
    paramCond = EntityCondition.makeCondition(paramsExpr, EntityOperator.AND);
    mainCond = paramCond;
}
List existingProdCatList = delegator.findList("ProductCategory", mainCond, null, null, null, false);
Map itenNoMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(existingProdCatList))
{
    existingProdCatIdList = EntityUtil.getFieldListFromEntityList(existingProdCatList, "productCategoryId", true);
}
Map longDescErrorMap = FastMap.newInstance();
Map masterProductIdMap = FastMap.newInstance();
List virtualFinishProductIdList = FastList.newInstance();

for(Map product : productDataList) {
    String productCategoryId = (String)product.get("productCategoryId");
    String longDescription = (String)product.get("longDescription");
    String defaultPrice = (String)product.get("defaultPrice");
    String listPrice = (String)product.get("listPrice");
    String internalName = (String)product.get("internalName");
    String plpImage = (String)product.get("smallImage");
    String pdpRegularImage = (String)product.get("largeImage");
    String masterProductId = (String)product.get("masterProductId");
    String productId = (String)product.get("productId");
    String manufacturerId = (String)product.get("manufacturerId");
    String bfTotalInventory = (String)product.get("bfInventoryTot");
    String bfWHInventory = (String)product.get("bfInventoryWhs");
    
    if(UtilValidate.isNotEmpty(masterProductId))
    {
        if(!OsafeAdminUtil.isValidId(masterProductId))
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "MasterProductId"), locale));
        }
    }
    if(UtilValidate.isNotEmpty(productId))
    {
        if(!OsafeAdminUtil.isValidId(productId))
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "ProductId"), locale));
        }
    }
    
    
    if(UtilValidate.isEmpty(masterProductId))
    {
        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "MasterProductIdMissingError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    if(UtilValidate.isNotEmpty(masterProductId))
    {
        newProductIdList.add(masterProductId);
    }
    
    if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
    {
        if(masterProductId.equals(productId)) {
            masterProductIdMap.put(masterProductId, masterProductId);
            if(!virtualFinishProductIdList.contains(masterProductId))
            {
                virtualFinishProductIdList.add(masterProductId);
            }
            else
            {
                productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "VirtualFinishProductIdExistingError", UtilMisc.toMap("rowNo", rowNo, "masterProductId", masterProductId, "productId", productId), locale));
            }
            
        }
    }
    
    if(UtilValidate.isEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
    {
        if(!virtualFinishProductIdList.contains(masterProductId))
        {
            virtualFinishProductIdList.add(masterProductId);
        }
        else
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "VirtualFinishProductIdExistingError", UtilMisc.toMap("rowNo", rowNo, "masterProductId", masterProductId), locale));
        }
    }
    
    if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
    {
        if(!masterProductId.equals(productId)) {
            boolean virtualProductExists = false;
	        if(masterProductIdMap.containsKey(masterProductId))
	        {
	            virtualProductExists = true;
	        }
	        else
	        {
	            if(ProductWorker.isVirtual(delegator,masterProductId))
	            {
	                virtualProductExists = true;
	            } 
	        }
	        if(!virtualProductExists)
	        {
	            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidVirtualProductReferenceError", UtilMisc.toMap("rowNo", rowNo, "productId", productId, "masterProductId", masterProductId), locale));
	        }
        }
        
    }
    
    if(UtilValidate.isNotEmpty(productCategoryId))
    {
       productCategoryIdList = StringUtil.split(productCategoryId,",");
       boolean categoryIdMatch = true;
       for (List productCatId: productCategoryIdList) 
       {
           categoryIdMatch = false;
           if(newProdCatIdList.contains(productCatId.trim()))
           {
               categoryIdMatch = true;
           }
           if(!categoryIdMatch)
           {
               if(existingProdCatIdList.contains(productCatId.trim()))
               {
                   categoryIdMatch = true;
               } else {
                   break;
               }
           }
       }
       if(!categoryIdMatch)
       {
           productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "CategoryIdMatchingError", UtilMisc.toMap("rowNo", rowNo), locale));
       }
    }
     String longDescErrorAdded = "";
     if(UtilValidate.isNotEmpty(masterProductId)) {
         longDescErrorAdded = longDescErrorMap.get(masterProductId);
         if(!longDescMap.containsKey(masterProductId) && longDescErrorAdded != 'Y')
         {
             productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankLongDescError", UtilMisc.toMap("rowNo", rowNo), locale));
             longDescErrorMap.put(masterProductId,"Y");
         }
     }
    
    
    if(UtilValidate.isNotEmpty(defaultPrice))
    {
        boolean checkFloatResult = OsafeAdminUtil.isFloat(defaultPrice);
        if(!checkFloatResult)
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidSalesPriceError", UtilMisc.toMap("rowNo", rowNo), locale));
        }
    }
    if(UtilValidate.isNotEmpty(listPrice))
    {
        boolean checkFloatResult = OsafeAdminUtil.isFloat(listPrice);
        if(!checkFloatResult)
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidListPriceError", UtilMisc.toMap("rowNo", rowNo), locale));
        }
    }
    if(UtilValidate.isNotEmpty(manufacturerId))
    {
        boolean manufacturerIdMatch = false;
    
        if(newManufacturerIdList.contains(manufacturerId) || existingManufacturerIdList.contains(manufacturerId))
        {
            manufacturerIdMatch = true;
        }
        if(!manufacturerIdMatch)
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ManufacturerIdMatchingError", UtilMisc.toMap("rowNo", rowNo), locale));
        }
    }
    
    if(UtilValidate.isNotEmpty(plpImage))
    {
        boolean isPlpImageExist = OsafeAdminUtil.isFileExist(osafeThemeImagePath, plpImage);
        if(!isPlpImageExist)
        {
            productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PLPImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo), locale));
        }
    }
    if(UtilValidate.isNotEmpty(pdpRegularImage))
    {
        boolean isPdpRegularImageExist = OsafeAdminUtil.isFileExist(osafeThemeImagePath, pdpRegularImage);
        if(!isPdpRegularImageExist)
        {
            productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PDPRegularImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo), locale));
        }
    }
       
    if(UtilValidate.isNotEmpty(bfTotalInventory))
    {
        boolean bfTotalInventoryVaild = UtilValidate.isSignedInteger(bfTotalInventory);
        if(!bfTotalInventoryVaild)
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFTotalInventoryRowError", UtilMisc.toMap("rowNo", rowNo), locale));
        }
        else
        {
            if(Integer.parseInt(bfTotalInventory) < -9999 || Integer.parseInt(bfTotalInventory) > 99999)
            {
                productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFTotalInventoryRowError", UtilMisc.toMap("rowNo", rowNo), locale));
            } 
        }
    }
    if(UtilValidate.isNotEmpty(bfWHInventory))
    {
        boolean bfWHInventoryVaild = UtilValidate.isSignedInteger(bfWHInventory);
        if(!bfWHInventoryVaild)
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFWHInventoryRowError", UtilMisc.toMap("rowNo", rowNo), locale));
        }
        else
        {
            if(Integer.parseInt(bfWHInventory) < -9999 || Integer.parseInt(bfWHInventory) > 99999)
            {
                productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFWHInventoryRowError", UtilMisc.toMap("rowNo", rowNo), locale));
            } 
        }
    }   
        
    if(UtilValidate.isNotEmpty(internalName))
    {
        List itenNoRowList = FastList.newInstance();
        if(itenNoMap.get(internalName)){
            itenNoRowList = (List)itenNoMap.get(internalName);
        } else {
            itenNoRowList = FastList.newInstance();
        }
        itenNoRowList.add(rowNo);
        itenNoMap.put(internalName,itenNoRowList);
    }
    rowNo++;
}
for (Map.Entry entry : itenNoMap.entrySet()) {
    List itenNoRowList = (List)entry.getValue();
    if(itenNoRowList.size() > 1)
    {
        for(Integer itemRowNo : itenNoRowList){
            productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "UniqueItemNoWarning", UtilMisc.toMap("rowNo", itemRowNo), locale));
        }
    }
}

//Validation for Product Associations
rowNo = 1;
List existingProductList = delegator.findList("Product", null, null, null, null, false);
if(UtilValidate.isNotEmpty(existingProductList))
{
    existingProductIdList = EntityUtil.getFieldListFromEntityList(existingProductList, "productId", true);
}
for(Map productAssoc : productAssocDataList) {
    String productId = (String)productAssoc.get("productId");
    String productIdTo = (String)productAssoc.get("productIdTo");
    boolean productIdMatch = false;
    boolean productIdToMatch = false;
    
    if(newProductIdList.contains(productId) || existingProductIdList.contains(productId))
    {
        productIdMatch = true;
    }
    if(!productIdMatch)
    {
        productAssocErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductIdMatchingError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    
    if(newProductIdList.contains(productIdTo) || existingProductIdList.contains(productIdTo))
    {
        productIdToMatch = true;
    }
    if(!productIdToMatch)
    {
        productAssocErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductIdToMatchingError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    rowNo++;
}

//Validation for Product Manufacturers
rowNo = 1;
for(Map manufacturer : manufacturerDataList) {
    String manufacturerId = (String)manufacturer.get("partyId");
    
    if(UtilValidate.isNotEmpty(manufacturerId))
    {
        if(!OsafeAdminUtil.isValidId(manufacturerId))
        {
            productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "ManuId"), locale));
        }
    }
    rowNo++;
}

context.prodCatErrorList = prodCatErrorList;
context.prodCatWarningList = prodCatWarningList;
context.productErrorList = productErrorList;
context.productWarningList = productWarningList;
context.productAssocErrorList = productAssocErrorList;
context.productAssocWarningList = productAssocWarningList;
context.productManufacturerErrorList = productManufacturerErrorList;