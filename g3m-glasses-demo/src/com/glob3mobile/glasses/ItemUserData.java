

package com.glob3mobile.glasses;

import org.glob3.mobile.generated.MarkUserData;


public class ItemUserData
         extends
            MarkUserData {

   public String getName() {
      return _name;
   }


   public void setName(final String name) {
      _name = name;
   }


   public String getDistance() {
      return _distance;
   }


   public void setDistance(final String distance) {
      _distance = distance;
   }


   public String getPhoto() {
      return _photo;
   }


   public void setPhoto(final String photo) {
      _photo = photo;
   }


   private String _wpURL;
   private String _googlePlaceReference;
   private String _name;
   private String _distance;
   private String _photo;
   private String _vicinity;
   private String _latlon;


   public String getLatlon() {
      return _latlon;
   }


   public void setLatlon(final String latlon) {
      _latlon = latlon;
   }


   public String getWpURL() {
      return _wpURL;
   }


   public String getGooglePlaceReference() {
      return _googlePlaceReference;
   }


   public void setGooglePlaceReference(final String googlePlaceReference) {
      _googlePlaceReference = googlePlaceReference;
   }


   public void setWpURL(final String wpURL) {
      _wpURL = wpURL;
   }


   public String getVicinity() {
      return _vicinity;
   }


   public void setVicinity(final String vicinity) {
      _vicinity = vicinity;
   }


   public String getLonlat() {
      return _latlon;
   }


   public void setLonlat(final String lonlat) {
      _latlon = lonlat;
   }

}
