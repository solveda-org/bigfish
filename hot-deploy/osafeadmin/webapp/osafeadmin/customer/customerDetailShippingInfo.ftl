<!-- start customerDetailShippingInfo.ftl -->
<div id="${fieldPurpose?if_exists}_addressEntry">
    <div class="heading">
     <h3>${uiLabelMap.ShippingAddressHeading}</h3>
    </div>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption"><label>&nbsp;</label></div>
            <div class="entry checkbox small">
            	<#if (errorMessage?has_content || errorMessageList?has_content) && parameters.isSameAsBilling?has_content && parameters.isSameAsBilling == "Y">
			        <#assign isSameAsBilling = "Y" />
			    <#elseif !errorMessage?has_content && !errorMessageList?has_content>
			    	<#assign isSameAsBilling = "Y" />
			    </#if>
                <input type="checkbox" class="checkbox" name="isSameAsBilling" id="isSameAsBilling" value="Y"  <#if isSameAsBilling?has_content && isSameAsBilling == "Y">checked</#if>/>${uiLabelMap.ShippingSameAsBillingLabel}
            </div>
        </div>
    </div>
    <div id="${fieldPurpose?if_exists}_addressSection">
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonAddressEntry")}
    </div>
</div>
<!-- end customerDetailShippingInfo.ftl -->