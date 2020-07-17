<script language="JavaScript" type="text/javascript">
 <#if currentProduct?exists>
  <#assign PDP_QTY_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_DEFAULT")!/>
  <#if !PDP_QTY_DEFAULT?has_content || !(Static["com.osafe.util.Util"].isNumber(PDP_QTY_DEFAULT))>
  	<#assign PDP_QTY_DEFAULT = "1"/>
  </#if>
  
  <#assign personalizationDefaultMap = Static["org.ofbiz.base.util.UtilProperties"].getResourceBundleMap("parameters_personalization", locale)>

  jQuery(function() 
  {
		jQuery(".js_pdpTabs").tabs();
  });
  
    function sortReviews(screen) 
    {
  	  var sortOption = jQuery('#js_'+screen+'reviewSort').val();
  	  jQuery('#js_'+screen+'SortReviewBy').val(sortOption);
      var reviewParams = jQuery('.js_'+screen+'ReviewList').find('input.reviewParam').serialize();
      jQuery.get('<@ofbizUrl>sortReview?'+reviewParams+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) 
      {
          var sortedList = jQuery(data).find('#js_'+screen+'reviewList');
          jQuery('#js_'+screen+'reviewList').replaceWith(sortedList);
          var sortPagingList = jQuery(data).find('#js_'+screen+'reviewPagingList');
          jQuery('#js_'+screen+'reviewPagingList').replaceWith(sortPagingList);
      });
    }

    var detailImageUrl = null;
    function setAddProductId(name) 
    {
        document.addform.add_product_id.value = name;
        if (document.addform.quantity == null) return;
    }
    function setProductStock(name) 
    {
        var elm = document.getElementById("js_addToCart");
        var elmWishlist = document.getElementById("js_addToWishlist");
        if(VARSTOCK[name]=="outOfStock")
        {
            elm.setAttribute("onClick","javascript:void(0)");
            jQuery('#js_addToCart').addClass("inactiveAddToCart");
            jQuery('#js_quantity').attr("disabled", "disabled");
            
            if (elmWishlist !=null )
            {
	            elmWishlist.setAttribute("onClick","javascript:void(0)");
	            jQuery('#js_addToWishlist').addClass("inactiveAddToWishlist");
            }
            
            if (jQuery('.js_pdpOutOfStockContent').length) 
            {
                jQuery('.js_pdpOutOfStockContent').show();
            }
        }
        else 
        {
            jQuery('#js_addToCart').removeClass("inactiveAddToCart");
            elm.setAttribute("onClick","javascript:addItemToCart()");
            jQuery('#js_quantity').removeAttr("disabled");
            if (jQuery('.js_pdpOutOfStockContent').length) 
            {
                jQuery('.js_pdpOutOfStockContent').hide();
            }
            
	        if (elmWishlist !=null )
	        {
	            elmWishlist.setAttribute("onClick","javascript:addItemToWishlist()");
	            jQuery('#js_addToWishlist').removeClass("inactiveAddToWishlist");
	        }
        }
        checkProductInStore(VARINSTORE[name]);
    }

    function checkProductInStore(value) 
    {
        if(value !=null && value=="Y")
        {
            if (jQuery('.js_pdpInStoreOnlyContent').length) 
            {
                jQuery('.js_pdpInStoreOnlyContent').show();
            }
            if (jQuery('#js_quantity_div').length) 
            {
                jQuery('#js_quantity_div').hide();
            }
            if (jQuery('#js_addToCart_div').length) 
            {
                jQuery('#js_addToCart_div').hide();
            }
            if (jQuery('#js_addToWishlist_div').length) 
            {
                jQuery('#js_addToWishlist_div').hide();
            }
        }
        else
        {
            if (jQuery('.js_pdpInStoreOnlyContent').length) 
            {
                jQuery('.js_pdpInStoreOnlyContent').hide();
            }
            if (jQuery('#js_quantity_div').length) 
            {
                jQuery('#js_quantity_div').show();
            }
            if (jQuery('#js_addToCart_div').length) 
            {
                jQuery('#js_addToCart_div').show();
            }
            if (jQuery('#js_addToWishlist_div').length) 
            {
                jQuery('#js_addToWishlist_div').show();
            }
        }
    }

    function addItemToCart() 
    {
       if(isItemSelectedPdp())
       {
       	    <#-- get ProductName, Quantity, and Product Id -->
       		var quantity = Number(0);
       		if(jQuery('form[name=addform] input[name="quantity"]').length)
           	{
           		quantity = Number(jQuery('form[name=addform] input[name="quantity"]').val());
           	}
           	else
           	{
           		quantity = Number(1);
           	}
           	var add_product_id = jQuery('form[name=addform] input[name="add_product_id"]').val();
           	var productName = "${wrappedPdpProductName!}";
           	<#-- check if qty is whole number -->
           	if(isQtyWhole(quantity,productName))
           	{
           		if(!(isQtyZero(quantity,productName,add_product_id)))
           		{
	           		<#-- check how many already in cart and add to qty -->
	           		quantity = Number(quantity) + Number(getQtyInCart(add_product_id));
	           		<#-- get lower and upper limits for quantity -->
	                <#-- validate qty limits -->
	                if(validateQtyMinMax(add_product_id,productName,quantity))
	                {
		               <#-- add to cart action -->
		    		   recurrenceIsChecked = jQuery('#js_pdpPriceRecurrenceCB').is(":checked");
			    	   if(recurrenceIsChecked)
			    	   {
		                  document.addform.action="<@ofbizUrl>${addToCartRecurrenceAction!""}</@ofbizUrl>";
		               }
		               else
		               {
		                  document.addform.action="<@ofbizUrl>${addToCartAction!""}</@ofbizUrl>";
		               }
		               document.addform.submit();
	                }
                }
           	}
       }
    }
    
    
    function addItemToWishlist() 
    {
       if(isItemSelectedPdp())
       {
       	    <#-- get ProductName, Quantity, and Product Id -->
       		var quantity = Number(0);
       		if(jQuery('form[name=addform] input[name="quantity"]').length)
           	{
           		quantity = Number(jQuery('form[name=addform] input[name="quantity"]').val());
           	}
           	else
           	{
           		quantity = Number(1);
           	}
           	var add_product_id = jQuery('form[name=addform] input[name="add_product_id"]').val();
           	var productName = "${wrappedPdpProductName!}";
           	<#-- check if qty is whole number -->
           	if(isQtyWhole(quantity,productName))
           	{
           	   if(!(isQtyZero(quantity,productName,add_product_id)))
           	   {
               		<#-- add to wish list action -->
               		document.addform.action="<@ofbizUrl>${addToWishListAction!""}</@ofbizUrl>";
               		document.addform.submit();
               }
           	}
       }
    }
    
    function addMultiItemsToCart(pdpSelectMultiVariant) 
    {
    	var addItemsToCart = "true";
    	var count = 0;
    	jQuery('.js_add_multi_product_quantity').each(function () 
    	{
    		if(pdpSelectMultiVariant == "CHECKBOX")
    		{
    			variantIsChecked = jQuery('#js_add_multi_product_id_'+count).is(":checked");
    		}
    		else
    		{
    			//does not apply
    			variantIsChecked = true;
    		}
    		if(variantIsChecked)
    		{
    			var quantity = jQuery(this).val();
    			var add_productId = jQuery('#js_add_multi_product_id_'+count).val();
    			var productName = "${wrappedPdpProductName!}";
    			<#-- check if qty is whole number -->
    			if(quantity != "") 
				{
					if(isQtyWhole(quantity,productName))
					{
						<#-- check how many already in cart and add to qty -->
		           		quantity = Number(quantity) + Number(getQtyInCart(add_productId));
		                <#-- validate qty limits -->
		                if(!(validateQtyMinMax(add_productId,productName,quantity)))
		                {
		                	addItemsToCart = "false";
		                }
					}
					else
					{
						addItemsToCart = "false";
					}
				}
				else
				{
					addItemsToCart = "false";
				}
    		}
    		count = count + 1;
    	}); 
		if(addItemsToCart == "true")
		{
	    	// add to cart action
	        document.addform.action="<@ofbizUrl>${addMultiItemsToCartAction!""}</@ofbizUrl>";
	        document.addform.submit();
        }
    }
    
    
    function addMultiItemsToWishlist(pdpSelectMultiVariant) 
    {
    	var addItemsToCart = "true";
    	var count = 0;
    	jQuery('.js_add_multi_product_quantity').each(function () 
    	{
    		if(pdpSelectMultiVariant == "CHECKBOX")
    		{
    			variantIsChecked = jQuery('#js_add_multi_product_id_'+count).is(":checked");
    		}
    		else
    		{
    			//does not apply
    			variantIsChecked = true;
    		}
    		if(variantIsChecked)
    		{
    			var quantity = jQuery(this).val();
    			var add_productId = jQuery('#js_add_multi_product_id_'+count).val();
    			var productName = "${wrappedPdpProductName!}";
    			<#-- check if qty is whole number -->
    			if(quantity != "") 
				{
					if(!(isQtyWhole(quantity,productName)))
					{
						addItemsToCart = "false";
					}
				}
    		}
    		count = count + 1;
    	}); 
		if(addItemsToCart == "true")
		{
	    	// add to wishlist action
	        document.addform.action="<@ofbizUrl>${addMultiItemsToWishlistAction!""}</@ofbizUrl>";
	        document.addform.submit();
        }
    }
    
    var isWhole_re = /^\s*\d+\s*$/;
    function isWhole (s) 
    {
        return String(s).search (isWhole_re) != -1
    } 	
 
    function replaceDetailImage(largeImageUrl, detailImageUrl) 
    {
        if (!jQuery('#mainImages').length) 
        {
            var mainImages = jQuery('#js_mainImageDiv').clone();
            jQuery(mainImages).find('img.js_productLargeImage').attr('id', 'js_mainImage');
            jQuery('#js_productDetailsImageContainer').html(mainImages.html());
            jQuery('#js_seeMainImage a').attr("href", "javascript:replaceDetailImage('"+largeImageUrl+"', '"+detailImageUrl+"');");
        }
        <#assign activeZoom = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PDP_IMG_ZOOM_ACTIVE_FLAG")/>
        <#if activeZoom>
            var mainImages = jQuery('#js_mainImageDiv').clone();
            jQuery(mainImages).find('img.js_productLargeImage').attr('id', 'js_mainImage');
            jQuery(mainImages).find('img.js_productLargeImage').attr('src', largeImageUrl+ "?" + new Date().getTime());
            jQuery(mainImages).find('a').attr('class', 'innerZoom');
            if(detailImageUrl != "") 
            {
              jQuery(mainImages).find('a').attr('href', detailImageUrl);
            } 
            else 
            {
                jQuery(mainImages).find('a').attr('href', 'javaScript:void(0);');
            }
            jQuery('#js_productDetailsImageContainer').html(mainImages.html());
            activateZoom(detailImageUrl);
            
        </#if>
        if (document.images['js_mainImage'] != null) 
        {
            var detailimagePath;
            document.getElementById("js_mainImage").setAttribute("src",largeImageUrl);
            if(document.getElementById('js_largeImage')) 
            {
                setDetailImage(detailImageUrl);
            }
            <#assign IMG_SIZE_PDP_REG_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_REG_W")!""/>
            document.getElementById("js_mainImage").setAttribute("class","js_productLargeImage<#if !IMG_SIZE_PDP_REG_W?has_content> productLargeImageDefaultWidth</#if>");
            detailimagePath = "javascript:displayDialogBox('largeImage_')";
            if (jQuery('#js_mainImageLink').length) 
            {
                jQuery('#js_mainImageLink').attr("href",detailimagePath);
            }
        }
    }

    function setDetailImage(detailImageUrl) 
    {
        if (typeof detailImageUrl == "undefined" || detailImageUrl == "NULL" || detailImageUrl == "") 
        {
            return;
        }
        var image = new Image();
        image.src = detailImageUrl+ "?" + new Date().getTime();
        image.onerror = function () {
          jQuery('#js_largeImage').attr('src', '/osafe_theme/images/user_content/images/NotFoundImagePDPDetail.jpg');
      };
      image.onload = function () 
      {
          jQuery('#js_largeImage').attr('src', detailImageUrl);
      };
    }
    
    function findIndex(name) 
    {
        OPT = eval("getFormOption()");
        for (i = 0; i < OPT.length; i++) 
        {
            if (OPT[i] == name) 
            {
                return i;
            }
        }
        return -1;
    }
    
    var userModifiedQty = "N";
    jQuery(document).ready(function()
    {
    	jQuery('#js_quantity').keyup(function(){
		  userModifiedQty = "Y";
		}); 
    });
	
    var firstNoSelection = "false";
    function getList(name, index, src) 
    {
    	var noSelection = "false";
        currentFeatureIndex = findIndex(name);
        if(firstNoSelection == "true")
        {
        	noSelection ="true";
        }
        if(index != -1)
        {
        	var liElm = jQuery('#Li'+name+" li").get(index);
		}
		else
		{
			var liElm = jQuery('#Li'+name+" li").get(0);
			noSelection ="true";
		}
        jQuery(liElm).siblings("li").removeClass("selected");
        jQuery(liElm).addClass("selected");
        
        <#-- set the drop down index for swatch selection -->
        document.forms["addform"].elements[name].selectedIndex = (index*1)+1;
        
        OPT = eval("getFormOption()");
        if (currentFeatureIndex < (OPT.length-1)) 
        {
		    
            <#-- eval the next list if there are more -->
            var selectedValue = document.forms["addform"].elements[name].options[(index*1)+1].value;
            var selectedText = document.forms["addform"].elements[name].options[(index*1)+1].text;
            
            var mapKey = name+'_'+selectedText;
            var VARMAP = eval("getFormOptionVarMap()");
            var featureGroupDesc = VARGROUPMAP[VARMAP[mapKey]];

            jQuery('.js_pdpRecentlyViewed .'+featureGroupDesc).click();
            jQuery('.js_pdpComplement .'+featureGroupDesc).click();
            jQuery('.js_pdpAccessory .'+featureGroupDesc).click();
            
            jQuery('.js_pdpRecentlyViewed .'+selectedText).click();
            jQuery('.js_pdpComplement .'+selectedText).click();
            jQuery('.js_pdpAccessory .'+selectedText).click();
            
            var detailImgUrl = '';
            if(VARMAP[mapKey]) 
            {
                if(jQuery('#js_mainImage_'+VARMAP[mapKey]).length) 
                { 
                    var variantMainImages = jQuery('#js_mainImage_'+VARMAP[mapKey]).clone();
                    //jQuery(variantMainImages).find('img').each(function(){jQuery(this).attr('src', jQuery(this).attr('title')+ "?" + new Date().getTime());})
                    jQuery(variantMainImages).find('a').attr('class', 'innerZoom');
                    detailImgUrl = jQuery(variantMainImages).find('a').attr('href');
                    jQuery('#js_productDetailsImageContainer').html(variantMainImages.html());
                }
                    var variantAltImages = jQuery('#js_altImage_'+VARMAP[mapKey]).clone();
                    //jQuery(variantAltImages).find('img').each(function(){ jQuery(this).attr('src', jQuery(this).attr('title')+ "?" + new Date().getTime());})
                    jQuery('#js_eCommerceProductAddImage').html(variantAltImages.html());

                    var variantSeeMainImages = jQuery('#js_seeMainImage_'+VARMAP[mapKey]).clone();
                    jQuery('#js_seeMainImage').html(variantSeeMainImages.html());
                    
                    var variantProductVideo = jQuery('#js_productVideo_'+VARMAP[mapKey]).html();
                    jQuery('#js_productVideo').html(variantProductVideo);
                    
                    var variantProductVideoLink = jQuery('#js_productVideoLink_'+VARMAP[mapKey]).html();
                    jQuery('#js_productVideoLink').html(variantProductVideoLink);
                    
                    var variantProductVideo360 = jQuery('#js_productVideo360_'+VARMAP[mapKey]).html();
                    jQuery('#js_productVideo360').html(variantProductVideo360);
                    
                    var variantProductVideo360Link = jQuery('#js_productVideo360Link_'+VARMAP[mapKey]).html();
                    jQuery('#js_productVideo360Link').html(variantProductVideo360Link);
                    
                    var variantPdpPriceSavingMoney = jQuery('#js_pdpPriceSavingMoney_'+VARMAP[mapKey]).html();
                    jQuery('#js_pdpPriceSavingMoney').html(variantPdpPriceSavingMoney);
                    
                    var variantPdpPriceSavingPercent = jQuery('#js_pdpPriceSavingPercent_'+VARMAP[mapKey]).html();
                    jQuery('#js_pdpPriceSavingPercent').html(variantPdpPriceSavingPercent);
                    
                    var variantPdpPriceList = jQuery('#js_pdpPriceList_'+VARMAP[mapKey]).html();
                    jQuery('#js_pdpPriceList').html(variantPdpPriceList);
                    
                    var variantPdpPriceOnline = jQuery('#js_pdpPriceOnline_'+VARMAP[mapKey]).html();
                    jQuery('#js_pdpPriceOnline').html(variantPdpPriceOnline);
                    
                    var variantPdpVolumePricing = jQuery('#js_pdpVolumePricing_'+VARMAP[mapKey]).html();
                    jQuery('#js_pdpVolumePricing').html(variantPdpVolumePricing);     
                    
                    var variantPdpInternalName = jQuery('#js_pdpInternalName_'+VARMAP[mapKey]).html();
                    jQuery('#js_pdpInternalName').html(variantPdpInternalName);           
                    
                    var variantLargeImages = jQuery('#js_largeImageUrl_'+VARMAP[mapKey]).clone();
            		var variantPdpLongDescription = jQuery('#js_pdpLongDescription_Virtual').html();
            		var variantPdpDistinguishingFeature = jQuery('#js_pdpDistinguishingFeature_Virtual').html();
            		var variantPdpDeliveryInfo = jQuery('#js_pdpDeliveryInfo_Virtual').html();
            		var variantPdpDirections = jQuery('#js_pdpDirections_Virtual').html();
            		var variantPdpIngredients = jQuery('#js_pdpIngredients_Virtual').html();
            		var variantPdpSalesPitch = jQuery('#js_pdpSalesPitch_Virtual').html();
            		var variantPdpSpecialInstructions = jQuery('#js_pdpSpecialInstructions_Virtual').html();
            		var variantPdpTermsConditions = jQuery('#js_pdpTermsConditions_Virtual').html();
            		var variantPdpWarnings = jQuery('#js_pdpWarnings_Virtual').html();
            		var variantPdpSpecificContent01 = jQuery('#js_pdpSpecificContent01_'+VARMAP[mapKey]).html();
            		var variantPdpSpecificContent02 = jQuery('#js_pdpSpecificContent02_'+VARMAP[mapKey]).html();
            		var variantPdpSpecificContent03 = jQuery('#js_pdpSpecificContent03_'+VARMAP[mapKey]).html();
            		var variantPdpAttach01 = jQuery('#js_pdpAttach01_'+VARMAP[mapKey]).html();
            		var variantPdpAttach02 = jQuery('#js_pdpAttach02_'+VARMAP[mapKey]).html();
            		var variantPdpAttach03 = jQuery('#js_pdpAttach03_'+VARMAP[mapKey]).html();
            	
            		jQuery(variantLargeImages).find('.js_mainImageLink').attr('id', 'js_mainImageLink');
            		jQuery('#js_seeLargerImage').html(variantLargeImages.html());
            		jQuery('#js_pdpLongDescription').html(variantPdpLongDescription);
            		jQuery('#js_pdpDistinguishingFeature').html(variantPdpDistinguishingFeature);
            		jQuery('#js_pdpDeliveryInfo').html(variantPdpDeliveryInfo);
            		jQuery('#js_pdpDirections').html(variantPdpDirections);
            		jQuery('#js_pdpIngredients').html(variantPdpIngredients);
            		jQuery('#js_pdpSalesPitch').html(variantPdpSalesPitch);
            		jQuery('#js_pdpSpecialInstructions').html(variantPdpSpecialInstructions);
            		jQuery('#js_pdpTermsConditions').html(variantPdpTermsConditions);
            		jQuery('#js_pdpWarnings').html(variantPdpWarnings);
            		jQuery('#js_pdpSpecificContent01').html(variantPdpSpecificContent01);
            		jQuery('#js_pdpSpecificContent02').html(variantPdpSpecificContent02);
            		jQuery('#js_pdpSpecificContent03').html(variantPdpSpecificContent03);
            		jQuery('#js_pdpAttach01').html(variantPdpAttach01);
            		jQuery('#js_pdpAttach02').html(variantPdpAttach02);
            		jQuery('#js_pdpAttach03').html(variantPdpAttach03);
					
					if(jQuery('#js_quantity').length)
					{
						var productAttrPdpQtyDefault="";
	            		if(jQuery('#js_pdpQtyDefaultAttributeValue_' + VARMAP[mapKey]).length)
	            		{
	            			productAttrPdpQtyDefault = jQuery('#js_pdpQtyDefaultAttributeValue_' + VARMAP[mapKey]).val();
	            		}
	            		else
	            		{
	            			productAttrPdpQtyDefault = Number('${PDP_QTY_DEFAULT!}');
	            		}
	            		if(productAttrPdpQtyDefault && userModifiedQty == "N")
	            		{
	            			jQuery('#js_quantity').val(productAttrPdpQtyDefault);
	            		}
            		}
                    
            }
            if (index == -1) 
            {
               for (i = currentFeatureIndex; i < OPT.length; i++) 
               {
                   var featureName = jQuery('.js_selectableFeature_'+(i+1)).attr("name");
               
                   if(i == 0)
                   {
                       <#if featureOrderFirst?has_content>
                           var Variable1 = eval("list${StringUtil.wrapString(featureOrderFirst)!}()");
                           jQuery('#js_addToCart').addClass("inactiveAddToCart");
	                       jQuery('#js_addToWishlist').addClass("inactiveAddToWishlist");
	                       jQuery('#js_quantity').attr("disabled", "disabled");
                       </#if>
                   }
                   else
                   {    
                       
	                   if(i == currentFeatureIndex)
	                   {
	                       var Variable1 = eval("list" + featureName + jQuery('.js_selectableFeature_'+i).val() + "()");
	                       var Variable1 = eval("listLi" + featureName + jQuery('.js_selectableFeature_'+i).val() + "()");
	                       jQuery('.js_selectableFeature_'+(i+1)).children().removeAttr("disabled"); 
	                   }
	                   else
	                   {
	                       var Variable1 = eval("list" + featureName + "()");
	                       var Variable1 = eval("listLi" + featureName + "()");
	                   }
                   }
               } 
              
              
              firstNoSelection = "true";
				
			  var variantLargeImages = jQuery('#js_largeImageUrl_Virtual').clone();
              var variantPdpLongDescription = jQuery('#js_pdpLongDescription_Virtual').html();
              var variantPdpDistinguishingFeature = jQuery('#js_pdpDistinguishingFeature_Virtual').html();
              var variantPdpDeliveryInfo = jQuery('#js_pdpDeliveryInfo_Virtual').html();
              var variantPdpDirections = jQuery('#js_pdpDirections_Virtual').html();
              var variantPdpIngredients = jQuery('#js_pdpIngredients_Virtual').html();
              var variantPdpSalesPitch = jQuery('#js_pdpSalesPitch_Virtual').html();
              var variantPdpSpecialInstructions = jQuery('#js_pdpSpecialInstructions_Virtual').html();
              var variantPdpTermsConditions = jQuery('#js_pdpTermsConditions_Virtual').html();
              var variantPdpWarnings = jQuery('#js_pdpWarnings_Virtual').html();
              var variantPdpInternalName = jQuery('#js_pdpInternalName_Virtual').html();
              var variantPdpAttach01 = jQuery('#js_pdpAttach01_Virtual').html();
              var variantPdpAttach02 = jQuery('#js_pdpAttach02_Virtual').html();
              var variantPdpAttach03 = jQuery('#js_pdpAttach03_Virtual').html();
              var variantPdpPriceList = jQuery('#js_pdpPriceList_Virtual').html();
        	  var variantPdpPriceOnline = jQuery('#js_pdpPriceOnline_Virtual').html();
        	  var variantPdpPriceSavingMoney = jQuery('#js_pdpPriceSavingMoney_Virtual').html();
        	  var variantPdpPriceSavingPercent = jQuery('#js_pdpPriceSavingPercent_Virtual').html();
        	  var variantPdpVolumePricing = jQuery('#js_pdpVolumePricing_Virtual').html();
            	
              jQuery(variantLargeImages).find('.js_mainImageLink').attr('id', 'js_mainImageLink');
              jQuery('#js_seeLargerImage').html(variantLargeImages.html());
              jQuery('#js_pdpLongDescription').html(variantPdpLongDescription);
              jQuery('#js_pdpDistinguishingFeature').html(variantPdpDistinguishingFeature);
              jQuery('#js_pdpDeliveryInfo').html(variantPdpDeliveryInfo);
              jQuery('#js_pdpDirections').html(variantPdpDirections);
              jQuery('#js_pdpIngredients').html(variantPdpIngredients);
              jQuery('#js_pdpSalesPitch').html(variantPdpSalesPitch);
              jQuery('#js_pdpSpecialInstructions').html(variantPdpSpecialInstructions);
              jQuery('#js_pdpTermsConditions').html(variantPdpTermsConditions);
              jQuery('#js_pdpWarnings').html(variantPdpWarnings);
              jQuery('#js_pdpInternalName').html(variantPdpInternalName); 
              jQuery('#js_pdpAttach01').html(variantPdpAttach01);
              jQuery('#js_pdpAttach02').html(variantPdpAttach02);
              jQuery('#js_pdpAttach03').html(variantPdpAttach03);
              jQuery('#js_pdpPriceList').html(variantPdpPriceList);
              jQuery('#js_pdpPriceOnline').html(variantPdpPriceOnline);
              jQuery('#js_pdpPriceSavingMoney').html(variantPdpPriceSavingMoney);
              jQuery('#js_pdpPriceSavingPercent').html(variantPdpPriceSavingPercent);
			  jQuery('#js_pdpVolumePricing').html(variantPdpVolumePricing);
            } 
            else 
            {
                firstNoSelection = "false";
                //alert(OPT[(currentFeatureIndex+1)]);
                var Variable1 = eval("list" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
                var Variable2 = eval("listLi" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
                  
                  var elm = document.getElementById("js_addToCart");
                  elm.setAttribute("onClick","javascript:addItemToCart()");
                  var elm = document.getElementById("js_addToWishlist");
                  if (elm !=null )
                  {
                    elm.setAttribute("onClick","javascript:addItemToWishlist()");
                  }
                  if (currentFeatureIndex+1 <= (OPT.length-1) ) 
                  {
	                    var nextFeatureLength = document.forms["addform"].elements[OPT[(currentFeatureIndex+1)]].length;
	                    if(nextFeatureLength == 2) 
	                    {
	                      getList(OPT[(currentFeatureIndex+1)],'0',1);
	                      return;
	                    } 
	                    else 
	                    {
	                      jQuery('#js_addToCart').addClass("inactiveAddToCart");
	                      jQuery('#js_quantity').attr("disabled", "disabled");
	                      if (elm !=null )
	                      {
	                          jQuery('#js_addToWishlist').addClass("inactiveAddToWishlist");
	                      }
	                    }
                  }
                   
            }
            <#-- set the product ID to NULL to trigger the alerts -->
            setAddProductId('NULL');

        }
        else 
        {
            
			<#-- this is the final selection -- locate the selected index of the last selection -->
            var indexSelected = document.forms["addform"].elements[name].selectedIndex;
            <#-- using the selected index locate the sku -->
            var sku = document.forms["addform"].elements[name].options[indexSelected].value;
            <#-- set the product ID -->
            if(firstNoSelection == "false")
            {
            	setAddProductId(sku);
            }
            else
            {
            	setAddProductId("");
            }
            
            var varProductId = jQuery('#js_add_product_id').val();
			
            if(varProductId == "")
            {
            	jQuery('#js_addToCart').addClass("inactiveAddToCart");
				jQuery('#js_addToWishlist').addClass("inactiveAddToWishlist");
				jQuery('#js_quantity').attr("disabled", "disabled");
			}
			else 
			{
                setProductStock(sku);
			}
			
			if(noSelection=="true" || varProductId == "")
			{
            	var indexDisplayed = 1;
            	varProductId = document.forms["addform"].elements[name].options[indexDisplayed].value;
            }
        
            if(jQuery('#js_mainImage_'+varProductId).length) 
            {
	            var variantMainImages = jQuery('#js_mainImage_'+varProductId).clone();
	            //jQuery(variantMainImages).find('img').each(function(){jQuery(this).attr('src', jQuery(this).attr('title')+ "?" + new Date().getTime());})
	            jQuery(variantMainImages).find('a').attr('class', 'innerZoom');
	            detailImgUrl = jQuery(variantMainImages).find('a').attr('href');
	            jQuery('#js_productDetailsImageContainer').html(variantMainImages.html());
	        }
	        
	        var variantAltImages = jQuery('#js_altImage_'+varProductId).clone();
        	var variantProductVideo = jQuery('#js_productVideo_'+varProductId).html();
        	var variantProductVideoLink = jQuery('#js_productVideoLink_'+varProductId).html();
        	var variantProductVideo360 = jQuery('#js_productVideo360_'+varProductId).html();
        	var variantProductVideo360Link = jQuery('#js_productVideo360Link_'+varProductId).html();
        	
        	
	            
            if(noSelection=="true" || varProductId == "")
            {
            	var variantLargeImages = jQuery('#js_largeImageUrl_Virtual').clone();
            	var variantPdpLongDescription = jQuery('#js_pdpLongDescription_Virtual').html();
            	var variantPdpDistinguishingFeature = jQuery('#js_pdpDistinguishingFeature_Virtual').html();
            	var variantPdpDeliveryInfo = jQuery('#js_pdpDeliveryInfo_Virtual').html();
            	var variantPdpDirections = jQuery('#js_pdpDirections_Virtual').html();
            	var variantPdpIngredients = jQuery('#js_pdpIngredients_Virtual').html();
            	var variantPdpSalesPitch = jQuery('#js_pdpSalesPitch_Virtual').html();
            	var variantPdpSpecialInstructions = jQuery('#js_pdpSpecialInstructions_Virtual').html();
            	var variantPdpTermsConditions = jQuery('#js_pdpTermsConditions_Virtual').html();
            	var variantPdpWarnings = jQuery('#js_pdpWarnings_Virtual').html();
            	var variantPdpInternalName = jQuery('#js_pdpInternalName_Virtual').html();
            	var variantPdpSpecificContent01 = jQuery('#js_pdpSpecificContent01_Virtual').html();
            	var variantPdpSpecificContent02 = jQuery('#js_pdpSpecificContent02_Virtual').html();
            	var variantPdpSpecificContent03 = jQuery('#js_pdpSpecificContent03_Virtual').html();
            	var variantPdpAttach01 = jQuery('#js_pdpAttach01_Virtual').html();
                var variantPdpAttach02 = jQuery('#js_pdpAttach02_Virtual').html();
                var variantPdpAttach03 = jQuery('#js_pdpAttach03_Virtual').html();
                var variantPdpPriceList = jQuery('#js_pdpPriceList_Virtual').html();
        	    var variantPdpPriceOnline = jQuery('#js_pdpPriceOnline_Virtual').html();
        	    var variantPdpPriceSavingMoney = jQuery('#js_pdpPriceSavingMoney_Virtual').html();
        	    var variantPdpPriceSavingPercent = jQuery('#js_pdpPriceSavingPercent_Virtual').html();
        	    var variantPdpVolumePricing = jQuery('#js_pdpVolumePricing_Virtual').html();
            }
            else
            {
            	var variantLargeImages = jQuery('#js_largeImageUrl_'+varProductId).clone();
            	var variantPdpLongDescription = jQuery('#js_pdpLongDescription_'+varProductId).html();
            	var variantPdpDistinguishingFeature = jQuery('#js_pdpDistinguishingFeature_'+varProductId).html();
            	var variantPdpDeliveryInfo = jQuery('#js_pdpDeliveryInfo_'+varProductId).html();
            	var variantPdpDirections = jQuery('#js_pdpDirections_'+varProductId).html();
            	var variantPdpIngredients = jQuery('#js_pdpIngredients_'+varProductId).html();
            	var variantPdpSalesPitch = jQuery('#js_pdpSalesPitch_'+varProductId).html();
            	var variantPdpSpecialInstructions = jQuery('#js_pdpSpecialInstructions_'+varProductId).html();
            	var variantPdpTermsConditions = jQuery('#js_pdpTermsConditions_'+varProductId).html();
            	var variantPdpWarnings = jQuery('#js_pdpWarnings_'+varProductId).html();
            	var variantPdpInternalName = jQuery('#js_pdpInternalName_'+varProductId).html();
            	var variantPdpSpecificContent01 = jQuery('#js_pdpSpecificContent01_'+varProductId).html();
            	var variantPdpSpecificContent02 = jQuery('#js_pdpSpecificContent02_'+varProductId).html();
            	var variantPdpSpecificContent03 = jQuery('#js_pdpSpecificContent03_'+varProductId).html();
            	var variantPdpAttach01 = jQuery('#js_pdpAttach01_'+varProductId).html();
                var variantPdpAttach02 = jQuery('#js_pdpAttach02_'+varProductId).html();
                var variantPdpAttach03 = jQuery('#js_pdpAttach03_'+varProductId).html();
                var variantPdpPriceList = jQuery('#js_pdpPriceList_'+varProductId).html();
        	    var variantPdpPriceOnline = jQuery('#js_pdpPriceOnline_'+varProductId).html();
        	    var variantPdpPriceSavingMoney = jQuery('#js_pdpPriceSavingMoney_'+varProductId).html();
        	    var variantPdpPriceSavingPercent = jQuery('#js_pdpPriceSavingPercent_'+varProductId).html();
        	    var variantPdpVolumePricing = jQuery('#js_pdpVolumePricing_'+varProductId).html();
            }
            
            //jQuery(variantAltImages).find('img').each(function(){jQuery(this).attr('src', jQuery(this).attr('title')+ "?" + new Date().getTime());})
            jQuery('#js_eCommerceProductAddImage').html(variantAltImages.html());
			jQuery(variantLargeImages).find('.js_mainImageLink').attr('id', 'js_mainImageLink');
            jQuery('#js_seeLargerImage').html(variantLargeImages.html());
            jQuery('#js_productVideo').html(variantProductVideo);
            jQuery('#js_productVideoLink').html(variantProductVideoLink);
            jQuery('#js_productVideo360').html(variantProductVideo360);
            jQuery('#js_productVideo360Link').html(variantProductVideo360Link);
            jQuery('#js_pdpPriceSavingMoney').html(variantPdpPriceSavingMoney);
            jQuery('#js_pdpPriceSavingPercent').html(variantPdpPriceSavingPercent);
			jQuery('#js_pdpVolumePricing').html(variantPdpVolumePricing);
            jQuery('#js_pdpPriceList').html(variantPdpPriceList);
            jQuery('#js_pdpPriceOnline').html(variantPdpPriceOnline);
            jQuery('#js_pdpLongDescription').html(variantPdpLongDescription);
            jQuery('#js_pdpDistinguishingFeature').html(variantPdpDistinguishingFeature);
    		jQuery('#js_pdpDeliveryInfo').html(variantPdpDeliveryInfo);
    		jQuery('#js_pdpDirections').html(variantPdpDirections);
    		jQuery('#js_pdpIngredients').html(variantPdpIngredients);
    		jQuery('#js_pdpSalesPitch').html(variantPdpSalesPitch);
    		jQuery('#js_pdpSpecialInstructions').html(variantPdpSpecialInstructions);
    		jQuery('#js_pdpTermsConditions').html(variantPdpTermsConditions);
    		jQuery('#js_pdpWarnings').html(variantPdpWarnings);
    		jQuery('#js_pdpInternalName').html(variantPdpInternalName);
    		jQuery('#js_pdpSpecificContent01').html(variantPdpSpecificContent01);
    		jQuery('#js_pdpSpecificContent02').html(variantPdpSpecificContent01);
    		jQuery('#js_pdpSpecificContent03').html(variantPdpSpecificContent01);
    		jQuery('#js_pdpAttach01').html(variantPdpAttach01);
    		jQuery('#js_pdpAttach02').html(variantPdpAttach02);
    		jQuery('#js_pdpAttach03').html(variantPdpAttach03);
    		
    		if(jQuery('#js_quantity').length)
    		{
	    		var productAttrPdpQtyDefault="";
	            if(jQuery('#js_pdpQtyDefaultAttributeValue_'+varProductId).length)
	    		{
	    			productAttrPdpQtyDefault = jQuery('#js_pdpQtyDefaultAttributeValue_'+varProductId).val();
	    		}
	    		else
	    		{
	    			productAttrPdpQtyDefault = Number('${PDP_QTY_DEFAULT!}');
	    		}
	    		if(productAttrPdpQtyDefault && (userModifiedQty == "N"))
	    		{
	    		   jQuery('#js_quantity').val(productAttrPdpQtyDefault);
	    		}
    		}
            
        }
        activateZoom(detailImgUrl);
        activateScroller();
    }


    function activateZoom(imgUrl) 
    {
        if (typeof imgUrl == "undefined" || imgUrl == "NULL" || imgUrl == "") 
        {
            return;
        }
        var image = new Image();
        image.src = imgUrl+ "?" + new Date().getTime();
        image.onerror = function () 
        {
            jQuery('.innerZoom').attr('href', 'javaScript:void(0);');
            return false;
        };
        image.onload = function () 
        {
            jQuery('.innerZoom').jqzoom(zoomOptions);
        };
        
    }
    
    function activateInitialZoom() 
    {
        jQuery('.innerZoom').each(function() 
        {
            var elm = this;
            var image = new Image();
            image.src = this.href+ "?" + new Date().getTime();
            image.onerror = function () 
            {
                jQuery(elm).attr('href', 'javaScript:void(0);');
                return false;
            };
            image.onload = function () 
            {
                jQuery('.innerZoom').jqzoom(zoomOptions);
            };
        });
    }

    function activateScroller() 
    {
        <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PDP_ALT_IMG_SCROLLER_ACTIVE")>
            if(!jQuery('#js_altImageThumbnails').length) 
            {
                jQuery('#js_eCommerceProductAddImage').find('ul').attr('id', 'js_altImageThumbnails');
            }
            jQuery('#js_altImageThumbnails').addClass('imageScroller');
            jQuery('#js_altImageThumbnails').jcarousel(
            {
                <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PDP_ALT_IMG_SCROLLER_VERTICAL")>
                    vertical: true,
                </#if>
                scroll: ${PDP_ALT_IMG_SCROLLER_IMAGES!"2"},
                itemFallbackDimension: 300
            });
        </#if>
    }

    function showProductVideo(videoDivClass)
    {
        if (jQuery.browser.msie) jQuery('object > embed').unwrap(); 
        videoDiv = '.'+ videoDivClass;
        jQuery('#js_productDetailsImageContainer').html(jQuery(videoDiv).clone().removeClass(videoDivClass).show());
    }

    var zoomOptions = {
        zoomType: 'innerzoom',
        lens:true,
        preloadImages: true,
        preloadText: ''
    };


    function makeProductUrl(elm) 
    {
        var plpFeatureSwatchImageId = jQuery(elm).attr("id");
        var plpFeatureSwatchImageIdArr = plpFeatureSwatchImageId.split("|");
        var pdpUrlId = plpFeatureSwatchImageIdArr[1]+plpFeatureSwatchImageIdArr[0]; 
        var pdpUrl = document.getElementById(pdpUrlId).value;
        
        jQuery(elm).parents('.productItem').find('.js_eCommerceThumbNailHolder').find('.js_swatchProduct').find('a').attr("href",pdpUrl);
        jQuery(elm).parents('.productItem').find('a.js_pdpUrl').attr("href",pdpUrl);
        jQuery(elm).parents('.productItem').find('a.js_pdpUrl.js_review').attr("href",pdpUrl+"#productReviews");
    }
  

  jQuery(document).ready(function()
  {
    jQuery('.js_pdpFeatureSwatchImage').click(function() 
    {
        if (jQuery('.js_seeItemDetail').length) 
        {
            jQuery('#js_plpQuicklook_Container .js_seeItemDetail').attr('href', jQuery('#quicklook_Url_'+this.title).val());
        }
    }); 
  
    var pdpTab = '${StringUtil.wrapString(parameters.tab)!""}';
    if(pdpTab != '') 
    {
        var tabAnchor = jQuery('#js_' + pdpTab).parents('.ui-tabs-panel').attr('id');
	    if(tabAnchor != '') 
	    {
	        jQuery.find('a[href="#'+tabAnchor+'"]').each(function(elm)
	        {
	            jQuery(elm).click();
	        });
           var tabAnchorParent = jQuery('#' + tabAnchor).parents('.js_pdpTabs');
	       jQuery('html,body').animate({scrollTop: tabAnchorParent.offset().top},'slow'); 
	    }      
        
    }

    jQuery('.js_pdpUrl.js_review').click(function() 
    {
        var tabAnchor = jQuery('#js_productReviews').parents('.ui-tabs-panel').attr('id');
        jQuery.find('a[href="#'+tabAnchor+'"]').each(function(elm)
        {
            jQuery(elm).click();
        });
    });

    jQuery('#js_reviewSort').change(function() 
    {
        var tabAnchor = jQuery('#js_productReviews').parents('.ui-tabs-panel').attr('id');
        jQuery.find('a[href="#'+tabAnchor+'"]').each(function(elm)
        {
            jQuery(elm).click();
        });
     });
     
    jQuery('.js_plpFeatureSwatchImage').click(function() 
    {
        var swatchVariant = jQuery(this).next('.js_swatchVariant').clone();
        
        var swatchVariantOnlinePrice = jQuery(this).nextAll('.js_swatchVariantOnlinePrice:first').clone().show();
        swatchVariantOnlinePrice.removeClass('js_swatchVariantOnlinePrice').addClass('js_plpPriceOnline');
        jQuery(this).parents('.productItem').find('.js_plpPriceOnline').replaceWith(swatchVariantOnlinePrice);

        var swatchVariantListPrice = jQuery(this).nextAll('.js_swatchVariantListPrice:first').clone().show();
        swatchVariantListPrice.removeClass('js_swatchVariantListPrice').addClass('js_plpPriceList');
        jQuery(this).parents('.productItem').find('.js_plpPriceList').replaceWith(swatchVariantListPrice);
        
        var swatchVariantSaveMoney = jQuery(this).nextAll('.js_swatchVariantSaveMoney:first').clone().show();
        swatchVariantSaveMoney.removeClass('js_swatchVariantSaveMoney').addClass('js_plpPriceSavingMoney');
        jQuery(this).parents('.productItem').find('.js_plpPriceSavingMoney').replaceWith(swatchVariantSaveMoney);
        
        var swatchVariantSavingPercent = jQuery(this).nextAll('.js_swatchVariantSavingPercent:first').clone().show();
        swatchVariantSavingPercent.removeClass('js_swatchVariantSavingPercent').addClass('js_plpPriceSavingPercent');
        jQuery(this).parents('.productItem').find('.js_plpPriceSavingPercent').replaceWith(swatchVariantSavingPercent);
        
        jQuery(this).parents('.productItem').find('.js_eCommerceThumbNailHolder').find('.js_swatchProduct').replaceWith(swatchVariant);
        
        jQuery('.js_eCommerceThumbNailHolder').find('.js_swatchVariant').show().attr("class", "js_swatchProduct");
        jQuery(this).siblings('.js_plpFeatureSwatchImage').removeClass('selected');
        jQuery(this).addClass('selected');
        makeProductUrl(this);
    });
    
    activateInitialZoom();

    var selectedSwatch = '${StringUtil.wrapString(parameters.productFeatureType)!""}';
    if(selectedSwatch != '') 
    {
        var featureArray = selectedSwatch.split(":");
        //jQuery('.pdpRecentlyViewed .'+featureArray[1]).click();
        //jQuery('.pdpComplement .'+featureArray[1]).click();
    }
    <#if isPdpInStoreOnly?exists>
        checkProductInStore('${isPdpInStoreOnly}');
    </#if>
  });
  
  function updateImageText(idx) 
  {
  		<#assign textPersonalization = productAtrributeMap.PT_TEXT_PERSONALIZE!/>
		<#if textPersonalization?has_content && textPersonalization == "TRUE">
	    	jQuery('#js_productDetailsImageContainer').css("position", "relative");
	    	
	    	var img = document.getElementById('js_mainImage'); 
			var img_Width = img.clientWidth;
			var img_Height = img.clientHeight;
	    	
	    	if(jQuery('#js_imageTextArea').length)
			{
				jQuery('#js_imageTextArea').css("width", img_Width + "px");
			}
	    	
	        var updateText = jQuery('#textLine_' + idx).val();
	        var textDivId = "textOverImg_" + idx;
	        if(jQuery('#' + textDivId).length)
	        {
		        jQuery('#' + textDivId).text(updateText);
			}
			else
			{	
				textAreaHeight = "${personalizationDefaultMap.PT_AREA_HEIGHT!"46%"}";
				textAreaPosLeft = "${personalizationDefaultMap.PT_AREA_POS_LEFT!"50px"}";
				textAreaPosRight = "${personalizationDefaultMap.PT_AREA_POS_RIGHT!"50px"}";
				textAreaPosBottom = "${personalizationDefaultMap.PT_AREA_POS_BOTTOM!"3"}";
				<#if productAtrributeMap.PT_AREA_HEIGHT?has_content>
					textAreaHeight = "${productAtrributeMap.PT_AREA_HEIGHT!}";
				</#if>
				<#if productAtrributeMap.PT_AREA_POS_LEFT?has_content>
					textAreaPosLeft = "${productAtrributeMap.PT_AREA_POS_LEFT!}";
				</#if>
				<#if productAtrributeMap.PT_AREA_POS_RIGHT?has_content>
					textAreaPosRight = "${productAtrributeMap.PT_AREA_POS_RIGHT!}";
				</#if>
				<#if productAtrributeMap.PT_AREA_POS_BOTTOM?has_content>
					textAreaPosBottom = "${productAtrributeMap.PT_AREA_POS_BOTTOM!}";
				</#if>
				
				<#assign textLinesNum = personalizationDefaultMap.PT_TEXT_LINES_NUM!"3"/>
				<#if productAtrributeMap.PT_TEXT_LINES_NUM?has_content>
					<#assign textLinesNum = productAtrributeMap.PT_TEXT_LINES_NUM!/>
				</#if>
				
				<#if textLinesNum?has_content && Static["com.osafe.util.Util"].isNumber(textLinesNum) && (textLinesNum?number &gt; 0)>
					if(!jQuery('#js_imageTextArea').length)
					{
						jQuery('<div id="js_imageTextArea" style="position:absolute; width: ' + img_Width + 'px; z-index: 2; bottom:0; height: ' + textAreaHeight + '; padding-left: ' + textAreaPosLeft + '; padding-right: ' + textAreaPosRight + '; padding-bottom:' + textAreaPosBottom + ';"><div id="js_innerTextArea" style="height:100%; width:100%;"><div id="js_textHolder" style="position:relative;"></div></div></div>').prependTo('#js_productDetailsImageContainer');
					}
					
					var isInserted = 'N';
					var isOtherTextFound = 'N';
					var lastIdxFound = -1;
					jQuery('.js_personalizedTextLine').each(function() 
			        {
			            var personalizedLineId = jQuery(this).attr('id');
			            personalizedLineIdArr = personalizedLineId.split('_');
			            personalizedLineIdx = personalizedLineIdArr[1];
			            
			            existingId = "textOverImg_" + personalizedLineIdx;
			            if(jQuery('#' + existingId).length)
	        			{
	        				isOtherTextFound = 'Y';
	        				lastIdxFound = Number(personalizedLineIdx);
				            if(Number(personalizedLineIdx) > Number(idx))
				            {
				            	jQuery('#' + existingId).before('<div class="textOverImg" id="' + textDivId + '">' + updateText + '</div>');
				            	isInserted = 'Y';
				            	return false;
			            	}
			            }
			            
			        });
			        
					if(isInserted == 'N')
					{
						if(isOtherTextFound == 'N')
						{
							jQuery('<div class="textOverImg" id="' + textDivId + '">' + updateText + '</div>').prependTo('#js_textHolder');
						}
						else
						{
							jQuery('#textOverImg_' + lastIdxFound).after('<div class="textOverImg" id="' + textDivId + '">' + updateText + '</div>');
						}
					}
				</#if>
			}
			
			var container = document.getElementById('js_innerTextArea'); 
			var content = document.getElementById('js_textHolder'); 
			jQuery(content).css("top", (container.clientHeight -content.clientHeight)/2) + "px";
			
			var txtHeight = Number(0);
			jQuery('.textOverImg').each(function() 
	        {
	        	txtHeight = Number(txtHeight) + Number(jQuery(this).height());
	        });
	        var halfTxtHeight = Number(txtHeight/2);
			
			textColor = "${StringUtil.wrapString(personalizationDefaultMap.PT_TEXT_COLOR)!""}";
			<#if productAtrributeMap.PT_TEXT_COLOR?has_content>
				textColor = "${StringUtil.wrapString(productAtrributeMap.PT_TEXT_COLOR)!}";
			</#if>
			if(textColor != "")
	  		{
	  			jQuery('.textOverImg').css("color", textColor);
	  		}
	  		
	  		fontWeight = "${personalizationDefaultMap.PT_FONT_WEIGHT!""}";
	  		<#if productAtrributeMap.PT_FONT_WEIGHT?has_content>
				fontWeight = "${productAtrributeMap.PT_FONT_WEIGHT!}";
			</#if>
			if(fontWeight != "")
	  		{
	  			jQuery('.textOverImg').css("font-weight", fontWeight);
	  		}
		</#if>
        
  }
  function updateImageTextSize(idx) 
  {
  		if(jQuery('#fontSize_' + idx).length)
        {
	  		var updateFontEnum = jQuery('#fontSize_' + idx).val();
	  		if(updateFontEnum =="")
	  		{
	  			updateFontEnum = "${personalizationDefaultMap.PT_DEFAULT_FONT_SIZE!}";
	  			<#if productAtrributeMap.PT_DEFAULT_FONT_SIZE?has_content>
					updateFontEnum = "${productAtrributeMap.PT_DEFAULT_FONT_SIZE!}";
				</#if>
	  		}
	  		var updateFont = "20";
	  		<#assign fontSizeEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "FONT_SIZE"))/>  
	      	<#if fontSizeEnums?has_content>
	      		<#list fontSizeEnums as fontSizeEnum>
	      			if(updateFontEnum == "${fontSizeEnum.enumId!}")
	      			{
	      				updateFont = "${fontSizeEnum.enumCode!}";
	      			}
	          	</#list>
	      	</#if>
	      	
	      	
	      	<#-- We currently support defining line bottom margins for 7 (enumerated) font size values -->
	      	updateFontEnumArr = updateFontEnum.split('_');
			updateFontEnumSizeLevel = updateFontEnumArr[2];
			updateTextMarginBottom = "-10px";
			if(updateFontEnumSizeLevel == "1")
	  		{
				updateTextMarginBottom = "${personalizationDefaultMap.PT_MARG_BOTTOM_FNT_1!"-3px"}";
				<#if productAtrributeMap.PT_MARG_BOTTOM_FNT_1?has_content>
					updateTextMarginBottom = "${productAtrributeMap.PT_MARG_BOTTOM_FNT_1!}";
				</#if>
			}
			else if(updateFontEnumSizeLevel == "2")
	  		{
				updateTextMarginBottom = "${personalizationDefaultMap.PT_MARG_BOTTOM_FNT_2!"-4px"}";
				<#if productAtrributeMap.PT_MARG_BOTTOM_FNT_2?has_content>
					updateTextMarginBottom = "${productAtrributeMap.PT_MARG_BOTTOM_FNT_2!}";
				</#if>
			}
			else if(updateFontEnumSizeLevel == "3")
	  		{
				updateTextMarginBottom = "${personalizationDefaultMap.PT_MARG_BOTTOM_FNT_3!"-5px"}";
				<#if productAtrributeMap.PT_MARG_BOTTOM_FNT_3?has_content>
					updateTextMarginBottom = "${productAtrributeMap.PT_MARG_BOTTOM_FNT_3!}";
				</#if>
			}
			else if(updateFontEnumSizeLevel == "4")
	  		{
				updateTextMarginBottom = "${personalizationDefaultMap.PT_MARG_BOTTOM_FNT_4!"-7px"}";
				<#if productAtrributeMap.PT_MARG_BOTTOM_FNT_4?has_content>
					updateTextMarginBottom = "${productAtrributeMap.PT_MARG_BOTTOM_FNT_4!}";
				</#if>
			}
			else if(updateFontEnumSizeLevel == "5")
	  		{
				updateTextMarginBottom = "${personalizationDefaultMap.PT_MARG_BOTTOM_FNT_5!"-8px"}";
				<#if productAtrributeMap.PT_MARG_BOTTOM_FNT_5?has_content>
					updateTextMarginBottom = "${productAtrributeMap.PT_MARG_BOTTOM_FNT_5!}";
				</#if>
			}
			else if(updateFontEnumSizeLevel == "6")
	  		{
				updateTextMarginBottom = "${personalizationDefaultMap.PT_MARG_BOTTOM_FNT_6!"-9px"}";
				<#if productAtrributeMap.PT_MARG_BOTTOM_FNT_6?has_content>
					updateTextMarginBottom = "${productAtrributeMap.PT_MARG_BOTTOM_FNT_6!}";
				</#if>
			}
			else if(updateFontEnumSizeLevel == "7")
	  		{
				updateTextMarginBottom = "${personalizationDefaultMap.PT_MARG_BOTTOM_FNT_7!"-10px"}";
				<#if productAtrributeMap.PT_MARG_BOTTOM_FNT_7?has_content>
					updateTextMarginBottom = "${productAtrributeMap.PT_MARG_BOTTOM_FNT_7!}";
				</#if>
			}
	      	
	      	
	  		var textDivId = "textOverImg_" + idx;
	        if(jQuery('#' + textDivId).length)
	        {
		        jQuery('#' + textDivId).css("font-size", updateFont + "px");
		        jQuery('#' + textDivId).css("margin-bottom", updateTextMarginBottom);
			}
			
			var container = document.getElementById('js_innerTextArea'); 
			var content = document.getElementById('js_textHolder'); 
			jQuery(content).css("top", (container.clientHeight -content.clientHeight)/2) + "px";
		}
  }
  
  function updateImageTextFont() 
  {
  		if(jQuery('#js_fontEnum').length)
  		{
	  		var updateFontFamilyEnum = jQuery('#js_fontEnum').val();
	  		if(updateFontFamilyEnum =="")
	  		{
	  			updateFontFamilyEnum = "${personalizationDefaultMap.PT_DEFALUT_FONT!}";
	  			<#if productAtrributeMap.PT_DEFALUT_FONT?has_content>
					updateFontFamilyEnum = "${productAtrributeMap.PT_DEFALUT_FONT!}";
				</#if>
	  		}
	  		var updateFontFamily = "Arial";
	  		<#assign fontFamilyEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "FONT_FAMILY"))/>  
	      	<#if fontSizeEnums?has_content>
	      		<#list fontFamilyEnums as fontFamilyEnum>
	      			if(updateFontFamilyEnum == "${fontFamilyEnum.enumId!}")
	      			{
	      				updateFontFamily = "${fontFamilyEnum.description!}";
	      			}
	          	</#list>
	      	</#if>
	
	        if(jQuery('.textOverImg').length)
	        {
		        jQuery('.textOverImg').css("font-family", updateFontFamily);
			}
		}
  }
  
  function updateImageTextAlignment() 
  {
  		if(jQuery('#js_textAlign').length)
  		{
	  		var updateTextAlignEnum = jQuery('#js_textAlign').val();
	  		if(updateTextAlignEnum =="")
	  		{
	  			updateTextAlignEnum = "${personalizationDefaultMap.PT_DEFALUT_TEXT_ALIGN!}";
	  			<#if productAtrributeMap.PT_TEXT_DEFALUT_ALIGN?has_content>
					updateTextAlignEnum = "${productAtrributeMap.PT_TEXT_DEFALUT_ALIGN!}";
				</#if>
	  		}
	  		var updateTextAlign = "Arial";
	  		<#assign textAlignEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "TEXT_ALIGN"))/>  
	      	<#if textAlignEnums?has_content>
	      		<#list textAlignEnums as textAlignEnum>
	      			if(updateTextAlignEnum == "${textAlignEnum.enumId!}")
	      			{
	      				updateTextAlign = "${textAlignEnum.description!}";
	      			}
	          	</#list>
	      	</#if>
	
	        if(jQuery('.textOverImg').length)
	        {
		        jQuery('.textOverImg').css("text-align", updateTextAlign);
			}
		}
  }
  
  function updatePersonalizationMap(elm) 
  {
  		textLinesNum = "${personalizationDefaultMap.PT_TEXT_LINES_NUM!}";
		<#if productAtrributeMap.PT_TEXT_LINES_NUM?has_content>
			textLinesNum = "${productAtrributeMap.PT_TEXT_LINES_NUM!}";
		</#if>
  		var value = jQuery(elm).val();
  		var name = jQuery(elm).attr("name");
  		jQuery.get('<@ofbizUrl>updatePersonalizationMap?textLinesNum='+textLinesNum+'&inputName='+name+'&'+name+'='+value+'&callback=Y&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
            
        });
  }
  
  
  jQuery(document).ready(function()
  {
  	<#assign textLinesNum = personalizationDefaultMap.PT_TEXT_LINES_NUM!/>
	<#if productAtrributeMap.PT_TEXT_LINES_NUM?has_content>
		<#assign textLinesNum = productAtrributeMap.PT_TEXT_LINES_NUM!/>
	</#if>
	<#if textLinesNum?has_content && Static["com.osafe.util.Util"].isNumber(textLinesNum) && (textLinesNum?number &gt; 0)>
		<#list 0 .. (textLinesNum?number-1) as idx>
			updateImageText('${idx!}');
			updateImageTextSize('${idx!}');
		</#list>
	</#if>
	updateImageTextFont();
	updateImageTextAlignment();
  });

</#if>

 </script>
