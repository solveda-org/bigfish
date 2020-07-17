package product;
import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilProperties;

productStoreList = delegator.findList("ProductStore",null,null,null,null,true);
productStore=EntityUtil.getFirst(productStoreList);

if (UtilValidate.isNotEmpty(productStore))
{
  productStoreId=productStore.getString("productStoreId");
  productStoreParmList = delegator.findByAnd("XProductStoreParm",UtilMisc.toMap("productStoreId",productStoreId));
  if (UtilValidate.isNotEmpty(productStoreParmList))
  {
    parmIter = productStoreParmList.iterator();
    while (parmIter.hasNext()) 
    {
      prodStoreParm = (GenericValue) parmIter.next();
      context.put(prodStoreParm.getString("parmKey"),prodStoreParm.getString("parmValue"));
    }
  }
}

if (UtilValidate.isNotEmpty(parameters.productId)) {
    product = delegator.findOne("Product",["productId":parameters.productId], true);
    context.product = product;
    if (product) 
     {
    	
            productContentWrapper = new ProductContentWrapper(product, request);
        
     }
    String productDetailHeading = "";
    if(productContentWrapper)
    {
        productDetailHeading = productContentWrapper.get("PRODUCT_NAME");
        if (UtilValidate.isEmpty(productDetailHeading)) {
            productDetailHeading = product.get("productName");
        }
        if (UtilValidate.isEmpty(productDetailHeading)) {
            productDetailHeading = product.get("internalName");
        }
        context.productDetailHeading = productDetailHeading;
        context.productContentWrapper = productContentWrapper;
    }
    try {
      totAltImg = Integer.parseInt(UtilProperties.getPropertyValue("osafe", "pdp-alternate-images"));
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
}