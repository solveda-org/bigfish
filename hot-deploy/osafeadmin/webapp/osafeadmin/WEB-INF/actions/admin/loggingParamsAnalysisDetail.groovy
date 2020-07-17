package admin;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.string.*;

context.printVerboseValue = UtilProperties.getPropertyValue("debug.properties", "print.verbose");
context.printTimingValue = UtilProperties.getPropertyValue("debug.properties", "print.timing");
context.printInfoValue = UtilProperties.getPropertyValue("debug.properties", "print.info");
context.printImportantValue = UtilProperties.getPropertyValue("debug.properties", "print.important");
context.printWarningValue = UtilProperties.getPropertyValue("debug.properties", "print.warning");
context.printErrorValue = UtilProperties.getPropertyValue("debug.properties", "print.error");
context.printFatalValue = UtilProperties.getPropertyValue("debug.properties", "print.fatal");

