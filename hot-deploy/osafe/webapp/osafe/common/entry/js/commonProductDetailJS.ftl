<script language="JavaScript" type="text/javascript">
  <#if currentProduct?exists>
  function sortReviews() {
      document.addform.sortReviewBy.value=document.getElementById('reviewSort').value;
      var reviewParams = jQuery('.pdpReviewList').find('input.reviewParam').serialize();
      jQuery.get('<@ofbizUrl>sortPdpReview?'+reviewParams+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
          var sortedList = jQuery(data).find('.pdpReviewList');
          jQuery('.pdpReviewList').replaceWith(sortedList);
      });
  }

  jQuery(document).ready(function(){
    jQuery('.pdpFeatureSwatchImage').click(function() {
        if (jQuery('.seeItemDetail').length) {
            jQuery('#plpQuicklook_Container .seeItemDetail').attr('href', jQuery('#quicklook_Url_'+this.title).val());
        }
    }); 
  
  jQuery('.pdpUrl.review').click(function() {
     var tabAnchor = jQuery('#productReviews').parents('.ui-tabs-panel').attr('id');
     jQuery.find('a[href="#'+tabAnchor+'"]').each(function(elm){
      jQuery(elm).click();
     });
    });

  jQuery('#reviewSort').change(function() {
     var tabAnchor = jQuery('#productReviews').parents('.ui-tabs-panel').attr('id');
     jQuery.find('a[href="#'+tabAnchor+'"]').each(function(elm){
      jQuery(elm).click();
     });
    });



    jQuery('.eCommerceRecentlyViewedProduct .plpFeatureSwatchImage').click(function() {
        var swatchVariant = jQuery(this).next('.swatchVariant').clone();
        
        var swatchVariantOnlinePrice = jQuery(this).nextAll('.swatchVariantOnlinePrice:first').clone().show();
        swatchVariantOnlinePrice.removeClass('swatchVariantOnlinePrice').addClass('plpPriceOnline');
        jQuery(this).parents('.eCommerceListItem').find('.pdpRecentPriceOnlineSeq').find('.plpPriceOnline').replaceWith(swatchVariantOnlinePrice);

        var swatchVariantListPrice = jQuery(this).nextAll('.swatchVariantListPrice:first').clone().show();
        swatchVariantListPrice.removeClass('swatchVariantListPrice').addClass('plpPriceList');
        jQuery(this).parents('.eCommerceListItem').find('.pdpRecentPriceListSeq').find('.plpPriceList').replaceWith(swatchVariantListPrice);
        
        var swatchVariantSaveMoney = jQuery(this).nextAll('.swatchVariantSaveMoney:first').clone().show();
        swatchVariantSaveMoney.removeClass('swatchVariantSaveMoney').addClass('plpPriceSavingMoney');
        jQuery(this).parents('.eCommerceListItem').find('.pdpRecentPriceSavingMoneySeq').find('.plpPriceSavingMoney').replaceWith(swatchVariantSaveMoney);
        
        var swatchVariantSavingPercent = jQuery(this).nextAll('.swatchVariantSavingPercent:first').clone().show();
        swatchVariantSavingPercent.removeClass('swatchVariantSavingPercent').addClass('plpPriceSavingPercent');
        jQuery(this).parents('.eCommerceListItem').find('.pdpRecentPriceSavingPercentSeq').find('.plpPriceSavingPercent').replaceWith(swatchVariantSavingPercent);
        
        jQuery(this).parents('.eCommerceListItem').find('.eCommerceThumbNailHolder').find('.swatchProduct').replaceWith(swatchVariant);
        
        jQuery('.eCommerceThumbNailHolder').find('.swatchVariant').show().attr("class", "swatchProduct");
        jQuery(this).siblings('.plpFeatureSwatchImage').removeClass('selected');
        jQuery(this).addClass('selected');
        makeProductUrl(this);
    });
    
    jQuery('.eCommerceComplementProduct .plpFeatureSwatchImage').click(function() {
        var swatchVariant = jQuery(this).next('.swatchVariant').clone();
        
        var swatchVariantOnlinePrice = jQuery(this).nextAll('.swatchVariantOnlinePrice:first').clone().show();
        swatchVariantOnlinePrice.removeClass('swatchVariantOnlinePrice').addClass('plpPriceOnline');
        jQuery(this).parents('.eCommerceListItem').find('.pdpComplementPriceOnlineSeq').find('.plpPriceOnline').replaceWith(swatchVariantOnlinePrice);

        var swatchVariantListPrice = jQuery(this).nextAll('.swatchVariantListPrice:first').clone().show();
        swatchVariantListPrice.removeClass('swatchVariantListPrice').addClass('plpPriceList');
        jQuery(this).parents('.eCommerceListItem').find('.pdpComplementPriceListSeq').find('.plpPriceList').replaceWith(swatchVariantListPrice);
        
        var swatchVariantSaveMoney = jQuery(this).nextAll('.swatchVariantSaveMoney:first').clone().show();
        swatchVariantSaveMoney.removeClass('swatchVariantSaveMoney').addClass('plpPriceSavingMoney');
        jQuery(this).parents('.eCommerceListItem').find('.pdpComplementPriceSavingMoneySeq').find('.plpPriceSavingMoney').replaceWith(swatchVariantSaveMoney);
        
        var swatchVariantSavingPercent = jQuery(this).nextAll('.swatchVariantSavingPercent:first').clone().show();
        swatchVariantSavingPercent.removeClass('swatchVariantSavingPercent').addClass('plpPriceSavingPercent');
        jQuery(this).parents('.eCommerceListItem').find('.pdpComplementPriceSavingPercentSeq').find('.plpPriceSavingPercent').replaceWith(swatchVariantSavingPercent);
        
        jQuery(this).parents('.eCommerceListItem').find('.eCommerceThumbNailHolder').find('.swatchProduct').replaceWith(swatchVariant);
        jQuery('.eCommerceThumbNailHolder').find('.swatchVariant').show().attr("class", "swatchProduct");
        jQuery(this).siblings('.plpFeatureSwatchImage').removeClass('selected');
        jQuery(this).addClass('selected');
        makeProductUrl(this);
    });
    
    jQuery('.eCommerceAccessoryProduct .plpFeatureSwatchImage').click(function() {
        var swatchVariant = jQuery(this).next('.swatchVariant').clone();
        
        var swatchVariantOnlinePrice = jQuery(this).nextAll('.swatchVariantOnlinePrice:first').clone().show();
        swatchVariantOnlinePrice.removeClass('swatchVariantOnlinePrice').addClass('plpPriceOnline');
        jQuery(this).parents('.eCommerceListItem').find('.pdpAccessoryPriceOnlineSeq').find('.plpPriceOnline').replaceWith(swatchVariantOnlinePrice);

        var swatchVariantListPrice = jQuery(this).nextAll('.swatchVariantListPrice:first').clone().show();
        swatchVariantListPrice.removeClass('swatchVariantListPrice').addClass('plpPriceList');
        jQuery(this).parents('.eCommerceListItem').find('.pdpAccessoryPriceListSeq').find('.plpPriceList').replaceWith(swatchVariantListPrice);
        
        var swatchVariantSaveMoney = jQuery(this).nextAll('.swatchVariantSaveMoney:first').clone().show();
        swatchVariantSaveMoney.removeClass('swatchVariantSaveMoney').addClass('plpPriceSavingMoney');
        jQuery(this).parents('.eCommerceListItem').find('.pdpAccessoryPriceSavingMoneySeq').find('.plpPriceSavingMoney').replaceWith(swatchVariantSaveMoney);
        
        var swatchVariantSavingPercent = jQuery(this).nextAll('.swatchVariantSavingPercent:first').clone().show();
        swatchVariantSavingPercent.removeClass('swatchVariantSavingPercent').addClass('plpPriceSavingPercent');
        jQuery(this).parents('.eCommerceListItem').find('.pdpAccessoryPriceSavingPercentSeq').find('.plpPriceSavingPercent').replaceWith(swatchVariantSavingPercent);
        
        jQuery(this).parents('.eCommerceListItem').find('.eCommerceThumbNailHolder').find('.swatchProduct').replaceWith(swatchVariant);
        jQuery('.eCommerceThumbNailHolder').find('.swatchVariant').show().attr("class", "swatchProduct");
        jQuery(this).siblings('.plpFeatureSwatchImage').removeClass('selected');
        jQuery(this).addClass('selected');
        makeProductUrl(this);
    });
    
    activateInitialZoom();

    var selectedSwatch = '${StringUtil.wrapString(parameters.productFeatureType)!""}';
    if(selectedSwatch != '') {
        var featureArray = selectedSwatch.split(":");
        //jQuery('.pdpRecentlyViewed .'+featureArray[1]).click();
        //jQuery('.pdpComplement .'+featureArray[1]).click();
        
    }
    
  });
    var detailImageUrl = null;
    function setAddProductId(name) {
        document.addform.add_product_id.value = name;
        if (document.addform.quantity == null) return;
    }
    function setProductStock(name) {
        var elm = document.getElementById("addToCart");
        if(VARSTOCK[name]=="outOfStock")
        {
            elm.setAttribute("onClick","javascript:void(0)");
            jQuery('#addToCart').addClass("inactiveAddToCart");
        } else {
            jQuery('#addToCart').removeClass("inactiveAddToCart");
            elm.setAttribute("onClick","javascript:addItem()");
        }
    }
    
    function addItem() {
       if (document.addform.add_product_id.value == 'NULL' || document.addform.add_product_id.value == '') {
           for (i = 0; i < OPT.length; i++) {
            var optionName = OPT[i];
            var indexSelected = document.forms["addform"].elements[optionName].selectedIndex;
            if(indexSelected <= 0)
            {
                // Trim the FT prefix and convert to title case
                var properName = OPT[i].substr(2);

                // capitalize comes from prototype, do capitalize to each part
                var parts = properName.split('_');
                parts.each(function(element,index){
                    parts[index] = element.capitalize();
                });
                properName = parts.join(" ");
                alert("Please select a " + properName);
                break;
            }
           }
           return;
       } else {
           var quantity = document.addform.quantity.value;
           var lowerLimit = ${PDP_QTY_MIN!"1"};
           var upperLimit = ${PDP_QTY_MAX!"99"};
           if(quantity < lowerLimit)
           {
                alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPMinQtyError,'\"','\\"'))}");
                return false;
           }
           if(upperLimit!= 0 && quantity > upperLimit)
           {
                alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPMaxQtyError,'\"','\\"'))}");
                return false;
           }
           if(!isWhole(quantity))
           {
                alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPQtyDecimalNumberError,'\"','\\"'))}");
                return false;
           }
           document.addform.submit();
       }
    }
    
    function replaceDetailImage(largeImageUrl, detailImageUrl) {
        if (!jQuery('#mainImages').length) {
            var mainImages = jQuery('#mainImageDiv').clone();
            jQuery(mainImages).find('img.productLargeImage').attr('id', 'mainImage');
            jQuery('#productDetailsImageContainer').html(mainImages.html());
            jQuery('#seeMainImage a').attr("href", "javascript:replaceDetailImage('"+largeImageUrl+"', '"+detailImageUrl+"');");
        }
        <#assign activeZoom = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PDP_IMG_ZOOM_ACTIVE_FLAG")/>
        <#if activeZoom>
            var mainImages = jQuery('#mainImageDiv').clone();
            jQuery(mainImages).find('img.productLargeImage').attr('id', 'mainImage');
            jQuery(mainImages).find('img.productLargeImage').attr('src', largeImageUrl+ "?" + new Date().getTime());
            jQuery(mainImages).find('a').attr('class', 'innerZoom');
            if(detailImageUrl != "") {
              jQuery(mainImages).find('a').attr('href', detailImageUrl);
            } else {
                jQuery(mainImages).find('a').attr('href', 'javaScript:void(0);');
            }
            jQuery('#productDetailsImageContainer').html(mainImages.html());
            activateZoom(detailImageUrl);
            
        </#if>
        if (document.images['mainImage'] != null) {
            var detailimagePath;
            document.getElementById("mainImage").setAttribute("src",largeImageUrl);
            if(document.getElementById('largeImage')) {
                setDetailImage(detailImageUrl);
            }
            <#assign IMG_SIZE_PDP_REG_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_REG_W")!""/>
            document.getElementById("mainImage").setAttribute("class","productLargeImage<#if !IMG_SIZE_PDP_REG_W?has_content> productLargeImageDefaultWidth</#if>");
            detailimagePath = "javascript:displayDialogBox('largeImage_')";
            if (jQuery('#mainImageLink').length) {
                jQuery('#mainImageLink').attr("href",detailimagePath);
            }
        }
    }

    function setDetailImage(detailImageUrl) {
        if (typeof detailImageUrl == "undefined" || detailImageUrl == "NULL" || detailImageUrl == "") 
        {
            return;
        }
        var image = new Image();
        image.src = detailImageUrl+ "?" + new Date().getTime();
        image.onerror = function () {
          jQuery('#largeImage').attr('src', '/osafe_theme/images/user_content/images/NotFoundImagePDPDetail.jpg');
      };
      image.onload = function () {
          jQuery('#largeImage').attr('src', detailImageUrl);
      };
    }
    
    function findIndex(name) {
        for (i = 0; i < OPT.length; i++) {
            if (OPT[i] == name) {
                return i;
            }
        }
        return -1;
    }

    function getList(name, index, src) 
    {
        currentFeatureIndex = findIndex(name);
        
        if(index != -1)
        {
        	var liElm = jQuery('#Li'+name+" li").get(index);
		}
		else
		{
			var liElm = jQuery('#Li'+name+" li").get(0);
		}
        jQuery(liElm).siblings("li").removeClass("selected");
        jQuery(liElm).addClass("selected");
        
        // set the drop down index for swatch selection
        document.forms["addform"].elements[name].selectedIndex = (index*1)+1;
        if (currentFeatureIndex < (OPT.length-1)) 
        {
            // eval the next list if there are more
            var selectedValue = document.forms["addform"].elements[name].options[(index*1)+1].value;
            var selectedText = document.forms["addform"].elements[name].options[(index*1)+1].text;
            
            var mapKey = name+'_'+selectedText;
            var featureGroupDesc = VARGROUPMAP[VARMAP[mapKey]];

            jQuery('.pdpRecentlyViewed .'+featureGroupDesc).click();
            jQuery('.pdpComplement .'+featureGroupDesc).click();
            jQuery('.pdpAccessory .'+featureGroupDesc).click();
            
            jQuery('.pdpRecentlyViewed .'+selectedText).click();
            jQuery('.pdpComplement .'+selectedText).click();
            jQuery('.pdpAccessory .'+selectedText).click();
            
            var detailImgUrl = '';
            if(VARMAP[mapKey]) 
            {
                if(jQuery('#mainImage_'+VARMAP[mapKey]).length) 
                { 
                    var variantMainImages = jQuery('#mainImage_'+VARMAP[mapKey]).clone();
                    //jQuery(variantMainImages).find('img').each(function(){jQuery(this).attr('src', jQuery(this).attr('title')+ "?" + new Date().getTime());})
                    jQuery(variantMainImages).find('a').attr('class', 'innerZoom');
                    detailImgUrl = jQuery(variantMainImages).find('a').attr('href');
                    jQuery('#productDetailsImageContainer').html(variantMainImages.html());
                }
                    var variantAltImages = jQuery('#altImage_'+VARMAP[mapKey]).clone();
                    //jQuery(variantAltImages).find('img').each(function(){ jQuery(this).attr('src', jQuery(this).attr('title')+ "?" + new Date().getTime());})
                    jQuery('#eCommerceProductAddImage').html(variantAltImages.html());

                    var variantLargeImages = jQuery('#largeImageUrl_'+VARMAP[mapKey]).clone();
                    jQuery(variantLargeImages).find('.mainImageLink').attr('id', 'mainImageLink');
                    jQuery('#seeLargerImage').html(variantLargeImages.html());

                    var variantSeeMainImages = jQuery('#seeMainImage_'+VARMAP[mapKey]).clone();
                    jQuery('#seeMainImage').html(variantSeeMainImages.html());
                    
                    var variantProductVideo = jQuery('#productVideo_'+VARMAP[mapKey]).html();
                    jQuery('#productVideo').html(variantProductVideo);
                    
                    var variantProductVideoLink = jQuery('#productVideoLink_'+VARMAP[mapKey]).html();
                    jQuery('#productVideoLink').html(variantProductVideoLink);
                    
                    var variantProductVideo360 = jQuery('#productVideo360_'+VARMAP[mapKey]).html();
                    jQuery('#productVideo360').html(variantProductVideo360);
                    
                    var variantProductVideo360Link = jQuery('#productVideo360Link_'+VARMAP[mapKey]).html();
                    jQuery('#productVideo360Link').html(variantProductVideo360Link);
                    
                    var variantPdpPriceSavingMoney = jQuery('#pdpPriceSavingMoney_'+VARMAP[mapKey]).html();
                    jQuery('#pdpPriceSavingMoney').html(variantPdpPriceSavingMoney);
                    
                    var variantPdpPriceSavingPercent = jQuery('#pdpPriceSavingPercent_'+VARMAP[mapKey]).html();
                    jQuery('#pdpPriceSavingPercent').html(variantPdpPriceSavingPercent);
                    
                    var variantPdpPriceList = jQuery('#pdpPriceList_'+VARMAP[mapKey]).html();
                    jQuery('#pdpPriceList').html(variantPdpPriceList);
                    
                    var variantPdpPriceOnLine = jQuery('#pdpPriceOnLine_'+VARMAP[mapKey]).html();
                    jQuery('#pdpPriceOnLine').html(variantPdpPriceOnLine);
                    
                    var variantPdpVolumePricing = jQuery('#pdpVolumePricing_'+VARMAP[mapKey]).html();
                    jQuery('#pdpVolumePricing').html(variantPdpVolumePricing);
                    
                    var variantPdpLongDescription = jQuery('#pdpLongDescription_'+VARMAP[mapKey]).html();
                    jQuery('#pdpLongDescription').html(variantPdpLongDescription);
                    
                    var variantPdpDistinguishingFeature = jQuery('#pdpDistinguishingFeature_'+VARMAP[mapKey]).html();
                    jQuery('#pdpDistinguishingFeature').html(variantPdpDistinguishingFeature);
                
                    
            }
            if (index == -1) {
              <#if featureOrderFirst?exists>
                var Variable1 = eval("list" + "${featureOrderFirst}" + "()");
              </#if>
            } else {
                var Variable1 = eval("list" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
                var Variable2 = eval("listLi" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
                  
                  if (currentFeatureIndex+1 <= (OPT.length-1) ) 
                  {
                    var nextFeatureLength = document.forms["addform"].elements[OPT[(currentFeatureIndex+1)]].length;
                    if(nextFeatureLength == 2) {
                      getList(OPT[(currentFeatureIndex+1)],'0',1);
                      jQuery('#addToCart').removeClass("inactiveAddToCart");
                      return;
                    } else {
                      jQuery('#addToCart').addClass("inactiveAddToCart");
                    }
                  }
                  var elm = document.getElementById("addToCart");
                  elm.setAttribute("onClick","javascript:addItem()");
                  
                   
            }
            // set the product ID to NULL to trigger the alerts
            setAddProductId('NULL');

        }
        else 
        {
            
            // this is the final selection -- locate the selected index of the last selection
            var indexSelected = document.forms["addform"].elements[name].selectedIndex;
            // using the selected index locate the sku
            var sku = document.forms["addform"].elements[name].options[indexSelected].value;
            // set the product ID
            setAddProductId(sku);
            
            var varProductId = jQuery('#add_product_id').val();
            if(varProductId == "")
            {
            	jQuery('#addToCart').addClass("inactiveAddToCart");
			}
			else 
			{
				setProductStock(sku);
			}
        
            if(jQuery('#mainImage_'+varProductId).length) 
            {
	            var variantMainImages = jQuery('#mainImage_'+varProductId).clone();
	            //jQuery(variantMainImages).find('img').each(function(){jQuery(this).attr('src', jQuery(this).attr('title')+ "?" + new Date().getTime());})
	            jQuery(variantMainImages).find('a').attr('class', 'innerZoom');
	            detailImgUrl = jQuery(variantMainImages).find('a').attr('href');
	            jQuery('#productDetailsImageContainer').html(variantMainImages.html());
	        }
	            var variantAltImages = jQuery('#altImage_'+varProductId).clone();
	            //jQuery(variantAltImages).find('img').each(function(){jQuery(this).attr('src', jQuery(this).attr('title')+ "?" + new Date().getTime());})
	            jQuery('#eCommerceProductAddImage').html(variantAltImages.html());
	
	            var variantLargeImages = jQuery('#largeImageUrl_'+varProductId).clone();
	            jQuery(variantLargeImages).find('.mainImageLink').attr('id', 'mainImageLink');
	            jQuery('#seeLargerImage').html(variantLargeImages.html());
	            
	            var variantProductVideo = jQuery('#productVideo_'+varProductId).html();
	            jQuery('#productVideo').html(variantProductVideo);
	            
	            var variantProductVideoLink = jQuery('#productVideoLink_'+varProductId).html();
	            jQuery('#productVideoLink').html(variantProductVideoLink);
	            
	            var variantProductVideo360 = jQuery('#productVideo360_'+varProductId).html();
	            jQuery('#productVideo360').html(variantProductVideo360);
	            
	            var variantProductVideo360Link = jQuery('#productVideo360Link_'+varProductId).html();
	            jQuery('#productVideo360Link').html(variantProductVideo360Link);
	            
	            var variantPdpPriceSavingMoney = jQuery('#pdpPriceSavingMoney_'+varProductId).html();
                jQuery('#pdpPriceSavingMoney').html(variantPdpPriceSavingMoney);
                
                var variantPdpPriceSavingPercent = jQuery('#pdpPriceSavingPercent_'+varProductId).html();
                jQuery('#pdpPriceSavingPercent').html(variantPdpPriceSavingPercent);

                var variantPdpVolumePricing = jQuery('#pdpVolumePricing_'+varProductId).html();
                jQuery('#pdpVolumePricing').html(variantPdpVolumePricing);
                
                var variantPdpPriceList = jQuery('#pdpPriceList_'+varProductId).html();
                jQuery('#pdpPriceList').html(variantPdpPriceList);
                
                var variantPdpPriceOnLine = jQuery('#pdpPriceOnLine_'+varProductId).html();
                jQuery('#pdpPriceOnLine').html(variantPdpPriceOnLine);
                
                
                
                var variantPdpLongDescription = jQuery('#pdpLongDescription_'+varProductId).html();
                jQuery('#pdpLongDescription').html(variantPdpLongDescription);
                
                var variantPdpDistinguishingFeature = jQuery('#pdpDistinguishingFeature_'+varProductId).html();
                jQuery('#pdpDistinguishingFeature').html(variantPdpDistinguishingFeature);
            
        }
        activateZoom(detailImgUrl);
        activateScroller();
    }


    function activateZoom(imgUrl) {
        if (typeof imgUrl == "undefined" || imgUrl == "NULL" || imgUrl == "") 
        {
            return;
        }
        var image = new Image();
        image.src = imgUrl+ "?" + new Date().getTime();
        image.onerror = function () {
            jQuery('.innerZoom').attr('href', 'javaScript:void(0);');
            return false;
        };
        image.onload = function () {
            jQuery('.innerZoom').jqzoom(zoomOptions);
        };
        
    }
    
    function activateInitialZoom() {
        jQuery('.innerZoom').each(function() {
            var elm = this;
            var image = new Image();
            image.src = this.href+ "?" + new Date().getTime();
            image.onerror = function () {
                jQuery(elm).attr('href', 'javaScript:void(0);');
                return false;
            };
            image.onload = function () {
                jQuery('.innerZoom').jqzoom(zoomOptions);
            };
        });
    }

    function activateScroller() {
           <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PDP_ALT_IMG_SCROLLER_ACTIVE")>
            if(!jQuery('#altImageThumbnails').length) {
                jQuery('#eCommerceProductAddImage').find('ul').attr('id', 'altImageThumbnails');
            }
            jQuery('#altImageThumbnails').addClass('imageScroller');
            jQuery('#altImageThumbnails').jcarousel({
            <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PDP_ALT_IMG_SCROLLER_VERTICAL")>
                vertical: true,
            </#if>
                scroll: ${PDP_ALT_IMG_SCROLLER_IMAGES!"2"},
                itemFallbackDimension: 300
            });
        </#if>
    }

    function showProductVideo(videoDivClass){
        if (jQuery.browser.msie) jQuery('object > embed').unwrap(); 
        videoDiv = '.'+ videoDivClass;
        jQuery('#productDetailsImageContainer').html(jQuery(videoDiv).clone().removeClass(videoDivClass).show());
    }

	jQuery(function() {
		jQuery(".pdpTabs").tabs();
	});

var zoomOptions = {
    zoomType: 'innerzoom',
    lens:true,
    preloadImages: true,
    preloadText: ''
};


    function makeProductUrl(elm) {
        var plpFeatureSwatchImageId = jQuery(elm).attr("id");
        var plpFeatureSwatchImageIdArr = plpFeatureSwatchImageId.split("|");
        var pdpUrlId = plpFeatureSwatchImageIdArr[1]+plpFeatureSwatchImageIdArr[0]; 
        var pdpUrl = document.getElementById(pdpUrlId).value;
        
        jQuery(elm).parents('.eCommerceListItem').find('.eCommerceThumbNailHolder').find('.swatchProduct').find('a').attr("href",pdpUrl);
        jQuery(elm).parents('.eCommerceListItem').find('a.pdpUrl').attr("href",pdpUrl);
        jQuery(elm).parents('.eCommerceListItem').find('a.pdpUrl.review').attr("href",pdpUrl+"#productReviews");
    }
</#if>

 </script>
