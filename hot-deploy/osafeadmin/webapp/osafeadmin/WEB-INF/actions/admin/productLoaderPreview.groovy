package admin;

import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilValidate;

String xlsFileName = parameters.uploadedXLSFile;
String xlsFilePath = parameters.uploadedXLSPath;

session.setAttribute("uploadedXLSFile",parameters.uploadedXLSFile);
if(UtilValidate.isEmpty(parameters.uploadedXLSFile))
{
    xlsFileName = session.getAttribute("uploadedXLSFile");
}

if(UtilValidate.isEmpty(parameters.uploadedXLSPath))
{  
    xlsFilePath = parameters.xlsFilePath;
}

String tempDir = xlsFilePath;
String filePath = tempDir + xlsFileName;

List productCatDataList = FastList.newInstance();
List productDataList = FastList.newInstance();
List productAssocDataList = FastList.newInstance();
List productFacetGroupDataList = FastList.newInstance();
List productFacetValueDataList = FastList.newInstance();
List manufacturerDataList = FastList.newInstance();
List productAttributesDataList = FastList.newInstance();
List errorMessageList = FastList.newInstance();


List prodCatErrorList = FastList.newInstance();
List prodCatWarningList = FastList.newInstance();
List productErrorList = FastList.newInstance();
List productWarningList = FastList.newInstance();
List productAssocErrorList = FastList.newInstance();
List productAssocWarningList = FastList.newInstance();
List productFacetGroupErrorList = FastList.newInstance();
List productFacetGroupWarningList = FastList.newInstance();
List productFacetValueErrorList = FastList.newInstance();
List productFacetValueWarningList = FastList.newInstance();
List productManufacturerErrorList = FastList.newInstance();
List productManufacturerWarningList = FastList.newInstance();
List productAttributesErrorList = FastList.newInstance();
List productAttributesWarningList = FastList.newInstance();

if (UtilValidate.isNotEmpty(filePath) && UtilValidate.isNotEmpty(xlsFileName)) 
{
	Map<String, Object> productDataListSvcCtx = FastMap.newInstance();
	productDataListSvcCtx.put("productFilePath", xlsFilePath);
	productDataListSvcCtx.put("productFileName", xlsFileName);
	
	// Call Service to get the List of Product File Data
	Map productDataListSvcRes = dispatcher.runSync("getProductDataListFromFile", productDataListSvcCtx);
	
	productCatDataList = UtilGenerics.checkList(productDataListSvcRes.get("productCatDataList"), Map.class);
	productDataList = UtilGenerics.checkList(productDataListSvcRes.get("productDataList"), Map.class);
	productAssocDataList = UtilGenerics.checkList(productDataListSvcRes.get("productAssocDataList"), Map.class);
	productFacetGroupDataList = UtilGenerics.checkList(productDataListSvcRes.get("productFacetGroupDataList"), Map.class);
	productFacetValueDataList = UtilGenerics.checkList(productDataListSvcRes.get("productFacetValueDataList"), Map.class);
	manufacturerDataList = UtilGenerics.checkList(productDataListSvcRes.get("manufacturerDataList"), Map.class);
	productAttributesDataList = UtilGenerics.checkList(productDataListSvcRes.get("productAttributesDataList"), Map.class);
	
	errorMessageList = UtilGenerics.checkList(productDataListSvcRes.get("errorMessageList"), String.class);
}

Map<String, Object> svcCtx = FastMap.newInstance();
svcCtx.put("productCatDataList", productCatDataList);
svcCtx.put("productDataList", productDataList);
svcCtx.put("productAssocDataList", productAssocDataList);
svcCtx.put("productFacetGroupDataList", productFacetGroupDataList);
svcCtx.put("productFacetValueDataList", productFacetValueDataList);
svcCtx.put("manufacturerDataList", manufacturerDataList);
svcCtx.put("productAttributesDataList", productAttributesDataList);

//Call Service to Validate the Product File Data
svcRes = dispatcher.runSync("validateProductData", svcCtx);

prodCatErrorList = UtilGenerics.checkList(svcRes.get("prodCatErrorList"), String.class);
prodCatWarningList = UtilGenerics.checkList(svcRes.get("prodCatWarningList"), String.class);
productErrorList = UtilGenerics.checkList(svcRes.get("productErrorList"), String.class);
productWarningList = UtilGenerics.checkList(svcRes.get("productWarningList"), String.class);
productAssocErrorList = UtilGenerics.checkList(svcRes.get("productAssocErrorList"), String.class);
productAssocWarningList = UtilGenerics.checkList(svcRes.get("productAssocWarningList"), String.class);
productFacetGroupErrorList = UtilGenerics.checkList(svcRes.get("productFacetGroupErrorList"), String.class);
productFacetGroupWarningList = UtilGenerics.checkList(svcRes.get("productFacetGroupWarningList"), String.class);
productFacetValueErrorList = UtilGenerics.checkList(svcRes.get("productFacetValueErrorList"), String.class);
productFacetValueWarningList = UtilGenerics.checkList(svcRes.get("productFacetValueWarningList"), String.class);
productManufacturerErrorList = UtilGenerics.checkList(svcRes.get("productManufacturerErrorList"), String.class);
productManufacturerWarningList = UtilGenerics.checkList(svcRes.get("productManufacturerWarningList"), String.class);
productAttributesErrorList = UtilGenerics.checkList(svcRes.get("productAttributesErrorList"), String.class);
productAttributesWarningList = UtilGenerics.checkList(svcRes.get("productAttributesWarningList"), String.class);
validationErrorMessageList = UtilGenerics.checkList(svcRes.get("errorMessageList"), String.class);
if(UtilValidate.isNotEmpty(validationErrorMessageList))
{
	errorMessageList.addAll(validationErrorMessageList);
}


context.productCatDataList = productCatDataList;
context.productDataList = productDataList;
context.productAssocDataList = productAssocDataList;
context.productFacetGroupDataList = productFacetGroupDataList;
context.productFacetValueDataList = productFacetValueDataList;
context.manufacturerDataList = manufacturerDataList;
context.productAttributesDataList = productAttributesDataList;

context.xlsFileName = xlsFileName;
context.xlsFilePath = xlsFilePath;

context.prodCatErrorList = prodCatErrorList;
context.prodCatWarningList = prodCatWarningList;
context.productErrorList = productErrorList;
context.productWarningList = productWarningList;
context.productAssocErrorList = productAssocErrorList;
context.productAssocWarningList = productAssocWarningList;
context.productFacetGroupErrorList = productFacetGroupErrorList;
context.productFacetGroupWarningList = productFacetGroupWarningList;
context.productFacetValueErrorList = productFacetValueErrorList;
context.productFacetValueWarningList = productFacetValueWarningList;
context.productManufacturerErrorList = productManufacturerErrorList;
context.productManufacturerWarningList = productManufacturerWarningList;
context.productAttributesErrorList = productAttributesErrorList;
context.productAttributesWarningList = productAttributesWarningList;
context.errorMessageList = errorMessageList;