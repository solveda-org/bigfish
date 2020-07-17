<#if adminTopMenuList?has_content>
  <#list adminTopMenuList?sort_by("sequenceNumber") as adminTopMenu>
    <#assign seqNum = adminTopMenu.sequenceNumber />
    <ul class="navigationMenu<#if seqNum != 0> additional</#if>">
      <#list adminTopMenu.child?sort_by("sequenceNumber") as adminTopMenuItem>
        <#if adminTopMenuItem.entity?exists && adminTopMenuItem.action?exists>
          <#if security.hasEntityPermission(adminTopMenuItem.entity?if_exists, adminTopMenuItem.action?if_exists, session)>
            <li class="${adminTopMenuItem.className?if_exists}">
              <a href="<#if adminTopMenuItem.href?has_content><@ofbizUrl>${adminTopMenuItem.href}</@ofbizUrl><#else>#</#if>">${uiLabelMap.get(adminTopMenuItem.text?if_exists)}</a>
              <#if adminTopMenuItem.child?has_content>
                <ul>
                  <#list adminTopMenuItem.child?sort_by("sequenceNumber") as subMenuItem>
                    <li class="${subMenuItem.className?if_exists}"><a href="<#if subMenuItem.href?has_content><@ofbizUrl>${subMenuItem.href!}</@ofbizUrl></#if>">${uiLabelMap.get(subMenuItem.text?if_exists)}</a></li>
                  </#list>
                </ul>
              </#if>
            </li>
            <li class="navSpacer"></li>
          </#if>
          <#else>
            <li class="${adminTopMenuItem.className?if_exists}">
              <a href="<#if adminTopMenuItem.href?has_content><@ofbizUrl>${adminTopMenuItem.href}</@ofbizUrl><#else>#</#if>">${uiLabelMap.get(adminTopMenuItem.text?if_exists)}</a>
              <#if adminTopMenuItem.child?has_content>
                <ul>
                  <#list adminTopMenuItem.child?sort_by("sequenceNumber") as subMenuItem>
                    <li class="${subMenuItem.className?if_exists}"><a href="<#if subMenuItem.href?has_content><@ofbizUrl>${subMenuItem.href!}</@ofbizUrl></#if>">${uiLabelMap.get(subMenuItem.text?if_exists)}</a></li>
                  </#list>
                </ul>
              </#if>
            </li>
          </#if>
        </#list>
      </ul>
    </#list>
  </ul>
</#if>

