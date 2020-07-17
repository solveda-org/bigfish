/*******  http://maps.googleapis.com/maps/api/geocode/xml?sensor=false&address=1600+Amphitheatre+Parkway%2C+Mountain+View%2C+CA  ***********
 *<?xml version="1.0" encoding="UTF-8"?>
 *<GeocodeResponse>
 * <status>OK</status>
 * <result>
 *  <type>street_address</type>
 *  <formatted_address>1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA</formatted_address>
 *  <address_component>
 *   <long_name>1600</long_name>
 *   <short_name>1600</short_name>
 *   <type>street_number</type>
 *  </address_component>
 *  <address_component>
 *   <long_name>Amphitheatre Pkwy</long_name>
 *   <short_name>Amphitheatre Pkwy</short_name>
 *   <type>route</type>
 *  </address_component>
 *  <address_component>
 *   <long_name>Mountain View</long_name>
 *   <short_name>Mountain View</short_name>
 *   <type>locality</type>
 *   <type>political</type>
 *  </address_component>
 *  <address_component>
 *   <long_name>Santa Clara</long_name>
 *   <short_name>Santa Clara</short_name>
 *   <type>administrative_area_level_2</type>
 *   <type>political</type>
 *  </address_component>
 *  <address_component>
 *   <long_name>California</long_name>
 *   <short_name>CA</short_name>
 *   <type>administrative_area_level_1</type>
 *   <type>political</type>
 *  </address_component>
 *  <address_component>
 *   <long_name>United States</long_name>
 *   <short_name>US</short_name>
 *   <type>country</type>
 *   <type>political</type>
 *  </address_component>
 *  <address_component>
 *   <long_name>94043</long_name>
 *   <short_name>94043</short_name>
 *   <type>postal_code</type>
 *  </address_component>
 *  <geometry>
 *   <location>
 *    <lat>37.4211444</lat>
 *    <lng>-122.0853032</lng>
 *   </location>
 *   <location_type>ROOFTOP</location_type>
 *   <viewport>
 *    <southwest>
 *     <lat>37.4197954</lat>
 *     <lng>-122.0866522</lng>
 *    </southwest>
 *    <northeast>
 *     <lat>37.4224934</lat>
 *     <lng>-122.0839542</lng>
 *    </northeast>
 *   </viewport>
 *  </geometry>
 * </result>
 *</GeocodeResponse>
 *******************************************************************************/
package com.osafe.geo;

public class MyOsafeGeoTest {

    public static void main(String[] args) throws Exception {

        OsafeGeo tOsafeGeo = OsafeGeo.fromAddress("1600 Amphitheatre Parkway, Mountain View, CA");
        System.out.println(tOsafeGeo.latitude());
        System.out.println(tOsafeGeo.longitude());

        tOsafeGeo = OsafeGeo.fromAddress("411 Aayachi Apartments, Sector-45, Gurgaon, Haryana, India");
        System.out.println(tOsafeGeo.latitude());
        System.out.println(tOsafeGeo.longitude());

        tOsafeGeo = OsafeGeo.fromAddress("");
        if(tOsafeGeo.isEmpty()) {
            System.out.println("===test empty===");
        }
    }

}
