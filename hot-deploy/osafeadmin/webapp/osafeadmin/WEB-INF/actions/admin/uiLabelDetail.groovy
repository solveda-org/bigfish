package admin;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import com.osafe.services.OsafeManageXml;
import javolution.util.FastMap;
import java.util.Map;
import org.ofbiz.base.util.FileUtil;

uiLabelKey = parameters.key;
if (UtilValidate.isNotEmpty(uiLabelKey)) {
    context.uiLabelEntry = OsafeManageXml.findByKeyFromXmlFile(XmlFilePath, "key", uiLabelKey);
}
addUiLabelKey = parameters.addKey;
if (UtilValidate.isNotEmpty(addUiLabelKey)) {
    tmpDir = FileUtil.getFile("runtime/tmp");
    uploadedFile = new File(tmpDir, context.uploadedFileName);
    context.uiLabelEntry = OsafeManageXml.findByKeyFromXmlFile(uploadedFile.getAbsolutePath(), "key", addUiLabelKey);
}