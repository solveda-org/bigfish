package common;

import org.ofbiz.base.util.*;
import javolution.util.FastList;

import org.ofbiz.entity.GenericValue;

if (UtilValidate.isNotEmpty(context.contentId)) 
{
    if (UtilValidate.isNotEmpty(context.content))
    {
        //set Meta title, Description and Keywords
        if (UtilValidate.isNotEmpty(content.contentName)) {
            context.metaTitle = content.contentName;
            context.metaKeywords = content.contentName;
        }
        if (UtilValidate.isNotEmpty(content.description)) {
            context.metaDescription = content.description;
        }
        //override HTML title, metatags, metakeywords
        contentAttrList = delegator.findByAnd("ContentAttribute",["contentId":contentId]);
        for(GenericValue contentAttr : contentAttrList)
        {
            if(contentAttr.attrName == 'HTML_PAGE_TITLE') {
                if(UtilValidate.isNotEmpty(contentAttr.attrValue)) {
                    context.metaTitle = contentAttr.attrValue;
                }
            }
            if(contentAttr.attrName == 'HTML_PAGE_META_DESC') {
                if(UtilValidate.isNotEmpty(contentAttr.attrValue)) {
                    context.metaDescription = contentAttr.attrValue;
                }
            }
            if(contentAttr.attrName == 'HTML_PAGE_META_KEY') {
                if(UtilValidate.isNotEmpty(contentAttr.attrValue)) {
                    context.metaKeywords = contentAttr.attrValue;
                }
            }
        }
    }
}
