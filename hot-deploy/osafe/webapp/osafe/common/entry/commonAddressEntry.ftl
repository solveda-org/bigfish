<script type="text/javascript">
    jQuery(document).ready(function () {
  		//Check if country is available according to DIV sequencing strategy
  		//If NOT we need to add a hidden jquery field to handle processing on the back end and jquery getPostalAdress method (formEntryJS.ftl)
  		//In this case Country is set to system parameter COUNTRY_DEFAULT
	  	if (jQuery('#js_${fieldPurpose?if_exists}_ADDRESS_ENTRY').length)
	  	{
	  		if (jQuery('#js_${fieldPurpose?if_exists}_COUNTRY').length)
	  		{
	  			//Country will be processed as normal
	  		}
	  		else
	  		{
	  			//When only one country is supported (Country Div is hidden or Drop Down is not displayed)
	  			<#assign defaultCountry = Static["com.osafe.util.Util"].getProductStoreParm(request,"COUNTRY_DEFAULT")!"USA"/> 
	      		var defaultCountryValue = "${defaultCountry!"USA"}";
	  			jQuery('<input>').attr({
				    type: 'hidden',
				    id: 'js_${fieldPurpose?if_exists}_COUNTRY',
				    name: '${fieldPurpose?if_exists}_COUNTRY',
				    value: ''+defaultCountryValue+''
				}).appendTo('#js_${fieldPurpose?if_exists}_ADDRESS_ENTRY');
				jQuery('#js_${fieldPurpose?if_exists}_COUNTRY').val(defaultCountryValue);
				updateShippingOption('N');
	  		}
	  	}
	  	
	  	//When country is changed get the list of available state/province geo. 
        if (jQuery('#js_${fieldPurpose?if_exists}_COUNTRY')) 
        {
            if(!jQuery('#${fieldPurpose?if_exists}_STATE_LIST_FIELD').length) 
            {
                getAssociatedStateList('js_${fieldPurpose?if_exists}_COUNTRY', 'js_${fieldPurpose?if_exists}_STATE', 'advice-required-${fieldPurpose?if_exists}_STATE', '${fieldPurpose?if_exists}_STATES');
            }
            getAddressFormat("${fieldPurpose?if_exists}");
            jQuery('#js_${fieldPurpose?if_exists}_COUNTRY').change(function()
            {
                getAssociatedStateList('js_${fieldPurpose?if_exists}_COUNTRY', 'js_${fieldPurpose?if_exists}_STATE', 'advice-required-${fieldPurpose?if_exists}_STATE', '${fieldPurpose?if_exists}_STATES');
                getAddressFormat("${fieldPurpose?if_exists}");
            });
        }
    });
</script>
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign postalAddressContactMechId = postalAddressData.contactMechId!"" />
</#if>
<#if !showAddressEntryBoxHeading?has_content>
    <#assign showAddressEntryBoxHeading = "Y"/>
</#if>
<#if showAddressEntryBoxHeading == "Y">
    <h3>${addressEntryBoxHeading!"Address"}</h3>
</#if>
<#if addressEntryInfo?exists && addressEntryInfo?has_content>
   <p class="instructions">${addressEntryInfo!}</p>
</#if>
<#if addressInstructionsInfo?exists && addressInstructionsInfo?has_content>
     <p class="instructions">${StringUtil.wrapString(addressInstructionsInfo!"")}</p>
</#if>

<input type="hidden" id="js_${fieldPurpose?if_exists}AddressContactMechId" name="${fieldPurpose?if_exists}AddressContactMechId" value="${postalAddressContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}HomePhoneContactMechId" name="${fieldPurpose?if_exists}HomePhoneContactMechId" value="${telecomHomeNoContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}MobilePhoneContactMechId" name="${fieldPurpose?if_exists}MobilePhoneContactMechId" value="${telecomMobileNoContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}_ADDRESS_ALLOW_SOL" name="${fieldPurpose?if_exists}_ADDRESS_ALLOW_SOL" value="N"/>
<input type="hidden" name="${fieldPurpose?if_exists}_USE_SCREEN" value="${fieldPurpose?if_exists}"/>
<#if isShipping?has_content && isShipping == "Y">
    <#if (errorMessage?has_content || errorMessageList?has_content) && requestParameters.isSameAsBilling?has_content && requestParameters.isSameAsBilling == "Y">
        <#assign isSameAsBilling = "Y" />
    <#elseif !errorMessage?has_content && !errorMessageList?has_content>
    	<#assign isSameAsBilling = "Y" />
    </#if>
    <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
      <div class="entry addressCheckbox">
        <label for="isSameAsBilling">${uiLabelMap.SameAsBillingCaption}</label>
        <div class="entryField">
        	<label class="checkboxOptionLabel"><input type="checkbox" class="checkbox" name="isSameAsBilling" id="js_isSameAsBilling" value="Y" <#if isSameAsBilling?has_content && isSameAsBilling == "Y">checked</#if> /></label>
        </div>
      </div>
    </#if>
    <div class="addressSection">
    <div id="js_${fieldPurpose?if_exists}_AddressSection">
</#if>
<div>
    <@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS_ERROR"/>
</div>

<!-- address common field entry -->
${screens.render("component://osafe/widget/EcommerceDivScreens.xml#${addressDivScreenPrefix}DivSequence")}

<!-- This looks odd but must stay.  The two opening DIVS are in the addressInfoSameAsBilling.ftl. -->
<#if isShipping?has_content && isShipping == "Y">
    </div>
    </div>
</#if>