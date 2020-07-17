package product;

import org.ofbiz.base.util.*;

if (UtilValidate.isEmpty(parameters.variantProductId)) 
{
    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("OSafeAdminUiLabels", "ProductVariantGroupEmptyError", locale));
    return "error";
} else 
{
	passedVariantProductIds = parameters.variantProductId instanceof Collection ? parameters.variantProductId as TreeSet : [parameters.variantProductId] as TreeSet;
	context.passedVariantProductIds = passedVariantProductIds;
    return "success";
}