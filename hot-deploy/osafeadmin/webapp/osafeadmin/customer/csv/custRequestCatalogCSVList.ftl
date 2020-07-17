<#if custRequestList?exists && custRequestList?has_content>
  <#list custRequestList as custRequestInfo>
    <#assign custRequest = custRequestInfo.CustRequest!>
    <#assign custReqAttributeList = custRequestInfo.CustRequestAttributeList!>
    <#assign address2 = ""/>
    <#assign address3 = ""/>
    <#assign state = ""/>
    <#assign phone = ""/>
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
          <#if custReqAttribute.attrName == 'ADDRESS1'>
            <#assign address1 = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign address1 = Static["org.ofbiz.base.util.StringUtil"].wrapString(address1)/>
          </#if>
          <#if custReqAttribute.attrName == 'ADDRESS2'>
            <#assign address2 = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign address2 = Static["org.ofbiz.base.util.StringUtil"].wrapString(address2)/>
          </#if>
          <#if custReqAttribute.attrName == 'ADDRESS3'>
            <#assign address3 = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign address3 = Static["org.ofbiz.base.util.StringUtil"].wrapString(address3)/>
          </#if>
          <#if custReqAttribute.attrName == 'CITY'>
            <#assign city = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign city = Static["org.ofbiz.base.util.StringUtil"].wrapString(city)/>
          </#if>
          <#if custReqAttribute.attrName == 'STATE_PROVINCE'>
            <#assign state = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign state = Static["org.ofbiz.base.util.StringUtil"].wrapString(state)/>
          </#if>
          <#if custReqAttribute.attrName == 'ZIP_POSTAL_CODE'>
            <#assign zip = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign zip = Static["org.ofbiz.base.util.StringUtil"].wrapString(zip)/>
          </#if>
          <#if custReqAttribute.attrName == 'COUNTRY'>
            <#assign country = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
          </#if>
          <#if custReqAttribute.attrName == 'EMAIL_ADDRESS'>
            <#assign email = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
            <#assign email = Static["org.ofbiz.base.util.StringUtil"].wrapString(email)/>
          </#if>
          <#if custReqAttribute.attrName == 'CONTACT_PHONE'>
            <#assign phone = Static["org.ofbiz.base.util.StringUtil"].replaceString(custReqAttribute.attrValue!"",","," ")/>
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
        ${storeName!},${StringUtil.wrapString(custRequest.custRequestId?if_exists)},${custRequest.custRequestDate!},${lname!},${fname!},${address1!},${address2!},${address3!},${city!},${state!},${zip!},${country!},${email!},${phone!},${comment!},${exported!},${downloadedDate!}
  </#list>
</#if>
