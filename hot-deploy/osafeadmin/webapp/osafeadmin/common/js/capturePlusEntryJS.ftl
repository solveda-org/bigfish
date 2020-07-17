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
            <#if Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(request,"COUNTRY_MULTI")>
                jQuery("#${fieldPurpose!}_country > option").each(function() {
                    if (this.value == countryCode) {
                       jQuery(this).attr('selected', 'selected');
                       jQuery(this).change();
                    }
                });
            <#else>
                jQuery('#${fieldPurpose!}_country').val(countryCode);
            </#if>
            if (company == null || company.length == 0) {
                jQuery('#${fieldPurpose!}_address1').val(line1);
                jQuery('#${fieldPurpose!}_address1').change();
                jQuery('#${fieldPurpose!}_address2').val(line2);
                jQuery('#${fieldPurpose!}_address2').change();
                jQuery('#${fieldPurpose!}_address3').val(line3);
                jQuery('#${fieldPurpose!}_address3').change();
            } else {
                jQuery('#${fieldPurpose!}_address1').val(company);
                jQuery('#${fieldPurpose!}_address1').change();
                jQuery('#${fieldPurpose!}_address2').val(line1);
                jQuery('#${fieldPurpose!}_address2').change();
                jQuery('#${fieldPurpose!}_address3').val(line2+" "+line3);
                jQuery('#${fieldPurpose!}_address3').change();
            }
            jQuery('#${fieldPurpose!}_city').val(city);
            jQuery('#${fieldPurpose!}_city').change();
            jQuery("#${fieldPurpose!}_state > option").each(function() {
                if (this.value == provinceCode) {
                   jQuery(this).attr('selected', 'selected');
                }
            });
            jQuery('#${fieldPurpose!}_state').change();
            jQuery('#${fieldPurpose!}_postalCode').val(postalCode);
            jQuery('#${fieldPurpose!}_postalCode').change();
        }
    </script>
</#if>