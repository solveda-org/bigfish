package admin;

import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.FileUtil;

import com.osafe.services.OsafeManageXml;

productStore = globalContext.productStore;
productStoreId=globalContext.productStoreId;

if (UtilValidate.isNotEmpty(productStore))
{
	productStoreId = productStore.productStoreId;
    parmKey = parameters.parmKey;
    if (UtilValidate.isNotEmpty(parmKey))
    {
      context.productStoreParm = delegator.findOne("XProductStoreParm",["productStoreId":productStoreId,"parmKey":parmKey], false);
    }
}

addParameterKey = parameters.addKey;
if (UtilValidate.isNotEmpty(addParameterKey)) {
    tmpDir = FileUtil.getFile("runtime/tmp");
    uploadedFile = new File(tmpDir, context.uploadedFileName);
    context.productStoreParm = OsafeManageXml.findByKeyFromXmlFile(uploadedFile.getAbsolutePath(), "parmKey", addParameterKey);
}