package admin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilProperties;

if (UtilValidate.isNotEmpty(context.productStoreId)) {
    //When Replace with file Exists.
    if(UtilValidate.isNotEmpty(parameters.replaceWithFileName)) {
        tmpDir = FileUtil.getFile("runtime/tmp/upload");
        replaceWithFile = new File(tmpDir, parameters.replaceWithFileName);
        context.textData = FileUtil.readTextFile(replaceWithFile, true);
        context.styleFilePath = parameters.activeStyleFilePath;
        context.detailInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","EditStyleDetailHeading",["styleFileName" : parameters.fileName], locale )
    }
    //Else Display the Selected file
    else{
    	styleFileName = parameters.fileName;
        Map<String, Object> svcCtx = FastMap.newInstance();
        userLogin = session.getAttribute("userLogin");
        svcCtx.put("userLogin", userLogin);
        if (UtilValidate.isNotEmpty(context.visualThemeId)) {
            svcCtx.put("visualThemeId",context.visualThemeId);
        }
        svcCtx.put("productStoreId",context.productStoreId);
        svcRes = dispatcher.runSync("getStyleFilePath", svcCtx);
        parentStyleFilePath = svcRes.get("styleFilePath");
        
	    if(UtilValidate.isNotEmpty(parentStyleFilePath) && UtilValidate.isNotEmpty(styleFileName)) {
	    	parentStyleFile = FileUtil.getFile(parentStyleFilePath);
	        if (parentStyleFile.exists()) {
	            fileList = FastList.newInstance();
	            // get all the CSS files.
	            files = parentStyleFile.getParentFile().listFiles(new FileUtil.SearchTextFilesFilter("css", FastSet.newInstance(), FastSet.newInstance()));
	            for (int i = 0; i < files.length; i++) {
	                if (!files[i].isDirectory() && !files[i].getName().startsWith(".")) {
	                     // find the Required file in the list.
	                     if (files[i].getName().equalsIgnoreCase(styleFileName)){
	                          context.detailInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","EditStyleDetailHeading",["styleFileName" : styleFileName], locale )
                              styleFilePath = files[i].getPath();
                              styleFile = FileUtil.getFile(styleFilePath);
                              context.textData = FileUtil.readTextFile(styleFile, true);
                              context.styleFilePath = styleFilePath;
	                          break;
	                     }
	                }
	            }
	        }
	    }
    }
}

if(UtilValidate.isEmpty(context.textData)) {
    context.execAction = "";
    context.updateAction = "";
    context.replaceWithAction = "";
}


boolean isCompressed = false;
//check if file is compressed
if(UtilValidate.isNotEmpty(context.textData)) {
	//check if there is a double space anywhere inside of the file "  ": If there is then it is NOT compressed
	stringbufferTextData = context.textData;
	int doubleSpace = stringbufferTextData.indexOf("  ");
	if(doubleSpace == -1)
	{
		isCompressed = true;
		context.infoMessage = UtilProperties.getMessage("OSafeAdminUiLabels","CssCompressedInfoMessage",locale);  
		context.updateAction = "";
		context.execAction = "";
	}
}
context.isCompressed = isCompressed;




