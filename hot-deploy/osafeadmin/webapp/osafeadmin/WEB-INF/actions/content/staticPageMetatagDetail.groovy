package content;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;

if (UtilValidate.isNotEmpty(parameters.contentId)) 
{
    contentId = parameters.contentId;
    content = delegator.findOne("Content",["contentId": contentId], false);
    
    //HTML title, metatags, metakeywords
    contentAttrList = delegator.findByAnd("ContentAttribute",["contentId":contentId]);
    for(GenericValue contentAttr : contentAttrList)
    {
        if(contentAttr.attrName == 'HTML_PAGE_TITLE')
        {
            String metaTitle = contentAttr.attrValue;
            if(UtilValidate.isNotEmpty(metaTitle)) 
            {
                context.metaTitle = metaTitle;
            }
        }
        if(contentAttr.attrName == 'HTML_PAGE_META_DESC')
        {
            String metaDescription = contentAttr.attrValue;
            if(UtilValidate.isNotEmpty(metaDescription)) 
            {
                context.metaDesc = metaDescription;
            }
        }
        if(contentAttr.attrName == 'HTML_PAGE_META_KEY')
        {
            String metaKeywords = contentAttr.attrValue;
            if(UtilValidate.isNotEmpty(metaKeywords)) 
            {
                context.metaKeyword = metaKeywords;
            }
        }
    }
    
    dataResource = content.getRelatedOne("DataResource");
    if (UtilValidate.isNotEmpty(dataResource)) 
    {
        electronicText = dataResource.getRelatedOne("ElectronicText");
        if (UtilValidate.isNotEmpty(electronicText)) 
        {
                context.defaultTitle = content.contentName;
                context.defaultMetaDescription = electronicText.textData;
                context.defaultMetaKeywords = electronicText.textData;
        }
    }

}

 