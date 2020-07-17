    <#assign localPrevButtonVisible = prevButtonVisible!"Y">
    <#assign localPrevButtonUrl = prevButtonUrl!"javascript:submitForm(document.checkoutInfoForm, 'BK', '');">
    <#assign localPrevButtonClass = prevButtonClass!"standardBtn negative">
    <#assign localPrevButtonDescription = prevButtonDescription!uiLabelMap.PreviousBtn>

    <#assign localNextButtonVisible = nextButtonVisible!"Y">
    <#assign localNextButtonUrl = nextButtonUrl!"javascript:submitForm(document.checkoutInfoForm, 'DN', '');">
    <#assign localNextButtonClass = nextButtonClass!"standardBtn positive">
    <#assign localNextButtonDescription = nextButtonDescription!uiLabelMap.ContinueBtn>

    <#assign localSubmitOrderButtonVisible = submitOrderButtonVisible!"N">
    <!-- Start left and right bottom buttons  -->
        <#if localPrevButtonVisible == "Y"><a href="${localPrevButtonUrl}" class="${localPrevButtonClass}">${localPrevButtonDescription}</a></#if>
        <#if localNextButtonVisible == "Y"><a href="${localNextButtonUrl}" class="${localNextButtonClass}">${localNextButtonDescription}</a></#if>
        <#if localSubmitOrderButtonVisible == "Y">
            <form type="post" action="" name="submitOrderForm">
                <input type="button" id="submitOrderBtn" name="submitOrderBtn" value="${uiLabelMap.SubmitOrderBtn}" onclick="submitOrder();" class="${localNextButtonClass}" />
            </form>
        </#if>
