

package com.glob3mobile.glasses;

import java.util.ArrayList;


public class GooglePlaceItem
         extends
            G3MGlassItem {

   private String            _reference;
   private String            _urlIcon;
   private ArrayList<String> _photosReference;
   private String            _mainPhotoReference;
   private String            _formatedAddress;
   private int               _userRateTotal;
   private int               _distance;
   private String            _vicinity;


   public int getDistance() {
      return _distance;
   }


   public void setDistance(final int distance) {
      _distance = distance;
   }


   public String getReference() {
      return _reference;
   }


   public void setReference(final String reference) {
      _reference = reference;
   }


   public String getUrlIcon() {
      return _urlIcon;
   }


   public void setUrlIcon(final String urlIcon) {
      _urlIcon = urlIcon;
   }


   public ArrayList<String> getPhotosReference() {
      return _photosReference;
   }


   public void setPhotosReference(final ArrayList<String> photosReference) {
      _photosReference = photosReference;
   }


   public String getMainPhotoReference() {
      return _mainPhotoReference;
   }


   public void setMainPhotoReference(final String mainPhotoReference) {
      _mainPhotoReference = mainPhotoReference;
   }


   public String getFormatedAddress() {
      return _formatedAddress;
   }


   public void setFormatedAddress(final String formatedAddress) {
      _formatedAddress = formatedAddress;
   }


   public int getUserRateTotal() {
      return _userRateTotal;
   }


   public void setUserRateTotal(final int userRateTotal) {
      _userRateTotal = userRateTotal;
   }


   public String getVicinity() {
      return _vicinity;
   }


   public void setVicinity(final String vicinity) {
      _vicinity = vicinity;
   }

}
