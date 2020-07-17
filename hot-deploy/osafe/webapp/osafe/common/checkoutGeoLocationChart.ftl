<#if parameters.showMap?has_content && parameters.showMap == "Y">
<script type="text/javascript">
    function hideDirection() {
        jQuery('.noDirection').hide();
        jQuery('.mapDirection').hide();
        jQuery('.mapCanvas').removeClass('mapCanvasWithDirection');
        jQuery('.routeDirection').children().remove();
        loadMap();
    }
    function showDirection() {
        jQuery('.noDirection').hide();
        jQuery('.mapDirection').show();
        jQuery('.mapCanvas').addClass("mapCanvasWithDirection");
    }
    function noDirection() {
        jQuery('.noDirection').show();
        jQuery('.mapDirection').show();
        jQuery('.mapCanvas').addClass("mapCanvasWithDirection");
    }
</script>
<#if geoChart?has_content>
    <#if geoChart.dataSourceId?has_content>
      <#if geoChart.dataSourceId == dataSourceId>
        <div class="mapCanvas" id="<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>" style="width:${geoChart.width}; height:${geoChart.height};">
          <div class="mapLoading">${uiLabelMap.GeoLocationLoadingInfo}</div>
        </div>
        <div class="mapDirection" style="height:${geoChart.height};">
          <div id="noDirection" class="noDirection">
            <span>${uiLabelMap.NoDirectionRouteFoundError}</span>
          </div>
          <div id="routeDirection" class="routeDirection" style="height:${geoChart.height};"></div>
          <div id="closeDirection" class="closeDirection">
            <a href="javascript:void(0);" class="standardBtn action" onclick="hideDirection();">${uiLabelMap.CloseBtn}</a>
          </div>
        </div>
        <script type="text/javascript">
        function loadScript() {
            var script = document.createElement("script");
            script.type = "text/javascript";
            script.src = "${StringUtil.wrapString(geoChart.GeoMapRequestUrl)}&callback=loadMap";
            document.body.appendChild(script);
            jQuery('#isGoogleApi').val("Y");
        }

        var directionsService;
        var directionsDisplay;
        
        function loadMap() {
          var mapOptions = {zoom: Math.min (${geoChart.zoom}),
                mapTypeId: google.maps.MapTypeId.ROADMAP,
                scrollwheel: false,
                mapTypeControl: true,
                mapTypeControlOptions: {
                                       style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR
                                       },
                zoomControl: true,
                zoomControlOptions: {
                                    style: google.maps.ZoomControlStyle.SMALL
                                    }
                }
          var map = new google.maps.Map(document.getElementById('map_canvas'), mapOptions);

          infoWindow = new google.maps.InfoWindow();
          directionsService = new google.maps.DirectionsService();
          var rendererOptions = { suppressBicyclingLayer: true,suppressMarkers: true
                                };
          directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
          directionsDisplay.setMap(map);
          directionsDisplay.setPanel(document.getElementById("routeDirection"));

          geocoder = new google.maps.Geocoder();  
          jQuery('#address').keyup(function() { 
              geocoder.geocode( { 'address': jQuery('#address').val()}, function(results, status) {
                if (status == google.maps.GeocoderStatus.OK)
                {
                    jQuery('#latitude').val(results[0].geometry.location.lat());
                    jQuery('#longitude').val(results[0].geometry.location.lng());
                }
                else
                {
                    jQuery('#latitude').val("");
                    jQuery('#longitude').val("");
                }
              });
          });

          <#if geoChart.points?has_content>
            <#list geoChart.points as point>
              <#if point.userLocation?has_content && point.userLocation == "Y">
                  map.setCenter(new google.maps.LatLng(${point.lat}, ${point.lon}));
              </#if>
            </#list>
          <#else>
            map.setCenter(new google.maps.LatLng(0, 0));
          </#if>

          <#if geoChart.points?has_content>
            <#list geoChart.points as point>
              var GMarkerOptions = {
                position: new google.maps.LatLng(${point.lat}, ${point.lon}),
                map: map
              };
              var marker_${point_index} = new google.maps.Marker(GMarkerOptions);
              <#if point.userLocation?has_content && point.userLocation == "Y">
                marker_${point_index}.setIcon(new google.maps.MarkerImage("https://maps.google.com/mapfiles/kml/pal5/icon6.png"));
              </#if>
              <#if point.closures?has_content>
                google.maps.event.addListener(marker_${point_index}, "click", function() {
                <#if point.closures.storeDetail?has_content>
                  <#assign storeDetail = point.closures.storeDetail />
                  <#assign storeContactNumber = storeDetail.contactNumber!""/>
                  <#if storeDetail.countryGeoId?has_content && (storeDetail.countryGeoId == "USA" || storeDetail.countryGeoId == "CAN")>
                    <#assign storeContactNumber = storeDetail.areaCode+"-"+storeDetail.contactNumber3+"-"+storeDetail.contactNumber4/>
                  </#if>
                  var message = '<ul class="storeDetailsBubble">'+
                                  '<li class="storeName">${storeDetail.storeName!""} (${storeDetail.storeCode!""})</li>';
                  <#if userLocation?exists && userLocation?has_content>
                    message = message+'<li class="distance"><span class="label">${uiLabelMap.StoreLocatorDistanceCaption}</span> <span class="value">${storeDetail.distance!""}</span></li>';
                  </#if>
                  message = message+'<li class="storeAddress">' + 
                                    '<span class="label">${uiLabelMap.StoreLocatorAddressCaption}</span>' + 
                                    '<span class="value">' + 
                                        '<span class="addressLine addressLine1">${storeDetail.address1!""}</span><br/> <span class="addressLine addressLine2">${storeDetail.address2!""}</span> <span class="addressLine addressLine3">${storeDetail.address3!""}</span>' + 
                                        '<span class="addressCity">${storeDetail.city!""},</span> ' + 
                                        '<span class="addressState">${storeDetail.stateProvinceGeoId!""}</span> ' + 
                                        '<span class="addressZipCode">${storeDetail.postalCode!""}</span>' + 
                                      '</span>' + 
                                    '</li>';
                  <#if storeDetail.openingHoursContentId?exists && storeDetail.openingHoursContentId?has_content>
                    <#assign openingHours = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeDetail.openingHoursContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
                    <#if openingHours?has_content && openingHours != "null">
                      <#assign openingHours = Static["com.osafe.util.Util"].getFormattedText(openingHours)/>
                      message = message+'<li class="openingHours"><span class="label">${uiLabelMap.StoreLocatorHourCaption}</span> <span class="value">${openingHours!""}</span></li>';
                    </#if>
                  </#if>
                  <#if storeDetail.storeNoticeContentId?exists && storeDetail.storeNoticeContentId?has_content>
                    <#assign storeNotice = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeDetail.storeNoticeContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
                    <#if storeNotice?has_content && openingHours != "null">
                      <#assign storeNotice = Static["com.osafe.util.Util"].getFormattedText(storeNotice)/>
                      message = message+'<li class="storeNotice"><span class="label">${uiLabelMap.StoreLocatorNoticeCaption}</span> <span class="value">${storeNotice!""}</span></li>';
                    </#if>
                  </#if>
                  message = message+'<li class="storePhone">' + 
                                      '<span class="label">${uiLabelMap.StoreLocatorPhoneCaption}</span> <span class="value">${storeContactNumber!""}</span>' + 
                                    '</li>'+
                                  '</ul>';
                <#else>
                  var message = '${point.closures.data}';
                </#if>
                  infoWindow.setContent(message);
                  infoWindow.open(map, marker_${point_index})
                });
              </#if>
            </#list>
          </#if>
          }
          function setDirections(fromAddress, toAddress, travelMode) {
            var request = { origin:fromAddress,
                            destination:toAddress,
                            travelMode: google.maps.TravelMode[travelMode]
                            <#if geoChart.uom?has_content && geoChart.uom.equalsIgnoreCase("Kilometers")>
                              ,unitSystem: google.maps.UnitSystem.METRIC
                            <#elseif geoChart.uom?has_content && geoChart.uom.equalsIgnoreCase("Miles")>
                              ,unitSystem: google.maps.UnitSystem.IMPERIAL
                            </#if>
                          };
            directionsService.route(request, function(response, status) {
                          if (status == google.maps.DirectionsStatus.OK) {
                            directionsDisplay.setDirections(response);
                            showDirection();
                          } else if (status == google.maps.DirectionsStatus.ZERO_RESULTS) {
                            directionsDisplay.setDirections(response);
                            noDirection();
                          }
                        });
                        infoWindow.close();
          }
      </script>
      </#if>
    </#if>
<#else>
  <h2>${uiLabelMap.NoGeoLocationAvailableInfo}</h2>
</#if>
</#if>