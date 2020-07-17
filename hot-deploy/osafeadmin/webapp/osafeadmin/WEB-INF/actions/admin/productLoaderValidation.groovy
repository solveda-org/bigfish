package admin;

import java.util.List;
import java.io.File;
import java.util.Map;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.FastMap;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import com.osafe.util.OsafeAdminUtil;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import com.osafe.util.OsafeProductLoaderHelper;
import org.ofbiz.base.util.Debug;
String productLoadImagesDir = parameters.productLoadImagesDir;
List prodCatErrorList = FastList.newInstance();
List prodCatWarningList = FastList.newInstance();
Set prevProdCatList = FastSet.newInstance();
List existingProdCatIdList = FastList.newInstance();

List existingProdCatList = delegator.findList("ProductCategory", null, null, null, null, false);
Map itenNoMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(existingProdCatList))
{
    existingProdCatIdList = EntityUtil.getFieldListFromEntityList(existingProdCatList, "productCategoryId", true);
}

List productErrorList = FastList.newInstance();
List productWarningList = FastList.newInstance();

List productAssocErrorList = FastList.newInstance();
List productAssocWarningList = FastList.newInstance();

List productFeatureSwatchErrorList = FastList.newInstance();

List productManufacturerErrorList = FastList.newInstance();

List productCatDataList = context.productCatDataList;
List productDataList = context.productDataList;
List productAssocDataList = context.productAssocDataList;
List productFeatureSwatchDataList = context.productFeatureSwatchDataList;
List manufacturerDataList = context.manufacturerDataList;

Set productFeatureSet = FastSet.newInstance();
Map mFeatureTypeMap = FastMap.newInstance();
int totalSelectableFeature = 5;
int totalDescriptiveFeature = 5;
List productFeatures = delegator.findList("ProductFeature", null, null, null, null, false);
List productFeatureIds = FastList.newInstance();
if(UtilValidate.isNotEmpty(productFeatures))
{
	productFeatureIds = EntityUtil.getFieldListFromEntityList(productFeatures,"productFeatureId", true);
}

String osafeThemeServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "osafe.theme.server"), context);
String osafeThemeImagePath = osafeThemeServerPath; 
//Get the DEFAULT_IMAGE_DIRECTORY path from OsafeImagePath.xml

String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
	
List<Map<Object, Object>> imageLocationPrefList = OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);

Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();

for(Map<Object, Object> imageLocationPref : imageLocationPrefList) 
{
    imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
}

String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
if(UtilValidate.isNotEmpty(defaultImageDirectory)) 
{
    osafeThemeImagePath = osafeThemeImagePath + defaultImageDirectory;
}

//Validation

List newProdCatIdList = FastList.newInstance();
List itemNoList = FastList.newInstance();

//Validation for Product Category
int rowNo = 1;
for(Map productCategory : productCatDataList) 
{
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
            prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "CategoryId", "idData", productCategoryId), locale));
        }
        if(productCategoryId.length() > 20)
        {
        	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "IdLengthExceedError", UtilMisc.toMap("rowNo", rowNo, "idField", "Category ID", "fieldData", productCategoryId), locale));
        }
        prevProdCatList.add(productCategoryId);
    }

    if(UtilValidate.isNotEmpty(parentCategoryId))
    {
    	parentCategoryIdMatch = false;
        if(prevProdCatList.contains(parentCategoryId.trim()))
        {
        	parentCategoryIdMatch = true;
        }
        else
        {
        	if(existingProdCatIdList.contains(parentCategoryId.trim()))
            {
        		parentCategoryIdMatch = true;
            }
        }
    
        if(!parentCategoryIdMatch)
        {
        	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ParentCategoryIdMatchingError", UtilMisc.toMap("rowNo", rowNo, "parentCategoryId", parentCategoryId), locale));
        }
        
        if(UtilValidate.isEmpty(productCategoryId))
        {
            prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ParentCategoryIdAssociationError", UtilMisc.toMap("rowNo", rowNo, "parentCategoryId", parentCategoryId), locale));
        }
        else 
        {
            newProdCatIdList.add(productCategoryId);
        }
    }
    else
    {
    	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankParentCategoryIdError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    if(UtilValidate.isEmpty(categoryName))
    {
        prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankCategoryNameError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    else
    {
      if(categoryName.length() > 100)
        {
        	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "CatNameLengthExceedError", UtilMisc.toMap("rowNo", rowNo, "idField", "Category Name", "fieldData", categoryName), locale));
        }
    } 
    
    if(UtilValidate.isEmpty(description))
    {
        prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankDescriptionError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    else
    {
      if(description.length() > 255)
        {
        	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "DescLengthExceedError", UtilMisc.toMap("rowNo", rowNo, "idField", "Category Description", "fieldData", description), locale));
        }
    }  
    if(UtilValidate.isEmpty(longDescription))
    {
        prodCatWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankLongDescWarning", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    if(UtilValidate.isNotEmpty(plpImageName))
    {
    	if(!UtilValidate.isUrl(plpImageName))
    	{
	        boolean isFileExist = (new File(osafeThemeImagePath, plpImageName)).exists();
	        if(!isFileExist)
	        {
	            prodCatWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PLPImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo, "plpImageData", plpImageName), locale));
	        }
    	}
    }
    rowNo++;
}

List newManufacturerIdList = FastList.newInstance();
List existingManufacturerIdList = FastList.newInstance();
for(Map manufacturerData : manufacturerDataList) 
{
    String manufacturerId = (String)manufacturerData.get("partyId")
    if(UtilValidate.isNotEmpty(manufacturerId)) 
    {
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
for(Map product : productDataList) 
{
    String masterProductId = (String)product.get("masterProductId");
    String longDescription = (String)product.get("longDescription");
    if(UtilValidate.isNotEmpty(longDescription) && UtilValidate.isNotEmpty(masterProductId)) 
    {
        longDescMap.put(masterProductId,longDescription);
    }
}

List newProductIdList = FastList.newInstance();
List existingProductIdList = FastList.newInstance();
rowNo = 1;

Map longDescErrorMap = FastMap.newInstance();
Map masterProductIdMap = FastMap.newInstance();
List virtualFinishProductIdList = FastList.newInstance();

for(Map product : productDataList) 
{
    String productCategoryId = (String)product.get("productCategoryId");
    String longDescription = (String)product.get("longDescription");
    String defaultPrice = (String)product.get("defaultPrice");
    String listPrice = (String)product.get("listPrice");
    String internalName = (String)product.get("internalName");
	String productName = (String)product.get("productName");
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
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "Master Product ID", "idData", masterProductId), locale));
        }
    }
    if(UtilValidate.isNotEmpty(productId))
    {
        if(!OsafeAdminUtil.isValidId(productId))
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "Product ID", "idData", productId), locale));
        }
    }
    
    if(UtilValidate.isNotEmpty(masterProductId) && masterProductId.length() > 20)
    {
    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "IdLengthExceedError", UtilMisc.toMap("rowNo", rowNo, "idField", "Master Product ID", "fieldData", masterProductId), locale));
    }
    if(UtilValidate.isNotEmpty(productId) && productId.length() > 20)
    {
    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "IdLengthExceedError", UtilMisc.toMap("rowNo", rowNo, "idField", "Product ID", "fieldData", productId), locale));
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
        if(masterProductId.equals(productId)) 
        {
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
        if(!masterProductId.equals(productId)) 
        {
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
               } 
           }
           if(!categoryIdMatch)
           {
               productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "CategoryIdMatchingError", UtilMisc.toMap("rowNo", rowNo, "categoryId", productCatId), locale));
           }
       }
    }
     String longDescErrorAdded = "";
     if(UtilValidate.isNotEmpty(masterProductId)) 
     {
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
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidSalesPriceError", UtilMisc.toMap("rowNo", rowNo, "salesPrice", defaultPrice), locale));
        }
    }
    if(UtilValidate.isNotEmpty(listPrice))
    {
        boolean checkFloatResult = OsafeAdminUtil.isFloat(listPrice);
        if(!checkFloatResult)
        {
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidListPriceError", UtilMisc.toMap("rowNo", rowNo, "listPrice", listPrice), locale));
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
            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ManufacturerIdMatchingError", UtilMisc.toMap("rowNo", rowNo, "manuId", manufacturerId), locale));
        }
    }
    
    if(UtilValidate.isNotEmpty(plpImage))
    {
        boolean isPlpImageExist = (new File(osafeThemeImagePath, plpImage)).exists();
        if(!UtilValidate.isUrl(plpImage))
    	{
        	if(!isPlpImageExist)
            {
                productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PLPImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo, "plpImageData", plpImage), locale));
            }
    	}
    }
    if(UtilValidate.isNotEmpty(pdpRegularImage))
    {
        boolean isPdpRegularImageExist = (new File(osafeThemeImagePath, pdpRegularImage)).exists();
        if(!UtilValidate.isUrl(pdpRegularImage))
    	{
        	if(!isPdpRegularImageExist)
            {
                productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PDPRegularImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo, "pdpRegularImage", pdpRegularImage), locale));
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
            else
            {
                if((UtilValidate.isNotEmpty(bfWHInventory)) && (Integer.parseInt(bfTotalInventory) <  Integer.parseInt(bfWHInventory)))
                {
                    productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFTotalInventoryRowError", UtilMisc.toMap("rowNo", rowNo), locale));
                }
            }
        }
    }
        
	if(UtilValidate.isNotEmpty(productName))
	{
		if(productName.size() > 100)
		{
			productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductNameLengthExceedRowError", UtilMisc.toMap("rowNo", rowNo), locale));
		}
		if(!OsafeAdminUtil.isValidName(productName))
        {
			productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductNameInvalidRowError", UtilMisc.toMap("rowNo", rowNo), locale));
        }
	}
	
    if(UtilValidate.isNotEmpty(internalName))
    {
		if(internalName.size() > 255)
		{
			productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InternalNameLengthExceedRowError", UtilMisc.toMap("rowNo", rowNo), locale));
		}
		if(!OsafeAdminUtil.isValidName(internalName))
        {
			productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InternalNameInvalidRowError", UtilMisc.toMap("rowNo", rowNo), locale));
        }
        List itenNoRowList = FastList.newInstance();
        if(itenNoMap.get(internalName))
        {
            itenNoRowList = (List)itenNoMap.get(internalName);
        } 
        else 
        {
            itenNoRowList = FastList.newInstance();
        }
        itenNoRowList.add(rowNo);
        itenNoMap.put(internalName,itenNoRowList);
    }
    
	for(int j = 1; j <= totalSelectableFeature; j++)
    {
		if(UtilValidate.isNotEmpty(product.get("selectabeFeature_"+j)))
		{
			String parseFeatureType = (String)product.get("selectabeFeature_"+j);
			int iFeatIdx = parseFeatureType.indexOf(':');
	        if (iFeatIdx > -1)
	        {
	            String featureType = parseFeatureType.substring(0,iFeatIdx).trim();
	            String sFeatures = parseFeatureType.substring(iFeatIdx +1);
	            String[] featureTokens = sFeatures.split(",");
	            for (int f=0;f < featureTokens.length;f++)
	            { 
					featureTypeFeatureId = featureType+":"+featureTokens[f].trim();
					tempFeatureTypeFeatureId = (featureType+""+featureTokens[f].trim()).replaceAll(" ", "_");
					if(!OsafeAdminUtil.isValidId(tempFeatureTypeFeatureId))
					{
						productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "Feature", "idData", featureTokens[f].trim()), locale));
					}
	                productFeatureSet.add(featureType+":"+featureTokens[f].trim());
	            }
	        }
		}
    }
	for(int j = 1; j <= totalDescriptiveFeature; j++)
    {
		if(UtilValidate.isNotEmpty(product.get("descriptiveFeature_"+j)))
		{
			String parseFeatureType = (String)product.get("descriptiveFeature_"+j);
			int iFeatIdx = parseFeatureType.indexOf(':');
	        if (iFeatIdx > -1)
	        {
	            String featureType = parseFeatureType.substring(0,iFeatIdx).trim();
	            String sFeatures = parseFeatureType.substring(iFeatIdx +1);
	            String[] featureTokens = sFeatures.split(",");
	            for (int f=0;f < featureTokens.length;f++)
	            { 
					featureTypeFeatureId = featureType+":"+featureTokens[f].trim();
					tempFeatureTypeFeatureId = (featureType+""+featureTokens[f].trim()).replaceAll(" ", "_");
					if(!OsafeAdminUtil.isValidId(tempFeatureTypeFeatureId))
					{
						productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "Feature", "idData", featureTokens[f].trim()), locale));
					}
	                productFeatureSet.add(featureType+":"+featureTokens[f].trim());
	            }
	        }
		}
    }
    rowNo++;
}
for (Map.Entry entry : itenNoMap.entrySet()) 
{
    List itenNoRowList = (List)entry.getValue();
    String internalName = (String)entry.getKey();
    if(itenNoRowList.size() > 1)
    {
        for(Integer itemRowNo : itenNoRowList)
        {
            productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "UniqueItemNoWarning", UtilMisc.toMap("rowNo", itemRowNo, "internalName", internalName), locale));
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
for(Map productAssoc : productAssocDataList) 
{
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
        productAssocErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductIdMatchingError", UtilMisc.toMap("rowNo", rowNo, "productId", productId), locale));
    }
    
    if(newProductIdList.contains(productIdTo) || existingProductIdList.contains(productIdTo))
    {
        productIdToMatch = true;
    }
    if(!productIdToMatch)
    {
        productAssocErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductIdToMatchingError", UtilMisc.toMap("rowNo", rowNo, "productIdTo", productIdTo), locale));
    }
    rowNo++;
}

//Validation for Product Feature Swatch
rowNo = 1;
for(Map productFeatureSwatch : productFeatureSwatchDataList) 
{
    String productFeatureId = (String)productFeatureSwatch.get("featureId");
    boolean productFeatureIdMatch = false;
    if(UtilValidate.isNotEmpty(productFeatureId))
    {
		countSemicolon = productFeatureId.count(":");
		tempProductFeatureId = productFeatureId;
		if(countSemicolon == 1)
		{
			//we expect that spaces MIGHT come after the semicolon and we ALWAYS expect one semicolon (ex. "COLOR:Animal Print")
			tempProductFeatureIdArray = tempProductFeatureId.split(":");
			tempProductFeatureId = tempProductFeatureIdArray[0] + tempProductFeatureIdArray[1].replaceAll(" ", "_");
		}
		if(!OsafeAdminUtil.isValidId(tempProductFeatureId))
		{
			productFeatureSwatchErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "Feature", "idData", tempProductFeatureIdArray[1].trim()), locale));
		}
		
        if(productFeatureSet.contains(productFeatureId))
        {
        	productFeatureIdMatch = true;
        }
        else
        {
        	if(UtilValidate.isNotEmpty(productFeatureIds))
        	{
	        	mFeatureTypeMap.clear();
	        	OsafeProductLoaderHelper.buildFeatureMap(mFeatureTypeMap, productFeatureId);
	        	
	        	Set featureTypeSet = mFeatureTypeMap.keySet();
	        	Map mFeatureIdImageExists = FastMap.newInstance();
	    		Iterator iterFeatureType = featureTypeSet.iterator();
	    		while (iterFeatureType.hasNext())
	    		{
	    			String featureType =(String)iterFeatureType.next();
	    			String featureTypeId = StringUtil.removeSpaces(featureType).toUpperCase();
	    			if (featureTypeId.length() > 20)
	    			{
	    				featureTypeId=featureTypeId.substring(0,20);
	    			}
	    			FastMap mFeatureMap=(FastMap)mFeatureTypeMap.get(featureType);
	        		Set featureSet = mFeatureMap.keySet();
	        		Iterator iterFeature = featureSet.iterator();
	        		while (iterFeature.hasNext())
	        		{
	        			String feature =(String)iterFeature.next();
	        			String featureId =StringUtil.removeSpaces(feature).toUpperCase();
	        			featureId =StringUtil.replaceString(featureId, "&", "");
	        			featureId=featureTypeId+"_"+featureId;
	        			if (featureId.length() > 20)
	        			{
	        				featureId=featureId.substring(0,20);
	        			}
	        			if(productFeatureIds.contains(featureId))
	        			{
	        			    productFeatureIdMatch = true;	
	        			}
	        		}
	    		}
        	}
        }
        if(!productFeatureIdMatch)
        {
        	productFeatureSwatchErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductFeatureMatchError", UtilMisc.toMap("rowNo", rowNo, "featureId", tempProductFeatureIdArray[1]), locale));
        }
    }
    rowNo++;
}


//Validation for Product Manufacturers
rowNo = 1;
for(Map manufacturer : manufacturerDataList) 
{
    String manufacturerId = (String)manufacturer.get("partyId");
    
    if(UtilValidate.isNotEmpty(manufacturerId))
    {
        if(!OsafeAdminUtil.isValidId(manufacturerId))
        {
            productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "ManuId", "idData", manufacturerId), locale));
        }
        if(manufacturerId.length() > 20)
        {
        	productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "IdLengthExceedError", UtilMisc.toMap("rowNo", rowNo, "idField", "Manu ID", "fieldData", manufacturerId), locale));
        }
    }
    else
    {
    	productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankManuIdError", UtilMisc.toMap("rowNo", rowNo), locale));
    }
    rowNo++;
}

context.prodCatErrorList = prodCatErrorList;
context.prodCatWarningList = prodCatWarningList;
context.productErrorList = productErrorList;
context.productWarningList = productWarningList;
context.productAssocErrorList = productAssocErrorList;
context.productAssocWarningList = productAssocWarningList;
context.productFeatureSwatchErrorList = productFeatureSwatchErrorList;
context.productManufacturerErrorList = productManufacturerErrorList;