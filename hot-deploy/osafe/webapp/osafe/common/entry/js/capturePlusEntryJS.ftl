<#if requestAttributes.osafeCapturePlus?exists>
    <script type="text/javascript">
        function CapturePlusCallback_${fieldPurpose!}(uid, response) {

            var company = "";
            var line1 = "";
            var line2 = "";
            var line3 = "";
            var city = "";
            var postalCode = "";
            var provinceCode = "";
            var countryCode = "";
            for (var elem = response.length - 1; elem >= 0; elem--) {
                switch (response[elem].FieldName) {
                    case "Company":
                        company = response[elem].FormattedValue;
                        break;
                    case "Line1":
                        line1 = response[elem].FormattedValue;
                        break;
                    case "Line2":
                        line2 = response[elem].FormattedValue;
                        break;
                    case "Line3":
                        line3 = response[elem].FormattedValue;
                        break;
                    case "City":
                        city = response[elem].FormattedValue;
                        break;
                    case "ProvinceCode":
                        provinceCode = response[elem].FormattedValue;
                        break;
                    case "CountryCode":
                        countryCode = response[elem].FormattedValue;
                        break;
                    case "PostalCode":
                        postalCode = response[elem].FormattedValue;
                        break;
                }
             }
            <#if COUNTRY_MULTI?has_content && Static["com.osafe.util.Util"].isProductStoreParmTrue(COUNTRY_MULTI)>
                jQuery("#${fieldPurpose!}_COUNTRY > option").each(function() {
                    if (this.value == countryCode) {
                       jQuery(this).attr('selected', 'selected');
                       jQuery(this).change();
                    }
                });
            <#else>
                jQuery('#${fieldPurpose!}_COUNTRY').val(countryCode);
            </#if>
            if (company == null || company.length == 0) {
                jQuery('#${fieldPurpose!}_ADDRESS1').val(line1);
                jQuery('#${fieldPurpose!}_ADDRESS1').change();
                jQuery('#${fieldPurpose!}_ADDRESS2').val(line2);
                jQuery('#${fieldPurpose!}_ADDRESS2').change();
                jQuery('#${fieldPurpose!}_ADDRESS3').val(line3);
                jQuery('#${fieldPurpose!}_ADDRESS3').change();
            } else {
                jQuery('#${fieldPurpose!}_ADDRESS1').val(company);
                jQuery('#${fieldPurpose!}_ADDRESS1').change();
                jQuery('#${fieldPurpose!}_ADDRESS2').val(line1);
                jQuery('#${fieldPurpose!}_ADDRESS2').change();
                jQuery('#${fieldPurpose!}_ADDRESS3').val(line2+" "+line3);
                jQuery('#${fieldPurpose!}_ADDRESS3').change();
            }
            jQuery('#${fieldPurpose!}_CITY').val(city);
            jQuery('#${fieldPurpose!}_CITY').change();
            jQuery("#${fieldPurpose!}_STATE > option").each(function() {
                if (this.value == provinceCode) {
                   jQuery(this).attr('selected', 'selected');
                }
            });
            jQuery('#${fieldPurpose!}_STATE').change();
            jQuery('#${fieldPurpose!}_POSTAL_CODE').val(postalCode);
            jQuery('#${fieldPurpose!}_POSTAL_CODE').change();
        }
    </script>
</#if>