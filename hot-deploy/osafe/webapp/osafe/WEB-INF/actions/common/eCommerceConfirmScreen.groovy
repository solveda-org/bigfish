package common;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;

companyName = context.get("companyName");
globalContext.confirmMessage   = confirmTextMapValue.replaceAll('companyName', companyName);

