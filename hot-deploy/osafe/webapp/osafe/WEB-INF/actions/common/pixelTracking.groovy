import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;

if (UtilValidate.isNotEmpty(context.pageTrackingList))
{   
    pixelTrackingList = EntityUtil.filterByAnd(context.pageTrackingList, UtilMisc.toMap("pixelPagePosition", request.getAttribute("pixelPagePosition")));
    pixelTrackingList = EntityUtil.orderBy(pixelTrackingList,UtilMisc.toList("pixelSequenceNum"));
    context.pixelTrackingList = pixelTrackingList;
}

