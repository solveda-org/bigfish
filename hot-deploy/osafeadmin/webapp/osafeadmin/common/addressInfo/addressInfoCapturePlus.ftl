<div class="addressInfoCustomObject">
    <#if requestAttributes.osafeCapturePlus?exists>
        <#assign osafeCapturePlus = requestAttributes.osafeCapturePlus/>
        <#if osafeCapturePlus.isNotEmpty()>
            <#assign assignedComponentId = osafeCapturePlus.getComponentId(fieldPurpose!)!""/>
            <#if assignedComponentId?has_content>
                <#list osafeCapturePlus.getOsafeCapturePlusUseInfoIter() as osafeCapturePlusUsed>
                    <#if (osafeCapturePlusUsed.getFieldPurpose() == fieldPurpose!)>
                        <#assign pcaCssUrl = osafeCapturePlus.getPcaCssUrl() />
                        <#assign pcaJsUrl = osafeCapturePlus.getPcaJsUrl() />
                        <#assign pcaApiUsedKey = osafeCapturePlusUsed.getPcaApiKey()/>
                        <#assign pcaApiUsedAppNo = osafeCapturePlusUsed.getPcaApiAppNo()/>
                        <#assign pcaCssUrl = pcaCssUrl+ "?" + "key" + "=" + pcaApiUsedKey />
                        <#assign pcaJsUrl = pcaJsUrl+ "?" + "key" + "=" + pcaApiUsedKey+ "&" + "app" + "=" + pcaApiUsedAppNo />
                        <link rel="stylesheet" type="text/css" href="${pcaCssUrl}" />
                        <script type="text/javascript" src="${pcaJsUrl}"></script>
                    </#if>
                </#list>
                <div class="entry capturePlus">
                    <div id="${assignedComponentId!}"></div>
                </div>
            </#if>
            ${setRequestAttribute("osafeCapturePlus",osafeCapturePlus)}
        </#if>
    </#if>
</div>