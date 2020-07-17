package com.osafe.events;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.HttpRequestFileUpload;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.osafe.services.OsafeAdminCatalogServices;
import com.osafe.services.OsafeAdminScheduledJobServices;
import com.osafe.util.OsafeAdminUtil;


public class OsafeAdminEvent {

    private static final ResourceBundle OSAFE_PROPS = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());
	
    public static String validateMediaContent(HttpServletRequest request, HttpServletResponse response) {
        Locale locale = UtilHttp.getLocale(request);
        if (locale == null)
            locale = Locale.getDefault();
        String mediaType = request.getParameter("mediaType");
        String newFolderName = request.getParameter("newFolderName");
        if(mediaType.equals("newFolder"))
        {
        	if(UtilValidate.isEmpty(newFolderName))
            {
        		String errMsg = UtilProperties.getMessage("OSafeAdminUiLabels", "NewFolderLabel", locale);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        	else
        	{
        		mediaType = newFolderName;
        	}
        }
        Map<String, Object> serviceContext = FastMap.newInstance();
        
        String osafeThemeServerPath = FlexibleStringExpander.expandString(OSAFE_PROPS.getString("osafeThemeServer"), serviceContext);
        String userContentImagePath = FlexibleStringExpander.expandString(OSAFE_PROPS.getString("userContentImagePath"), serviceContext);
        
        String contentTargetPath =  osafeThemeServerPath + userContentImagePath + mediaType+"/";
        String contentTempPath =  osafeThemeServerPath+"/osafe_theme/images/temp_user_content/";
        HttpRequestFileUpload uploadTempObject = new HttpRequestFileUpload();
        
        uploadTempObject.setSavePath(contentTempPath);
        try {
            uploadTempObject.doUpload(request);
        } catch(Exception e) {
            String errMsg = UtilProperties.getMessage("OSafeAdminUiLabels", "ValidMediaFileError", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        String mediaFileName = uploadTempObject.getFilename();
        List<File> fileList = OsafeAdminUtil.getUserContent(mediaType, true);
        List<String> fileNameList = FastList.newInstance();
        for(File file : fileList) {
            fileNameList.add(file.getName());
        }
        if(fileNameList.contains(mediaFileName)) {
            String errMsg = UtilProperties.getMessage("OSafeAdminUiLabels", "SameNameMediaFileError", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        } else {
            request.setAttribute("contentTempPath", contentTempPath);
            request.setAttribute("contentTargetPath", contentTargetPath);
            request.setAttribute("mediaFileName", mediaFileName);
        }
       return "success";
    }
    
    public static String catalogAssetChecker(HttpServletRequest request, HttpServletResponse response) 
    {
        List resultList = FastList.newInstance();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeAdminUiLabels", locale);
        if (locale == null)
            locale = Locale.getDefault();
        try
        {
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            Map<String, Object> result = ServiceUtil.returnSuccess();
            String browseRootProductCategoryId = (String) request.getParameter("browseRootProductCategoryId");
            
            List<Map<String, Object>> productCategories = FastList.newInstance();
            List<String> ProductContentTypeList = FastList.newInstance();
            //List of all Product Content URLs
            ProductContentTypeList.add("SMALL_IMAGE_URL");
            ProductContentTypeList.add("SMALL_IMAGE_ALT_URL");
            ProductContentTypeList.add("LARGE_IMAGE_URL");
            ProductContentTypeList.add("DETAIL_IMAGE_URL");
            ProductContentTypeList.add("THUMBNAIL_IMAGE_URL");
            ProductContentTypeList.add("PDP_VIDEO_URL");
            ProductContentTypeList.add("PDP_VIDEO_360_URL");
            int totAltImg = 1;
            if(UtilValidate.isNotEmpty(OSAFE_PROPS.getString("pdpAlternateImages")))
            {
                totAltImg = Integer.parseInt(OSAFE_PROPS.getString("pdpAlternateImages"));
            }
            for(int altImgNo = 1; altImgNo <= totAltImg; altImgNo++)
            {
                ProductContentTypeList.add("XTRA_IMG_"+altImgNo+"_LARGE");
                ProductContentTypeList.add("ADDITIONAL_IMAGE_"+altImgNo);
                ProductContentTypeList.add("XTRA_IMG_"+altImgNo+"_DETAIL");
            }
            productCategories = OsafeAdminCatalogServices.getRelatedCategories(delegator, browseRootProductCategoryId, null, true, false, true);
            Map<String, Object> serviceContext = FastMap.newInstance();
            String osafeThemeServerPath = FlexibleStringExpander.expandString(OSAFE_PROPS.getString("osafeThemeServer"), serviceContext);
            for (Map<String, Object> workingCategoryMap : productCategories) 
            {
               GenericValue workingCategory = null;
               workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
               if(UtilValidate.isNotEmpty(workingCategory))
               {
                  String categoryImageURL =workingCategory.getString("categoryImageUrl");
                   // Asset check for ACTIVE CATEGORIES
                   if(UtilValidate.isNotEmpty(categoryImageURL))
                   {
                      String categoryImagePath = osafeThemeServerPath + categoryImageURL;
                       if(!(new File(categoryImagePath).exists()))
                       {
                           Map<String, Object> categoryInfoMap = FastMap.newInstance();
                           categoryInfoMap.put("type",uiLabelMap.get("CategoryLabel"));
                           categoryInfoMap.put("assetID",workingCategory.getString("productCategoryId"));
                           categoryInfoMap.put("ID",workingCategory.getString("productCategoryId"));
                           categoryInfoMap.put("description",workingCategory.getString("description"));
                           categoryInfoMap.put("assetType","CATEGORY_IMAGE_URL");
                           categoryInfoMap.put("imageURL",categoryImageURL);
                           resultList.add(categoryInfoMap);
                       }
                       
                   }
                   else if(UtilValidate.isEmpty(categoryImageURL))
                   {
                       Map<String, Object> categoryInfoMap = FastMap.newInstance();
                       categoryInfoMap.put("type",uiLabelMap.get("CategoryLabel"));
                       categoryInfoMap.put("assetID",workingCategory.getString("productCategoryId"));
                       categoryInfoMap.put("ID",workingCategory.getString("productCategoryId"));
                       categoryInfoMap.put("description",workingCategory.getString("description"));
                       categoryInfoMap.put("assetType","CATEGORY_IMAGE_URL");
                       categoryInfoMap.put("imageURL",uiLabelMap.get("BlankURLLabel"));
                       resultList.add(categoryInfoMap);
                   }
                   // Asset check for ASSOCIATED PRODUCTS(Virtual + Variants) to an active category.  
                   List<GenericValue> productCategoryMembers = EntityUtil.filterByDate(delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", workingCategory.getString("productCategoryId"))));
                   for (GenericValue productCategoryMember : productCategoryMembers) 
                   {
                       List<GenericValue> productList = FastList.newInstance();
                       GenericValue product = productCategoryMember.getRelatedOne("Product");
                       if(UtilValidate.isNotEmpty(product))
                       {
                           //Making List of All virtual Products and corresponding variant products.
                           String productId = product.getString("productId");
                           productList.add(product);
                           if("Y".equals(product.getString("isVirtual")))
                           {
                               List<GenericValue> virtualProductAssocs = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"), UtilMisc.toList("-fromDate"));
                               for(GenericValue virtualProductAssoc : virtualProductAssocs) 
                               {
                                   productList.add(virtualProductAssoc.getRelatedOne("AssocProduct"));
                               }
                           }
                           for(GenericValue productGV : productList) 
                           {
                               productId = productGV.getString("productId");
                               String productType = (String) uiLabelMap.get("ProductVirtualLabel");
                               String productName = ProductContentWrapper.getProductContentAsText(productGV, "PRODUCT_NAME", locale, dispatcher);
                               String contentId = null;
                               String productImageUrl =null;
                               if(productGV.getString("isVariant").equals("Y"))
                               {
                                    productType = (String) uiLabelMap.get("ProductVariantLabel");
                                    GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator);
                                    productName = ProductContentWrapper.getProductContentAsText(parentProduct, "PRODUCT_NAME", locale, dispatcher);
                               }
                               //Verifying ASSET existence for a product (virtual or variant)
                               for(String ContentType : ProductContentTypeList)
                               {   
                                   List<GenericValue> productContent = productGV.getRelated("ProductContent");
                                   productContent = EntityUtil.filterByDate(productContent,true);
                                   List<GenericValue> productContentType = EntityUtil.filterByAnd(productContent,UtilMisc.toMap("productContentTypeId",ContentType));
                                   //Verifying If the productContentTypeId exists for a particular product.
                                   if (UtilValidate.isNotEmpty(productContentType))
	                               {
                                       GenericValue productImgContent = EntityUtil.getFirst(productContentType);
                                       contentId = (String)productImgContent.get("contentId");
                                       if (UtilValidate.isNotEmpty(contentId))
                                       {
                                           GenericValue content = productImgContent.getRelatedOne("Content");
                                           productImageUrl = content.getRelatedOne("DataResource").getString("objectInfo");
                                       }
	                                   if (UtilValidate.isNotEmpty(productImageUrl))
	                                   {
	                                       String productImagePath =osafeThemeServerPath + productImageUrl;
	                                       if(!(new File(productImagePath).exists()))
	                                       {
	                                           Map<String, Object> productInfoMap = FastMap.newInstance();
	                                           productInfoMap.put("type",productType);
	                                           productInfoMap.put("assetID",productId+"_"+ContentType);
	                                           productInfoMap.put("ID",productId);
	                                           productInfoMap.put("description",productName);
	                                           productInfoMap.put("assetType",ContentType);
	                                           productInfoMap.put("imageURL",productImageUrl);
	                                           resultList.add(productInfoMap);
	                                       }
	                                   }
	                                   else if(UtilValidate.isEmpty(productImageUrl))
	                                   {
	                                       Map<String, Object> productInfoMap = FastMap.newInstance();
	                                       productInfoMap.put("type",productType);
	                                       productInfoMap.put("assetID",productId+"_"+ContentType);
	                                       productInfoMap.put("ID",productId);
	                                       productInfoMap.put("description",productName);
	                                       productInfoMap.put("assetType",ContentType);
	                                       productInfoMap.put("imageURL",uiLabelMap.get("BlankURLLabel"));
	                                       resultList.add(productInfoMap);
	                                   }
	                               }
                               }
                           } 
                       }
                   }
               }
            }
            request.setAttribute("resultList",resultList);
        }
        catch(Exception e)
        {
        }
        if(UtilValidate.isNotEmpty(resultList))
        {
            return "assetMissing";
        }
        
        else
        {
            List<String> success_list = new ArrayList<String>();
            success_list.add((String) uiLabelMap.get("AssetCheckSuccessInfo"));
            request.setAttribute("osafeSuccessMessageList", success_list);
            return "success";
        }
    }
    
    public static String checkProductFileInSession(HttpServletRequest request, HttpServletResponse response) 
    {
    	String xlsFileName = (String) request.getSession().getAttribute("uploadedXLSFile");
    	if(UtilValidate.isEmpty(xlsFileName)) {
    		request.setAttribute("_ERROR_MESSAGE_LIST_", null);
    		return "error";
    	}
    	else {
    		return "success";
    	}
    }
 
}