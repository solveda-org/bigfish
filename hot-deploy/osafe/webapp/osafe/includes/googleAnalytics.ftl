<!-- Start Google Analytics  -->
<#if GA_ACCOUNT_NO?has_content>
  <script type="text/javascript">
      var _gaq = _gaq || [];
      _gaq.push(['_setAccount', '${GA_ACCOUNT_NO!""}']);
      <#if pageTitle?has_content> 
       _gaq.push(['H1', '${pageTitle!""}']);
      </#if>
      _gaq.push(['_trackPageview']);
  <#if orderConfirmed?has_content>
     <#if googleTransMap?has_content>
         _gaq.push(  ['_addTrans',
             '${googleTransMap.orderId!""}',                // order ID - required
             '${googleTransMap.storeName!""}',  // affiliation or store name
             '${googleTransMap.grandTotal}',                // total - required
             '${googleTransMap.taxAmount}',                 // tax
             '${googleTransMap.shippingAmount}',            // shipping
             '${googleTransMap.shipCity!""}',               // city
             '${googleTransMap.shipState!""}',              // state or province
             '${googleTransMap.shipCountry!""}'             // country
         ]);
         // add item might be called for every item in the shopping cart
         // where your ecommerce engine loops through each item in the cart and
         // prints out _addItem for each
         <#if transItemList?has_content>
           <#list transItemList as transItem>
               _gaq.push(  ['_addItem',
                   '${transItem.orderId!""}',                // order ID - required
                   '${transItem.skuCode!""}',                // SKU/code - required
                   '${transItem.itemDescription!""}',        // product name
                   '${transItem.categoryName!""}',                             // category or variation
                   '${transItem.unitPrice}',       // unit price - required
                   '${transItem.quantity}'         // quantity - required
               ]);
           </#list>
         </#if>
         _gaq.push(['_trackTrans']); //submits transaction to the Analytics servers
     </#if> <#-- end googleTransMap -->
  </#if>
     (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
      })();
    </script>
</#if>
<!-- End Google Analytics New -->

<!-- Google Code for Purchase Conversion Page -->
<#if ADWORDS_CONV_ID?has_content>
<script language="JavaScript" type="text/javascript">
	var google_conversion_id = ${ADWORDS_CONV_ID!""};
	var google_conversion_language = ${ADWORDS_CONV_LANG!""};
	var google_conversion_format = ${ADWORDS_CONV_FORMAT!""};
	var google_conversion_color = ${ADWORDS_CONV_COLOR!""};
	var google_conversion_label = ${ADWORDS_CONV_LABEL!""};
	var google_conversion_value = ${ADWORDS_CONV_VALUE!""};
	<#if conversionValue?has_content>
    	var google_conversion_value = ${conversionValue};
    </#if>
	</script>
	<script language="text/javaScript" src="https://www.googleadservices.com/pagead/conversion.js">
	</script>
	<noscript>
    <div style="display:inline">
  	  <img height="1" width="1" border="border-style:none;" src="https://www.googleadservices.com/pagead/conversion/${ADWORDS_CONV_ID}/?value=${conversionValue!ADWORDS_CONV_VALUE}&label=${ADWORDS_CONV_LABEL}&guid=ON&script=0">
	</div>
	</noscript>
</#if>	
<!-- End Google Code for Purchase Conversion Page -->
	

<!-- End Google Code for Purchase Conversion Page -->	