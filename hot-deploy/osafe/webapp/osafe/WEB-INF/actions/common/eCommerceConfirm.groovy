package common;

import org.ofbiz.base.util.UtilProperties;

confirmH1MapName = context.get("confirmH1MapName");
confirmH1MapValue = UtilProperties.getMessage("OSafeUiLabels", confirmH1MapName, locale);
confirmH2MapName = context.get("confirmH2MapName");
confirmH2MapValue = UtilProperties.getMessage("OSafeUiLabels", confirmH2MapName, locale);
confirmTextMapName = context.get("confirmTextMapName");
confirmTextMapValue = UtilProperties.getMessage("OSafeUiLabels", confirmTextMapName, locale);

context.pageTitle = confirmH1MapValue;
context.title = pageTitle;
globalContext.confirmHeadingH2 = confirmH2MapValue;
globalContext.confirmMessage = confirmTextMapValue;


