<div class="${request.getAttribute("attributeClass")!}">
  <#assign shipGroup= request.getAttribute("shipGroup")!/>
  <#if shipGroup?has_content && ((!isStorePickUp?exists) || isStorePickUp=="N")>
      <#assign contactMech = delegator.findByPrimaryKeyCache("ContactMech", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", shipGroup.contactMechId))?if_exists />
      <#if contactMech?has_content>
          <#assign postalAddress = contactMech.getRelatedOneCache("PostalAddress")?if_exists />
            ${setRequestAttribute("PostalAddress", postalAddress)}
            ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_FULL_ADDRESS")}
            ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
      </#if>
  </#if>
</div>

