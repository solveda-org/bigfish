package common;

import org.ofbiz.base.util.UtilProperties;

productStore = context.productStore;

context.productStoreName = productStore.storeName;

clientLogoPath = UtilProperties.getPropertyValue("osafe", "client-logo-path");
context.clientLogoPath =  clientLogoPath;