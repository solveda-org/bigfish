package common;

import org.ofbiz.base.util.*;
import com.osafe.util.Util;


metaTitle = Util.getProductStoreParm(request,"SEO_HOME_PAGE_TITLE");
metaH1 = Util.getProductStoreParm(request,"SEO_HOME_PAGE_H1");
metaKeywords =Util.getProductStoreParm(request,"SEO_HOME_META_KEY");
metaDescription = Util.getProductStoreParm(request,"SEO_HOME_META_DESC");
canonicalUrl = Util.getProductStoreParm(request,"HTTP_HOST");
if(UtilValidate.isNotEmpty(metaTitle)) 
{
    context.metaTitle = metaTitle;
}
if(UtilValidate.isNotEmpty(metaH1)) 
{
    context.pageTitle = metaH1;
}
if(UtilValidate.isNotEmpty(metaKeywords)) 
{
    context.metaKeywords = metaKeywords;
}
if(UtilValidate.isNotEmpty(metaDescription)) 
{
    context.metaDescription = metaDescription;
}
if(UtilValidate.isNotEmpty(canonicalUrl)) 
{
    context.canonicalUrl = canonicalUrl;
}