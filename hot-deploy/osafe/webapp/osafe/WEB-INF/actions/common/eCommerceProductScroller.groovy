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
	    	
	if (UtilValidate.isNotEmpty(viewName) && !viewName.equalsIgnoreCase("ecommerceProductDetail"))
	{
	    session.setAttribute("PDP_LAST_VIEW", viewName);
	}
	    	
	if (UtilValidate.isNotEmpty(session.getAttribute("PDP_LAST_VIEW")) && (session.getAttribute("PDP_LAST_VIEW").equalsIgnoreCase("ecommerceProductList") || session.getAttribute("PDP_LAST_VIEW").equalsIgnoreCase("eCommercePlpQuickLook")))
	{
	    int currentProductIndex = 0;
		//get the productDocumentList generated in SolrEvents each time the PLP is displayed
	    List productDocumentList = session.getAttribute("productDocumentList");
		//traverse the list and when the current productId is found, get that index and set it to currentProductIndex
	    productDocumentList.eachWithIndex { productDocument, i ->
	        if(productId.equalsIgnoreCase(productDocument.productId))
	        {
	            currentProductIndex = i;
	        }
	    }
		//set currentProductIndex attribute to be used to find the next and previous product urls
		request.setAttribute("currentProductIndex", currentProductIndex);
	}	
}
    	
return "success";