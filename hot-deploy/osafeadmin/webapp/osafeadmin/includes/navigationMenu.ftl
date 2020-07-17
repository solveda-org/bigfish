<#if adminTopMenuList?has_content>
    <ul id="navigationMenu">
        <#list adminTopMenuList?sort_by("sequenceNumber") as adminTopMenuItem>
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
</#if>

<script>
var ulTagList = jQuery('.topLevel ul');
if (ulTagList.length > 0) {
    ulTagList.each(function() {
        var aTagList = jQuery(this).find('a');
        var width = 0;
        for (var cnt = 0; cnt < jQuery(aTagList).length; cnt++) {
        var aTagTxtWidth= jQuery(aTagList[cnt]).html().length*10-10;
            if(width < aTagTxtWidth) {
                width  = aTagTxtWidth;
            }
        }
        if (width > jQuery(this).width()){
            jQuery(this).width(width+'px');
        }
    });
}
</script>
