<#if productStorePaymentSetting?has_content>
    <#assign paymentServiceTypeEnums = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "PRDS_PAYSVC"), Static["org.ofbiz.base.util.UtilMisc"].toList("description")) />
    <#assign selectedPaymentServiceTypeEnum = parameters.paymentServiceTypeEnumId!productStorePaymentSetting.paymentServiceTypeEnumId!""/>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.ServiceTypeCaption}</label>
            </div>
            <div class="infoValue">
                <select name="paymentServiceTypeEnumId" id="paymentServiceTypeEnumId" class="extraSmall">
                  <#if paymentServiceTypeEnums?has_content>
                    <#list paymentServiceTypeEnums as paymentServiceTypeEnum>
                      <option value='${paymentServiceTypeEnum.enumId!}' <#if selectedPaymentServiceTypeEnum == paymentServiceTypeEnum.enumId! >selected='selected'</#if>>
                          ${(paymentServiceTypeEnum.get("description",locale))?default(paymentServiceTypeEnum.enumId!)}
                      </option>
                    </#list>
                  </#if>
                </select>
            </div>
        </div>
    </div>

    <#assign paymentMethodTypes = delegator.findByAnd("PaymentMethodType", Static["org.ofbiz.base.util.UtilMisc"].toMap(), Static["org.ofbiz.base.util.UtilMisc"].toList("description")) />
    <#assign selectedPaymentMethodType = parameters.paymentMethodTypeId!productStorePaymentSetting.paymentMethodTypeId!""/>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.PaymentMethodTypeCaption}</label>
            </div>
            <div class="infoValue">
                <select name="paymentMethodTypeId" id="paymentMethodTypeId" class="extraSmall">
                    <#if paymentMethodTypes?has_content && displayPaymentMethodTypes?has_content>
                        <#assign displayPaymentMethodTypeList = Static["org.ofbiz.base.util.StringUtil"].split(displayPaymentMethodTypes,"|")/>
                        <#list paymentMethodTypes as paymentMethodType>
                            <#list displayPaymentMethodTypeList as displayPaymentMethodType>
                                <#if paymentMethodType.paymentMethodTypeId.equals(displayPaymentMethodType)>
                                    <option value='${paymentMethodType.paymentMethodTypeId!}' <#if selectedPaymentMethodType == paymentMethodType.paymentMethodTypeId! >selected=selected</#if>>
                                        ${(paymentMethodType.get("description",locale))?default(paymentMethodType.paymentMethodTypeId!)}
                                    </option>
                                </#if>
                            </#list>
                        </#list>
                    </#if>
                </select>
            </div>
        </div>
    </div>

    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.ServiceNameCaption}</label>
            </div>
            <div class="infoValue">
                <input class="large" type="text" id="paymentService" name="paymentService" maxlength="255" value="${parameters.paymentService!productStorePaymentSetting.paymentService!""}"/>
            </div>
        </div>
    </div>

    <#assign paymentCustomMethods = delegator.findByAnd("CustomMethod", Static["org.ofbiz.base.util.UtilMisc"].toMap(), Static["org.ofbiz.base.util.UtilMisc"].toList("description")) />
    <#assign selectedPaymentCustomMethod = parameters.paymentCustomMethodId!productStorePaymentSetting.paymentCustomMethodId!""/>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.CustomMethodCaption}</label>
            </div>
            <div class="infoValue">
                <select name="paymentCustomMethodId" id="paymentCustomMethodId" class="extraSmall">
                  <#if paymentCustomMethods?has_content>
                    <#list paymentCustomMethods as paymentCustomMethod>
                      <option value='${paymentCustomMethod.customMethodId!}' <#if selectedPaymentCustomMethod == paymentCustomMethod.customMethodId! >selected=selected</#if>>
                          ${paymentCustomMethod.description?if_exists} (${paymentCustomMethod.customMethodName?if_exists})
                      </option>
                    </#list>
                  </#if>
                </select>
            </div>
        </div>
    </div>

    <#assign paymentGatewayConfigs = delegator.findByAnd("PaymentGatewayConfig", Static["org.ofbiz.base.util.UtilMisc"].toMap(), Static["org.ofbiz.base.util.UtilMisc"].toList("description")) />
    <#assign selectedPaymentGatewayConfig = parameters.paymentGatewayConfigId!productStorePaymentSetting.paymentGatewayConfigId!""/>
    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.PaymentGatwayConfigIdCaption}</label>
            </div>
            <div class="infoValue">
                <select name="paymentGatewayConfigId" id="paymentGatewayConfigId" class="extraSmall">
                  <#if paymentGatewayConfigs?has_content>
                    <#list paymentGatewayConfigs as paymentGatewayConfig>
                      <option value='${paymentGatewayConfig.paymentGatewayConfigId!}' <#if selectedPaymentGatewayConfig == paymentGatewayConfig.paymentGatewayConfigId! >selected=selected</#if>>
                          ${(paymentGatewayConfig.get("description",locale))?default(paymentGatewayConfig.paymentGatewayConfigId!)}
                      </option>
                    </#list>
                  </#if>
                </select>
            </div>
        </div>
    </div>

    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.PaymentPropertiesCaption}</label>
            </div>
            <div class="infoValue">
                <input class="large" type="text" id="paymentPropertiesPath" name="paymentPropertiesPath" maxlength="255" value="${parameters.paymentPropertiesPath!productStorePaymentSetting.paymentPropertiesPath!""}"/>
            </div>
        </div>
    </div>

    <div class="infoRow row">
        <div class="infoEntry long">
            <div class="infoCaption">
                <label>${uiLabelMap.ApplyToAllCaption}</label>
            </div>
            <div class="infoValue">
                <div class="entry checkbox short">
                    <input class="checkBoxEntry" type="radio" id="applyToAllProducts" name="applyToAllProducts" value="Y" <#if ((parameters.applyToAllProducts?exists && parameters.applyToAllProducts?string == "Y") || (productStorePaymentSetting.applyToAllProducts?exists && productStorePaymentSetting.applyToAllProducts?string == "Y"))>checked="checked"</#if>/>${uiLabelMap.CommonY}
                    <input class="checkBoxEntry" type="radio" id="applyToAllProducts" name="applyToAllProducts" value="N" <#if ((parameters.applyToAllProducts?exists && parameters.applyToAllProducts?string == "N") || (productStorePaymentSetting.applyToAllProducts?exists && productStorePaymentSetting.applyToAllProducts?string == "N"))>checked="checked"</#if>/>${uiLabelMap.CommonN}
                </div>
            </div>
        </div>
    </div>
<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>