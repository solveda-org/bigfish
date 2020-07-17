import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.OsafeAdminUtil;

 // set the page parameters
if(UtilValidate.isNotEmpty(pagingListSize) && UtilValidate.isNotEmpty(pagingList))
{
    viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
    context.viewIndex= viewIndex;
    defaultViewSize = OsafeAdminUtil.getProductStoreParm(request, "ADM_DEF_LIST_ROWS");
    if(UtilValidate.isEmpty(defaultViewSize)){
        defaultViewSize = UtilProperties.getPropertyValue("osafeAdmin", "default-view-size");
    }
    viewSize = Integer.valueOf(parameters.viewSize ?: defaultViewSize);
    
    defaultViewSizeMax = OsafeAdminUtil.getProductStoreParm(request, "ADM_WARN_LIST_ROWS");
    if(UtilValidate.isEmpty(defaultViewSizeMax)) 
    {
        defaultViewSizeMax = UtilProperties.getPropertyValue("osafeAdmin", "default-view-size-max");
    }
    viewSizeMax = Integer.valueOf(parameters.viewSizeMax ?: defaultViewSizeMax);
    context.viewSize = viewSize;
    context.showPages = defaultViewSize;
    context.viewSizeMax= viewSizeMax;
    if(viewIndex == 0) {
       viewIndex = viewIndex + 1;
    }
    lowIndex = (viewIndex -1) * viewSize + 1;
    if(lowIndex > pagingListSize) 
    {
       lowIndex = pagingListSize;
    }
    context.lowIndex=lowIndex;
    if(viewIndex == 0) 
    {
       viewIndex = viewIndex + 1;
    }
    highIndex = viewIndex * viewSize;
    if (highIndex > pagingListSize) 
    {
       highIndex = pagingListSize;
    }
    context.highIndex=highIndex;
    subList = pagingList.subList(lowIndex-1, highIndex);
    context.resultList = subList;
    context.confirmAction = StringUtil.wrapString(parameters.thisRequestUri+"?viewSize="+pagingListSize+"&viewIndex=1&showPagesLink=Y");
}