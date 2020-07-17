package common;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.StringUtil;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.apache.commons.lang.StringUtils;
import com.osafe.util.Util;
import java.util.Map;

String searchText = com.osafe.util.Util.stripHTML(parameters.searchText);
String searchTextSpellCheck = com.osafe.util.Util.stripHTML(parameters.searchTextSpellCheck);
if (UtilValidate.isNotEmpty(searchText))
{
	SearchResultsPageTitle = UtilProperties.getMessage("OSafeUiLabels", "SearchResultsNotFoundTitle", UtilMisc.toMap("searchText", searchText), locale)
	context.pageTitle = SearchResultsPageTitle;
}




