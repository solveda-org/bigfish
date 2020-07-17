<#if requestAttributes.osafeCapturePlus?exists>
    <#assign osafeCapturePlus = requestAttributes.osafeCapturePlus/>
    <#if osafeCapturePlus.isNotEmpty()>
        <script type="text/javascript">
            function CapturePlusCallback(uid, response) {
                switch (uid) {
                    <#list osafeCapturePlus.getOsafeCapturePlusUseInfoIter() as osafeCapturePlusUsed>
                        <#assign assignedComponentId = osafeCapturePlusUsed.getAssignedComponentId()/>
                        <#if assignedComponentId?has_content>
                            case "${assignedComponentId}" :
                                CapturePlusCallback_${osafeCapturePlusUsed.getFieldPurpose()}(uid, response);
                                break;
                        </#if>
                    </#list>
                }
            }
        </script>
    </#if>
</#if>
