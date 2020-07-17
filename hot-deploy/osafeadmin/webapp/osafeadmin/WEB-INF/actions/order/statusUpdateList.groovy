package order;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

// Paging variables
viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
viewSize = Integer.valueOf(parameters.viewSize ?: UtilProperties.getPropertyValue("osafeAdmin", "default-view-size"));
context.viewIndex = viewIndex;
context.viewSize = viewSize;
Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);

orderId = StringUtils.trimToEmpty(parameters.orderId);
contentList = FastList.newInstance();

if (UtilValidate.isNotEmpty(orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	context.orderHeader = orderHeader;
	
	orderProductStore = orderHeader.getRelatedOne("ProductStore");
	if (UtilValidate.isNotEmpty(orderProductStore.storeName))
	{
		productStoreName = orderProductStore.storeName;
	}
	else
	{
		productStoreName = orderHeader.productStoreId;
	}
	context.productStoreName = productStoreName;
}

if (UtilValidate.isNotEmpty(orderHeader))
{
	contentList = orderHeader.getRelatedOrderBy("OrderStatus", ["orderStatusId"]);
	
	notes = orderHeader.getRelated("OrderHeaderNoteView");
	context.notesCount = notes.size();
}
context.resultList = contentList;