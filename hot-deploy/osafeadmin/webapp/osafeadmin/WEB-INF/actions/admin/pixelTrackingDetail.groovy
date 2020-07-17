package admin;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


pixelId = parameters.pixelId;
productStoreId = parameters.productStoreId;
if (UtilValidate.isNotEmpty(pixelId) && UtilValidate.isNotEmpty(productStoreId)) {
    pixelTrack = delegator.findOne("XPixelTracking",UtilMisc.toMap("pixelId", pixelId, "productStoreId", productStoreId), true);
    if (UtilValidate.isNotEmpty(pixelTrack) && UtilValidate.isNotEmpty(pixelTrack.contentId)) {
        pixelContent = delegator.findOne("Content",UtilMisc.toMap("contentId", pixelTrack.contentId), true);
        dataResource = pixelContent.getRelatedOne("DataResource");
        if (UtilValidate.isNotEmpty(dataResource))
         {
            electronicText = dataResource.getRelatedOne("ElectronicText");
            context.eText = electronicText.textData;
         }
        context.pixelContent = pixelContent;
    }
    context.pixelTrack = pixelTrack;
}
