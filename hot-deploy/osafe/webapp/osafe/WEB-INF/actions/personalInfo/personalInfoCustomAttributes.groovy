package personalInfo;

import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.services.OsafeManageXml;
import javolution.util.FastMap;

customPartyAttributeList = null;
Map<String, Object> svcCtx = FastMap.newInstance();
svcCtx.put("useCache", "true");
partyCustomAttributeListRes = dispatcher.runSync("getPartyCustomAttributeList", svcCtx);
//CustomPartyAttributeServices.getPartyCustomAttributeList();

context.customPartyAttributeList = partyCustomAttributeListRes.get("customPartyAttributeList");

