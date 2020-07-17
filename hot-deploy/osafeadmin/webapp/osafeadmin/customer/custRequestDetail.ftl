<#if custRequest?has_content>
  <#if custRequest.createdDate?has_content>
    <#assign createdDate =(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(custRequest.createdDate, preferredDateFormat).toLowerCase())!"N/A" />
  </#if>
  
  <#assign custReqAttributeList = delegator.findByAnd("CustRequestAttribute",Static["org.ofbiz.base.util.UtilMisc"].toMap("custRequestId", custRequest.custRequestId))>
  <#assign comment =""/>
  <#assign caption = "_Caption" />
  <#if custReqAttributeList?exists && custReqAttributeList?has_content>
  <#list custReqAttributeList as custReqAttribute>
    <#if custReqAttribute.attrName == 'LAST_NAME'>
      <#assign attrNameLname = custReqAttribute.attrName+caption!""/>
      <#assign lname = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'FIRST_NAME'>
      <#assign attrNameFname = custReqAttribute.attrName+caption!""/>
      <#assign fname = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'ADDRESS1'>
      <#assign attrNameAdd1 = custReqAttribute.attrName+caption!""/>
      <#assign address1 = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'ADDRESS2'>
      <#assign attrNameAdd2 = custReqAttribute.attrName+caption!""/>
      <#assign address2 = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'ADDRESS3'>
      <#assign attrNameAdd3 = custReqAttribute.attrName+caption!""/>
      <#assign address3 = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'CITY'>
      <#assign attrNameCity = custReqAttribute.attrName+caption!""/>
      <#assign city = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'STATE_PROVINCE'>
      <#assign attrNameState = custReqAttribute.attrName+caption!""/>
      <#assign state = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'ZIP_POSTAL_CODE'>
      <#assign attrNameZip = custReqAttribute.attrName+caption!""/>
      <#assign zip = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'COUNTRY'>
      <#assign attrNameCountry = custReqAttribute.attrName+caption!""/>
      <#assign country = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'REASON_FOR_CONTACT'>
      <#assign attrNameReason = custReqAttribute.attrName+caption!""/>
      <#assign contactUsReason = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'EMAIL_ADDRESS'>
      <#assign attrNameEmail = custReqAttribute.attrName+caption!""/>
      <#assign email = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'CONTACT_PHONE'>
      <#assign attrNamePhone = custReqAttribute.attrName+caption!""/>
      <#assign phone = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'ORDER_NUMBER'>
      <#assign attrNameOrderNo = custReqAttribute.attrName+caption!""/>
      <#assign orderNo = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'DATETIME_DOWNLOADED'>
      <#assign attrNameExportedDate = custReqAttribute.attrName+caption!""/>
      <#assign exportedDate = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'IS_DOWNLOADED'>
      <#assign attrNameExported = custReqAttribute.attrName+caption!""/>
      <#assign exported = custReqAttribute.attrValue!""/>
    </#if>
    <#if custReqAttribute.attrName == 'COMMENT'>
      <#assign attrNameComment = custReqAttribute.attrName+caption!""/>
      <#assign comment = custReqAttribute.attrValue!""/>
    </#if>
  </#list>
  </#if>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.IdCaption}</label>
      </div>
      <div class="infoValue">
        ${custRequest.custRequestId!""}
      </div>
    </div>
  </div>
  
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameLname, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">${lname!""}</div>
    </div>
  </div>
       
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameFname, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">${fname!""}</div>
    </div>
  </div>
      
  <#if address1?exists && address1?has_content>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameAdd1, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">${address1!""}</div>
    </div>
  </div>
  </#if>
       
  <#if address2?exists && address2?has_content>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>
            <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameAdd2, locale)/>${labelName!}
          </label>
        </div>
        <div class="infoValue">${address2!""}</div>
      </div>
    </div>
  </#if>
       
  <#if address3?exists && address3?has_content>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>
            <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameAdd3, locale)/>${labelName!}
          </label>
        </div>
        <div class="infoValue">${address3!""}</div>
      </div>
    </div>
  </#if>
       
  <#if city?exists && city?has_content>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameCity, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">${city!""}</div>
    </div>
  </div>
  </#if>
  
  <#if state?exists && state?has_content>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameState, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">${state!""}</div>
    </div>
  </div>
  </#if>
  
  <#if zip?exists && zip?has_content>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameZip, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">${zip!""}</div>
    </div>
  </div>
  </#if>
       
  <#if country?exists && country?has_content>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>
            <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameCountry, locale)/>${labelName!}
          </label>
        </div>
        <div class="infoValue">${country!""}</div>
      </div>
    </div>
  </#if>
  
  <#if contactUsReason?exists && contactUsReason?has_content>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>
            <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameReason, locale)/>${labelName!}
          </label>
        </div>
        <div class="infoValue">${contactUsReason!""}</div>
      </div>
    </div>
  </#if>
       
  <#if email?exists && email?has_content>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameEmail, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">${email!""}</div>
    </div>
  </div>
 </#if>
       
  <#if orderNo?exists && orderNo?has_content>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>
            <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameOrderNo, locale)/>${labelName!}
          </label>
        </div>
        <div class="infoValue">${orderNo!}</div>
      </div>
    </div>
  </#if>
       
  <#if phone?exists && phone?has_content>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>
            <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNamePhone, locale)/>${labelName!}
          </label>
        </div>
        <div class="infoValue">
         <#if (phone?length gt 6)>
           ${phone?substring(0,3)}-${phone?substring(3,6)}-${phone?substring(6)}
         <#else>
           ${phone}
         </#if>
        </div>
      </div>
    </div>
  </#if>
       
  <#if comment?exists && comment?has_content>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>
            <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameComment, locale)/>${labelName!}
          </label>
        </div>
        <div class="infoValue">${comment!""}</div>
      </div>
    </div>
  </#if>
       
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameExported, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">
        <#if exported == 'Y'>
          ${uiLabelMap.ExportStatusInfo}
        <#else>
          ${uiLabelMap.DownloadNewInfo}
        </#if>
      </div>
    </div>
  </div>
  
  <#if exportedDate?exists && exportedDate?has_content>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <#assign labelName = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OsafeAdminUiLabels", attrNameExportedDate, locale)/>${labelName!}
        </label>
      </div>
      <div class="infoValue">
        <#assign expDateTs = Static["java.sql.Timestamp"].valueOf(exportedDate)/>
        ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(expDateTs, preferredDateFormat).toLowerCase())!"N/A"}
      </div>
    </div>
  </div>
  </#if>
  
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.CreatedDateCaption}</label>
      </div>
      <div class="infoValue">${createdDate!""}</div>
    </div>
  </div>
  
<#else>
  ${uiLabelMap.NoDataAvailableInfo}
</#if>
