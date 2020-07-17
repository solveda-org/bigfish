package admin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.*;
import org.ofbiz.base.util.string.*;

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
            fileList = FastList.newInstance();
            files = styleFile.getParentFile().listFiles(new FileUtil.SearchTextFilesFilter("css", FastSet.newInstance(), FastSet.newInstance()));
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory() && !files[i].getName().startsWith(".")) {
                    fileList.add(files[i]);
                }
            }
            context.styleFileList = fileList;
        }
    }
}