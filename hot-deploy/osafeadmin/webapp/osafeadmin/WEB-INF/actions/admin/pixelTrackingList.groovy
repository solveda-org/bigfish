package admin;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

pixelTrackings = delegator.findList("XPixelTracking", EntityCondition.makeCondition([productStoreId : productStoreId]), null, null, null, false);
contentIds = EntityUtil.getFieldListFromEntityList(pixelTrackings, "contentId", true);

contentList = delegator.findList("Content", EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIds), null, null, null, false);
contentMap = [:];
contentTextMap = [:];
contentList.each{content ->
    dataResource = content.getRelatedOne("DataResource");
    if (UtilValidate.isNotEmpty(dataResource))
     {
        electronicText = dataResource.getRelatedOne("ElectronicText");
        if (UtilValidate.isNotEmpty(electronicText))
        {
            contentText = electronicText.textData;
            contentTextMap.put(content.contentId, contentText);
        }
     }
    contentMap.put(content.contentId, content);
}
context.resultList = pixelTrackings;
context.contentMap = contentMap;
context.contentTextMap = contentTextMap;