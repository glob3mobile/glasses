

package com.glob3mobile.glasses;

import org.glob3.mobile.generated.Geodetic3D;


public class WikipediaArticle {

   String     _summary;
   String     _title;
   String     _wikipediaURL;
   double     _lon;
   double     _lat;
   Geodetic3D _position;


   public WikipediaArticle() {
      // TODO Auto-generated constructor stub
   }


   public String getSummary() {
      return _summary;
   }


   public void setSummary(final String summary) {
      _summary = summary;
   }


   public String getTitle() {
      return _title;
   }


   public void setTitle(final String title) {
      this._title = title;
   }


   public String getWikipediaURL() {
      return _wikipediaURL;
   }


   public void setWikipediaURL(final String wikipediaURL) {
      this._wikipediaURL = wikipediaURL;
   }


   public double getLon() {
      return _lon;
   }


   public void setLon(final double lon) {
      this._lon = lon;
   }


   public double getLat() {
      return _lat;
   }


   public void setLat(final double lat) {
      this._lat = lat;
   }


   public Geodetic3D getPosition() {
      return _position;
   }


   public void setPosition(final Geodetic3D position) {
      this._position = position;
   }


}
