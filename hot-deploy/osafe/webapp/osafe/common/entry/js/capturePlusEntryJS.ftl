<#if requestAttributes.osafeCapturePlus?exists>
    <script type="text/javascript">
        function CapturePlusCallback_${fieldPurpose!}(uid, response) {
            for (var elem = response.length - 1; elem >= 0; elem--) {
                switch (response[elem].FieldName) {
                    case "Line1":
                        jQuery('#${fieldPurpose!}_ADDRESS1').val(response[elem].FormattedValue);
                        break;
                    case "Line2":
                        jQuery('#${fieldPurpose!}_ADDRESS2').val(response[elem].FormattedValue);
                        break;
                    case "Line3":
                        jQuery('#${fieldPurpose!}_ADDRESS3').val(response[elem].FormattedValue);
                        break;
                    case "City":
                        jQuery('#${fieldPurpose!}_CITY').val(response[elem].FormattedValue);
                        break;
                    case "ProvinceCode":
                        jQuery("#${fieldPurpose!}_STATE > option").each(function() {
                            if (this.value == response[elem].FormattedValue) {
                               jQuery(this).attr('selected', 'selected');
                            }
                        });
                        break;
                    case "CountryCode":
                        <#if COUNTRY_MULTI?has_content && Static["com.osafe.util.Util"].isProductStoreParmTrue(COUNTRY_MULTI)>
                            jQuery("#${fieldPurpose!}_COUNTRY > option").each(function() {
                                if (this.value == response[elem].FormattedValue) {
                                   jQuery(this).attr('selected', 'selected');
                                   jQuery(this).change();
                                }
                            });
                        <#else>
                            jQuery('#${fieldPurpose!}_COUNTRY').val(response[elem].FormattedValue);
                        </#if>
                        break;
                    case "PostalCode":
                        jQuery('#${fieldPurpose!}_POSTAL_CODE').val(response[elem].FormattedValue);
                        break;
                }
             }
        }
    </script>
</#if>