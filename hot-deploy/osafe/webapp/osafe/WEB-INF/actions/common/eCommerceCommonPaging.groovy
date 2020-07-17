package common;

import java.util.List;
import java.util.Map;
import com.osafe.util.Util;			
import org.ofbiz.base.util.*;
			
if (UtilValidate.isNotEmpty(pagingList) && UtilValidate.isNotEmpty(pagingListSize))
{
    viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
    context.viewIndex= viewIndex;
    defaultViewSize = Util.getProductStoreParm(request, "PLP_NUM_ITEMS_PER_PAGE");
    if(UtilValidate.isEmpty(defaultViewSize)||defaultViewSize.equals("0"))
    {
        defaultViewSize=20;
    }	
    try
    {
      viewSize = Integer.valueOf(parameters.viewSize ?: defaultViewSize);
    }
    catch(NumberFormatException e)
    {
      viewSize =20;
    }
    int noOfPages=pagingListSize/viewSize;
    pages=pagingListSize%viewSize;	
    if(pages==0)
    {
      lastIndex=noOfPages;
    }
    else
    {
	  lastIndex=noOfPages+1;
    }
    context.lastIndex=lastIndex;
    context.viewSize = viewSize;	    
    context.showPages = defaultViewSize;
    if(viewIndex == 0) 
    {
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
    context.eCommerceResultList = subList;			   
}