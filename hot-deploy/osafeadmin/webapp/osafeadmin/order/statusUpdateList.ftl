<!-- start statusUpdateList.ftl -->
          <thead>
            <tr class="heading">
                <th class="dateCol">${uiLabelMap.DateLabel}</th>
                <th class="dateCol">${uiLabelMap.TimeLabel}</th>
                <th class="nameCol">${uiLabelMap.StatusIdLabel}</th>
                <th class="nameCol">${uiLabelMap.ItemSeqIdLabel}</th>
                <th class="nameCol">${uiLabelMap.ProductNoLabel}</th>
                <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
                <th class="nameCol">${uiLabelMap.ProductNameLabel}</th>
                <th class="nameCol">${uiLabelMap.PaymentPrefIdLabel}</th>
                <th class="nameCol">${uiLabelMap.UserLoginLabel}</th>
            </tr>
          </thead>        
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as orderStatus>
              <#assign hasNext = orderStatus_has_next>            
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">      
                    <#if orderStatus.statusDatetime?exists && orderStatus.statusDatetime?has_content>
                        <#assign orderStatusDate = "${(Static['com.osafe.util.OsafeAdminUtil'].convertDateTimeFormat(orderStatus.statusDatetime, preferredDateTimeFormat).toLowerCase())!}" >
                        <#assign orderStatusDate = orderStatusDate?split(" ") />
                    </#if>  
                    <#assign orderPaymentPreferenceId = orderStatus.getString("orderPaymentPreferenceId")?if_exists />
                    <#assign orderItem = orderStatus.getRelatedOne("OrderItem")?if_exists>
                    <#assign orderPaymentPreference = orderStatus.getRelatedOne("OrderPaymentPreference")?if_exists>
                    <#if orderItem?exists && orderItem?has_content>
                        <#if orderItem.productId?exists && orderItem.productId?has_content>
                            <#assign product = orderItem.getRelatedOne("Product")?if_exists>        
                            <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product,request)>
                            <#assign productName = productContentWrapper.get("PRODUCT_NAME")!product.productName!"">         
                        </#if>
                    </#if>      
                    <td class="idCol <#if !hasNext>lastRow</#if>" >${orderStatusDate[0]!""}</td>
                    <td class="dateCol <#if !hasNext>lastRow</#if>">${orderStatusDate[1]!""}</td>
                    <td class="nameCol<#if !hasNext>lastRow</#if>">${orderStatus.statusId!""}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>"><a href="<@ofbizUrl>orderItemDetail?orderId=${orderItem.orderId!}</@ofbizUrl>">${orderItem.orderItemSeqId!""}</a></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${orderItem.productId!""}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>"><#if orderItem?exists && orderItem?has_content><#if product?exists && product?has_content>${product.internalName!""}</#if></#if></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>"><#if orderItem?exists && orderItem?has_content><#if product?exists && product?has_content>${productName!""}</#if></#if></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>"><a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>">${orderPaymentPreferenceId!""}</a></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${orderStatus.statusUserLogin!""}</td>             
                </tr>       
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>    
            </#list>
        </#if>
<!-- end statusUpdateList.ftl -->