package common;

import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.store.*;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.webapp.stats.VisitHandler;

dispatcher = request.getAttribute("dispatcher");
webSiteId = CatalogWorker.getWebSiteId(request);
catalogName = CatalogWorker.getCatalogName(request);
prodCatalogId = CatalogWorker.getCurrentCatalogId(request);
productStoreId = ProductStoreWorker.getProductStoreId(request);
userLogin = session.getAttribute("userLogin");
productId = parameters.productId;
requestUrl = request.getRequestURL();
requestQuery = request.getQueryString();
requestUrl = requestUrl + '?' + requestQuery;

context.requestUrl=requestUrl;

