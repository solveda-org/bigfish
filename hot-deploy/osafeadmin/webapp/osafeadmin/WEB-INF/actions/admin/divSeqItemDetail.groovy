package admin;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import com.osafe.services.OsafeManageXml;
import javolution.util.FastMap;
import java.util.Map;
import org.ofbiz.base.util.FileUtil;

XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
allScreenSearchList =  OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
context.allScreens = allScreenSearchList;

divSeqItemKey = parameters.key;
divSeqItemScreen = parameters.screen;
if (UtilValidate.isNotEmpty(divSeqItemKey) && UtilValidate.isNotEmpty(divSeqItemScreen)) {
    XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
    List<Map<Object, Object>> listMaps = OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
    context.divSeqItemEntry = OsafeManageXml.findByKeyAndScreenFromListMaps(listMaps, "key", divSeqItemKey, "screen", divSeqItemScreen);
    allScreenSearchList =  OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
    context.allScreens = allScreenSearchList;
}
addDivSeqItemKey = parameters.addKey;
addDivSeqItemScreen = parameters.screen;
if (UtilValidate.isNotEmpty(addDivSeqItemKey) && UtilValidate.isNotEmpty(addDivSeqItemScreen)) {
    tmpDir = FileUtil.getFile("runtime/tmp");
    uploadedFile = new File(tmpDir, context.uploadedFileName);
    XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
    allScreenSearchList =  OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
    context.allScreens = allScreenSearchList;
    List<Map<Object, Object>> listMaps = OsafeManageXml.getListMapsFromXmlFile(uploadedFile.getAbsolutePath());
    context.divSeqItemEntry = OsafeManageXml.findByKeyAndScreenFromListMaps(listMaps, "key", addDivSeqItemKey, "screen", addDivSeqItemScreen);
}

