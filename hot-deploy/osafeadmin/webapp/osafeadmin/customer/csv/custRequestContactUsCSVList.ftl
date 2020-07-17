<#if custRequestList?exists && custRequestList?has_content>
  <#list custRequestList as custRequestInfo>
    <#assign custRequest = custRequestInfo.CustRequest!>
    <#assign custReqAttributeList = custRequestInfo.CustRequestAttributeList!>
    <#assign phone = ""/>
    <#assign orderNo = ""/>
    <#assign comment = ""/>
    <#assign exported = ""/>
    <#assign downloadedDate = ""/>
    <#if custReqAttributeList?exists && custReqAttributeList?has_content>
        <#list custReqAttributeList as custReqAttribute>
          <#if custReqAttribute.attrName == 'LAST_NAME'>
            <#assign lname = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
          </#if>
          <#if custReqAttribute.attrName == 'FIRST_NAME'>
            <#assign fname = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
          </#if>
          <#if custReqAttribute.attrName == 'REASON_FOR_CONTACT'>
            <#assign contactUsReason = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign contactUsReason = Static["org.ofbiz.base.util.StringUtil"].wrapString(contactUsReason)/>
          </#if>
          <#if custReqAttribute.attrName == 'EMAIL_ADDRESS'>
            <#assign email = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign email = Static["org.ofbiz.base.util.StringUtil"].wrapString(email)/>
          </#if>
          <#if custReqAttribute.attrName == 'CONTACT_PHONE'>
            <#assign phone = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
          </#if>
          <#if custReqAttribute.attrName == 'ORDER_NUMBER'>
            <#assign orderNo = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign orderNo = Static["org.ofbiz.base.util.StringUtil"].wrapString(orderNo)/>
          </#if>
          <#if custReqAttribute.attrName == 'IS_DOWNLOADED'>
            <#assign exported = custReqAttribute.attrValue!""/>
          </#if>
          <#if custReqAttribute.attrName == 'COMMENT'>
            <#assign comment = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign comment = Static["org.ofbiz.base.util.StringUtil"].replaceString(comment!"","\r"," ")/>
            <#assign comment = Static["org.ofbiz.base.util.StringUtil"].replaceString(comment!"","\n"," ")/>
            <#assign comment = Static["org.ofbiz.base.util.StringUtil"].wrapString(comment)/>
          </#if>
          <#if custReqAttribute.attrName == 'DATETIME_DOWNLOADED'>
            <#assign downloadedDate = custReqAttribute.attrValue!""/>
            <#assign downloadedDate = Static["org.ofbiz.base.util.StringUtil"].wrapString(downloadedDate)/>
          </#if>
        </#list>
    </#if>
        <#if phone?has_content && (phone?length gt 6)>
          <#assign phone = phone?substring(0,3)+"-"+phone?substring(3,6)+"-"+phone?substring(6)/>
        </#if>
        <#assign productStore = delegator.findOne("ProductStore", {"productStoreId" : custRequest.productStoreId}, false)!""/>
        <#if productStore?has_content>
           <#assign storeName=productStore.storeName!""/>
        </#if>
        ${storeName!},${StringUtil.wrapString(custRequest.custRequestId?if_exists)},${custRequest.custRequestDate!},${lname!},${fname!},${contactUsReason!},${email!},${phone!},${orderNo!},${comment!},${exported!},${downloadedDate!}
  </#list>
</#if>
