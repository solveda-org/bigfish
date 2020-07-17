package common;

import org.ofbiz.base.util.*;
import javolution.util.FastList;

import org.ofbiz.entity.GenericValue;


if (UtilValidate.isNotEmpty(context.contentId) && UtilValidate.isNotEmpty(context.productStoreId)) 
{
    xContentXref = delegator.findByPrimaryKeyCache("XContentXref", [bfContentId : context.contentId, productStoreId : context.productStoreId]);
    if (UtilValidate.isNotEmpty(xContentXref))
    {
        content = xContentXref.getRelatedOneCache("Content");
        context.content = content;
        if ("CTNT_PUBLISHED".equals(content.statusId))
        {
            //set Meta title, Description and Keywords
            if (UtilValidate.isNotEmpty(content.contentName)) 
            {
                context.metaTitle = content.contentName;
                context.metaKeywords = content.contentName;
            }
            if (UtilValidate.isNotEmpty(content.description)) 
            {
                context.metaDescription = content.description;
            }
            //override HTML title, metatags, metakeywords
            contentAttrList = content.getRelatedCache("ContentAttribute");
            for(GenericValue contentAttr : contentAttrList)
            {
                if(contentAttr.attrName == 'HTML_PAGE_TITLE') 
                {
                    if(UtilValidate.isNotEmpty(contentAttr.attrValue)) 
                    {
                        context.metaTitle = contentAttr.attrValue;
                    }
                }
                if(contentAttr.attrName == 'HTML_PAGE_META_DESC') 
                {
                    if(UtilValidate.isNotEmpty(contentAttr.attrValue)) 
                    {
                        context.metaDescription = contentAttr.attrValue;
                    }
                }
                if(contentAttr.attrName == 'HTML_PAGE_META_KEY') 
                {
                    if(UtilValidate.isNotEmpty(contentAttr.attrValue)) 
                    {
                        context.metaKeywords = contentAttr.attrValue;
                    }
                }
            }        	
        }
    }
}

