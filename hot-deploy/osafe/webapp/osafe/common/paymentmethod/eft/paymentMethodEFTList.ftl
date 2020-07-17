<div class="${request.getAttribute("attributeClass")!}">
	<form method="post" id="paymentMethodForm" name="paymentMethodForm" >
        <input type="hidden" name="paymentMethodId" value="" id="js_paymentMethodId"/>
        <#if savedPaymentMethodEftValueMaps?has_content>
            <div class="boxList paymentMethodList">
                <#assign alreadyShownSavedEftAccountList = Static["javolution.util.FastList"].newInstance()/>
                <#list savedPaymentMethodEftValueMaps as savedPaymentMethodEftValueMap>
                  <#assign savedPaymentMethodEft = savedPaymentMethodEftValueMap.paymentMethod?if_exists/>
                  <#assign savedEftAccount = savedPaymentMethodEftValueMap.eftAccount?if_exists/>
                  <#if (savedEftAccount?has_content) && ("EFT_ACCOUNT" == savedPaymentMethodEft.paymentMethodTypeId)>
                      <#assign bankName=savedEftAccount.bankName?if_exists/>
                      <#assign accountNumber=savedEftAccount.accountNumber?if_exists/>
                      <#if bankName?has_content && (accountNumber?has_content) && (!alreadyShownSavedEftAccountList.contains(accountNumber+bankName))>
                          ${setRequestAttribute("savedEftAccount", savedEftAccount)}
                          ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#paymentMethodEFTItemsDivSequence")}
                          <#assign changed = alreadyShownSavedEftAccountList.add(accountNumber+bankName)/>
                      </#if>
                  </#if>
                </#list>
            </div>
        <#else>
            <div class="displayBox">
              <h3>${uiLabelMap.NoSavedPaymentMethodEftFound}</h3>
            </div>
        </#if>
    </form>
</div>

