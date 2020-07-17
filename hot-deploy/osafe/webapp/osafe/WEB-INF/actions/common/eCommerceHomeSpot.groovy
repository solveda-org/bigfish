package content;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.transaction.*

contentTypeId=context.contentTypeId;
productStore = ProductStoreWorker.getProductStore(request);
if(UtilValidate.isNotEmpty(contentTypeId) && UtilValidate.isNotEmpty(productStore))
{
    xContentXrefList = productStore.getRelatedCache("XContentXref");
    xContentXrefList = EntityUtil.filterByAnd(xContentXrefList, UtilMisc.toMap("contentTypeId" , contentTypeId));
    xContentXrefList = EntityUtil.orderBy(xContentXrefList,UtilMisc.toList("bfContentId"));
    context.spotsList = xContentXrefList;
}

