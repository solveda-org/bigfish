import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import java.lang.*;
import org.ofbiz.webapp.taglib.*;
import org.ofbiz.product.store.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.product.store.ProductStoreWorker;

pixelTrackingList = [];
productStore = ProductStoreWorker.getProductStore(request);
if (UtilValidate.isNotEmpty(productStore))
{
  pixelTrackingList = productStore.getRelatedCache("XPixelTracking");
  pixelTrackingList = EntityUtil.filterByDate(pixelTrackingList,true);
  context.pixelTrackingList = pixelTrackingList;
}

