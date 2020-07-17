package admin;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilProperties;

if (UtilValidate.isNotEmpty(context.productStoreId)) {
    Map<String, Object> svcCtx = FastMap.newInstance();
    userLogin = session.getAttribute("userLogin");
    svcCtx.put("userLogin", userLogin);
    if (UtilValidate.isNotEmpty(context.visualThemeId)) {
        svcCtx.put("visualThemeId",context.visualThemeId);
    }
    svcCtx.put("productStoreId",context.productStoreId);
    svcRes = dispatcher.runSync("getStyleFilePath", svcCtx);
    styleFilePath = svcRes.get("styleFilePath");
    if(UtilValidate.isNotEmpty(styleFilePath)) {
    	styleFile = FileUtil.getFile(styleFilePath);
        if (styleFile.exists()) {
            context.styleFileName = styleFile.getName();
            context.detailInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","EditStyleDetailHeading",["styleFileName" : styleFile.getName()], locale )
            if(UtilValidate.isNotEmpty(context.replaceWithFileName)) {
                tmpDir = FileUtil.getFile("runtime/tmp");
                replaceWithFile = new File(tmpDir, context.replaceWithFileName);
                context.textData = FileUtil.readTextFile(replaceWithFile, true);
            } else {
                context.textData = FileUtil.readTextFile(styleFile, true);
            }
        }
    }
}
if(UtilValidate.isEmpty(context.textData)) {
    context.execAction = "";
    context.updateAction = "";
    context.replaceWithAction = "";
}