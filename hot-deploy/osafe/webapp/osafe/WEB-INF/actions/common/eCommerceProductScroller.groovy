package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilGenerics;

productId = parameters.productId;
if(UtilValidate.isNotEmpty(productId))
{
	viewName = "";
	if (UtilValidate.isNotEmpty(session.getAttribute("_LAST_VIEW_NAME_"))) 
	{
	    viewName = (String) session.getAttribute("_LAST_VIEW_NAME_");
	    urlParams = UtilGenerics.checkMap(session.getAttribute("_LAST_VIEW_PARAMS_"));
	} 
	    	
	if (!viewName.equalsIgnoreCase("ecommerceProductDetail"))
	{
	    session.setAttribute("PDP_LAST_VIEW", viewName);
	}
	    	
	if (UtilValidate.isNotEmpty(session.getAttribute("PDP_LAST_VIEW")) && (session.getAttribute("PDP_LAST_VIEW").equalsIgnoreCase("ecommerceProductList") || session.getAttribute("PDP_LAST_VIEW").equalsIgnoreCase("eCommercePlpQuickLook")))
	{
	    int currentProductIndex = 0;
	    List productDocumentList = session.getAttribute("productDocumentList");
	    productDocumentList.eachWithIndex { productDocument, i ->
	        if(productId.equalsIgnoreCase(productDocument.productId))
	        {
	            currentProductIndex = i;
	        }
	    }
		request.setAttribute("currentProductIndex", currentProductIndex);
	}	
}
    	
return "success";