package product;

import javolution.util.FastList;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilProperties;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.product.ProductWorker;
import org.apache.commons.lang.StringUtils;

osafeProperties = UtilProperties.getResourceBundleMap("OsafeProperties.xml", locale);
if (UtilValidate.isNotEmpty(parameters.productId)) 
{

    product = delegator.findOne("Product",["productId":parameters.productId], false);
    context.product = product;
    if (UtilValidate.isNotEmpty(product)) 
    {
        productContentWrapper = new ProductContentWrapper(product, request);
    }
    String productDetailHeading = "";
    if (UtilValidate.isNotEmpty(productContentWrapper))
    {
        productDetailHeading = StringEscapeUtils.unescapeHtml(productContentWrapper.get("PRODUCT_NAME").toString());
        if (UtilValidate.isEmpty(productDetailHeading)) 
        {
            productDetailHeading = product.get("productName");
        }
        if (UtilValidate.isEmpty(productDetailHeading)) 
        {
            productDetailHeading = product.get("internalName");
        }
        context.productDetailHeading = productDetailHeading;
        context.productContentWrapper = productContentWrapper;
    }
    try {
      totAltImg = Integer.parseInt(osafeProperties.pdpAlternateImages);
    }
    catch(NumberFormatException nfe) {
    	Debug.logError(nfe, nfe.getMessage(),"");
    	totAltImg = 4;
    }
    maxAltImages = FastList.newInstance();
    for(imgNo = 1; imgNo <= totAltImg; imgNo++)
    {
    	maxAltImages.add(imgNo.toString());
    }
    context.maxAltImages = maxAltImages;
    
    maxAattachs = FastList.newInstance();
    for(attachNo = 1; attachNo <= 3; attachNo++)
    {
    	maxAattachs.add(attachNo.toString());
    }
    context.maxAattachs = maxAattachs;
}