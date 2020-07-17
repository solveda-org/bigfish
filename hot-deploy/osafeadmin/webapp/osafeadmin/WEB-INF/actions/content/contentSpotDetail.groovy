package content;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.product.ProductContentWrapper;
import org.apache.commons.lang.StringEscapeUtils;

userLogin = session.getAttribute("userLogin");
requestParams = UtilHttp.getParameterMap(request);
context.userLogin=userLogin;
content="";
bfContentId="";

bfContentId = requestParams.get("contentId");
if (UtilValidate.isEmpty(bfContentId)) 
{
	bfContentId = parameters.contentId;
}

if(UtilValidate.isNotEmpty(bfContentId))
{
	if (UtilValidate.isEmpty(parameters.productCategoryId))
	{
	    xContentXref =delegator.findOne("XContentXref",UtilMisc.toMap("bfContentId", bfContentId, "productStoreId", productStoreId), false);
		if (UtilValidate.isNotEmpty(xContentXref))
		{
			content = xContentXref.getRelatedOne("Content");
		}
		else
		{
			//for the case of ProductCategoryContent
			contentId = bfContentId;
			content = delegator.findOne("Content",UtilMisc.toMap("contentId", contentId), false);
		}
	}
	else
	{
	    content =delegator.findOne("Content",UtilMisc.toMap("contentId", bfContentId), false);
	}
	if (UtilValidate.isNotEmpty(content))
	{
	    dataResource = content.getRelatedOne("DataResource");
	    if (UtilValidate.isNotEmpty(dataResource))
	    {
	        electronicText = dataResource.getRelatedOne("ElectronicText");
	        if(UtilValidate.isNotEmpty(electronicText))
	        {
	        	context.eText = electronicText.textData;
	        }
	        context.dataResource = dataResource;
	    }
	 }
}
if(UtilValidate.isNotEmpty(parameters.productId))
{
	product = delegator.findOne("Product",["productId":parameters.productId], false);
    productContentWrapper = new ProductContentWrapper(product, request);
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
    }
}
context.content = content;
context.bfContentId = bfContentId;
 