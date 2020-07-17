package common;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import java.util.Map;
import javolution.util.FastMap;

confirmH1MapName = context.get("confirmH1MapName");
confirmH1MapValue = UtilProperties.getMessage("OSafeUiLabels", confirmH1MapName, locale);
confirmH2MapName = context.get("confirmH2MapName");
confirmH2MapValue = UtilProperties.getMessage("OSafeUiLabels", confirmH2MapName, locale);
confirmTextMapName = context.get("confirmTextMapName");
confirmTextMapValue = UtilProperties.getMessage("OSafeUiLabels", confirmTextMapName, locale);


//if the message contains variables
Map<String, Object> messageVariables = FastMap.newInstance();
Client = context.get("Client");
if (UtilValidate.isNotEmpty(Client))
{
	messageVariables.put("Client", Client);
}
productDescription = context.get("productDescription");
if (UtilValidate.isNotEmpty(productDescription))
{
	messageVariables.put("productDescription", productDescription);
}

//if messageVariables map is not empty then add variables to the messages
if (UtilValidate.isNotEmpty(messageVariables))
{
	confirmH1MapValue = UtilProperties.getMessage("OSafeUiLabels", confirmH1MapName, messageVariables, locale);
	confirmH2MapValue = UtilProperties.getMessage("OSafeUiLabels", confirmH2MapName, messageVariables, locale);
	confirmTextMapValue = UtilProperties.getMessage("OSafeUiLabels", confirmTextMapName, messageVariables, locale);
}


context.pageTitle = confirmH1MapValue;
context.title = pageTitle;
globalContext.confirmHeadingH2 = confirmH2MapValue;
globalContext.confirmTextMapValue = confirmTextMapValue;


